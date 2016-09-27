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

package org.jbpm.console.ng.cm.model;

import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.kie.api.runtime.process.ProcessInstance;

@Portable
public class CaseInstanceSummary extends GenericSummary {

    private String description;
    private Integer status;
    private String containerId;
    private String owner;
    private Date startedAt;
    private Date completedAt;
    private String caseDefinitionId;
    private List<CaseRoleAssignmentSummary> roleAssignments;

    public CaseInstanceSummary() {
    }

    public CaseInstanceSummary(final String caseId, final String description, final Integer status, final String containerId) {
        super(caseId, null);
        this.description = description;
        this.status = status;
        this.containerId = containerId;
    }

    public String getCaseId() {
        return (String) getId();
    }

    public void setCaseId(String caseId) {
        setId(caseId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getStatusString() {
        if (status == null) {
            return "";
        }
        switch (status) {
            case ProcessInstance.STATE_PENDING:
                return "Pending";
            case ProcessInstance.STATE_ACTIVE:
                return "Active";
            case ProcessInstance.STATE_COMPLETED:
                return "Completed";
            case ProcessInstance.STATE_ABORTED:
                return "Aborted";
            case ProcessInstance.STATE_SUSPENDED:
                return "Suspended";
            default:
                return "";
        }
    }

    public Boolean isActive() {
        return status == 1;
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
        this.roleAssignments = roleAssignments;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setCaseDefinitionId(final String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    @Override
    public String toString() {
        return "CaseInstanceSummary{" +
                "description='" + description + '\'' +
                ", status=" + status +
                ", containerId='" + containerId + '\'' +
                ", owner='" + owner + '\'' +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", caseDefinitionId='" + caseDefinitionId + '\'' +
                ", roleAssignments=" + roleAssignments +
                "} " + super.toString();
    }

}