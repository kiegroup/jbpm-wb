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
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.es.util.ExecutionErrorType;

@Bindable
@Portable
public class ExecutionErrorSummary extends GenericSummary<String> {

    private String errorId;
    private ExecutionErrorType type;
    private String deploymentId;
    private Long processInstanceId;
    private String processId;
    private Long activityId;
    private String activityName;
    private Long jobId;
    private String errorMessage;
    private String error;
    private Boolean acknowledged;
    private String acknowledgedBy;
    private Date acknowledgedAt;
    private Date errorDate;

    public ExecutionErrorSummary() {
    }

    public ExecutionErrorSummary(String errorId,
                                 String type,
                                 String deploymentId,
                                 Long processInstanceId,
                                 String processId,
                                 Long activityId,
                                 String activityName,
                                 Long jobId,
                                 String errorMessage,
                                 Short acknowledged,
                                 String acknowledgedBy,
                                 Date acknowledgedAt,
                                 Date errorDate) {
        this.id = errorId;
        this.name = errorId;
        this.errorId = errorId;
        this.type = ExecutionErrorType.fromType(type);
        this.deploymentId = deploymentId;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.activityId = activityId;
        this.activityName = activityName;
        this.jobId = jobId;
        this.errorMessage = errorMessage;
        this.acknowledged = (acknowledged != null && acknowledged > 0);
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = acknowledgedAt;
        this.errorDate = errorDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public ExecutionErrorType getType() {
        return type;
    }

    public void setType(ExecutionErrorType type) {
        this.type = type;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isAcknowledged() {
        if (acknowledged != null) {
            return acknowledged.booleanValue();
        }
        return false;
    }

    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public Date getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(Date acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    @Override
    public String toString() {
        return "ExecutionErrorSummary{" +
                "errorId='" + errorId + '\'' +
                ", type=" + type +
                ", deploymentId='" + deploymentId + '\'' +
                ", processInstanceId=" + processInstanceId +
                ", processId='" + processId + '\'' +
                ", activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", jobId=" + jobId +
                ", errorMessage='" + errorMessage + '\'' +
                ", error='" + error + '\'' +
                ", acknowledged=" + acknowledged +
                ", acknowledgedBy='" + acknowledgedBy + '\'' +
                ", acknowledgedAt=" + acknowledgedAt +
                ", errorDate=" + errorDate +
                "} " + super.toString();
    }

    public static class Builder {

        private ExecutionErrorSummary error = new ExecutionErrorSummary();

        public ExecutionErrorSummary build() {
            return error;
        }

        public Builder errorId(String errorId) {
            error.setErrorId(errorId);
            error.setId(errorId);
            return this;
        }

        public Builder type(ExecutionErrorType type) {
            error.setType(type);
            return this;
        }

        public Builder deploymentId(String deploymentId) {
            error.setDeploymentId(deploymentId);
            return this;
        }

        public Builder message(String message) {
            error.setErrorMessage(message);
            return this;
        }

        public Builder error(String errorStr) {
            error.setError(errorStr);
            return this;
        }

        public Builder acknowledgedBy(String user) {
            error.setAcknowledgedBy(user);
            return this;
        }

        public Builder processInstanceId(Long piId) {
            error.setProcessInstanceId(piId);
            return this;
        }

        public Builder activityId(Long activityId) {
            error.setActivityId(activityId);
            return this;
        }

        public Builder acknowledged(boolean acknowledged) {
            error.setAcknowledged(acknowledged);
            return this;
        }

        public Builder acknowledgedAt(Date acknowledgedAt) {
            error.setAcknowledgedAt(acknowledgedAt);
            return this;
        }

        public Builder processId(String processId) {
            error.setProcessId(processId);
            return this;
        }

        public Builder activityName(String activityName) {
            error.setActivityName(activityName);
            return this;
        }

        public Builder errorDate(Date errorDate) {
            error.setErrorDate(errorDate);
            return this;
        }

        public Builder jobId(Long jobId) {
            error.setJobId(jobId);
            return this;
        }
    }
}
