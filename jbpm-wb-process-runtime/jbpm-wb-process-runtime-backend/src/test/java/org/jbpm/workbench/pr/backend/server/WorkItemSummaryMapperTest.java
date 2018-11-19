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

import org.jbpm.workbench.pr.model.WorkItemSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.WorkItemInstance;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

public class WorkItemSummaryMapperTest {

    public static void assertWorkItemSummary(final WorkItemInstance wii,
                                             final WorkItemSummary wis) {
        assertNotNull(wii);
        assertNotNull(wis);

        assertEquals(wii.getId(),
                     wis.getId());
        assertEquals(wii.getName(),
                     wis.getName());
        if (wii.getParameters() != null) {
            assertEquals(wii.getParameters().size(),
                         wis.getParameters().size());
            wis.getParameters()
                    .stream()
                    .forEach(wisp -> assertEquals(wii.getParameters().get(wisp.getId()).toString(),
                                                  wisp.getName()));
        } else {
            assertNull(wis.getParameters());
        }
    }

    @Test
    public void testWorkItemSummaryMapper_mapWorkItemSummary() {

        final WorkItemInstance workItemInstance = WorkItemInstance.builder()
                .id(1L)
                .name("Human Task")
                .parameters(singletonMap("initiator",
                                         "String"))
                .build();

        assertWorkItemSummary(workItemInstance,
                              new WorkItemSummaryMapper().apply(workItemInstance));
    }

    @Test
    public void testWorkItemSummaryMapper_mapNull() {
        assertNull(new WorkItemSummaryMapper().apply(null));
    }
}
