/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class TaskDetailsPresenter {

    public interface TaskDetailsView extends IsWidget {

        void init( final TaskDetailsPresenter presenter );

        void displayNotification( final String text );

        TextArea getTaskDescriptionTextArea();

        Select getTaskPriorityListBox();

        UTCDateBox getDueDate();

        UTCTimeBox getDueDateTime();

        TextBox getUserText();

        // Commented out until we add the posibility of adding sub tasks
        // ListBox getSubTaskStrategyListBox();
        // Commented out until we add the posibility of adding sub tasks
        // public String[] getSubTaskStrategies();
        public String[] getPriorities();

        TextBox getTaskStatusText();
        
        Button getUpdateTaskButton();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskDetailsView view;

    @Inject
    private Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected;

    @Inject
    private Caller<TaskQueryService> taskQueryService;

    @Inject
    private Caller<TaskOperationsService> taskOperationsService;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    private Event<TaskCalendarEvent> taskCalendarEvent;

    private long currentTaskId = 0;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }


    public void updateTask( final String taskDescription,
                            final String userId,
                            final Date dueDate,
                            final int priority ) {

        if ( currentTaskId > 0 ) {
            List<String> descriptions = new ArrayList<String>();
            descriptions.add( taskDescription );

            taskOperationsService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void nothing ) {
                    view.displayNotification( "Task Details Updated for Task id = " + currentTaskId + ")" );
                    taskRefreshed.fire( new TaskRefreshedEvent( currentTaskId ) );
                    taskCalendarEvent.fire( new TaskCalendarEvent( currentTaskId ) );
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message,
                                      Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).updateTask( currentTaskId, priority, descriptions, dueDate );

        }

    }

    public void refreshTask() {

        taskQueryService.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( TaskSummary details ) {
                if ( details == null ) {
                    view.getTaskDescriptionTextArea().setEnabled( false );
                    view.getDueDate().setEnabled( false );
                    view.getUserText().setEnabled( false );
                    view.getTaskStatusText().setEnabled( false );
                    view.getDueDateTime().setEnabled(false);
                    view.getTaskPriorityListBox().setEnabled(false);
                    view.getUpdateTaskButton().setEnabled(false);
                    return;
                }
                if ( details.getStatus().equals( "Completed" ) ) {

                    view.getTaskDescriptionTextArea().setEnabled( false );
                    view.getDueDate().setEnabled( false );
                    view.getUserText().setEnabled( false );
                    view.getTaskStatusText().setEnabled( false );
                    view.getDueDateTime().setEnabled(false);
                    view.getTaskPriorityListBox().setEnabled(false);
                    view.getUpdateTaskButton().setEnabled(false);

                }

                view.getTaskDescriptionTextArea().setText( details.getDescription() );
                view.getDueDate().setValue( UTCDateBox.date2utc( details.getExpirationTime() ) );
                view.getDueDateTime().setValue( UTCDateBox.date2utc( details.getExpirationTime() ) );
                view.getUserText().setText( details.getActualOwner() );
                view.getUserText().setEnabled( false );
                view.getTaskStatusText().setText( details.getStatus() );
                view.getTaskStatusText().setEnabled( false );

                int i = 0;
                // Commented out until we add the posibility of adding sub tasks
                // for (String strategy : view.getSubTaskStrategies()) {
                // if (details.getSubTaskStrategy().equals(strategy)) {
                // view.getSubTaskStrategyListBox().setSelectedIndex(i);
                // }
                // i++;
                // }
                i = 0;
                for ( String priority : view.getPriorities() ) {
                    if ( details.getPriority() == i ) {
                        view.getTaskPriorityListBox().setValue( String.valueOf( i ) );
                    }
                    i++;
                }
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getItem( new TaskKey( currentTaskId ) );
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        refreshTask();
    }

    public void onTaskRefreshedEvent( @Observes final TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshTask();
        }
    }
}
