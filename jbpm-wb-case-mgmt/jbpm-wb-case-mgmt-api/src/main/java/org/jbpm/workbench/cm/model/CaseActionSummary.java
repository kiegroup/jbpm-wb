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

package org.jbpm.workbench.cm.model;

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseActionType;

import static org.jbpm.workbench.cm.util.CaseActionType.DYNAMIC_SUBPROCESS_TASK;
import static org.jbpm.workbench.cm.util.CaseActionType.DYNAMIC_USER_TASK;

@Bindable
@Portable
public class CaseActionSummary {

    private Long id;
    private String name;
    private String type;
    private Date createdOn;
    private String stageId;
    private String actualOwner;
    private CaseActionType actionType;
    private CaseActionStatus actionStatus;

    public CaseActionSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public CaseActionType getActionType() {
        return actionType;
    }

    public void setActionType(CaseActionType actionType) {
        this.actionType = actionType;
    }

    public CaseActionStatus getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(CaseActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CaseActionSummary that = (CaseActionSummary) o;
        if (this.actionType == DYNAMIC_USER_TASK || this.actionType == DYNAMIC_SUBPROCESS_TASK) {
            return Objects.equals(name,
                                  that.getName());
        } else {
            return Objects.equals(id,
                                  that.getId());
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseActionSummary{" +
                " id='" + id + '\'' +
                " name='" + name + '\'' +
                " type='" + type + '\'' +
                " createdOn='" + createdOn + '\'' +
                " stageId='" + stageId + '\'' +
                " actualOwner='" + actualOwner + '\'' +
                " actionType='" + actionType + '\'' +
                " actionStatus='" + actionStatus + '\'' +
                '}';
    }

    public static class Builder {

        private CaseActionSummary caseActionSummary = new CaseActionSummary();

        public CaseActionSummary build() {
            return caseActionSummary;
        }

        public Builder id(final Long id) {
            caseActionSummary.setId(id);
            return this;
        }

        public Builder name(final String name) {
            caseActionSummary.setName(name);
            return this;
        }

        public Builder type(String type) {
            caseActionSummary.setType(type);
            return this;
        }

        public Builder createdOn(Date createdOn) {
            caseActionSummary.setCreatedOn(createdOn);
            return this;
        }

        public Builder stageId(String stageId) {
            caseActionSummary.setStageId(stageId);
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            caseActionSummary.setActualOwner(actualOwner);
            return this;
        }

        public Builder actionType(CaseActionType actionType) {
            caseActionSummary.setActionType(actionType);
            return this;
        }

        public Builder actionStatus(CaseActionStatus actionStatus) {
            caseActionSummary.setActionStatus(actionStatus);
            return this;
        }
    }
}