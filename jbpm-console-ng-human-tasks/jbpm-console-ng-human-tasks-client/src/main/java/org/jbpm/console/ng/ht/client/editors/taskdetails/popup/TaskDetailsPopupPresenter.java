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

package org.jbpm.console.ng.ht.client.editors.taskdetails.popup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Task Details Popup")
public class TaskDetailsPopupPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface TaskDetailsPopupView extends UberView<TaskDetailsPopupPresenter> {

        void displayNotification( String text );

        Label getTaskIdText();

        Label getTaskNameText();

        TextArea getTaskDescriptionTextArea();

        ListBox getTaskPriorityListBox();

        DateTimeBox getDueDate();

        TextBox getUserText();

        TextBox getProcessInstanceIdText();

        TextBox getProcessIdText();

        // Commented out until we add the posibility of adding sub tasks
        // ListBox getSubTaskStrategyListBox();
        // Commented out until we add the posibility of adding sub tasks
        // public String[] getSubTaskStrategies();

        public String[] getPriorities();

        TextBox getTaskStatusText();

        Button getpIDetailsButton();

        UnorderedList getNavBarUL();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskDetailsPopupView view;

    @Inject
    Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    
    @Inject
    private Event<TaskRefreshedEvent> userTaskChanges;

    private PlaceRequest place;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @WorkbenchPartView
    public UberView<TaskDetailsPopupPresenter> getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {

        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary processInstance ) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instance Details" );
                placeRequestImpl.addParameter( "processInstanceId", view.getProcessInstanceIdText().getText() );
                placeRequestImpl.addParameter( "processDefId", processInstance.getProcessId() );
                placeManager.goTo( placeRequestImpl );
            }
        } ).getProcessInstanceById( Long.parseLong( view.getProcessInstanceIdText().getText() ) );

    }

    public void updateTask( final long taskId,
                            final String taskName,
                            final String taskDescription,
                            final String userId,
                            // final String subTaskStrategy,
                            final Date dueDate,
                            final int priority ) {

        if ( taskId > 0 ) {
            List<String> descriptions = new ArrayList<String>();
            descriptions.add( taskDescription );

            List<String> names = new ArrayList<String>();
            names.add( taskName );

            taskServices.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void nothing ) {
                    view.displayNotification( "Task Details Updated for Task id = " + taskId + ")" );
                    userTaskChanges.fire( new TaskRefreshedEvent( taskId ) );
                }
            } ).updateSimpleTaskDetails( taskId, names, Integer.valueOf( priority ), descriptions,
                                         // subTaskStrategy,
                                         dueDate );

        }

    }

    public void refreshTask( long taskId ) {

        taskServices.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( TaskSummary details ) {
                if ( details.getStatus().equals( "Completed" ) ) {

                    view.getTaskDescriptionTextArea().setEnabled( false );
                    view.getDueDate().setEnabled( false );
                    view.getUserText().setEnabled( false );
                    view.getTaskStatusText().setEnabled( false );
                    view.getProcessInstanceIdText().setEnabled( false );
                }

                view.getTaskIdText().setText( String.valueOf( details.getId() ) );
                view.getTaskNameText().setText( details.getName() );
                view.getTaskDescriptionTextArea().setText( details.getDescription() );
                view.getDueDate().setValue( details.getExpirationTime() );
                view.getUserText().setText( details.getActualOwner() );
                view.getUserText().setEnabled( false );
                view.getTaskStatusText().setText( details.getStatus() );
                view.getTaskStatusText().setEnabled( false );
                view.getProcessIdText().setEnabled(false);
                if ( details.getProcessInstanceId() == -1 ) {
                    view.getProcessInstanceIdText().setText( "None" );
                    view.getProcessIdText().setText( "None" );
                    view.getpIDetailsButton().setEnabled( false );
                } else {
                    view.getProcessInstanceIdText().setText( String.valueOf( details.getProcessInstanceId() ) );
                    view.getProcessIdText().setText( details.getProcessId() );
                }

                view.getProcessInstanceIdText().setEnabled( false );

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
                        view.getTaskPriorityListBox().setSelectedIndex( i );
                    }
                    i++;
                }

            }
        } ).getTaskDetails( taskId );

    }

    public void onTaskSelected( @Observes TaskSelectionEvent taskSelection ) {
        refreshTask( taskSelection.getTaskId() );
    }

    @OnOpen
    public void onOpen() {
        final long taskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
        view.getTaskIdText().setText( String.valueOf( taskId ) );
        view.getNavBarUL().clear();
        NavLink detailsLink = new NavLink( constants.Details() );
        detailsLink.setStyleName( "active" );

        NavLink workLink = new NavLink( constants.Work() );

        workLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display Popup");
                placeRequestImpl.addParameter("taskId", String.valueOf(taskId));
                placeManager.goTo(placeRequestImpl);
            }
        } );
        NavLink commentsLink = new NavLink( constants.Comments() );
        commentsLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Task Comments Popup" );
                placeRequestImpl.addParameter( "taskId", String.valueOf( taskId ) );
                placeManager.goTo( placeRequestImpl );
            }
        } );
        
        NavLink assignmentsLink = new NavLink( constants.Assignments());
        assignmentsLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Task Assignments Popup" );
                placeRequestImpl.addParameter( "taskId", String.valueOf( taskId ) );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        view.getNavBarUL().add( workLink );
        view.getNavBarUL().add( detailsLink );
        view.getNavBarUL().add( assignmentsLink );
        view.getNavBarUL().add( commentsLink );
        refreshTask( Long.parseLong( view.getTaskIdText().getText() ) );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
