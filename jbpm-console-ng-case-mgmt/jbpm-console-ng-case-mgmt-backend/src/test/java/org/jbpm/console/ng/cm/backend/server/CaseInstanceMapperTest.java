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

import java.util.Date;

import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseInstance;

import static org.junit.Assert.*;

public class CaseInstanceMapperTest {

    public static void assertCaseInstance(final CaseInstance ci, final CaseInstanceSummary cis) {
        assertNotNull(cis);
        assertEquals(ci.getCaseId(), cis.getCaseId());
        assertEquals(ci.getContainerId(), cis.getContainerId());
        assertEquals(ci.getCaseStatus(), cis.getStatus());
        assertEquals(ci.getCaseDescription(), cis.getDescription());
        assertEquals(ci.getCaseOwner(), cis.getOwner());
        assertEquals(ci.getStartedAt(), cis.getStartedAt());
        assertEquals(ci.getCompletedAt(), cis.getCompletedAt());
        assertEquals(ci.getCaseDefinitionId(), cis.getCaseDefinitionId());
    }

    @Test
    public void testCaseInstanceMapper() {
        final CaseInstance ci = new CaseInstance();
        ci.setCaseDescription("New case");
        ci.setCaseId("CASE-1");
        ci.setCaseStatus(1);
        ci.setContainerId("org.jbpm");
        ci.setCaseDefinitionId("org.jbpm.case");
        ci.setCaseOwner("admin");
        ci.setStartedAt(new Date());
        ci.setCompletedAt(new Date());

        final CaseInstanceSummary cis = new CaseInstanceMapper().apply(ci);

        assertCaseInstance(ci, cis);
    }

}