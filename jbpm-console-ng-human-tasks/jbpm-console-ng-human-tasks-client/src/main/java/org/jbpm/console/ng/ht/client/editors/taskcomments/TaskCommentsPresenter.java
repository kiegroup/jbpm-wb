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

package org.jbpm.console.ng.ht.client.editors.taskcomments;

import com.github.gwtbootstrap.client.ui.Button;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import javax.enterprise.event.Observes;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Task Comments")
public class TaskCommentsPresenter {

    public interface TaskCommentsView extends UberView<TaskCommentsPresenter> {


        TextArea getNewTaskCommentTextArea();

        Button addCommentButton();

        DataGrid<CommentSummary> getDataGrid();

        SimplePager getPager();
        
        void displayNotification( String text );
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskCommentsView view;

    @Inject
    Identity identity;
    
    private long currentTaskId = 0;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private ListDataProvider<CommentSummary> dataProvider = new ListDataProvider<CommentSummary>();

    public ListDataProvider<CommentSummary> getDataProvider() {
        return dataProvider;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Comments();
    }

    @WorkbenchPartView
    public UberView<TaskCommentsPresenter> getView() {
        return view;
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        this.currentTaskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
        refreshComments( );
        view.getDataGrid().redraw();
    }

    public void refreshComments( ) {
        taskServices.call( new RemoteCallback<List<CommentSummary>>() {

            @Override
            public void callback( List<CommentSummary> comments ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( comments );
                if ( comments.size() > 0 ) {
                    view.getDataGrid().setHeight( "350px" );
                    view.getPager().setVisible( true );
                }
                dataProvider.refresh();
                view.getDataGrid().redraw();
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).getAllCommentsByTaskId( currentTaskId );

    }

    public void addTaskComment( String text,
                                Date addedOn ) {
        taskServices.call( new RemoteCallback<Long>() {

            @Override
            public void callback( Long response ) {
                refreshComments( );
                view.getNewTaskCommentTextArea().setText("");
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).addComment( currentTaskId, text, identity.getName(), addedOn );
    }
    
    public void removeTaskComment( long commentId ) {
        taskServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long response ) {
                refreshComments( );
                view.getNewTaskCommentTextArea().setText("");
                view.displayNotification("Comment Deleted!");
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).deleteComment( currentTaskId, commentId );
    }

    public void addDataDisplay( HasData<CommentSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event){
        if(currentTaskId == event.getTaskId()){
            refreshComments( );
            view.getDataGrid().redraw();
        }
    }
}
