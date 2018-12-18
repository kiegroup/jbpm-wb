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
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;

@Remote
public interface ProcessService {

    void abortProcessInstance(ProcessInstanceKey processInstance);

    void abortProcessInstances(String serverTemplateId,
                               Map<String, List<Long>> containerInstances);

    Long startProcess(String serverTemplateId,
                      String containerId,
                      String processId,
                      String correlationKey,
                      Map<String, Object> params);

    List<String> getAvailableSignals(String serverTemplateId,
                                     String containerId,
                                     Long processInstanceId);

    void signalProcessInstances(String serverTemplateId,
                                List<String> containers,
                                List<Long> processInstanceId,
                                String signal,
                                Object event);

    void setProcessVariable(String serverTemplateId,
                            String deploymentId,
                            long processInstanceId,
                            String variableName,
                            String value);
}