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
package org.jbpm.console.ng.client.editors.tasklist.alternative;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.event.Observes;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;

import org.jbpm.console.ng.client.i18n.Constants;
import org.jbpm.console.ng.client.resources.ShowcaseImages;
import org.jbpm.console.ng.shared.events.UserTaskEvent;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite
        implements
        TasksListPresenter.InboxView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
   
    private TasksListPresenter presenter;
    @Inject
    @DataField
    public Button refreshTasksButton;
    
    @Inject
    @DataField
    public TaskListBox taskListBox;
    @Inject
    private Event<NotificationEvent> notification;
    private Constants constants = GWT.create(Constants.class);
    ShowcaseImages images = GWT.create(ShowcaseImages.class);

    @Override
    public void init(TasksListPresenter presenter) {
        this.presenter = presenter;

        taskListBox.setPresenter(presenter);

        taskListBox.setIdentity(identity);

        refreshTasks();

    }

    public void recieveStatusChanged(@Observes UserTaskEvent event) {
        refreshTasks();

    }

//
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public void refreshTasks() {
//        Boolean isCheckedCompleted = showCompletedCheck.getValue();
//        Boolean isCheckedGroupTasks = showGroupTasksCheck.getValue();
//        Boolean isCheckedPersonalTasks = showPersonalTasksCheck.getValue();
        presenter.refreshTasks(identity.getName(), false, false, false);
    }

    public TaskListBox getTaskListBox() {
        return taskListBox;
    }

    @EventHandler("refreshTasksButton")
    public void refreshTasksButton(ClickEvent e) {
        refreshTasks();
    }
}
