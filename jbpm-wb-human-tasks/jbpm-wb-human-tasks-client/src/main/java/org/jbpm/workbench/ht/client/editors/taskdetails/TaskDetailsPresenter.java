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
import org.jbpm.workbench.common.client.util.UTCDateBox;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

@Dependent
public class TaskDetailsPresenter {

    @Inject
    private TranslationService translationService;

    @Inject
    protected Event<TaskRefreshedEvent> taskRefreshed;

    TaskDetailsView view;

    private Constants constants = Constants.INSTANCE;

    @Inject
    private Caller<TaskService> taskService;

    private long currentTaskId = 0;

    private String currentServerTemplateId;

    private String currentContainerId;

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

        if (currentTaskId > 0) {

            taskService.call((Void) -> {
                view.displayNotification(constants.TaskDetailsUpdatedForTaskId(currentTaskId));
                taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
            }).updateTask(currentServerTemplateId,
                          currentContainerId,
                          currentTaskId,
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
                                  String processId) {
        view.setTaskDescription(description);
        final Long date = UTCDateBox.date2utc(expirationTime);
        if (date != null) {
            view.setDueDate(date);
            view.setDueDateTime(date);
        }
        view.setUser(actualOwner);
        view.setTaskStatus(status);
        view.setTaskPriority(priority);
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
        view.setDueDateTimeEnabled(false);
        view.setTaskPriorityEnabled(false);
        view.setUpdateTaskVisible(false);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentTaskId = event.getTaskId();
        this.currentServerTemplateId = event.getServerTemplateId();
        this.currentContainerId = event.getContainerId();
        if (event.isForLog()) {
            setReadOnlyTaskDetail();
        }

        setTaskDetails(translationService.format(event.getStatus()),
                       event.getDescription(),
                       event.getActualOwner(),
                       event.getExpirationTime(),
                       String.valueOf(event.getPriority()),
                       event.getProcessInstanceId(),
                       event.getProcessId());
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            taskService.call((TaskSummary task) -> {
                setTaskDetails(translationService.format(task.getStatus()),
                               task.getDescription(),
                               task.getActualOwner(),
                               task.getExpirationTime(),
                               String.valueOf(task.getPriority()),
                               task.getProcessInstanceId(),
                               task.getProcessId());
            }).getTask(currentServerTemplateId,
                       currentContainerId,
                       currentTaskId);
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

        void setDueDate(Long date);

        void setDueDateEnabled(Boolean enabled);

        void setDueDateTime(Long time);

        void setDueDateTimeEnabled(Boolean enabled);

        void setUser(String user);

        void setTaskStatus(String status);

        void setTaskPriority(String priority);

        void setProcessInstanceId(String none);

        void setProcessId(String none);

        void setTaskPriorityEnabled(Boolean enabled);

        void setUpdateTaskVisible(Boolean enabled);

        void displayNotification(final String text);
    }
}
