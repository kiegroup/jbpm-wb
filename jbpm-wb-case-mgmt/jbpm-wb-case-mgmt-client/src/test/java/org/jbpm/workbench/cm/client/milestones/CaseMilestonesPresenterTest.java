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

package org.jbpm.workbench.cm.client.milestones;

import java.util.List;

import com.google.common.collect.Lists;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.client.util.CaseMilestoneStatus;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseMilestonesPresenterTest extends AbstractCaseInstancePresenterTest {

    final String serverTemplateId = "serverTemplateId",
            containerId = "containerId",
            caseDefId = "caseDefinitionId",
            caseId = "caseId";

    @Mock
    CaseMilestoneListPresenter.CaseMilestoneListView caseMilestoneListView;

    @InjectMocks
    CaseMilestoneListPresenter presenter;

    List<CaseMilestoneSummary> caseMilestonesSummaryList = Lists.newArrayList(createCaseMilestone());

    CaseInstanceSummary cis;

    private static CaseMilestoneSummary createCaseMilestone() {
        return CaseMilestoneSummary.builder()
                .identifier("identifier")
                .name("milestoneName")
                .status(CaseMilestoneStatus.AVAILABLE.getStatus())
                .achieved(false)
                .build();
    }

    @Override
    public CaseMilestoneListPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);
        when(caseManagementService.getCaseMilestones(anyString(),
                                                     anyString(),
                                                     any(CaseMilestoneSearchRequest.class))).thenReturn(caseMilestonesSummaryList);
        when(caseMilestoneListView.getCaseMilestoneSearchRequest()).thenReturn(new CaseMilestoneSearchRequest());
        presenter.setCaseService(caseService);

        cis = CaseInstanceSummary.builder().containerId(containerId).caseId(caseId).caseDefinitionId(caseDefId).build();
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefId).build();

        when(caseManagementService.getCaseDefinition(serverTemplateId,
                                                     cis.getContainerId(),
                                                     cis.getCaseDefinitionId())).thenReturn(cds);

        List<CaseMilestoneSummary> milestones = singletonList(createCaseMilestone());
        when(caseManagementService.getCaseMilestones(anyString(),
                                                     anyString(),
                                                     any(CaseMilestoneSearchRequest.class))).thenReturn(milestones);
    }

    @Test
    public void testClearCaseInstance() {
        presenter.clearCaseInstance();

        verifyClearCaseInstance();
    }

    private void verifyClearCaseInstance() {
        verify(caseMilestoneListView).removeAllMilestones();
    }

    @Test
    public void testLoadCaseInstance() {
        List<CaseMilestoneSummary> milestones = singletonList(createCaseMilestone());
        when(caseManagementService.getCaseMilestones(anyString(),
                                                     anyString(),
                                                     any(CaseMilestoneSearchRequest.class))).thenReturn(milestones);

        setupCaseInstance(cis,
                          serverTemplateId);

        verifyClearCaseInstance();
        verify(caseMilestoneListView).setCaseMilestoneList(milestones);
    }

    @Test
    public void testRefreshData() {
        setupCaseInstance(cis,
                          serverTemplateId);
        presenter.searchCaseMilestones();

        verify(caseManagementService,
               times(2)).getCaseMilestones(cis.getContainerId(),
                                           cis.getCaseId(),
                                           caseMilestoneListView.getCaseMilestoneSearchRequest());
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(caseMilestoneListView,
               times(2)).setCaseMilestoneList(captor.capture());
        assertEquals(caseMilestonesSummaryList.size(),
                     captor.getValue().size());
    }
}