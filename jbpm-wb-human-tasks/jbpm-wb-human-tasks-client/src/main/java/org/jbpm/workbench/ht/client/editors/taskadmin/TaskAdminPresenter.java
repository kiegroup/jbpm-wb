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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.ht.client.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

@Dependent
public class TaskAdminPresenter {

    public interface TaskAdminView extends IsWidget {

        void displayNotification( String text );

        FormControlStatic getUsersGroupsControlsPanel();

        Button getForwardButton();

        TextBox getUserOrGroupText();

        Button getReminderButton();

        FormControlStatic getActualOwnerPanel();

        void init( final TaskAdminPresenter presenter );
    }

    private Constants constants = Constants.INSTANCE;

    @Inject
    private TaskAdminView view;

    @Inject
    private User identity;

    @Inject
    protected Caller<TaskService> taskService;

    private long currentTaskId = 0;
    private String serverTemplateId;
    private String containerId;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }

    public void forwardTask(String entity) {
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskSuccessfullyForwarded());
                        taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                        refreshTaskPotentialOwners();
                    }

                }
        ).delegate(serverTemplateId, containerId, currentTaskId, entity);
    }

    public void reminder() {
        taskService.call(
                new RemoteCallback<TaskAssignmentSummary>() {
                    @Override
                    public void callback(TaskAssignmentSummary ts) {
                        view.displayNotification(constants.ReminderSentTo(identity.getIdentifier()));
                    }
                }
        ).executeReminderForTask(serverTemplateId, containerId, currentTaskId, identity.getIdentifier());
    }

    public void refreshTaskPotentialOwners() {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(currentTaskId);

        taskService.call(
                new RemoteCallback<TaskAssignmentSummary>() {
                    @Override
                    public void callback(TaskAssignmentSummary ts) {
                        if (ts == null) {
                            view.getReminderButton().setEnabled(false);
                            view.getForwardButton().setEnabled(false);
                            view.getUserOrGroupText().setEnabled(false);
                            return;
                        }
                        if (ts.getPotOwnersString() != null && ts.getPotOwnersString().isEmpty()) {
                            view.getUsersGroupsControlsPanel().setText(Constants.INSTANCE.No_Potential_Owners());
                        } else {
                            view.getUsersGroupsControlsPanel().setText("" + ts.getPotOwnersString().toString());
                        }
                        view.getForwardButton().setEnabled(true);
                        view.getUserOrGroupText().setEnabled(true);

                        if (ts.getActualOwner() == null || ts.getActualOwner().equals("")) {
                            view.getReminderButton().setEnabled(false);
                            view.getActualOwnerPanel().setText(Constants.INSTANCE.No_Actual_Owner());
                        } else {
                            view.getReminderButton().setEnabled(true);
                            view.getActualOwnerPanel().setText(ts.getActualOwner());
                        }
                    }
                }
        ).getTaskAssignmentDetails(serverTemplateId, containerId, currentTaskId);
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        serverTemplateId = event.getServerTemplateId();
        containerId = event.getContainerId();
        refreshTaskPotentialOwners();
    }

    public void onTaskRefreshedEvent( @Observes TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshTaskPotentialOwners();
        }
    }
}
