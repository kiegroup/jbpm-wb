/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.mobile.ht.client.newtask;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;


/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class NewTaskPresenter  {

    public interface NewTaskView extends MGWTUberView<NewTaskPresenter> {

        HasText getTaskNameTextBox();

        HasValue<Boolean> getAssignToMeCheckBox();

        HasText getDueOnDateBox();

        MListBox getPriorityListBox();

        HasText getUserTextBox();

        HasTapHandlers getAddTaskButton();
        
        void goBackToTaskList();

    }

    @Inject
    private NewTaskView view;
    
    
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    public NewTaskPresenter() {
    }


    public void addTask(final List<String> users, List<String> groups, final String taskName, int priority,
            boolean isAssignToMe, long dueDate, long dueDateTime) {
        Map<String, Object> templateVars = new HashMap<String, Object>();
        Date due = new Date(dueDate + dueDateTime);
        templateVars.put("due", due);
        templateVars.put("now", new Date());

        String str = "(with (new Task()) { priority = " + priority
                + ", taskData = (with( new TaskData()) { createdOn = now, expirationTime = due } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        str += " [";
        if (users != null && !users.isEmpty()) {

            for (String user : users) {
                str += "new User('" + user + "'), ";
            }

        }
        if (groups != null && !groups.isEmpty()) {

            for (String group : groups) {
                str += "new Group('" + group + "'), ";
            }

        }
        str += "], businessAdministrators = [ new Group('Administrators') ],}),";
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')]})";

        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.goBackToTaskList();
            }
        }).addTask(str, null, templateVars);
    }

}
