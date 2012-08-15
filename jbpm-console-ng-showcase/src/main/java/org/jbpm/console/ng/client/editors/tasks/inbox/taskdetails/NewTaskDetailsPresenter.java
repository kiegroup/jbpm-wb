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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.model.TaskSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "Task Details")
public class NewTaskDetailsPresenter {

    public interface InboxView
            extends
            IsWidget {

        void displayNotification(String text);

        TextBox getTaskIdText();

        TextBox getGroupText();

        TextBox getTaskNameText();

        TextBox getTaskDescriptionText();

        TextBox getTaskPriorityText();

        DatePicker getDueDate();
        
        TextBox getUserText();
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Task";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public NewTaskDetailsPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void updateTask(final long taskId, final String taskDescription, Date dueDate, String priority) {

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
                    view.displayNotification("Task Description Updated for Task id = " + taskId + ")");

                }
            }).setDescriptions(taskId, new ArrayList<String>() {
                {
                    add(taskDescription);
                }
            });

            taskServices.call(new RemoteCallback<Void>() {
                @Override
                public void callback(Void nothing) {
                    view.displayNotification("Task Expiration Date Updated for Task id = " + taskId + ")");

                }
            }).setExpirationDate(taskId, dueDate);




        }

    }

    public void refreshTask(long taskId) {
        taskServices.call(new RemoteCallback<TaskSummary>() {
            @Override
            public void callback(TaskSummary details) {
                view.getTaskNameText().setText(details.getName());
                view.getTaskPriorityText().setText(String.valueOf(details.getPriority()));
                view.getTaskDescriptionText().setText(details.getDescription());
                view.getDueDate().setValue(details.getExpirationTime());
                view.getUserText().setText(details.getActualOwner());
            }
        }).getTaskDetails(taskId);

    }
}
