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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.client.events.CaseCreatedEvent;
import org.jbpm.workbench.cm.client.newcase.NewCaseInstancePresenter;
import org.jbpm.workbench.cm.client.overview.CaseOverviewPresenter;
import org.jbpm.workbench.cm.client.perspectives.CaseOverviewPerspective;
import org.jbpm.workbench.cm.client.util.AbstractPresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.CASE_LIST;

@Dependent
@WorkbenchScreen(identifier = CaseInstanceListPresenter.SCREEN_ID)
public class CaseInstanceListPresenter extends AbstractPresenter<CaseInstanceListPresenter.CaseInstanceListView> {

    public static final String SCREEN_ID = "Case List";

    private Caller<CaseManagementService> caseService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TranslationService translationService;

    @Inject
    private NewCaseInstancePresenter newCaseInstancePresenter;

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASE_LIST);
    }

    @PostConstruct
    @Override
    public void init() {
        super.init();
        refreshData();
    }

    public void createCaseInstance() {
        newCaseInstancePresenter.show();
    }

    protected void refreshData() {
        caseService.call((List<CaseInstanceSummary> cases) -> {
            view.setCaseInstanceList(cases);
        }).getCaseInstances(view.getCaseInstanceSearchRequest());
    }

    protected void selectCaseInstance(final CaseInstanceSummary cis) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(CaseOverviewPresenter.PARAMETER_SERVER_TEMPLATE_ID,
                       "");
        parameters.put(CaseOverviewPresenter.PARAMETER_CONTAINER_ID,
                       cis.getContainerId());
        parameters.put(CaseOverviewPresenter.PARAMETER_CASE_ID,
                       cis.getCaseId());
        final DefaultPlaceRequest overview = new DefaultPlaceRequest(CaseOverviewPerspective.PERSPECTIVE_ID,
                                                                     parameters);
        placeManager.goTo(overview);
    }

    protected void cancelCaseInstance(final CaseInstanceSummary caseInstanceSummary) {
        caseService.call(
                e -> refreshData()
        ).cancelCaseInstance(null,
                             caseInstanceSummary.getContainerId(),
                             caseInstanceSummary.getCaseId());
    }

    protected void closeCaseInstance(final CaseInstanceSummary caseInstanceSummary) {
        caseService.call(
                e -> refreshData()
        ).closeCaseInstance(caseInstanceSummary.getContainerId(),
                            caseInstanceSummary.getCaseId(),
                            null);
    }

    public void onCaseCreatedEvent(@Observes CaseCreatedEvent event) {
        refreshData();
    }

    protected void searchCaseInstances() {
        refreshData();
    }

    @Inject
    public void setCaseService(final Caller<CaseManagementService> caseService) {
        this.caseService = caseService;
    }

    public interface CaseInstanceListView extends UberElement<CaseInstanceListPresenter> {

        void setCaseInstanceList(List<CaseInstanceSummary> caseInstanceList);

        CaseInstanceSearchRequest getCaseInstanceSearchRequest();
    }
}