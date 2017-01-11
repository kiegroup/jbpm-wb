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

package org.jbpm.console.ng.cm.client.actions;

import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.console.ng.cm.client.util.CaseActionStatus;
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;
import org.jbpm.console.ng.cm.util.CaseActionsLists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseAllActionListPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock(name = "view")
    CaseAllActionListPresenter.CaseAllActionListView caseAllActionsView;

    @Mock
    User identity;

    @Mock
    CaseActionsLists caseActionsLists;

    @InjectMocks
    CaseAllActionListPresenter presenter;

    @Override
    public CaseAllActionListPresenter getPresenter() {
        return presenter;
    }

    List<CaseActionSummary> caseActionSummaryList = Lists.newArrayList(createCaseActionSummary());

    CaseInstanceSummary cis;
    String containerId = "containerId",
            caseDefId ="caseDefinitionId",
            caseId = "caseId",
            serverTemplateId = "serverTemplateId";

    private static CaseActionSummary createCaseActionSummary() {
        return CaseActionSummary.builder()
                .id(Long.valueOf(1))
                .name("milestoneName")
                .status(CaseActionStatus.READY.getStatus())
                .build();
    }

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);

        presenter.setCaseService(caseService);

        cis = CaseInstanceSummary.builder().containerId(containerId).caseId(caseId).caseDefinitionId(caseDefId).build();
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefId).build();

        when(caseManagementService.getCaseDefinition(serverTemplateId, cis.getContainerId(), cis.getCaseDefinitionId())).thenReturn(cds);
        when(caseManagementService.getCaseActionsLists(anyString(), anyString(), anyString())).thenReturn(caseActionsLists);

        when(caseActionsLists.getAvailableActionList()).thenReturn(caseActionSummaryList);
        when(caseActionsLists.getInprogressActionList()).thenReturn(caseActionSummaryList);
        when(caseActionsLists.getCompleteActionList()).thenReturn(caseActionSummaryList);

    }

    @Test
    public void testLoadCaseInstance() {

        setupCaseInstance(cis, serverTemplateId);
        verify(caseManagementService).getCaseActionsLists(containerId,caseId,identity.getIdentifier());
        verify(caseAllActionsView).setCaseActionList(CaseActionsFilterBy.AVAILABLE, caseActionSummaryList);
        verify(caseAllActionsView).setCaseActionList(CaseActionsFilterBy.IN_PROGRESS, caseActionSummaryList);
        verify(caseAllActionsView).setCaseActionList(CaseActionsFilterBy.COMPLETED, caseActionSummaryList);
        verify(caseActionsLists).getAvailableActionList();
        verify(caseActionsLists).getInprogressActionList();
        verify(caseActionsLists).getCompleteActionList();

    }

    @Test
    public void testAddDynamicAction() {
        String actionName="dynAct-name";
        String actionDescription="dynAct-name";
        String actionActors="dynAct-actors";
        String actionGroups="dynAct-groups";
        setupCaseInstance(cis, serverTemplateId);
        presenter.addDynamicAction(actionName, actionDescription, actionActors, actionGroups);

        verify(caseManagementService).addDynamicUserTask(eq(containerId), eq(caseId), eq(actionName), eq(actionDescription),eq(actionActors),eq(actionGroups),any());
    }

    @Test
    public void testTriggerAdhocFragment() {
        String adhocFragmentName = "adhocFrag-name";

        setupCaseInstance(cis, serverTemplateId);
        presenter.triggerAdhocFragment(adhocFragmentName);

        verify(caseManagementService).triggerAdHocFragmentInStage(eq(containerId), eq(caseId), eq(""), eq(adhocFragmentName), any());
    }

}
