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
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.Map;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Task Assignments")
public class TaskAssignmentsPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface TaskAssignmentsView extends UberView<TaskAssignmentsPresenter> {

        void displayNotification( String text );

        FlowPanel getUsersGroupsControlsPanel();
        
        
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TaskAssignmentsView view;

    @Inject
    private Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;
    
    private long currentTaskId = 0;
    
    

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Assignments();
    }

    @WorkbenchPartView
    public UberView<TaskAssignmentsPresenter> getView() {
        return view;
    }

   

    

    public void refreshTaskPotentialOwners( ) {
        List<Long> taskIds = new ArrayList<Long>(1);
        taskIds.add(currentTaskId);
        taskServices.call( new RemoteCallback<Map<Long, List<String>>>() {
            @Override
            public void callback( Map<Long, List<String>> ids ) {
                if(ids.isEmpty()){
                    view.getUsersGroupsControlsPanel().add(new HTMLPanel("no potential owners"));
                }else{
                    view.getUsersGroupsControlsPanel().add(new HTMLPanel(""+ids.get(currentTaskId).toString()));
                }
            }
        } ).getPotentialOwnersForTaskIds(taskIds);

    }

   

    @OnOpen
    public void onOpen() {
        
        this.currentTaskId = Long.parseLong( place.getParameter( "taskId", "0" ).toString() );
  
        refreshTaskPotentialOwners(  );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
