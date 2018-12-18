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

package org.jbpm.workbench.wi.workitems.model;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class ServiceTaskSummary {

    private String id;
    private String icon;
    private String name;
    private String description;
    private String additionalInfo;
    private Boolean enabled;
    
    private Set<String> installedOn;

    public ServiceTaskSummary() {

    }

    public ServiceTaskSummary(String id, String icon, String name, String description, String additionalInfo, Boolean enabled, Set<String> installedOn) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.enabled = enabled;
        this.installedOn = installedOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<String> getInstalledOn() {
        return installedOn;
    }
    
    public void setInstalledOn(Set<String> installedOn) {
        this.installedOn = installedOn;
    }

    @Override
    public String toString() {
        return "ServiceTaskSummary [icon=" + icon + ", name=" + name + ", description=" + description + ", enabled=" + enabled + "]";
    }

}
