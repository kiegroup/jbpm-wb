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
package org.jbpm.workbench.ht.client.editors.taskadmin;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Dependent
public class TaskAdminPresenter {

    @Inject
    protected Caller<TaskService> taskService;

    private Constants constants = Constants.INSTANCE;

    @Inject
    private TaskAdminView view;

    @Inject
    private User identity;

    private long currentTaskId = 0;

    private String serverTemplateId;

    private String containerId;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    public void setTaskService(final Caller<TaskService> taskService) {
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getView() {
        return view;
    }

    public void forwardTask(final String entity) {
        taskService.call(nothing -> {
            notification.fire(new NotificationEvent(constants.TaskSuccessfullyForwarded()));
            taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
            refreshTaskPotentialOwners();
        }).forward(serverTemplateId,
                   containerId,
                   currentTaskId,
                   entity);
    }

    public void reminder() {
        taskService.call(ts -> notification.fire(new NotificationEvent(constants.ReminderSentTo(identity.getIdentifier()))))
                .executeReminderForTask(serverTemplateId,
                                        containerId,
                                        currentTaskId,
                                        identity.getIdentifier());
    }

    public void refreshTaskPotentialOwners() {
        view.enableReminderButton(false);
        view.enableForwardButton(false);
        view.enableUserOrGroupText(false);
        view.setUsersGroupsControlsPanelText(emptyList());
        view.clearUserOrGroupText();
        view.setActualOwnerText("");
        taskService.call((TaskAssignmentSummary ts) -> {
                             if (ts == null) {
                                 return;
                             }
                             if (ts.getPotOwnersString() == null || ts.getPotOwnersString().isEmpty()) {
                                 view.setUsersGroupsControlsPanelText(singletonList(Constants.INSTANCE.No_Potential_Owners()));
                             } else {
                                 view.setUsersGroupsControlsPanelText(ts.getPotOwnersString());
                             }

                             view.enableForwardButton(ts.isForwardAllowed());
                             view.enableUserOrGroupText(ts.isForwardAllowed());

                             if (ts.getActualOwner() == null || ts.getActualOwner().equals("")) {
                                 view.enableReminderButton(false);
                                 view.setActualOwnerText(Constants.INSTANCE.No_Actual_Owner());
                             } else {
                                 view.enableReminderButton(true);
                                 view.setActualOwnerText(ts.getActualOwner());
                             }
                         }
        ).getTaskAssignmentDetails(serverTemplateId,
                                   containerId,
                                   currentTaskId);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        if (!event.isForLog()) {
            this.currentTaskId = event.getTaskId();
            serverTemplateId = event.getServerTemplateId();
            containerId = event.getContainerId();
            refreshTaskPotentialOwners();
        }
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshTaskPotentialOwners();
        }
    }

    public interface TaskAdminView extends UberView<TaskAdminPresenter> {

        void setUsersGroupsControlsPanelText(List<String> text);

        void enableForwardButton(boolean enabled);

        void enableUserOrGroupText(boolean enabled);

        void enableReminderButton(boolean enabled);

        void setActualOwnerText(String actualOwnerText);

        void clearUserOrGroupText();
    }
}
