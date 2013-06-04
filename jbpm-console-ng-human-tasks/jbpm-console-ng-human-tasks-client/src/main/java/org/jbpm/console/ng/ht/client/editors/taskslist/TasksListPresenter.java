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

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import java.util.HashMap;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.client.util.DateRange;
import org.jbpm.console.ng.ht.client.util.DateUtils;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.workbench.model.menu.MenuSearchItem;


@Dependent
@WorkbenchScreen(identifier = "Tasks List")
public class TasksListPresenter {

    public static final int DAYS_FOR_DAY_VIEW = 1;
    public static final int DAYS_FOR_WEEK_VIEW = 7;
    public static final int DAYS_FOR_MONTH_VIEW = 35;
    public static final int DAYS_FOR_GRID_VIEW = 365;

    private List<TaskSummary> allTaskSummaries;

    private Map<Day, List<TaskSummary>> currentDayTasks;

    public List<TaskSummary> getAllTaskSummaries() {
        return allTaskSummaries;
    }



    public interface TaskListView extends UberView<TasksListPresenter> {

        void displayNotification( String text );

        TaskListMultiDayBox getTaskListMultiDayBox();

        MultiSelectionModel<TaskSummary> getSelectionModel();

        TextBox getSearchBox();

        void refreshTasks();
    }

    public enum TaskType {
        PERSONAL, ACTIVE, GROUP, ALL
    }

    public enum TaskView {
        DAY, WEEK, MONTH, GRID
    }

