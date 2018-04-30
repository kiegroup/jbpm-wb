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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseAdHocFragment;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.api.model.cases.CaseStage;

import static org.junit.Assert.*;

public class CaseInstanceMapperTest {

    static void assertCaseInstance(final CaseInstance ci,
                                   final CaseInstanceSummary cis) {
        assertNotNull(cis);
        assertEquals(ci.getCaseId(),
                     cis.getCaseId());
        assertEquals(ci.getContainerId(),
                     cis.getContainerId());
        assertEquals(ci.getCaseStatus(),
                     cis.getStatus().getId());
        assertEquals(ci.getCaseDescription(),
                     cis.getDescription());
        assertEquals(ci.getCaseOwner(),
                     cis.getOwner());
        assertEquals(ci.getStartedAt(),
                     cis.getStartedAt());
        assertEquals(ci.getCompletedAt(),
                     cis.getCompletedAt());
        assertEquals(ci.getCaseDefinitionId(),
                     cis.getCaseDefinitionId());
        assertCaseStages(ci.getStages(),
                         cis.getStages());
    }

    public static void assertCaseStages(final List<CaseStage> csl,
                                        final List<CaseStageSummary> cssl) {
        assertNotNull(cssl);
        if (csl == null) {
            assertEquals(0,
                         cssl.size());
        } else {
            assertEquals(cssl.size(),
                         csl.size());

            CaseStage caseStage;
            CaseStageSummary caseStageSummary;
            for (int i = 0; i < csl.size(); i++) {
                caseStage = csl.get(i);
                caseStageSummary = cssl.get(i);
                assertEquals(caseStageSummary.getName(),
                             caseStage.getName());
                assertEquals(caseStageSummary.getIdentifier(),
                             caseStage.getIdentifier());
                assertEquals(caseStageSummary.getStatus(),
                             caseStage.getStatus());
                assertCaseStageAdHocFragments(caseStage.getAdHocFragments(),
                                              caseStageSummary.getAdHocActions(),
                                              caseStage.getIdentifier());
            }
        }
    }

    private static void assertCaseStageAdHocFragments(final List<CaseAdHocFragment> cahfl,
                                                      final List<CaseActionSummary> casl,
                                                      final String stageId) {
        assertNotNull(casl);
        if (cahfl == null) {
            assertEquals(0,
                         casl.size());
        } else {
            assertEquals(casl.size(),
                         cahfl.size());

            CaseAdHocFragment caseAdHocFragment;
            CaseActionSummary caseActionSummary;
            for (int i = 0; i < cahfl.size(); i++) {
                caseAdHocFragment = cahfl.get(i);
                caseActionSummary = casl.get(i);
                assertEquals(caseActionSummary.getName(),
                             caseAdHocFragment.getName());
                assertNotNull(caseActionSummary.getStage());
                assertEquals(stageId,
                             caseActionSummary.getStage().getIdentifier());
                assertEquals(CaseActionType.AD_HOC_TASK,
                             caseActionSummary.getActionType());
            }
        }
    }

    @Test
    public void testCaseInstanceMapper_mapCaseInstance() {
        final CaseInstance ci = createCaseInstance();
        final List<CaseStage> stagesList = new ArrayList();
        final List<CaseAdHocFragment> stageAdHocFragments = new ArrayList();
        stageAdHocFragments.add(CaseAdHocFragment.builder().name("ad_hoc_stage_f1_name").build());
        stageAdHocFragments.add(CaseAdHocFragment.builder().name("ad_hoc_stage_f2_name").build());

        stagesList.add(CaseStage.builder().name("stage1").status("Available").id("stage1").adHocFragments(stageAdHocFragments).build());
        stagesList.add(CaseStage.builder().name("stage2").status("Completed").id("stage2").build());
        ci.setStages(stagesList);

        final CaseInstanceSummary cis = new CaseInstanceMapper().apply(ci);

        assertCaseInstance(ci,
                           cis);
    }

    @Test
    public void testCaseInstanceMapper_mapNull() {
        final CaseInstance ci = null;
        final CaseInstanceSummary cis = new CaseInstanceMapper().apply(ci);
        assertNull(cis);
    }

    private CaseInstance createCaseInstance() {
        return CaseInstance.builder()
                .caseDescription("New case")
                .caseId("CASE-1")
                .caseStatus(1)
                .containerId("org.jbpm")
                .caseDefinitionId("org.jbpm.case")
                .caseOwner("admin")
                .startedAt(new Date())
                .completedAt(new Date())
                .build();
    }
}