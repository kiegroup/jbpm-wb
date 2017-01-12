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

package org.jbpm.workbench.cm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import static java.util.Optional.ofNullable;

@Bindable
@Portable
public class CaseInstanceSummary {

    private String caseId;
    private String description;
    private Integer status;
    private String containerId;
    private String owner;
    private Date startedAt;
    private Date completedAt;
    private String caseDefinitionId;
    private List<CaseRoleAssignmentSummary> roleAssignments = new ArrayList<>();
    private List<CaseStageSummary> stages = new ArrayList<>();

    public CaseInstanceSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(final String caseId) {
        this.caseId = caseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(final String containerId) {
        this.containerId = containerId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(final Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(final Date completedAt) {
        this.completedAt = completedAt;
    }

    public List<CaseRoleAssignmentSummary> getRoleAssignments() {
        return roleAssignments;
    }

    public void setRoleAssignments(final List<CaseRoleAssignmentSummary> roleAssignments) {
        this.roleAssignments = ofNullable(roleAssignments).orElse(new ArrayList<>());
    }

    public List<CaseStageSummary> getStages() {
        return stages;
    }

    public void setStages(final List<CaseStageSummary> stages) {
        this.stages = ofNullable(stages).orElse(new ArrayList<>());
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setCaseDefinitionId(final String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseInstanceSummary that = (CaseInstanceSummary) o;
        return Objects.equals(caseId, that.caseId);
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseId == null) ? 0 : caseId.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseInstanceSummary{" +
                "caseId='" + caseId + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", containerId='" + containerId + '\'' +
                ", owner='" + owner + '\'' +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", caseDefinitionId='" + caseDefinitionId + '\'' +
                ", roleAssignments=" + roleAssignments + '\'' +
                ", stages=" + stages +
                '}';
    }

    public static class Builder {

        private CaseInstanceSummary caseInstance = new CaseInstanceSummary();

        public CaseInstanceSummary build() {
            return caseInstance;
        }

        public Builder caseId(final String caseId) {
            caseInstance.setCaseId(caseId);
            return this;
        }

        public Builder description(final String description) {
            caseInstance.setDescription(description);
            return this;
        }

        public Builder status(final Integer status) {
            caseInstance.setStatus(status);
            return this;
        }

        public Builder containerId(final String containerId) {
            caseInstance.setContainerId(containerId);
            return this;
        }

        public Builder owner(final String owner) {
            caseInstance.setOwner(owner);
            return this;
        }

        public Builder startedAt(final Date startedAt) {
            caseInstance.setStartedAt(startedAt);
            return this;
        }

        public Builder completedAt(final Date completedAt) {
            caseInstance.setCompletedAt(completedAt);
            return this;
        }

        public Builder caseDefinitionId(final String caseDefinitionId) {
            caseInstance.setCaseDefinitionId(caseDefinitionId);
            return this;
        }

        public Builder roleAssignments(final List<CaseRoleAssignmentSummary> roleAssignments) {
            caseInstance.setRoleAssignments(roleAssignments);
            return this;
        }

        public Builder stages(final List<CaseStageSummary> stages) {
            caseInstance.setStages(stages);
            return this;
        }

    }

}