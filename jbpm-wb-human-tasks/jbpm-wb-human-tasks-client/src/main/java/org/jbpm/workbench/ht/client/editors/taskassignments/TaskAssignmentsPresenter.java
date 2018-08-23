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
package org.jbpm.workbench.ht.client.editors.taskassignments;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Dependent
public class TaskAssignmentsPresenter extends AbstractTaskPresenter {

    private Constants constants = Constants.INSTANCE;
    private TaskAssignmentsView view;
    private Caller<TaskService> taskService;
    private Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    public TaskAssignmentsPresenter(TaskAssignmentsView view,
                                    Caller<TaskService> taskService,
                                    Event<TaskRefreshedEvent> taskRefreshed) {
        this.view = view;
        this.taskService = taskService;
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
        taskService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void nothing) {
                        view.displayNotification(constants.TaskSuccessfullyDelegated());
                        view.setDelegateButtonActive(false);
                        view.setHelpText(constants.DelegationSuccessfully());
                        taskRefreshed.fire(new TaskRefreshedEvent(getServerTemplateId(),
                                                                  getContainerId(),
                                                                  getTaskId()));
                        refreshTaskPotentialOwners();
                    }
                },
                new DefaultErrorCallback() {

                    @Override
                    public boolean error(Message message,
                                         Throwable throwable) {
                        view.setDelegateButtonActive(true);
                        view.setHelpText(constants.DelegationUnable());
                        return super.error(message,
                                           throwable);
                    }
                }
        ).delegate(getServerTemplateId(),
                   getContainerId(),
                   getTaskId(),
                   entity);
    }

    public void refreshTaskPotentialOwners() {
        if (getTaskId() != null) {
            view.enableDelegateButton(false);
            view.enableUserOrGroupInput(false);
            view.setPotentialOwnersInfo(emptyList());

            taskService.call(new RemoteCallback<TaskAssignmentSummary>() {
                @Override
                public void callback(final TaskAssignmentSummary response) {
                    if (response == null || response.getPotOwnersString() == null || response.getPotOwnersString().isEmpty()) {
                        view.setPotentialOwnersInfo(singletonList(constants.No_Potential_Owners()));
                    } else {
                        view.setPotentialOwnersInfo(response.getPotOwnersString());
                        view.enableDelegateButton(response.isDelegationAllowed());
                        view.enableUserOrGroupInput(response.isDelegationAllowed());
                    }
                }
            }).getTaskAssignmentDetails(getServerTemplateId(),
                                        getContainerId(),
                                        getTaskId());
        }
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        if (!event.isForLog()) {
            setSelectedTask(event);
            view.setHelpText("");
            view.clearUserOrGroupInput();
            refreshTaskPotentialOwners();
        }
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (isSameTaskFromEvent().test(event)) {
            refreshTaskPotentialOwners();
        }
    }

    public interface TaskAssignmentsView extends IsWidget {

        void init(final TaskAssignmentsPresenter presenter);

        void displayNotification(String text);

        void setPotentialOwnersInfo(List<String> owners);

        void enableDelegateButton(boolean enable);

        void setDelegateButtonActive(boolean enable);

        void clearUserOrGroupInput();

        void enableUserOrGroupInput(boolean enable);

        void setHelpText(String text);
    }
}
