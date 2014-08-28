///*
// * Copyright 2012 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.console.ng.ht.client.editors.taskslist.calendar;
//
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.user.cellview.client.ColumnSortList;
//import com.google.gwt.user.datepicker.client.CalendarUtil;
//import com.google.gwt.view.client.AsyncDataProvider;
//import com.google.gwt.view.client.HasData;
//import com.google.gwt.view.client.Range;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import javax.enterprise.context.Dependent;
//import javax.inject.Inject;
//import org.jboss.errai.bus.client.api.messaging.Message;
//import org.jboss.errai.common.client.api.Caller;
//import org.jboss.errai.common.client.api.ErrorCallback;
//import org.jboss.errai.common.client.api.RemoteCallback;
//import org.jbpm.console.ng.ga.model.PortableQueryFilter;
//import org.jbpm.console.ng.gc.client.list.base.AbstractListPresenter;
//import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
//import org.jbpm.console.ng.gc.client.util.DateRange;
//import org.jbpm.console.ng.gc.client.util.DateUtils;
//import org.jbpm.console.ng.gc.client.util.TaskUtils;
//import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskType;
//import org.jbpm.console.ng.gc.client.util.TaskUtils.TaskView;
//import org.jbpm.console.ng.ht.client.i18n.Constants;
//import org.jbpm.console.ng.ht.model.TasksPerDaySummary;
//import org.jbpm.console.ng.ht.service.TaskCalendarService;
//import org.jbpm.console.ng.ht.service.TaskOperationsService;
//import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
//import org.uberfire.client.annotations.WorkbenchPartTitle;
//import org.uberfire.client.annotations.WorkbenchPartView;
//import org.uberfire.client.annotations.WorkbenchScreen;
//import org.uberfire.client.mvp.UberView;
//import org.uberfire.paging.PageResponse;
//
//@Dependent
//@WorkbenchScreen(identifier = "Calendar Tasks List")
//public class TasksListCalendarPresenter extends AbstractListPresenter<TasksPerDaySummary> {
//
//  public interface TaskListCalendarView extends ListView<TasksPerDaySummary, TasksListCalendarPresenter> {
//
//  }
//
//  @Inject
//  private TaskListCalendarView view;
//
//  private Constants constants = GWT.create(Constants.class);
//
//  @Inject
//  private Caller<TaskCalendarService> taskCalendarService;
//
//  @Inject
//  private Caller<TaskOperationsService> taskOperationsService;
//
//  private Map<String, Object> filterParams = new HashMap<String, Object>();
//
//  private TaskType currentStatusFilter = TaskUtils.TaskType.ACTIVE;
//
//  public TasksListCalendarPresenter() {
//    dataProvider = new AsyncDataProvider<TasksPerDaySummary>() {
//
//      @Override
//      protected void onRangeChanged(HasData<TasksPerDaySummary> display) {
//
//        final Range visibleRange = display.getVisibleRange();
//        ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
//        if (currentFilter == null) {
//          currentFilter = new PortableQueryFilter(visibleRange.getStart(),
//                  visibleRange.getLength(),
//                  false, "",
//                  (columnSortList.size() > 0) ? columnSortList.get(0)
//                  .getColumn().getDataStoreName() : "",
//                  (columnSortList.size() > 0) ? columnSortList.get(0)
//                  .isAscending() : true);
//
//        }
//        // If we are refreshing after a search action, we need to go back to offset 0
//        if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
//                || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
//          currentFilter.setOffset(visibleRange.getStart());
//          currentFilter.setCount(visibleRange.getLength());
//        } else {
//          currentFilter.setOffset(0);
//          currentFilter.setCount(view.getListGrid().getPageSize());
//        }
//        filterParams.put("statuses", TaskUtils.getStatusByType(currentStatusFilter));
//        filterParams.put("userId", identity.getName());
//
//        currentFilter.setParams(filterParams);
//        currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
//                .getColumn().getDataStoreName() : "");
//        currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
//                .isAscending() : true);
//
//        taskCalendarService.call(new RemoteCallback<PageResponse<TasksPerDaySummary>>() {
//          @Override
//          public void callback(PageResponse<TasksPerDaySummary> response) {
//            dataProvider.updateRowCount(response.getTotalRowSize(),
//                    response.isTotalRowSizeExact());
//            dataProvider.updateRowData(response.getStartRowIndex(),
//                    response.getPageRowList());
//          }
//        }, new ErrorCallback<Message>() {
//          @Override
//          public boolean error(Message message, Throwable throwable) {
//            view.hideBusyIndicator();
//            view.displayNotification("Error: Getting Tasks: " + throwable.toString());
//            GWT.log(message.toString());
//            return true;
//          }
//        }).getData(currentFilter);
//
//      }
//    };
//  }
//  
//  public void refreshActiveTasks() {
//    currentStatusFilter = TaskUtils.TaskType.ACTIVE;
//    refreshGrid();
//  }
//
//  public void refreshPersonalTasks() {
//    currentStatusFilter = TaskUtils.TaskType.PERSONAL;
//    refreshGrid();
//  }
//
//  public void refreshGroupTasks() {
//    currentStatusFilter = TaskUtils.TaskType.GROUP;
//    refreshGrid();
//  }
//
//  public void refreshAllTasks() {
//    currentStatusFilter = TaskUtils.TaskType.ALL;
//    refreshGrid();
//  }
//
//  @WorkbenchPartTitle
//  public String getTitle() {
//    return constants.Tasks_List();
//  }
//
//  @WorkbenchPartView
//  public UberView<TasksListCalendarPresenter> getView() {
//    return view;
//  }
//
//  public void releaseTask(final Long taskId, final String userId) {
//    taskOperationsService.call(new RemoteCallback<Void>() {
//      @Override
//      public void callback(Void nothing) {
//        view.displayNotification("Task Released");
//        refreshGrid();
//      }
//    }, new ErrorCallback<Message>() {
//      @Override
//      public boolean error(Message message, Throwable throwable) {
//        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//        return true;
//      }
//    }).release(taskId, userId);
//  }
//
//  public void claimTask(final Long taskId, final String userId) {
//    taskOperationsService.call(new RemoteCallback<Void>() {
//      @Override
//      public void callback(Void nothing) {
//        view.displayNotification("Task Claimed");
//        refreshGrid();
//      }
//    }, new ErrorCallback<Message>() {
//      @Override
//      public boolean error(Message message, Throwable throwable) {
//        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
//        return true;
//      }
//    }).claim(taskId, userId);
//  }
//
// 
//
//}
