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

package org.jbpm.console.ng.cm.client.list;

import java.util.Arrays;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.model.events.CaseCancelEvent;
import org.jbpm.console.ng.cm.model.events.CaseDestroyEvent;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.menu.ServerTemplateSelectorMenuBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CaseInstanceListPresenterTest {

    @Mock
    CaseManagementService caseManagementService;

    Caller<CaseManagementService> casesService;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Mock
    CaseInstanceListPresenter.CaseInstanceListView view;

    @Mock
    ExtendedPagedTable<CaseInstanceSummary> pagedTable;

    @Mock
    EventSourceMock<CaseCancelEvent> caseCancelEvent;

    @Mock
    EventSourceMock<CaseDestroyEvent> caseDestroyEvent;

    @InjectMocks
    CaseInstanceListPresenter presenter;

    @Before
    public void init() {
        casesService = new CallerMock<>(caseManagementService);
        presenter.setCasesService(casesService);
        presenter.setCaseCancelEvent(caseCancelEvent);
        presenter.setCaseDestroyEvent(caseDestroyEvent);
        when(view.getListGrid()).thenReturn(pagedTable);
    }

    @Test
    public void testCancelCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        when(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate()).thenReturn(serverTemplateId);

        presenter.onOpen();
        final CaseInstanceSummary cis = new CaseInstanceSummary("caseId", "description", 0, "containerId");
        presenter.cancelCaseInstance(cis);

        verify(caseManagementService).cancelCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseCancelEvent> captor = ArgumentCaptor.forClass(CaseCancelEvent.class);
        verify(caseCancelEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
        verify(pagedTable).setVisibleRangeAndClearData(any(Range.class), anyBoolean());
    }

    @Test
    public void testDestroyCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        when(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate()).thenReturn(serverTemplateId);

        presenter.onOpen();
        final CaseInstanceSummary cis = new CaseInstanceSummary("caseId", "description", 0, "containerId");
        presenter.destroyCaseInstance(cis);

        verify(caseManagementService).destroyCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
        final ArgumentCaptor<CaseDestroyEvent> captor = ArgumentCaptor.forClass(CaseDestroyEvent.class);
        verify(caseDestroyEvent).fire(captor.capture());
        assertEquals(cis.getCaseId(), captor.getValue().getCaseId());
        verify(pagedTable).setVisibleRangeAndClearData(any(Range.class), anyBoolean());
    }

    @Test
    public void testGetData() {
        final String serverTemplateId = "serverTemplateId";
        when(serverTemplateSelectorMenuBuilder.getSelectedServerTemplate()).thenReturn(serverTemplateId);

        presenter.onOpen();

        final CaseInstanceSummary cis = new CaseInstanceSummary("caseId", "description", 0, "containerId");
        when(caseManagementService.getCaseInstances(serverTemplateId, 0, 10)).thenReturn(Arrays.asList(cis));
        final Range range = new Range(0, 10);
        presenter.getData(range);

        verify(caseManagementService).getCaseInstances(serverTemplateId, 0, 10);
        verify(view).hideBusyIndicator();
    }

}
