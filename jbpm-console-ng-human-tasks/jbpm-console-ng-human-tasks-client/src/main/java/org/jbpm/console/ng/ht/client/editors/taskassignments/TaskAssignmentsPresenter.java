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
package org.jbpm.console.ng.ht.client.editors.taskassignments;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
public class TaskAssignmentsPresenter {

    public interface TaskAssignmentsView extends IsWidget {

        void init(final TaskAssignmentsPresenter presenter);

        void displayNotification(String text);

        void setPotentialOwnersInfo(String text);

        void enableDelegateButton(boolean enable);

        void setDelegateButtonActive(boolean enable);

        void clearUserOrGroupInput();

        void enableUserOrGroupInput(boolean enable);

        void setHelpText(String text);
    }

    private Constants constants = Constants.INSTANCE;
    private TaskAssignmentsView view;
    private User identity;
    private Caller<TaskLifeCycleService> taskLifecycleService;
    private Caller<TaskOperationsService> taskOperationsService;
    private Event<TaskRefreshedEvent> taskRefreshed;
    private long currentTaskId = 0;

    @Inject
    public TaskAssignmentsPresenter(
            TaskAssignmentsView view,
            User identity,
            Caller<TaskLifeCycleService> taskLifecycleService,
            Caller<TaskOperationsService> taskOperationsService,
            Event<TaskRefreshedEvent> taskRefreshed
    ) {
        this.view = view;
        this.identity = identity;
        this.taskLifecycleService = taskLifecycleService;
        this.taskOperationsService = taskOperationsService;
        this.taskRefreshed = taskRefreshed;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public IsWidget getView() {
        return view;
    }

    public void delegateTask(String entity) {
        if (entity == null || "".equals(entity.trim())) {
            view.setHelpText(constants.DelegationUserInputRequired());
            return;
        }
        taskLifecycleService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskSuccessfullyDelegated());
                        view.setDelegateButtonActive(false);
                        view.setHelpText(constants.DelegationSuccessfully());
                        taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                        refreshTaskPotentialOwners();
                    }
                },
                new DefaultErrorCallback() {

                    @Override
                    public boolean error(Message message, Throwable throwable) {
                        view.setDelegateButtonActive(true);
                        view.setHelpText(constants.DelegationUnable());
                        return super.error(message, throwable);
                    }
                }
        ).delegate(currentTaskId, identity.getIdentifier(), entity);
    }

    public void refreshTaskPotentialOwners() {
        if (currentTaskId != 0) {
            view.enableDelegateButton(false);
            view.enableUserOrGroupInput(false);
            view.setPotentialOwnersInfo("");

            taskOperationsService.call(new RemoteCallback<TaskSummary>() {
                @Override
                public void callback(final TaskSummary response) {
                    if (response.getPotOwnersString() == null || response.getPotOwnersString().isEmpty()) {
                        view.setPotentialOwnersInfo(constants.No_Potential_Owners());
                    } else {
                        view.setPotentialOwnersInfo(response.getPotOwnersString().toString());
                    }
                }
            }, new DefaultErrorCallback()).getTaskDetails(currentTaskId);

            final Set<String> groups = new HashSet<String>();
            for( final Group group : identity.getGroups()){
                groups.add( group.getName() );
            }
            for( final Role role : identity.getRoles()){
                groups.add( role.getName() );
            }

            taskOperationsService.call(new RemoteCallback<Boolean>() {
                @Override
                public void callback(final Boolean delegateEnabled) {
                    view.enableDelegateButton(delegateEnabled);
                    view.enableUserOrGroupInput(delegateEnabled);
                }
            }, new DefaultErrorCallback()).allowDelegate(currentTaskId, identity.getIdentifier(), groups);
        }
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentTaskId = event.getTaskId();
        view.setHelpText("");
        view.clearUserOrGroupInput();
        refreshTaskPotentialOwners();
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshTaskPotentialOwners();
        }
    }
}
