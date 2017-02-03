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

import java.util.List;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

@Dependent
@WorkbenchScreen(identifier = CaseStagesPresenter.SCREEN_ID)
public class CaseStagesPresenter extends AbstractCaseInstancePresenter<CaseStagesPresenter.CaseStagesView> {

    public static final String SCREEN_ID = "Case Stages Screen";


    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(Constants.CASE_STAGES);
    }

    @Override
    protected void clearCaseInstance() {
        view.removeAllStages();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.setCaseStagesList( cis.getStages());
    }

    public interface CaseStagesView extends UberElement<CaseStagesPresenter> {

        void removeAllStages();

        void setCaseStagesList(List<CaseStageSummary> caseStagesList);

    }

}