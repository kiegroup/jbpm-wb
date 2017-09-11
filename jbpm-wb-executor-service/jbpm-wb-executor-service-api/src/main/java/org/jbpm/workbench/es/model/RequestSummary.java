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

package org.jbpm.workbench.es.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.es.util.RequestStatus;

@Portable
public class RequestSummary extends GenericSummary<Long> {

    private Long jobId;
    private Date time;
    private RequestStatus status;
    private String commandName;
    private String message;
    // Business Key for callback
    private String key;
    // Number of times that this request must be retried
    private Integer retries = 0;
    // Number of times that this request has been executed
    private Integer executions = 0;
    private String processName;
    private Long processInstanceId;
    private String processInstanceDescription;
    private String deploymentId;

    public RequestSummary() {
    }

    public RequestSummary(Long jobId,
                          Date time,
                          RequestStatus status,
                          String commandName,
                          String message,
                          String key,
                          Integer retries,
                          Integer executions,
                          String processName,
                          Long processInstanceId,
                          String processInstanceDescription,
                          String deploymentId) {
        this.id = jobId;
        this.jobId = jobId;
        this.time = time;
        this.status = status;
        this.commandName = commandName;
        this.message = message;
        this.key = key;
        this.retries = retries;
        this.executions = executions;
        this.processName = processName;
        this.processInstanceId = processInstanceId;
        this.processInstanceDescription = processInstanceDescription;
        this.deploymentId = deploymentId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Integer getExecutions() {
        return executions;
    }

    public void setExecutions(Integer executions) {
        this.executions = executions;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }
    
    public String getDeploymentId() {
        return deploymentId;
    }
    
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
    
}
