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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseActionsPresenter.SCREEN_ID)
public class CaseActionsPresenter extends AbstractCaseInstancePresenter<CaseActionsPresenter.CaseActionsView> {

    public static final String SCREEN_ID = "Case Actions";

    @Inject
    User identity;

    @Inject
    private NewActionView newActionView;

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASE_ACTIONS);
    }

    @Override
    protected void clearCaseInstance() {
        view.removeAllTasks();
        newActionView.setCaseStagesList(new ArrayList<>());
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.updateListHeaders();
        newActionView.setCaseStagesList(cis.getStages());
        refreshData(true);
    }

    protected void showAddDynUserTaskAction() {
        newActionView.show(() ->
                addDynamicAction(newActionView.getTaskName(),
                        newActionView.getDescription(),
                        newActionView.getActors(),
                        newActionView.getGroups(),
                        newActionView.getStageId()));
    }

    protected void addDynamicAction(final String taskName, final String taskDescription, String actors, String groups, String stageId) {
        if (isNullOrEmpty(stageId)) {
            caseService.call((r) -> refreshData(false)).addDynamicUserTask(containerId, caseId, taskName, taskDescription, actors, groups, null);
        } else {
            caseService.call((r) -> refreshData(false)).addDynamicUserTaskToStage(containerId, caseId, stageId, taskName, taskDescription, actors, groups, null);
        }
    }

    protected void triggerAdHocAction(final String actionName) {
        caseService.call((r) -> refreshData(false)).triggerAdHocAction(containerId, caseId, actionName, null);
    }

    protected void triggerAdHocActionInStage(final String actionName, final String stageId) {
        caseService.call((r) -> refreshData(false)).triggerAdHocActionInStage(containerId, caseId, stageId, actionName, null);
    }

    protected void refreshData(final boolean refreshAvailableActions) {
        caseService.call((Actions actions) -> {
            if (refreshAvailableActions) {
                List<CaseActionSummary> availableActions = new ArrayList<>();
                availableActions.add(CaseActionSummary.builder()
                        .name(translationService.getTranslation(ADD_DYNAMIC_USER_TASK))
                        .status(CaseActionType.ADD_DYNAMIC_USER_TASK)
                        .build());
                availableActions.addAll(actions.getAvailableActions());
                view.setAvailableActionsList(availableActions);
            }
            view.setInProgressActionsList(actions.getInProgressAction());
            view.setCompletedActionsList(actions.getCompleteActions());
        }).getCaseActions(serverTemplateId, containerId, caseId, identity.getIdentifier());
    }

    public interface CaseActionsView extends UberElement<CaseActionsPresenter> {

        void removeAllTasks();

        void setAvailableActionsList(List<CaseActionSummary> caseActionList);

        void setInProgressActionsList(List<CaseActionSummary> caseActionList);

        void setCompletedActionsList(List<CaseActionSummary> caseActionList);

        void updateListHeaders();

    }

    public interface CaseActionsListView extends UberElement<CaseActionsPresenter> {

        void removeAllTasks();

        void setCaseActionList(List<CaseActionSummary> caseActionList);

        void updateActionsHeader(final String heatherText, final String... stylesClass);

    }

    public interface NewActionView extends UberElement<CaseActionsPresenter> {

        void show(Command okCommand);

        void hide();

        String getTaskName();

        String getDescription();

        String getActors();

        String getGroups();

        void setCaseStagesList(List<CaseStageSummary> caseStagesList);

        String getStageId();

    }

    public interface CaseActionAction extends Command {

        String label();

    }

}