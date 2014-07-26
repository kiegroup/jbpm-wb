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

package org.jbpm.console.ng.bd.backend.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.model.RuntimeLogSummary;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.backend.server.TaskDefHelper;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.console.ng.pr.backend.server.NodeInstanceHelper;
import org.jbpm.console.ng.pr.backend.server.ProcessHelper;
import org.jbpm.console.ng.pr.backend.server.ProcessInstanceHelper;
import org.jbpm.console.ng.pr.backend.server.VariableHelper;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Service combines the Data and BPMN2 services.
 * 
 * The responsibility of this service is to allow us to query all the data that is generated by the assets and the runtimes that
 * we are using
 */
@Service
@ApplicationScoped
public class DataServiceEntryPointImpl implements DataServiceEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(DataServiceEntryPointImpl.class);
    @Inject
    private RuntimeDataService dataService;
    
    @Inject
    private TaskServiceEntryPoint taskService;
    
    @Inject
    private BPMN2DataService bpmn2Service;

    public DataServiceEntryPointImpl() {
    }

   
    
    @Override
    public Map<String, String> getServiceTasks(String processId){
        return bpmn2Service.getAllServiceTasks(processId);
    }
    
    
    @Override
    public Collection<ProcessInstanceSummary> getProcessInstances() {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstances());
    }

    @Override
    public Collection<ProcessInstanceSummary> getProcessInstancesByDeploymentId(String deploymentId, List<Integer> states) {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstancesByDeploymentId(deploymentId, states));
    }

    @Override
    public Collection<ProcessSummary> getProcessesByFilter(String filter) {
        return ProcessHelper.adaptCollection(dataService.getProcessesByFilter(filter));
    }

    @Override
    public ProcessInstanceSummary getProcessInstanceById(long processInstanceId) {
        return ProcessInstanceHelper.adapt(dataService.getProcessInstanceById(processInstanceId));
    }

    @Override
    public ProcessSummary getProcessById(String deploymentId, String processId) {
        return ProcessHelper.adapt(dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId));
    }

    @Override
    public Collection<ProcessInstanceSummary> getProcessInstancesByProcessDefinition(String processDefId) {
        return ProcessInstanceHelper.adaptCollection(dataService.getProcessInstancesByProcessDefinition(processDefId));
    }

    @Override
    public Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceHistory(piDesc.getDeploymentId(),
                processInstanceId));
    }

    @Override
    public Collection<NodeInstanceSummary> getProcessInstanceHistory(long processInstanceId, boolean completed) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceHistory(piDesc.getDeploymentId(),
                processInstanceId, completed));
    }

    @Override
    public Collection<NodeInstanceSummary> getProcessInstanceFullHistory(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceFullHistory(piDesc.getDeploymentId(),
                processInstanceId));
    }

    @Override
    public Collection<NodeInstanceSummary> getProcessInstanceActiveNodes(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceActiveNodes(piDesc.getDeploymentId(),
                processInstanceId));
    }

    @Override
    public Collection<ProcessInstanceSummary> getProcessInstances(List<Integer> states, String filterText, String initiator) {
        Collection<ProcessInstanceDesc> result = null;
        if (!filterText.equals("")) {
            // search by process name
            result = dataService.getProcessInstancesByProcessName(states, filterText, initiator);
        } else {
            result = dataService.getProcessInstances(states, initiator);
        }

        return ProcessInstanceHelper.adaptCollection(result);
    }

    @Override
    public Collection<NodeInstanceSummary> getProcessInstanceCompletedNodes(long processInstanceId) {
        ProcessInstanceDesc piDesc = dataService.getProcessInstanceById(processInstanceId);
        return NodeInstanceHelper.adaptCollection(dataService.getProcessInstanceCompletedNodes(piDesc.getDeploymentId(),
                processInstanceId));

    }

    @Override
    public Collection<VariableSummary> getVariableHistory(long processInstanceId, String variableId) {
        return VariableHelper.adaptCollection(dataService.getVariableHistory(processInstanceId, variableId));
    }

    /*
     * BPMN2
     */

    @Override
    public Collection<String> getReusableSubProcesses(String processId) {
        return bpmn2Service.getReusableSubProcesses(processId);
    }

    @Override
    public List<String> getAssociatedDomainObjects(String processId) {
        return bpmn2Service.getAssociatedDomainObjects(processId);
    }

    @Override
    public Map<String, String> getRequiredInputData(String processId) {
        return bpmn2Service.getProcessData(processId);
    }

    @Override
    public List<String> getAssociatedForms(String processId) {
        return bpmn2Service.getAssociatedForms(processId);
    }

    @Override
    public Collection<TaskDefSummary> getAllTasksDef(String processId) {
        return TaskDefHelper.adaptCollection(bpmn2Service.getAllTasksDef(processId));
    }

    @Override
    public Map<String, String> getAssociatedEntities(String processId) {
        return bpmn2Service.getAssociatedEntities(processId);
    }

    @Override
    public ProcessSummary getProcessDesc(String processId) {
        return ProcessHelper.adapt(bpmn2Service.getProcessDesc(processId));
    }

    @Override
    public Collection<VariableSummary> getVariablesCurrentState(long processInstanceId, String processId) {
        Map<String, String> properties = new HashMap<String, String>(bpmn2Service.getProcessData(processId));
        return VariableHelper.adaptCollection(dataService.getVariablesCurrentState(processInstanceId), properties,
                processInstanceId);
    }

    @Override
    public Map<String, String> getTaskInputMappings(String processId, String taskName) {
        return bpmn2Service.getTaskInputMappings(processId, taskName);
    }

    @Override
    public Map<String, String> getTaskOutputMappings(String processId, String taskName) {
        return bpmn2Service.getTaskOutputMappings(processId, taskName);
    }

    @Override
    public Collection<RuntimeLogSummary> getBusinessLogs(long processInstanceId) {
        ProcessInstanceSummary processInstanceData = getProcessInstanceById(processInstanceId);
        List<NodeInstanceSummary> processInstanceHistory = (List<NodeInstanceSummary>)getProcessInstanceHistory(processInstanceId);
        List<TaskEventSummary> allTaskEventsByProcessInstanceId = taskService.getAllTaskEventsByProcessInstanceId(processInstanceId, "");
        List<RuntimeLogSummary> logs = new ArrayList<RuntimeLogSummary>(processInstanceHistory.size() + allTaskEventsByProcessInstanceId.size());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy HH:mm:ss");
        PrettyTime prettyDateFormatter = new PrettyTime();

        for(int i = processInstanceHistory.size() - 1 ; i >= 0; i--){
          NodeInstanceSummary nis = processInstanceHistory.get(i);
          try{            
              if(nis.getType().equals("HumanTaskNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), "Task '" + nis.getNodeName() + "' was created", "System"));              
                for(TaskEventSummary te : allTaskEventsByProcessInstanceId){
                  if(te.getWorkItemId() != null && nis.getId() == te.getWorkItemId()){
                    if(te.getType().equals("CLAIMED") || te.getType().equals("RELEASED") || te.getType().equals("COMPLETED")){
                      logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), "Task '" + nis.getNodeName() + 
                              "' was " + te.getType().toLowerCase() + " by user " + te.getUserId(), "Human"));
                    }
                  }
                }
              }else if(nis.getType().equals("StartNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), "Process '" + processInstanceData.getProcessName() + "' was created", "Human"));
              }else if(nis.getType().equals("EndNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), "Process '" + processInstanceData.getProcessName() + "' was completed", "System"));
              }
          } catch (ParseException e) {            
              throw new RuntimeException("Can't create date from string using 'dd/MMM/yy HH:mm:ss' format!",
                      e);
          }

        }
        return logs;
    }

  @Override
  public Collection<RuntimeLogSummary> getAllRuntimeLogs(long processInstanceId) {
        List<NodeInstanceSummary> processInstanceHistory = (List<NodeInstanceSummary>)getProcessInstanceHistory(processInstanceId);
        List<TaskEventSummary> allTaskEventsByProcessInstanceId = taskService.getAllTaskEventsByProcessInstanceId(processInstanceId, "");
        List<RuntimeLogSummary> logs = new ArrayList<RuntimeLogSummary>(processInstanceHistory.size() + allTaskEventsByProcessInstanceId.size());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy HH:mm:ss");
        PrettyTime prettyDateFormatter = new PrettyTime();

        for(int i = processInstanceHistory.size() - 1 ; i >= 0; i--){
          NodeInstanceSummary nis = processInstanceHistory.get(i);
          try{
              if(nis.getType().equals("HumanTaskNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), nis.getNodeName() + "("+nis.getType()+")", "System"));              
                for(TaskEventSummary te : allTaskEventsByProcessInstanceId){
                  if(te.getWorkItemId() != null && nis.getId() == te.getWorkItemId()){
                    if(te.getType().equals("ADDED")){
                      logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), te.getUserId() + "->" +te.getType(), "System"));
                    }else{
                      logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), te.getUserId() + "->" +te.getType(), "Human"));
                    }                  
                  }
                }
              }else if(nis.getType().equals("StartNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), nis.getNodeName() + "("+nis.getType()+")", "Human"));
              }else {
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(dateFormat.parse(nis.getTimestamp())), nis.getNodeName() + "("+nis.getType()+")", "System"));
              }
          } catch (ParseException e) {            
              throw new RuntimeException("Can't create date from string using 'dd/MMM/yy HH:mm:ss' format!",
                      e);
          }

        }
        return logs;
  }
    /** Logs */
    
}
