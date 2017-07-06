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

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class CaseMilestoneSummary {

    private String name;
    private String identifier;
    private boolean achieved;
    private Date achievedAt;
    private String status;

    public CaseMilestoneSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public Date getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(Date achievedAt) {
        this.achievedAt = achievedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CaseMilestoneSummary that = (CaseMilestoneSummary) o;
        return Objects.equals(identifier,
                              that.getIdentifier());
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseMilestoneSummary{" +
                " name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", achieved='" + achieved + '\'' +
                ", achievedAt='" + achievedAt + '\'' +
                ", status='" + status +
                '}';
    }

    public static class Builder {

        private CaseMilestoneSummary caseMilestone = new CaseMilestoneSummary();

        public CaseMilestoneSummary build() {
            return caseMilestone;
        }

        public Builder name(final String name) {
            caseMilestone.setName(name);
            return this;
        }

        public Builder identifier(final String identifier) {
            caseMilestone.setIdentifier(identifier);
            return this;
        }

        public Builder achieved(boolean achieved) {
            caseMilestone.setAchieved(achieved);
            return this;
        }

        public Builder achievedAt(final Date achievedAt) {
            caseMilestone.setAchievedAt(achievedAt);
            return this;
        }

        public Builder status(final String status) {
            caseMilestone.setStatus(status);
            return this;
        }
    }
}