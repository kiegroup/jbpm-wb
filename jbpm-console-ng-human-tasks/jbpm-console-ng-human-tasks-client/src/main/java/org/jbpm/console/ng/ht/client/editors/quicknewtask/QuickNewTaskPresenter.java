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

package org.jbpm.console.ng.ht.client.editors.quicknewtask;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;

import java.util.List;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.util.UTCDateBox;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.NewTaskEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Quick New Task")
public class QuickNewTaskPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface QuickNewTaskView extends UberView<QuickNewTaskPresenter> {

        void displayNotification( String text );

        TextBox getTaskNameText();

        Button getAddTaskButton();
    }

    @Inject
    QuickNewTaskView view;

    @Inject
    Identity identity;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;
    
    @Inject
    private Event<NewTaskEvent> newTaskEvent;
    
    
    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.New_Task();
    }

    @WorkbenchPartView
    public UberView<QuickNewTaskPresenter> getView() {
        return view;
    }

    public QuickNewTaskPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void addTask( final List<String> users, List<String> groups,
                         final String taskName,
                         int priority,
                         boolean isAssignToMe,
                         long dueDate, long dueDateTime ) {
        Map<String, Object> templateVars = new HashMap<String, Object>();
        Date due = UTCDateBox.utc2date( dueDate + dueDateTime ); 
        templateVars.put( "due", due );
        templateVars.put( "now", new Date() );

        String str = "(with (new Task()) { priority = " + priority
                + ", taskData = (with( new TaskData()) { createdOn = now, expirationTime = due } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        str += " [";
        if ( users != null && !users.isEmpty() ) {
            
            for(String user : users){
                str += "new User('" + user + "'), ";
            } 
            
        }
        if ( groups != null && !groups.isEmpty() ) {
            
            for(String group : groups){
                str += "new Group('" + group + "'), ";
            } 
            
        }
        str+="], businessAdministrators = [ new Group('Administrators') ],}),";
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')]})";
        if ( isAssignToMe && users != null && users.isEmpty() && groups != null 
                && containsGroup(groups, identity.getRoles()) ) {
            //System.out.println(" FIRST OPTION -> Groups were I'm Included  and I want to be autoassigned add/start/claim!!");
            taskServices.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long taskId ) {
                    refreshNewTask(taskId, taskName, "Task Created and Started (id = " + taskId + ")");
                }
            }, new ErrorCallback<Message>() {
                   @Override
                   public boolean error( Message message, Throwable throwable ) {
                       ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                       return true;
                   }
               } ).addTaskAndClaimAndStart( str, null, identity.getName(), templateVars );
        } else if ( !isAssignToMe && users != null && users.isEmpty() && groups != null 
                && containsGroup(groups, identity.getRoles()) ) {
            //System.out.println(" Second OPTION -> Group task but I don't want to be assigned automatically  -> just add!!");
            taskServices.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long taskId ) {
                    refreshNewTask(taskId, taskName, "Task Created and Started (id = " + taskId + ")");

                }
            }, new ErrorCallback<Message>() {
                   @Override
                   public boolean error( Message message, Throwable throwable ) {
                       ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                       return true;
                   }
               } ).addTask( str, null, templateVars );
        }  if (users != null && !users.isEmpty() && users.contains(identity.getName())) {
            //System.out.println(" THIRD OPTION -> Users that includes me add / start!!");
            taskServices.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long taskId ) {
                    refreshNewTask(taskId, taskName, "Task Created (id = " + taskId + ")");

                }
            }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).addTaskAndStart(str, null, identity.getName(), templateVars );
        } else if (users != null && !users.isEmpty() && !users.contains(identity.getName())) {
            //System.out.println(" FOURTH OPTION -> users that are not me -> just adding!!");
            taskServices.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long taskId ) {
                    refreshNewTask(taskId, taskName, "Task Created (id = " + taskId + ")");

                }
            }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).addTask(str, null, templateVars );
        }else if(groups != null && !groups.isEmpty() && !containsGroup(groups, identity.getRoles())){
            //System.out.println(" FIFTH OPTION -> groups were I'm not in -> just adding!!");
            taskServices.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long taskId ) {
                    refreshNewTask(taskId, taskName, "Task Created (id = " + taskId + ")");
                }
            }, new ErrorCallback<Message>() {
                   @Override
                   public boolean error( Message message, Throwable throwable ) {
                       ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                       return true;
                   }
               } ).addTask(str, null, templateVars );
        } 

    }
    
    private void refreshNewTask(Long taskId, String taskName, String msj){
        view.displayNotification( msj );
        newTaskEvent.fire( new NewTaskEvent( taskId, taskName ));
        close();
    }

    private boolean containsGroup(List<String> groups, List<Role> roles){
        for(String g : groups){
            for(Role r : roles){
                //System.out.println(" ->  Role: '"+r.getName()+"' == '"+g+"'");
                if(r.getName().trim().equals(g.trim())){
                  //  System.out.println(" YEAH!!!!  Role: '"+r.getName()+"' == '"+g+"'");
                    return true;
                }
            }
        }
        return false;
    }
    
    @OnOpen
    public void onOpen() {
        view.getTaskNameText().setFocus( true );

    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }
}
