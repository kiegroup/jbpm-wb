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

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericErrorSummary;

@Portable
public class ProcessInstanceSummary extends GenericErrorSummary<Long> {

    private String serverTemplateId;
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
    private Integer slaCompliance;
    private Date slaDueDate;

    public ProcessInstanceSummary(String serverTemplateId,
                                  Long processInstanceId,
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
                                  Integer slaCompliance,
                                  Date slaDueDate,
                                  Integer errorCount) {
        super(errorCount, processInstanceId, processName);
        this.serverTemplateId = serverTemplateId;
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
        this.slaCompliance = slaCompliance;
        this.slaDueDate = slaDueDate;
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
    
    public Integer getSlaCompliance() {
        return slaCompliance;
    }
    
    public void setSlaCompliance(Integer slaCompliance) {
        this.slaCompliance = slaCompliance;
    }
    
    public Date getSlaDueDate() {
        return slaDueDate;
    }
    
    public void setSlaDueDate(Date slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public ProcessInstanceKey getProcessInstanceKey() {
        return new ProcessInstanceKey(serverTemplateId,
                                      deploymentId,
                                      processInstanceId);
    }

    @Override
    public String toString() {
        return "ProcessInstanceSummary{" +
                "serverTemplateId='" + serverTemplateId + '\'' +
                ", processInstanceId=" + processInstanceId +
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
                ", slaCompliance=" + slaCompliance +
                ", slaDueDate=" + slaDueDate +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @NonPortable
    public static final class Builder {

        private ProcessInstanceSummary processInstanceSummary;

        private Builder() {
            processInstanceSummary = new ProcessInstanceSummary();
        }

        public Builder withErrorCount(Integer errorCount) {
            processInstanceSummary.setErrorCount(errorCount);
            return this;
        }

        public Builder withServerTemplateId(String serverTemplateId) {
            processInstanceSummary.setServerTemplateId(serverTemplateId);
            return this;
        }

        public Builder withProcessInstanceId(Long processInstanceId) {
            processInstanceSummary.setProcessInstanceId(processInstanceId);
            processInstanceSummary.setId(processInstanceId);
            return this;
        }

        public Builder withProcessId(String processId) {
            processInstanceSummary.setProcessId(processId);
            return this;
        }

        public Builder withProcessName(String processName) {
            processInstanceSummary.setProcessName(processName);
            processInstanceSummary.setName(processName);
            return this;
        }

        public Builder withProcessVersion(String processVersion) {
            processInstanceSummary.setProcessVersion(processVersion);
            return this;
        }

        public Builder withCallbacks(List<LabeledCommand> callbacks) {
            processInstanceSummary.setCallbacks(callbacks);
            return this;
        }

        public Builder withState(Integer state) {
            processInstanceSummary.setState(state);
            return this;
        }

        public Builder withStartTime(Date startTime) {
            processInstanceSummary.setStartTime(startTime);
            return this;
        }

        public Builder withEndTime(Date endTime) {
            processInstanceSummary.setEndTime(endTime);
            return this;
        }

        public Builder withDeploymentId(String deploymentId) {
            processInstanceSummary.setDeploymentId(deploymentId);
            return this;
        }

        public Builder withInitiator(String initiator) {
            processInstanceSummary.setInitiator(initiator);
            return this;
        }

        public Builder withProcessInstanceDescription(String processInstanceDescription) {
            processInstanceSummary.setProcessInstanceDescription(processInstanceDescription);
            return this;
        }

        public Builder withParentId(Long parentId) {
            processInstanceSummary.setParentId(parentId);
            return this;
        }

        public Builder withCorrelationKey(String correlationKey) {
            processInstanceSummary.setCorrelationKey(correlationKey);
            return this;
        }

        public Builder withActiveTasks(List<UserTaskSummary> activeTasks) {
            processInstanceSummary.setActiveTasks(activeTasks);
            return this;
        }

        public Builder withLastModificationDate(Date lastModificationDate) {
            processInstanceSummary.setLastModificationDate(lastModificationDate);
            return this;
        }

        public Builder withSlaCompliance(Integer slaCompliance) {
            processInstanceSummary.setSlaCompliance(slaCompliance);
            return this;
        }

        public Builder withSlaDueDate(Date slaDueDate) {
            processInstanceSummary.setSlaDueDate(slaDueDate);
            return this;
        }

        public ProcessInstanceSummary build() {
            return processInstanceSummary;
        }
    }
}
