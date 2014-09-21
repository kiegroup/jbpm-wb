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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.uberfire.paging.PageResponse;
import static org.jbpm.console.ng.ht.util.TaskRoleDefinition.*;
/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskQueryServiceImpl implements TaskQueryService {

  @Inject
  private InternalTaskService taskService;

  @Inject
  private RuntimeDataService runtimeDataService;


  public TaskQueryServiceImpl() {
  }
 

  @Override
  public PageResponse<TaskSummary> getData(QueryFilter filter) {
    PageResponse<TaskSummary> response = new PageResponse<TaskSummary>();
    List<String> statusesString = null;
    String userId = "";
    String taskRole="";
    if (filter.getParams() != null) {
      userId = (String) filter.getParams().get("userId");
      statusesString = (List<String>) filter.getParams().get("statuses");
      taskRole=(String) filter.getParams().get("taskRole");

    }
    List<Status> statuses = new ArrayList<Status>();
    for (String s : statusesString) {
      statuses.add(Status.valueOf(s));
    }
    
    org.kie.internal.query.QueryFilter qf = new QueryFilterImpl(filter.getOffset(), filter.getCount() + 1,
                                                                    filter.getOrderBy(), filter.isAscending());
    List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>();
    if (TASK_ROLE_ADMINISTRATOR.equals(taskRole)){
        taskSummaries = TaskSummaryHelper.adaptCollection(runtimeDataService.getTasksAssignedAsBusinessAdministrator(userId,qf),true);
    }else{
        taskSummaries = TaskSummaryHelper.adaptCollection(runtimeDataService.getTasksAssignedAsPotentialOwner(userId, null, statuses, qf));
    }
    
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
  public TaskSummary getItem(TaskKey key) {
    Task task = taskService.getTaskById(key.getTaskId());
        if (task != null) {
            List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
            List<String> potOwnersString = null;
            if (potentialOwners != null) {
                potOwnersString = new ArrayList<String>(potentialOwners.size());
                for (OrganizationalEntity e : potentialOwners) {
                    potOwnersString.add(e.getId());
                }
            } 
            return new TaskSummary(task.getId(), task.getName(),
                    task.getDescription(), task.getTaskData().getStatus().name(), task.getPriority(), (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                    .getId() : "", (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId()
                    : "", task.getTaskData().getCreatedOn(), task.getTaskData().getActivationTime(), task.getTaskData()
                    .getExpirationTime(), task.getTaskData().getProcessId(), task.getTaskData().getProcessSessionId(),
                    task.getTaskData().getProcessInstanceId(), task.getTaskData().getDeploymentId()
                    , (int) task.getTaskData().getParentId());
        }
        return null;
  }

}
