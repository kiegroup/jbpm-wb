/**
 * Copyright 2010 JBoss Inc
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
package org.droolsjbpm.services.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.drools.definition.process.Process;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class DomainKnowledgeServiceBaseTest {
  
   
    @Inject 
    protected KnowledgeDomainService knowledgeService;
    
    @Inject
    protected TaskServiceEntryPoint taskService; 
 
    @Test
    public void testSimpleProcess() throws Exception {
        StatefulKnowledgeSession ksession = knowledgeService.getSessionByBusinessKey("default");
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        Collection<Process> processes = ksession.getKnowledgeBase().getProcesses();
        
        assertEquals(1, processes.size());
        
        ksession.startProcess( "org.jbpm.writedocument", null );
        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();
        
        assertEquals(1, processInstances.size());
        
        assertEquals(1, tasksAssignedAsPotentialOwner.size());
        
        // Get Twice to test duplicated items
        tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        
        assertEquals(1, tasksAssignedAsPotentialOwner.size());
        
        
        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);
        
        
        taskService.start(task.getId(), "salaboy");
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Result", "Initial Document");
        taskService.complete(task.getId(), "salaboy", result);
        
        
        
        List<TaskSummary> translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());
        
        
        List<TaskSummary> reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());
        
        translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());
        
        reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());
        
        
        
        
        
        
    }
   
}
