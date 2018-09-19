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
package org.jbpm.workbench.pr.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class ProcessInstanceLogSummary extends GenericSummary<Long> {

    private Date date;
    private String nodeId;
    private String nodeType;
    private boolean completed;
    private Long workItemId;
    private Long referenceId;
    private String nodeContainerId;
    private Long logType;
    private String logDeploymentId;

    public static Builder builder() {
        return new Builder();
    }

    public ProcessInstanceLogSummary() {
    }

    public ProcessInstanceLogSummary(Long id,
                                     Date date,
                                     String nodeName,
                                     String nodeType,
                                     boolean completed) {
        super(id,
              nodeName);
        this.date = date;
        this.nodeType = nodeType;
        this.completed = completed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getNodeContainerId() {
        return nodeContainerId;
    }

    public void setNodeContainerId(String nodeContainerId) {
        this.nodeContainerId = nodeContainerId;
    }

    public Long getLogType() {
        return logType;
    }

    public void setLogType(Long logType) {
        this.logType = logType;
    }

    public String getLogDeploymentId() {
        return logDeploymentId;
    }

    public void setLogDeploymentId(String logDeploymentId) {
        this.logDeploymentId = logDeploymentId;
    }

    @NonPortable
    public static final class Builder {

        private ProcessInstanceLogSummary processInstanceLogSummary = new ProcessInstanceLogSummary();

        private Builder() {
        }

        public ProcessInstanceLogSummary build() {
            return processInstanceLogSummary;
        }

        public Builder id(Long id) {
            processInstanceLogSummary.setId(id);
            return this;
        }

        public Builder name(String name) {
            processInstanceLogSummary.setName(name);
            return this;
        }

        public Builder date(Date nodeDate) {
            processInstanceLogSummary.setDate(nodeDate);
            return this;
        }

        public Builder nodeId(String nodeId) {
            processInstanceLogSummary.setNodeId(nodeId);
            return this;
        }

        public Builder nodeType(String nodeType) {
            processInstanceLogSummary.setNodeType(nodeType);
            return this;
        }

        public Builder completed(boolean completed) {
            processInstanceLogSummary.setCompleted(completed);
            return this;
        }

        public Builder workItemId(Long workItemId) {
            processInstanceLogSummary.setWorkItemId(workItemId);
            return this;
        }

        public Builder referenceId(Long referenceId) {
            processInstanceLogSummary.setReferenceId(referenceId);
            return this;
        }

        public Builder nodeContainerId(String nodeContainerId) {
            processInstanceLogSummary.setNodeContainerId(nodeContainerId);
            return this;
        }

        public Builder logType(Long logType) {
            processInstanceLogSummary.setLogType(logType);
            return this;
        }

        public Builder logDeploymentId(String logDeploymentId) {
            processInstanceLogSummary.setLogDeploymentId(logDeploymentId);
            return this;
        }
    }

    @Override
    public String toString() {
        return "ProcessInstanceLogSummary{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", date=" + date +
                ", nodeId='" + nodeId + '\'' +
                ", processType='" + nodeType + '\'' +
                ", completed='" + completed + '\'' +
                ", workItemId=" + workItemId +
                ", referenceId=" + referenceId +
                ", nodeContainerId=" + nodeContainerId +
                ", logType='" + logType + '\'' +
                ", logDeploymentId=" + logDeploymentId +
                "} " + super.toString();
    }
}
