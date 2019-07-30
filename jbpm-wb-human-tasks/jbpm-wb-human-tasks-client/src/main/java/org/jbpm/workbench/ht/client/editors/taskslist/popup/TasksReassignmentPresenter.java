/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.taskslist.popup;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.TaskKey;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@WorkbenchPopup(identifier = TasksReassignmentPresenter.TASKS_REASSIGNMENT_POPUP)
public class TasksReassignmentPresenter {

    public static final String TASKS_REASSIGNMENT_POPUP = "Tasks Reassignment Popup";

    private Constants constants = GWT.create(Constants.class);

    @Inject
    private TasksReassignmentView view;

    @Inject
    private PlaceManager placeManager;

    private Event<TaskRefreshedEvent> taskRefreshed;

    private Event<NotificationEvent> notificationEvent;

    private PlaceRequest place;

    private Caller<TaskService> taskService;

    private String serverTemplateId;

    private String[] deploymentIds;

    private String[] taskIdsStr;

    private List<TaskKey> tasksToReassign;

    @Inject
    public void setTaskService(final Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_Reassignment();
    }

    @WorkbenchPartView
    public UberView<TasksReassignmentPresenter> getView() {
        return view;
    }

    @Inject
    public void setNotificationEvent(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    @Inject
    public void setTaskRefreshedEvent(Event<TaskRefreshedEvent> taskRefreshedEvent) {
        this.taskRefreshed = taskRefreshedEvent;
    }

    public void reassignTasksToUser(final String userId) {

        taskService.call((List<TaskAssignmentSummary> reassignments) -> {
                             reassignments.forEach(taskAssignmentSummary -> {
                                 if (taskAssignmentSummary.isDelegationAllowed()) {
                                     displayNotification(constants.TaskWasDelegated(String.valueOf(taskAssignmentSummary.getTaskId()), taskAssignmentSummary.getTaskName(), userId));
                                 } else {
                                     displayUnsuccessfulNotification(constants.ReassignmentNotAllowedOn(String.valueOf(taskAssignmentSummary.getTaskId()), taskAssignmentSummary.getTaskName(), userId));
                                 }
                             });
                             reassignments.forEach(taskAssignmentSummary -> {
                                 if (taskAssignmentSummary.isDelegationAllowed()) {
                                     taskRefreshed.fire(new TaskRefreshedEvent());
                                     return;
                                 }
                             });
                             placeManager.closePlace(place);
                         },
                         new DefaultErrorCallback() {

                             @Override
                             public boolean error(Message message,
                                                  Throwable throwable) {
                                 displayUnsuccessfulNotification(constants.UnableToPerformReassignment(userId, throwable.getMessage()));
                                 return false;
                             }
                         }).delegateTasks(serverTemplateId, tasksToReassign, userId);
    }

    public void displayNotification(String text) {
        notificationEvent.fire(new NotificationEvent(text));
    }

    public void displayUnsuccessfulNotification(String text) {
        notificationEvent.fire(new NotificationEvent(text, NotificationEvent.NotificationType.WARNING));
    }

    @OnOpen
    public void onOpen() {
        serverTemplateId = place.getParameter("serverTemplateId", "").toString();
        deploymentIds = place.getParameter("deploymentIds", "").toString().split(",");
        taskIdsStr = place.getParameter("taskIds", "-1").split(",");
        tasksToReassign = new ArrayList();
        for (int i = 0; i < taskIdsStr.length; i++) {
            tasksToReassign.add(new TaskKey(serverTemplateId, deploymentIds[i], Long.valueOf(taskIdsStr[i])));
        }
    }

    public interface TasksReassignmentView extends UberView<TasksReassignmentPresenter> {

    }
}
