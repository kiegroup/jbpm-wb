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
import org.jbpm.workbench.cm.util.CaseActionType;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseAdHocFragment;
import org.kie.server.api.model.instance.NodeInstance;


import static org.junit.Assert.*;

public class CaseActionMapperTest {

    public static void assertCaseActionAdHocFragment(final CaseAdHocFragment cc, final CaseActionSummary ccs) {
        assertNotNull(ccs);
        assertNotNull(cc);
        assertEquals(cc.getName(), ccs.getName());
        assertEquals(cc.getType(), ccs.getType());
        assertEquals(CaseActionType.AD_HOC, ccs.getActionType());
    }

    @Test
    public void testCaseActionAdHocFragmentMapper_mapCaseAction() {
        final CaseAdHocFragment cc = CaseAdHocFragment.builder()
                .name("AdhocFragment-name")
                .type("AdhocFragment-type")
                .build();

        final CaseActionSummary ccs = new CaseActionAdHocMapper("").apply(cc);
        assertCaseActionAdHocFragment(cc, ccs);
    }

    @Test
    public void testCaseActionAdHocFragmentMapper_mapNull() {
        final CaseAdHocFragment cc = null;
        final CaseActionSummary ccs = new CaseActionAdHocMapper("").apply(cc);
        assertNull(ccs);
    }

    public static void assertCaseActionNodeInstance(final NodeInstance nodeInstance, final CaseActionSummary caseActionSummary) {
        assertNotNull(caseActionSummary);
        assertNotNull(nodeInstance);
        assertEquals(nodeInstance.getName(), caseActionSummary.getName());
        assertEquals(nodeInstance.getNodeType(), caseActionSummary.getType());
        assertEquals(nodeInstance.getDate(), caseActionSummary.getCreatedOn());
    }

    @Test
    public void testCaseActionNodeInstanceMapper_mapCaseAction() {
        String actualOwner = "actualOwner";

        final NodeInstance nodeInstance = NodeInstance.builder()
                .name("NodeInst-name")
                .nodeType("NodeInst-type")
                .date(new Date())
                .build();

        final CaseActionSummary caseActionSummary = new CaseActionNodeInstanceMapper(actualOwner, CaseActionType.INPROGRESS).apply(nodeInstance);
        assertCaseActionNodeInstance(nodeInstance, caseActionSummary);
        assertEquals(actualOwner, caseActionSummary.getActualOwner());
        assertEquals(CaseActionType.INPROGRESS, caseActionSummary.getActionType());
    }

    @Test
    public void testCaseActionNodeInstanceMapper_mapNull() {
        final NodeInstance nodeInstance = null;
        final CaseActionSummary ccs = new CaseActionNodeInstanceMapper("", CaseActionType.INPROGRESS).apply(nodeInstance);
        assertNull(ccs);
    }

}
