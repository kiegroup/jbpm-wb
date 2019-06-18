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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jbpm.workbench.pr.backend.server.ProcessInstanceVariableMapper;
import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.kie.server.api.model.instance.VariableInstance;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.Comparator.comparing;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

public class VariableHelper {

    public static final Collection<String> DOCUMENT_TYPES;

    public static final String JBPM_DOCUMENT = "org.jbpm.document.Document";

    public static final String DOCUMENT_COLLECTION = "org.jbpm.document.DocumentCollection";
    public static final String DOCUMENT_COLLECTION_IMPL = "org.jbpm.document.service.impl.DocumentCollectionImpl";
    public static final String LEGACY_DOCUMENTS = "org.jbpm.document.Documents";

    private static final List<String> excludedVariables = Collections.singletonList("processId");

    private static final Map<String, VariableProcessor> processors = new HashMap<>();

    static {
        DOCUMENT_TYPES = Arrays.asList(JBPM_DOCUMENT, DOCUMENT_COLLECTION, DOCUMENT_COLLECTION_IMPL, LEGACY_DOCUMENTS);

        registerProcessor(new DocumentsVariableProcessor(DOCUMENT_COLLECTION));
        registerProcessor(new DocumentsVariableProcessor(DOCUMENT_COLLECTION_IMPL));
        registerProcessor(new DocumentsVariableProcessor(LEGACY_DOCUMENTS));
    }

    public static void registerProcessor(VariableProcessor processor) {
        processors.put(processor.getSupportedType(), processor);
    }

    public static List<ProcessVariableSummary> adaptCollection(List<VariableInstance> variables,
                                                               Map<String, String> properties,
                                                               long processInstanceId,
                                                               String deploymentId,
                                                               String serverTemplateId,
                                                               String sortBy,
                                                               boolean asc) {

        List<VariableInstance> filteredVariables = variables.stream()
                .filter(variable -> !excludedVariables.contains(variable.getVariableName()))
                .collect(Collectors.toList());

        List<ProcessVariableSummary> variablesSummary = new ArrayList<>();

        properties.forEach((key, value) -> {
            VariableProcessor processor = processors.getOrDefault(value, new DefaultVariableProcessor());
            processor.process(processInstanceId, key, value, filteredVariables, deploymentId, serverTemplateId,
                              variableSummary -> variablesSummary.add(variableSummary));
        });

        variablesSummary.addAll(ofNullable(filteredVariables).orElse(emptyList())
                                        .stream()
                                        .map(new ProcessInstanceVariableMapper(deploymentId, serverTemplateId, ""))
                                        .collect(Collectors.toList()));

        Comparator<ProcessVariableSummary> comparator;
        switch (ofNullable(sortBy).orElse(COL_PROCESS_INSTANCE_VAR_ID)) {
            case COL_PROCESS_INSTANCE_VAR_ID:
                comparator = comparing(ProcessVariableSummary::getVariableId);
                break;
            case COL_PROCESS_INSTANCE_VAR_VALUE:
                comparator = comparing(ProcessVariableSummary::getNewValue);
                break;
            case COL_PROCESS_INSTANCE_VAR_TYPE:
                comparator = comparing(ProcessVariableSummary::getType);
                break;
            case COL_PROCESS_INSTANCE_VAR_LASTMOD:
                comparator = comparing(ProcessVariableSummary::getTimestamp);
                break;
            default:
                comparator = comparing(ProcessVariableSummary::getName);
        }

        variablesSummary.sort(asc ? comparator : comparator.reversed());

        return variablesSummary;
    }

    public interface VariableProcessor {

        String getSupportedType();

        void process(long processInstanceId, String varName, String varType, List<VariableInstance> variables,
                     String deploymentId, String serverTemplateId, Consumer<ProcessVariableSummary> consumer);
    }
}
