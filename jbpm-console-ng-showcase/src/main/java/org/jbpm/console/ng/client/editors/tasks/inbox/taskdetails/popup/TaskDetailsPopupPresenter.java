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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails.popup;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.events.TaskSelectionEvent;
import org.jbpm.console.ng.shared.model.TaskSummary;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Task Details Popup")
public class TaskDetailsPopupPresenter {

    public interface InboxView
            extends
            UberView<TaskDetailsPopupPresenter> {

        void displayNotification(String text);

        TextBox getTaskIdText();


        TextBox getTaskNameText();

        TextArea getTaskDescriptionTextArea();

        ListBox getTaskPriorityListBox();

        DateBox getDueDate();

        TextBox getUserText();

        ListBox getSubTaskStrategyListBox();

        public String[] getSubTaskStrategies();

        public String[] getPriorities();
        
        TextBox getTaskStatusText();
        
        Button getUpdateButton();
        
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
    @Inject
    Identity identity;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest place;

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Task Details Popup";
    }

    @WorkbenchPartView
    public UberView<TaskDetailsPopupPresenter> getView() {
        return view;
    }

   

    public void updateTask(final long taskId, final String taskName, final String taskDescription, final String userId, final String subTaskStrategy,
            final Date dueDate, final int priority) {

        if (taskId > 0) {
            List<String> descriptions = new ArrayList<String>();
            descriptions.add(taskDescription);
            
            List<String> names = new ArrayList<String>();
            names.add(taskName);
            
            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Details Updated for Task id = " + taskId + ")");

                }
            }).updateSimpleTaskDetails(taskId, names, Integer.valueOf(priority), descriptions, subTaskStrategy, dueDate);

        }

    }

    public void refreshTask(long taskId) {
        
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary details) {
                if(details.getStatus().equals("Completed")){
                    
                    view.getTaskIdText().setEnabled(false);
                    view.getTaskNameText().setEnabled(false);
                    view.getTaskDescriptionTextArea().setEnabled(false);
                    view.getDueDate().setEnabled(false);
                    view.getUserText().setEnabled(false);
                    view.getTaskStatusText().setEnabled(false);
                    view.getUpdateButton().setEnabled(false);
                }
                
                view.getTaskIdText().setText(String.valueOf(details.getId()));
                view.getTaskIdText().setEnabled(false);
                view.getTaskNameText().setText(details.getName());
                view.getTaskDescriptionTextArea().setText(details.getDescription());
                view.getDueDate().setValue(details.getExpirationTime());
                view.getUserText().setText(details.getActualOwner());
                view.getUserText().setEnabled(false);
                view.getTaskStatusText().setText(details.getStatus());
                view.getTaskStatusText().setEnabled(false);

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
               


            }
        }).getTaskDetails(taskId);

    }

    public void onTaskSelected(@Observes TaskSelectionEvent taskSelection) {
        refreshTask(taskSelection.getTaskId());
    }

    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        long taskId = Long.parseLong(p.getParameter("taskId", "0").toString());
        view.getTaskIdText().setText(String.valueOf(taskId));
        refreshTask(Long.parseLong(view.getTaskIdText().getText()));
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
