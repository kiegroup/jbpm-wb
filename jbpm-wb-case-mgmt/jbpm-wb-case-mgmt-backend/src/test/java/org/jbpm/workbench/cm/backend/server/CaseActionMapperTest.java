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

package org.jbpm.workbench.cm.backend.server;

import java.util.Date;

import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseAdHocFragment;
import org.kie.server.api.model.instance.NodeInstance;

import static org.junit.Assert.*;

public class CaseActionMapperTest {

    static private final String STAGE_ID = "StageId";
    static private final String HUMAN_TASK_OWNER = "humanTaskOwner";

    public static void assertCaseActionAdHocFragment(final CaseAdHocFragment cc,
                                                     final CaseActionSummary ccs) {
        assertNotNull(ccs);
        assertNotNull(cc);

        assertEquals(cc.getName(),
                     ccs.getName());
        assertEquals(cc.getType(),
                     ccs.getType());
        assertEquals(CaseActionType.AD_HOC_TASK,
                     ccs.getActionType());
        assertEquals(CaseActionStatus.AVAILABLE,
                     ccs.getActionStatus());
    }

    public static void assertCaseActionAdHocFragmentWithStage(final CaseAdHocFragment cc,
                                                              final CaseActionSummary ccs) {
        assertCaseActionAdHocFragment(cc,
                                      ccs);
        assertNotNull(ccs.getStage());
    }

    public static void assertCaseActionNodeInstance(final NodeInstance nodeInstance,
                                                    final CaseActionSummary caseActionSummary) {
        assertNotNull(caseActionSummary);
        assertNotNull(nodeInstance);

        assertEquals(nodeInstance.getId(),
                     caseActionSummary.getId());
        assertEquals(nodeInstance.getName(),
                     caseActionSummary.getName());
        assertEquals(nodeInstance.getNodeType(),
                     caseActionSummary.getType());
        assertEquals(nodeInstance.getDate(),
                     caseActionSummary.getCreatedOn());
        assertNotNull(caseActionSummary.getActionStatus());
    }

    @Test
    public void testCaseActionAdHocFragmentMapper_mapCaseAction() {
        final CaseAdHocFragment cc = CaseAdHocFragment.builder()
                .name("AdhocFragment-name")
                .type("AdhocFragment-type")
                .build();

        final CaseActionSummary ccs = new CaseActionAdHocMapper(CaseStageSummary.builder().identifier(STAGE_ID).build()).apply(cc);
        assertCaseActionAdHocFragmentWithStage(cc,
                                               ccs);
    }

    @Test
    public void testCaseActionAdHocFragmentMapper_mapNull() {
        final CaseAdHocFragment cc = null;
        final CaseActionSummary ccs = new CaseActionAdHocMapper().apply(cc);
        assertNull(ccs);
    }

    @Test
    public void testCaseActionNodeInstanceMapper_mapCaseAction() {
        final NodeInstance nodeInstance = NodeInstance.builder()
                .name("NodeInst-name")
                .nodeType("NodeInst-type")
                .date(new Date())
                .build();
        final CaseActionSummary caseActionSummary = new CaseActionNodeInstanceMapper(HUMAN_TASK_OWNER,
                                                                                     CaseActionStatus.IN_PROGRESS)
                .apply(nodeInstance);

        assertCaseActionNodeInstance(nodeInstance,
                                     caseActionSummary);
        assertEquals(HUMAN_TASK_OWNER,
                     caseActionSummary.getActualOwner());
        assertEquals(CaseActionStatus.IN_PROGRESS,
                     caseActionSummary.getActionStatus());
    }

    @Test
    public void testCaseActionNodeInstanceMapper_mapNull() {
        final NodeInstance nodeInstance = null;
        final CaseActionSummary ccs = new CaseActionNodeInstanceMapper(HUMAN_TASK_OWNER,
                                                                       CaseActionStatus.IN_PROGRESS).apply(nodeInstance);

        assertNull(ccs);
    }
}
