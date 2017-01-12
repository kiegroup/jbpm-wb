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

import java.util.List;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

import org.uberfire.client.mvp.UberElement;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseMilestoneListPresenter.SCREEN_ID)
public class CaseMilestoneListPresenter extends AbstractCaseInstancePresenter<CaseMilestoneListPresenter.CaseMilestoneListView> {

        public static final String SCREEN_ID = "Case Milestone List";


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
        refreshData(caseId);
    }

    protected void searchCaseMilestones() {
        refreshData(caseId);
    }

    protected void refreshData(String caseId) {
        caseService.call((List<CaseMilestoneSummary> milestones) -> {
            view.setCaseMilestoneList(milestones);
        }).getCaseMilestones(containerId, caseId, view.getCaseMilestoneSearchRequest());
    }

    public interface CaseMilestoneListView extends UberElement<CaseMilestoneListPresenter> {

        void removeAllMilestones();

        void setCaseMilestoneList(List<CaseMilestoneSummary> caseMilestoneList);

        CaseMilestoneSearchRequest getCaseMilestoneSearchRequest();

    }

}