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

package org.jbpm.workbench.cm.client.actions;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseActionSummary;

@Dependent
@Templated
public class CaseActionsViewImpl extends AbstractView<CaseActionsPresenter> implements CaseActionsPresenter.CaseActionsView {

    public static final String AVAILABLE_ACTIONS = "AVAILABLE";
    public static final String INPROGRESS_ACTIONS = "IN_PROGRESS";
    public static final String COMPLETED_ACTIONS = "COMPLETED";

    @Inject
    @DataField("available-actions")
    CaseActionsListViewImpl availableActions;

    @Inject
    @DataField("inprogress-actions")
    CaseActionsListViewImpl inprogressActions;

    @Inject
    @DataField("completed-actions")
    CaseActionsListViewImpl completedActions;

    @Inject
    @DataField("actions")

    private Div actionsContainer;

    @Inject
    private TranslationService translationService;

    @Override
    public void init(final CaseActionsPresenter presenter) {
        this.presenter = presenter;
        availableActions.init(presenter);
        inprogressActions.init(presenter);
        completedActions.init(presenter);
    }


    @Override
    public HTMLElement getElement() {
        return actionsContainer;
    }

    @Override
    public void removeAllTasks() {
        availableActions.removeAllTasks();
        inprogressActions.removeAllTasks();
        completedActions.removeAllTasks();
    }

    @Override
    public void setAvailableActionsList(List<CaseActionSummary> caseActionList) {
        availableActions.setCaseActionList(caseActionList);
    }

    @Override
    public void setInProgressActionsList(List<CaseActionSummary> caseActionList) {
        inprogressActions.setCaseActionList(caseActionList);
    }

    @Override
    public void setCompletedActionsList(List<CaseActionSummary> caseActionList) {
        completedActions.setCaseActionList(caseActionList);
    }

    @Override
    public void updateListHeaders() {
        availableActions.updateActionsHeader(translationService.format(AVAILABLE_ACTIONS),
                "fa", "fa-flag-o", "kie-card__subtitle-icon", "kie-card__subtitle-icon--available");
        inprogressActions.updateActionsHeader(translationService.format(INPROGRESS_ACTIONS),
                "fa", "fa-flag-checkered", "kie-card__subtitle-icon", "kie-card__subtitle-icon--inprogress");
        completedActions.updateActionsHeader(translationService.format(COMPLETED_ACTIONS),
                "fa", "fa-flag", "kie-card__subtitle-icon", "kie-card__subtitle-icon--complete");
    }

}