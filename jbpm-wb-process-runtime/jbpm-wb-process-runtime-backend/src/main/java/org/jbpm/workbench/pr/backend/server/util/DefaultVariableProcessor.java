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

package org.jbpm.workbench.pr.backend.server.util;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.kie.server.api.model.instance.VariableInstance;

public class DefaultVariableProcessor implements VariableHelper.VariableProcessor {

    @Override
    public void process(long processInstanceId, String varName, String varType, List<VariableInstance> variables, Consumer<ProcessVariableSummary> consumer) {
        Optional<VariableInstance> optional = variables.stream()
                .filter(variableInstance -> variableInstance.getVariableName().equals(varName))
                .findAny();

        if (optional.isPresent()) {
            VariableInstance variable = optional.get();
            consumer.accept(new ProcessVariableSummary(variable.getVariableName(), variable.getVariableName(), variable.getProcessInstanceId(), variable.getOldValue(), variable.getValue(), variable.getDate().getTime(), varType));
            variables.remove(variable);
        } else {
            consumer.accept(new ProcessVariableSummary(varName, "", processInstanceId, "", "", new Date().getTime(), varType));
        }
    }

    @Override
    public String getSupportedType() {
        return null;
    }
}
