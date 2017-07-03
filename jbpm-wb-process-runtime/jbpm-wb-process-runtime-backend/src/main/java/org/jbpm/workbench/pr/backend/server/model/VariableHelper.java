/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.kie.server.api.model.instance.VariableInstance;

public class VariableHelper {

    private static final List<String> excludedVariables = Arrays.asList(new String[]{"processId"});

    public static List<ProcessVariableSummary> adaptCollection(List<VariableInstance> variables,
                                                               Map<String, String> properties,
                                                               long processInstanceId,
                                                               String deploymentId,
                                                               String serverTemplateId) {
        List<ProcessVariableSummary> variablesSummary = new ArrayList<ProcessVariableSummary>();
        for (VariableInstance v : variables) {
            if (excludedVariables.contains(v.getVariableName())) {
                continue;
            }
            String type = properties.remove(v.getVariableName());
            variablesSummary.add(new ProcessVariableSummary(v.getVariableName(),
                                                            v.getVariableName(),
                                                            v.getProcessInstanceId(),
                                                            v
                                                                    .getOldValue(),
                                                            v.getValue(),
                                                            v.getDate().getTime(),
                                                            type,
                                                            deploymentId,
                                                            serverTemplateId));
        }
        if (!properties.isEmpty()) {
            for (Entry<String, String> entry : properties.entrySet()) {
                variablesSummary.add(new ProcessVariableSummary(entry.getKey(),
                                                                "",
                                                                processInstanceId,
                                                                "",
                                                                "",
                                                                new Date().getTime(),
                                                                entry.getValue(),
                                                                deploymentId,
                                                                serverTemplateId));
            }
        }

        return variablesSummary;
    }
}
