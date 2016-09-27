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

import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

import static java.util.Collections.emptyList;

@Portable
public class CaseRoleAssignmentSummary extends GenericSummary {

    private List<String> groups;
    private List<String> users;

    public CaseRoleAssignmentSummary() {
    }

    public CaseRoleAssignmentSummary(final String name, final List<String> groups, final List<String> users) {
        super(name, name);
        this.groups = Optional.ofNullable(groups).orElse(emptyList());
        this.users = Optional.ofNullable(users).orElse(emptyList());
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "CaseRoleAssignmentSummary{" +
                "groups=" + groups +
                ", users=" + users +
                "} " + super.toString();
    }
}