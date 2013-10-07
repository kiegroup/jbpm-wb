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

package org.jbpm.console.ng.bd.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;

@Remote
public interface DataServiceEntryPoint {

    Map<String, String> getServiceTasks(String processId);
    
    Collection<ProcessInstanceSummary> getProcessInstances();

    Collection<ProcessInstanceSummary> getProcessInstancesByDeploymentId(String sessionId, List<Integer> states);

    Collection<ProcessSummary> getProcessesByFilter(String filter);

    ProcessInstanceSummary getProcessInstanceById(long processInstanceId);

    ProcessSummary getProcessById(String deploymentId, String processId);

    Collection<ProcessSummary> getProcesses();

    Collection<ProcessInstanceSummary> getProcessInstancesByProcessDefinition(String processDefId);

    Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId);

    Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId, boolean completed);

    Collection<NodeInstanceSummary> getProcessInstanceFullHistory(long processInstanceId);

    Collection<NodeInstanceSummary> getProcessInstanceActiveNodes(long processInstanceId);

    Collection<ProcessInstanceSummary> getProcessInstances(List<Integer> states, String filterText, String initiator);

    Collection<NodeInstanceSummary> getProcessInstanceCompletedNodes(long processInstanceId);

    Collection<VariableSummary> getVariableHistory(long processInstanceId, String variableId);

    /*
     * BPMN2
     */

    Collection<String> getReusableSubProcesses(String processId);

    List<String> getAssociatedDomainObjects(String processId);

    Map<String, String> getRequiredInputData(String processId);

    List<String> getAssociatedForms(String processId);

    Collection<TaskDefSummary> getAllTasksDef(String processId);

    Map<String, String> getAssociatedEntities(String processId);

    ProcessSummary getProcessDesc(String processId);

    Collection<VariableSummary> getVariablesCurrentState(long processInstanceId, String processId);

    Map<String, String> getTaskInputMappings(String processId, String taskName);

    Map<String, String> getTaskOutputMappings(String processId, String taskName);

}
