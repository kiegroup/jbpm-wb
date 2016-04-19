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
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.model.TaskAssignmentSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.InternalTaskService;

@Service
@ApplicationScoped
public class TaskOperationsServiceImpl implements TaskOperationsService {

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
                         Date dueDate, final List<String> users, List<String> groups, String identity, boolean start,
                         boolean claim,String taskformName,String deploymentId, Long processInstanceId){
        TaskFluent taskFluent = new TaskFluent().setName(taskName)
                                                .setPriority(priority)
                                                .setDueDate(dueDate)
                                                .setFormName(taskformName);
        if(deploymentId != null && !deploymentId.equals("")){
            taskFluent.setDeploymentID( deploymentId );
        }else{
            taskFluent.setDeploymentID(null);
        }
        if(processInstanceId > 0){
            taskFluent.setProcessInstanceId(processInstanceId);
        }
                
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
            List<String> potOwnersString = getPotentialOwnersByTaskId(potentialOwners);
            return new TaskSummary(task.getId(), task.getName(),
                    task.getDescription(), task.getTaskData().getStatus().name(), task.getPriority(), (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                    .getId() : "", (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId()
                    : "", task.getTaskData().getCreatedOn(), task.getTaskData().getActivationTime(), task.getTaskData()
                    .getExpirationTime(), task.getTaskData().getProcessId(), task.getTaskData().getProcessSessionId(),
                    task.getTaskData().getProcessInstanceId(), task.getTaskData().getDeploymentId()
                    , (int) task.getTaskData().getParentId(),false,potOwnersString);
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
  
    @Override
    public TaskAssignmentSummary getTaskAssignmentDetails(long taskId) {
        Task task = taskService.getTask(taskId);
        if (task != null) {
            List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
            List<String> potOwnersString = new ArrayList<String>();
            if (potentialOwners != null) {
                potOwnersString = getPotentialOwnersByTaskId(potentialOwners);
            }
            
            return new TaskAssignmentSummary(task.getId(),task.getName(),(task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                .getId() : "",potOwnersString);
            }
        return null;
    }
    
    private List<String> getPotentialOwnersByTaskId(List<OrganizationalEntity> potentialOwners){
        
        List<String> orgEntitiesSimple = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
           if (entity instanceof Group) {
             
             orgEntitiesSimple.add("Group:" + entity.getId());
            } else if (entity instanceof User) {
              
                orgEntitiesSimple.add("User:" + entity.getId());
             }
        }
        return orgEntitiesSimple;
     
    }
    @Override
   	public void executeReminderForTask(long taskId,String fromUser) {
    	internalTaskService.executeReminderForTask(taskId,fromUser);
   	}

    /**
     * Checks if the user is allowed to delegate the given task
     *
     * @param taskId
     * @param userId
     * @param groups
     * @return
     */
    @Override
    public Boolean allowDelegate(long taskId, final String userId, final Set<String> groups) {
        final Task task = taskService.getTask(taskId);
        if (task == null) {
            return false;
        }

        if (task.getTaskData().getStatus() == Status.Completed) {
            return false;
        }

        final User actualOwner = task.getTaskData().getActualOwner();
        if (actualOwner != null && actualOwner.getId().equals(userId)) {
            return true;
        }

        final User initiator = task.getPeopleAssignments().getTaskInitiator();
        if (initiator != null && initiator.getId().equals(userId)) {
            return true;
        }

        final List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        if (potentialOwners != null && (isUserInList(potentialOwners, userId) || isGroupInList(potentialOwners, groups))) {
            return true;
        }

        final List<OrganizationalEntity> businessAdministrators = task.getPeopleAssignments().getBusinessAdministrators();
        if (businessAdministrators != null && (isUserInList(businessAdministrators, userId) || isGroupInList(businessAdministrators, groups))) {
            return true;
        }

        return false;
    }

    private boolean isUserInList(final List<OrganizationalEntity> entities, final String userId) {
        for (final OrganizationalEntity entity : entities) {
            if (entity instanceof User && entity.getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isGroupInList(final List<OrganizationalEntity> entities, final Set<String> groups) {
        for (final OrganizationalEntity entity : entities) {
            if (entity instanceof Group && groups.contains(entity.getId())) {
                return true;
            }
        }

        return false;
    }

}