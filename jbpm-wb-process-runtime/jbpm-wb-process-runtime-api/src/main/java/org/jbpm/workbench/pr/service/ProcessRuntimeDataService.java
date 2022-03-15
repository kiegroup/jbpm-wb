/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.workbench.pr.model.*;

@Remote
public interface ProcessRuntimeDataService {

    ProcessInstanceSummary getProcessInstance(ProcessInstanceKey processInstanceKey);

    List<NodeInstanceSummary> getProcessInstanceActiveNodes(ProcessInstanceKey processInstanceKey);

    List<NodeInstanceSummary> getProcessInstanceCompletedNodes(ProcessInstanceKey processInstanceKey);

    List<TimerInstanceSummary> getProcessInstanceTimerInstances(ProcessInstanceKey processInstanceKey);

    ProcessInstanceDiagramSummary getProcessInstanceDiagramSummary(ProcessInstanceKey processInstanceKey,
                                                                   String completedNodeColor,
                                                                   String completedNodeBorderColor,
                                                                   String activeNodeBorderColor,
                                                                   String activeAsyncNodeBorderColor);

    List<ProcessSummary> getProcesses(String serverTemplateId,
                                      Integer page,
                                      Integer pageSize,
                                      String sort,
                                      Boolean sortOrder);

    ProcessSummary getProcess(ProcessDefinitionKey processDefinitionKey);

    List<ProcessSummary> getProcessesByFilter(String serverTemplateId,
                                              String textSearch,
                                              Integer page,
                                              Integer pageSize,
                                              String sort,
                                              Boolean sortOrder);

    List<TaskDefSummary> getProcessUserTasks(String serverTemplateId,
                                             String containerId,
                                             String processId);

    WorkItemSummary getWorkItemByProcessInstanceId(String serverTemplateId,
                                                   String containerId,
                                                   Long processInstanceId,
                                                   Long workItemId);

    void triggerProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                    Long nodeId);

    void cancelProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                   Long nodeInstanceId);

    void reTriggerProcessInstanceNode(ProcessInstanceKey processInstanceKey,
                                      Long nodeInstanceId);

    void rescheduleTimerInstance(ProcessInstanceKey processInstanceKey,
                                 TimerInstanceSummary summary);

    ProcessInstanceSummary getProcessInstanceByCorrelationKey(String serverTemplateId,
                                                              String correlationKey);                                 
}
