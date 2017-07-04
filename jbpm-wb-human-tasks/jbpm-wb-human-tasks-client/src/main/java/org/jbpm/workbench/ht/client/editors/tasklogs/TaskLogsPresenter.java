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
package org.jbpm.workbench.ht.client.editors.tasklogs;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Dependent
public class TaskLogsPresenter {

    private TaskLogsView view;

    private Caller<TaskService> taskService;

    private long currentTaskId = 0;

    private String serverTemplateId;

    private String containerId;

    @Inject
    public TaskLogsPresenter(final TaskLogsView view,
                             final Caller<TaskService> taskService) {
        this.view = view;
        this.taskService = taskService;
    }

    public IsWidget getView() {
        return view;
    }

    public void refreshLogs() {
        view.setLogTextAreaText(emptyList());
        taskService.call(
                new RemoteCallback<List<TaskEventSummary>>() {
                    @Override
                    public void callback(final List<TaskEventSummary> events) {
                        view.setLogTextAreaText(events.stream().map(e -> summaryToString(e)).collect(toList()));
                    }

                    public String summaryToString(TaskEventSummary tes) {
                        String timeStamp = DateUtils.getDateTimeStr(tes.getLogTime());
                        String additionalDetail = "UPDATED".equals(tes.getType()) ? tes.getMessage() : tes.getUserId();
                        return timeStamp + ": Task " + tes.getType() + " (" + additionalDetail + ")";
                    }
                }
        ).getTaskEvents(serverTemplateId,
                        containerId,
                        currentTaskId);
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        this.currentTaskId = event.getTaskId();
        this.containerId = event.getContainerId();
        this.serverTemplateId = event.getServerTemplateId();
        refreshLogs();
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshLogs();
        }
    }

    public interface TaskLogsView extends IsWidget {

        void displayNotification(String text);

        void setLogTextAreaText(List<String> logs);
    }
}
