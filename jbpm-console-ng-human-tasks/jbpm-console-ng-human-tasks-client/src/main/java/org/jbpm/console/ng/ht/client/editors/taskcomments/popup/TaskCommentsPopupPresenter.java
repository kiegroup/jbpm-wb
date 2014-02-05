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

package org.jbpm.console.ng.ht.client.editors.taskcomments.popup;

import com.github.gwtbootstrap.client.ui.Button;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
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
@WorkbenchPopup(identifier = "Task Comments Popup")
public class TaskCommentsPopupPresenter {

    public interface TaskCommentsPopupView extends UberView<TaskCommentsPopupPresenter> {

        Label getTaskIdText();

        Label getTaskNameText();

        UnorderedList getNavBarUL();

        TextArea getNewTaskCommentTextArea();

        Button addCommentButton();

        DataGrid<CommentSummary> getDataGrid();

        SimplePager getPager();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskCommentsPopupView view;

    @Inject
    Identity identity;

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
    public UberView<TaskCommentsPopupPresenter> getView() {
        return view;
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        final long taskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
        view.getTaskIdText().setText( String.valueOf( taskId ) );
        view.getNavBarUL().clear();
        NavLink commentsLink = new NavLink( constants.Comments() );
        commentsLink.setStyleName( "active" );

        NavLink workLink = new NavLink( constants.Work() );

        workLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Display Popup" );
                placeRequestImpl.addParameter( "taskId", String.valueOf( taskId ) );
                placeManager.goTo( placeRequestImpl );
            }
        } );
        NavLink detailsLink = new NavLink( constants.Details() );
        detailsLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Task Details Popup" );
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
        refreshComments( taskId );
        view.getDataGrid().redraw();
    }

    public void refreshComments( long taskId ) {
        taskServices.call( new RemoteCallback<TaskSummary>() {

            @Override
            public void callback( TaskSummary details ) {
                view.getTaskIdText().setText( String.valueOf( details.getId() ) );
                view.getTaskNameText().setText( details.getName() );
            }
        } ).getTaskDetails( taskId );
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
        } ).getAllCommentsByTaskId( taskId );

    }

    public void addTaskComment( final long taskId,
                                String text,
                                Date addedOn ) {
        taskServices.call( new RemoteCallback<Long>() {

            @Override
            public void callback( Long response ) {
                refreshComments( taskId );
                view.getNewTaskCommentTextArea().setText("");
            }
        } ).addComment( taskId, text, identity.getName(), addedOn );
    }

    public void addDataDisplay( HasData<CommentSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
