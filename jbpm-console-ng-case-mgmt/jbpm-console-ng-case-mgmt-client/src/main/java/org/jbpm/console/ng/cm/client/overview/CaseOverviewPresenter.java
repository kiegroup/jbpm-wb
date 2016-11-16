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

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.cm.client.comments.CaseCommentsPresenter;
import org.jbpm.console.ng.cm.client.milestones.CaseMilestoneListPresenter;
import org.jbpm.console.ng.cm.client.details.CaseDetailsPresenter;
import org.jbpm.console.ng.cm.client.events.CaseCancelEvent;
import org.jbpm.console.ng.cm.client.events.CaseDestroyEvent;
import org.jbpm.console.ng.cm.client.events.CaseRefreshEvent;
import org.jbpm.console.ng.cm.client.perspectives.CaseInstanceListPerspective;
import org.jbpm.console.ng.cm.client.roles.CaseRolesPresenter;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnOpen;

import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseOverviewPresenter.SCREEN_ID)
public class CaseOverviewPresenter extends AbstractCaseInstancePresenter<CaseOverviewPresenter.CaseOverviewView> {

    public static final String SCREEN_ID = "Case Overview";

    @Inject
    PlaceManager placeManager;

    private Event<CaseCancelEvent> caseCancelEvent;

    private Event<CaseDestroyEvent> caseDestroyEvent;

    private Event<CaseRefreshEvent> caseRefreshEvent;

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASE_OVERVIEW);
    }

    @OnOpen
    public void onOpen() {
        view.addCaseDetails(CaseDetailsPresenter.SCREEN_ID, place.getParameters());
        view.addCaseRoles(CaseRolesPresenter.SCREEN_ID, place.getParameters());
        view.addCaseComments(CaseCommentsPresenter.SCREEN_ID, place.getParameters());
        view.addCaseMilestones(CaseMilestoneListPresenter.SCREEN_ID,place.getParameters());
    }

    protected void refreshCase() {
        caseRefreshEvent.fire(new CaseRefreshEvent(caseId));
    }

    @Override
    protected void clearCaseInstance() {
        view.setCaseTitle("");
        view.setCaseId("");
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.setCaseTitle(cis.getDescription());
        view.setCaseId(cis.getCaseId());
    }

    protected void cancelCaseInstance() {
        caseService.call(
                e -> {
                    caseCancelEvent.fire(new CaseCancelEvent(caseId));
                    backToList();
                }
        ).cancelCaseInstance(serverTemplateId, containerId, caseId);
    }

    protected void destroyCaseInstance() {
        caseService.call(
                e -> {
                    caseDestroyEvent.fire(new CaseDestroyEvent(caseId));
                    backToList();
                }
        ).destroyCaseInstance(serverTemplateId, containerId, caseId);
    }

    protected void backToList() {
        placeManager.goTo(CaseInstanceListPerspective.PERSPECTIVE_ID);
    }

    @Inject
    public void setCaseCancelEvent(final Event<CaseCancelEvent> caseCancelEvent) {
        this.caseCancelEvent = caseCancelEvent;
    }

    @Inject
    public void setCaseDestroyEvent(final Event<CaseDestroyEvent> caseDestroyEvent) {
        this.caseDestroyEvent = caseDestroyEvent;
    }

    @Inject
    public void setCaseRefreshEvent(final Event<CaseRefreshEvent> caseRefreshEvent) {
        this.caseRefreshEvent = caseRefreshEvent;
    }

    public interface CaseOverviewView extends UberElement<CaseOverviewPresenter> {

        void setCaseTitle(String title);

        void setCaseId(String caseId);

        void addCaseDetails(String placeId, Map<String, String> properties);

        void addCaseStages(String placeId, Map<String, String> properties);

        void addCaseActions(String placeId, Map<String, String> properties);

        void addCaseComments(String placeId, Map<String, String> properties);

        void addCaseFiles(String placeId, Map<String, String> properties);

        void addCaseRoles(String placeId, Map<String, String> properties);

        void addCaseMilestones(String placeId, Map<String, String> properties);

        void addCaseActivities(String placeId, Map<String, String> properties);

    }

}