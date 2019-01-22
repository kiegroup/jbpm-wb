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
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
@Bindable
public class TimerSummary extends GenericSummary<Long> {

    private String uniqueId;
    private Long nodeId;

    public TimerSummary() {
    }

    public TimerSummary(final Long id,
                        final Long nodeId,
                        final String name,
                        final String uniqueId) {
        super(id,
              name);
        this.nodeId = nodeId;
        this.uniqueId = uniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "TimerSummary{" +
                "uniqueId='" + uniqueId + '\'' +
                ", nodeId=" + nodeId +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @NonPortable
    public static final class Builder {

        private TimerSummary timerSummary;

        private Builder() {
            timerSummary = new TimerSummary();
        }

        public Builder uniqueId(String uniqueId) {
            timerSummary.setUniqueId(uniqueId);
            return this;
        }

        public Builder nodeId(Long nodeId) {
            timerSummary.setNodeId(nodeId);
            return this;
        }

        public Builder id(Long id) {
            timerSummary.setId(id);
            return this;
        }

        public Builder name(String name) {
            timerSummary.setName(name);
            return this;
        }

        public Builder callbacks(List<LabeledCommand> callbacks) {
            timerSummary.setCallbacks(callbacks);
            return this;
        }

        public TimerSummary build() {
            return timerSummary;
        }
    }
}
