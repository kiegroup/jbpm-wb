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

package org.jbpm.workbench.cm.client.milestones;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

import org.uberfire.client.mvp.UberElement;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;
import static java.util.stream.Collectors.toList;

@Dependent
@WorkbenchScreen(identifier = CaseMilestoneListPresenter.SCREEN_ID)
public class CaseMilestoneListPresenter extends AbstractCaseInstancePresenter<CaseMilestoneListPresenter.CaseMilestoneListView> {

    public static final int PAGE_SIZE = 10;

    public static final String SCREEN_ID = "Case Milestone List";

    Set<CaseMilestoneSummary> visibleCaseMilestones = new HashSet<CaseMilestoneSummary>();

    public CaseMilestoneListPresenter() {
        setPageSize();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(MILESTONES);
    }

    @Override
    protected void clearCaseInstance() {
        view.removeAllMilestones();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        refreshData();
    }
    
    @Override
    public void setPageSize() {
        this.pageSize = PAGE_SIZE;          
    }

    protected void searchCaseMilestones() {
        refreshData();
    }

    protected void refreshData() {
        milestonesServiceCall(caseId,
                              getCurrentPage());
    }

    protected void milestonesServiceCall(String caseId,
                                         int currentPage) {
        caseService.call((List<CaseMilestoneSummary> milestones) -> {
            visibleCaseMilestones.addAll(milestones);
            view.setCaseMilestoneList(visibleCaseMilestones.stream()
                                          .collect(toList()));
        }).getCaseMilestones(containerId,
                             caseId,
                             view.getCaseMilestoneSearchRequest(),
                             getCurrentPage(),
                             getPageSize());

        loadButtonToggle();
    }

    protected void loadMoreCaseMilestones() {
        setCurrentPage(getCurrentPage() + 1);
        milestonesServiceCall(caseId,
                              getCurrentPage());
    }

    private void loadButtonToggle() {
        caseService.call((List<CaseMilestoneSummary> milestones) -> {
            if (milestones.isEmpty()) {
                view.hideLoadButton();
            } else {
                view.showLoadButton();
            }
        }).getCaseMilestones(containerId,
                             caseId,
                             view.getCaseMilestoneSearchRequest(),
                             getCurrentPage() + 1,
                             getPageSize());
    }

    public interface CaseMilestoneListView extends UberElement<CaseMilestoneListPresenter> {

        void removeAllMilestones();

        void setCaseMilestoneList(List<CaseMilestoneSummary> caseMilestoneList);

        CaseMilestoneSearchRequest getCaseMilestoneSearchRequest();

        void hideLoadButton();

        void showLoadButton();
    }
}