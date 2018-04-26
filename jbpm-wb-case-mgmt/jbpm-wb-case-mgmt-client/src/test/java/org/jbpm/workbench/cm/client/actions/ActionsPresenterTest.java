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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
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
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;
import static org.jbpm.workbench.cm.util.CaseActionType.*;
import static org.jbpm.workbench.cm.util.CaseStageStatus.*;
import static org.junit.Assert.assertEquals;
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
        cis.setStages(new ArrayList<>(Arrays.asList(createCaseStageSummary(AVAILABLE.getStatus()),
                                                    createCaseStageSummary(ACTIVE.getStatus()))));
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
        verify(newActionViewMock).clearAllStages();
        verify(newActionViewMock).clearAllProcessDefinitions();
    }

    private void verifyCaseInstanceLoaded() {
        final List<CaseStageSummary> allStagesList = cis.getStages();
        verify(caseAllActionsView).updateListHeaders();
        verify(presenter).setCaseStagesList(allStagesList);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(newActionViewMock).addStages(captor.capture());
        assertEquals(1,
                     captor.getValue().size());
        assertEquals(ACTIVE.getStatus(),
                     ((CaseStageSummary)captor.getValue().get(0)).getStatus());
        verifySubProcessesLoaded();
        verifyActionsLoaded();
    }

    private void verifySubProcessesLoaded() {
        verify(caseManagementService).getProcessDefinitions(containerId);
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(newActionViewMock).setProcessDefinitions(captor.capture());
        assertEquals(1,
                     captor.getValue().size());
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
        assertEquals(DYNAMIC_USER_TASK,
                     ((CaseActionSummary) captor.getValue().get(0)).getActionType());
        assertEquals(DYNAMIC_SUBPROCESS_TASK,
                     ((CaseActionSummary) captor.getValue().get(1)).getActionType());

        verify(caseAllActionsView).setInProgressActionsList(caseActionSummaryList);
        verify(caseAllActionsView).setCompletedActionsList(caseActionSummaryList);
    }

    @Test
    public void testSetAdHocAction_statusAvailable() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.AVAILABLE);
        caseActionSummary.setActionType(AD_HOC_TASK);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        verifyAvailableActionIsSet(translationService.format(AVAILABLE_IN) + ": " + translationService.format(CASE));
    }

    @Test
    public void testSetAdHocActionInStage_statusAvailable() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.AVAILABLE);
        caseActionSummary.setActionType(AD_HOC_TASK);
        caseActionSummary.setStage(CaseStageSummary.builder().name("Stage_Id").build());
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        verifyAvailableActionIsSet(translationService.format(AVAILABLE_IN) + ": Stage_Id");
    }

    @Test
    public void testSetDynamicAction_statusAvailable() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.AVAILABLE);
        caseActionSummary.setActionType(DYNAMIC_USER_TASK);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        verifyAvailableActionIsSet(translationService.format(DYMANIC));

        final ArgumentCaptor<CaseActionsPresenter.CaseActionAction> caseActionActionCaptor =
                ArgumentCaptor.forClass(CaseActionsPresenter.CaseActionAction.class);
        verify(caseActionItemViewMock).addAction(caseActionActionCaptor.capture());
        assertEquals(translationService.format(ACTION_START),
                     caseActionActionCaptor.getValue().label());

        caseActionActionCaptor.getValue().execute();
        final CaseActionType actionType = caseActionSummary.getActionType();
        verify(presenter).setNewDynamicAction(actionType);
        verify(newActionViewMock).show(eq(actionType),
                                       any(Command.class));
    }

    private void verifyAvailableActionIsSet(final String actionInfo) {
        getPresenter().setAction(caseActionItemViewMock);

        verify(presenter).prepareAction(caseActionItemViewMock);
        verify(caseActionItemViewMock).addActionInfo(actionInfo);
        verify(caseActionItemViewMock).addAction(any(CaseActionsPresenter.CaseActionAction.class));
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
        verify(presenter,
               never()).prepareAction(any(CaseActionItemView.class));
    }

    @Test
    public void testSetAction_statusCompleted() {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.COMPLETED);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        getPresenter().setAction(caseActionItemViewMock);

        verify(caseActionItemViewMock).addCreationDate();
        verify(caseActionItemViewMock,
               never()).addActionOwner(anyString());
        verify(presenter,
               never()).prepareAction(any(CaseActionItemView.class));
    }

    private CaseActionSummary createCaseActionSummary(final CaseActionStatus status) {
        final CaseActionSummary caseActionSummary = createCaseActionSummary();
        caseActionSummary.setActionStatus(status);
        return caseActionSummary;
    }

    private final String taskName = "dynTask-name";
    private final String actors = "dynTask-actors";
    private final String groups = "dynTask-groups";
    private final String description = "dynTask-description";
    private final ProcessDefinitionSummary processDefinitionSummary = ProcessDefinitionSummary.builder()
                                                                                              .id("processDefinitionId")
                                                                                              .build();

    @Test
    public void testAddDynamicUserTaskAction() {
        verifyDynamicActionAdded(DYNAMIC_USER_TASK,
                                         null);

        verify(caseManagementService).addDynamicUserTask(anyString(),
                                                         anyString(),
                                                         eq(taskName),
                                                         eq(description),
                                                         eq(actors),
                                                         eq(groups),
                                                         eq(null));
    }

    @Test
    public void testAddDynamicUserTaskActionToStage() {
        final String stageId = "stageId";
        verifyDynamicActionAdded(DYNAMIC_USER_TASK,
                                 stageId);

        verify(caseManagementService).addDynamicUserTaskToStage(anyString(),
                                                                anyString(),
                                                                eq(stageId),
                                                                eq(taskName),
                                                                eq(description),
                                                                eq(actors),
                                                                eq(groups),
                                                                eq(null));
    }

    @Test
    public void testAddDynamicSubProcessTaskAction() {
        verifyDynamicActionAdded(DYNAMIC_SUBPROCESS_TASK,
                                 null);

        verify(caseManagementService).addDynamicSubProcess(anyString(),
                                                           anyString(),
                                                           eq(processDefinitionSummary.getId()),
                                                           eq(null));
    }

    @Test
    public void testAddDynamicSubProcessTaskActionToStage() {
        final String stageId = "stageId";
        verifyDynamicActionAdded(DYNAMIC_SUBPROCESS_TASK,
                                 stageId);

        verify(caseManagementService).addDynamicSubProcessToStage(anyString(),
                                                                  anyString(),
                                                                  eq(stageId),
                                                                  eq(processDefinitionSummary.getId()),
                                                                  eq(null));
    }

    private void verifyDynamicActionAdded(final CaseActionType actionType,
                                          final String stageId) {
        final CaseActionSummary caseActionSummary = createCaseActionSummary(CaseActionStatus.AVAILABLE);
        caseActionSummary.setActionType(actionType);
        when(caseActionItemViewMock.getValue()).thenReturn(caseActionSummary);

        verifyAvailableActionIsSet(translationService.format(DYMANIC));

        final ArgumentCaptor<CaseActionsPresenter.CaseActionAction> caseActionActionCaptor =
                ArgumentCaptor.forClass(CaseActionsPresenter.CaseActionAction.class);
        verify(caseActionItemViewMock).addAction(caseActionActionCaptor.capture());
        caseActionActionCaptor.getValue().execute();

        switch (actionType) {
            case DYNAMIC_USER_TASK: {
                when(newActionViewMock.getTaskName()).thenReturn(taskName);
                when(newActionViewMock.getDescription()).thenReturn(description);
                when(newActionViewMock.getActors()).thenReturn(actors);
                when(newActionViewMock.getGroups()).thenReturn(groups);
                when(newActionViewMock.getStageId()).thenReturn(stageId);

                final ArgumentCaptor<Command> okCommandCaptor = ArgumentCaptor.forClass(Command.class);
                verify(newActionViewMock).show(eq(caseActionSummary.getActionType()),
                                               okCommandCaptor.capture());
                okCommandCaptor.getValue().execute();

                verify(presenter).addDynamicUserTaskAction(eq(taskName),
                                                           eq(description),
                                                           eq(actors),
                                                           eq(groups),
                                                           eq(stageId));
                break;
            }
            case DYNAMIC_SUBPROCESS_TASK: {
                final String processDefinitionName = "dynTask-processDefinitionName";
                when(newActionViewMock.getProcessDefinitionName()).thenReturn(processDefinitionName);
                when(newActionViewMock.getStageId()).thenReturn(stageId);
                presenter.getProcessDefinitionSummaryMap().put(processDefinitionName,
                                                               processDefinitionSummary);

                final ArgumentCaptor<Command> okCommandCaptor = ArgumentCaptor.forClass(Command.class);
                verify(newActionViewMock).show(eq(caseActionSummary.getActionType()),
                                               okCommandCaptor.capture());
                okCommandCaptor.getValue().execute();

                verify(presenter).addDynamicSubprocessTaskAction(eq(processDefinitionName),
                                                                 eq(stageId));
                break;
            }
        }
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
