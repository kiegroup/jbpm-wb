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

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.cm.client.events.CaseRefreshEvent;
import org.jbpm.console.ng.cm.client.perspectives.CaseInstanceListPerspective;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.client.events.CaseCancelEvent;
import org.jbpm.console.ng.cm.client.events.CaseDestroyEvent;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.jbpm.console.ng.cm.client.overview.CaseOverviewPresenter.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseOverviewPresenterTest {

    @Mock
    CaseOverviewPresenter.CaseOverviewView view;

    @Mock
    PlaceManager placeManager;

    @Mock
    CaseManagementService caseManagementService;

    Caller<CaseManagementService> caseService;

    @Mock
    EventSourceMock<CaseCancelEvent> caseCancelEvent;

    @Mock
    EventSourceMock<CaseDestroyEvent> caseDestroyEvent;

    @Mock
    EventSourceMock<CaseRefreshEvent> caseRefreshEvent;

    @InjectMocks
    CaseOverviewPresenter presenter;

    @Before
    public void init(){
        caseService = new CallerMock<>(caseManagementService);
        presenter.setCaseService(caseService);
        presenter.setCaseCancelEvent(caseCancelEvent);
        presenter.setCaseDestroyEvent(caseDestroyEvent);
        presenter.setCaseRefreshEvent(caseRefreshEvent);
    }

    @Test
    public void testInit(){
        presenter.init();

        verify(view).init(presenter);
    }

    @Test
    public void testReturnToCaseList(){
        presenter.backToList();

        verify(placeManager).goTo(CaseInstanceListPerspective.PERSPECTIVE_ID);
    }

    @Test
    public void testLoadCaseInstance(){
        presenter.loadCaseInstance();

        verify(view).setCaseId("");
        verify(view).setCaseTitle("");
        verifyNoMoreInteractions(view);
        verify(caseManagementService, never()).getCaseInstance(anyString(), anyString(), anyString());
    }

    @Test
    public void testRefreshCase(){
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.refreshCase();

        final ArgumentCaptor<CaseRefreshEvent> captor = ArgumentCaptor.forClass(CaseRefreshEvent.class);
        verify(caseRefreshEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }

    @Test
    public void testOnCaseRefreshEvent() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.onCaseRefreshEvent(new CaseRefreshEvent(cis.getCaseId()));

        verify(view, times(2)).setCaseId("");
        verify(view, times(2)).setCaseTitle("");
        verify(caseManagementService, times(2)).getCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
    }

    @Test
    public void testCancelCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.cancelCaseInstance();

        verify(caseManagementService).cancelCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseCancelEvent> captor = ArgumentCaptor.forClass(CaseCancelEvent.class);
        verify(caseCancelEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }

    @Test
    public void testDestroyCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.destroyCaseInstance();

        verify(caseManagementService).destroyCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseDestroyEvent> captor = ArgumentCaptor.forClass(CaseDestroyEvent.class);
        verify(caseDestroyEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
    }

    private CaseInstanceSummary setupCaseInstance(final String serverTemplateId){
        final CaseInstanceSummary cis = new CaseInstanceSummary("caseId", "description", 0, "containerId");
        final PlaceRequest placeRequest = new DefaultPlaceRequest();
        placeRequest.addParameter(PARAMETER_SERVER_TEMPLATE_ID, serverTemplateId);
        placeRequest.addParameter(PARAMETER_CONTAINER_ID, cis.getContainerId());
        placeRequest.addParameter(PARAMETER_CASE_ID, cis.getCaseId());
        when(caseManagementService.getCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId())).thenReturn(cis);

        presenter.onStartup(placeRequest);

        return cis;
    }

}