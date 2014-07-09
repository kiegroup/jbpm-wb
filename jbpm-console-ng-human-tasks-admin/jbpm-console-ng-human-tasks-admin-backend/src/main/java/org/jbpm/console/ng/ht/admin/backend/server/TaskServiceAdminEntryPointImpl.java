/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.admin.backend.server;

import java.io.StringReader;
import java.util.HashMap;
import javax.annotation.PostConstruct;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.admin.service.TaskServiceAdminEntryPoint;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;


@Service
@ApplicationScoped
public class TaskServiceAdminEntryPointImpl implements TaskServiceAdminEntryPoint {

    @Inject
    private InternalTaskService taskService;


    @PostConstruct
    public void init(){
    }

    @Override
    public void generateMockTasks(String userName, int amountOfTasks) {
        for (int i = 0; i < amountOfTasks; i++) {
          Task task = new TaskFluent().setName("Task #" + i + " - name ")
                                      .setDescription(" Task #" + i + " - description")
                                      .addPotentialUser(userName)
                                      .setAdminUser("Administrator")
                                      .setAdminGroup("Administrators")
                                      .getTask();
          long addTask = taskService.addTask(task, new HashMap<String, Object>());
          taskService.start(addTask, userName);
        }
    }
    
    

   

}
