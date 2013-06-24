/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.bd.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KModuleDeploymentUnitSummary extends DeploymentUnitSummary {

    private String groupId;
    private String artifactId;
    private String version;
    private String kbaseName;
    private String ksessionName;
    private String strategy;

    public KModuleDeploymentUnitSummary() {
    }

    public KModuleDeploymentUnitSummary(String id, String type) {
        super(id, type);
    }

    public KModuleDeploymentUnitSummary(String id, String groupId, String artifactId, String version, String kbaseName,
            String ksessionName, String strategy) {
        super(id, "kjar");
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.kbaseName = kbaseName;
        this.ksessionName = ksessionName;
        this.strategy = strategy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKbaseName() {
        return kbaseName;
    }

    public void setKbaseName(String kbaseName) {
        this.kbaseName = kbaseName;
    }

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

}
