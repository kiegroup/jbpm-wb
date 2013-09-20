/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.editors.taskslist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;

import java.util.Date;

import javax.enterprise.event.Event;

import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;

public class TaskListMultiDayBox extends Composite implements RequiresResize {

    private FlowPanel tasksContainer = new FlowPanel();

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<TaskSelectionEvent> taskSelection;
    
    @Inject
    private Event<TaskCalendarEvent> taskCalendarEvent;
    
    private TasksListPresenter presenter;

    private Map<Day, List<TaskSummary>> sectionTasks = new LinkedHashMap<Day, List<TaskSummary>>();
    
    private Long idTaskSelected;

    public TaskListMultiDayBox() {
    }

    public TasksListPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(TasksListPresenter presenter) {
        this.presenter = presenter;
    }

    public void init() {
        tasksContainer.setStyleName("task-container");
        initWidget(tasksContainer);
    }

    public void refresh() {

        for (Day section : sectionTasks.keySet()) {
            TaskListDayBox taskList = new TaskListDayBox(section, sectionTasks.get(section), identity, placeManager, taskSelection, presenter, 
                    taskCalendarEvent, this.idTaskSelected);
            taskList.init();
            tasksContainer.add(taskList);
        }
    }
    
    public void addTasksByDay(String day, List<TaskSummary> taskSummaries) {
        sectionTasks.put(new Day(new Date(), day), taskSummaries);
    }

    public void addTasksByDay(Day day, List<TaskSummary> taskSummaries) {
        sectionTasks.put(day, taskSummaries);
    }

    public void clear() {
        tasksContainer.clear();
        sectionTasks.clear();
    }

    @Override
    public void onResize() {
    }

    public Long getIdTaskSelected() {
        return idTaskSelected;
    }

    public void setIdTaskSelected(Long idTaskSelected) {
        this.idTaskSelected = idTaskSelected;
    }

}
