/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.backend.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.TasksPerDaySummary;
import org.jbpm.console.ng.ht.service.TaskCalendarService;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.InternalTaskService;
import org.uberfire.paging.PageResponse;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskCalendarServiceImpl implements TaskCalendarService {

  @Inject
  private InternalTaskService taskService;

  public TaskCalendarServiceImpl() {
  }

  /*
   * The getData method requires the following paremeters
   *  userId -> The user that is requesting the tasks
   *  dateFrom -> The initial date to look for the tasks due date
   *  dateTo -> The end date to look for the tasks due date 
   *  ownedFilter -> if it is true we will look only for currently owned tasks, if not 
                     all the AssignedAsPotentialOwner tasks will be queried
   *  statuses -> a list of string representing the status of the tasks that we want to filter 
  */
  @Override
  public PageResponse<TasksPerDaySummary> getData(QueryFilter filter) {
    PageResponse<TasksPerDaySummary> response = new PageResponse<TasksPerDaySummary>();
    List<String> statusesString = null;
    String userId = "";
    Date dateFrom = null;
    Date dateTo = null;
    boolean ownedFilter = false;
    if (filter.getParams() != null) {
      userId = (String) filter.getParams().get("userId");
      statusesString = (List<String>) filter.getParams().get("statuses");
      dateFrom = (Date) filter.getParams().get("dateFrom");
      dateTo = (Date) filter.getParams().get("dateTo");
      ownedFilter = (Boolean) filter.getParams().get("ownedFilter");
    }
    List<Status> statuses = new ArrayList<Status>();
    for (String s : statusesString) {
      statuses.add(Status.valueOf(s));
    }

    org.kie.internal.task.api.QueryFilter qf = new QueryFilterImpl(filter.getOffset(), filter.getCount() + 1);
    List<TaskSummary> taskSummaries = null;
    if(!ownedFilter){        
      taskSummaries = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, null, statuses, qf));
    }else{
      taskSummaries = TaskSummaryHelper.adaptCollection(taskService.getTasksOwned(userId, statuses, qf));
    }

    LocalDate dayFrom = new LocalDate(dateFrom);
    LocalDate dayTo = new LocalDate(dateTo);

    LocalDate today = new LocalDate();

    int nrOfDaysTotal = getNumberOfDaysWithinDateRange(dayFrom, dayTo);

    List<TasksPerDaySummary> tasksPerDay = createTasksPerDayHolder(dayFrom, nrOfDaysTotal);

    fillTasksPerDay(tasksPerDay, taskSummaries, today);

    response.setStartRowIndex(filter.getOffset());
    response.setTotalRowSize(taskSummaries.size() - 1);
    if (taskSummaries.size() > filter.getCount()) {
      response.setTotalRowSizeExact(false);
    } else {
      response.setTotalRowSizeExact(true);
    }

    if (!taskSummaries.isEmpty() && taskSummaries.size() > (filter.getCount() + filter.getOffset())) {
      response.setPageRowList(tasksPerDay);
      response.setLastPage(false);

    } else {
      response.setPageRowList(tasksPerDay);
      response.setLastPage(true);

    }
    return response;
  }

  private int getNumberOfDaysWithinDateRange(LocalDate dayFrom, LocalDate dayTo) {
    Days daysBetween = Days.daysBetween(dayFrom, dayTo);
    return daysBetween.getDays() + 1;
  }

  private List<TasksPerDaySummary> createTasksPerDayHolder(LocalDate dayFrom,
          int nrOfDaysTotal) {
    List<TasksPerDaySummary> tasksPerDay = new ArrayList<TasksPerDaySummary>();
    for (int i = 0; i < nrOfDaysTotal; i++) {
      tasksPerDay.add(new TasksPerDaySummary(transformLocalDateToDay(dayFrom.plusDays(i)), new ArrayList<TaskSummary>()));
    }
    return tasksPerDay;
  }

  private void fillTasksPerDay(List<TasksPerDaySummary> tasksPerDay,
          List<TaskSummary> taskSummaries, LocalDate today) {
    for (TaskSummary taskSummary : taskSummaries) {
      LocalDate expDate;
      if (taskSummary.getExpirationTime() == null) {
        expDate = today;
      } else {
        expDate = new LocalDate(taskSummary.getExpirationTime());
      }
      for (TasksPerDaySummary tpds : tasksPerDay) {
        if (tpds.getDay().equals(transformLocalDateToDay(expDate))) {
          tpds.getTasks().add(taskSummary);
        }
      }
    }
  }

  private Day transformLocalDateToDay(LocalDate localDate) {
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE dd");
    Date dayDate = localDate.toDateTimeAtStartOfDay().toDate();
    return new Day(dayDate, dayFormat.format(dayDate));
  }
  
  
  

}
