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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

@Dependent
public class TaskLogsPresenter {

    public interface TaskLogsView extends IsWidget {

        void init( final TaskLogsPresenter presenter );

        void displayNotification( final String text );

        void setLogTextAreaText( final String text );

    }

    private TaskLogsView view;

    private Caller<TaskService> taskService;

    private long currentTaskId = 0;
    private String serverTemplateId;
    private String containerId;

    @Inject
    public TaskLogsPresenter( final TaskLogsView view, final Caller<TaskService> taskService) {
        this.view = view;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }

    public void refreshLogs() {
        view.setLogTextAreaText("");
        taskService.call(
                new RemoteCallback<List<TaskEventSummary>>() {
                    @Override
                    public void callback(List<TaskEventSummary>events) {
                        SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                        for (TaskEventSummary tes : events) {
                            String summaryStr = summaryToString(tes);
                            safeHtmlBuilder.appendEscapedLines(summaryStr);
                        }
                        view.setLogTextAreaText(safeHtmlBuilder.toSafeHtml().asString());
                    }

                    public String summaryToString(TaskEventSummary tes) {
                        String timeStamp = DateUtils.getDateTimeStr(tes.getLogTime());
                        String additionalDetail = "UPDATED".equals(tes.getType()) ? tes.getMessage() : tes.getUserId();
                        return timeStamp + ": Task " + tes.getType() + " (" + additionalDetail + ")\n";
                    }
                }
        ).getTaskEvents( serverTemplateId, containerId, currentTaskId );
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        this.containerId = event.getContainerId();
        this.serverTemplateId = event.getServerTemplateId();
        refreshLogs();
    }

    public void onTaskRefreshedEvent( @Observes final TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshLogs();
        }
    }
}
