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

package org.jbpm.workbench.wi.client.casemgmt;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.*;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CaseProvisioningAppLauncherHandlerTest {

    @Spy
    Event<AppLauncherAddEvent> appLauncherAddEvent = new EventSourceMock<>();

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<>();

    @Mock
    CaseProvisioningService caseManagementService;

    @InjectMocks
    CaseProvisioningAppLauncherHandler appLauncherHandler;

    @Before
    public void init() {
        final Caller<CaseProvisioningService> service = new CallerMock<>(caseManagementService);
        appLauncherHandler.setCaseProvisioningService(service);
        doNothing().when(notification).fire(any(NotificationEvent.class));
        doNothing().when(appLauncherAddEvent).fire(any(AppLauncherAddEvent.class));
    }

    @Test
    public void testVerifyCaseAppStatusDisabled() {
        when(caseManagementService.getProvisioningStatus()).thenReturn(DISABLED);

        appLauncherHandler.verifyCaseAppStatus();

        verify(appLauncherAddEvent, never()).fire(any(AppLauncherAddEvent.class));
    }

    @Test
    public void testVerifyCaseAppStatusCompleted() {
        when(caseManagementService.getProvisioningStatus()).thenReturn(COMPLETED);
        when(caseManagementService.getApplicationContext()).thenReturn("/context");

        appLauncherHandler.verifyCaseAppStatus();

        verify(appLauncherAddEvent).fire(any(AppLauncherAddEvent.class));
    }

    @Test
    public void testOnCaseManagementProvisioningStartedEvent() {
        appLauncherHandler.onCaseManagementProvisioningStartedEvent(new CaseProvisioningStartedEvent());

        verify(appLauncherAddEvent, never()).fire(any(AppLauncherAddEvent.class));
        assertNotification(DEFAULT);
    }



    @Test
    public void testOnCaseManagementProvisioningCompletedEvent() {
        appLauncherHandler.onCaseManagementProvisioningCompletedEvent(new CaseProvisioningCompletedEvent("/context"));

        verify(appLauncherAddEvent).fire(any(AppLauncherAddEvent.class));
        assertNotification(SUCCESS);
    }

    @Test
    public void testOnCaseManagementProvisioningFailedEvent() {
        appLauncherHandler.onCaseManagementProvisioningFailedEvent(new CaseProvisioningFailedEvent());

        verify(appLauncherAddEvent, never()).fire(any(AppLauncherAddEvent.class));
        assertNotification(ERROR);
    }

    protected void assertNotification(final NotificationEvent.NotificationType notificationType) {
        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification).fire(captor.capture());
        assertEquals(notificationType, captor.getValue().getType());
    }

}