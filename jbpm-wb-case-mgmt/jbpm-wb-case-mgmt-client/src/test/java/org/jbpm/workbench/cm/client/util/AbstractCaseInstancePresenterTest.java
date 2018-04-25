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

package org.jbpm.workbench.cm.client.util;

import java.util.Collections;
import java.util.Date;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.jbpm.workbench.cm.util.CaseStatus;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter.PARAMETER_CASE_ID;
import static org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter.PARAMETER_CONTAINER_ID;
import static org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter.PARAMETER_SERVER_TEMPLATE_ID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public abstract class AbstractCaseInstancePresenterTest {

    final protected static String serverTemplateId = "serverTemplateId",
                                  containerId = "containerId",
                                  caseDefId = "caseDefinitionId",
                                  caseId = "caseId";

    @Mock
    protected CaseManagementService caseManagementService;

    protected Caller<CaseManagementService> caseService;

    @Mock
    protected TranslationService translationService;

    protected static CaseInstanceSummary newCaseInstanceSummary() {
        return CaseInstanceSummary.builder()
                .caseId(caseId)
                .description("description")
                .status(CaseStatus.OPEN)
                .containerId(containerId)
                .owner("admin")
                .completedAt(new Date())
                .startedAt(new Date())
                .caseDefinitionId(caseDefId)
                .roleAssignments(Collections.emptyList())
                .build();
    }

    protected CaseStageSummary createCaseStageSummary(final String stageStatus) {
        return CaseStageSummary.builder()
                               .identifier("stage")
                               .name("stageName")
                               .status(stageStatus)
                               .build();
    }

    @Before
    public void init() {
        caseService = new CallerMock<>(caseManagementService);
        getPresenter().setCaseService(caseService);
        doAnswer(im -> im.getArguments()[0]).when(translationService).format(anyString());
    }

    protected CaseInstanceSummary setupCaseInstance(final String serverTemplateId) {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        setupCaseInstance(cis,
                          serverTemplateId);
        return cis;
    }

    protected void setupCaseInstance(final CaseInstanceSummary cis,
                                     final String serverTemplateId) {
        final PlaceRequest placeRequest = new DefaultPlaceRequest();
        placeRequest.addParameter(PARAMETER_SERVER_TEMPLATE_ID,
                                  serverTemplateId);
        placeRequest.addParameter(PARAMETER_CONTAINER_ID,
                                  cis.getContainerId());
        placeRequest.addParameter(PARAMETER_CASE_ID,
                                  cis.getCaseId());
        when(caseManagementService.getCaseInstance(serverTemplateId,
                                                   cis.getContainerId(),
                                                   cis.getCaseId())).thenReturn(cis);

        getPresenter().onStartup(placeRequest);
    }

    public abstract AbstractCaseInstancePresenter getPresenter();
}