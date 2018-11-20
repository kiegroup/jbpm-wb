/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;
import java.util.function.Function;

import org.jbpm.workbench.pr.model.WorkItemParameterSummary;
import org.jbpm.workbench.pr.model.WorkItemSummary;
import org.kie.server.api.model.instance.WorkItemInstance;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class WorkItemSummaryMapper implements Function<WorkItemInstance, WorkItemSummary> {

    @Override
    public WorkItemSummary apply(final WorkItemInstance workItemInstance) {
        final Optional<WorkItemInstance> request = ofNullable(workItemInstance);
        if (request.isPresent() == false) {
            return null;
        }

        WorkItemSummary summary = WorkItemSummary.builder()
                .id(workItemInstance.getId())
                .name(workItemInstance.getName())
                .state(workItemInstance.getState())
                .parameters(request.map(r -> r.getParameters())
                                    .orElse(emptyMap())
                                    .entrySet()
                                    .stream()
                                    .map(e -> new WorkItemParameterSummary(e.getKey(),
                                                                           String.valueOf(e.getValue())))
                                    .collect(toList()))
                .build();

        return summary;
    }
}
