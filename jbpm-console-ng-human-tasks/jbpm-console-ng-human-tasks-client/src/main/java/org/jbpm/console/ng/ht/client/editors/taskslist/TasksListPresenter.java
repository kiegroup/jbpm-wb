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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.enterprise.event.Observes;

import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@Dependent
@WorkbenchScreen(identifier = "Tasks List")
public class TasksListPresenter {

    public interface TaskListView
            extends
            UberView<TasksListPresenter> {

        void displayNotification(String text);

        TaskListMultiDayBox getTaskListMultiDayBox();

        void refreshTasks();
    }
    @Inject
    private TaskListView view;
    @Inject
    private Identity identity;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Tasks List";
    }

    @WorkbenchPartView
    public UberView<TasksListPresenter> getView() {
        return view;
    }

    public TasksListPresenter() {
    }

    @PostConstruct
    public void init() {
    }
    private List<String> getGroups(Identity identity){
        List<Role> roles = identity.getRoles();
        List<String> groups = new ArrayList<String>(roles.size());
        for (Role r : roles) {
            groups.add(r.getName().trim());
        }
        return groups;
    }
    public void refresh3DaysActiveTasks(){
        List<String> groups = getGroups(identity);
        Date today = new Date();
        Date daysAfter = new Date(today.getTime() + ((24 * 60 * 60 * 1000) * 3));
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {
                    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }).getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(identity.getName(), groups, today, daysAfter, "en-UK");
    
    }
    public void refreshWeekActiveTasks(){
        List<String> groups = getGroups(identity);
        Date today = new Date();
        Date daysAfter = new Date(today.getTime() + ((24 * 60 * 60 * 1000) * 5));
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {
                    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }).getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(identity.getName(), groups, today, daysAfter, "en-UK");
    }
    public void refreshMonthActiveTasks(){
        List<String> groups = getGroups(identity);
        Date today = new Date();
        Date daysAfter = new Date(today.getTime() + new Long((24 * 60 * 60 * 1000) *21L));
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {
                    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }).getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(identity.getName(), groups, today, daysAfter, "en-UK");
    
    }
    
    public void refreshActiveTasks() {
        List<String> groups = getGroups(identity);
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {
                    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }).getTasksAssignedPersonalAndGroupsTasksByDays(identity.getName(), groups, "en-UK");
    }

    public void refreshAllTasks() {
        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Ready");
        statuses.add("InProgress");
        statuses.add("Created");
        statuses.add("Reserved");
        statuses.add("Completed");
        statuses.add("Aborted");
        statuses.add("Suspended");
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();



            }
        }).getTasksOwnedByDays(identity.getName(),statuses, "en-UK");
    }

    public void refreshPersonalTasks() {

        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Ready");
        statuses.add("InProgress");
        statuses.add("Created");
        statuses.add("Reserved");
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
               view.getTaskListMultiDayBox().clear();
               for (Day day : tasks.keySet()) {    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();


            }
        }).getTasksOwnedByDays(identity.getName(), statuses, "en-UK");
    }

    public void refreshGroupTasks() {
        List<String> groups = getGroups(identity);
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.getTaskListMultiDayBox().clear();
                for (Day day : tasks.keySet()) {    
                    view.getTaskListMultiDayBox().addTasksByDay(day, tasks.get(day));
                }
                view.getTaskListMultiDayBox().refresh();


            }
        }).getTasksAssignedByGroupsByDays(groups, "en-UK");
    }

    public void startTasks(final List<Long> selectedTasks, final String userId) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Started");
                view.refreshTasks();
            }
        }).startBatch(selectedTasks, userId);



    }

    public void releaseTasks(List<Long> selectedTasks, final String userId) {


        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Released");
                view.refreshTasks();
            }
        }).releaseBatch(selectedTasks, userId);

    }

    public void completeTasks(List<Long> selectedTasks, final String userId) {


        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Completed");
                view.refreshTasks();
            }
        }).completeBatch(selectedTasks, userId, null);



    }

    public void claimTasks(List<Long> selectedTasks, final String userId) {

        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Claimed");
                view.refreshTasks();

            }
        }).claimBatch(selectedTasks, userId);



    }

    @OnReveal
    public void onReveal() {
        view.refreshTasks();
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        view.refreshTasks();
    }
}
