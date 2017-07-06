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

package org.jbpm.workbench.pr.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProcessDefSelectionEvent {

    private String processId;
    private String processDefName;
    private String deploymentId;
    private String serverTemplateId;
    private boolean dynamic;

    public ProcessDefSelectionEvent() {
    }

    public ProcessDefSelectionEvent(String processId) {
        this.processId = processId;
    }

    public ProcessDefSelectionEvent(String processId,
                                    String deploymentId,
                                    String serverTemplateId,
                                    String processDefName,
                                    boolean dynamic) {
        this(processId);
        this.deploymentId = deploymentId;
        this.serverTemplateId = serverTemplateId;
        this.processDefName = processDefName;
        this.dynamic = dynamic;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessDefName() {
        return processDefName;
    }

    public void setProcessDefName(String processDefName) {
        this.processDefName = processDefName;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}
