/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class WorkItemSummary extends GenericSummary<Long> {

    private Integer state = 0;
    private List<WorkItemParameterSummary> parameters;

    public WorkItemSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<WorkItemParameterSummary> getParameters() {
        return parameters;
    }

    public void setParameters(List<WorkItemParameterSummary> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "WorkItemSummary{" +
                " Id='" + id + '\'' +
                ", name =" + name +
                ", pagemeters:" + parameters +
                "} " + super.toString();
    }

    @NonPortable
    public static final class Builder {

        private WorkItemSummary workItemSummary = new WorkItemSummary();

        private Builder() {
        }

        public WorkItemSummary build() {
            return workItemSummary;
        }

        public Builder id(Long taskId) {
            this.workItemSummary.setId(taskId);
            return this;
        }

        public Builder name(String taskName) {
            this.workItemSummary.setName(taskName);
            return this;
        }

        public Builder state(Integer state) {
            this.workItemSummary.setState(state);
            return this;
        }

        public Builder parameters(List<WorkItemParameterSummary> parameters) {
            this.workItemSummary.setParameters(parameters);
            return this;
        }
    }
}
