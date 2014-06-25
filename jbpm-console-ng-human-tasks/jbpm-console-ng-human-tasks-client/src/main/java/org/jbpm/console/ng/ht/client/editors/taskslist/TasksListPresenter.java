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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.gc.client.experimental.pagination.GenericDataProvider;
import org.jbpm.console.ng.gc.client.experimental.pagination.JBPMSimplePager;
import org.jbpm.console.ng.gc.client.util.DataGridUtils;
import org.jbpm.console.ng.gc.client.util.DateRange;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.jbpm.console.ng.gc.client.util.TaskUtils;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskType;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskView;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.EditPanelEvent;
import org.jbpm.console.ng.ga.model.events.SearchEvent;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Tasks List")
public class TasksListPresenter implements GenericDataProvider<TaskSummary>{

    public TasksListPresenter() {
        makeMenuBar();
    }

    @Override
    public List<TaskSummary> getAllServerSideData() {
        return allTaskSummaries;
    }

    @Override
    public ListDataProvider<TaskSummary> getClientSideDataProvider() {
        return dataProvider;
    }

    @Override
    public void refresh(int start, int offset, boolean clear) {
        refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 
                        start, offset, clear);
    }

    public interface TaskListView extends UberView<TasksListPresenter> {

        void displayNotification(String text);

        TaskListMultiDayBox getTaskListMultiDayBox();

        void refreshTasks();

        Date getCurrentDate();

        TaskView getCurrentView();

        TaskType getCurrentTaskType();

        String getCurrentFilter();

        void setCurrentFilter(String currentFilter);

        DataGrid<TaskSummary> getTaskListGrid();

        void setGridView();

        void setCalendarView(TaskView taskView);

        void setActiveButton(TaskType taskType);

        void changeCurrentDate(Date date);
        
        JBPMSimplePager getPager();
    }

    private Constants constants = GWT.create(Constants.class);

    private List<TaskSummary> allTaskSummaries;

    private Map<Day, List<TaskSummary>> currentDayTasks;

    private Menus menus;

    private PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TaskListView view;

    @Inject
    private Identity identity;

    @Inject
    private Caller<TaskServiceEntryPoint> taskServices;

    @Inject
    private Event<ClearSearchEvent> clearSearchEvent;


    private ListDataProvider<TaskSummary> dataProvider = new ListDataProvider<TaskSummary>();

    public static final ProvidesKey<TaskSummary> KEY_PROVIDER = new ProvidesKey<TaskSummary>() {
        @Override
        public Object getKey(TaskSummary item) {
            return item == null ? null : item.getId();
        }
    };

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }

    @WorkbenchPartView
    public UberView<TasksListPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @OnFocus
    public void onFocus() {
        refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 
                        view.getPager().getCurrentPage() * DataGridUtils.pageSize, (DataGridUtils.pageSize * DataGridUtils.clientSidePages), true);
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {
        String currentView = place.getParameter("currentView", "");
        String currentTaskType = place.getParameter("currentTaskType", "");
        String currentDateString = place.getParameter("currentDate", "");
        view.setCurrentFilter(place.getParameter("currentFilter", ""));
        view.changeCurrentDate(!currentDateString.equals("") ? new Date(Long.valueOf(currentDateString)) : new Date());
        TaskView currentTaskView = !currentView.equals("") ? TaskView.valueOf(currentView) : TaskView.GRID;
        TaskType currentTaskTypeEnum = !currentTaskType.equals("") ? TaskType.valueOf(currentTaskType) : TaskType.ACTIVE;
        refreshView(currentTaskView);
        view.setActiveButton(currentTaskTypeEnum);
    }

    public List<TaskSummary> getAllTaskSummaries() {
        return allTaskSummaries;
    }

    public void filterTasks(String text, boolean clear) {
        ColumnSortList.ColumnSortInfo sortInfo = view.getTaskListGrid().getColumnSortList().size() > 0 ? view.getTaskListGrid()
                .getColumnSortList().get(0) : null;
        if (allTaskSummaries != null) {
            this.filterGrid(sortInfo, text, clear);
        }
        if (currentDayTasks != null) {
            this.filterCalendar(DataGridUtils.idTaskCalendar, text);
        }
        refreshEditPanel();
        view.getTaskListGrid().setFocus(true);
    }

    private void filterGrid(ColumnSortList.ColumnSortInfo sortInfo, String text, boolean clear) {
        List<TaskSummary> filteredTasksSimple = Lists.newArrayList();
        if (!text.equals("")) {
            for (TaskSummary ts : allTaskSummaries) {
                if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredTasksSimple.add(ts);
                }
            }
        } else {
            filteredTasksSimple = allTaskSummaries;
        }
        if(clear){
            dataProvider.getList().clear();
        }
        dataProvider.getList().addAll(filteredTasksSimple);
        if (sortInfo != null && sortInfo.isAscending()) {
            view.getTaskListGrid().getColumnSortList().clear();
            ColumnSortInfo columnSortInfo = new ColumnSortInfo(sortInfo.getColumn(), sortInfo.isAscending());
            view.getTaskListGrid().getColumnSortList().push(columnSortInfo);
            ColumnSortEvent.fire(view.getTaskListGrid(), view.getTaskListGrid().getColumnSortList());
        }
    }

    private void filterCalendar(Long idTaskSelected, String text) {
        Map<Day, List<TaskSummary>> tasksCalendar = new LinkedHashMap<Day, List<TaskSummary>>(currentDayTasks);
        Map<Day, List<TaskSummary>> filteredTasksCalendar = new LinkedHashMap<Day, List<TaskSummary>>();
        view.getTaskListMultiDayBox().clear();
        if (!text.equals("")) {
            for (Day day : tasksCalendar.keySet()) {
                if (filteredTasksCalendar.get(day) == null) {
                    filteredTasksCalendar.put(day, new ArrayList<TaskSummary>());
                }
                for (TaskSummary ts : tasksCalendar.get(day)) {
                    if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
                        filteredTasksCalendar.get(day).add(ts);
                    }
                }
            }
        } else {
            filteredTasksCalendar = currentDayTasks;
            view.getTaskListMultiDayBox().setIdTaskSelected(idTaskSelected);
        }
        for (Day day : filteredTasksCalendar.keySet()) {
            view.getTaskListMultiDayBox().addTasksByDay(day, new ArrayList<TaskSummary>(filteredTasksCalendar.get(day)));
        }
        view.getTaskListMultiDayBox().refresh();

    }

    private void refreshEditPanel() {
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
        if (this.getCurrentView() == TaskView.GRID) {
            if (view.getTaskListGrid().getRowCount() == 0 && status == PlaceStatus.OPEN) {
                closeEditPanel();
            }
        } else {
            boolean close = true;
            if (currentDayTasks != null) {
                for (Map.Entry<Day, List<TaskSummary>> entry : currentDayTasks.entrySet()) {
                    if (entry.getValue() != null && entry.getValue().size() > 0) {
                        close = false;
                        break;
                    }
                }
            }
            if (close && status == PlaceStatus.OPEN) {
                closeEditPanel();
            }
        }

    }

    public void startTasks(final List<Long> selectedTasks, final String userId) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Started");
                view.refreshTasks();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).startBatch(selectedTasks, userId);
    }

    public void releaseTasks(final List<Long> selectedTasks, final String userId) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Released");
                String idRowSelected = DataGridUtils.getIdRowSelected(view.getTaskListGrid());
                Long idSelected = 0L;
                if(idRowSelected != null){
                  idSelected = Long.valueOf(idRowSelected);
                }
                DataGridUtils.currentIdSelected = idSelected;
                view.refreshTasks();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).releaseBatch(selectedTasks, userId);
    }

    public void completeTasks(final List<Long> selectedTasks, final String userId) {
        taskServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task(s) Completed");
                view.refreshTasks();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).completeBatch(selectedTasks, userId, null);
    }

    public void claimTasks(final List<Long> selectedTasks, final String userId) {
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Task(s) Claimed");
                String idRowSelected = DataGridUtils.getIdRowSelected(view.getTaskListGrid());
                Long idSelected = 0L;
                if(idRowSelected != null){
                  idSelected = Long.valueOf(idRowSelected);
                }
                DataGridUtils.currentIdSelected = idSelected;
                view.refreshTasks();

            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).claimBatch(selectedTasks, userId);
    }

    /**
     * Refresh tasks based on specified date, view (day/week/month) and task
     * type.
     */
    public void refreshTasks(Date date, TaskView taskView, TaskType taskType,  int offset, int count, boolean clear) {
        switch (taskType) {
        case PERSONAL:
            refreshPersonalTasks(date, taskView, offset, count, clear);
            break;
        default:
            refreshTasksByType(date, taskView, taskType, offset, count, clear);
            break;
        }
    }

    private DateRange determineDateRangeForTaskViewBasedOnSpecifiedDate(Date date, TaskView taskView) {
        DateRange dateRange;
        switch (taskView) {
        case DAY:
            dateRange = new DateRange(new Date(date.getTime()), new Date(date.getTime()), 0);
            break;
        case WEEK:
            dateRange = DateUtils.getWeekDateRange(date);
            break;
        case MONTH:
            DateRange monthRange = DateUtils.getMonthDateRange(date);
            DateRange firstWeekRange = DateUtils.getWeekDateRange(monthRange.getStartDate());
            DateRange lastWeekRange = DateUtils.getWeekDateRange(monthRange.getEndDate());
            int daysBetween = CalendarUtil.getDaysBetween(firstWeekRange.getStartDate(), lastWeekRange.getEndDate());
            dateRange = new DateRange(firstWeekRange.getStartDate(), lastWeekRange.getEndDate(), daysBetween);
            break;
        case GRID:
            dateRange = new DateRange(new Date(date.getTime()), new Date(date.getTime()), 0);
            break;
        default:
            throw new IllegalStateException("Unrecognized view type '" + taskView + "'!");
        }
        return dateRange;
    }

    public void refreshTasksByType(Date date, TaskView taskView, TaskType type,  int offset, int count, boolean clear) {
        if (taskView.equals(TaskView.GRID)) {
            this.refreshGrid(type,  offset,  count,  clear);
        } else {
            this.refreshCalendar(type, date, taskView);
        }

    }

    private void refreshGrid(TaskType type, int offset, int count, final boolean clear){
        final long currentTime = System.currentTimeMillis();
        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
            @Override
            public void callback(List<TaskSummary> tasks) {
                view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
                allTaskSummaries = tasks;
                filterTasks(view.getCurrentFilter(), clear);
                view.getTaskListGrid().setFocus(true);
                
            }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error( Message message, Throwable throwable ) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
          }
      }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), TaskUtils.getStatusByType(type),
                null, offset, count);
        
    }

    private void refreshCalendar(TaskType type, Date date, TaskView taskView){
        DateRange dateRangeToShow = determineDateRangeForTaskViewBasedOnSpecifiedDate(date, taskView);
        Date fromDate = dateRangeToShow.getStartDate();
        int daysTotal = dateRangeToShow.getDaysInBetween() + 1;
        final long currentTime = System.currentTimeMillis();
        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
            @Override
            public void callback(Map<Day, List<TaskSummary>> tasks) {
                view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
                currentDayTasks = tasks;
                filterTasks(view.getCurrentFilter(), false);
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).getTasksAssignedAsPotentialOwnerFromDateToDateByDays(identity.getName(), TaskUtils.getStatusByType(type),
                fromDate, daysTotal);
    }

    public void refreshPersonalTasks(Date date, TaskView taskView, int offset, int count, final boolean clear) {
        if (taskView.equals(TaskView.GRID)) {
            final long currentTime = System.currentTimeMillis();
            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
                @Override
                public void callback(List<TaskSummary> tasks) {
                    view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
                    allTaskSummaries = tasks;
                    filterTasks(view.getCurrentFilter(), clear);

                }
            }, new ErrorCallback<Message>() {
                  @Override
                  public boolean error( Message message, Throwable throwable ) {
                      ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                      return true;
                  }
              }).getTasksOwnedByExpirationDateOptional(identity.getName(), TaskUtils.getStatusByType(TaskType.PERSONAL), null, offset, count);

        } else {
            DateRange dateRangeToShow = determineDateRangeForTaskViewBasedOnSpecifiedDate(date, taskView);
            Date fromDate = dateRangeToShow.getStartDate();
            int daysTotal = dateRangeToShow.getDaysInBetween() + 1;
            final long currentTime = System.currentTimeMillis();
            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
                @Override
                public void callback(Map<Day, List<TaskSummary>> tasks) {
                    view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
                    currentDayTasks = tasks;
                    filterTasks(view.getCurrentFilter(), true);
                    view.getTaskListGrid().setFocus(true);
                }
            }, new ErrorCallback<Message>() {
                  @Override
                  public boolean error( Message message, Throwable throwable ) {
                      ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                      return true;
                  }
              }).getTasksOwnedFromDateToDateByDays(identity.getName(), TaskUtils.getStatusByType(TaskType.PERSONAL), fromDate,
                    daysTotal);
        }
    }

    public void addDataDisplay(HasData<TaskSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<TaskSummary> getDataProvider() {
        return dataProvider;
    }

    private void makeMenuBar() {
        menus = MenuFactory.newTopLevelMenu(constants.New_Task()).respondsWith(new Command() {
            @Override
            public void execute() {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
                placeManager.goTo(placeRequestImpl);
            }
        }).endMenu().newTopLevelMenu(constants.Refresh()).respondsWith(new Command() {
            @Override
            public void execute() {
                refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 0, DataGridUtils.pageSize, true);
                view.setCurrentFilter("");
                view.displayNotification(constants.Tasks_Refreshed());
                clearSearchEvent.fire(new ClearSearchEvent());
            }
        }).endMenu().build();
    }

    private void refreshView(TaskView currentTaskView) {
        switch (currentTaskView) {
        case GRID:
            view.setGridView();
            break;
        default:
            view.setCalendarView(currentTaskView);
            break;
        }
    }

    public Date getCurrentDate() {
        return view.getCurrentDate();
    }

    public TaskView getCurrentView() {
        return view.getCurrentView();
    }

    public TaskType getCurrentTaskType() {
        return view.getCurrentTaskType();
    }

    public String getCurrentFilter() {
        return view.getCurrentFilter();
    }

    public void closeEditPanel() {
        placeManager.closePlace(new DefaultPlaceRequest("Task Details Multi"));
    }

    public void changeBgTaskCalendar(@Observes TaskCalendarEvent taskCalendarEvent) {
        if (currentDayTasks != null) {
            DataGridUtils.idTaskCalendar = taskCalendarEvent.getTaskEventId();
            this.filterCalendar(taskCalendarEvent.getTaskEventId(), view.getCurrentFilter());
        }
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if (closed.getPlace().getIdentifier().equals("Form Display")
                || closed.getPlace().getIdentifier().equals("Quick New Task")) {
            view.refreshTasks();
        }
    }

    public void onSearchEvent(@Observes final SearchEvent searchEvent) {
        view.setCurrentFilter(searchEvent.getFilter());
        refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 
                view.getPager().getCurrentPage() * DataGridUtils.pageSize, (DataGridUtils.pageSize * DataGridUtils.clientSidePages), true);
    }

    public void editPanelEvent( @Observes EditPanelEvent editPanelEvent ){
        view.displayNotification( "The process was finished. Last task completed: " + editPanelEvent.getTaskId() );
        closeEditPanel();
    }


}