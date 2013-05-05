/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.ht.client.editors.taskslist;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import javax.enterprise.event.Observes;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.security.Identity;

import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite
        implements
        TasksListPresenter.TaskListView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private TasksListPresenter presenter;
    @Inject
    @DataField
    public NavLink dayViewTasksNavLink;
    @Inject
    @DataField
    public NavLink advancedViewTasksNavLink;
    @Inject
    @DataField
    public NavLink monthViewTasksNavLink;
    @Inject
    @DataField
    public NavLink weekViewTasksNavLink;
    @Inject
    @DataField
    public NavLink createQuickTaskNavLink;
    @Inject
    @DataField
    public NavLink showAllTasksNavLink;
    @Inject
    @DataField
    public NavLink showPersonalTasksNavLink;
    @Inject
    @DataField
    public NavLink showGroupTasksNavLink;
    @Inject
    @DataField
    public NavLink showActiveTasksNavLink;
    @Inject
    @DataField
    public Label taskCalendarViewLabel;
    
    @Inject
    @DataField 
    public Label filtersLabel;
    
    @Inject
    @DataField
    public FlowPanel tasksViewContainer;
    @Inject
    private TaskListMultiDayBox taskListMultiDayBox;
    @Inject
    private Event<NotificationEvent> notification;
    
    private String currentView = "day";
    
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(final TasksListPresenter presenter) {
        this.presenter = presenter;
        taskListMultiDayBox.init();
        taskListMultiDayBox.setPresenter(presenter);

        refreshDayTasks();
        // By Default we will start in Day View
        tasksViewContainer.setStyleName("day");
        tasksViewContainer.add(taskListMultiDayBox);
        dayViewTasksNavLink.setText(constants.Day());
        dayViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.setStyleName("day");
                dayViewTasksNavLink.setStyleName("active");
                weekViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("");
                advancedViewTasksNavLink.setStyleName("");
                currentView = "day";
                refreshDayTasks();
            }
        });
        weekViewTasksNavLink.setText(constants.Week());
        weekViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.setStyleName("week");
                dayViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("");
                advancedViewTasksNavLink.setStyleName("");
                weekViewTasksNavLink.setStyleName("active");
                currentView = "week";
                refreshWeekTasks();
            }
        });

        monthViewTasksNavLink.setText(constants.Month());
        monthViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tasksViewContainer.setStyleName("month");
                dayViewTasksNavLink.setStyleName("");
                advancedViewTasksNavLink.setStyleName("");
                weekViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("active");
                currentView = "month";
                refreshMonthTasks();
            }
        });

        advancedViewTasksNavLink.setText(constants.Advanced());
        advancedViewTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dayViewTasksNavLink.setStyleName("");
                weekViewTasksNavLink.setStyleName("");
                monthViewTasksNavLink.setStyleName("");
                advancedViewTasksNavLink.setStyleName("active");
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Grid Tasks List");
                placeManager.goTo(placeRequestImpl);
            }
        });

        createQuickTaskNavLink.setText(constants.New_Task());
        createQuickTaskNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
                placeManager.goTo(placeRequestImpl);
            }
        });

        

        // Filters
        showPersonalTasksNavLink.setText(constants.Personal());
        showPersonalTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showPersonalTasksNavLink.setStyleName("active");
                showGroupTasksNavLink.setStyleName("");
                showActiveTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("");
                presenter.refreshPersonalTasks();
            }
        });

        showGroupTasksNavLink.setText(constants.Group());
        showGroupTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showGroupTasksNavLink.setStyleName("active");
                showPersonalTasksNavLink.setStyleName("");
                showActiveTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("");
                presenter.refreshGroupTasks();
            }
        });


        showActiveTasksNavLink.setText(constants.Active());
        showActiveTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showGroupTasksNavLink.setStyleName("");
                showPersonalTasksNavLink.setStyleName("");
                showActiveTasksNavLink.setStyleName("active");
                showAllTasksNavLink.setStyleName("");
                presenter.refreshActiveTasks();
            }
        });

        showAllTasksNavLink.setText(constants.All());
        showAllTasksNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showGroupTasksNavLink.setStyleName("");
                showPersonalTasksNavLink.setStyleName("");
                showActiveTasksNavLink.setStyleName("");
                showAllTasksNavLink.setStyleName("active");
                presenter.refreshAllTasks();
            }
        });

        taskCalendarViewLabel.setText(constants.Tasks_List_Calendar_View());
        taskCalendarViewLabel.setStyleName("");
        
        filtersLabel.setText(constants.Filters());
    }

    public void recieveStatusChanged(@Observes UserTaskEvent event) {
        refreshTasks();

    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public void refreshDayTasks() {
        monthViewTasksNavLink.setStyleName("");
        weekViewTasksNavLink.setStyleName("");
        dayViewTasksNavLink.setStyleName("active");
        
        showGroupTasksNavLink.setStyleName("");
        showPersonalTasksNavLink.setStyleName("");
        showActiveTasksNavLink.setStyleName("active");
        showAllTasksNavLink.setStyleName("");
        presenter.refresh3DaysActiveTasks();
    }
    
    public void refreshWeekTasks(){
        
        monthViewTasksNavLink.setStyleName("");
        weekViewTasksNavLink.setStyleName("active");
        dayViewTasksNavLink.setStyleName("");
        
        showGroupTasksNavLink.setStyleName("");
        showPersonalTasksNavLink.setStyleName("");
        showActiveTasksNavLink.setStyleName("active");
        showAllTasksNavLink.setStyleName("");
        presenter.refreshWeekActiveTasks();
    }
    
    public void refreshMonthTasks(){
        monthViewTasksNavLink.setStyleName("active");
        weekViewTasksNavLink.setStyleName("");
        dayViewTasksNavLink.setStyleName("");
        
        showGroupTasksNavLink.setStyleName("");
        showPersonalTasksNavLink.setStyleName("");
        showActiveTasksNavLink.setStyleName("active");
        showAllTasksNavLink.setStyleName("");
        presenter.refreshMonthActiveTasks();
    }
    
    public void refreshTasks() {
       if(currentView.equals("day")){
           refreshDayTasks();
       }else if(currentView.equals("week")){
           refreshWeekTasks();
       }else if(currentView.equals("month")){
           refreshMonthTasks();
       }
       
        
    }

    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }
}
