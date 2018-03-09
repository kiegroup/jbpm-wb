/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.wi.client.editors.deployment.descriptor;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.wi.client.editors.deployment.descriptor.type.DDResourceType;
import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.console.ng.wi.dd.service.DDEditorService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.CommandErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "org.kie.jbpmconsole.dd", supportedTypes = {DDResourceType.class}, priority = 101)
public class DeploymentDescriptorEditorPresenter extends KieEditor {

    @Inject
    protected Caller<DDEditorService> ddEditorService;

    private DeploymentDescriptorView view;

    @Inject
    private DDResourceType type;

    @Inject
    private Event<NotificationEvent> notification;

    private DeploymentDescriptorModel model;

    @Inject
    public DeploymentDescriptorEditorPresenter(final DeploymentDescriptorView baseView) {
        super(baseView);
        view = baseView;
    }

    //This is called after the View's content has been loaded
    public void onAfterViewLoaded() {
        setOriginalHash(model.hashCode());
    }

    @OnStartup
    public void onStartup(final ObservablePath path, final PlaceRequest place) {
        ddEditorService.call().createIfNotExists(path);
        init(path, place, type);
    }

    protected void loadContent() {
        view.showLoading();
        ddEditorService.call(new RemoteCallback<DeploymentDescriptorModel>() {

                                 @Override
                                 public void callback(final DeploymentDescriptorModel content) {
                                     //Path is set to null when the Editor is closed (which can happen before async calls complete).
                                     if (versionRecordManager.getCurrentPath() == null) {
                                         return;
                                     }

                                     model = content;
                                     resetEditorPages(content.getOverview());
                                     addSourcePage();

                                     view.setContent(content);
                                     onAfterViewLoaded();
                                     view.hideBusyIndicator();
                                 }
                             },
                             getNoSuchFileExceptionErrorCallback()).load(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void onValidate(final Command callFinished) {
        ddEditorService.call(new RemoteCallback<List<ValidationMessage>>() {
            @Override
            public void callback(final List<ValidationMessage> results) {
                if (results == null || results.isEmpty()) {
                    notifyValidationSuccess();
                } else {
                    ValidationPopup.showMessages(results);
                }
                callFinished.execute();
            }
        }, new CommandErrorCallback(callFinished)).validate(versionRecordManager.getCurrentPath(),
                                                            model);
    }

    protected void notifyValidationSuccess() {
        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                NotificationEvent.NotificationType.SUCCESS));
    }

    protected void save() {
        new SaveOperationService().save(versionRecordManager.getCurrentPath(),
                                        new ParameterizedCommand<String>() {
                                            @Override
                                            public void execute(final String comment) {
                                                view.showSaving();
                                                view.updateContent(model);
                                                ddEditorService.call(getSaveSuccessCallback(model.hashCode()),
                                                                     new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                          model,
                                                                                                                          metadata,
                                                                                                                          comment);
                                            }
                                        }
        );
        concurrentUpdateSessionInfo = null;
    }

    protected void addSourcePage() {

        addPage(new PageImpl(view.getSourceEditor(),
                             CommonConstants.INSTANCE.SourceTabTitle()) {
            @Override
            public void onFocus() {
                onSourceTabSelected();
            }

            @Override
            public void onLostFocus() {

            }
        });
    }

    @Override
    public void onSourceTabSelected() {
        view.updateContent(model);
        ddEditorService.call(new RemoteCallback<String>() {
            @Override
            public void callback(String source) {
                updateSource(source);
            }
        }).toSource(versionRecordManager.getCurrentPath(), model);
    }

    protected void updateSource(String source) {
        view.setSource(source);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @OnMayClose
    public boolean checkIfDirty() {
        view.updateContent(model);
        return super.mayClose(model.hashCode());
    }

    @WorkbenchPartTitleDecoration
    public com.google.gwt.user.client.ui.IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                }))
                .addValidate(getValidateCommand())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }
}
