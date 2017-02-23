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

import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.ProcessDefinitionSummary;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.definition.ProcessDefinition;

import static org.junit.Assert.*;

public class ProcessDefinitionMapperTest {

    public static void assertCaseDefinition(final ProcessDefinition pd, final ProcessDefinitionSummary pds) {
        assertNotNull(pds);

        assertEquals(pd.getName(), pds.getName());
        assertEquals(pd.getId(), pds.getId());
        assertEquals(pd.getContainerId(), pds.getContainerId());
        assertEquals(pd.getVersion(), pds.getVersion());
        assertEquals(pd.getPackageName(), pds.getPackageName());

    }

    @Test
    public void testProcessDefinitionMapper_mapProcessDefinition() {
        final ProcessDefinition pd = new ProcessDefinition();
        pd.setId("org.jbpm.case");
        pd.setName("New case");
        pd.setContainerId("org.jbpm");
        pd.setVersion("1.0");
        pd.setPackageName("packageName");

        final ProcessDefinitionSummary pds = new ProcessDefinitionMapper().apply(pd);
        assertCaseDefinition(pd, pds);
    }

    @Test
    public void testCaseDefinitionMapper_mapNull() {
        final CaseDefinition cd = null;
        final CaseDefinitionSummary cds = new CaseDefinitionMapper().apply(cd);
        assertNull(cds);
    }

}