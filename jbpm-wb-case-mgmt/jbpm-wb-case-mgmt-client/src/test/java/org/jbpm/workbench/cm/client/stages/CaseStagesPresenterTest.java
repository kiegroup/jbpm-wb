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

package org.jbpm.workbench.cm.client.stages;

import com.google.common.collect.Lists;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.CaseStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseStagesPresenterTest extends AbstractCaseInstancePresenterTest {

    final static String serverTemplateId = "serverTemplateId",
            containerId = "containerId",
            caseDefId = "caseDefinitionId",
            caseId = "caseId";

    @Mock
    CaseStagesPresenter.CaseStagesView caseStagesView;

    @InjectMocks

    CaseStagesPresenter presenter;

    CaseInstanceSummary cis;

    private static CaseStageSummary createCaseStageSummary() {
        return CaseStageSummary.builder()
                .identifier("stage1")
                .name("stageName")
                .status(CaseStageStatus.AVAILABLE.getStatus())
                .build();
    }

    private static CaseInstanceSummary createCaseInstance() {
        return CaseInstanceSummary.builder()
                .caseId(caseId)
                .caseDefinitionId(caseDefId)
                .description("description")
                .status(CaseStatus.OPEN)
                .containerId(containerId)
                .stages(Lists.newArrayList(createCaseStageSummary()))
                .build();
    }

    @Override
    public CaseStagesPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void init() {
        cis = createCaseInstance();
        caseService = new CallerMock<>(caseManagementService);

        presenter.setCaseService(caseService);

        cis = CaseInstanceSummary.builder().containerId(containerId).caseId(caseId).caseDefinitionId(caseDefId).build();
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefId).build();

        when(caseManagementService.getCaseDefinition(serverTemplateId,
                                                     cis.getContainerId(),
                                                     cis.getCaseDefinitionId())).thenReturn(cds);
        when(caseManagementService.getCaseInstance(serverTemplateId,
                                                   containerId,
                                                   caseId)).thenReturn(cis);
    }

    @Test
    public void testClearCaseInstance() {
        presenter.clearCaseInstance();

        verifyClearCaseInstance();
    }

    private void verifyClearCaseInstance() {
        verify(caseStagesView).removeAllStages();
    }

    @Test
    public void testLoadCaseInstance() {
        setupCaseInstance(cis,
                          serverTemplateId);

        verifyClearCaseInstance();
        verify(caseStagesView).setCaseStagesList(cis.getStages());
    }
}