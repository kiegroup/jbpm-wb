/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.definition.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = PROCESS_DEFINITION_DETAILS_SCREEN)
public class ProcessDefinitionDetailsPresenter implements RefreshMenuBuilder.SupportsRefresh {

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private ProcessDefinitionDetailsTabPresenter detailPresenter;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Inject
    private ProcessDiagramPresenter processDiagramPresenter;

    @Inject
    protected ProcessDefinitionDetailsView view;

    @Inject
    protected PopupFormDisplayerView formDisplayPopUp;

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private String deploymentId = "";

    private String processId = "";

    private String processDefName = "";

    private String serverTemplateId = "";

    private boolean dynamic;

    protected PrimaryActionMenuBuilder primaryActionMenuBuilder;

    private PlaceRequest place;

    @PostConstruct
    public void init() {
        setPrimaryActionMenuBuilder(new PrimaryActionMenuBuilder(Constants.INSTANCE.New_Process_Instance(),
                                                                 () -> createNewProcessInstance()));
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartView
    public UberView<ProcessDefinitionDetailsPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(primaryActionMenuBuilder).endMenu()
                .build();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @Override
    public void onRefresh() {
        processDefSelectionEvent.fire(new ProcessDefSelectionEvent(processId,
                                                                   deploymentId,
                                                                   serverTemplateId,
                                                                   processDefName,
                                                                   dynamic));
    }

    protected void setPrimaryActionMenuBuilder(final PrimaryActionMenuBuilder primaryActionMenuBuilder){
        this.primaryActionMenuBuilder = primaryActionMenuBuilder;
    }

    public void onProcessSelectionEvent(@Observes final ProcessDefSelectionEvent event) {
        deploymentId = event.getDeploymentId();
        processId = event.getProcessId();
        processDefName = event.getProcessDefName();
        serverTemplateId = event.getServerTemplateId();
        dynamic = event.isDynamic();

        primaryActionMenuBuilder.setVisible(dynamic == false);

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                               String.valueOf(deploymentId) + " - " + processDefName));
    }

    public void createNewProcessInstance() {
        final ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(serverTemplateId,
                                                                                                  deploymentId,
                                                                                                  processId,
                                                                                                  processDefName),
                                                                         processDefName,
                                                                         dynamic);

        formDisplayPopUp.setTitle(processDefName);
        startProcessDisplayProvider.setup(config,
                                          formDisplayPopUp);
    }

    public IsWidget getDetailsView() {
        return detailPresenter.getWidget();
    }

    public IsWidget getProcessDiagramView() {
        return processDiagramPresenter.getView();
    }

    public interface ProcessDefinitionDetailsView extends UberView<ProcessDefinitionDetailsPresenter> {

    }
}
