/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.bd.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.service.ItemKey;

@Portable
public class ProcessInstanceKey implements ItemKey {

    private String serverTemplateId;
    private Long processInstanceId;

    public ProcessInstanceKey() {
    }

    public ProcessInstanceKey(String serverTemplateId, Long processInstanceId) {
        this.serverTemplateId = serverTemplateId;
        this.processInstanceId = processInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.serverTemplateId != null ? this.serverTemplateId.hashCode() : 0);
        hash = ~~hash;
        hash = 13 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
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
        final ProcessInstanceKey other = (ProcessInstanceKey) obj;
        if (this.serverTemplateId != other.serverTemplateId && (this.serverTemplateId == null || !this.serverTemplateId.equals(other.serverTemplateId))) {
            return false;
        }
        if (this.processInstanceId != other.processInstanceId && (this.processInstanceId == null || !this.processInstanceId.equals(other.processInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProcessInstanceKey{" + "processInstanceId=" + processInstanceId + '}';
    }

}
