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
package org.jbpm.console.ng.bd.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.RulesNotificationService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.model.RuleNotificationSummary;
import org.jbpm.console.ng.bd.model.StatefulKnowledgeSessionSummary;
import org.jbpm.console.ng.bd.service.KnowledgeDomainServiceEntryPoint;
import org.jbpm.console.ng.ht.backend.server.TaskDefHelper;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.pr.backend.server.NodeInstanceHelper;
import org.jbpm.console.ng.pr.backend.server.ProcessHelper;
import org.jbpm.console.ng.pr.backend.server.ProcessInstanceHelper;
import org.jbpm.console.ng.pr.backend.server.VariableHelper;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;


import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class KnowledgeDomainServiceEntryPointImpl implements KnowledgeDomainServiceEntryPoint {

    @Inject
    KnowledgeDomainService domainService;
    @Inject
    RulesNotificationService rulesNotificationService;
    @Inject
    KnowledgeDataService dataService;
    @Inject
    BPMN2DataService bpmn2Service;
    @Inject
    FileService fs;

    public KnowledgeDomainServiceEntryPointImpl() {
    }

    public Collection<String> getSessionsNames() {
        return domainService.getSessionsNames();
    }

    public int getAmountOfSessions() {
        return domainService.getAmountOfSessions();
    }

    public Collection<ProcessInstanceSummary> getProcessInstances() {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstances());
    }

    public Collection<ProcessInstanceSummary> getProcessInstancesBySessionId(String sessionId) {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstancesBySessionId(sessionId));
    }

    public Collection<ProcessSummary> getProcessesBySessionId(String sessionId) {
        return ProcessHelper.adaptCollection(dataService.getProcessesByDomainName(sessionId));
    }

    public ProcessInstanceSummary getProcessInstanceById(long processInstanceId) {
        int sessionId = domainService.getSessionForProcessInstanceId(processInstanceId);
        return ProcessInstanceHelper.adapt(dataService.getProcessInstanceById(sessionId, processInstanceId));
    }


    public Collection<ProcessSummary> getProcesses() {
        return ProcessHelper.adaptCollection(dataService.getProcesses());
    }
    
    public Collection<ProcessInstanceSummary> getProcessInstancesByProcessDefinition(String processDefId) {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstancesByProcessDefinition(processDefId));
    }

    public List<String> getAssociatedDomainObjects(String processId) {
        return bpmn2Service.getAssociatedDomainObjects(processId);
    }

    public Map<String, String> getRequiredInputData(String processId) {
        return bpmn2Service.getProcessData(processId);
    }

    public List<String> getAssociatedForms(String processId) {
        return bpmn2Service.getAssociatedForms(processId);
    }

    public Collection<TaskDefSummary> getAllTasksDef(String processId) {
        return TaskDefHelper.adaptCollection(bpmn2Service.getAllTasksDef(processId));
    }

    public Map<String, String> getAvailableProcesses() {
        return domainService.getAvailableProcesses();
    }

    public Map<String, String> getAvailableProcessesPath() {
        return domainService.getAvailableProcessesPaths();
    }

    public Map<String, String> getAssociatedEntities(String processId) {
        return bpmn2Service.getAssociatedEntities(processId);
    }

    public Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId) {
        int sessionId = domainService.getSessionForProcessInstanceId(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceHistory(sessionId, processInstanceId));
    }

    public Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId, boolean completed) {
        int sessionId = domainService.getSessionForProcessInstanceId(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceHistory(sessionId, processInstanceId, completed));
    }

    public Collection<NodeInstanceSummary> getProcessInstanceFullHistory(long processInstanceId) {
        int sessionId = domainService.getSessionForProcessInstanceId(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceFullHistory(sessionId, processInstanceId));
    }

    public Collection<NodeInstanceSummary> getProcessInstanceActiveNodes(long processInstanceId) {
        int sessionId = domainService.getSessionForProcessInstanceId(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceActiveNodes(sessionId, processInstanceId));
    }

    public ProcessSummary getProcessDesc(String processId) {
        return ProcessHelper.adapt(bpmn2Service.getProcessDesc(processId));
    }

    public Collection<VariableSummary> getVariablesCurrentState(long processInstanceId, String processId) {
        Map<String, String> properties = new HashMap<String, String>(bpmn2Service.getProcessData(processId));
        return VariableHelper.adaptCollection(dataService.getVariablesCurrentState(processInstanceId), properties, processInstanceId);
    }

    public Map<String, String> getTaskInputMappings(String processId, String taskName) {
        return bpmn2Service.getTaskInputMappings(processId, taskName);
    }

    public Map<String, String> getTaskOutputMappings(String processId, String taskName) {
        return bpmn2Service.getTaskOutputMappings(processId, taskName);
    }

   

    @Override
    public Collection<ProcessInstanceSummary> getProcessInstances(List<Integer> states, String filterText,
            int filterType, String initiator) {
        Collection<ProcessInstanceDesc> result = null;
        if (filterType == 0) {
            // search by process id
            result = dataService.getProcessInstancesByProcessId(states, filterText, initiator);
        } else if (filterType == 1) {
            // search by process name
            result = dataService.getProcessInstancesByProcessName(states, filterText, initiator);
        } else {
            result = dataService.getProcessInstances(states, initiator);
        }

        return ProcessInstanceHelper.adaptCollection(result);
    }


    @Override
    public Collection<String> getAvailableSignals(String businessKey, long processInstanceId) {
        StatefulKnowledgeSession ksession = domainService.getSessionsByName(businessKey).values().iterator().next();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        Collection<String> activeSignals = new ArrayList<String>();

        if (processInstance != null) {
            ((ProcessInstanceImpl) processInstance).setProcess(ksession.getKieBase().getProcess(processInstance.getProcessId()));
            Collection<NodeInstance> activeNodes = ((WorkflowProcessInstance) processInstance).getNodeInstances();

            activeSignals.addAll(ProcessInstanceHelper.collectActiveSignals(activeNodes));
        }

        return activeSignals;
    }

    @Override
    public void setProcessVariable(long processInstanceId, String variableId, Object value) {
        StatefulKnowledgeSession ksession = domainService.getSessionById(domainService.getSessionForProcessInstanceId(processInstanceId));
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);

        ((WorkflowProcessInstance) processInstance).setVariable(variableId, value);

    }

    @Override
    public Collection<VariableSummary> getVariableHistory(long processInstanceId, String variableId) {
        return VariableHelper.adaptCollection(dataService.getVariableHistory(processInstanceId, variableId));
    }

    public ProcessSummary getProcessesById(long processInstanceId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<String> getReusableSubProcesses(String processId) {

        return bpmn2Service.getReusableSubProcesses(processId);
    }

    public void checkFileSystem() {
        fs.fetchChanges();
    }

    public String createProcessDefinitionFile(String name) {
      return fs.createFile(name).toString();
    }
    
    public void fetchChanges() {
        fs.fetchChanges();
    }

    public byte[] loadFile(Path file) {
        try {
            return fs.loadFile(file);
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Iterable<Path> loadFilesByType(String path, String fileType) {
        try {
            return fs.loadFilesByType(path, fileType);
        } catch (FileException ex) {
            Logger.getLogger(KnowledgeDomainServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void createDomain() {
        domainService.createDomain();
    }

    public StatefulKnowledgeSessionSummary getSessionSummaryByName(int sessionId) {
        return StatefulKnowledgeSessionHelper.adapt(domainService.getSessionById(sessionId));
    }

    public void insertNotification(int sessionId, String notification) {
        rulesNotificationService.insertNotification(sessionId, notification);
    }

    public Collection<RuleNotificationSummary> getAllNotificationInstance() {
        return RuleNotificationHelper.adaptCollection(rulesNotificationService.getAllNotificationInstance());
    }

    public Collection<RuleNotificationSummary> getAllNotificationInstanceBySessionId(int sessionId) {
        return RuleNotificationHelper.adaptCollection(rulesNotificationService.getAllNotificationInstanceBySessionId(sessionId));
    }

    
}
