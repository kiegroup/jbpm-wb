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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.utils.TaskFluent;
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
  
  
}
