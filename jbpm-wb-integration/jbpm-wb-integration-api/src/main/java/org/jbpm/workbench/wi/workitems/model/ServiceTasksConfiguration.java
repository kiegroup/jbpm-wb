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

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ServiceTasksConfiguration {

    private Boolean mavenInstall;
    private Boolean installPomDeps;
    private Boolean versionRange;

    public ServiceTasksConfiguration() {

    }

    public ServiceTasksConfiguration(Boolean mavenInstall, Boolean installPomDeps, Boolean versionRange) {
        super();
        this.mavenInstall = mavenInstall;
        this.installPomDeps = installPomDeps;
        this.versionRange = versionRange;
    }

    public Boolean getMavenInstall() {
        return mavenInstall;
    }

    public void setMavenInstall(Boolean mavenInstall) {
        this.mavenInstall = mavenInstall;
    }

    public Boolean getInstallPomDeps() {
        return installPomDeps;
    }

    public void setInstallPomDeps(Boolean installPomDeps) {
        this.installPomDeps = installPomDeps;
    }

    public Boolean getVersionRange() {
        return versionRange;
    }

    public void setVersionRange(Boolean versionRange) {
        this.versionRange = versionRange;
    }

    @Override
    public String toString() {
        return "ServiceTasksConfiguration [mavenInstall=" + mavenInstall + ", installPomDeps=" + installPomDeps + ", versionRange=" + versionRange + "]";
    }

}
