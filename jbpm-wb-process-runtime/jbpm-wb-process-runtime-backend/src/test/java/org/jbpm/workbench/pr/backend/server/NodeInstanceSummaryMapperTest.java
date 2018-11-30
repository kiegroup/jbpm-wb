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

import java.util.Date;

import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.NodeInstance;

import static org.junit.Assert.*;

public class NodeInstanceSummaryMapperTest {

    public static void assertNodeInstanceSummary(final NodeInstance ni,
                                                 final NodeInstanceSummary ns) {
        assertNotNull(ns);

        assertEquals(ni.getId(),
                     ns.getId());
        assertEquals(ni.getName(),
                     ns.getName());
        assertEquals(ni.getProcessInstanceId(),
                     ns.getProcessId());
        assertEquals(ni.getDate(),
                     ns.getTimestamp());
        assertEquals(ni.getNodeId(),
                     ns.getNodeUniqueName());
        assertEquals(ni.getNodeType(),
                     ns.getType());
        assertEquals(ni.getConnection(),
                     ns.getConnection());
        assertEquals(ni.getCompleted(),
                     ns.isCompleted());
        assertEquals(ni.getReferenceId(),
                     ns.getReferenceId());
        assertEquals(ni.getSlaCompliance(),
                     ns.getSlaCompliance());
        assertEquals(ni.getSlaDueDate(),
                     ns.getSlaDueDate());
    }

    @Test
    public void testNodeInstanceSummaryMapper() {
        NodeInstance ni = NodeInstance.builder()
                .id(1l).name("name-3")
                .nodeId("123_123")
                .processInstanceId(2l)
                .referenceId(4l)
                .slaCompliance(4)
                .slaDueDate(new Date())
                .connection("con")
                .workItemId(3l)
                .date(new Date())
                .nodeType("HumanTask")
                .completed(true)
                .build();

        assertNodeInstanceSummary(ni,
                                  new NodeInstanceSummaryMapper().apply(ni));
    }

    @Test
    public void testNodeInstanceSummaryMapperNull() {
        assertNull(new NodeInstanceSummaryMapper().apply(null));
    }
}
