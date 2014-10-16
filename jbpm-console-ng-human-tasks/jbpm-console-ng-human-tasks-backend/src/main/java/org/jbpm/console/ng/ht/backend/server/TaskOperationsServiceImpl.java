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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskOperationsServiceImpl implements TaskOperationsService{

  @Inject
  private InternalTaskService internalTaskService;
    
  @Inject
  private UserTaskService taskService;

  @Inject
  private RuntimeDataService runtimeDataService;

  @Override
  public long addQuickTask(
                         final String taskName,
                         int priority,
                         Date dueDate, final List<String> users, List<String> groups, String identity, boolean start, boolean claim){
        TaskFluent taskFluent = new TaskFluent().setName(taskName)
                                                .setPriority(priority)
                                                .setDueDate(dueDate);
                
        for(String user : users){
            taskFluent.addPotentialUser(user);
        }
        for(String group : groups){
            taskFluent.addPotentialGroup(group);
        }
        taskFluent.setAdminUser("Administrator");
        taskFluent.setAdminGroup("Administrators");
        long taskId = internalTaskService.addTask(taskFluent.getTask(), new HashMap<String, Object>());
        if(start){
            taskService.start(taskId, identity);
        }
        if(claim){
            taskService.claim(taskId, identity);
        }
        
        return taskId;
    }

  @Override
  public void updateTask(long taskId, int priority, List<String> taskDescription,
            Date dueDate) {
        taskService.setPriority(taskId, priority);
        if(taskDescription != null){
          taskService.setDescription(taskId, taskDescription.get(0));
        }
        if(dueDate != null){
          taskService.setExpirationDate(taskId, dueDate);
        }
  }
  
  
    @Override
    public TaskSummary getTaskDetails(long taskId) {
        Task task = taskService.getTask(taskId);
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

    @Override
    public long saveContent(long taskId, Map<String, Object> values) {
        return taskService.saveContent(taskId, values);
    }
    
    @Override
    public boolean existInDatabase(long taskId) {
        return runtimeDataService.getTaskById(taskId) == null ? false : true;
    }
  
  
}
