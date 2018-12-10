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

package org.jbpm.workbench.pr.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
@Bindable
public class NodeInstanceSummary extends GenericSummary<Long> {

    private Long processId;
    private String nodeUniqueName;
    private String type;
    private Date timestamp;
    private String connection;
    private Boolean completed;
    private Long referenceId;
    private Integer slaCompliance;
    private Date slaDueDate;
    private String description;

    public NodeInstanceSummary(Long id,
                               Long processId,
                               String nodeName,
                               String nodeUniqueName,
                               String type,
                               Date timestamp,
                               String connection,
                               Boolean completed,
                               Long referenceId,
                               Integer slaCompliance,
                               Date slaDueDate) {
        super(id,
              nodeName);
        this.processId = processId;
        this.nodeUniqueName = nodeUniqueName;
        this.type = type;
        this.timestamp = timestamp;
        this.connection = connection;
        this.completed = completed;
        this.referenceId = referenceId;
        this.slaCompliance = slaCompliance;
        this.slaDueDate = slaDueDate;
    }

    public NodeInstanceSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeUniqueName() {
        return nodeUniqueName;
    }

    public void setNodeUniqueName(String nodeUniqueName) {
        this.nodeUniqueName = nodeUniqueName;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String incomingConnection) {
        this.connection = incomingConnection;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getLabel() {
        return getId() + "-" + (getName() == null || getName().trim().isEmpty() ? getType() : getName());
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getSlaCompliance() {
        return slaCompliance;
    }

    public void setSlaCompliance(Integer slaCompliance) {
        this.slaCompliance = slaCompliance;
    }

    public Date getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(Date slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "NodeInstanceSummary{" +
                "processId=" + processId +
                ", nodeUniqueName='" + nodeUniqueName + '\'' +
                ", type='" + type + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", connection='" + connection + '\'' +
                ", completed=" + completed +
                ", referenceId=" + referenceId +
                ", slaCompliance=" + slaCompliance +
                ", slaDueDate=" + slaDueDate +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @NonPortable
    public static final class Builder {

        private NodeInstanceSummary nodeInstanceSummary;

        private Builder() {
            nodeInstanceSummary = new NodeInstanceSummary();
        }

        public Builder withId(Long id) {
            nodeInstanceSummary.setId(id);
            return this;
        }

        public Builder withName(String name) {
            nodeInstanceSummary.setName(name);
            return this;
        }

        public Builder withProcessId(Long processId) {
            nodeInstanceSummary.setProcessId(processId);
            return this;
        }

        public Builder withNodeUniqueName(String nodeUniqueName) {
            nodeInstanceSummary.setNodeUniqueName(nodeUniqueName);
            return this;
        }

        public Builder withType(String type) {
            nodeInstanceSummary.setType(type);
            return this;
        }

        public Builder withTimestamp(Date timestamp) {
            nodeInstanceSummary.setTimestamp(timestamp);
            return this;
        }

        public Builder withConnection(String connection) {
            nodeInstanceSummary.setConnection(connection);
            return this;
        }

        public Builder withCompleted(Boolean completed) {
            nodeInstanceSummary.setCompleted(completed);
            return this;
        }

        public Builder withReferenceId(Long referenceId) {
            nodeInstanceSummary.setReferenceId(referenceId);
            return this;
        }

        public Builder withSlaCompliance(Integer slaCompliance) {
            nodeInstanceSummary.setSlaCompliance(slaCompliance);
            return this;
        }

        public Builder withSlaDueDate(Date slaDueDate) {
            nodeInstanceSummary.setSlaDueDate(slaDueDate);
            return this;
        }

        public NodeInstanceSummary build() {
            return nodeInstanceSummary;
        }
    }
}
