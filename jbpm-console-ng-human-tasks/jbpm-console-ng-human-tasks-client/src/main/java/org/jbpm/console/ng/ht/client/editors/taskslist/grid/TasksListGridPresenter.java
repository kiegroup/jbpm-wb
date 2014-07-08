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

package org.jbpm.console.ng.ht.client.editors.taskslist.grid;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.util.TaskUtils;
import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskType;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskService;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;
import org.uberfire.paging.PageResponse;

@Dependent
@WorkbenchScreen(identifier = "Grid Tasks List")
public class TasksListGridPresenter extends AbstractListPresenter<TaskSummary>{

  
  
  public interface TaskListView extends ListView<TaskSummary, TasksListGridPresenter> {

  }
  
  @Inject
  private TaskListView view;
  
  private Constants constants = GWT.create(Constants.class);

  @Inject
  private Caller<TaskService> taskService;  
  
  private Map<String, Object> filterParams = new HashMap<String, Object>();
  
  private TaskType currentStatusFilter = TaskUtils.TaskType.ACTIVE;

  public TasksListGridPresenter() {
    dataProvider = new AsyncDataProvider<TaskSummary>() {

      @Override
      protected void onRangeChanged(HasData<TaskSummary> display) {

        final Range visibleRange = display.getVisibleRange();
        ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
        if (currentFilter == null) {
          currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                  visibleRange.getLength(),
                  false, "",
                  (columnSortList.size() > 0) ? columnSortList.get(0)
                  .getColumn().getDataStoreName() : "",
                  (columnSortList.size() > 0) ? columnSortList.get(0)
                  .isAscending() : true);
          
          
        }
        // If we are refreshing after a search action, we need to go back to offset 0
        if(currentFilter.getParams() == null || currentFilter.getParams().isEmpty() 
                || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")){
          currentFilter.setOffset(visibleRange.getStart());
          currentFilter.setCount(visibleRange.getLength());
        }else{
          currentFilter.setOffset(0);
          currentFilter.setCount(view.getListGrid().getPageSize());
        }
        filterParams.put("statuses", TaskUtils.getStatusByType(currentStatusFilter) );
        filterParams.put("userId", identity.getName());
        
        currentFilter.setParams(filterParams);
        currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
                .getColumn().getDataStoreName() : "");
        currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
                .isAscending() : true);
        
        taskService.call(new RemoteCallback<PageResponse<TaskSummary>>() {
          @Override
          public void callback(PageResponse<TaskSummary> response) {
            dataProvider.updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
            dataProvider.updateRowData( response.getStartRowIndex(),
                                       response.getPageRowList() );
          }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error(Message message, Throwable throwable) {
            view.hideBusyIndicator();
            view.displayNotification("Error: Getting Tasks: " + throwable.toString());
            GWT.log(message.toString());
            return true;
          }
        }).getData(currentFilter); 

      }
    };
  }

    
  public void refreshActiveTasks() {
    currentStatusFilter = TaskUtils.TaskType.ACTIVE;
    refreshGrid();
  }

  public void refreshPersonalTasks() {
    currentStatusFilter = TaskUtils.TaskType.PERSONAL;
    refreshGrid();
  }

  public void refreshGroupTasks() {
    currentStatusFilter = TaskUtils.TaskType.GROUP;
    refreshGrid();
  }

  public void refreshAllTasks() {
    currentStatusFilter = TaskUtils.TaskType.ALL;
    refreshGrid();
  }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }

    @WorkbenchPartView
    public UberView<TasksListGridPresenter> getView() {
        return view;
    }


    

   

  
