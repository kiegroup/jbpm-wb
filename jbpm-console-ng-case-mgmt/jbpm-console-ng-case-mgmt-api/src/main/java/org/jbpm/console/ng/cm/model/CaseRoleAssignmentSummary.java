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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import static java.util.Optional.ofNullable;

@Bindable
@Portable
public class CaseRoleAssignmentSummary {

    private String name;
    private List<String> groups = new ArrayList<>();
    private List<String> users = new ArrayList<>();

    public CaseRoleAssignmentSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(final List<String> groups) {
        this.groups = ofNullable(groups).orElse(new ArrayList<>());
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(final List<String> users) {
        this.users = ofNullable(users).orElse(new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseRoleAssignmentSummary that = (CaseRoleAssignmentSummary) o;
        return Objects.equals(name, that.name);
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "CaseRoleAssignmentSummary{" +
                "name='" + name + '\'' +
                ", groups=" + groups +
                ", users=" + users +
                '}';
    }

    public static class Builder {

        private CaseRoleAssignmentSummary caseRoleAssignment = new CaseRoleAssignmentSummary();

        public CaseRoleAssignmentSummary build() {
            return caseRoleAssignment;
        }

        public Builder name(final String name) {
            caseRoleAssignment.setName(name);
            return this;
        }

        public Builder groups(final List<String> groups) {
            caseRoleAssignment.setGroups(groups);
            return this;
        }

        public Builder users(final List<String> users) {
            caseRoleAssignment.setUsers(users);
            return this;
        }

    }

}