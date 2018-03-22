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
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsPresenterTest {

    private static final Long PI_ID = 1L;
    private static final String SERVER_TEMPLATE_ID = "serverTemplateIdTest";
    private static final String PI_DEPLOYMENT_ID = "deploymentIdTest";
    private static final String PI_PROCESS_DEF_ID = "processDefIdTest";
    private static final String PI_PROCESS_DEF_NAME = "processDefNameTest";

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

    @InjectMocks
    private ProcessInstanceDetailsPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(changeTitleWidgetEvent).fire(any(ChangeTitleWidgetEvent.class));
        doNothing().when(processInstanceSelected).fire(any(ProcessInstanceSelectionEvent.class));
        doNothing().when(processInstancesUpdatedEvent).fire(any(ProcessInstancesUpdateEvent.class));
    }

    @Test
    public void isForLogRemainsEnabledAfterRefresh() {
        //When task selected with logOnly
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(PI_DEPLOYMENT_ID,
                                                                            PI_ID,
                                                                            PI_PROCESS_DEF_ID,
                                                                            PI_PROCESS_DEF_NAME,
                                                                            0,
                                                                            true,
                                                                            SERVER_TEMPLATE_ID));
        //Then only tab log is displayed
        verify(view).displayOnlyLogTab();
        assertTrue(presenter.isForLog());

        presenter.onRefresh();
        assertTrue(presenter.isForLog());
    }

    @Test
    public void isForLogRemainsDisabledAfterRefresh() {
        //When task selected without logOnly
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(PI_DEPLOYMENT_ID,
                                                                            PI_ID,
                                                                            PI_PROCESS_DEF_ID,
                                                                            PI_PROCESS_DEF_NAME,
                                                                            0,
                                                                            false,
                                                                            SERVER_TEMPLATE_ID));

        //Then alltabs are displayed
        verify(view).displayAllTabs();
        assertFalse(presenter.isForLog());

        presenter.onRefresh();
        assertFalse(presenter.isForLog());
    }

    @Test
    public void confirmPopupTest() {
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(PI_DEPLOYMENT_ID,
                                                                            PI_ID,
                                                                            PI_PROCESS_DEF_ID,
                                                                            PI_PROCESS_DEF_NAME,
                                                                            0,
                                                                            true,
                                                                            SERVER_TEMPLATE_ID));
        presenter.abortProcessInstance();
        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(confirmPopup).show(any(),
                                  any(),
                                  any(),
                                  captureCommand.capture());

        remoteProcessServiceCaller = new CallerMock<ProcessService>(processService);
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

        verify(processService).abortProcessInstance(eq(SERVER_TEMPLATE_ID),
                                                    eq(PI_DEPLOYMENT_ID),
                                                    eq(PI_ID));
    }
}
