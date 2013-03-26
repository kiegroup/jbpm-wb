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

import com.github.gwtbootstrap.client.ui.NavLink;
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
    public FlowPanel tasksViewContainer;
    
    @Inject
    private  TaskListMultiDayBox taskListMultiDayBox;
    @Inject
    private Event<NotificationEvent> notification;
    

    @Override
    public void init(TasksListPresenter presenter) {
        this.presenter = presenter;

        taskListMultiDayBox.setPresenter(presenter);

        

        refreshTasks();
        // By Default we will start in Day View
        tasksViewContainer.setStyleName("day");
        tasksViewContainer.add(taskListMultiDayBox);
        dayViewTasksNavLink.setText("Day");
        dayViewTasksNavLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
              tasksViewContainer.setStyleName("day");
              weekViewTasksNavLink.setStyleName("");
              monthViewTasksNavLink.setStyleName("");
              advancedViewTasksNavLink.setStyleName("");
              refreshTasks();
            }
        });
        weekViewTasksNavLink.setText("Week");
        weekViewTasksNavLink.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            tasksViewContainer.setStyleName("week");
            dayViewTasksNavLink.setStyleName("");
            monthViewTasksNavLink.setStyleName("");
            advancedViewTasksNavLink.setStyleName("");
            weekViewTasksNavLink.setStyleName("active");
            refreshTasks();
          }
        });
        
        monthViewTasksNavLink.setText("Month");
        monthViewTasksNavLink.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            tasksViewContainer.setStyleName("month");
            dayViewTasksNavLink.setStyleName("");
            advancedViewTasksNavLink.setStyleName("");
            weekViewTasksNavLink.setStyleName("");
            monthViewTasksNavLink.setStyleName("active");
            refreshTasks();
          }
        });
        
        advancedViewTasksNavLink.setText("Advanced");
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
        
        createQuickTaskNavLink.setText("New Task");
        createQuickTaskNavLink.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
              PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
              placeManager.goTo(placeRequestImpl);
          }
        });
        
        advancedViewTasksNavLink.setText("Advanced");
        advancedViewTasksNavLink.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
              PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Grid Tasks List");
              placeManager.goTo(placeRequestImpl);
          }
        });
        

    }

    public void recieveStatusChanged(@Observes UserTaskEvent event) {
        refreshTasks();

    }

//
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public void refreshTasks() {
//        Boolean isCheckedCompleted = showCompletedCheck.getValue();
//        Boolean isCheckedGroupTasks = showGroupTasksCheck.getValue();
//        Boolean isCheckedPersonalTasks = showPersonalTasksCheck.getValue();
        presenter.refreshTasks(identity.getName(), false, false, false);
    }

    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }

}
