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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class TaskLogsPresenter extends AbstractTaskPresenter {

    public static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private List<TaskEventSummary> visibleLogs = new ArrayList<>();

    private TaskLogsView view;

    private Caller<TaskService> taskService;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Inject
    public TaskLogsPresenter(final TaskLogsView view,
                             final Caller<TaskService> taskService) {
        this.view = view;
        this.taskService = taskService;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public void setCurrentPage(int i) {
        this.currentPage = i;
    }

    public IsWidget getView() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void refreshLogs() {
        currentPage = 0;
        loadTaskLogs();
    }

    public void loadTaskLogs() {
        taskService.call((final List<TaskEventSummary> events) -> {
            if (currentPage == 0) {
                visibleLogs = new ArrayList();
            }
            visibleLogs.addAll(events);
            view.hideLoadButton(events.size() < PAGE_SIZE);
            view.setLogs(visibleLogs.stream().collect(Collectors.toList()));
        }).getTaskEvents(getServerTemplateId(),
                         getContainerId(),
                         getTaskId(),
                         currentPage,
                         getPageSize());
    }

    public void loadMoreProcessInstanceLogs() {
        setCurrentPage(currentPage + 1);
        loadTaskLogs();
    }

    public void onTaskSelectionEvent(@Observes final TaskSelectionEvent event) {
        setSelectedTask(event);
        refreshLogs();
    }

    public void onTaskRefreshedEvent(@Observes final TaskRefreshedEvent event) {
        if (isSameTaskFromEvent().test(event)) {
            refreshLogs();
        }
    }

    public interface TaskLogsView extends UberElemental<TaskLogsPresenter> {

        void setLogs(List<TaskEventSummary> logs);

        void hideLoadButton(boolean hidden);

    }
}
