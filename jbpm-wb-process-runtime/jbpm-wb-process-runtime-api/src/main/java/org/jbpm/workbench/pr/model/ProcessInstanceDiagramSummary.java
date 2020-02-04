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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
public class ProcessInstanceDiagramSummary extends GenericSummary<Long> {

    private String svgContent;

    private List<NodeInstanceSummary> nodeInstances;

    private ProcessSummary processDefinition;

    private List<TimerInstanceSummary> timerInstances;

    private List<ProcessInstanceSummary> subProcessInstances = new ArrayList<>();

    private ProcessInstanceSummary parentProcessInstanceSummary;

    public ProcessInstanceSummary getParentProcessInstanceSummary() {
        return parentProcessInstanceSummary;
    }

    public void setParentProcessInstanceSummary(ProcessInstanceSummary parentProcessInstanceSummary) {
        this.parentProcessInstanceSummary = parentProcessInstanceSummary;
    }

    public ProcessInstanceDiagramSummary() {
    }

    public String getSvgContent() {
        return svgContent;
    }

    public void setSvgContent(String svgContent) {
        this.svgContent = svgContent;
    }

    public List<NodeInstanceSummary> getNodeInstances() {
        return nodeInstances;
    }

    public void setNodeInstances(List<NodeInstanceSummary> nodeInstances) {
        this.nodeInstances = nodeInstances;
    }

    public ProcessSummary getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessSummary processDefinition) {
        this.processDefinition = processDefinition;
    }

    public void setTimerInstances(List<TimerInstanceSummary> timerInstances) {
        this.timerInstances = timerInstances;
    }

    public List<TimerInstanceSummary> getTimerInstances() {
        return timerInstances;
    }

    public List<ProcessInstanceSummary> getSubProcessInstances() {
        return subProcessInstances;
    }

    public void setSubProcessInstances(List<ProcessInstanceSummary> subProcessInstances) {
        this.subProcessInstances = subProcessInstances;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDiagramSummary{" +
                "svgContent='" + svgContent + '\'' +
                ", nodeInstances=" + nodeInstances +
                ", processDefinition=" + processDefinition +
                ", timerInstances=" + timerInstances +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @NonPortable
    public static final class Builder {

        private ProcessInstanceDiagramSummary summary;

        private Builder() {
            summary = new ProcessInstanceDiagramSummary();
        }

        public Builder withSvgContent(String svgContent) {
            summary.setSvgContent(svgContent);
            return this;
        }

        public Builder withId(Long id) {
            summary.setId(id);
            return this;
        }

        public Builder withName(String name) {
            summary.setName(name);
            return this;
        }

        public Builder withNodeInstances(List<NodeInstanceSummary> nodeInstances) {
            summary.setNodeInstances(nodeInstances);
            return this;
        }

        public Builder withProcessDefinition(ProcessSummary processDefinition) {
            summary.setProcessDefinition(processDefinition);
            return this;
        }

        public Builder withTimerInstances(List<TimerInstanceSummary> timerInstances) {
            summary.setTimerInstances(timerInstances);
            return this;
        }

        public Builder withSubProcessInstances(List<ProcessInstanceSummary> subProcessInstances){
            summary.setSubProcessInstances(subProcessInstances);
            return this;
        }

        public Builder withParentProcessInstanceSummary(ProcessInstanceSummary parentProcessInstance) {
            summary.setParentProcessInstanceSummary(parentProcessInstance);
            return this;
        }

        public ProcessInstanceDiagramSummary build() {
            return summary;
        }
    }
}
