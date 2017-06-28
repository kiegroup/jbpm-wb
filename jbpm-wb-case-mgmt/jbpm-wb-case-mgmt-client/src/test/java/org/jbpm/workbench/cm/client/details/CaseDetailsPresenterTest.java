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

package org.jbpm.workbench.cm.client.details;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.client.events.CaseRefreshEvent;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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

        verify(view).setValue(new CaseInstanceSummary());
        verifyNoMoreInteractions(view);
        verify(caseManagementService,
               never()).getCaseInstance(anyString(),
                                        anyString(),
                                        anyString());
    }

    @Test
    public void testOnStartup() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        verify(view).setValue(new CaseInstanceSummary());
        verify(view).setValue(cis);
        verifyNoMoreInteractions(view);
        verify(caseManagementService).getCaseInstance(serverTemplateId,
                                                      cis.getContainerId(),
                                                      cis.getCaseId());
    }

    @Test
    public void testOnCaseRefreshEvent() {
        final String serverTemplateId = "serverTemplateId";
        final CaseInstanceSummary cis = setupCaseInstance(serverTemplateId);

        presenter.onCaseRefreshEvent(new CaseRefreshEvent(cis.getCaseId()));

        verify(view,
               times(2)).setValue(new CaseInstanceSummary());
        verify(view,
               times(2)).setValue(cis);

        verifyNoMoreInteractions(view);
        verify(caseManagementService,
               times(2)).getCaseInstance(serverTemplateId,
                                         cis.getContainerId(),
                                         cis.getCaseId());
    }
}