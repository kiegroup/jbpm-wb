/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class TaskEventSummary extends GenericSummary {

    private Long eventId;
    private Long taskId;
    private String type;
    private String userId;
    private Date logTime;
    private Long workItemId;
    private String message;

    public TaskEventSummary() {
    }

    public TaskEventSummary(Long eventId, Long taskId, String type, String userId, Long workItemId, Date logTime, String message) {
        this.id = eventId;
        this.name = taskId + type;
        this.eventId = eventId;
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
        this.logTime = logTime;
        this.workItemId = workItemId;
        this.message = message;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TaskEventSummary{" +
                "eventId=" + eventId +
                ", taskId=" + taskId +
                ", type='" + type + '\'' +
                ", userId='" + userId + '\'' +
                ", logTime=" + logTime +
                ", workItemId=" + workItemId +
                ", message='" + message + '\'' +
                '}';
    }
}
