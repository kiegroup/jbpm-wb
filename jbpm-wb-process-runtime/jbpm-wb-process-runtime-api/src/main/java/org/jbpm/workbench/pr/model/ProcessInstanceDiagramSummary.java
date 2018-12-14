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
public class ProcessInstanceDiagramSummary extends GenericSummary<Long> {

    private String svgContent;

    private List<NodeInstanceSummary> nodeInstances;

    private List<ProcessNodeSummary> processNodes;

    private List<TimerInstanceSummary> timerInstances;

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

    public List<ProcessNodeSummary> getProcessNodes() {
        return processNodes;
    }

    public void setProcessNodes(List<ProcessNodeSummary> processNodes) {
        this.processNodes = processNodes;
    }

    public void setTimerInstances(List<TimerInstanceSummary> timerInstances) {
        this.timerInstances = timerInstances;
    }

    public List<TimerInstanceSummary> getTimerInstances() {
        return timerInstances;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDiagramSummary{" +
                "svgContent='" + svgContent + '\'' +
                ", nodeInstances=" + nodeInstances +
                ", processNodes=" + processNodes +
                ", timerInstances=" + timerInstances +
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

        public Builder withProcessNodes(List<ProcessNodeSummary> processNodes) {
            summary.setProcessNodes(processNodes);
            return this;
        }

        public Builder withTimerInstances(List<TimerInstanceSummary> timerInstances) {
            summary.setTimerInstances(timerInstances);
            return this;
        }

        public ProcessInstanceDiagramSummary build() {
            return summary;
        }
    }
}
