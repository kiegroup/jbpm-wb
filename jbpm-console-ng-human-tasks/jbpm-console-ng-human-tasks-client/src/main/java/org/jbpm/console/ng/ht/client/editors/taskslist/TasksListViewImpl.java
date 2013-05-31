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

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskType;
import org.jbpm.console.ng.ht.client.editors.taskslist.TasksListPresenter.TaskView;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.client.util.CalendarPicker;
import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite implements TasksListPresenter.TaskListView {

    private Constants constants = GWT.create( Constants.class );

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
    private CalendarPicker calendarPicker;

    @Inject
    @DataField
    public FlowPanel tasksViewContainer;

    @Inject
    private TaskListMultiDayBox taskListMultiDayBox;

    @Inject
    private Event<NotificationEvent> notification;

    private Date currentDate;

    private TaskView currentView = TaskView.DAY;

    private TaskType currentTaskType = TaskType.ACTIVE;

    @Override
    public void init( final TasksListPresenter presenter ) {
        this.presenter = presenter;
        taskListMultiDayBox.init();
        taskListMultiDayBox.setPresenter( presenter );
        calendarPicker.init();
        currentDate = new Date();
        calendarPicker.setViewType( "day" );
        calendarPicker.addValueChangeHandler( new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange( ValueChangeEvent<Date> event ) {
                currentDate = event.getValue();
                refreshTasks();
            }
        } );

        // By Default we will start in Day View
        tasksViewContainer.setStyleName( "day" );
        tasksViewContainer.add( taskListMultiDayBox );
        dayViewTasksNavLink.setText( constants.Day() );
        dayViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                tasksViewContainer.setStyleName( "day" );
                dayViewTasksNavLink.setStyleName( "active" );
                weekViewTasksNavLink.setStyleName( "" );
                monthViewTasksNavLink.setStyleName( "" );
                advancedViewTasksNavLink.setStyleName( "" );
                currentView = TaskView.DAY;
                calendarPicker.setViewType( "day" );
                refreshTasks();
            }
        } );
        weekViewTasksNavLink.setText( constants.Week() );
        weekViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                tasksViewContainer.setStyleName( "week" );
                dayViewTasksNavLink.setStyleName( "" );
                monthViewTasksNavLink.setStyleName( "" );
                advancedViewTasksNavLink.setStyleName( "" );
                weekViewTasksNavLink.setStyleName( "active" );
                currentView = TaskView.WEEK;
                calendarPicker.setViewType( "week" );
                refreshTasks();
            }
        } );

        monthViewTasksNavLink.setText( constants.Month() );
        monthViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                tasksViewContainer.setStyleName( "month" );
                dayViewTasksNavLink.setStyleName( "" );
                advancedViewTasksNavLink.setStyleName( "" );
                weekViewTasksNavLink.setStyleName( "" );
                monthViewTasksNavLink.setStyleName( "active" );
                currentView = TaskView.MONTH;
                calendarPicker.setViewType( "month" );
                refreshTasks();
            }
        } );

        advancedViewTasksNavLink.setText( constants.Advanced() );
        advancedViewTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                dayViewTasksNavLink.setStyleName( "" );
                weekViewTasksNavLink.setStyleName( "" );
                monthViewTasksNavLink.setStyleName( "" );
                advancedViewTasksNavLink.setStyleName( "active" );
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Grid Tasks List" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        createQuickTaskNavLink.setText( constants.New_Task() );
        createQuickTaskNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Quick New Task" );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        // Filters
        showPersonalTasksNavLink.setText( constants.Personal() );
        showPersonalTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showPersonalTasksNavLink.setStyleName( "active" );
                showGroupTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.PERSONAL;
                refreshTasks();
            }
        } );

        showGroupTasksNavLink.setText( constants.Group() );
        showGroupTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "active" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.GROUP;
                refreshTasks();
            }
        } );

        showActiveTasksNavLink.setText( constants.Active() );
        showActiveTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "active" );
                showAllTasksNavLink.setStyleName( "" );
                currentTaskType = TaskType.ACTIVE;
                refreshTasks();
            }
        } );

        showAllTasksNavLink.setText( constants.All() );
        showAllTasksNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                showGroupTasksNavLink.setStyleName( "" );
                showPersonalTasksNavLink.setStyleName( "" );
                showActiveTasksNavLink.setStyleName( "" );
                showAllTasksNavLink.setStyleName( "active" );
                currentTaskType = TaskType.ALL;
                refreshTasks();

            }
        } );

        taskCalendarViewLabel.setText( constants.Tasks_List_Calendar_View() );
        taskCalendarViewLabel.setStyleName( "" );
        refreshTasks();
    }

    public void recieveStatusChanged( @Observes UserTaskEvent event ) {
        refreshTasks();

    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void refreshTasks() {
        presenter.refreshTasks( currentDate, currentView, currentTaskType );
    }

    @Override
    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }

}
