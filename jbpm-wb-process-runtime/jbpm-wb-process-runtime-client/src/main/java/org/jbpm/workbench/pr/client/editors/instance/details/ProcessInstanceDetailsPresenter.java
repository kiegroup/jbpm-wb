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
package org.jbpm.workbench.pr.client.editors.instance.details;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramPresenter;
import org.jbpm.workbench.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.workbench.pr.client.editors.instance.log.RuntimeLogPresenter;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = PROCESS_INSTANCE_DETAILS_SCREEN)
public class ProcessInstanceDetailsPresenter implements RefreshMenuBuilder.SupportsRefresh {

    @Inject
    public ProcessInstanceDetailsView view;

    @Inject
    ConfirmPopup confirmPopup;

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private PlaceManager placeManager;

    private Caller<ProcessService> processService;

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    private ProcessInstanceDetailsTabPresenter detailsPresenter;

    @Inject
    private ProcessDiagramPresenter processDiagramPresenter;

    @Inject
    private ProcessVariableListPresenter variableListPresenter;

    @Inject
    private ProcessDocumentListPresenter documentListPresenter;

    @Inject
    private RuntimeLogPresenter runtimeLogPresenter;

    private String selectedDeploymentId = "";

    private int selectedProcessInstanceStatus = 0;

    private String selectedProcessDefName = "";

    private PlaceRequest place;

    private String deploymentId = "";

    private String processId = "";

    private Long processInstanceId;

    private String serverTemplateId = "";

    private boolean forLog = false;

    @Inject
    public void setProcessService(final Caller<ProcessService> processService) {
        this.processService = processService;
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public boolean isForLog() {
        return forLog;
    }

    public void setIsForLog(boolean isForLog) {
        this.forLog = isForLog;
    }

    public void onProcessSelectionEvent(@Observes ProcessInstanceSelectionEvent event) {

        deploymentId = event.getDeploymentId();
        processId = event.getProcessDefId();
        processInstanceId = event.getProcessInstanceId();
        serverTemplateId = event.getServerTemplateId();
        selectedDeploymentId = event.getDeploymentId();
        selectedProcessInstanceStatus = event.getProcessInstanceStatus();
        selectedProcessDefName = event.getProcessDefName();
        setIsForLog(event.isForLog());

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                               String.valueOf(processInstanceId) + " - " + selectedProcessDefName));

        if (isForLog()) {
            view.displayOnlyLogTab();
        } else {
            view.displayAllTabs();
        }
        view.selectInstanceDetailsTab();
    }

    @Override
    public void onRefresh() {
        processInstanceSelected.fire(new ProcessInstanceSelectionEvent(selectedDeploymentId,
                                                                       processInstanceId,
                                                                       processId,
                                                                       selectedProcessDefName,
                                                                       selectedProcessInstanceStatus,
                                                                       isForLog(),
                                                                       serverTemplateId));
    }

    public void signalProcessInstance() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP);

        placeRequestImpl.addParameter("processInstanceId",
                                      String.valueOf(processInstanceId));
        placeRequestImpl.addParameter("deploymentId",
                                      deploymentId);
        placeRequestImpl.addParameter("serverTemplateId",
                                      serverTemplateId);
        placeManager.goTo(placeRequestImpl);
    }

    public void abortProcessInstance() {
        confirmPopup.show(constants.Abort_Confirmation(),
                          constants.Abort(),
                          constants.Abort_Process_Instance(),
                          () -> {
                              displayNotification(constants.Aborting_Process_Instance(processInstanceId));
                              processService.call(
                                      (Void processInstance) ->
                                              processInstancesUpdatedEvent
                                                      .fire(new ProcessInstancesUpdateEvent(0L)))
                                      .abortProcessInstance(serverTemplateId,
                                                            deploymentId,
                                                            processInstanceId);
                          });
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelMenu(constants.Signal())
                    .respondsWith(() -> signalProcessInstance())
                .endMenu()
                .newTopLevelMenu(constants.Abort())
                .respondsWith(() -> abortProcessInstance())
                .endMenu()
                .build();
    }

    public void variableListRefreshGrid() {
        variableListPresenter.refreshGrid();
    }

    public void documentListRefreshGrid() {
        documentListPresenter.refreshGrid();
    }

    public IsWidget getProcessInstanceView() {
        return detailsPresenter.getWidget();
    }

    public IsWidget getProcessVariablesView() {
        return variableListPresenter.getWidget();
    }

    public IsWidget getDocumentView() {
        return documentListPresenter.getWidget();
    }

    public IsWidget getLogsView() {
        return runtimeLogPresenter.getWidget();
    }

    public IsWidget getProcessDiagramView() {
        return processDiagramPresenter.getView();
    }

    public interface ProcessInstanceDetailsView extends UberView<ProcessInstanceDetailsPresenter> {

        void selectInstanceDetailsTab();

        void displayAllTabs();

        void displayOnlyLogTab();
    }
}
