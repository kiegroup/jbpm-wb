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


import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;


@Bindable
@Portable
public class ProcessDefinitionSummary {

    private String id;
    private String name;
    private String containerId;
    private String version;
    private String packageName;

    public ProcessDefinitionSummary() {
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public String toString() {
        return "ProcessDefinitionSummary{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", containerId='" + containerId + '\'' +
                ", version=" + version +
                ", packageName=" + packageName +
                '}';
    }

    public static class Builder {

        private ProcessDefinitionSummary caseDefinition = new ProcessDefinitionSummary();

        public ProcessDefinitionSummary build() {
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

        public Builder version(final String version) {
            caseDefinition.setVersion(version);
            return this;
        }

        public Builder packageName(final String packageName) {
            caseDefinition.setPackageName(packageName);
            return this;
        }
    }

}