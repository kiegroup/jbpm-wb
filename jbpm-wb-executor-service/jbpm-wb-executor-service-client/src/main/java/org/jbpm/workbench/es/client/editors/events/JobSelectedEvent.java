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
package org.jbpm.workbench.es.client.editors.events;

public class JobSelectedEvent {

    private String deploymentId;
    private Long jobId;
    private String serverTemplateId;

    public JobSelectedEvent() {
    }

    public JobSelectedEvent(String serverTemplateId,
                            String deploymentId,
                            Long jobId) {
        this.serverTemplateId = serverTemplateId;
        this.jobId = jobId;
        this.deploymentId = deploymentId;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    public String toString() {
        return "JobSelectedEvent{ " +
                "serverTemplateId=" + serverTemplateId + " jobId=" + jobId + " deploymentId=" + deploymentId + '}';
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.serverTemplateId != null ? this.serverTemplateId.hashCode() : 0);
        hash = ~~hash;
        hash = 37 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
        hash = ~~hash;
        hash = 37 * hash + (this.jobId != null ? this.jobId.hashCode() : 0);
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
        final JobSelectedEvent other = (JobSelectedEvent) obj;
        if (this.jobId == null ? other.jobId != null : !this.jobId.equals(other.jobId)) {
            return false;
        }
        if (this.deploymentId == null ? other.deploymentId != null : !this.deploymentId.equals(other.deploymentId)) {
            return false;
        }
        if (this.serverTemplateId == null ? other.serverTemplateId != null : !this.serverTemplateId.equals(other.serverTemplateId)) {
            return false;
        }
        return true;
    }
}
