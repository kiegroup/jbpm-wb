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
package org.jbpm.workbench.ht.client.editors.taskdetails;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

@Dependent
public class TaskDetailsPresenter extends AbstractTaskPresenter {

    @Inject
    private TranslationService translationService;

    @Inject
    protected Event<TaskRefreshedEvent> taskRefreshed;

    protected TaskDetailsView view;

    private Constants constants = Constants.INSTANCE;

    @Inject
    private Caller<TaskService> taskService;

    @Inject
    public TaskDetailsPresenter(TaskDetailsView view,
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

    public void updateTask(final String taskDescription,
                           final Date dueDate,
                           final int priority) {

        if (getTaskId() != null) {

            taskService.call((Void) -> {
                view.displayNotification(constants.TaskDetailsUpdatedForTaskId(getTaskId()));
                taskRefreshed.fire(new TaskRefreshedEvent(getServerTemplateId(),
                                                          getContainerId(),
                                                          getTaskId()));
            }).updateTask(getServerTemplateId(),
                          getContainerId(),
                          getTaskId(),
                          priority,
                          taskDescription,
                          dueDate);
        }
    }

    protected void setTaskDetails(String status,
                                  String description,
                                  String actualOwner,
                                  Date expirationTime,
                                  String priority,
                                  Long processInstanceId,
                                  String processId,
                                  Integer slaCompliance) {
        view.setTaskDescription(description);
        view.setSelectedDate(expirationTime);
        view.setUser(actualOwner);
        view.setTaskStatus(status);
        view.setTaskPriority(priority);
        view.setSlaCompliance(slaCompliance);
        if (processInstanceId == null) {
            view.setProcessInstanceId("");
            view.setProcessId("");
            return;
        }

        view.setProcessInstanceId(String.valueOf(processInstanceId));
        view.setProcessId(processId);
    }

    public void setReadOnlyTaskDetail() {
        view.setTaskDescriptionEnabled(false);
        view.setDueDateEnabled(false);
        view.setTaskPriorityEnabled(false);
        view.setUpdateTaskVisible(false);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        setSelectedTask(event);
        if (event.isForLog()) {
            setReadOnlyTaskDetail();
        }

        setTaskDetails(translationService.format(event.getStatus()),
                       event.getDescription(),
                       event.getActualOwner(),
                       event.getExpirationTime(),
                       String.valueOf(event.getPriority()),
                       event.getProcessInstanceId(),
                       event.getProcessId(),
                       event.getSlaCompliance());
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (isSameTaskFromEvent().test(event)) {
            taskService.call(
                    (TaskSummary task) -> {
                        if (task != null) {
                            setTaskDetails(translationService.format(task.getStatus()),
                                           task.getDescription(),
                                           task.getActualOwner(),
                                           task.getExpirationTime(),
                                           String.valueOf(task.getPriority()),
                                           task.getProcessInstanceId(),
                                           task.getProcessId(),
                                           task.getSlaCompliance());
                        }
                    }).getTask(getServerTemplateId(), getContainerId(), getTaskId());
        }
    }

    @Inject
    public void setTranslationService(TranslationService translationService) {
        this.translationService = translationService;
    }

    public interface TaskDetailsView extends IsWidget {

        void init(final TaskDetailsPresenter presenter);

        void setTaskDescription(String text);

        void setTaskDescriptionEnabled(Boolean enabled);

        void setSelectedDate(Date date);

        void setDueDateEnabled(Boolean enabled);

        void setUser(String user);

        void setTaskStatus(String status);

        void setSlaCompliance(Integer slaCompliance);

        void setTaskPriority(String priority);

        void setProcessInstanceId(String none);

        void setProcessId(String none);

        void setTaskPriorityEnabled(Boolean enabled);

        void setUpdateTaskVisible(Boolean enabled);

        void displayNotification(final String text);
    }
}
