package org.jbpm.console.ng.ht.client.editors.taskprocesscontext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.uberfire.client.mvp.PlaceManager;


import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;

@Dependent
public class TaskProcessContextPresenter {
    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskProcessContextView view;

    @Inject
    private Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected;

    @Inject
    private Caller<TaskQueryService> taskQueryService;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<TaskStyleEvent> taskStyleEvent;


    private long currentTaskId = 0;
    public interface TaskProcessContextView extends IsWidget {
        void init( final TaskProcessContextPresenter presenter );

        TextBox getProcessInstanceIdText();

        TextBox getProcessIdText();
        
        Button getpIDetailsButton();
        
        public void displayNotification( String text );
    }
    
    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }
    
    public void goToProcessInstanceDetails() {

        dataServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary processInstance ) {

                placeManager.goTo( "Process Instances" );
                processInstanceSelected.fire( new ProcessInstancesWithDetailsRequestEvent( processInstance.getDeploymentId(),
                                                                                           processInstance.getProcessInstanceId(), processInstance.getProcessId(), processInstance.getProcessName(), processInstance.getState() ) );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getProcessInstanceById( Long.parseLong( view.getProcessInstanceIdText().getText() ) );

    }
    
    public void refreshProcessContextOfTask() {
        taskQueryService.call( new RemoteCallback<TaskSummary>() {
            @Override
            public void callback( TaskSummary details ) {
                if ( details == null ) {
                    return;
                }
                if ( details.getStatus().equals( "Completed" ) ) {

                    view.getProcessInstanceIdText().setEnabled( false );
                }

                view.getProcessIdText().setEnabled( false );
                if ( details.getProcessInstanceId() == -1 ) {
                    view.getProcessInstanceIdText().setText( "None" );
                    view.getProcessIdText().setText( "None" );
                    view.getpIDetailsButton().setEnabled( false );
                } else {
                    view.getProcessInstanceIdText().setText( String.valueOf( details.getProcessInstanceId() ) );
                    view.getProcessIdText().setText( details.getProcessId() );
                    view.getpIDetailsButton().setEnabled( true );
                }

                view.getProcessInstanceIdText().setEnabled( false );


                changeStyleRow( details.getTaskId() );

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
    
    private void changeStyleRow( final long idTask ) {
        taskStyleEvent.fire( new TaskStyleEvent( idTask ) );
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        refreshProcessContextOfTask();
    }

    public void onTaskRefreshedEvent( @Observes final TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshProcessContextOfTask();
        }
    }
}
