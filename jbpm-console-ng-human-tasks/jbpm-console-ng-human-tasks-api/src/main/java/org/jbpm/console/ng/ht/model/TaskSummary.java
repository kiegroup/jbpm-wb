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
package org.jbpm.console.ng.ht.model;

import java.util.Date;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.jboss.errai.common.client.api.annotations.Portable;

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
    private Long processInstanceId;
    private String processId;
    private int processSessionId;
    private String deploymentId;

    private Long parentId;

    public TaskSummary(long taskId, String taskName, String description, String status,
            int priority, String actualOwner, String createdBy, Date createdOn, Date activationTime,
            Date expirationTime, String processId, int processSessionId, long processInstanceId, String deploymentId, long parentId) {
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
    }

    public TaskSummary() {
    }

  public TaskSummary(Long taskId, String taskName) {
    this.taskId = taskId;
    this.taskName = taskName;
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

    public int getProcessSessionId() {
        return processSessionId;
    }

    public long getParentId() {
        return parentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public String toString() {
        return "TaskSummary [id=" + taskId + ", name=" + taskName + ", description=" + description + ", deploymentId=" + deploymentId
                + ", status=" + status + ", priority=" + priority + ", parentId=" + parentId
                + ", actualOwner=" + actualOwner + ", createdBy=" + createdBy + ", createdOn=" + createdOn
                + ", activationTime=" + activationTime + ", expirationTime=" + expirationTime + ", processInstanceId="
                + processInstanceId + ", processId=" + processId + ", processSessionId=" + processSessionId + "]";
    }

}
