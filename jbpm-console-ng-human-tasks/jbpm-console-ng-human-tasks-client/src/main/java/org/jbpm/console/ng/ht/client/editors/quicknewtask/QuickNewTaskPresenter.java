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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.he.client.event.ActionHumanEvent;
import org.jbpm.console.ng.he.client.event.HumanEvent;
import org.jbpm.console.ng.he.client.event.LevelHumanEvent;
import org.jbpm.console.ng.he.client.event.StatusHumanEvent;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@WorkbenchPopup(identifier = "Quick New Task")
public class QuickNewTaskPresenter {

    private Constants constants = GWT.create(Constants.class);

    public interface QuickNewTaskView extends UberView<QuickNewTaskPresenter> {

        void displayNotification(String text);

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
    private Event<HumanEvent> pointHistoryEvent;

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Quick_Task();
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

    public void addTask(final List<String> users, List<String> groups, String taskName, int priority, boolean isQuickTask,
            Date due) {

        Map<String, Object> templateVars = new HashMap<String, Object>();
        templateVars.put("due", due);

        String str = "(with (new Task()) { priority = " + priority
                + ", taskData = (with( new TaskData()) { expirationTime = due } ), ";
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
        str += "], }),";
        str += "names = [ new I18NText( 'en-UK', '" + taskName + "')]})";
        if (isQuickTask) {
            taskServices.call(new RemoteCallback<Long>() {
                @Override
                public void callback(Long taskId) {
                    view.displayNotification("Task Created and Started (id = " + taskId + ")");
                    pointHistoryEvent.fire(new HumanEvent(taskId.toString(), identity.getName(),
                            ActionHumanEvent.HUMAN_TASKS_CREATED_STARTED, StatusHumanEvent.SUCCESS, LevelHumanEvent.INFO));
                    close();
                }
            }).addTaskAndStart(str, null, identity.getName(), templateVars);
        } else {
            taskServices.call(new RemoteCallback<Long>() {
                @Override
                public void callback(Long taskId) {
                    view.displayNotification("Task Created (id = " + taskId + ")");
                    pointHistoryEvent.fire(new HumanEvent(taskId.toString(), identity.getName(),
                            ActionHumanEvent.HUMAN_TASKS_CREATED, StatusHumanEvent.SUCCESS, LevelHumanEvent.INFO));
                    close();
                }
            }).addTask(str, null, templateVars);
        }

    }

    @OnReveal
    public void onReveal() {
        view.getTaskNameText().setFocus(true);

    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
