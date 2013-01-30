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
package org.jbpm.console.ng.bd.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.bd.model.RuleNotificationSummary;
import org.jbpm.console.ng.bd.model.StatefulKnowledgeSessionSummary;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.commons.java.nio.file.Path;


/**
 *
 * @author salaboy
 */
@Remote
public interface KnowledgeDomainServiceEntryPoint {

    StatefulKnowledgeSessionSummary getSessionSummaryByName(int kSessionId);

    Collection<String> getSessionsNames();

    int getAmountOfSessions();

    Collection<ProcessInstanceSummary> getProcessInstances();

    Collection<ProcessInstanceSummary> getProcessInstances(List<Integer> states, String filterText,
            int filterType, String initiator);

    Collection<ProcessInstanceSummary> getProcessInstancesBySessionId(String sessionId);

    Collection<ProcessSummary> getProcessesBySessionId(String sessionId);

    ProcessInstanceSummary getProcessInstanceById(long processInstanceId);

    Collection<ProcessSummary> getProcesses();

    Collection<ProcessInstanceSummary> getProcessInstancesByProcessDefinition(String processDefId);
    
    Collection<TaskDefSummary> getAllTasksDef(String processId);

    Map<String, String> getAvailableProcesses();
    
    Map<String, String> getAvailableProcessesPath();
    
    String createProcessDefinitionFile(String name);

    Map<String, String> getRequiredInputData(String processId);

    Map<String, String> getAssociatedEntities(String processId);

    Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId);

    Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId, boolean completed);

    Collection<NodeInstanceSummary> getProcessInstanceFullHistory(long processInstanceId);

    Collection<NodeInstanceSummary> getProcessInstanceActiveNodes(long processInstanceId);

    ProcessSummary getProcessDesc(String processId);

    Collection<VariableSummary> getVariablesCurrentState(long processInstanceId, String processId);

    public Map<String, String> getTaskInputMappings(String processId, String taskName);

    public Map<String, String> getTaskOutputMappings(String processId, String taskName);


    Collection<String> getAvailableSignals(String businessKey, long processInstanceId);

    void setProcessVariable(long processInstanceId, String variableId, Object value);

    Collection<VariableSummary> getVariableHistory(long processInstanceId, String variableId);

    Collection<String> getReusableSubProcesses(String processId);

    public void checkFileSystem();

    public void fetchChanges();

    public byte[] loadFile(Path file);

    public Iterable<Path> loadFilesByType(String path, String fileType);

    public void createDomain();

    public void insertNotification(int sessionId, String notification);

    public Collection<RuleNotificationSummary> getAllNotificationInstance();

    public Collection<RuleNotificationSummary> getAllNotificationInstanceBySessionId(int sessionId);
}
