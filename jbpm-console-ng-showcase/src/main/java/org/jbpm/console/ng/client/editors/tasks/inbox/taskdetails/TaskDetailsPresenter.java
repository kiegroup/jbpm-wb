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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.event.Observes;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.events.TaskSelectionEvent;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PassThroughPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Task Details")
public class TaskDetailsPresenter {
    
    public interface InboxView
            extends
            UberView<TaskDetailsPresenter> {
        
        void displayNotification(String text);
        
        TextBox getTaskIdText();
        
        ListBox getGroupListBox();
        
        TextBox getTaskNameText();
        
        TextArea getTaskDescriptionTextArea();
        
        ListBox getTaskPriorityListBox();
        
        DateBox getDueDate();
        
        TextBox getUserText();
        
        ListBox getSubTaskStrategyListBox();
        
        public String[] getSubTaskStrategies();
        
        public String[] getPriorities();
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    Identity identity;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Task Details";
    }
    
    @WorkbenchPartView
    public UberView<TaskDetailsPresenter> getView() {
        return view;
    }
    
    public void forwardTask(final long taskId, final String userId, final String entityId) {
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary details) {
                if (entityId != null && !entityId.equals("")) {
                    taskServices.call(new RemoteCallback<Void>() {
                        @Override
                        public void callback(Void nothing) {
                            view.displayNotification("Task Forwarded from = " + userId + " -(to)-> " + entityId + ")");
                            view.getUserText().setText("");
                        }
                    }).forward(taskId, userId, entityId);
                }
            }
        }).getTaskDetails(taskId);
    }
    
    public void updateTask(final long taskId, final String taskDescription, final String userId, final String entityId, final String subTaskStrategy,
            final Date dueDate, final int priority) {
        
        if (taskId > 0) {
            
            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Priority Updated for Task id = " + taskId + ")");
                    
                }
            }).setPriority(taskId, Integer.valueOf(priority));
            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Sub Task Strategy Updated for Task id = " + taskId + ")");
                    
                }
            }).setSubTaskStrategy(taskId, subTaskStrategy);
            List<String> descriptions = new ArrayList<String>();
            descriptions.
                    add(taskDescription);
            
            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Description Updated for Task id = " + taskId + ")");
                    
                }
            }).setDescriptions(taskId, descriptions);
            
            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Expiration Date Updated for Task id = " + taskId + " -> " + dueDate.toString() + ")");
                }
            }).setExpirationDate(taskId, dueDate);
            
            
            
        }
        
    }
    
    public void refreshTask(long taskId) {
        List<Role> roles = identity.getRoles();
        final List<String> stringRoles = new ArrayList<String>(roles.size());
        for (Role r : roles) {
            stringRoles.add(r.getName());
        }
        
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary details) {
                view.getTaskIdText().setText(String.valueOf(details.getId()));
                view.getTaskNameText().setText(details.getName());
                view.getTaskDescriptionTextArea().setText(details.getDescription());
                view.getDueDate().setValue(details.getExpirationTime());
                view.getUserText().setText(details.getActualOwner());
                
                
                int i = 0;
                for (String strategy : view.getSubTaskStrategies()) {
                    if (details.getSubTaskStrategy().equals(strategy)) {
                        view.getSubTaskStrategyListBox().setSelectedIndex(i);
                    }
                    i++;
                }
                i = 0;
                for (String priority : view.getPriorities()) {
                    if (details.getPriority() == i) {
                        view.getTaskPriorityListBox().setSelectedIndex(i);
                    }
                    i++;
                }
                List<String> stringPotentialOwners = details.getPotentialOwners();
                List<String> forwardBox = new ArrayList<String>();
                
                for (String owner : stringPotentialOwners) {
                    if (!stringRoles.contains(owner) && !forwardBox.contains(owner)) {
                        forwardBox.add(owner);
                    }
                    
                }
                for (Role role : identity.getRoles()) {
                    if (!forwardBox.contains(role.getName())) {
                        forwardBox.add(role.getName());
                    }
                }
                
                for (String item : forwardBox) {
                    view.getGroupListBox().addItem(item, item);
                }
                
                
            }
        }).getTaskDetails(taskId);
        
    }
    
    public void onTaskSelected(@Observes TaskSelectionEvent taskSelection) {
        refreshTask(taskSelection.getTaskId());
    }
    
    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        long taskId = Long.parseLong(((PassThroughPlaceRequest) p).getPassThroughParameter("taskId", "0").toString());
        view.getTaskIdText().setText(String.valueOf(taskId));
        refreshTask(Long.parseLong(view.getTaskIdText().getText()));
    }
}
