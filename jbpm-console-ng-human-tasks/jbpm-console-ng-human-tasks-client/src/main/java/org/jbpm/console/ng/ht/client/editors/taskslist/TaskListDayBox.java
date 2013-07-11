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

import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class TaskListDayBox extends Composite implements RequiresResize {
    private static IconType ICON_TASKS_COLLAPSED = IconType.DOUBLE_ANGLE_DOWN;
    private static IconType ICON_TASKS_VISIBLE = IconType.DOUBLE_ANGLE_UP;

    private Day day;
    private List<TaskSummary> taskSummaries;
    private Identity identity;
    private PlaceManager placeManager;
    private TasksListPresenter presenter;
    private FlowPanel taskListBox = new FlowPanel();
    private FlowPanel dayTaskContainer = new FlowPanel();
    private FlowPanel top = new FlowPanel();
    private FlowPanel fluidRow = new FlowPanel();
    private FlowPanel span12 = new FlowPanel();
    private IconAnchor iconAndDayName = new IconAnchor();
    private Collapse collapsible = new Collapse();

    private boolean tasksCollapsed = false;

    public TaskListDayBox(Day day, List<TaskSummary> taskSummaries, Identity identity, PlaceManager placeManager,
            TasksListPresenter presenter) {
        this.day = day;
        this.taskSummaries = taskSummaries;
        this.identity = identity;
        this.placeManager = placeManager;
        this.presenter = presenter;
    }

    public void init() {
        fluidRow.setStyleName("row-fluid");
        span12.setStyleName("span12");
        fluidRow.add(span12);

        taskListBox.setStyleName("tasks-list");
        dayTaskContainer.setStyleName("day-tasks-container");
        top.setStyleName("top");
        DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy");
        String dayAndDate = day.getDayOfWeekName();// + " - " + fmt.format(day.getDate());
        iconAndDayName.setText(dayAndDate + " (" + taskSummaries.size() + ")");
        // show/hide the tasks when the icon is clicked
        iconAndDayName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                collapsible.toggle();
                toggleIcon();
            }
        });

        top.add(iconAndDayName);
        dayTaskContainer.add(top);
        span12.add(taskListBox);
        collapsible.add(fluidRow);
        dayTaskContainer.add(collapsible);

        if (taskSummaries.size() > 0) {
            collapsible.setDefaultOpen(true);
            tasksCollapsed = false;
            iconAndDayName.setIcon(ICON_TASKS_VISIBLE);
        } else {
            tasksCollapsed = true;
            iconAndDayName.setIcon(ICON_TASKS_COLLAPSED);
        }
        initWidget(dayTaskContainer);
        taskListBox.clear();
        for (TaskSummary ts : this.taskSummaries) {
            String hour = "";
            if(ts.getExpirationTime() != null){
                fmt = DateTimeFormat.getFormat("hh:mm a");
                hour = fmt.format(ts.getExpirationTime());
            }
            taskListBox.add(new TaskBox(placeManager, presenter, identity, ts.getId(), ts.getName(), ts.getActualOwner(), ts
                    .getPotentialOwners(), ts.getStatus(), ts.getPriority(), hour));
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

    /**
     * Changes the icon based on {@code tasksCollapsed} flag.
     * 
     * @param iconAnchor component for which toggle the icon
     */
    private void toggleIcon() {
        if (tasksCollapsed) {
            iconAndDayName.setIcon(ICON_TASKS_VISIBLE);
        } else {
            iconAndDayName.setIcon(ICON_TASKS_COLLAPSED);
        }
        tasksCollapsed = !tasksCollapsed;
    }

}
