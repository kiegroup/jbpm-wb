/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 * @author salaboy
 */

@Portable
public class TaskEventSummary implements Serializable {
    private Long id;
    private Long taskId;
    private String type;
    private String userId;
    private Date logTime;

    public TaskEventSummary() {
    }

    public TaskEventSummary(Long id, Long taskId, String type, String userId, Date logTime) {
        this.id = id;
        this.taskId = taskId;
        this.type = type;
        this.userId = userId;
        this.logTime = logTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    @Override
    public String toString() {
        return "TaskEventSummary{" + "id=" + id + ", taskId=" + taskId + ", type=" + type + ", userId=" + userId + ", logTime=" + logTime + '}';
    }
    
}
