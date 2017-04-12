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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import static java.util.Optional.ofNullable;

@Bindable
@Portable
public class CaseDefinitionSummary implements Comparable<CaseDefinitionSummary> {

    private String id;
    private String name;
    private String containerId;
    private Map<String, Integer> roles = new HashMap<>();

    public CaseDefinitionSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUniqueId() {
        return getId() + "|" + getContainerId();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(final String containerId) {
        this.containerId = containerId;
    }

    public Map<String, Integer> getRoles() {
        return roles;
    }

    public void setRoles(final Map<String, Integer> roles) {
        this.roles = ofNullable(roles).orElse(new HashMap<>());
    }

    @Override
    public int compareTo(final CaseDefinitionSummary caseDefinitionSummary) {
        return getId().compareTo(caseDefinitionSummary.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseDefinitionSummary that = (CaseDefinitionSummary) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(containerId, that.containerId);
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = ~~result;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseDefinitionSummary{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", containerId='" + containerId + '\'' +
                ", roles=" + roles +
                '}';
    }

    public static class Builder {

        private CaseDefinitionSummary caseDefinition = new CaseDefinitionSummary();

        public CaseDefinitionSummary build() {
            return caseDefinition;
        }

        public Builder id(final String id) {
            caseDefinition.setId(id);
            return this;
        }

        public Builder name(final String name) {
            caseDefinition.setName(name);
            return this;
        }

        public Builder containerId(final String containerId) {
            caseDefinition.setContainerId(containerId);
            return this;
        }

        public Builder roles(final Map<String, Integer> roles) {
            caseDefinition.setRoles(roles);
            return this;
        }

    }

}