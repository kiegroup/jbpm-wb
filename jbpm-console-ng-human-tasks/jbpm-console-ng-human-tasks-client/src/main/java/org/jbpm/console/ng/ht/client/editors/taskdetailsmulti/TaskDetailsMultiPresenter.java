/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskform.TaskFormPresenter;
import org.jbpm.console.ng.ht.client.editors.tasklogs.TaskLogsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskprocesscontext.TaskProcessContextPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.jbpm.console.ng.ht.forms.client.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Task Details Multi", preferredWidth = 655)
public class TaskDetailsMultiPresenter implements RefreshMenuBuilder.SupportsRefresh {

    public interface TaskDetailsMultiView
            extends UberView<TaskDetailsMultiPresenter> {

        void setAdminTabVisible(boolean value);

        void displayAllTabs();

        void displayOnlyLogTab();
    }

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
    private TaskProcessContextPresenter taskProcessContextPresenter;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private Event<TaskSelectionEvent> taskSelected;

    private PlaceRequest place;

    private Long taskId;

    private String serverTemplateId = "";

    private String containerId = "";

    private String processId = "";

    private boolean forLog = false;

    private boolean forAdmin = false;

    @WorkbenchPartView
    public UberView<TaskDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
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
        taskId = event.getTaskId();
        serverTemplateId = event.getServerTemplateId();
        containerId = event.getContainerId();
        processId = event.getTaskName();

        taskFormPresenter.getTaskFormView().getDisplayerView().setOnCloseCommand(new Command() {
            @Override
            public void execute() {
                closeDetails();
            }
        });
        taskFormDisplayProvider.setup(new HumanTaskDisplayerConfig(new TaskKey(serverTemplateId, containerId, taskId)), taskFormPresenter.getTaskFormView().getDisplayerView());

        setIsForLog(event.isForLog());
        setIsForAdmin(event.isForAdmin());

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place, String.valueOf(taskId) + " - " + processId));

        if (isForLog()) {
            view.displayOnlyLogTab();
            disableTaskDetailsEdition();
        } else {
            view.displayAllTabs();
        }
        if (isForAdmin()) {
            view.setAdminTabVisible(true);
        } else {
            view.setAdminTabVisible(false);
        }
    }

    public void closeDetails() {
        placeManager.closePlace(place);
    }

    @Override
    public void onRefresh() {
        taskSelected.fire(new TaskSelectionEvent(serverTemplateId, containerId, taskId, processId, isForAdmin(), isForLog()));
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

    public IsWidget getProcessContextView() {
        return taskProcessContextPresenter.getView();
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

    public void taskDetailsRefresh() {
        taskDetailsPresenter.refreshTask();
    }

    public void disableTaskDetailsEdition() {
        taskDetailsPresenter.setReadOnlyTaskDetail();
    }

    public void taskProcessContextRefresh() {
        taskProcessContextPresenter.refreshProcessContextOfTask();
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

}
