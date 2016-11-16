/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ks.integration.AbstractKieServerService;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessInstanceKey;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.RuntimeLogSummary;
import org.jbpm.console.ng.pr.model.TaskDefSummary;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.jbpm.console.ng.pr.service.ProcessRuntimeDataService;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.ocpsoft.prettytime.PrettyTime;

import static java.util.stream.Collectors.toList;
import static java.util.Collections.emptyList;

@Service
@ApplicationScoped
public class RemoteProcessRuntimeDataServiceImpl extends AbstractKieServerService implements ProcessRuntimeDataService {

    public List<ProcessInstanceSummary> getProcessInstances(String serverTemplateId, List<Integer> statuses, Integer page, Integer pageSize) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        List<ProcessInstance> processInstances = queryServicesClient.findProcessInstancesByStatus(statuses, page, pageSize);

        return processInstances
                .stream()
                .map( p -> build(p))
                .collect(toList());
    }

    @Override
    public ProcessInstanceSummary getProcessInstance(String serverTemplateId, ProcessInstanceKey processInstanceKey) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        ProcessInstance processInstance = queryServicesClient.findProcessInstanceById(processInstanceKey.getProcessInstanceId());

        return build(processInstance);
    }

    @Override
    public List<NodeInstanceSummary> getProcessInstanceActiveNodes(String serverTemplateId, Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        List<NodeInstanceSummary> instances = new ArrayList<NodeInstanceSummary>();
        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        List<NodeInstance> nodeInstances = queryServicesClient.findActiveNodeInstances(processInstanceId, 0, 100);

        for (NodeInstance instance : nodeInstances) {
            NodeInstanceSummary summary = new NodeInstanceSummary(instance.getId(),
                    instance.getProcessInstanceId(),
                    instance.getName(),
                    instance.getNodeId(),
                    instance.getNodeType(),
                    instance.getDate().toString(),
                    instance.getConnection(),
                    false);

            instances.add(summary);
        }

        return instances;
    }

    @Override
    public List<RuntimeLogSummary> getBusinessLogs(String serverTemplateId, String processName, Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        List<NodeInstance> processInstanceHistory = getProcessInstanceHistory(serverTemplateId, processInstanceId);
        List<TaskEventInstance> allTaskEventsByProcessInstanceId = new ArrayList<TaskEventInstance>();//taskAuditService.getAllTaskEventsByProcessInstanceId(processInstanceId, "");
        List<RuntimeLogSummary> logs = new ArrayList<RuntimeLogSummary>(processInstanceHistory.size() + allTaskEventsByProcessInstanceId.size());
        PrettyTime prettyDateFormatter = new PrettyTime();

        for(int i = processInstanceHistory.size() - 1 ; i >= 0; i--){
            NodeInstance nis = processInstanceHistory.get(i);

            if(nis.getNodeType().equals("HumanTaskNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), "Task '" + nis.getName() + "' was created", "System"));
                for(TaskEventInstance te : allTaskEventsByProcessInstanceId){
                    if(te.getWorkItemId() != null && nis.getId() == te.getWorkItemId() &&
                        (te.getType().equals("CLAIMED") || te.getType().equals("RELEASED") || te.getType().equals("COMPLETED"))){
                            logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), "Task '" + nis.getName() +
                                    "' was " + te.getType().toLowerCase() + " by user " + te.getUserId(), "Human"));
                    }
                }
            }else if(nis.getNodeType().equals("StartNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), "Process '" + processName + "' was created", "Human"));
            }else if(nis.getNodeType().equals("EndNode")){
                logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), "Process '" + processName + "' was completed", "System"));
            }


        }
        return logs;
    }

    @Override
    public List<ProcessSummary> getProcesses(String serverTemplateId, Integer page, Integer pageSize, String sort, Boolean sortOrder) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        List<ProcessDefinition> processes = queryServicesClient.findProcesses(page, pageSize, sort, sortOrder);

        return processes
                .stream()
                .map(definition -> new ProcessSummary(definition.getId(),
                        definition.getName(),
                        definition.getContainerId(),
                        definition.getPackageName(),
                        "",
                        definition.getVersion(),
                        "",
                        ""))
                .collect(toList());
    }

    @Override
    public ProcessSummary getProcess(final String serverTemplateId, final ProcessDefinitionKey processDefinitionKey) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        ProcessServicesClient queryServicesClient = getClient(serverTemplateId, ProcessServicesClient.class);

        ProcessDefinition definition = queryServicesClient.getProcessDefinition(processDefinitionKey.getDeploymentId(), processDefinitionKey.getProcessId());

        ProcessSummary summary = new ProcessSummary(definition.getId(),
                definition.getName(),
                definition.getContainerId(),
                definition.getPackageName(),
                "",
                definition.getVersion(),
                "",
                "");

        summary.setAssociatedEntities(definition.getAssociatedEntities());
        summary.setProcessVariables(definition.getProcessVariables());
        summary.setReusableSubProcesses(definition.getReusableSubProcesses());
        summary.setServiceTasks(definition.getServiceTasks());

        return summary;
    }

    @Override
    public List<ProcessSummary> getProcessesByFilter(String serverTemplateId, String textSearch, Integer page, Integer pageSize, String sort, Boolean sortOrder) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        List<ProcessDefinition> processes = queryServicesClient.findProcesses(textSearch, page, pageSize, sort, sortOrder);

        return processes
                .stream()
                .map(definition -> new ProcessSummary(definition.getId(),
                        definition.getName(),
                        definition.getContainerId(),
                        definition.getPackageName(),
                        "",
                        definition.getVersion(),
                        "",
                        ""))
                .collect(toList());
    }

    @Override
    public ProcessSummary getProcessesByContainerIdProcessId(final String serverTemplateId, final String containerId, final String processId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        ProcessDefinition definition = queryServicesClient.findProcessByContainerIdProcessId(containerId, processId);

        ProcessSummary summary = new ProcessSummary(definition.getId(),
                definition.getName(),
                definition.getContainerId(),
                definition.getPackageName(),
                "",
                definition.getVersion(),
                "",
                "");

        summary.setAssociatedEntities(definition.getAssociatedEntities());
        summary.setProcessVariables(definition.getProcessVariables());
        summary.setReusableSubProcesses(definition.getReusableSubProcesses());
        summary.setServiceTasks(definition.getServiceTasks());

        return summary;
    }

    @Override
    public List<TaskDefSummary> getProcessUserTasks(final String serverTemplateId, final String containerId, final String processId){
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        ProcessServicesClient processServicesClient = getClient(serverTemplateId, ProcessServicesClient.class);

        final UserTaskDefinitionList userTaskDefinitionList = processServicesClient.getUserTaskDefinitions(containerId, processId);

        return userTaskDefinitionList.getItems().stream().map(t -> new TaskDefSummary(t.getName())).collect(toList());
    }

    @Override
    public List<RuntimeLogSummary> getRuntimeLogs(final String serverTemplateId, final Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        List<NodeInstance> processInstanceHistory = getProcessInstanceHistory(serverTemplateId, processInstanceId);
        List<TaskEventInstance> allTaskEventsByProcessInstanceId = new ArrayList<TaskEventInstance>();//taskAuditService.getAllTaskEventsByProcessInstanceId(processInstanceId, "");
        List<RuntimeLogSummary> logs = new ArrayList<RuntimeLogSummary>(processInstanceHistory.size() + allTaskEventsByProcessInstanceId.size());
        PrettyTime prettyDateFormatter = new PrettyTime();

        for(int i = processInstanceHistory.size() - 1 ; i >= 0; i--){
            NodeInstance nis = processInstanceHistory.get(i);

                if(nis.getNodeType().equals("HumanTaskNode")){
                    logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), nis.getName() + "("+nis.getNodeType()+")", "System"));
                    for(TaskEventInstance te : allTaskEventsByProcessInstanceId){
                        if(te.getWorkItemId() != null && nis.getId() == te.getWorkItemId()){
                            if(te.getType().equals("ADDED")){
                                logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), te.getUserId() + "->" +te.getType(), "System"));
                            }else{
                                logs.add(new RuntimeLogSummary(nis.getId(), "- " + prettyDateFormatter.format(te.getLogTime()), te.getUserId() + "->" +te.getType(), "Human"));
                            }
                        }
                    }
                }else if(nis.getNodeType().equals("StartNode")){
                    logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), nis.getName() + "("+nis.getNodeType()+")", "Human"));
                }else {
                    logs.add(new RuntimeLogSummary(nis.getId(), prettyDateFormatter.format(nis.getDate()), nis.getName() + "("+nis.getNodeType()+")", "System"));
                }

        }
        return logs;
    }

    protected List<NodeInstance> getProcessInstanceHistory(final String serverTemplateId, final Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId, QueryServicesClient.class);

        return queryServicesClient.findNodeInstances(processInstanceId, 0, 100);

    }

    protected ProcessInstanceSummary build(ProcessInstance processInstance) {
        ProcessInstanceSummary summary = new ProcessInstanceSummary(
                processInstance.getId(),
                processInstance.getProcessId(),
                processInstance.getContainerId(),
                processInstance.getProcessName(),
                processInstance.getProcessVersion(),
                processInstance.getState(),
                processInstance.getDate(),
                processInstance.getInitiator(),
                processInstance.getProcessInstanceDescription(),
                processInstance.getCorrelationKey(),
                processInstance.getParentId()
        );

        if (processInstance.getActiveUserTasks() != null && processInstance.getActiveUserTasks().getTasks() != null) {
            List<TaskSummary> tasks = processInstance.getActiveUserTasks().getItems();

            List<UserTaskSummary> userTaskSummaries = new ArrayList<UserTaskSummary>();
            for (TaskSummary taskSummary : tasks) {
                UserTaskSummary userTaskSummary = new UserTaskSummary(taskSummary.getId(),
                        taskSummary.getName(),
                        taskSummary.getActualOwner(),
                        taskSummary.getStatus());

                userTaskSummaries.add(userTaskSummary);
            }
            summary.setActiveTasks(userTaskSummaries);
        }

        return summary;
    }

}