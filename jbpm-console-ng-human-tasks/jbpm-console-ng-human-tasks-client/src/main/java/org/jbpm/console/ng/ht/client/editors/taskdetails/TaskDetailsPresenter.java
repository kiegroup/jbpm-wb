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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.client.util.UTCDateBox;
import org.jbpm.console.ng.ht.client.util.UTCTimeBox;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskStyleEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Task Details")
public class TaskDetailsPresenter {

    private Constants constants = GWT.create(Constants.class);

    public interface TaskDetailsView extends UberView<TaskDetailsPresenter> {

        void displayNotification(String text);

        TextArea getTaskDescriptionTextArea();

        ListBox getTaskPriorityListBox();

        UTCDateBox getDueDate();
        
        UTCTimeBox getDueDateTime();

        TextBox getUserText();

        TextBox getProcessInstanceIdText();

        TextBox getProcessIdText();

        HTML getLogTextArea();

        // Commented out until we add the posibility of adding sub tasks
        // ListBox getSubTaskStrategyListBox();
        // Commented out until we add the posibility of adding sub tasks
        // public String[] getSubTaskStrategies();
        public String[] getPriorities();

        TextBox getTaskStatusText();

        Button getpIDetailsButton();

    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    TaskDetailsView view;

    @Inject
    Identity identity;
    
    @Inject
    private Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected;

    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @Inject
    private Event<TaskRefreshedEvent> taskRefreshed;

    @Inject
    private Event<TaskStyleEvent> taskStyleEvent;

    @Inject
    private Event<TaskCalendarEvent> taskCalendarEvent;

    private PlaceRequest place;

    private long currentTaskId = 0;

    private String currentTaskName = "";

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @WorkbenchPartView
    public UberView<TaskDetailsPresenter> getView() {
        return view;
    }

    public void goToProcessInstanceDetails() {
        
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary processInstance) {
                
                placeManager.goTo("Process Instances");
                processInstanceSelected.fire(new ProcessInstancesWithDetailsRequestEvent(processInstance.getDeploymentId(),
                                processInstance.getId(), processInstance.getProcessId()));
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getProcessInstanceById(Long.parseLong(view.getProcessInstanceIdText().getText()));
        
    }

    public void updateTask(final String taskDescription,
            final String userId,
            // final String subTaskStrategy,
            final Date dueDate,
            final int priority) {

        if (currentTaskId > 0) {
            List<String> descriptions = new ArrayList<String>();
            descriptions.add(taskDescription);

            List<String> names = new ArrayList<String>();
            names.add(currentTaskName);

            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Details Updated for Task id = " + currentTaskId + ")");
                    taskRefreshed.fire(new TaskRefreshedEvent(currentTaskId));
                    taskCalendarEvent.fire(new TaskCalendarEvent(currentTaskId));
                }
            }, new ErrorCallback<Message>() {
                  @Override
                  public boolean error( Message message, Throwable throwable ) {
                      ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                      return true;
                  }
              }).updateSimpleTaskDetails(currentTaskId, names, Integer.valueOf(priority), descriptions,
                    // subTaskStrategy,
                    dueDate);

        }

    }

    public void refreshTask() {

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

                view.getTaskDescriptionTextArea().setText(details.getDescription());
                view.getDueDate().setValue(UTCDateBox.date2utc(details.getExpirationTime()));
                view.getDueDateTime().setValue(UTCDateBox.date2utc(details.getExpirationTime()));
                view.getUserText().setText(details.getActualOwner());
                view.getUserText().setEnabled(false);
                view.getTaskStatusText().setText(details.getStatus());
                view.getTaskStatusText().setEnabled(false);
                view.getProcessIdText().setEnabled(false);
                if (details.getProcessInstanceId() == -1) {
                    view.getProcessInstanceIdText().setText("None");
                    view.getProcessIdText().setText("None");
                    view.getpIDetailsButton().setEnabled(false);
                } else {
                    view.getProcessInstanceIdText().setText(String.valueOf(details.getProcessInstanceId()));
                    view.getProcessIdText().setText(details.getProcessId());
                }

                view.getProcessInstanceIdText().setEnabled(false);

                int i = 0;
                // Commented out until we add the posibility of adding sub tasks
                // for (String strategy : view.getSubTaskStrategies()) {
                // if (details.getSubTaskStrategy().equals(strategy)) {
                // view.getSubTaskStrategyListBox().setSelectedIndex(i);
                // }
                // i++;
                // }
                i = 0;
                for (String priority : view.getPriorities()) {
                    if (details.getPriority() == i) {
                        view.getTaskPriorityListBox().setSelectedIndex(i);
                    }
                    i++;
                }

                changeStyleRow(details.getId());

            }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error( Message message, Throwable throwable ) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
          }
      }).getTaskDetails(currentTaskId);
        taskServices.call(new RemoteCallback<List<TaskEventSummary>>() {
            @Override
            public void callback(List<TaskEventSummary> events) {
                view.getLogTextArea().setText("");
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
                for (TaskEventSummary tes : events) {
                    String timeStamp = format.format(tes.getLogTime());
                    safeHtmlBuilder.appendEscapedLines(timeStamp + ": Task - " + tes.getType() + " (" + tes.getUserId() + ") \n");
                }
                view.getLogTextArea().setHTML(safeHtmlBuilder.toSafeHtml());
            }

        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getAllTaskEvents(currentTaskId);

    }
    
   
        

    private void changeStyleRow(long idTask) {
        taskStyleEvent.fire(new TaskStyleEvent(idTask));
    }

    @OnOpen
    public void onOpen() {
        this.currentTaskId = Long.parseLong(place.getParameter("taskId", "0").toString());
        this.currentTaskName = place.getParameter("taskName", "");
        refreshTask();
    }

    public void onTaskRefreshedEvent(@Observes TaskRefreshedEvent event) {
        if (currentTaskId == event.getTaskId()) {
            refreshTask();
        }
    }
    @OnClose
    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

}
