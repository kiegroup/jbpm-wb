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
package org.jbpm.workbench.pr.client.editors.instance.signal;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceSignalPresenterTest {

    private static final Long PI_ID = 1L;
    private static final Long PI_ID2 = 2L;
    private static final String SERVER_TEMPLATE_ID = "serverTemplateIdTest";
    private static final String PI_DEPLOYMENT_ID = "deploymentIdTest";

    @Mock
    public ProcessInstanceSignalPresenter.PopupView view;

    @Mock
    ConfirmPopup confirmPopup;

    @Spy
    Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent = new EventSourceMock<>();

    private CallerMock<ProcessService> remoteProcessServiceCaller;

    @Mock
    private ProcessService processService;

    @Mock
    private PlaceRequest place;

    @Mock
    private PlaceManager placeManager;

    @InjectMocks
    private ProcessInstanceSignalPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(processInstancesUpdatedEvent).fire(any(ProcessInstancesUpdateEvent.class));
        remoteProcessServiceCaller = new CallerMock<ProcessService>(processService);
        presenter.setProcessService(remoteProcessServiceCaller);
        when(place.getParameter("serverTemplateId",
                                "")).thenReturn(SERVER_TEMPLATE_ID);
        when(place.getParameter("deploymentId",
                                "")).thenReturn(PI_DEPLOYMENT_ID);
        when(place.getParameter("processInstanceId",
                                "-1")).thenReturn(PI_ID + "");
    }

    @Test
    public void signalProcessInstancesEmptyRefTest() {
        when(view.getSignalRefText()).thenReturn("");
        presenter.signalProcessInstances((Arrays.asList(PI_ID)));

        verify(view).setHelpText(Constants.INSTANCE.Signal_Name_Required());
        verifyNoMoreInteractions(processService);
    }

    @Test
    public void signalProcessInstancesTest() {
        String signalRef = "SIGNAL_REF";
        String eventText = "EVENT_TEXT";
        List<Long> processInstanceIds = Arrays.asList(PI_ID,
                                                      PI_ID2);

        presenter.onStartup(place);
        presenter.onOpen();
        when(view.getSignalRefText()).thenReturn(signalRef);
        when(view.getEventText()).thenReturn(eventText);
        presenter.signalProcessInstances(processInstanceIds);

        verify(view,
               times(processInstanceIds.size())).displayNotification(anyString());
        verify(view).displayNotification(Constants.INSTANCE.Signaling_Process_Instance() + " (" + Constants.INSTANCE.Id() + " = " + PI_ID + ") " +
                                                 Constants.INSTANCE.Signal() + " = " + signalRef + " - " +
                                                 Constants.INSTANCE.Signal_Data() + " = " + eventText);
        verify(view).displayNotification(Constants.INSTANCE.Signaling_Process_Instance() + " (" + Constants.INSTANCE.Id() + " = " + PI_ID2 + ") " +
                                                 Constants.INSTANCE.Signal() + " = " + signalRef + " - " +
                                                 Constants.INSTANCE.Signal_Data() + " = " + eventText);
        verify(processService).signalProcessInstances(eq(SERVER_TEMPLATE_ID),
                                                      eq(Arrays.asList(PI_DEPLOYMENT_ID)),
                                                      eq(processInstanceIds),
                                                      eq(signalRef),
                                                      eq(eventText));
    }
}
