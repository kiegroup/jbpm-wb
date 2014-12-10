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

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.kie.api.task.model.Task;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskLifeCycleServiceImpl implements TaskLifeCycleService {

    @Inject
    private UserTaskService taskService;

    public TaskLifeCycleServiceImpl() {
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
    public void claim(long taskId, String user, String deploymentId) {
        taskService.execute(deploymentId, new CompositeCommand<Void>(new StartTaskCommand(taskId, user), new ClaimTaskCommand(taskId, user)));
    }

    @Override
    public void release(long taskId, String user) {
        taskService.release(taskId, user);
    }

    @Override
    public void delegate(long taskId, String userId, String targetEntityId) {
        taskService.delegate(taskId, userId, targetEntityId);
    }
}
