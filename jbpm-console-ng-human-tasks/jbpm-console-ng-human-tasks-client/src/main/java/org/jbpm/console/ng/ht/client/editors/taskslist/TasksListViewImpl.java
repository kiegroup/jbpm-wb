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
package org.jbpm.console.ng.ht.client.editors.taskslist;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import javax.enterprise.event.Observes;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.security.Identity;

import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "TasksListViewImpl.html")
public class TasksListViewImpl extends Composite
        implements
        TasksListPresenter.TaskListView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
   
    private TasksListPresenter presenter;
    @Inject
    @DataField
    public Button dayViewTasksButton;
    
    @Inject
    @DataField
    public Button weekViewTasksButton;
    
    @Inject
    @DataField
    public Button createQuickTaskButton;
    
    @Inject
    @DataField
    public FlowPanel tasksViewContainer;
    
    @Inject
    private  TaskListMultiDayBox taskListMultiDayBox;
    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(TasksListPresenter presenter) {
        this.presenter = presenter;

        taskListMultiDayBox.setPresenter(presenter);

        

        refreshTasks();
        // By Default we will start in Day View
        tasksViewContainer.setStyleName("day");
        tasksViewContainer.add(taskListMultiDayBox);
        

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

    public TaskListMultiDayBox getTaskListMultiDayBox() {
        return taskListMultiDayBox;
    }

   

    @EventHandler("dayViewTasksButton")
    public void dayViewTasksButton(ClickEvent e) {
        tasksViewContainer.setStyleName("day");
        refreshTasks();
    }
    
    @EventHandler("weekViewTasksButton")
    public void weekViewTasksButton(ClickEvent e) {
        tasksViewContainer.setStyleName("week");
        refreshTasks();
    }
    
    @EventHandler("createQuickTaskButton")
    public void createQuickTaskButton(ClickEvent e) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
        placeManager.goTo(placeRequestImpl);
    }
}