//    public void filterTasks(String text, boolean clear) {
//        ColumnSortList.ColumnSortInfo sortInfo = view.getTaskListGrid().getColumnSortList().size() > 0 ? view.getTaskListGrid()
//                .getColumnSortList().get(0) : null;
//        if (allTaskSummaries != null) {
//            this.filterGrid(sortInfo, text, clear);
//        }
//        if (currentDayTasks != null) {
//            this.filterCalendar(DataGridUtils.idTaskCalendar, text);
//        }
//        refreshEditPanel();
//        view.getTaskListGrid().setFocus(true);
//    }
//
//    private void filterGrid(ColumnSortList.ColumnSortInfo sortInfo, String text, boolean clear) {
//        List<TaskSummary> filteredTasksSimple = Lists.newArrayList();
//        if (!text.equals("")) {
//            for (TaskSummary ts : allTaskSummaries) {
//                if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
//                    filteredTasksSimple.add(ts);
//                }
//            }
//        } else {
//            filteredTasksSimple = allTaskSummaries;
//        }
//        if(clear){
//            dataProvider.getList().clear();
//        }
//        dataProvider.getList().addAll(filteredTasksSimple);
//        if (sortInfo != null && sortInfo.isAscending()) {
//            view.getTaskListGrid().getColumnSortList().clear();
//            ColumnSortInfo columnSortInfo = new ColumnSortInfo(sortInfo.getColumn(), sortInfo.isAscending());
//            view.getTaskListGrid().getColumnSortList().push(columnSortInfo);
//            ColumnSortEvent.fire(view.getTaskListGrid(), view.getTaskListGrid().getColumnSortList());
//        }
//    }
//
//    private void filterCalendar(Long idTaskSelected, String text) {
//        Map<Day, List<TaskSummary>> tasksCalendar = new LinkedHashMap<Day, List<TaskSummary>>(currentDayTasks);
//        Map<Day, List<TaskSummary>> filteredTasksCalendar = new LinkedHashMap<Day, List<TaskSummary>>();
//        view.getTaskListMultiDayBox().clear();
//        if (!text.equals("")) {
//            for (Day day : tasksCalendar.keySet()) {
//                if (filteredTasksCalendar.get(day) == null) {
//                    filteredTasksCalendar.put(day, new ArrayList<TaskSummary>());
//                }
//                for (TaskSummary ts : tasksCalendar.get(day)) {
//                    if (ts.getName().toLowerCase().contains(text.toLowerCase())) {
//                        filteredTasksCalendar.get(day).add(ts);
//                    }
//                }
//            }
//        } else {
//            filteredTasksCalendar = currentDayTasks;
//            view.getTaskListMultiDayBox().setIdTaskSelected(idTaskSelected);
//        }
//        for (Day day : filteredTasksCalendar.keySet()) {
//            view.getTaskListMultiDayBox().addTasksByDay(day, new ArrayList<TaskSummary>(filteredTasksCalendar.get(day)));
//        }
//        view.getTaskListMultiDayBox().refresh();
//
//    }
//
//    private void refreshEditPanel() {
//        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Task Details Multi"));
//        if (this.getCurrentView() == TaskView.GRID) {
//            if (view.getTaskListGrid().getRowCount() == 0 && status == PlaceStatus.OPEN) {
//                closeEditPanel();
//            }
//        } else {
//            boolean close = true;
//            if (currentDayTasks != null) {
//                for (Map.Entry<Day, List<TaskSummary>> entry : currentDayTasks.entrySet()) {
//                    if (entry.getValue() != null && entry.getValue().size() > 0) {
//                        close = false;
//                        break;
//                    }
//                }
//            }
//            if (close && status == PlaceStatus.OPEN) {
//                closeEditPanel();
//            }
//        }
//
//    }

    public void startTask(final Long taskId, final String userId) {
        taskService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Started");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).start(taskId, userId);
    }

    public void releaseTask(final Long taskId, final String userId) {
        taskService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Released");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).release(taskId, userId);
    }

    

    public void claimTask(final Long taskId, final String userId) {
        taskService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Task Claimed");
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
              @Override
              public boolean error( Message message, Throwable throwable ) {
                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                  return true;
              }
          }).claim(taskId, userId);
    }

