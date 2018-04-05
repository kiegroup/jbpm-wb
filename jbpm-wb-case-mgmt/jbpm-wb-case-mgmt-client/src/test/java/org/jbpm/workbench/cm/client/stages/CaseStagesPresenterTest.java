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

package org.jbpm.workbench.cm.client.stages;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.jbpm.workbench.cm.util.CaseStageStatus.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseStagesPresenterTest extends AbstractCaseInstancePresenterTest {

    private CaseStageItemViewImpl caseStageItemViewMock;

    @Mock
    private CaseStagesPresenter.CaseStagesView caseStagesView;

    @Spy
    @InjectMocks
    private CaseStagesPresenter presenter;

    @Override
    public CaseStagesPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void setup() {
        caseStageItemViewMock = Mockito.mock(CaseStageItemViewImpl.class);
    }

    @Test
    public void testClearAndLoadCaseInstance() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        cis.setStages(singletonList(createCaseStageSummary(AVAILABLE.getStatus())));
        setupCaseInstance(cis,
                          serverTemplateId);

        verify(caseStagesView).removeAllStages();
        verify(caseStagesView).setCaseStagesList(cis.getStages());
        verify(caseStagesView).getCaseStageComponentList();
        verify(presenter).setStages();
        verifyNoMoreInteractions(caseStagesView);
    }

    @Test
    public void testSetStage_stageActive() {
        when(caseStageItemViewMock.getValue()).thenReturn(createCaseStageSummary(ACTIVE.getStatus()));
        getPresenter().setStage(caseStageItemViewMock);
        verify(caseStageItemViewMock).showStageActive();
    }

    @Test
    public void testSetStage_stageNotActive() {
        when(caseStageItemViewMock.getValue()).thenReturn(createCaseStageSummary(AVAILABLE.getStatus()),
                                                          createCaseStageSummary(COMPLETED.getStatus()),
                                                          createCaseStageSummary(CANCELED.getStatus()));
        getPresenter().setStage(caseStageItemViewMock);
        getPresenter().setStage(caseStageItemViewMock);
        getPresenter().setStage(caseStageItemViewMock);

        verify(caseStageItemViewMock,
               never()).showStageActive();
    }

    private CaseStageSummary createCaseStageSummary(final String stageStatus) {
        return CaseStageSummary.builder()
                               .identifier("stage")
                               .name("stageName")
                               .status(stageStatus)
                               .build();
    }
}