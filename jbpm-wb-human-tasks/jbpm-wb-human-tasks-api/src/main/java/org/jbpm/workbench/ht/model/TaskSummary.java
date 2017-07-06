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
package org.jbpm.workbench.ht.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class TaskSummary extends GenericSummary<Long> {

    private String description;
    private String status;
    private Integer priority;
    private String actualOwner;
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationTime;
    private Date lastModificationDate;
    private Long processInstanceId;
    private String processInstanceCorrelationKey;
    private String processInstanceDescription;
    private String processId;
    private String deploymentId;
    private Boolean isForAdmin = Boolean.FALSE;
    private Boolean isLogOnly = Boolean.FALSE;
    private Long parentId;
    private Map<String, String> domainData = new HashMap<>();

    public static Builder builder() {
        return new Builder();
    }

    public void addDomainData(String key,
                              String value) {
        domainData.put(key,
                       value);
    }

    public String getDomainDataValue(String key) {
        return domainData.get(key);
    }

    public Map<String, String> getDomainData() {
        return domainData;
    }

    public void setDomainData(Map<String, String> domainData) {
        this.domainData = domainData;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public boolean isForAdmin() {
        return isForAdmin;
    }

    public void setForAdmin(Boolean forAdmin) {
        this.isForAdmin = forAdmin;
    }

    public boolean isLogOnly() {
        return isLogOnly;
    }

    public void setLogOnly(Boolean logOnly) {
        isLogOnly = logOnly;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getProcessInstanceCorrelationKey() {
        return processInstanceCorrelationKey;
    }

    public void setProcessInstanceCorrelationKey(String processInstanceCorrelationKey) {
        this.processInstanceCorrelationKey = processInstanceCorrelationKey;
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }

    @Override
    public String toString() {
        return "TaskSummary{" +
                "description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", actualOwner='" + actualOwner + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                ", activationTime=" + activationTime +
                ", expirationTime=" + expirationTime +
                ", lastModificationDate=" + lastModificationDate +
                ", processInstanceId=" + processInstanceId +
                ", processInstanceCorrelationKey='" + processInstanceCorrelationKey + '\'' +
                ", processInstanceDescription='" + processInstanceDescription + '\'' +
                ", processId='" + processId + '\'' +
                ", deploymentId='" + deploymentId + '\'' +
                ", isForAdmin=" + isForAdmin +
                ", isLogOnly=" + isLogOnly +
                ", parentId=" + parentId +
                ", domainData=" + domainData +
                "} " + super.toString();
    }

    @NonPortable
    public static final class Builder {

        private TaskSummary taskSummary = new TaskSummary();

        private Builder() {
        }

        public TaskSummary build() {
            return taskSummary;
        }

        public Builder id(Long taskId) {
            this.taskSummary.setId(taskId);
            return this;
        }

        public Builder name(String taskName) {
            this.taskSummary.setName(taskName);
            return this;
        }

        public Builder description(String description) {
            this.taskSummary.setDescription(description);
            return this;
        }

        public Builder status(String status) {
            this.taskSummary.setStatus(status);
            return this;
        }

        public Builder priority(Integer priority) {
            this.taskSummary.setPriority(priority);
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            this.taskSummary.setActualOwner(actualOwner);
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.taskSummary.setCreatedBy(createdBy);
            return this;
        }

        public Builder createdOn(Date createdOn) {
            this.taskSummary.setCreatedOn(createdOn);
            return this;
        }

        public Builder activationTime(Date activationTime) {
            this.taskSummary.setActivationTime(activationTime);
            return this;
        }

        public Builder expirationTime(Date expirationTime) {
            this.taskSummary.setExpirationTime(expirationTime);
            return this;
        }

        public Builder lastModificationDate(Date lastModificationDate) {
            this.taskSummary.setLastModificationDate(lastModificationDate);
            return this;
        }

        public Builder processInstanceId(Long processInstanceId) {
            this.taskSummary.setProcessInstanceId(processInstanceId);
            return this;
        }

        public Builder processInstanceCorrelationKey(String processInstanceCorrelationKey) {
            this.taskSummary.setProcessInstanceCorrelationKey(processInstanceCorrelationKey);
            return this;
        }

        public Builder processInstanceDescription(String processInstanceDescription) {
            this.taskSummary.setProcessInstanceDescription(processInstanceDescription);
            return this;
        }

        public Builder processId(String processId) {
            this.taskSummary.setProcessId(processId);
            return this;
        }

        public Builder deploymentId(String deploymentId) {
            this.taskSummary.setDeploymentId(deploymentId);
            return this;
        }

        public Builder isForAdmin(Boolean isForAdmin) {
            this.taskSummary.setForAdmin(isForAdmin);
            return this;
        }

        public Builder isLogOnly(Boolean isLogOnly) {
            this.taskSummary.setLogOnly(isLogOnly);
            return this;
        }

        public Builder parentId(Long parentId) {
            this.taskSummary.setParentId(parentId);
            return this;
        }

        public Builder domainData(Map<String, String> domainData) {
            this.taskSummary.setDomainData(domainData);
            return this;
        }
    }
}