//    /**
//     * Refresh tasks based on specified date, view (day/week/month) and task
//     * type.
//     */
//    public void refreshTasks(Date date, TaskView taskView, TaskType taskType,  int offset, int count, boolean clear) {
//        switch (taskType) {
//        case PERSONAL:
//            refreshPersonalTasks(date, taskView, offset, count, clear);
//            break;
//        default:
//            refreshTasksByType(date, taskView, taskType, offset, count, clear);
//            break;
//        }
//    }
//
//    private DateRange determineDateRangeForTaskViewBasedOnSpecifiedDate(Date date, TaskView taskView) {
//        DateRange dateRange;
//        switch (taskView) {
//        case DAY:
//            dateRange = new DateRange(new Date(date.getTime()), new Date(date.getTime()), 0);
//            break;
//        case WEEK:
//            dateRange = DateUtils.getWeekDateRange(date);
//            break;
//        case MONTH:
//            DateRange monthRange = DateUtils.getMonthDateRange(date);
//            DateRange firstWeekRange = DateUtils.getWeekDateRange(monthRange.getStartDate());
//            DateRange lastWeekRange = DateUtils.getWeekDateRange(monthRange.getEndDate());
//            int daysBetween = CalendarUtil.getDaysBetween(firstWeekRange.getStartDate(), lastWeekRange.getEndDate());
//            dateRange = new DateRange(firstWeekRange.getStartDate(), lastWeekRange.getEndDate(), daysBetween);
//            break;
//        case GRID:
//            dateRange = new DateRange(new Date(date.getTime()), new Date(date.getTime()), 0);
//            break;
//        default:
//            throw new IllegalStateException("Unrecognized view type '" + taskView + "'!");
//        }
//        return dateRange;
//    }
//
//    public void refreshTasksByType(Date date, TaskView taskView, TaskType type,  int offset, int count, boolean clear) {
//        if (taskView.equals(TaskView.GRID)) {
//            this.refreshGrid(type,  offset,  count,  clear);
//        } else {
//            this.refreshCalendar(type, date, taskView);
//        }
//
//    }
//
//    private void refreshGrid(TaskType type, int offset, int count, final boolean clear){
//        final long currentTime = System.currentTimeMillis();
//        taskServices.call(new RemoteCallback<List<TaskSummary>>() {
//            @Override
//            public void callback(List<TaskSummary> tasks) {
//                view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
//                allTaskSummaries = tasks;
//                filterTasks(view.getCurrentFilter(), clear);
//                view.getTaskListGrid().setFocus(true);
//                
//            }
//        }, new ErrorCallback<Message>() {
//          @Override
//          public boolean error( Message message, Throwable throwable ) {
//              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//              return true;
//          }
//      }).getTasksAssignedAsPotentialOwnerByExpirationDateOptional(identity.getName(), TaskUtils.getStatusByType(type),
//                null, offset, count);
//        
//    }
//
//    private void refreshCalendar(TaskType type, Date date, TaskView taskView){
//        DateRange dateRangeToShow = determineDateRangeForTaskViewBasedOnSpecifiedDate(date, taskView);
//        Date fromDate = dateRangeToShow.getStartDate();
//        int daysTotal = dateRangeToShow.getDaysInBetween() + 1;
//        final long currentTime = System.currentTimeMillis();
//        taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
//            @Override
//            public void callback(Map<Day, List<TaskSummary>> tasks) {
//                view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
//                currentDayTasks = tasks;
//                filterTasks(view.getCurrentFilter(), false);
//            }
//        }, new ErrorCallback<Message>() {
//              @Override
//              public boolean error( Message message, Throwable throwable ) {
//                  ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//                  return true;
//              }
//          }).getTasksAssignedAsPotentialOwnerFromDateToDateByDays(identity.getName(), TaskUtils.getStatusByType(type),
//                fromDate, daysTotal);
//    }
//
//    public void refreshPersonalTasks(Date date, TaskView taskView, int offset, int count, final boolean clear) {
//        if (taskView.equals(TaskView.GRID)) {
//            final long currentTime = System.currentTimeMillis();
//            taskServices.call(new RemoteCallback<List<TaskSummary>>() {
//                @Override
//                public void callback(List<TaskSummary> tasks) {
//                    view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
//                    allTaskSummaries = tasks;
//                    filterTasks(view.getCurrentFilter(), clear);
//
//                }
//            }, new ErrorCallback<Message>() {
//                  @Override
//                  public boolean error( Message message, Throwable throwable ) {
//                      ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//                      return true;
//                  }
//              }).getTasksOwnedByExpirationDateOptional(identity.getName(), TaskUtils.getStatusByType(TaskType.PERSONAL), null, offset, count);
//
//        } else {
//            DateRange dateRangeToShow = determineDateRangeForTaskViewBasedOnSpecifiedDate(date, taskView);
//            Date fromDate = dateRangeToShow.getStartDate();
//            int daysTotal = dateRangeToShow.getDaysInBetween() + 1;
//            final long currentTime = System.currentTimeMillis();
//            taskServices.call(new RemoteCallback<Map<Day, List<TaskSummary>>>() {
//                @Override
//                public void callback(Map<Day, List<TaskSummary>> tasks) {
//                    view.displayNotification("Tasks ("+tasks.size()+") retrieved in: "+((double)(System.currentTimeMillis()-currentTime)/1000)+"s");
//                    currentDayTasks = tasks;
//                    filterTasks(view.getCurrentFilter(), true);
//                    view.getTaskListGrid().setFocus(true);
//                }
//            }, new ErrorCallback<Message>() {
//                  @Override
//                  public boolean error( Message message, Throwable throwable ) {
//                      ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//                      return true;
//                  }
//              }).getTasksOwnedFromDateToDateByDays(identity.getName(), TaskUtils.getStatusByType(TaskType.PERSONAL), fromDate,
//                    daysTotal);
//        }
//    }

    

//    private void makeMenuBar() {
//        menus = MenuFactory.newTopLevelMenu(constants.New_Task()).respondsWith(new Command() {
//            @Override
//            public void execute() {
//                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
//                placeManager.goTo(placeRequestImpl);
//            }
//        }).endMenu().newTopLevelMenu(constants.Refresh()).respondsWith(new Command() {
//            @Override
//            public void execute() {
//                refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 0, DataGridUtils.pageSize, true);
//                view.setCurrentFilter("");
//                view.displayNotification(constants.Tasks_Refreshed());
//                clearSearchEvent.fire(new ClearSearchEvent());
//            }
//        }).endMenu().build();
//    }

   

   

   

//    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
//        if (closed.getPlace().getIdentifier().equals("Form Display")
//                || closed.getPlace().getIdentifier().equals("Quick New Task")) {
//            view.refreshTasks();
//        }
//    }
//
//    public void onSearchEvent(@Observes final SearchEvent searchEvent) {
//        view.setCurrentFilter(searchEvent.getFilter());
//        refreshTasks(view.getCurrentDate(), view.getCurrentView(), view.getCurrentTaskType(), 
//                view.getPager().getCurrentPage() * DataGridUtils.pageSize, (DataGridUtils.pageSize * DataGridUtils.clientSidePages), true);
//    }

//    public void editPanelEvent( @Observes EditPanelEvent editPanelEvent ){
//        view.displayNotification( "The process was finished. Last task completed: " + editPanelEvent.getTaskId() );
//        closeEditPanel();
//    }


}