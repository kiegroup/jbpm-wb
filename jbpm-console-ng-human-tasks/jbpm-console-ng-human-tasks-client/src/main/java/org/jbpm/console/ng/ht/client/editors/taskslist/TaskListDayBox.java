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

import java.util.List;

import org.jbpm.console.ng.ht.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * 
 * @author salaboy
 */
public class TaskListDayBox extends Composite implements RequiresResize {

    private List<TaskSummary> taskSummaries;
    private FlowPanel taskListBox = new FlowPanel();
    private FlowPanel dayTaskContainer = new FlowPanel();
    private FlowPanel top = new FlowPanel();
    private FlowPanel fluidRow = new FlowPanel();
    private FlowPanel span12 = new FlowPanel();
    private IconAnchor iconAndDayName = new IconAnchor();
    private Collapse collapsible = new Collapse();
    private TasksListPresenter presenter;
    private Identity identity;
    private PlaceManager placeManager;
    
    private boolean tasksVisible = true;

    public TaskListDayBox(String day, List<TaskSummary> taskSummaries, Identity identity, PlaceManager placeManager,
            TasksListPresenter presenter) {
        fluidRow.setStyleName("row-fluid");
        span12.setStyleName("span12");
        fluidRow.add(span12);

        taskListBox.setStyleName("tasks-list");
        dayTaskContainer.setStyleName("day-tasks-container");
        top.setStyleName("top");
        iconAndDayName.setText(day + " (" + taskSummaries.size() + ")");
        iconAndDayName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                collapsible.toggle();
                if (tasksVisible) {
                    iconAndDayName.setIcon(IconType.DOUBLE_ANGLE_DOWN);
                } else {
                    iconAndDayName.setIcon(IconType.DOUBLE_ANGLE_UP);
                }
                tasksVisible = !tasksVisible;
            }
        });
        top.add(iconAndDayName);
        dayTaskContainer.add(top);
        span12.add(taskListBox);
        collapsible.add(fluidRow);
        if (taskSummaries.size() > 0) {
            collapsible.setDefaultOpen(true);
            tasksVisible = true;
            iconAndDayName.setIcon(IconType.DOUBLE_ANGLE_UP);
        } else {
            tasksVisible = false;
            iconAndDayName.setIcon(IconType.DOUBLE_ANGLE_DOWN);
        }
        dayTaskContainer.add(collapsible);
        initWidget(dayTaskContainer);
        this.taskSummaries = taskSummaries;
        taskListBox.clear();
        for (TaskSummary ts : this.taskSummaries) {
            taskListBox.add(new TaskBox(placeManager, presenter, identity, ts.getId(), ts.getName(), ts.getActualOwner(), ts
                    .getPotentialOwners(), ts.getStatus()));
        }
    }

    public void setPresenter(TasksListPresenter presenter) {
        this.presenter = presenter;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public void setTaskSummaries(List<TaskSummary> taskSummaries) {
        this.taskSummaries = taskSummaries;
    }

    public List<TaskSummary> getTaskSummaries() {
        return taskSummaries;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void setPlaceManager(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public void onResize() {
    }
}
