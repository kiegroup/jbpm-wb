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

package org.jbpm.console.ng.cm.backend.server;

import java.util.function.Function;

import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.kie.server.api.model.cases.CaseDefinition;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

public class CaseDefinitionMapper implements Function<CaseDefinition, CaseDefinitionSummary> {

    @Override
    public CaseDefinitionSummary apply(final CaseDefinition cd) {
        if (cd == null) {
            return null;
        }

        final CaseDefinitionSummary cds = new CaseDefinitionSummary();
        cds.setId(cd.getIdentifier());
        cds.setName(cd.getName());
        cds.setContainerId(cd.getContainerId());
        cds.setRoles(ofNullable(cd.getRoles()).orElse(emptyMap()));
        return cds;
    }
}
