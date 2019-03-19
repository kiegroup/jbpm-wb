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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.ks.integration.AbstractKieServerService;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceDiagramSummary;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.TaskDefSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.jbpm.workbench.pr.model.WorkItemSummary;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.UserTaskDefinitionList;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.WorkItemInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@ApplicationScoped
public class RemoteProcessRuntimeDataServiceImpl extends AbstractKieServerService implements ProcessRuntimeDataService {

    public static int NOT_FOUND_ERROR_CODE = 404;    

    @Inject
    private ProcessImageService processImageService;

    @Override
    public ProcessInstanceSummary getProcessInstance(ProcessInstanceKey processInstanceKey) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return null;
        }

        QueryServicesClient queryServicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                            QueryServicesClient.class);

        ProcessInstance processInstance = queryServicesClient.findProcessInstanceById(processInstanceKey.getProcessInstanceId());

        return new ProcessInstanceSummaryMapper(processInstanceKey.getServerTemplateId()).apply(processInstance);
    }

    @Override
    public List<NodeInstanceSummary> getProcessInstanceActiveNodes(ProcessInstanceKey processInstanceKey) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                            QueryServicesClient.class);

        List<NodeInstance> nodeInstances = queryServicesClient.findActiveNodeInstances(processInstanceKey.getProcessInstanceId(),
                                                                                       0,
                                                                                       Integer.MAX_VALUE);

        return nodeInstances.stream().map(new NodeInstanceSummaryMapper()).collect(toList());
    }

    @Override
    public List<NodeInstanceSummary> getProcessInstanceCompletedNodes(ProcessInstanceKey processInstanceKey) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return emptyList();
        }

        QueryServicesClient queryServicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                            QueryServicesClient.class);

        List<NodeInstance> nodeInstances = queryServicesClient.findCompletedNodeInstances(processInstanceKey.getProcessInstanceId(),
                                                                                          0,
                                                                                          Integer.MAX_VALUE);

        return nodeInstances.stream().map(new NodeInstanceSummaryMapper()).collect(toList());
    }

    @Override
    public ProcessInstanceDiagramSummary getProcessInstanceDiagramSummary(ProcessInstanceKey processInstanceKey, String completedNodeColor,
                                                                          String completedNodeBorderColor, String activeNodeBorderColor) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return null;
        }

        final ProcessInstanceSummary processInstance = getProcessInstance(processInstanceKey);

        ProcessInstanceDiagramSummary summary = new ProcessInstanceDiagramSummary();
        summary.setId(processInstance.getId());
        summary.setName(processInstance.getName());

        summary.setSvgContent(processImageService.getProcessInstanceDiagram(processInstanceKey.getServerTemplateId(),
                                                                            processInstanceKey.getDeploymentId(),
                                                                            processInstanceKey.getProcessInstanceId(),
                                                                            completedNodeColor,
                                                                            completedNodeBorderColor,
                                                                            activeNodeBorderColor));

        summary.setProcessDefinition(getProcess(new ProcessDefinitionKey(processInstance.getServerTemplateId(),
                                                                         processInstance.getDeploymentId(),
                                                                         processInstance.getProcessId())));

        if (processInstance.getState() == org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE) {
            List<NodeInstanceSummary> nodeInstances = getProcessInstanceActiveNodes(processInstanceKey);
            nodeInstances.addAll(getProcessInstanceCompletedNodes(processInstanceKey));
            summary.setNodeInstances(nodeInstances);
            summary.setTimerInstances(getProcessInstanceTimerInstances(processInstanceKey));
        } else {
            summary.setNodeInstances(emptyList());
            summary.setTimerInstances(emptyList());
        }
        return summary;
    }

    @Override
    public List<TimerInstanceSummary> getProcessInstanceTimerInstances(ProcessInstanceKey processInstanceKey) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return emptyList();
        }

        ProcessAdminServicesClient servicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                              ProcessAdminServicesClient.class);
        return servicesClient.getTimerInstances(processInstanceKey.getDeploymentId(),
                                                processInstanceKey.getProcessInstanceId()).stream().map(new TimerInstanceSummaryMapper()).collect(toList());
    }

    @Override
    public void triggerProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                           Long nodeId) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return;
        }

        ProcessAdminServicesClient servicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                              ProcessAdminServicesClient.class);
        servicesClient.triggerNode(processInstanceKey.getDeploymentId(),
                                   processInstanceKey.getProcessInstanceId(),
                                   nodeId);
    }

    @Override
    public void cancelProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                          Long nodeInstanceId) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return;
        }

        ProcessAdminServicesClient servicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                              ProcessAdminServicesClient.class);
        servicesClient.cancelNodeInstance(processInstanceKey.getDeploymentId(),
                                          processInstanceKey.getProcessInstanceId(),
                                          nodeInstanceId);
    }

    @Override
    public void reTriggerProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                             Long nodeInstanceId) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return;
        }

        ProcessAdminServicesClient servicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                              ProcessAdminServicesClient.class);
        servicesClient.retriggerNodeInstance(processInstanceKey.getDeploymentId(),
                                             processInstanceKey.getProcessInstanceId(),
                                             nodeInstanceId);
    }

    @Override
    public void rescheduleTimerInstance(ProcessInstanceKey processInstanceKey,
                                        TimerInstanceSummary summary) {
        if (processInstanceKey == null || processInstanceKey.isValid() == false) {
            return;
        }

        ProcessAdminServicesClient servicesClient = getClient(processInstanceKey.getServerTemplateId(),
                                                              ProcessAdminServicesClient.class);
        if (summary.isRelative()) {
            servicesClient.updateTimerRelative(processInstanceKey.getDeploymentId(),
                                               processInstanceKey.getProcessInstanceId(),
                                               summary.getId(),
                                               summary.getDelay(),
                                               summary.getPeriod(),
                                               summary.getRepeatLimit());
        } else {
            servicesClient.updateTimer(processInstanceKey.getDeploymentId(),
                                       processInstanceKey.getProcessInstanceId(),
                                       summary.getId(),
                                       summary.getDelay(),
                                       summary.getPeriod(),
                                       summary.getRepeatLimit());
        }
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
    public ProcessSummary getProcess(final ProcessDefinitionKey processDefinitionKey) {
        if (processDefinitionKey == null || processDefinitionKey.isValid() == false) {
            return null;
        }

        ProcessServicesClient queryServicesClient = getClient(processDefinitionKey.getServerTemplateId(),
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

    @Override
    public WorkItemSummary getWorkItemByProcessInstanceId(final String serverTemplateId,
                                                          final String containerId,
                                                          final Long processInstanceId,
                                                          final Long workItemId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        ProcessServicesClient processServicesClient = getClient(serverTemplateId,
                                                                ProcessServicesClient.class);

        try {
            final WorkItemInstance workItem = processServicesClient.getWorkItem(containerId,
                                                                                processInstanceId,
                                                                                workItemId);
            return new WorkItemSummaryMapper().apply(workItem);
        } catch (KieServicesHttpException kieException) {
            if (kieException.getHttpCode() == NOT_FOUND_ERROR_CODE) {
                return null;
            } else {
                throw kieException;
            }
        }
    }
    
    @Override
    public ProcessInstanceSummary getProcessInstanceByCorrelationKey(String serverTemplateId, String correlationKey) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        QueryServicesClient queryServicesClient = getClient(serverTemplateId,
                                                            QueryServicesClient.class);

        ProcessInstance processInstance = queryServicesClient.findProcessInstanceByCorrelationKey(new CorrelationKey() {
            
            @Override
            public String toExternalForm() {
                return correlationKey;
            }
            
            @Override
            public List<CorrelationProperty<?>> getProperties() {
                return null;
            }
            
            @Override
            public String getName() {
                return correlationKey;
            }
        });

        return new ProcessInstanceSummaryMapper(serverTemplateId).apply(processInstance);
    }

}