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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.ht.client.i8n.Constants;
import org.jbpm.console.ng.ht.client.util.DateRange;
import org.jbpm.console.ng.ht.client.util.DateUtils;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchScreen(identifier = "Tasks List")
public class TasksListPresenter {

    public static final int DAYS_FOR_DAY_VIEW = 1;
    public static final int DAYS_FOR_WEEK_VIEW = 5;
    public static final int DAYS_FOR_MONTH_VIEW = 35;

    public interface TaskListView extends UberView<TasksListPresenter> {

        void displayNotification( String text );

        TaskListMultiDayBox getTaskListMultiDayBox();

        void refreshTasks();
    }

    public enum TaskType {
        PERSONAL, ACTIVE, GROUP, ALL
    }

    public enum TaskView {
        DAY, WEEK, MONTH
    }

    @Inject
    private TaskListView view;
    @Inject
    private Identity identity;
    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    private Constants constants = GWT.create( Constants.class );

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List_Calendar_View();
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

    public void refreshActiveTasks( Date fromDate,
                                    int daysTotal ) {
        List<String> groups = getGroups( identity );
        taskServices.call( new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback( Map<Day, List<TaskSummary>> tasks ) {
                view.getTaskListMultiDayBox().clear();
                for ( Day day : tasks.keySet() ) {

                    view.getTaskListMultiDayBox().addTasksByDay( day, tasks.get( day ) );
                }
                view.getTaskListMultiDayBox().refresh();
            }
        } ).getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays( identity.getName(), groups, fromDate, daysTotal, "en-UK" );
    }

    public void refresh3DaysActiveTasks( Date fromDate ) {
        refreshActiveTasks( fromDate, DAYS_FOR_DAY_VIEW );
    }

    public void refreshWeekActiveTasks( Date fromDate ) {
        refreshActiveTasks( fromDate, DAYS_FOR_WEEK_VIEW );
    }

    public void refreshMonthActiveTasks( Date fromDate ) {
        refreshActiveTasks( fromDate, DAYS_FOR_MONTH_VIEW );
    }

    public void refreshAllTasks( Date fromDate,
                                 int daysTotal ) {
        List<String> statuses = new ArrayList<String>( 4 );
        statuses.add( "Created" );
        statuses.add( "Ready" );
        statuses.add( "Reserved" );
        statuses.add( "InProgress" );
        statuses.add( "Suspended" );
        statuses.add( "Suspended" );
        statuses.add( "Failed" );
        statuses.add( "Error" );
        statuses.add( "Exited" );
        statuses.add( "Obsolete" );
        taskServices.call( new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback( Map<Day, List<TaskSummary>> tasks ) {
                view.getTaskListMultiDayBox().clear();
                for ( Day day : tasks.keySet() ) {
                    view.getTaskListMultiDayBox().addTasksByDay( day, tasks.get( day ) );
                }
                view.getTaskListMultiDayBox().refresh();
            }
        } ).getTasksOwnedFromDateToDateByDays( identity.getName(), statuses, fromDate, daysTotal, "en-UK" );
    }

    public void refreshPersonalTasks( Date fromDate,
                                      int daysTotal ) {
        List<String> statuses = new ArrayList<String>( 4 );
        statuses.add( "Ready" );
        statuses.add( "InProgress" );
        statuses.add( "Created" );
        statuses.add( "Reserved" );
        taskServices.call( new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback( Map<Day, List<TaskSummary>> tasks ) {
                view.getTaskListMultiDayBox().clear();
                for ( Day day : tasks.keySet() ) {
                    view.getTaskListMultiDayBox().addTasksByDay( day, tasks.get( day ) );
                }
                view.getTaskListMultiDayBox().refresh();
            }
        } ).getTasksOwnedFromDateToDateByDays( identity.getName(), statuses, fromDate, daysTotal, "en-UK" );
    }

    public void refreshGroupTasks( Date fromDate,
                                   int daysTotal ) {
        List<String> groups = getGroups( identity );
        taskServices.call( new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback( Map<Day, List<TaskSummary>> tasks ) {
                view.getTaskListMultiDayBox().clear();
                for ( Day day : tasks.keySet() ) {
                    view.getTaskListMultiDayBox().addTasksByDay( day, tasks.get( day ) );
                }
                view.getTaskListMultiDayBox().refresh();
            }
        } ).getTasksAssignedFromDateToDateByGroupsByDays( groups, fromDate, daysTotal, "en-UK" );
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

    @OnReveal
    public void onReveal() {
        view.refreshTasks();
    }

    public void formClosed( @Observes BeforeClosePlaceEvent closed ) {
        view.refreshTasks();
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
     * @param fromDate
     * @param taskType
     * @param taskView
     */
    public void refreshTasks( Date date,
                              TaskView taskView,
                              TaskType taskType ) {
        Date fromDate;
        int daysTotal;
        switch ( taskView ) {
            case DAY:
                daysTotal = DAYS_FOR_DAY_VIEW;
                fromDate = new Date( date.getTime() );
                break;
            case WEEK:
                daysTotal = DAYS_FOR_WEEK_VIEW;
                DateRange weekRange = DateUtils.getWeekDateRange( date );
                fromDate = weekRange.getStartDate();
                break;
            case MONTH:
                daysTotal = DAYS_FOR_MONTH_VIEW;
                DateRange monthRange = DateUtils.getMonthDateRange( date );
                fromDate = monthRange.getStartDate();
                break;
            default:
                throw new IllegalStateException( "Unreconginized view type '" + taskView + "'!" );
        }
        switch ( taskType ) {
            case PERSONAL:
                refreshPersonalTasks( fromDate, daysTotal );
                break;
            case ACTIVE:
                refreshActiveTasks( fromDate, daysTotal );
                break;
            case GROUP:
                refreshGroupTasks( fromDate, daysTotal );
                break;
            case ALL:
                refreshAllTasks( fromDate, daysTotal );
                break;
            default:
                throw new IllegalStateException( "Unrecognized task type '" + taskType + "'!" );
        }
    }

}
