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
package org.jbpm.workbench.pr.backend.server;

import java.util.function.Function;

import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.kie.server.api.model.instance.VariableInstance;

public class ProcessInstanceVariableMapper implements Function<VariableInstance, ProcessVariableSummary> {

    private String deploymentId;
    private String serverTemplateId;
    private String type = "";

    public ProcessInstanceVariableMapper(String deploymentId, String serverTemplateId, String type) {
        this.deploymentId = deploymentId;
        this.serverTemplateId = serverTemplateId;
        this.type = type;
    }

    @Override
    public ProcessVariableSummary apply(final VariableInstance variableInstance) {
        if (variableInstance == null) {
            return null;
        }

        return new ProcessVariableSummary(variableInstance.getVariableName(), variableInstance.getVariableName(),
                                          variableInstance.getProcessInstanceId(), variableInstance.getOldValue(),
                                          variableInstance.getValue(), variableInstance.getDate().getTime(), type,
                                          deploymentId, serverTemplateId);
    }
}
