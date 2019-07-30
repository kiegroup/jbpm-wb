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

package org.jbpm.workbench.ht.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class TaskAssignmentSummary extends GenericSummary<Long> {

    private Long taskId;
    private String taskName;
    private String actualOwner;
    private String createdBy;
    private List<String> potOwnersString;
    private List<String> businessAdmins;
    private String status;
    private Boolean delegationAllowed;
    private Boolean forwardAllowed;
    private String deploymentId;

    public TaskAssignmentSummary() {

    }

    public TaskAssignmentSummary(final Long taskId,
                                 final String taskName,
                                 final String actualOwner,
                                 final List<String> potOwnersString) {
        super();
        this.taskId = taskId;
        this.taskName = taskName;
        this.actualOwner = actualOwner;
        this.potOwnersString = potOwnersString;
    }

    public TaskAssignmentSummary(final Long taskId,
                                 final String taskName,
                                 final String actualOwner,
                                 final String createdBy,
                                 final List<String> potOwnersString,
                                 final List<String> businessAdmins,
                                 final String status) {
        this(taskId,
             taskName,
             actualOwner,
             potOwnersString);
        this.createdBy = createdBy;
        this.businessAdmins = businessAdmins;
        this.status = status;
        this.delegationAllowed = false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.id = taskId;
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.name = taskName;
        this.taskName = taskName;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public List<String> getPotOwnersString() {
        return potOwnersString;
    }

    public void setPotOwnersString(List<String> potOwnersString) {
        this.potOwnersString = potOwnersString;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getBusinessAdmins() {
        return businessAdmins;
    }

    public void setBusinessAdmins(List<String> businessAdmins) {
        this.businessAdmins = businessAdmins;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isDelegationAllowed() {
        return delegationAllowed;
    }

    public void setDelegationAllowed(Boolean delegationAllowed) {
        this.delegationAllowed = delegationAllowed;
    }

    public void setForwardAllowed(Boolean forwardAllowed) {
        this.forwardAllowed = forwardAllowed;
    }

    public Boolean isForwardAllowed() {
        return forwardAllowed;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @NonPortable
    public static final class Builder {

        private TaskAssignmentSummary taskAssignmentSummary = new TaskAssignmentSummary();

        private Builder() {
        }

        public TaskAssignmentSummary build() {
            return taskAssignmentSummary;
        }

        public Builder taskId(Long taskId) {
            this.taskAssignmentSummary.setTaskId(taskId);
            return this;
        }

        public Builder taskName(String taskName) {
            this.taskAssignmentSummary.setTaskName(taskName);
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            this.taskAssignmentSummary.setActualOwner(actualOwner);
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.taskAssignmentSummary.setCreatedBy(createdBy);
            return this;
        }

        public Builder status(String status) {
            this.taskAssignmentSummary.setStatus(status);
            return this;
        }

        public Builder delegationAllowed(Boolean delegationAllowed) {
            this.taskAssignmentSummary.setDelegationAllowed(delegationAllowed);
            return this;
        }

        public Builder forwardAllowed(Boolean forwardAllowed) {
            this.taskAssignmentSummary.setForwardAllowed(forwardAllowed);
            return this;
        }

        public Builder deploymentId(String deploymentId) {
            this.taskAssignmentSummary.setDeploymentId(deploymentId);
            return this;
        }
    }

    @Override
    public String toString() {
        return "TaskAssignmentSummary{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", actualOwner='" + actualOwner + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", potOwnersString=" + potOwnersString +
                ", businessAdmins=" + businessAdmins +
                ", status='" + status + '\'' +
                ", delegationAllowed=" + delegationAllowed +
                ", forwardAllowed=" + forwardAllowed +
                ", deploymentId=" + deploymentId +
                "} " + super.toString();
    }
}