    @Inject
    private TaskListView view;
    @Inject
    private Identity identity;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();
    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
        @Override
        public Object getKey(TaskSummary item) {
            return item == null ? null : item.getId();
        }
    };


    private Constants constants = GWT.create( Constants.class );

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
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

    public void filterTasks(String text) {
        if(text.equals("")){
            if(allTaskSummaries != null){
                dataProvider.getList().clear();
                dataProvider.setList(new ArrayList<TaskSummary>(allTaskSummaries));
                dataProvider.refresh();

            }
            if(currentDayTasks != null){
                view.getTaskListMultiDayBox().clear();
                for (Day day : currentDayTasks.keySet()) {
                    view.getTaskListMultiDayBox().addTasksByDay(day, new ArrayList<TaskSummary>(currentDayTasks.get(day)));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }else{
            if(allTaskSummaries != null){
                List<TaskSummary> tasks = new ArrayList<TaskSummary>(allTaskSummaries);
                List<TaskSummary> filteredTasksSimple = new ArrayList<TaskSummary>();
                for(TaskSummary ts : tasks){
                    if(ts.getName().toLowerCase().contains(text.toLowerCase())){
                        filteredTasksSimple.add(ts);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.setList(filteredTasksSimple);
                dataProvider.refresh();
            }
            if(currentDayTasks != null){
                Map<Day, List<TaskSummary>> tasksCalendar = new HashMap<Day, List<TaskSummary>>(currentDayTasks);
                Map<Day, List<TaskSummary>> filteredTasksCalendar = new HashMap<Day, List<TaskSummary>>();
                view.getTaskListMultiDayBox().clear();
                for(Day d : tasksCalendar.keySet()){
                    if(filteredTasksCalendar.get(d) == null){
                        filteredTasksCalendar.put(d, new ArrayList<TaskSummary>());
                    }
                    for(TaskSummary ts : tasksCalendar.get(d)){
                        if(ts.getName().toLowerCase().contains(text.toLowerCase())){
                            filteredTasksCalendar.get(d).add(ts);
                        }
                    }
                }
                for (Day day : filteredTasksCalendar.keySet()) {
                    view.getTaskListMultiDayBox().addTasksByDay(day, new ArrayList<TaskSummary>(filteredTasksCalendar.get(day)));
                }
                view.getTaskListMultiDayBox().refresh();
            }
        }


    }

    public void startTasks( final List<Long> selectedTasks,
                            final String userId ) {
        taskServices.call( new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback( List<TaskSummary> tasks ) {
                view.displayNotification( "Task(s) Started" );
                view.refreshTasks();
            }
        } ).startBatch( selectedTasks, userId );
    }

    public void releaseTasks( List<Long> selectedTasks,
                              final String userId ) {
        taskServices.call( new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback( List<TaskSummary> tasks ) {
                view.displayNotification( "Task(s) Released" );
                view.refreshTasks();
            }
        } ).releaseBatch( selectedTasks, userId );
    }

    public void completeTasks( List<Long> selectedTasks,
                               final String userId ) {
        taskServices.call( new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback( List<TaskSummary> tasks ) {
                view.displayNotification( "Task(s) Completed" );
                view.refreshTasks();
            }
        } ).completeBatch( selectedTasks, userId, null );
    }

    public void claimTasks( List<Long> selectedTasks,
                            final String userId ) {
        taskServices.call( new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback( List<TaskSummary> tasks ) {
                view.displayNotification( "Task(s) Claimed" );
                view.refreshTasks();

            }
        } ).claimBatch( selectedTasks, userId );
    }

    public void formClosed( @Observes BeforeClosePlaceEvent closed ) {
        if(closed.getPlace().getIdentifier().equals("Form Display Modeler") || 
                closed.getPlace().getIdentifier().equals("Quick New Task")){
            view.refreshTasks();
        }
    }

    private List<String> getGroups( Identity identity ) {
        List<Role> roles = identity.getRoles();
        List<String> groups = new ArrayList<String>( roles.size() );
        for ( Role r : roles ) {
            groups.add( r.getName().trim() );
        }
        return groups;
    }

    /**
     * Refresh tasks based on specified date, view (day/week/month) and task type.
<<<<<<< HEAD
=======
     * @param date
     * @param taskType
     * @param taskView
>>>>>>> - unifying task lists
     */
    public void refreshTasks(Date date, TaskView taskView, TaskType taskType) {
        switch (taskType) {
            case PERSONAL:
                refreshPersonalTasks(date, taskView);
                break;
            case ACTIVE:
                refreshActiveTasks(date, taskView);
                break;
            case GROUP:
                refreshGroupTasks(date, taskView);
                break;
            case ALL:
                refreshAllTasks(date, taskView);
                break;
            default:
                throw new IllegalStateException("Unrecognized task type '" + taskType + "'!");
        }
    }

    public void refreshPersonalTasks(Date date, TaskView taskView) {
        Date fromDate = determineFirstDateForTaskViewBasedOnSpecifiedDate(date, taskView);
        int daysTotal = determineNumberOfDaysForTaskView(taskView);

        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Ready");
        statuses.add("InProgress");
        statuses.add("Created");
        statuses.add("Reserved");
        if (taskView.equals(TaskView.GRID)){
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    allTaskSummaries = tasks;
                    filterTasks(view.getSearchBox().getText());
                }
            }).getTasksOwnedByExpirationDateOptional(identity.getName(), statuses, null, "en-UK");

        } else {
            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
                @Override
                public void callback(Map<Day, List<TaskSummary>> tasks) {
                    currentDayTasks = tasks;
                    filterTasks(view.getSearchBox().getText());
                }
            }).getTasksOwnedFromDateToDateByDays(identity.getName(), statuses, fromDate, daysTotal, "en-UK");
        }
    }

    private int determineNumberOfDaysForTaskView(TaskView taskView) {
        int daysTotal;
        switch (taskView) {
            case DAY:
                daysTotal = DAYS_FOR_DAY_VIEW;
                break;
            case WEEK:
                daysTotal = DAYS_FOR_WEEK_VIEW;
                break;
            case MONTH:
                daysTotal = DAYS_FOR_MONTH_VIEW;
                break;
            case GRID:
                daysTotal = DAYS_FOR_GRID_VIEW;
                break;
            default:
                throw new IllegalStateException("Unreconginized view type '" + taskView + "'!");
        }
        return daysTotal;
    }

    private Date determineFirstDateForTaskViewBasedOnSpecifiedDate(Date date, TaskView taskView) {
        Date fromDate;
        switch (taskView) {
            case DAY:
                fromDate = new Date(date.getTime());
                break;
            case WEEK:
                DateRange weekRange = DateUtils.getWorkWeekDateRange(date);
                fromDate = weekRange.getStartDate();
                break;
            case MONTH:
                DateRange monthRange = DateUtils.getMonthDateRange(date);
                DateRange firstWeekRange = DateUtils.getWeekDateRange(monthRange.getStartDate());
                fromDate = firstWeekRange.getStartDate();
                break;
            case GRID:
                fromDate = new Date(date.getTime());
                break;
            default:
                throw new IllegalStateException("Unreconginized view type '" + taskView + "'!");
        }
        return fromDate;
    }

    public void refreshActiveTasks(Date date, TaskView taskView) {
        Date fromDate = determineFirstDateForTaskViewBasedOnSpecifiedDate(date, taskView);
        int daysTotal = determineNumberOfDaysForTaskView(taskView);

        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Ready");
        statuses.add("Reserved");
        statuses.add("InProgress");
        if(taskView.equals(TaskView.GRID)) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    allTaskSummaries = tasks;
                    filterTasks(view.getSearchBox().getText());
                    view.getSelectionModel().clear();
                }
            }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), statuses, null, "en-UK");
        } else {
            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
                @Override
                public void callback(Map<Day, List<TaskSummary>> tasks) {
                    currentDayTasks = tasks;
                    filterTasks(view.getSearchBox().getText());
                }
            }).getTasksAssignedAsPotentialOwnerFromDateToDateByDays(identity.getName(), fromDate, daysTotal, "en-UK");
        }
    }

    public void refreshGroupTasks(Date date, TaskView taskView) {
        Date fromDate = determineFirstDateForTaskViewBasedOnSpecifiedDate(date, taskView);
        int daysTotal = determineNumberOfDaysForTaskView(taskView);

        List<String> groups = getGroups(identity);
        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Ready");

        if (taskView.equals(TaskView.GRID)) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    allTaskSummaries = tasks;
                    filterTasks(view.getSearchBox().getText());
                    view.getSelectionModel().clear();
                }
            }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), statuses, null, "en-UK");
        } else {
            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
                @Override
                public void callback(Map<Day, List<TaskSummary>> tasks) {
                    currentDayTasks = tasks;
                    filterTasks(view.getSearchBox().getText());
                }
            }).getTasksAssignedFromDateToDateByGroupsByDays(identity.getName(), groups, fromDate, daysTotal, "en-UK");
        }
    }

    public void refreshAllTasks(Date date, TaskView taskView) {
        Date fromDate = determineFirstDateForTaskViewBasedOnSpecifiedDate(date, taskView);
        int daysTotal = determineNumberOfDaysForTaskView(taskView);

        List<String> statuses = new ArrayList<String>(4);
        statuses.add("Created");
        statuses.add("Ready");
        statuses.add("Reserved");
        statuses.add("InProgress");
        statuses.add("Suspended");
        statuses.add("Suspended");
        statuses.add("Failed");
        statuses.add("Error");
        statuses.add("Exited");
        statuses.add("Obsolete");
        statuses.add("Completed");

        if (taskView.equals(TaskView.GRID)) {
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    allTaskSummaries = tasks;
                    filterTasks(view.getSearchBox().getText());
                    view.getSelectionModel().clear();
                }
            }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), statuses, null, "en-UK");
        } else {
            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
                @Override
                public void callback(Map<Day, List<TaskSummary>> tasks) {
                    currentDayTasks = tasks;
                    filterTasks(view.getSearchBox().getText());
                }
            }).getTasksAssignedAsPotentialOwnerFromDateToDateByDays(identity.getName(), statuses, fromDate, daysTotal, "en-UK");
        }
    }

    public void addDataDisplay(HasData<TaskSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<TaskSummary> getDataProvider() {
        return dataProvider;
    }

}
