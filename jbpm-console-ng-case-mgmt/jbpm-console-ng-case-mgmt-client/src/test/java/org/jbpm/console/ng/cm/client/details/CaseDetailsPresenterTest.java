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

package org.jbpm.console.ng.cm.client.details;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.cm.client.AbstractCaseInstancePresenterTest;
import org.jbpm.console.ng.cm.client.events.CaseRefreshEvent;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CaseDetailsPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock
    CaseDetailsPresenter.CaseDetailsView view;

    @InjectMocks
    CaseDetailsPresenter presenter;

    @Override
    public CaseDetailsPresenter getPresenter() {
        return presenter;
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
    }

    @Test
    public void testFindCaseInstance() {
        presenter.findCaseInstance();

        verify(view).setCaseId("");
        verify(view).setCaseStatus("");
        verify(view).setCaseDescription("");
        verify(view).setCaseStartedAt("");
        verify(view).setCaseCompletedAt("");
        verify(view).setCaseOwner("");
        verifyNoMoreInteractions(view);
        verify(caseManagementService, never()).getCaseInstance(anyString(), anyString(), anyString());
    }

    @Test
    public void testOnStartup() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        verify(view).setCaseId("");
        verify(view).setCaseStatus("");
        verify(view).setCaseDescription("");
        verify(view).setCaseStartedAt("");
        verify(view).setCaseCompletedAt("");
        verify(view).setCaseOwner("");
        verify(view).setCaseId(cis.getCaseId());
        verify(view).setCaseStatus(cis.getStatusString());
        verify(view).setCaseDescription(cis.getDescription());
        verify(view).setCaseStartedAt(DateUtils.getDateTimeStr(cis.getStartedAt()));
        verify(view).setCaseCompletedAt(DateUtils.getDateTimeStr(cis.getCompletedAt()));
        verify(view).setCaseOwner(cis.getOwner());
        verifyNoMoreInteractions(view);
        verify(caseManagementService).getCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
    }

    @Test
    public void testOnCaseRefreshEvent() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.onCaseRefreshEvent(new CaseRefreshEvent(cis.getCaseId()));

        verify(view, times(2)).setCaseId("");
        verify(view, times(2)).setCaseStatus("");
        verify(view, times(2)).setCaseDescription("");
        verify(view, times(2)).setCaseStartedAt("");
        verify(view, times(2)).setCaseCompletedAt("");
        verify(view, times(2)).setCaseOwner("");
        verify(view, times(2)).setCaseId(cis.getCaseId());
        verify(view, times(2)).setCaseStatus(cis.getStatusString());
        verify(view, times(2)).setCaseDescription(cis.getDescription());
        verify(view, times(2)).setCaseStartedAt(DateUtils.getDateTimeStr(cis.getStartedAt()));
        verify(view, times(2)).setCaseCompletedAt(DateUtils.getDateTimeStr(cis.getCompletedAt()));
        verify(view, times(2)).setCaseOwner(cis.getOwner());
        verifyNoMoreInteractions(view);
        verify(caseManagementService, times(2)).getCaseInstance(serverTemplateId, cis.getContainerId(), cis.getCaseId());
    }

}