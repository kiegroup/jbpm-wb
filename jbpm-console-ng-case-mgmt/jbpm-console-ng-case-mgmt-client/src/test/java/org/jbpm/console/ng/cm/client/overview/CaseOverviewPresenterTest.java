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

package org.jbpm.console.ng.cm.client.overview;

import org.jbpm.console.ng.cm.client.events.CaseCancelEvent;
import org.jbpm.console.ng.cm.client.events.CaseDestroyEvent;
import org.jbpm.console.ng.cm.client.events.CaseRefreshEvent;
import org.jbpm.console.ng.cm.client.perspectives.CaseInstanceListPerspective;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseOverviewPresenterTest extends AbstractCaseInstancePresenterTest {

    private final String serverTemplateId = "serverTemplateId";

    @Mock
    CaseOverviewPresenter.CaseOverviewView view;

    @Mock
    PlaceManager placeManager;

    @Mock
    EventSourceMock<CaseCancelEvent> caseCancelEvent;

    @Mock
    EventSourceMock<CaseDestroyEvent> caseDestroyEvent;

    @Mock
    EventSourceMock<CaseRefreshEvent> caseRefreshEvent;

    @InjectMocks
    CaseOverviewPresenter presenter;

    @Before
    public void setup() {
        presenter.setCaseCancelEvent(caseCancelEvent);
        presenter.setCaseDestroyEvent(caseDestroyEvent);
        presenter.setCaseRefreshEvent(caseRefreshEvent);
    }

    @Override
    public CaseOverviewPresenter getPresenter() {
        return presenter;
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
    }

    @Test
    public void testReturnToCaseList() {
        presenter.backToList();

        verify(placeManager).goTo(CaseInstanceListPerspective.PERSPECTIVE_ID);
    }

    @Test
    public void testFindCaseInstance() {
        presenter.findCaseInstance();

        verify(view).setCaseId("");
        verify(view).setCaseTitle("");
        verifyNoMoreInteractions(view);
        verify(caseManagementService, never()).getCaseInstance(anyString(), anyString(), anyString());
    }

    @Test
    public void testRefreshCase() {
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.refreshCase();

        final ArgumentCaptor<CaseRefreshEvent> captor = ArgumentCaptor.forClass(CaseRefreshEvent.class);
        verify(caseRefreshEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }

    @Test
    public void testOnCaseRefreshEvent() {
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.onCaseRefreshEvent(new CaseRefreshEvent(cis.getCaseId()));

        verify(view, times(2)).setCaseId("");
        verify(view, times(2)).setCaseTitle("");
        verify(caseManagementService, times(2)).getCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
    }

    @Test
    public void testCancelCaseInstance() {
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.cancelCaseInstance();

        verify(caseManagementService).cancelCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseCancelEvent> captor = ArgumentCaptor.forClass(CaseCancelEvent.class);
        verify(caseCancelEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }

    @Test
    public void testDestroyCaseInstance() {
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.destroyCaseInstance();

        verify(caseManagementService).destroyCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseDestroyEvent> captor = ArgumentCaptor.forClass(CaseDestroyEvent.class);
        verify(caseDestroyEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }
}