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

package org.jbpm.workbench.ht.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class User extends IdentitySummary {

    private static final long serialVersionUID = 856385605085015299L;

    // TODO only for id, remove it later
    private static final String PREFIX = "user_id";

    private List<TypeRole> typesRole;

    private List<Group> groups;

    public User() {

    }

    public User(String description) {
        super(description);
    }

    // TODO please remove it when we have id
    public String getId() {
        return PREFIX + SEPARATOR + super.getName();
    }

    public List<TypeRole> getTypesRole() {
        return typesRole;
    }

    public void setTypesRole(List<TypeRole> typesRole) {
        this.typesRole = typesRole;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "User [typesRole=" + typesRole + ", groups=" + groups + "]";
    }

    

}
