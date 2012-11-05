/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.client.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TaskSummary
        implements
        Serializable {

    private long id;
    private String name;
    private String subject;
    private String description;
    // Was Status
    private String status;
    private int priority;
    private int parentId;
    private boolean skipable;
    //Was User
    private String actualOwner;
    //Was User
    private String createdBy;
    private Date createdOn;
    private Date activationTime;
    private Date expirationTime;
    private long processInstanceId;
    private String processId;
    private int processSessionId;
    private String subTaskStrategy;
    
    private boolean isGroupTask;
    private List<String> potentialOwners;

    public TaskSummary(long id,
            long processInstanceId,
            String name,
            String subject,
            String description,
            String status,
            int priority,
            boolean skipable,
            String actualOwner,
            String createdBy,
            Date createdOn,
            Date activationTime,
            Date expirationTime,
            String processId,
            int processSessionId,
            String subTaskStrategy,
            int parentId, List<String> potentialOwners) {
        super();
        this.id = id;
        this.processInstanceId = processInstanceId;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.skipable = skipable;
        this.actualOwner = actualOwner;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processId = processId;
        this.processSessionId = processSessionId;
        this.subTaskStrategy = subTaskStrategy;
        this.parentId = parentId;
        this.potentialOwners = potentialOwners;
    }
    

    public TaskSummary() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean skipable) {
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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getProcessSessionId() {
        return processSessionId;
    }

    public void setProcessSessionId(int processSessionId) {
        this.processSessionId = processSessionId;
    }

    public String getSubTaskStrategy() {
        return subTaskStrategy;
    }

    public void setSubTaskStrategy(String subTaskStrategy) {
        this.subTaskStrategy = subTaskStrategy;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<String> getPotentialOwners() {
        return potentialOwners;
    }

    public void setPotentialOwners(List<String> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 11 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 11 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 11 * hash + this.priority;
        hash = 11 * hash + this.parentId;
        hash = 11 * hash + (this.skipable ? 1 : 0);
        hash = 11 * hash + (this.actualOwner != null ? this.actualOwner.hashCode() : 0);
        hash = 11 * hash + (this.createdBy != null ? this.createdBy.hashCode() : 0);
        hash = 11 * hash + (this.createdOn != null ? this.createdOn.hashCode() : 0);
        hash = 11 * hash + (this.activationTime != null ? this.activationTime.hashCode() : 0);
        hash = 11 * hash + (this.expirationTime != null ? this.expirationTime.hashCode() : 0);
        hash = 11 * hash + (int) (this.processInstanceId ^ (this.processInstanceId >>> 32));
        hash = 11 * hash + (this.processId != null ? this.processId.hashCode() : 0);
        hash = 11 * hash + this.processSessionId;
        hash = 11 * hash + (this.subTaskStrategy != null ? this.subTaskStrategy.hashCode() : 0);
        hash = 11 * hash + (this.isGroupTask ? 1 : 0);
        hash = 11 * hash + (this.potentialOwners != null ? this.potentialOwners.hashCode() : 0);
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
        final TaskSummary other = (TaskSummary) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.subject == null) ? (other.subject != null) : !this.subject.equals(other.subject)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) : !this.status.equals(other.status)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if (this.parentId != other.parentId) {
            return false;
        }
        if (this.skipable != other.skipable) {
            return false;
        }
        if ((this.actualOwner == null) ? (other.actualOwner != null) : !this.actualOwner.equals(other.actualOwner)) {
            return false;
        }
        if ((this.createdBy == null) ? (other.createdBy != null) : !this.createdBy.equals(other.createdBy)) {
            return false;
        }
        if (this.createdOn != other.createdOn && (this.createdOn == null || !this.createdOn.equals(other.createdOn))) {
            return false;
        }
        if (this.activationTime != other.activationTime && (this.activationTime == null || !this.activationTime.equals(other.activationTime))) {
            return false;
        }
        if (this.expirationTime != other.expirationTime && (this.expirationTime == null || !this.expirationTime.equals(other.expirationTime))) {
            return false;
        }
        if (this.processInstanceId != other.processInstanceId) {
            return false;
        }
        if ((this.processId == null) ? (other.processId != null) : !this.processId.equals(other.processId)) {
            return false;
        }
        if (this.processSessionId != other.processSessionId) {
            return false;
        }
        if ((this.subTaskStrategy == null) ? (other.subTaskStrategy != null) : !this.subTaskStrategy.equals(other.subTaskStrategy)) {
            return false;
        }
        if (this.isGroupTask != other.isGroupTask) {
            return false;
        }
        if (this.potentialOwners != other.potentialOwners && (this.potentialOwners == null || !this.potentialOwners.equals(other.potentialOwners))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TaskSummary{" + "id=" + id + ", name=" + name + ", subject=" + subject + ", description=" + description + ", status=" + status + ", priority=" + priority + ", parentId=" + parentId + ", skipable=" + skipable + ", actualOwner=" + actualOwner + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", activationTime=" + activationTime + ", expirationTime=" + expirationTime + ", processInstanceId=" + processInstanceId + ", processId=" + processId + ", processSessionId=" + processSessionId + ", subTaskStrategy=" + subTaskStrategy + ", isGroupTask=" + isGroupTask + ", potentialOwners=" + potentialOwners + '}';
    }
    
    

    
    



    
}
