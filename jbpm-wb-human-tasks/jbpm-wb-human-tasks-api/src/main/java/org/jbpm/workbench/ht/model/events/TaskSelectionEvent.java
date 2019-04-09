/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.model.events;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TaskSelectionEvent extends AbstractTaskEvent {

    private Boolean forAdmin;
    private Boolean forLog;

    private String description;
    private Date expirationTime;
    private String actualOwner;
    private String status;
    private Integer priority;
    private Long processInstanceId;
    private String processId;
    private Integer slaCompliance;

    public TaskSelectionEvent() {
    }

    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId) {
        super(serverTemplateId, containerId, taskId);
    }

    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId, String taskName,
                              Boolean forAdmin, Boolean forLog) {
        super(serverTemplateId, containerId, taskId, taskName);
        this.forAdmin = forAdmin;
        this.forLog = forLog;
    }

    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId, String taskName, Boolean forAdmin,
                              Boolean forLog, String description, Date expirationTime, String status, String actualOwner,
                              Integer priority, Long processInstanceId, String processId) {
        this(serverTemplateId,containerId,taskId,taskName,forAdmin,forLog);
        this.description = description;
        this.expirationTime = expirationTime;
        this.status = status;
        this.actualOwner = actualOwner;
        this.priority = priority;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
    }
    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId, String taskName, Boolean forAdmin,
                              Boolean forLog, String description, Date expirationTime, String status, String actualOwner,
                              Integer priority, Long processInstanceId, String processId, Integer slaCompliance) {
        this(serverTemplateId,containerId,taskId,taskName,forAdmin,forLog, description,expirationTime, status,actualOwner,
             priority,processInstanceId, processId);
        this.slaCompliance = slaCompliance;
    }

    public String getDescription() {
        return description;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public String getStatus() {
        return status;
    }

    public Integer getSlaCompliance() {
        return slaCompliance;
    }

    public Integer getPriority() {
        return priority;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Boolean isForAdmin() {
        return forAdmin;
    }

    public Boolean isForLog() {
        return forLog;
    }

    @Override
    public String toString() {
        return "TaskSelectionEvent{" +
                "forAdmin=" + forAdmin +
                ", forLog=" + forLog +
                ", description='" + description + '\'' +
                ", expirationTime=" + expirationTime +
                ", actualOwner='" + actualOwner + '\'' +
                ", status='" + status + '\'' +
                ", slaCompliance='" + slaCompliance + '\'' +
                ", priority=" + priority +
                ", processInstanceId=" + processInstanceId +
                ", processId='" + processId + '\'' +
                "} " + super.toString();
    }
}
