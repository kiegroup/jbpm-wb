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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;

public class TaskBox extends Composite {

    private String taskName = "Default Task Name";
    private FocusPanel taskContainer = new FocusPanel();

    private FlowPanel taskPanel = new FlowPanel();
    private FlowPanel hourPanel = new FlowPanel();
    private FlowPanel taskPriorityPanel = new FlowPanel();
    private FlowPanel taskNamePanel = new FlowPanel();
    private FlowPanel taskOptions = new FlowPanel();
    private Label taskNameLabel = new Label();
    private long taskId = -1;
    private String actualOwner;
    private List<String> potentialOwners;
    private String status;
    private int priority;
    private String hour;
    private TasksListPresenter presenter;
    private Identity identity;
    private PlaceManager placeManager;

    public TaskBox() {

        taskPanel.setStyleName("task");
        taskNamePanel.add(taskNameLabel);
        hourPanel.setStyleName("hour");
        taskPanel.add(taskPriorityPanel);
        taskPanel.add(hourPanel);
        taskNamePanel.setStyleName("task-name");
        taskPanel.add(taskNamePanel);
        taskOptions.setStyleName("task-options");
        taskPanel.add(taskOptions);
        taskContainer.add(taskPanel);

        // All composites must call initWidget() in their constructors.
        initWidget(taskContainer);

    }

    public TaskBox(final PlaceManager placeManager,
            final TasksListPresenter presenter,
            final Identity identity,
            final long taskId,
            final String taskName,
            final String actualOwner,
            final List<String> potentialOwners,
            final String status, final int priority, String hour) {
        this();
        this.taskId = taskId;
        this.taskName = taskName;
        taskNameLabel.setText(taskName);
        this.actualOwner = actualOwner;
        this.potentialOwners = potentialOwners == null ? Collections.EMPTY_LIST : potentialOwners;
        this.status = status;
        this.presenter = presenter;
        this.identity = identity;
        this.priority = priority;
        this.hour = hour;

        hourPanel.add(new Label(hour));

        if (priority == 0 || priority == 1) {
            taskPriorityPanel.setStyleName("priority five");
        } else if (priority == 2 || priority == 3) {
            taskPriorityPanel.setStyleName("priority four");
        } else if (priority == 4 || priority == 5) {
            taskPriorityPanel.setStyleName("priority three");
        } else if (priority == 6 || priority == 7) {
            taskPriorityPanel.setStyleName("priority two");
        } else if (priority == 8 || priority == 9 || priority == 10) {
            taskPriorityPanel.setStyleName("priority one");
        }

        taskContainer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String currentView = presenter.getCurrentView().toString();
                String currentTaskType = presenter.getCurrentTaskType().toString();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Tasks With Details");
                placeRequestImpl.addParameter("taskId", String.valueOf(taskId));
                placeRequestImpl.addParameter("taskName", taskName);
                placeRequestImpl.addParameter("currentView", currentView);
                placeRequestImpl.addParameter("currentTaskType", currentTaskType);
                if(presenter.getCurrentDate() != null){
                    placeRequestImpl.addParameter("currentDate", String.valueOf(presenter.getCurrentDate().getTime()));
                }
                placeRequestImpl.addParameter("currentFilter", presenter.getCurrentFilter());
                placeManager.goTo(placeRequestImpl);
            }
        });

        List<FocusPanel> options = new ArrayList<FocusPanel>();
        FlowPanel personalOrGroupTask = new FlowPanel();
        //Claim
        if ("".equals(actualOwner) && status.equals("Ready")) {
            if (!potentialOwners.isEmpty() && !(potentialOwners.size() == 1 && potentialOwners.contains("User:" + identity.getName()))) {
                personalOrGroupTask.setStyleName("group-task");
                personalOrGroupTask.add(new HTML("Group Task"));
            } else {
                personalOrGroupTask.setStyleName("personal-task");
                personalOrGroupTask.add(new HTML("Personal Task"));
            }
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.claimTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Claim"));
            panel.setStyleName("clickable claim");
            options.add(focusPanel);

        } //Release
        else if (!"".equals(actualOwner) && actualOwner.equals(identity.getName())
                && potentialOwners != null && !potentialOwners.isEmpty()
                && (status.equals("Reserved") || status.equals("InProgress"))) {

            if (!potentialOwners.isEmpty()
                    && !(potentialOwners.size() == 1 && potentialOwners.contains("User:" + identity.getName()))) {
                personalOrGroupTask.setStyleName("group-task");
                personalOrGroupTask.add(new HTML("Group Task"));
            } else {
                personalOrGroupTask.setStyleName("personal-task");
                personalOrGroupTask.add(new HTML("Personal Task"));
            }
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.releaseTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Release"));
            panel.setStyleName("clickable release");
            options.add(focusPanel);

        } else {
            personalOrGroupTask.setStyleName("personal-task");
            personalOrGroupTask.add(new HTML("Personal Task"));
        }
        //Start
        if (status.equals("Reserved") && actualOwner.equals(identity.getName())) {
            FlowPanel panel = new FlowPanel();
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    List<Long> tasks = new ArrayList<Long>(1);
                    tasks.add(taskId);
                    presenter.startTasks(tasks, identity.getName());
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Start"));
            panel.setStyleName("clickable start");
            options.add(focusPanel);

        }
        //Complete
        if (status.equals("InProgress")) {
            FlowPanel panel = new FlowPanel();
            taskPanel.setStyleName("task in-progress");
            FocusPanel focusPanel = new FocusPanel(panel);
            focusPanel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display");
                    placeRequestImpl.addParameter("taskId", Long.toString(taskId));

                    placeManager.goTo(placeRequestImpl);
                    event.stopPropagation();
                }
            });
            panel.add(new HTML("Complete"));
            panel.setStyleName("clickable complete");
            options.add(focusPanel);
        }

        for (FocusPanel p : options) {
            taskOptions.add(p);
        }

        taskOptions.add(personalOrGroupTask);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        taskNameLabel.setText(taskName);
    }

}
