/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewCaseInstanceEvent implements Serializable {

    private String newCaseId;
    private String serverTemplateId;
    private String newProcessDefId;
    private String deploymentId;
    private String processDefName;

    public NewCaseInstanceEvent() {
    }

    public NewCaseInstanceEvent(String serverTemplateId,
                                   String deploymentId,
                                   String newCaseId,
                                   String newProcessDefId,
                                   String processDefName) {
        this.serverTemplateId = serverTemplateId;
        this.newCaseId = newCaseId;
        this.newProcessDefId = newProcessDefId;
        this.deploymentId = deploymentId;
        this.processDefName = processDefName;
    }
 
    public String getNewCaseId() {
        return newCaseId;
    }

    public String getNewProcessDefId() {
        return newProcessDefId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getProcessDefName() {
        return processDefName;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.serverTemplateId != null ? this.serverTemplateId.hashCode() : 0);
        hash = ~~hash;
        hash = 31 * hash + (this.newCaseId != null ? this.newCaseId.hashCode() : 0);
        hash = ~~hash;
        hash = 31 * hash + (this.newProcessDefId != null ? this.newProcessDefId.hashCode() : 0);
        hash = ~~hash;
        hash = 31 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);        
        hash = ~~hash;
        hash = 31 * hash + (this.processDefName != null ? this.processDefName.hashCode() : 0);
        hash = ~~hash;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NewCaseInstanceEvent other = (NewCaseInstanceEvent) obj;
        if (this.newCaseId != other.newCaseId && (this.newCaseId == null || !this.newCaseId.equals(other.newCaseId))) {
            return false;
        }
        if (this.newProcessDefId == null ? other.newProcessDefId != null : !this.newProcessDefId.equals(other.newProcessDefId)) {
            return false;
        }
        if (this.deploymentId == null ? other.deploymentId != null : !this.deploymentId.equals(other.deploymentId)) {
            return false;
        }        
        if (this.processDefName == null ? other.processDefName != null : !this.processDefName.equals(other.processDefName)) {
            return false;
        }
        if (this.serverTemplateId == null ? other.serverTemplateId != null : !this.serverTemplateId.equals(other.serverTemplateId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NewProcessInstanceEvent{serverTemplateId=" + serverTemplateId + ", " + "newProcessInstanceId=" + newCaseId + ", newProcessDefId=" + newProcessDefId + ", deploymentId=" + deploymentId + ", processDefName=" + processDefName + '}';
    }
}