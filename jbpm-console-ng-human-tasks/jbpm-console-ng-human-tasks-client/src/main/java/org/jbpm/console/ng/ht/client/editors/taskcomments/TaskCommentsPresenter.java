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

import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskCommentsService;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class TaskCommentsPresenter {

    public interface TaskCommentsView extends IsWidget {

        void init( TaskCommentsPresenter presenter );

        TextArea getNewTaskCommentTextArea();

        Button addCommentButton();

        DataGrid<CommentSummary> getDataGrid();

        SimplePager getPager();

        void displayNotification( String text );
    }

    @Inject
    private TaskCommentsView view;

    @Inject
    private User identity;

    private long currentTaskId = 0;

    
    @Inject
    Caller<TaskCommentsService> taskCommentsServices;

    private ListDataProvider<CommentSummary> dataProvider = new ListDataProvider<CommentSummary>();

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }

    public ListDataProvider<CommentSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshComments() {
        taskCommentsServices.call( new RemoteCallback<List<CommentSummary>>() {

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
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).getAllCommentsByTaskId( currentTaskId );

    }

    public void addTaskComment( final String text,
                                final Date addedOn ) {
        taskCommentsServices.call( new RemoteCallback<Long>() {

            @Override
            public void callback( Long response ) {
                refreshComments();
                view.getNewTaskCommentTextArea().setText( "" );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).addComment( currentTaskId, text, identity.getIdentifier(), addedOn );
    }

    public void removeTaskComment( long commentId ) {
        taskCommentsServices.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long response ) {
                refreshComments();
                view.getNewTaskCommentTextArea().setText( "" );
                view.displayNotification( "Comment Deleted!" );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).deleteComment( currentTaskId, commentId );
    }

    public void addDataDisplay( final HasData<CommentSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public void onTaskSelectionEvent( @Observes final TaskSelectionEvent event ) {
        this.currentTaskId = event.getTaskId();
        refreshComments();
        view.getDataGrid().redraw();
    }

    public void onTaskRefreshedEvent( @Observes final TaskRefreshedEvent event ) {
        if ( currentTaskId == event.getTaskId() ) {
            refreshComments();
            view.getDataGrid().redraw();
        }
    }
}
