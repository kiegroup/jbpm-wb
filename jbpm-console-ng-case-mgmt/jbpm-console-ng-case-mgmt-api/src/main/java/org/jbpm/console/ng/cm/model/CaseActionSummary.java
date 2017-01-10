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

package org.jbpm.console.ng.cm.model;

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class CaseActionSummary {

    private Long id;
    private String name;
    private String subject;
    private String description;
    private String status;
    private Integer priority;
    private Boolean skipable;
    private String actualOwner;
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationTime;
    private Long processInstanceId;
    private String processId;
    private String containerId;
    private Long parentId;

    public CaseActionSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Boolean getSkipable() {
        return skipable;
    }

    public void setSkipable(Boolean skipable) {
        this.skipable = skipable;
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

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseActionSummary that = (CaseActionSummary) o;
        return Objects.equals(id, that.getId());
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseActionSummary{" +
                " id='" + id + '\'' +
                " name='" + name + '\'' +
                " subject='" + subject + '\'' +
                " description='" + description + '\'' +
                " status='" + status + '\'' +
                " priority='" + priority + '\'' +
                " skipable='" + skipable + '\'' +
                " actualOwner='" + actualOwner + '\'' +
                " createdBy='" + createdBy + '\'' +
                " createdOn='" + createdOn + '\'' +
                " activationTime='" + activationTime + '\'' +
                " expirationTime='" + expirationTime + '\'' +
                " processInstanceId='" + processInstanceId + '\'' +
                " processId='" + processId  + '\'' +
                " containerId='" + containerId + '\'' +
                " parentId='" + parentId +
                '}';
    }

    public static class Builder {

        private CaseActionSummary caseActionSummary = new CaseActionSummary();

        public CaseActionSummary build() {
            return caseActionSummary;
        }

        public Builder id(final Long id) {
            caseActionSummary.setId(id);
            return this;
        }

        public Builder name(final String name) {
            caseActionSummary.setName(name);
            return this;
        }

        public Builder subject(String subject) {
            caseActionSummary.setSubject(subject);
            return this;
        }

        public Builder description(String description) {
            caseActionSummary.setDescription(description);
            return this;
        }

        public Builder status(String status) {
            caseActionSummary.setStatus(status);
            return this;
        }
        public Builder priority(Integer priority) {
            caseActionSummary.setPriority(priority);
            return this;
        }

        public Builder skipable(Boolean skipable) {
            caseActionSummary.setSkipable(skipable);
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            caseActionSummary.setActualOwner(actualOwner);
            return this;
        }

        public Builder createdBy(String createdBy) {
            caseActionSummary.setCreatedBy(createdBy);
            return this;
        }

        public Builder createdOn(Date createdOn) {
            caseActionSummary.setCreatedOn(createdOn);
            return this;
        }

        public Builder activationTime(Date activationTime) {
            caseActionSummary.setActivationTime(activationTime);
            return this;
        }

        public Builder expirationTime (Date expirationTime) {
            caseActionSummary.setExpirationTime(expirationTime);
            return this;
        }

        public Builder processInstanceId(Long processInstanceId) {
            caseActionSummary.setProcessInstanceId(processInstanceId);
            return this;
        }

        public Builder processId( String processId) {
            caseActionSummary.setProcessId(processId);
            return this;
        }

        public Builder containerId( String containerId) {
            caseActionSummary.setContainerId(containerId);
            return this;
        }

        public Builder parentId( Long parentId) {
            caseActionSummary.setParentId(parentId);
            return this;
        }


    }

}