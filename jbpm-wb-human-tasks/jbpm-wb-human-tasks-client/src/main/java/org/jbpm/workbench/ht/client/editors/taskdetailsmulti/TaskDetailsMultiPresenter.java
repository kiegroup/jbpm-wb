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
package org.jbpm.workbench.ht.client.editors.taskdetailsmulti;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.ht.client.editors.taskadmin.TaskAdminPresenter;
import org.jbpm.workbench.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.workbench.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.workbench.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.workbench.ht.client.editors.taskform.TaskFormPresenter;
import org.jbpm.workbench.ht.client.editors.tasklogs.TaskLogsPresenter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.forms.display.api.HumanTaskDisplayerConfig;
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayProvider;
import org.jbpm.workbench.ht.model.TaskKey;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = TASK_DETAILS_SCREEN)
public class TaskDetailsMultiPresenter extends AbstractTaskPresenter implements RefreshMenuBuilder.SupportsRefresh {

    private Constants constants = Constants.INSTANCE;

    private Caller<TaskService> taskDataService;

    @Inject
    private TaskDetailsMultiView view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private HumanTaskFormDisplayProvider taskFormDisplayProvider;

    @Inject
    private TaskFormPresenter taskFormPresenter;

    @Inject
    private TaskDetailsPresenter taskDetailsPresenter;

    @Inject
    private TaskLogsPresenter taskLogsPresenter;

    @Inject
    private TaskAssignmentsPresenter taskAssignmentsPresenter;

    @Inject
    private TaskCommentsPresenter taskCommentsPresenter;

    @Inject
    private TaskAdminPresenter taskAdminPresenter;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    private PlaceRequest place;

    private String processId = "";

    private boolean forLog = false;

    private boolean forAdmin = false;

    @WorkbenchPartView
    public UberView<TaskDetailsMultiPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public boolean isForAdmin() {
        return forAdmin;
    }

    public void setIsForAdmin(boolean isForAdmin) {
        this.forAdmin = isForAdmin;
    }

    public boolean isForLog() {
        return forLog;
    }

    public void setIsForLog(boolean isForLog) {
        this.forLog = isForLog;
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        boolean refreshDetails = isSameTaskFromEvent().test(event);
        setSelectedTask(event);
        processId = event.getTaskName();

        if (!event.isForLog()) {
            taskFormPresenter.getTaskFormView().getDisplayerView().setOnCloseCommand(() -> closeDetails());
            taskFormDisplayProvider.setup(new HumanTaskDisplayerConfig(new TaskKey(getServerTemplateId(),
                                                                                   getContainerId(),
                                                                                   getTaskId())),
                                          taskFormPresenter.getTaskFormView().getDisplayerView());
        }
        setIsForLog(event.isForLog());
        setIsForAdmin(event.isForAdmin());

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                               String.valueOf(getTaskId()) + " - " + processId));

        view.setAdminTabVisible(false);
        if (isForLog()) {
            view.displayOnlyLogTab();
            disableTaskDetailsEdition();
        } else {
            view.displayAllTabs();
            if (isForAdmin()) {
                view.setAdminTabVisible(true);
            }
        }

        if (!refreshDetails) {
            view.resetTabs(event.isForLog());
        }
    }

    public void closeDetails() {
        placeManager.closePlace(place);
    }

    @Override
    public void onRefresh() {
        taskDataService.call(
                (TaskSummary taskSummary) -> {
                    if (taskSummary != null) {
                        taskSelected.fire(new TaskSelectionEvent(getServerTemplateId(),
                                                                 taskSummary.getDeploymentId(),
                                                                 taskSummary.getId(),
                                                                 taskSummary.getName(),
                                                                 isForAdmin(),
                                                                 isForLog(),
                                                                 taskSummary.getDescription(),
                                                                 taskSummary.getExpirationTime(),
                                                                 taskSummary.getStatus(),
                                                                 taskSummary.getActualOwner(),
                                                                 taskSummary.getPriority(),
                                                                 taskSummary.getProcessInstanceId(),
                                                                 taskSummary.getProcessId(),
                                                                 taskSummary.getSlaCompliance()));
                    } else {
                        view.displayNotification(constants.TaskDetailsNotAvailable());
                    }
                }).getTask(getServerTemplateId(), getContainerId(), getTaskId());
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                .endMenu()
                .build();
    }

    public IsWidget getGenericFormView() {
        return taskFormPresenter.getView();
    }

    public IsWidget getTaskDetailsView() {
        return taskDetailsPresenter.getView();
    }

    public IsWidget getTaskAssignmentsView() {
        return taskAssignmentsPresenter.getView();
    }

    public IsWidget getTaskCommentsView() {
        return taskCommentsPresenter.getView();
    }

    public IsWidget getTaskAdminView() {
        return taskAdminPresenter.getView();
    }

    public IsWidget getTaskLogsView() {
        return taskLogsPresenter.getView();
    }

    public void disableTaskDetailsEdition() {
        taskDetailsPresenter.setReadOnlyTaskDetail();
    }

    public void taskAssignmentsRefresh() {
        taskAssignmentsPresenter.refreshTaskPotentialOwners();
    }

    public void taskCommentsRefresh() {
        taskCommentsPresenter.refreshComments();
    }

    public void taskLogsRefresh() {
        taskLogsPresenter.refreshLogs();
    }

    public void taskAdminRefresh() {
        taskAdminPresenter.refreshTaskPotentialOwners();
    }

    @Inject
    public void setTaskDataService(final Caller<TaskService> taskDataService) {
        this.taskDataService = taskDataService;
    }

    public interface TaskDetailsMultiView
            extends UberView<TaskDetailsMultiPresenter> {

        void setAdminTabVisible(boolean value);

        void displayAllTabs();

        void resetTabs(boolean onlyLogTab);

        void displayOnlyLogTab();

        void displayNotification(String text);
    }
}
