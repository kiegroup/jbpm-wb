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

package org.jbpm.workbench.cm.client.newcase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.client.events.CaseCreatedEvent;
import org.jbpm.workbench.cm.client.util.CaseRolesValidations;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewCaseInstancePresenterTest {

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    EventSourceMock<CaseCreatedEvent> caseCreatedEvent;

    @Mock
    NewCaseInstancePresenter.NewCaseInstanceView view;

    Caller<CaseManagementService> caseService;

    @Mock
    CaseManagementService caseManagementService;

    @Mock
    TranslationService translationService;

    @Mock
    CaseRolesValidations caseRolesValidations;

    @Mock
    User identity;

    @InjectMocks
    NewCaseInstancePresenter presenter;

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);
        presenter.setCaseService(caseService);
        presenter.setNotification(notificationEvent);
        presenter.setNewCaseEvent(caseCreatedEvent);
    }

    @Test
    public void testCreateInvalidCaseInstance() {
        presenter.createCaseInstance(null,
                                     anyString(),
                                     null);

        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent).fire(captor.capture());

        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(NotificationEvent.NotificationType.ERROR,
                     captor.getValue().getType());
    }

    @Test
    public void testCreateCaseInstance() {
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id("id").name("name").containerId("containerId").build();
        when(caseManagementService.getCaseDefinitions()).thenReturn(Arrays.asList(cds));
        final String owner = "userx";
        when(identity.getIdentifier()).thenReturn(owner);
        when(caseRolesValidations.validateRolesAssignments(any(CaseDefinitionSummary.class),
                                                           anyList())).thenReturn(EMPTY_LIST);

        presenter.show();

        verify(view).clearCaseDefinitions();
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).setCaseDefinitions(captor.capture());
        final List list = captor.getValue();
        assertEquals(1,
                     list.size());
        assertEquals(cds,
                     list.get(0));
        verify(view).setOwner(owner);
        verify(view).show();

        presenter.createCaseInstance(cds.getUniqueId(),
                                     owner,
                                     emptyList());

        verify(caseManagementService).startCaseInstance(null,
                                                        cds.getContainerId(),
                                                        cds.getId(),
                                                        owner,
                                                        emptyList());
        verify(view).hide();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(caseCreatedEvent).fire(any(CaseCreatedEvent.class));
    }
}