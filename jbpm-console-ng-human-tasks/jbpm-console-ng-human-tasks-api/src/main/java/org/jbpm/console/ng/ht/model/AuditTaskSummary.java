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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class AuditTaskSummary extends GenericSummary {

  private static final long serialVersionUID = -506604206868228075L;
  

  private Long taskId;

  private String status;
  private Date activationTime;
  private String taskName;
  private String description;
  private int priority;
  private String createdBy;
  private String actualOwner;
  private Date createdOn;
  private Date dueDate;
  private Long processInstanceId;
  private String processId;
  private Long processSessionId;
  private Long parentId;
  private String deploymentId;

  public AuditTaskSummary() {
  }

  
  
  public AuditTaskSummary( Long taskId, String status, Date activationTime,
          String name, String description, int priority, String createdBy, 
          String actualOwner, Date createdOn, Date dueDate, Long processInstanceId, 
          String processId, Long processSessionId, Long parentId, String deploymentId) {
    this.id = taskId;
    this.name = name;
    this.taskId = taskId;
    this.status = status;
    this.activationTime = activationTime;
    this.taskName = name;
    this.description = description;
    this.priority = priority;
    this.createdBy = createdBy;
    this.actualOwner = actualOwner;
    this.createdOn = createdOn;
    this.dueDate = dueDate;
    this.processInstanceId = processInstanceId;
    this.processId = processId;
    this.processSessionId = processSessionId;
    this.parentId = parentId;
    this.deploymentId = deploymentId;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  

  public Long getTaskId() {
    return taskId;
  }

  public String getStatus() {
    return status;
  }

  public Date getActivationTime() {
    return activationTime;
  }

  public String getTaskName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getPriority() {
    return priority;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getActualOwner() {
    return actualOwner;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public Long getProcessInstanceId() {
    return processInstanceId;
  }

  public String getProcessId() {
    return processId;
  }

  public long getProcessSessionId() {
    return processSessionId;
  }

  public Long getParentId() {
    return parentId;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  @Override
  public String toString() {
    return "TaskAuditSummary{ taskId=" + taskId + ", status=" + status + ", activationTime=" + activationTime + ", name=" + name + ", description=" + description + ", priority=" + priority + ", createdBy=" + createdBy + ", actualOwner=" + actualOwner + ", createdOn=" + createdOn + ", dueDate=" + dueDate + ", processInstanceId=" + processInstanceId + ", processId=" + processId + ", processSessionId=" + processSessionId + ", parentId=" + parentId + ", deploymentId=" + deploymentId + '}';
  }

  
}
