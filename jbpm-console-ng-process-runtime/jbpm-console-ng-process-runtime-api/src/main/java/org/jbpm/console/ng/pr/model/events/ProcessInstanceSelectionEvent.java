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
package org.jbpm.console.ng.pr.model.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProcessInstanceSelectionEvent {

    private Long processInstanceId;
    private String processDefId;
    private String deploymentId;
    private Integer processInstanceStatus;
    private String processDefName;
    private boolean forLog;

    public ProcessInstanceSelectionEvent() {
    }

    public ProcessInstanceSelectionEvent(String deploymentId, Long processInstanceId, String processDefId, String processDefName, Integer processInstanceStatus) {
        this.processInstanceId = processInstanceId;
        this.processDefId = processDefId;
        this.deploymentId = deploymentId;
        this.processInstanceStatus = processInstanceStatus;
        this.processDefName = processDefName;
        this.forLog = false;
    }

    public ProcessInstanceSelectionEvent(String deploymentId, Long processInstanceId, String processDefId,
                                         String processDefName, Integer processInstanceStatus, boolean forLog) {
        this(deploymentId, processInstanceId, processDefId, processDefName, processInstanceStatus);
        this.forLog = forLog;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Integer getProcessInstanceStatus() {
        return processInstanceStatus;
    }

    public String getProcessDefName() {
        return processDefName;
    }

    @Override
    public String toString() {
        return "ProcessInstanceSelectionEvent{" + "processInstanceId=" + processInstanceId + ", processDefId=" + processDefId + ", deploymentId=" + deploymentId + ", processInstanceStatus=" + processInstanceStatus + ", processDefName=" + processDefName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
        hash = 37 * hash + (this.processDefId != null ? this.processDefId.hashCode() : 0);
        hash = 37 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
        hash = 37 * hash + (this.processInstanceStatus != null ? this.processInstanceStatus.hashCode() : 0);
        hash = 37 * hash + (this.processDefName != null ? this.processDefName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessInstanceSelectionEvent other = (ProcessInstanceSelectionEvent) obj;
        if (this.processInstanceId != other.processInstanceId && (this.processInstanceId == null || !this.processInstanceId.equals(other.processInstanceId))) {
            return false;
        }
        if ((this.processDefId == null) ? (other.processDefId != null) : !this.processDefId.equals(other.processDefId)) {
            return false;
        }
        if ((this.deploymentId == null) ? (other.deploymentId != null) : !this.deploymentId.equals(other.deploymentId)) {
            return false;
        }
        if (this.processInstanceStatus != other.processInstanceStatus && (this.processInstanceStatus == null || !this.processInstanceStatus.equals(other.processInstanceStatus))) {
            return false;
        }
        if ((this.processDefName == null) ? (other.processDefName != null) : !this.processDefName.equals(other.processDefName)) {
            return false;
        }
        return true;
    }

    public boolean isForLog() {
        return forLog;
    }

}
