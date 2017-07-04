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

package org.jbpm.workbench.pr.service;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.model.RuntimeLogSummary;
import org.jbpm.workbench.pr.model.TaskDefSummary;

@Remote
public interface ProcessRuntimeDataService {

    ProcessInstanceSummary getProcessInstance(String serverTemplateId,
                                              ProcessInstanceKey processInstanceKey);

    List<NodeInstanceSummary> getProcessInstanceActiveNodes(String serverTemplateId,
                                                            String deploymentId,
                                                            Long processInstanceId);

    List<RuntimeLogSummary> getRuntimeLogs(String serverTemplateId,
                                           String deploymentId,
                                           Long processInstanceId);

    List<RuntimeLogSummary> getBusinessLogs(String serverTemplateId,
                                            String deploymentId,
                                            String processName,
                                            Long processInstanceId);

    List<ProcessSummary> getProcesses(String serverTemplateId,
                                      Integer page,
                                      Integer pageSize,
                                      String sort,
                                      Boolean sortOrder);

    ProcessSummary getProcess(String serverTemplateId,
                              ProcessDefinitionKey processDefinitionKey);

    List<ProcessSummary> getProcessesByFilter(String serverTemplateId,
                                              String textSearch,
                                              Integer page,
                                              Integer pageSize,
                                              String sort,
                                              Boolean sortOrder);

    List<TaskDefSummary> getProcessUserTasks(String serverTemplateId,
                                             String containerId,
                                             String processId);
}