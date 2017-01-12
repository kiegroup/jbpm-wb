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

package org.jbpm.workbench.cm.backend.server;

import java.util.Collections;

import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseDefinition;

import static org.junit.Assert.*;

public class CaseDefinitionMapperTest {

    public static void assertCaseDefinition(final CaseDefinition cd, final CaseDefinitionSummary cds) {
        assertNotNull(cds);

        assertEquals(cd.getName(), cds.getName());
        assertEquals(cd.getIdentifier(), cds.getId());
        assertEquals(cd.getContainerId(), cds.getContainerId());
        assertEquals(cd.getRoles(), cds.getRoles());
    }

    @Test
    public void testCaseDefinitionMapper_mapCaseDefinition() {
        final CaseDefinition cd = new CaseDefinition();
        cd.setIdentifier("org.jbpm.case");
        cd.setName("New case");
        cd.setContainerId("org.jbpm");
        cd.setRoles(Collections.singletonMap("participant", 2));

        final CaseDefinitionSummary cds = new CaseDefinitionMapper().apply(cd);

        assertCaseDefinition(cd, cds);
    }

    @Test
    public void testCaseDefinitionMapper_mapNull() {
        final CaseDefinition cd = null;
        final CaseDefinitionSummary cds = new CaseDefinitionMapper().apply(cd);
        assertNull(cds);
    }

}