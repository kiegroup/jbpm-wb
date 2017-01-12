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

package org.jbpm.console.ng.ga.forms.service.providing.model;

import java.util.Map;

public class TaskDefinition {

    private Long id;
    private String name;
    private String description;
    private String processId;
    private String deploymentId;
    private String status;
    private String formName;
    private boolean outputIncluded;
    private Map<String, String> taskInputDefinitions;
    private Map<String, String> taskOutputDefinitions;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public boolean isOutputIncluded() {
        return outputIncluded;
    }

    public void setOutputIncluded(boolean outputIncluded) {
        this.outputIncluded = outputIncluded;
    }

    public void setTaskInputDefinitions( Map<String, String> taskInstanceDefinitions ) {
        this.taskInputDefinitions = taskInstanceDefinitions;
    }

    public Map<String, String> getTaskInputDefinitions() {
        return taskInputDefinitions;
    }

    public void setTaskOutputDefinitions( Map<String, String> taskOutputDefinitions ) {
        this.taskOutputDefinitions = taskOutputDefinitions;
    }

    public Map<String, String> getTaskOutputDefinitions() {
        return taskOutputDefinitions;
    }
}
