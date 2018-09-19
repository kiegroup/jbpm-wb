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
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.pr.client.editors.diagram.ProcessDiagramPresenter;
import org.jbpm.workbench.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.workbench.pr.client.editors.instance.log.ProcessInstanceLogPresenter;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.kie.api.runtime.process.ProcessInstance;
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
    private ProcessInstanceLogPresenter processInstanceLogPresenter;

    private String selectedDeploymentId = "";

    private int selectedProcessInstanceStatus = 0;

    private String selectedProcessDefName = "";

    private PlaceRequest place;

    private String deploymentId = "";

    private String processId = "";

    private Long processInstanceId;

    private String serverTemplateId = "";

    private boolean forLog = false;

    PrimaryActionMenuBuilder signalProcessInstanceAction;

    PrimaryActionMenuBuilder abortProcessInstanceAction;

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
        setSignalProcessInstanceAction(new PrimaryActionMenuBuilder(constants.Signal(),
                                                                    () -> signalProcessInstance()));
        setAbortProcessInstanceAction(new PrimaryActionMenuBuilder(constants.Abort(),
                                                                   () -> openAbortProcessInstancePopup()));
        this.place = place;
    }

    public void setSignalProcessInstanceAction(PrimaryActionMenuBuilder signalProcessInstanceAction) {
        this.signalProcessInstanceAction = signalProcessInstanceAction;
    }

    public void setAbortProcessInstanceAction(PrimaryActionMenuBuilder abortProcessInstanceAction) {
        this.abortProcessInstanceAction = abortProcessInstanceAction;
    }

    private void setSignalAbortActionsVisible(boolean visible) {
        signalProcessInstanceAction.setVisible(visible);
        abortProcessInstanceAction.setVisible(visible);
    }

    public boolean isForLog() {
        return forLog;
    }

    public void setIsForLog(boolean isForLog) {
        this.forLog = isForLog;
    }

    public void onProcessSelectionEvent(@Observes final ProcessInstanceSelectionEvent event) {
        boolean refreshDetails = (event != null && event.getProcessInstanceId() != null && event.getProcessInstanceId().equals(processInstanceId));

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

        boolean signalAbortVisible = false;
        if (isForLog()) {
            view.displayOnlyLogTab();
        } else {
            if (selectedProcessInstanceStatus == ProcessInstance.STATE_ACTIVE) {
                signalAbortVisible = true;
            }
            view.displayAllTabs();
        }
        setSignalAbortActionsVisible(signalAbortVisible);
        if (!refreshDetails) {
            view.resetTabs(event.isForLog());
        }
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

    public void openAbortProcessInstancePopup() {
        confirmPopup.show(constants.Abort_Confirmation(),
                          constants.Abort(),
                          constants.Abort_Process_Instance(),
                          () -> abortProcessInstance());
    }

    protected void abortProcessInstance() {
        processService.call(
                (Void processInstance) -> {
                    displayNotification(constants.Aborting_Process_Instance(processInstanceId));
                    selectedProcessInstanceStatus = ProcessInstance.STATE_ABORTED;
                    setSignalAbortActionsVisible(false);
                    processInstancesUpdatedEvent
                            .fire(new ProcessInstancesUpdateEvent(0L));
                })
                .abortProcessInstance(serverTemplateId,
                                      deploymentId,
                                      processInstanceId);
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(signalProcessInstanceAction)
                .endMenu()
                .newTopLevelCustomMenu(abortProcessInstanceAction)
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
        return processInstanceLogPresenter.getWidget();
    }

    public IsWidget getProcessDiagramView() {
        return processDiagramPresenter.getView();
    }

    public interface ProcessInstanceDetailsView extends UberView<ProcessInstanceDetailsPresenter> {

        void displayAllTabs();

        void displayOnlyLogTab();

        void resetTabs(boolean onlyLogTab);

        void displayNotification(String text);
    }
}
