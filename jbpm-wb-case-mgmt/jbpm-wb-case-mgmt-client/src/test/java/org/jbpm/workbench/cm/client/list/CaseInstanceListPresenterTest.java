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

package org.jbpm.workbench.cm.client.list;

import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.cm.client.overview.CaseOverviewPresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.jbpm.workbench.cm.util.CaseStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseInstanceListPresenterTest {

    @Mock
    CaseManagementService caseManagementService;

    Caller<CaseManagementService> caseService;

    @Mock
    CaseInstanceListPresenter.CaseInstanceListView view;

    @Mock
    PlaceManager placeManager;

    @InjectMocks
    CaseInstanceListPresenter presenter;

    List<CaseInstanceSummary> caseInstanceSummaryList = Lists.newArrayList(createCaseInstance());

    private static CaseInstanceSummary createCaseInstance() {
        return CaseInstanceSummary.builder()
                .caseId("caseId")
                .description("description")
                .status(CaseStatus.OPEN)
                .containerId("containerId")
                .build();
    }

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);
        when(caseManagementService.getCaseInstances(any(CaseInstanceSearchRequest.class))).thenReturn(caseInstanceSummaryList);
        presenter.setCaseService(caseService);
        when(view.getValue()).thenReturn(new CaseInstanceSearchRequest());
    }

    @Test
    public void testCancelCaseInstance() {
        final CaseInstanceSummary cis = caseInstanceSummaryList.remove(0);

        presenter.cancelCaseInstance(cis);

        verify(caseManagementService).cancelCaseInstance(cis.getContainerId(), cis.getCaseId());
        verify(caseManagementService).getCaseInstances(any(CaseInstanceSearchRequest.class));
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).setCaseInstanceList(captor.capture());
        assertEquals(caseInstanceSummaryList.size(), captor.getValue().size());
    }

    @Test
    public void testCloseCaseInstance() {
        final CaseInstanceSummary cis = caseInstanceSummaryList.remove(0);

        presenter.closeCaseInstance(cis);

        verify(caseManagementService).closeCaseInstance(cis.getContainerId(),
                                                        cis.getCaseId(),
                                                        null);
        verify(caseManagementService).getCaseInstances(any(CaseInstanceSearchRequest.class));
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).setCaseInstanceList(captor.capture());
        assertEquals(caseInstanceSummaryList.size(),
                     captor.getValue().size());
    }

    @Test
    public void testRefreshData() {
        presenter.refreshData();

        verify(caseManagementService).getCaseInstances(view.getValue());
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).setCaseInstanceList(captor.capture());
        assertEquals(caseInstanceSummaryList.size(),
                     captor.getValue().size());
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        verify(view).setValue(new CaseInstanceSearchRequest());
        verify(caseManagementService).getCaseInstances(view.getValue());
    }

    @Test
    public void testSelectCaseInstance() {
        final CaseInstanceSummary cis = createCaseInstance();
        presenter.selectCaseInstance(cis);

        final ArgumentCaptor<DefaultPlaceRequest> captor = ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        verify(placeManager).goTo(captor.capture());

        final DefaultPlaceRequest dpr = captor.getValue();
        assertNotNull(dpr);
        assertEquals(cis.getContainerId(), dpr.getParameter(CaseOverviewPresenter.PARAMETER_CONTAINER_ID, null));
        assertEquals(cis.getCaseId(), dpr.getParameter(CaseOverviewPresenter.PARAMETER_CASE_ID, null));
    }
}
