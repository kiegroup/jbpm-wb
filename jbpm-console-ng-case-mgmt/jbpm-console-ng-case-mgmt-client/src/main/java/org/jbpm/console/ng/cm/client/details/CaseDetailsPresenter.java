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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.console.ng.cm.client.resources.i18n.Constants;
import org.jbpm.console.ng.cm.client.util.CaseStatusEnum;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = CaseDetailsPresenter.SCREEN_ID)
public class CaseDetailsPresenter extends AbstractCaseInstancePresenter {

    public static final String SCREEN_ID = "Case Details Screen";

    @Inject
    private CaseDetailsView view;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(Constants.CASE_DETAILS);
    }

    @WorkbenchPartView
    public UberView<CaseDetailsPresenter> getView() {
        return view;
    }

    @Override
    protected void clearCaseInstance() {
        view.setCaseId("");
        view.setCaseStatus("");
        view.setCaseDescription("");
        view.setCaseStartedAt("");
        view.setCaseCompletedAt("");
        view.setCaseOwner("");
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.setCaseId(cis.getCaseId());
        view.setCaseStatus(translationService.format(CaseStatusEnum.fromStatus(cis.getStatus()).getLabel()));
        view.setCaseDescription(cis.getDescription());
        view.setCaseStartedAt(DateUtils.getDateTimeStr(cis.getStartedAt()));
        view.setCaseCompletedAt(DateUtils.getDateTimeStr(cis.getCompletedAt()));
        view.setCaseOwner(cis.getOwner());
    }

    public interface CaseDetailsView extends UberView<CaseDetailsPresenter> {

        void setCaseDescription(String text);

        void setCaseStatus(String status);

        void setCaseId(String caseId);

        void setCaseStartedAt(String date);

        void setCaseCompletedAt(String date);

        void setCaseOwner(String owner);

    }

}