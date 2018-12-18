/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.instance.details;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.workbench.pr.client.editors.instance.diagram.ProcessInstanceDiagramPresenter;
import org.jbpm.workbench.pr.client.editors.instance.log.ProcessInstanceLogPresenter;
import org.jbpm.workbench.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsPresenterTest {

    private static final Long PI_ID = 1L;
    private static final String SERVER_TEMPLATE_ID = "serverTemplateIdTest";
    private static final String PI_DEPLOYMENT_ID = "deploymentIdTest";

    @Mock
    public ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView view;

    @Mock
    ConfirmPopup confirmPopup;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    @Spy
    Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent = new EventSourceMock<>();

    @Spy
    Event<ProcessInstanceSelectionEvent> processInstanceSelected = new EventSourceMock<ProcessInstanceSelectionEvent>();

    @Spy
    Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent = new EventSourceMock<ChangeTitleWidgetEvent>();

    private CallerMock<ProcessService> remoteProcessServiceCaller;

    @Mock
    private ProcessService processService;

    private Caller<ProcessRuntimeDataService> processRuntimeDataServiceCaller;

    @Mock
    private ProcessRuntimeDataService processRuntimeDataService;

    @Mock
    PrimaryActionMenuBuilder signalProcessInstanceAction;

    @Mock
    PrimaryActionMenuBuilder abortProcessInstanceAction;

    @Mock
    private ProcessVariableListPresenter variableListPresenter;

    @Mock
    private ProcessDocumentListPresenter documentListPresenter;

    @Mock
    private ProcessInstanceLogPresenter processInstanceLogPresenter;

    @Mock
    private ProcessInstanceDetailsTabPresenter detailsPresenter;

    @Mock
    private ProcessInstanceDiagramPresenter processDiagramPresenter;

    @InjectMocks
    private ProcessInstanceDetailsPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(changeTitleWidgetEvent).fire(any(ChangeTitleWidgetEvent.class));
        doNothing().when(processInstanceSelected).fire(any(ProcessInstanceSelectionEvent.class));
        doNothing().when(processInstancesUpdatedEvent).fire(any(ProcessInstancesUpdateEvent.class));
        presenter.setSignalProcessInstanceAction(signalProcessInstanceAction);
        presenter.setAbortProcessInstanceAction(abortProcessInstanceAction);
        remoteProcessServiceCaller = new CallerMock<>(processService);
        processRuntimeDataServiceCaller = new CallerMock<>(processRuntimeDataService);
        presenter.setProcessService(remoteProcessServiceCaller);
        presenter.setProcessRuntimeDataService(processRuntimeDataServiceCaller);
    }

    @Test
    public void isForLogRemainsEnabledAfterRefresh() {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withServerTemplateId(SERVER_TEMPLATE_ID).withDeploymentId(PI_DEPLOYMENT_ID).withProcessInstanceId(PI_ID).withState(1).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);

        //When task selected with logOnly
        boolean isLogOnly = true;
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(summary.getProcessInstanceKey(),
                                                                            isLogOnly));
        //Then only tab log is displayed
        verify(view).displayOnlyLogTab();
        assertTrue(presenter.isForLog());
        verify(view).resetTabs(isLogOnly);

        presenter.onRefresh();
        assertTrue(presenter.isForLog());
    }

    @Test
    public void isForLogRemainsDisabledAfterRefresh() {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withServerTemplateId(SERVER_TEMPLATE_ID).withDeploymentId(PI_DEPLOYMENT_ID).withProcessInstanceId(PI_ID).withState(1).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);

        //When task selected without logOnly
        boolean isLogOnly = false;
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(summary.getProcessInstanceKey(),
                                                                            isLogOnly));

        //Then alltabs are displayed
        verify(view).displayAllTabs();
        verify(view).resetTabs(isLogOnly);
        assertFalse(presenter.isForLog());

        presenter.onRefresh();
        assertFalse(presenter.isForLog());
    }

    @Test
    public void confirmPopupTest() {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withServerTemplateId(SERVER_TEMPLATE_ID).withDeploymentId(PI_DEPLOYMENT_ID).withProcessInstanceId(PI_ID).withState(1).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);

        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(summary.getProcessInstanceKey(),
                                                                            true));
        presenter.openAbortProcessInstancePopup();
        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(confirmPopup).show(any(),
                                  any(),
                                  any(),
                                  captureCommand.capture());

        remoteProcessServiceCaller = new CallerMock<>(processService);
        presenter.setProcessService(remoteProcessServiceCaller);
        captureCommand.getValue().execute();

        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent).fire(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(NotificationEvent.NotificationType.DEFAULT,
                     captor.getValue().getType());
        assertEquals(Constants.INSTANCE.Aborting_Process_Instance(PI_ID),
                     captor.getValue().getNotification());

        verify(processService).abortProcessInstance(new ProcessInstanceKey(SERVER_TEMPLATE_ID,
                                                                           PI_DEPLOYMENT_ID,
                                                                           PI_ID));
    }

    @Test
    public void abortActiveInstanceFromDetailsHidesActionsTest() {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withServerTemplateId(SERVER_TEMPLATE_ID).withDeploymentId(PI_DEPLOYMENT_ID).withProcessInstanceId(PI_ID).withState(1).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);


        doAnswer(invocation -> null).when(processService).abortProcessInstance(summary.getProcessInstanceKey());

        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(summary.getProcessInstanceKey(),
                                                                            false));
        verifySignalAbortActionsVisibility(false, true);
        verifyNoMoreInteractionsWithSignalAbortActions();

        reset(signalProcessInstanceAction);
        reset(abortProcessInstanceAction);

        presenter.abortProcessInstance();

        verifySignalAbortActionsVisibility(false);
        verifyNoMoreInteractionsWithSignalAbortActions();
    }

    @Test
    public void actionsDisabledForAbortedProcessInstance() {
        verifyActionsVisibility(ProcessInstance.STATE_ABORTED,
                                false,
                                false);
    }

    @Test
    public void actionsDisabledForCompletedProcessInstance() {
        verifyActionsVisibility(ProcessInstance.STATE_COMPLETED,
                                false,
                                false);
    }

    @Test
    public void actionDisabledForPendingProcessInstance() {
        verifyActionsVisibility(ProcessInstance.STATE_PENDING,
                                false,
                                false);
    }

    @Test
    public void actionDisabledForSuspendedProcessInstance() {
        verifyActionsVisibility(ProcessInstance.STATE_SUSPENDED,
                                false,
                                false);
    }

    @Test
    public void actionEnabledForActiveProcessInstance() {
        verifyActionsVisibility(ProcessInstance.STATE_ACTIVE,
                                false,
                                true);
    }

    @Test
    public void actionDisabledForActiveProcessInstanceForLog() {
        verifyActionsVisibility(ProcessInstance.STATE_ACTIVE,
                                true,
                                false);
    }

    private void verifyActionsVisibility(int status,
                                         boolean isForLog,
                                         boolean visibilityExpected) {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withServerTemplateId(SERVER_TEMPLATE_ID).withDeploymentId(PI_DEPLOYMENT_ID).withProcessInstanceId(PI_ID).withState(status).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);

        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(summary.getProcessInstanceKey(),
                                                                            isForLog));

        verifySignalAbortActionsVisibility(false, visibilityExpected);
        verifyNoMoreInteractionsWithSignalAbortActions();
    }

    private void verifySignalAbortActionsVisibility(Boolean... expectedValues) {
        ArgumentCaptor<Boolean> captorSignal = ArgumentCaptor.forClass(Boolean.class);
        verify(signalProcessInstanceAction, times(expectedValues.length)).setVisible(captorSignal.capture());
        assertThat(captorSignal.getAllValues()).containsExactly(expectedValues);

        ArgumentCaptor<Boolean> captorAbort = ArgumentCaptor.forClass(Boolean.class);
        verify(abortProcessInstanceAction, times(expectedValues.length)).setVisible(captorAbort.capture());
        assertThat(captorAbort.getAllValues()).containsExactly(expectedValues);
    }

    private void verifyNoMoreInteractionsWithSignalAbortActions() {
        verifyNoMoreInteractions(signalProcessInstanceAction);
        verifyNoMoreInteractions(abortProcessInstanceAction);
    }

    @Test
    public void refreshTest() {
        final ProcessInstanceSummary summary = ProcessInstanceSummary.builder().withState(1).build();
        when(processRuntimeDataService.getProcessInstance(any())).thenReturn(summary);


        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(SERVER_TEMPLATE_ID,
                                                                            PI_DEPLOYMENT_ID,
                                                                            PI_ID,
                                                                            false));
        verify(view).displayAllTabs();
        verify(view).resetTabs(false);

        presenter.onRefresh();

        final ArgumentCaptor<ProcessInstanceSelectionEvent> processInstanceSelectionEventArgumentCaptor = ArgumentCaptor.forClass(ProcessInstanceSelectionEvent.class);
        verify(processInstanceSelected).fire(processInstanceSelectionEventArgumentCaptor.capture());
        assertEquals(PI_DEPLOYMENT_ID,
                     processInstanceSelectionEventArgumentCaptor.getValue().getDeploymentId());
        assertEquals(PI_ID,
                     processInstanceSelectionEventArgumentCaptor.getValue().getProcessInstanceId());
        assertFalse(processInstanceSelectionEventArgumentCaptor.getValue().isForLog());
        assertEquals(SERVER_TEMPLATE_ID,
                     processInstanceSelectionEventArgumentCaptor.getValue().getServerTemplateId());

        presenter.onProcessSelectionEvent(processInstanceSelectionEventArgumentCaptor.getValue());
        verify(view,
               times(2)).displayAllTabs();
        verify(view).resetTabs(false);

        verify(variableListPresenter, times(2)).setProcessInstance(summary);
        verify(documentListPresenter, times(2)).setProcessInstance(summary);
        verify(processInstanceLogPresenter, times(2)).setProcessInstance(summary);
        verify(detailsPresenter, times(2)).setProcessInstance(summary);
        verify(processDiagramPresenter, times(2)).setProcessInstance(summary);
    }
}
