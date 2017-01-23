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

package org.jbpm.workbench.forms.client.display.displayers.pr;

import java.util.HashMap;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.forms.client.display.process.AbstractStartProcessFormDisplayer;
import org.jbpm.workbench.forms.client.display.util.JSNIHelper;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class AbstractStartProcessFormDisplayerTest {

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent = new EventSourceMock<NotificationEvent>();

    @Mock
    protected EventSourceMock<NewProcessInstanceEvent> newProcessInstanceEvent = new EventSourceMock<NewProcessInstanceEvent>();

    @Mock
    protected Caller<ProcessService> processServiceCaller;

    @Mock
    protected ProcessService processService;

    @Mock
    protected JSNIHelper jsniHelper;

    public abstract AbstractStartProcessFormDisplayer getStartProcessFormDisplayer();

    @Before
    public void setupMocks() {
        processServiceCaller = new CallerMock<ProcessService>(processService);
        getStartProcessFormDisplayer().setProcessService(processServiceCaller);
    }

    @Test
    public void testNotificationOnStartProcess() {
        getStartProcessFormDisplayer().setParentProcessInstanceId( 0L );
        getStartProcessFormDisplayer().startProcess( new HashMap<String, Object>() );
        verify( newProcessInstanceEvent ).fire( any( NewProcessInstanceEvent.class ) );
        ArgumentCaptor<NotificationEvent> argument = ArgumentCaptor.forClass( NotificationEvent.class );
        verify( notificationEvent ).fire( argument.capture() );
        assertEquals( NotificationEvent.NotificationType.SUCCESS, argument.getValue().getType() );
    }
}
