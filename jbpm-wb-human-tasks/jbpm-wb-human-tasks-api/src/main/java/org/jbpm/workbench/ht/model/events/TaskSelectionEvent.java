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

package org.jbpm.workbench.ht.model.events;

import java.io.Serializable;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TaskSelectionEvent implements Serializable {
    private String serverTemplateId;
    private String containerId;
    private Long taskId;
    private String taskName;
    private boolean forAdmin;
    private boolean forLog;

    public TaskSelectionEvent() {
    }

    public TaskSelectionEvent(Long taskId) {
        this.taskId = taskId;
    }

    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId, String taskName) {
        this.serverTemplateId = serverTemplateId;
        this.containerId = containerId;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public TaskSelectionEvent(String serverTemplateId, String containerId, Long taskId, String taskName,  boolean forAdmin, boolean forLog) {
        this.serverTemplateId = serverTemplateId;
        this.containerId = containerId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.forAdmin = forAdmin;
        this.forLog = forLog;
    }
    
    public String getTaskName() {
        return taskName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public boolean isForAdmin() {
        return forAdmin;
    }

    public boolean isForLog() {
        return forLog;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public String getContainerId() {
        return containerId;
    }
}
