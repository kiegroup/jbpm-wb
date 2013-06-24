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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import java.util.Map;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Task Assignments Popup")
public class TaskAssignmentsPopupPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface TaskAssignmentsPopupView extends UberView<TaskAssignmentsPopupPresenter> {

        void displayNotification( String text );

        Label getTaskIdText();

        Label getTaskNameText();
        
        FlowPanel getUsersGroupsControlsPanel();
        
        UnorderedList getNavBarUL();
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TaskAssignmentsPopupView view;

    @Inject
    private Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Assignments();
    }

    @WorkbenchPartView
    public UberView<TaskAssignmentsPopupPresenter> getView() {
        return view;
    }

   

    

    public void refreshTaskPotentialOwners( final long taskId ) {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(taskId);
        view.displayNotification("task ID before: "+taskId);
        taskServices.call( new RemoteCallback<Map<Long, List<String>>>() {
            @Override
            public void callback( Map<Long, List<String>> ids ) {
                view.displayNotification("task IDs after: "+ids);
                if(ids.isEmpty()){
                    view.getUsersGroupsControlsPanel().add(new HTMLPanel("no potential owners"));
                }else{
                    view.getUsersGroupsControlsPanel().add(new HTMLPanel(""+ids.get(taskId).toString()));
                }
            }
        } ).getPotentialOwnersForTaskIds(taskIds);

    }

   

    @OnReveal
    public void onReveal() {
        final long taskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
        taskServices.call( new RemoteCallback<TaskSummary>() {

            @Override
            public void callback( TaskSummary details ) {
                view.getTaskIdText().setText( String.valueOf( details.getId() ) );
                view.getTaskNameText().setText( details.getName() );
            }
        } ).getTaskDetails( taskId );
        
        view.getTaskIdText().setText( String.valueOf( taskId ) );
        view.getNavBarUL().clear();
        NavLink assignmentsLink = new NavLink( constants.Assignments());
        assignmentsLink.setStyleName( "active" );

        NavLink workLink = new NavLink( constants.Work() );
        workLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                close();
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Display" );
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

        view.getNavBarUL().add( workLink );
        view.getNavBarUL().add( detailsLink );
        view.getNavBarUL().add( assignmentsLink );
        view.getNavBarUL().add( commentsLink );
        refreshTaskPotentialOwners( Long.parseLong( view.getTaskIdText().getText() ) );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
