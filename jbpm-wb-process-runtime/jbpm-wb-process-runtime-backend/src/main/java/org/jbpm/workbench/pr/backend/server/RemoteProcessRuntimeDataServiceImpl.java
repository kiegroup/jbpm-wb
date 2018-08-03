/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ks.integration.AbstractKieServerService;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.RuntimeLogSummary;
import org.jbpm.workbench.pr.model.TaskDefSummary;
import org.jbpm.workbench.pr.model.UserTaskSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@ApplicationScoped
public class RemoteProcessRuntimeDataServiceImpl extends AbstractKieServerService implements ProcessRuntimeDataService {

    @Override
    public ProcessInstanceSummary getProcessInstance(String serverTemplateId,
                                                     ProcessInstanceKey processInstanceKey) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);

        ProcessInstance processInstance = queryServicesClient.findProcessInstanceById(processInstanceKey.getProcessInstanceId());

        return build(processInstance);
    }

    @Override
    public List<NodeInstanceSummary> getProcessInstanceActiveNodes(String serverTemplateId,
                                                                   String deploymentId,
                                                                   Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        List<NodeInstanceSummary> instances = new ArrayList<NodeInstanceSummary>();
        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);

        List<NodeInstance> nodeInstances = queryServicesClient.findActiveNodeInstances(processInstanceId,
                                                                                       0,
                                                                                       Integer.MAX_VALUE);

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
    public List<RuntimeLogSummary> getProcessInstanceLogs(String serverTemplateId,
                                                          String deploymentId,
                                                          Long processInstanceId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);
        List<NodeInstance> processInstanceHistory = queryServicesClient.findNodeInstances(processInstanceId,
                                                                                            0,
                                                                                            Integer.MAX_VALUE);

        return processInstanceHistory.stream().map(new RuntimeLogSummaryMapper()).collect(toList());
    }

    @Override
    public List<ProcessSummary> getProcesses(String serverTemplateId,
                                             Integer page,
                                             Integer pageSize,
                                             String sort,
                                             Boolean sortOrder) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);

        List<ProcessDefinition> processes = queryServicesClient.findProcesses(page,
                                                                              pageSize,
                                                                              sort,
                                                                              sortOrder);

        return processes.stream().map(new ProcessSummaryMapper()).collect(toList());
    }

    @Override
    public ProcessSummary getProcess(final String serverTemplateId,
                                     final ProcessDefinitionKey processDefinitionKey) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        ProcessServicesClient queryServicesClient = getClient(serverTemplateId,
                                                              ProcessServicesClient.class);

        ProcessDefinition definition = queryServicesClient.getProcessDefinition(processDefinitionKey.getDeploymentId(),
                                                                                processDefinitionKey.getProcessId());

        return new ProcessSummaryMapper().apply(definition);
    }

    @Override
    public List<ProcessSummary> getProcessesByFilter(String serverTemplateId,
                                                     String textSearch,
                                                     Integer page,
                                                     Integer pageSize,
                                                     String sort,
                                                     Boolean sortOrder) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);

        List<ProcessDefinition> processes = queryServicesClient.findProcesses(textSearch,
                                                                              page,
                                                                              pageSize,
                                                                              sort,
                                                                              sortOrder);

        return processes.stream().map(new ProcessSummaryMapper()).collect(toList());
    }

    @Override
    public List<TaskDefSummary> getProcessUserTasks(final String serverTemplateId,
                                                    final String containerId,
                                                    final String processId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        ProcessServicesClient processServicesClient = getClient(serverTemplateId,
                                                                ProcessServicesClient.class);

        final UserTaskDefinitionList userTaskDefinitionList = processServicesClient.getUserTaskDefinitions(containerId,
                                                                                                           processId);

        return userTaskDefinitionList.getItems().stream().map(t -> new TaskDefSummary(t.getName())).collect(toList());
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
                null,
                processInstance.getInitiator(),
                processInstance.getProcessInstanceDescription(),
                processInstance.getCorrelationKey(),
                processInstance.getParentId(),
                null,
                processInstance.getSlaCompliance(),
                processInstance.getSlaDueDate(),                
                0
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