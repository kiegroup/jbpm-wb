/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;

@Portable
public class ProcessInstanceSelectionEvent {

    private ProcessInstanceKey processInstanceKey;
    private ProcessDefinitionKey processDefinitionKey;
    private Integer processInstanceStatus;
    private boolean forLog;

    public ProcessInstanceSelectionEvent() {
    }

    public ProcessInstanceSelectionEvent(ProcessInstanceKey processInstanceKey,
                                         ProcessDefinitionKey processDefinitionKey,
                                         Integer processInstanceStatus,
                                         boolean forLog) {
        this.processInstanceKey = processInstanceKey;
        this.processDefinitionKey = processDefinitionKey;
        this.processInstanceStatus = processInstanceStatus;
        this.forLog = forLog;
    }

    public ProcessInstanceSelectionEvent(String deploymentId,
                                         Long processInstanceId,
                                         String processDefId,
                                         String processDefName,
                                         Integer processInstanceStatus,
                                         String serverTemplateId) {
        this(new ProcessInstanceKey(serverTemplateId,
                                    deploymentId,
                                    processInstanceId),
             new ProcessDefinitionKey(serverTemplateId,
                                      deploymentId,
                                      processDefId,
                                      processDefName),
             processInstanceStatus,
             false);
    }

    public ProcessInstanceSelectionEvent(String deploymentId,
                                         Long processInstanceId,
                                         String processDefId,
                                         String processDefName,
                                         Integer processInstanceStatus,
                                         boolean forLog,
                                         String serverTemplateId) {

        this(new ProcessInstanceKey(serverTemplateId,
                                    deploymentId,
                                    processInstanceId),
             new ProcessDefinitionKey(serverTemplateId,
                                      deploymentId,
                                      processDefId,
                                      processDefName),
             processInstanceStatus,
             forLog);
    }

    public Long getProcessInstanceId() {
        return processInstanceKey.getProcessInstanceId();
    }

    public String getProcessDefId() {
        return processDefinitionKey.getProcessId();
    }

    public String getDeploymentId() {
        return processInstanceKey.getDeploymentId();
    }

    public Integer getProcessInstanceStatus() {
        return processInstanceStatus;
    }

    public String getProcessDefName() {
        return processDefinitionKey.getProcessDefName();
    }

    public String getServerTemplateId() {
        return processInstanceKey.getServerTemplateId();
    }

    public ProcessInstanceKey getProcessInstanceKey() {
        return processInstanceKey;
    }

    public ProcessDefinitionKey getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public boolean isForLog() {
        return forLog;
    }

    @Override
    public String toString() {
        return "ProcessInstanceSelectionEvent{" +
                "processInstanceKey=" + processInstanceKey +
                ", processDefinitionKey=" + processDefinitionKey +
                ", processInstanceStatus=" + processInstanceStatus +
                ", forLog=" + forLog +
                '}';
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.processInstanceKey != null ? this.processInstanceKey.hashCode() : 0);
        hash = ~~hash;
        hash = 37 * hash + (this.processDefinitionKey != null ? this.processDefinitionKey.hashCode() : 0);
        hash = ~~hash;
        hash = 37 * hash + (this.processInstanceStatus != null ? this.processInstanceStatus.hashCode() : 0);
        hash = ~~hash;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProcessInstanceSelectionEvent)) {
            return false;
        }

        ProcessInstanceSelectionEvent that = (ProcessInstanceSelectionEvent) o;

        return getProcessInstanceKey().equals(that.getProcessInstanceKey());
    }
}
