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

package org.jbpm.workbench.pr.backend.server;

import org.jbpm.workbench.pr.model.ProcessSummary;
import org.junit.Test;
import org.kie.server.api.model.definition.ProcessDefinition;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

public class ProcessSummaryMapperTest {

    public static void assertProcessSummary(final ProcessDefinition pd,
                                            final ProcessSummary ps) {
        assertNotNull(ps);

        assertEquals(pd.getId(),
                     ps.getId());
        assertEquals(pd.getId(),
                     ps.getProcessDefId());
        assertEquals(pd.getName(),
                     ps.getName());
        assertEquals(pd.getName(),
                     ps.getProcessDefName());
        assertEquals(pd.isDynamic(),
                     ps.isDynamic());
        assertEquals(pd.getVersion(),
                     ps.getVersion());
        assertEquals(pd.getContainerId(),
                     ps.getDeploymentId());
        assertEquals(pd.getAssociatedEntities(),
                     ps.getAssociatedEntities());
        assertEquals(pd.getProcessVariables(),
                     ps.getProcessVariables());
        assertEquals(pd.getReusableSubProcesses(),
                     ps.getReusableSubProcesses());
        assertEquals(pd.getServiceTasks(),
                     ps.getServiceTasks());
    }

    @Test
    public void testProcessSummaryMapper_mapProcessSummary() {
        final ProcessDefinition pd = new ProcessDefinition();
        pd.setName("definitionName");
        pd.setId("definitionId");
        pd.setDynamic(true);
        pd.setContainerId("containerId");
        pd.setVersion("1.0");
        pd.setAssociatedEntities(singletonMap("e1",
                                              new String[0]));
        pd.setProcessVariables(singletonMap("initiator",
                                            "String"));
        pd.setReusableSubProcesses(singletonList("processOne"));
        pd.setServiceTasks(singletonMap("email",
                                        "org.jbpm"));

        assertProcessSummary(pd,
                             new ProcessSummaryMapper().apply(pd));
    }

    @Test
    public void testProcessSummaryMapper_mapNull() {
        assertNull(new ProcessSummaryMapper().apply(null));
    }
}
