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

package org.jbpm.workbench.cm.client.actions;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.ProcessDefinitionSummary;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionsPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock(name = "view")
    CaseActionsPresenter.CaseActionsView caseAllActionsView;

    @Mock(name = "NewActionView")
    CaseActionsPresenter.NewActionView newActionViewMock;

    @Mock
    User identity;

    @Mock
    Actions actions;

    @InjectMocks
    CaseActionsPresenter presenter;

    List<CaseActionSummary> caseActionSummaryList = Lists.newArrayList(createCaseActionSummary());
    CaseInstanceSummary cis;
    String containerId = "containerId",
            caseDefId = "caseDefinitionId",
            caseId = "caseId",
            serverTemplateId = "serverTemplateId";

    private static CaseActionSummary createCaseActionSummary() {
        return CaseActionSummary.builder()
                .id(Long.valueOf(1))
                .name("actionName")
                .status(CaseActionType.INPROGRESS)
                .build();
    }

    @Override
    public CaseActionsPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);

        presenter.setCaseService(caseService);

        cis = CaseInstanceSummary.builder().containerId(containerId).caseId(caseId).caseDefinitionId(caseDefId).build();
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefId).build();

        when(caseManagementService.getCaseDefinition(serverTemplateId, cis.getContainerId(), cis.getCaseDefinitionId())).thenReturn(cds);
        when(caseManagementService.getCaseActions(anyString(), anyString(), anyString(), anyString())).thenReturn(actions);

        when(actions.getAvailableActions()).thenReturn(caseActionSummaryList);
        when(actions.getInProgressAction()).thenReturn(caseActionSummaryList);
        when(actions.getCompleteActions()).thenReturn(caseActionSummaryList);
    }

    @Test
    public void testLoadCaseInstance() {
        String subProcessName ="Subprocess1";
        List<ProcessDefinitionSummary> pdsl = Arrays.asList(ProcessDefinitionSummary.builder().id("processId").name(subProcessName).build());
        when(caseManagementService.getProcessDefinitions(containerId)).thenReturn(pdsl);

        setupCaseInstance(cis, serverTemplateId);
        verify(caseManagementService).getCaseActions(serverTemplateId, containerId, caseId, identity.getIdentifier());

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        verify(caseAllActionsView).setAvailableActionsList(captor.capture());
        assertEquals(caseActionSummaryList.size() + 2, captor.getValue().size());
        assertEquals(CaseActionType.ADD_DYNAMIC_USER_TASK, ((CaseActionSummary) captor.getValue().get(0)).getActionType());
        assertEquals(CaseActionType.ADD_DYNAMIC_SUBPROCESS_TASK, ((CaseActionSummary) captor.getValue().get(1)).getActionType());

        verify(caseAllActionsView).setInProgressActionsList(caseActionSummaryList);
        verify(caseAllActionsView).setCompletedActionsList(caseActionSummaryList);
        verify(actions).getAvailableActions();
        verify(actions).getInProgressAction();
        verify(actions).getCompleteActions();
        verify(newActionViewMock, times(2)).setCaseStagesList(cis.getStages());
        final ArgumentCaptor<List> captor2 = ArgumentCaptor.forClass(List.class);

        verify(caseManagementService).getProcessDefinitions(containerId);
        verify(newActionViewMock).setProcessDefinitions(captor.capture());
        assertEquals(subProcessName,captor.getValue().get(0));
    }

    @Test
    public void testAddDynamicAction() {
        String actionName = "dynAct-name";
        String actionDescription = "dynAct-name";
        String actionActors = "dynAct-actors";
        String actionGroups = "dynAct-groups";
        setupCaseInstance(cis, serverTemplateId);
        presenter.addDynamicUserTaskAction(actionName, actionDescription, actionActors, actionGroups, null);

        verify(caseManagementService).addDynamicUserTask(eq(containerId), eq(caseId), eq(actionName),
                eq(actionDescription), eq(actionActors), eq(actionGroups), any());
    }

    @Test
    public void testAddDynamicActionInStage() {
        String actionName = "dynAct-name";
        String actionDescription = "dynAct-name";
        String actionActors = "dynAct-actors";
        String actionGroups = "dynAct-groups";
        String stageId = "dynAct-groups";
        setupCaseInstance(cis, serverTemplateId);


        presenter.addDynamicUserTaskAction(actionName, actionDescription, actionActors, actionGroups, stageId);

        verify(caseManagementService).addDynamicUserTaskToStage(eq(containerId), eq(caseId), eq(stageId), eq(actionName),
                eq(actionDescription), eq(actionActors), eq(actionGroups), any());
    }

    @Test
    public void testTriggerAdHocFragment() {
        String adhocFragmentName = "adhocFrag-name";

        setupCaseInstance(cis, serverTemplateId);
        presenter.triggerAdHocAction(adhocFragmentName);

        verify(caseManagementService).triggerAdHocAction(eq(containerId), eq(caseId), eq(adhocFragmentName), any());
    }

}
