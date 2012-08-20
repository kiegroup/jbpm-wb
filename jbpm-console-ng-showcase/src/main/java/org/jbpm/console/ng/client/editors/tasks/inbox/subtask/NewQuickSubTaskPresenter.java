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
package org.jbpm.console.ng.client.editors.tasks.inbox.subtask;

import org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails.*;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.model.TaskSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "Quick New Sub Task")
public class NewQuickSubTaskPresenter {

    public interface InboxView
            extends
            IsWidget {

        void displayNotification(String text);
        
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Quick New Sub Task";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public NewQuickSubTaskPresenter() {
    }

    @PostConstruct
    public void init() {
    }

   
    public void addSubTask(final long parentId, String assignee, String taskName) {
        String str = "(with (new Task()) { taskData = (with( new TaskData( )) { parentId = "+parentId+" } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        if (assignee != null && !assignee.equals("")) {
            str += " [new User('" + assignee + "')  ], }),";
        }
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')] })";
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Sub Task Created (id = " + taskId + " for parent: "+parentId+")");

            }
        }).addTask(str, null);
    }
}
