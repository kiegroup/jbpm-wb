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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskService;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.InternalTaskService;
import org.uberfire.paging.PageResponse;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskServiceImpl implements TaskService {

  @Inject
  private InternalTaskService taskService;


  public TaskServiceImpl() {
  }
 

  @Override
  public PageResponse<TaskSummary> getData(QueryFilter filter) {
    PageResponse<TaskSummary> response = new PageResponse<TaskSummary>();
    List<String> statusesString = null;
    String userId = "";
    if (filter.getParams() != null) {
      userId = (String) filter.getParams().get("userId");
      statusesString = (List<String>) filter.getParams().get("statuses");

    }
    List<Status> statuses = new ArrayList<Status>();
    for (String s : statusesString) {
      statuses.add(Status.valueOf(s));
    }
    
    org.kie.internal.task.api.QueryFilter qf = new QueryFilterImpl(filter.getOffset(), filter.getCount() + 1);
    List<TaskSummary> taskSummaries = TaskSummaryHelper.adaptCollection(
            taskService.getTasksAssignedAsPotentialOwner(userId, null, statuses, qf));

    response.setStartRowIndex(filter.getOffset());
    response.setTotalRowSize(taskSummaries.size() - 1);
    if(taskSummaries.size() > filter.getCount()){
      response.setTotalRowSizeExact(false);
    }else{
      response.setTotalRowSizeExact(true);
    }

    if (!taskSummaries.isEmpty() && taskSummaries.size() > (filter.getCount() + filter.getOffset())) {
      response.setPageRowList(new ArrayList<TaskSummary>(taskSummaries.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
      response.setLastPage(false);

    } else {
      response.setPageRowList(new ArrayList<TaskSummary>(taskSummaries));
      response.setLastPage(true);

    }
    return response;
  }

  @Override
  public void start(long taskId, String user) {
    taskService.start(taskId, user);
  }

  @Override
  public void complete(long taskId, String user, Map<String, Object> params) {
    taskService.complete(taskId, user, params);
  }

  @Override
  public void claim(long taskId, String user) {
    taskService.claim(taskId, user);
  }

  @Override
  public void release(long taskId, String user) {
    taskService.release(taskId, user);
  }

  @Override
  public void forward(long taskId, String userId, String targetEntityId) {
    taskService.forward(taskId, userId, targetEntityId);
  }

  @Override
  public void delegate(long taskId, String userId, String targetEntityId) {
    taskService.delegate(taskId, userId, targetEntityId);
  }

}
