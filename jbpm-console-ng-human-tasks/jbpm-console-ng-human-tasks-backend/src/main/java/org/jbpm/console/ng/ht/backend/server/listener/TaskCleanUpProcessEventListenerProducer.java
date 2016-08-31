/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.backend.server.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.runtime.manager.EventListenerProducer;
import org.jbpm.runtime.manager.api.qualifiers.Process;
import org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener;
import org.kie.api.task.TaskService;

/**
 *
 * @author salaboy
 */
@Process
public class TaskCleanUpProcessEventListenerProducer implements EventListenerProducer<ProcessEventListener>{

    @Inject
    private TaskService taskService;
    
    @Override
    public List<ProcessEventListener> getEventListeners(String identifier, Map<String, Object> params) {
        List<ProcessEventListener> taskCleanupListeners = new ArrayList<ProcessEventListener>();
        taskCleanupListeners.add(new TaskCleanUpProcessEventListener(taskService));
        return taskCleanupListeners;
    }
    
 
}
