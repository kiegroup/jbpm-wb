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
public class TaskRefreshedEvent implements Serializable {

    private String serverTemplateId;
    private String deploymentId;
    private Long taskId;
    private String taskName;

    public TaskRefreshedEvent(long taskId,
                              String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public TaskRefreshedEvent(long taskId) {
        this.taskId = taskId;
    }

    public TaskRefreshedEvent(String serverTemplateId,
                              String deploymentId,
                              Long taskId) {
        this.serverTemplateId = serverTemplateId;
        this.deploymentId = deploymentId;
        this.taskId = taskId;
    }

    public TaskRefreshedEvent() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }
}
