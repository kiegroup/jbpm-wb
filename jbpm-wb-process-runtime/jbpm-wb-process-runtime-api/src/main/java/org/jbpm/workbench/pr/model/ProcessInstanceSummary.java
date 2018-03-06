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

package org.jbpm.workbench.pr.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericErrorSummary;

@Portable
public class ProcessInstanceSummary extends GenericErrorSummary<Long> {

    private Long processInstanceId;
    private String processId;
    private String processName;
    private String processVersion;
    private Integer state;
    private Date startTime;
    private Date endTime;
    private String deploymentId;
    private String initiator;
    private String processInstanceDescription;
    private Long parentId;
    private String correlationKey;
    private Map<String, String> domainData = new HashMap<String, String>();
    private List<UserTaskSummary> activeTasks;
    private Date lastModificationDate;

    public ProcessInstanceSummary(Long processInstanceId,
                                  String processId,
                                  String deploymentId,
                                  String processName,
                                  String processVersion,
                                  Integer state,
                                  Date startTime,
                                  Date endTime,
                                  String initiator,
                                  String processInstanceDescription,
                                  String correlationKey,
                                  Long parentId,
                                  Date lastModificationDate,
                                  Integer errorCount) {
        super(errorCount, processInstanceId, processName);
        this.id = processInstanceId;
        this.name = processName;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.processName = processName;
        this.deploymentId = deploymentId;
        this.processVersion = processVersion;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        this.initiator = initiator;
        this.processInstanceDescription = processInstanceDescription;
        this.correlationKey = correlationKey;
        this.parentId = parentId;
        this.lastModificationDate = lastModificationDate;
    }

    public ProcessInstanceSummary() {
    }

    public void addDomainData(String key,
                              String value) {
        domainData.put(key,
                       value);
    }

    public String getDomainDataValue(String key) {
        return domainData.get(key);
    }

    public Map<String, String> getDomainData() {
        return domainData;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }

    public List<UserTaskSummary> getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(List<UserTaskSummary> activeTasks) {
        this.activeTasks = activeTasks;
    }

    public String getCorrelationKey() {
        return correlationKey;
    }

    public void setCorrelationKey(String correlationKey) {
        this.correlationKey = correlationKey;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public String toString() {
        return "ProcessInstanceSummary{" +
                "processInstanceId=" + processInstanceId +
                ", processId='" + processId + '\'' +
                ", processName='" + processName + '\'' +
                ", processVersion='" + processVersion + '\'' +
                ", state=" + state +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", deploymentId='" + deploymentId + '\'' +
                ", initiator='" + initiator + '\'' +
                ", processInstanceDescription='" + processInstanceDescription + '\'' +
                ", parentId=" + parentId +
                ", correlationKey='" + correlationKey + '\'' +
                ", domainData=" + domainData +
                ", activeTasks=" + activeTasks +
                ", lastModificationDate=" + lastModificationDate +
                "} " + super.toString();
    }
}
