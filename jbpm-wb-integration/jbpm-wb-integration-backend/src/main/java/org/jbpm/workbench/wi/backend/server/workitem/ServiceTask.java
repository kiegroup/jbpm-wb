/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.workitem;

import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;

public class ServiceTask {

    private String id;
    private String icon;
    private String name;
    private String description;
    private String additionalInfo;
    private Boolean enabled;
    private GAV gav;
    private String groupingConditionByGA;
    private String version;

    private String referenceLink;

    private Set<String> installedOn;

    private Set<String> installedOnBranch;

    private List<String> parameters;

    public ServiceTask() {

    }

    public ServiceTask(String id, String icon, String name, String description, String additionalInfo, Boolean enabled, Set<String> installedOn, List<String> parameters, String referenceLink, Set<String> installedOnBranch, String gav) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.enabled = enabled;
        this.installedOn = installedOn;
        this.parameters = parameters;
        this.referenceLink = referenceLink;
        this.installedOnBranch = installedOnBranch;
        if (!gav.isEmpty() && gav != null) {
            String[] splitStrings = gav.split(":");
            this.gav = new GAV(splitStrings[0], splitStrings[1], splitStrings[2]);
            this.groupingConditionByGA = splitStrings[0] + ":" + splitStrings[1];
            this.version = splitStrings[2];
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupingConditionByGA() {
        return groupingConditionByGA;
    }

    public void setGroupingConditionByGA(String groupingConditionByGA) {
        this.groupingConditionByGA = groupingConditionByGA;
    }

    public GAV getGav() {
        return gav;
    }

    public void setGav(GAV gav) {
        this.gav = gav;
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

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getReferenceLink() {
        return referenceLink;
    }

    public void setReferenceLink(String referenceLink) {
        this.referenceLink = referenceLink;
    }

    public Set<String> getInstalledOnBranch() {
        return installedOnBranch;
    }

    public void setInstalledOnBranch(Set<String> installedOnBranch) {
        this.installedOnBranch = installedOnBranch;
    }

    public int getCompareAbleVersion() {
        return Integer.parseInt(version.replaceAll("[^0-9]", ""));
    }

    @Override
    public String toString() {
        return "ServiceTask{" +
                "id='" + id + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", enabled=" + enabled +
                ", gav=" + gav +
                ", groupingConditionByGA='" + groupingConditionByGA + '\'' +
                ", version='" + version + '\'' +
                ", referenceLink='" + referenceLink + '\'' +
                ", installedOn=" + installedOn +
                ", installedOnBranch=" + installedOnBranch +
                ", parameters=" + parameters +
                '}';
    }
}
