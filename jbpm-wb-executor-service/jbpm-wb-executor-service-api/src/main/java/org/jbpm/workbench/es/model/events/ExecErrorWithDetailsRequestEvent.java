/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.es.model.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExecErrorWithDetailsRequestEvent {

    private String serverTemplateId;
    private String errorId;
    private String deploymentId;

    public ExecErrorWithDetailsRequestEvent() {
    }

    public ExecErrorWithDetailsRequestEvent(String serverTemplateId,
                                            String errorId,
                                            String deploymentId) {
        this.serverTemplateId = serverTemplateId;
        this.deploymentId = deploymentId;
        this.errorId = errorId;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    public String toString() {
        return "ExecErrorWithDetailsRequestEvent{" + "serverTemplateId=" + serverTemplateId + ", errorId=" + errorId +
                ", deploymentId=" + deploymentId + "}";
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.errorId != null ? this.errorId.hashCode() : 0);
        hash = ~~hash;
        hash = 71 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
        hash = ~~hash;
        hash = 71 * hash + (this.serverTemplateId != null ? this.serverTemplateId.hashCode() : 0);
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
        final ExecErrorWithDetailsRequestEvent other = (ExecErrorWithDetailsRequestEvent) obj;
        if (this.serverTemplateId == null ? other.serverTemplateId != null : !this.serverTemplateId.equals(other.serverTemplateId)) {
            return false;
        }
        if (this.deploymentId == null ? other.deploymentId != null : !this.deploymentId.equals(other.deploymentId)) {
            return false;
        }
        if (this.errorId == null ? other.errorId != null : !this.errorId.equals(other.errorId)) {
            return false;
        }
        return true;
    }
}