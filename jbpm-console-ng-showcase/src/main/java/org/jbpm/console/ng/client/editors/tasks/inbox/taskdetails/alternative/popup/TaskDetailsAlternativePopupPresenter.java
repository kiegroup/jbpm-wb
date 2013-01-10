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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails.alternative.popup;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
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
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.jbpm.console.ng.shared.events.TaskSelectionEvent;
import org.jbpm.console.ng.shared.model.ProcessInstanceSummary;
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
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Task Details Alternative Popup")
public class TaskDetailsAlternativePopupPresenter {

    public interface InboxView
            extends
            UberView<TaskDetailsAlternativePopupPresenter> {

        void displayNotification(String text);

        Label getTaskIdText();

        Label getTaskNameText();

        TextArea getTaskDescriptionTextArea();

        ListBox getTaskPriorityListBox();

        DateBox getDueDate();

        TextBox getUserText();

        TextBox getProcessInstanceIdText();

        ListBox getSubTaskStrategyListBox();

        public String[] getSubTaskStrategies();

        public String[] getPriorities();

        TextBox getTaskStatusText();

        
        Button getpIDetailsButton();
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
    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest place;

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Task Details Alternative Popup";
    }

    @WorkbenchPartView
    public UberView<TaskDetailsAlternativePopupPresenter> getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {

        knowledgeServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary processInstance) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instance Details Perspective");
                placeRequestImpl.addParameter("processInstanceId", view.getProcessInstanceIdText().getText());
                placeRequestImpl.addParameter("processDefId", processInstance.getProcessId());
                placeManager.goTo(placeRequestImpl);
            }
        }).getProcessInstanceById(0, Long.parseLong(view.getProcessInstanceIdText().getText()));



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
                if (details.getStatus().equals("Completed")) {

                    view.getTaskDescriptionTextArea().setEnabled(false);
                    view.getDueDate().setEnabled(false);
                    view.getUserText().setEnabled(false);
                    view.getTaskStatusText().setEnabled(false);

                    view.getProcessInstanceIdText().setEnabled(false);
                }

                view.getTaskIdText().setText(String.valueOf(details.getId()));
                view.getTaskNameText().setText(details.getName());
                view.getTaskDescriptionTextArea().setText(details.getDescription());
                view.getDueDate().setValue(details.getExpirationTime());
                view.getUserText().setText(details.getActualOwner());
                view.getUserText().setEnabled(false);
                view.getTaskStatusText().setText(details.getStatus());
                view.getTaskStatusText().setEnabled(false);
                if(details.getProcessInstanceId() == -1 ){
                    view.getProcessInstanceIdText().setText("None");
                    view.getpIDetailsButton().setEnabled(false);
                }else{
                    view.getProcessInstanceIdText().setText(String.valueOf(details.getProcessInstanceId()));
                }
                
                view.getProcessInstanceIdText().setEnabled(false);
                

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
        long taskId = Long.parseLong(place.getParameter("taskId", "0").toString());
        view.getTaskIdText().setText(String.valueOf(taskId));
        refreshTask(Long.parseLong(view.getTaskIdText().getText()));
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
