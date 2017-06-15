/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.type.DDResourceType;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;
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

    private Caller<DDEditorService> ddEditorService;

    private DeploymentDescriptorView view;

    @Inject
    private DDResourceType type;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private ValidationPopup validationPopup;

    private DeploymentDescriptorModel model;

    private TranslationService translationService;

    @Inject
    public DeploymentDescriptorEditorPresenter(final DeploymentDescriptorView baseView,
                                               final Caller<DDEditorService> ddEditorService,
                                               TranslationService translationService) {
        super(baseView);
        view = baseView;
        this.translationService = translationService;
        this.ddEditorService = ddEditorService;
    }

    //This is called after the View's content has been loaded
    public void onAfterViewLoaded() {
        setOriginalHash(model.hashCode());
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        ddEditorService.call().createIfNotExists(path);
        init(path,
             place,
             type);
        fillPersistenceModes();
        fillAuditModes();
        fillRuntimeStrategies(translationService);
        view.setSourceTabReadOnly(true);
        view.setup();
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

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                ddEditorService.call(new RemoteCallback<List<ValidationMessage>>() {
                    @Override
                    public void callback(final List<ValidationMessage> results) {
                        if (results == null || results.isEmpty()) {
                            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                                    NotificationEvent.NotificationType.SUCCESS));
                        } else {
                            validationPopup.showMessages(results);
                        }
                    }
                }).validate(versionRecordManager.getCurrentPath(),
                            model);
            }
        };
    }

    protected void save() {
        savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
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
        }).toSource(versionRecordManager.getCurrentPath(),
                    model);
    }

    protected void fillRuntimeStrategies(TranslationService translationService) {
        ClientRuntimeStrategy[] clientRuntimeStrategies = ClientRuntimeStrategy.values();
        for (ClientRuntimeStrategy clientRunTimeStrategy : clientRuntimeStrategies) {
            view.addRuntimeStrategy(clientRunTimeStrategy.getValue(translationService),
                                    clientRunTimeStrategy.name());
        }
    }

    protected void fillPersistenceModes() {
        view.addPersistenceMode("NONE",
                                "NONE");
        view.addPersistenceMode("JPA",
                                "JPA");
    }

    protected void fillAuditModes() {
        view.addAuditMode("NONE",
                          "NONE");
        view.addAuditMode("JPA",
                          "JPA");
        view.addAuditMode("JMS",
                          "JMS");
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
    public IsWidget getTitle() {
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
                .addValidate(onValidate())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }
}
