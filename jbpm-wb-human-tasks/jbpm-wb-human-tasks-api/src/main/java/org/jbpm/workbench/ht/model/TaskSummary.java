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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class TaskSummary extends GenericSummary {

    private Long taskId;
    private String taskName;
    private String description;
    private String status;
    private int priority;

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
    private Long processSessionId;
    private String deploymentId;
    private boolean isForAdmin;
    private boolean isLogOnly;
    private Long parentId;
    private List<String> potOwnersString = new ArrayList<String>();

    private Map<String, String> domainData = new HashMap<String, String>();

    public TaskSummary(long taskId, String taskName, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, long processSessionId, long processInstanceId, String deploymentId,
            long parentId, Date lastModificationDate, String processInstanceCorrelationKey, String processInstanceDescription) {
        super();
        this.id = taskId;
        this.name = taskName;
        this.taskId = taskId;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.actualOwner = actualOwner;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processId = processId;
        this.processSessionId = processSessionId;
        this.processInstanceId = processInstanceId;
        this.deploymentId = deploymentId;
        this.parentId = parentId;
        this.lastModificationDate = lastModificationDate;
        this.processInstanceCorrelationKey = processInstanceCorrelationKey;
        this.processInstanceDescription = processInstanceDescription;
    }

    public TaskSummary(long taskId, String taskName, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, long processSessionId, long processInstanceId,
            String deploymentId, long parentId, Date lastModificationDate, String processInstanceCorrelationKey,
            String processInstanceDescription, boolean isForAdmin) {
        this(taskId, taskName, description, status, priority,
                actualOwner, createdBy, createdOn, activationTime,
                expirationTime, processId, processSessionId,
                processInstanceId, deploymentId, parentId,
                lastModificationDate, processInstanceCorrelationKey, processInstanceDescription);
        this.isForAdmin = isForAdmin;
    }

    public TaskSummary(long taskId, String taskName, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, long processSessionId, long processInstanceId,
            String deploymentId, long parentId, Date lastModificationDate, String processInstanceCorrelationKey,
            String processInstanceDescription, boolean isForAdmin, boolean isLogOnly) {
        this(taskId, taskName, description, status, priority,
                actualOwner, createdBy, createdOn, activationTime,
                expirationTime, processId, processSessionId,
                processInstanceId, deploymentId, parentId, lastModificationDate,
                processInstanceCorrelationKey, processInstanceDescription, isForAdmin);
        this.isLogOnly = isLogOnly;

    }

    public TaskSummary(long taskId, String taskName, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, long processSessionId, long processInstanceId, String deploymentId,
            long parentId, Date lastModificationDate, String processInstanceCorrelationKey, String processInstanceDescription,
            boolean isForAdmin, List<String> potOwnersString) {
        this(taskId, taskName, description, status, priority,
                actualOwner, createdBy, createdOn, activationTime,
                expirationTime, processId, processSessionId,
                processInstanceId, deploymentId, parentId,
                lastModificationDate, processInstanceCorrelationKey, processInstanceDescription);
        this.isForAdmin = isForAdmin;
        this.potOwnersString.clear();
        this.potOwnersString.addAll(potOwnersString);
    }

    public TaskSummary() {
    }

    public TaskSummary(Long taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }
    
    public void addDomainData(String key, String value){
        domainData.put(key, value);
    }

    public String getDomainDataValue(String key){
        return domainData.get(key);
    }

    public Map<String, String> getDomainData(){
        return domainData;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public long getProcessSessionId() {
        return processSessionId;
    }

    public long getParentId() {
        return parentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public boolean isForAdmin() {
        return isForAdmin;
    }

    public void setForAdmin(boolean isForAdmin) {
        this.isForAdmin = isForAdmin;
    }

    public List<String> getPotOwnersString() {
        return potOwnersString;
    }

    public void setPotOwnersString(List<String> potOwnersString) {
        this.potOwnersString = potOwnersString;
    }

    public boolean isLogOnly() {
        return isLogOnly;
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
        return "TaskSummary {taskId=" + taskId + ", taskName=" + taskName + ", description=" + description + ", status=" + status + ", priority=" + priority + ", actualOwner=" + actualOwner + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", activationTime=" + activationTime + ", expirationTime=" + expirationTime + ", lastModificationDate=" + lastModificationDate + ", processInstanceId=" + processInstanceId + ", processInstanceCorrelationKey=" + processInstanceCorrelationKey + ", processInstanceDescription=" + processInstanceDescription + ", processId=" + processId + ", processSessionId=" + processSessionId + ", deploymentId=" + deploymentId + ", isForAdmin=" + isForAdmin + ", isLogOnly=" + isLogOnly + ", parentId=" + parentId + ", potOwnersString=" + potOwnersString + ", domainData=" + domainData + "}";
    }

}
