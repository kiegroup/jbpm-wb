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
package org.jbpm.console.ng.ht.client.editors.taskassignments;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskAssignmentSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class TaskAssignmentsPresenter {

    public interface TaskAssignmentsView extends IsWidget {

        void init( final TaskAssignmentsPresenter presenter );

        void displayNotification( String text );

        Label getUsersGroupsControlsPanel();

        Button getDelegateButton();

        TextBox getUserOrGroupText();

        HelpBlock getUserOrGroupHelpBlock();
    }

    @Inject
    private TaskAssignmentsView view;

    @Inject
    private User identity;

    @Inject
    Caller<TaskLifeCycleService> taskServices;
    
    @Inject
    Caller<TaskOperationsService> taskOperationsServices;

    private long currentTaskId = 0;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }

    public void delegateTask( String entity ) {
        taskServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "Task was successfully delegated" );
                view.getDelegateButton().setActive( false );
                view.getUserOrGroupHelpBlock().setText( Constants.INSTANCE.DelegationSuccessfully() );
                taskRefreshed.fire( new TaskRefreshedEvent( currentTaskId ) );
                refreshTaskPotentialOwners();
            }

        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                view.getDelegateButton().setActive( true );
                view.getUserOrGroupHelpBlock().setText( Constants.INSTANCE.DelegationUnable() );
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;

            }
        } ).delegate( currentTaskId, identity.getIdentifier(), entity );
    }

    public void refreshTaskPotentialOwners() {
        if(currentTaskId != 0){
            taskOperationsServices.call( new RemoteCallback<TaskSummary>() {

                @Override
                public void callback(TaskSummary response) {
                     if ( response == null ) {
                        view.getDelegateButton().setEnabled( false );
                        view.getUserOrGroupText().setEnabled( false );
                        return;
                    }
                    if(response.getStatus().equals("Completed") || response.getActualOwner().equals( "" ) 
                            || !response.getActualOwner().equals( identity.getIdentifier() )  ){
                        view.getDelegateButton().setEnabled( false );
                        view.getUserOrGroupText().setEnabled( false );
                    }else{
                        view.getDelegateButton().setEnabled( true );
                        view.getUserOrGroupText().setEnabled( true );
                    }

                }

            }).getTaskDetails(currentTaskId);

            taskOperationsServices.call( new RemoteCallback<TaskAssignmentSummary>() {
                @Override
                public void callback( TaskAssignmentSummary ts ) {
                    if ( ts == null ) {
                        return;
                    }
                    
                    if( ts.getPotOwnersString() != null && ts.getPotOwnersString().size() == 0 ){
                        view.getUsersGroupsControlsPanel().setText( Constants.INSTANCE.No_Potential_Owners() );
                    } else {
                           view.getUsersGroupsControlsPanel().setText("" + ts.getPotOwnersString().toString() );
                    }
                   
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message,
                                      Throwable throwable ) {
                    ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                    return true;
                }
            } ).getTaskAssignmentDetails( currentTaskId );
        }

    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        view.getUserOrGroupHelpBlock().setText( "" );
        view.getUserOrGroupText().setText( "" );
        refreshTaskPotentialOwners();
    }

    public void onTaskRefreshedEvent( @Observes TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshTaskPotentialOwners();
        }
    }

}
