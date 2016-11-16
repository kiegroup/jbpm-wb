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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.console.ng.cm.model.CaseMilestoneSummary;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseMilestone;

import static org.junit.Assert.*;

public class CaseMilestoneMapperTest {

    public static void assertCaseMilestone(final CaseMilestone cm, final CaseMilestoneSummary cis) {
        assertNotNull(cis);
        assertEquals(cm.getIdentifier(), cis.getIdentifier());
        assertEquals(cm.getName(), cis.getName());
        assertEquals(cm.getAchievedAt(), cis.getAchievedAt());
        assertEquals(cm.getStatus(), cis.getStatus());
        assertEquals(cm.isAchieved(), cis.isAchieved());
    }

    @Test
    public void testCaseInstanceMapper() {
        final CaseMilestone cm = new CaseMilestone();
        cm.setIdentifier("Milestone1");
        cm.setName("Milestone 1");
        cm.setAchieved(true);
        cm.setAchievedAt(new Date());
        cm.setStatus("OPEN");

        List<CaseMilestone> cmList= new ArrayList();
        cmList.add(cm);

        final CaseMilestoneSummary cms = new CaseMilestoneMapper().apply(cm);

        assertCaseMilestone(cm, cms);
    }

}