/*
 * Copyright 2012 JBoss by Red Hat.
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

package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class ProcessSummary extends GenericSummary{

    private String processDefId;
    private String processDefName;
    private String packageName;
    private String type;
    private String version;
    private String originalPath;
    private String deploymentId;
    private String encodedProcessSource;

    public ProcessSummary() {
    }

    public ProcessSummary(String processDefId, String processDefName, String deploymentId, String packageName, String type, String version,
            String originalpath, String processSource) {
        this.id = processDefId;    
        this.name = processDefName;
        this.processDefId = processDefId;
        this.processDefName = processDefName;
        this.deploymentId = deploymentId;
        this.packageName = packageName;
        this.type = type;
        this.version = version;
        this.originalPath = originalpath;
        this.encodedProcessSource = processSource;
    }

    public String getProcessDefId() {
      return processDefId;
    }

    public void setProcessDefId(String processDefId) {
      this.processDefId = processDefId;
    }

    public String getProcessDefName() {
      return processDefName;
    }

    public void setProcessDefName(String processDefName) {
      this.processDefName = processDefName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

    public void setEncodedProcessSource(String encodedProcessSource) {
        this.encodedProcessSource = encodedProcessSource;
    }

}
