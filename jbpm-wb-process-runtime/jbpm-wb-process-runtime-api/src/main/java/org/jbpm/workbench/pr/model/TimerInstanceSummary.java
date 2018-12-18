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

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jbpm.workbench.common.model.GenericSummary;

@Portable
@Bindable
public class TimerInstanceSummary extends GenericSummary<Long> {

    private Date activationTime;
    private Date lastFireTime;
    private Date nextFireTime;
    private Long delay;
    private Long period;
    private Integer repeatLimit;
    private Boolean relative = Boolean.FALSE;
    private Long processInstanceId;
    private String description;

    public TimerInstanceSummary() {
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(Date lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Integer getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(Integer repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public String getLabel(){
        return getId() + "-" + (getName() == null || getName().trim().isEmpty() ? "TimerInstance" : getName());
    }

    public Boolean isRelative() {
        return relative;
    }

    public void setRelative(Boolean relative) {
        this.relative = relative;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TimerInstanceSummary{" +
                "activationTime=" + activationTime +
                ", lastFireTime=" + lastFireTime +
                ", nextFireTime=" + nextFireTime +
                ", delay=" + delay +
                ", period=" + period +
                ", repeatLimit=" + repeatLimit +
                ", relative=" + relative +
                ", processInstanceId=" + processInstanceId +
                ", description='" + description + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @NonPortable
    public static final class Builder {

        private TimerInstanceSummary summary;

        private Builder() {
            summary = new TimerInstanceSummary();
        }

        public Builder withId(Long id) {
            summary.setId(id);
            return this;
        }

        public Builder withName(String name) {
            summary.setName(name);
            return this;
        }

        public Builder withActivationTime(Date activationTime) {
            summary.setActivationTime(activationTime);
            return this;
        }

        public Builder withLastFireTime(Date lastFireTime) {
            summary.setLastFireTime(lastFireTime);
            return this;
        }

        public Builder withNextFireTime(Date nextFireTime) {
            summary.setNextFireTime(nextFireTime);
            return this;
        }

        public Builder withDelay(Long delay) {
            summary.setDelay(delay);
            return this;
        }

        public Builder withPeriod(Long period) {
            summary.setPeriod(period);
            return this;
        }

        public Builder withRepeatLimit(Integer repeatLimit) {
            summary.setRepeatLimit(repeatLimit);
            return this;
        }

        public Builder withRelative(Boolean relative) {
            summary.setRelative(relative);
            return this;
        }

        public Builder withProcessInstanceId(Long processInstanceId) {
            summary.setProcessInstanceId(processInstanceId);
            return this;
        }

        public TimerInstanceSummary build() {
            return summary;
        }
    }
}
