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
package org.jbpm.console.ng.client.editors.tasks.inbox.quicknewtask.erraiui;

import org.jbpm.console.ng.client.editors.tasks.inbox.quicknewtask.*;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Errai UI - Quick New Task")
public class NewQuickPersonalTaskPresenter {

    public interface InboxView
            extends
            UberView<NewQuickPersonalTaskPresenter> {

        void displayNotification(String text);
    }
    @Inject
    InboxView view;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Errai UI - New Task";
    }

    @WorkbenchPartView
    public UberView<NewQuickPersonalTaskPresenter> getView() {
        return view;
    }

    public NewQuickPersonalTaskPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void addQuickTask(final String userId, String taskName) {
        String str = "(with (new Task()) { taskData = (with( new TaskData()) { expirationTime = new java.util.Date() } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = ";
        if (userId != null && !userId.equals("")) {
            str += " [new User('" + userId + "')  ], }),";
        }
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')]})";
        taskServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long taskId) {
                view.displayNotification("Task Created (id = " + taskId + ")");
                
            }
        }).addTask(str, null);

    }
}
