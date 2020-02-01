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
import org.jbpm.workbench.pr.model.ProcessInstanceKey;

@Portable
public class ProcessInstanceSelectionEvent {

    private ProcessInstanceKey processInstanceKey;
    private boolean forLog;
    private boolean isFromDiagram = false;

    public ProcessInstanceSelectionEvent() {
    }

    public ProcessInstanceSelectionEvent(ProcessInstanceKey processInstanceKey,
                                         boolean forLog) {
        this.processInstanceKey = processInstanceKey;
        this.forLog = forLog;
    }

    public ProcessInstanceSelectionEvent(String serverTemplateId,
                                         String deploymentId,
                                         Long processInstanceId,
                                         boolean forLog) {

        this(new ProcessInstanceKey(serverTemplateId,
                                    deploymentId,
                                    processInstanceId),
             forLog);
    }

    public ProcessInstanceSelectionEvent(ProcessInstanceKey processInstanceKey,
                                         boolean forLog,
                                         boolean isFromDiagram) {

        this.processInstanceKey = processInstanceKey;
        this.forLog = forLog;
        this.isFromDiagram = isFromDiagram;
    }

    public boolean isFromDiagram() {
        return isFromDiagram;
    }

    public Long getProcessInstanceId() {
        return processInstanceKey.getProcessInstanceId();
    }

    public String getDeploymentId() {
        return processInstanceKey.getDeploymentId();
    }

    public String getServerTemplateId() {
        return processInstanceKey.getServerTemplateId();
    }

    public ProcessInstanceKey getProcessInstanceKey() {
        return processInstanceKey;
    }

    public boolean isForLog() {
        return forLog;
    }

    @Override
    public String toString() {
        return "ProcessInstanceSelectionEvent{" +
                "processInstanceKey=" + processInstanceKey +
                ", forLog=" + forLog +
                '}';
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.processInstanceKey != null ? this.processInstanceKey.hashCode() : 0);
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
