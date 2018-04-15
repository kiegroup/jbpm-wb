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

import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.ProcessDefinitionSummary;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static org.jbpm.workbench.cm.util.CaseStageStatus.AVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static java.util.Collections.singletonList;

@RunWith(MockitoJUnitRunner.class)
public class ActionsPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock(name = "view")
    private CaseActionsPresenter.CaseActionsView caseAllActionsView;

    @Mock(name = "NewActionView")
    private CaseActionsPresenter.NewActionView newActionViewMock;

    @Mock
    private User identity;

    @Mock
    private Actions actions;

    @Spy
    @InjectMocks
    private CaseActionsPresenter presenter;

    private CaseActionItemView caseActionItemViewMock;

    private CaseInstanceSummary cis;

    private List<CaseActionSummary> caseActionSummaryList = Lists.newArrayList(createCaseActionSummary());

    private static CaseActionSummary createCaseActionSummary() {
        return CaseActionSummary.builder()
                .id(1L)
                .name("actionName")
                .build();
    }

    @Override
    public CaseActionsPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void setup() {
        caseActionItemViewMock = mock(CaseActionItemView.class);

        cis = newCaseInstanceSummary();
        cis.setStages(singletonList(createCaseStageSummary(AVAILABLE.getStatus())));
        when(caseManagementService.getCaseActions(anyString(),
                                                  anyString(),
                                                  anyString(),
                                                  anyString())).thenReturn(actions);

        when(actions.getAvailableActions()).thenReturn(caseActionSummaryList);
        when(actions.getInProgressAction()).thenReturn(caseActionSummaryList);
        when(actions.getCompleteActions()).thenReturn(caseActionSummaryList);
    }

    @Test
    public void clearAndLoadCaseInstanceTest() {
        when(caseManagementService.getProcessDefinitions(containerId)).thenReturn(singletonList(ProcessDefinitionSummary.builder()
                                                                                                                        .id("processId")
                                                                                                                        .name("SubProcess_1")
                                                                                                                        .build()));
        setupCaseInstance(cis,
                          serverTemplateId);

        verifyCaseInstanceCleared();
        verifyCaseInstanceLoaded();
    }

    private void verifyCaseInstanceCleared() {
        verify(caseAllActionsView).removeAllTasks();
        verify(newActionViewMock).setCaseStagesList(emptyList());
    }

    private void verifyCaseInstanceLoaded() {
        verify(caseAllActionsView).updateListHeaders();
        verify(newActionViewMock).setCaseStagesList(cis.getStages());
        verifySubProcessesLoaded();
        verifyActionsLoaded();
    }

    private void verifySubProcessesLoaded() {
        verify(caseManagementService).getProcessDefinitions(containerId);
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(newActionViewMock).setProcessDefinitions(captor.capture());
        assertEquals("SubProcess_1",
                     captor.getValue().get(0));
    }

    private void verifyActionsLoaded() {
        verify(presenter).refreshData(true);
        verify(caseManagementService).getCaseActions(serverTemplateId,
                                                     containerId,
                                                     caseId,
                                                     identity.getIdentifier());

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(caseAllActionsView).setAvailableActionsList(captor.capture());
        assertEquals(caseActionSummaryList.size() + 2,
                     captor.getValue().size());
        assertEquals(CaseActionType.DYNAMIC_USER_TASK,
                     ((CaseActionSummary) captor.getValue().get(0)).getActionType());
        assertEquals(CaseActionType.DYNAMIC_SUBPROCESS_TASK,
                     ((CaseActionSummary) captor.getValue().get(1)).getActionType());

        verify(caseAllActionsView).setInProgressActionsList(caseActionSummaryList);
        verify(caseAllActionsView).setCompletedActionsList(caseActionSummaryList);
    }

    @Test
    public void testSetAction_statusAvailable() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.AVAILABLE);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        getPresenter().setAction(caseActionItemViewMock);

        verify(caseActionItemViewMock).prepareAction(caseActionSummary);
    }

    @Test
    public void testSetAction_statusInProgress() {
        final CaseActionSummary caseActionSummaryWithoutOwner = createCaseActionSummary(CaseActionStatus.IN_PROGRESS);
        final CaseActionSummary caseActionSummaryWithOwner = createCaseActionSummary(CaseActionStatus.IN_PROGRESS);
        caseActionSummaryWithOwner.setActualOwner("owner");
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummaryWithoutOwner,
                                                           caseActionSummaryWithOwner);

        getPresenter().setAction(caseActionItemViewMock);
        verify(caseActionItemViewMock,
               never()).addActionOwner(anyString());

        getPresenter().setAction(caseActionItemViewMock);
        verify(caseActionItemViewMock,
               times(2)).addCreationDate();
        verify(caseActionItemViewMock).addActionOwner("owner");
        verify(caseActionItemViewMock,
               never()).prepareAction(any(CaseActionSummary.class));
    }

    @Test
    public void testSetAction_statusCompleted() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.COMPLETED);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        getPresenter().setAction(caseActionItemViewMock);

        verify(caseActionItemViewMock).addCreationDate();
        verify(caseActionItemViewMock,
               never()).addActionOwner(anyString());
        verify(caseActionItemViewMock,
               never()).prepareAction(any(CaseActionSummary.class));
    }

    private CaseActionSummary createCaseActionSummary(final CaseActionStatus status) {
        final CaseActionSummary caseActionSummary = createCaseActionSummary();
        caseActionSummary.setActionStatus(status);
        return caseActionSummary;
    }

    @Test
    public void testAddDynamicAction() {
        String actionName = "dynAct-name";
        String actionDescription = "dynAct-name";
        String actionActors = "dynAct-actors";
        String actionGroups = "dynAct-groups";
        setupCaseInstance(cis,
                          serverTemplateId);
        presenter.addDynamicUserTaskAction(actionName,
                                           actionDescription,
                                           actionActors,
                                           actionGroups,
                                           null);

        verify(caseManagementService).addDynamicUserTask(eq(containerId),
                                                         eq(caseId),
                                                         eq(actionName),
                                                         eq(actionDescription),
                                                         eq(actionActors),
                                                         eq(actionGroups),
                                                         any());
    }

    @Test
    public void testAddDynamicActionInStage() {
        String actionName = "dynAct-name";
        String actionDescription = "dynAct-name";
        String actionActors = "dynAct-actors";
        String actionGroups = "dynAct-groups";
        String stageId = "dynAct-groups";
        setupCaseInstance(cis,
                          serverTemplateId);

        presenter.addDynamicUserTaskAction(actionName,
                                           actionDescription,
                                           actionActors,
                                           actionGroups,
                                           stageId);

        verify(caseManagementService).addDynamicUserTaskToStage(eq(containerId),
                                                                eq(caseId),
                                                                eq(stageId),
                                                                eq(actionName),
                                                                eq(actionDescription),
                                                                eq(actionActors),
                                                                eq(actionGroups),
                                                                any());
    }

    @Test
    public void testTriggerAdHocFragment() {
        String adhocFragmentName = "adhocFrag-name";

        setupCaseInstance(cis,
                          serverTemplateId);
        presenter.triggerAdHocAction(adhocFragmentName);

        verify(caseManagementService).triggerAdHocAction(eq(containerId),
                                                         eq(caseId),
                                                         eq(adhocFragmentName),
                                                         any());
    }
}
