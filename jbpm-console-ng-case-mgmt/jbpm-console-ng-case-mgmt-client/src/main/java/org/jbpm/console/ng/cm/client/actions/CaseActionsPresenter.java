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

package org.jbpm.console.ng.cm.client.actions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.util.CaseActionSearchRequest;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseActionsPresenter.SCREEN_ID)
public class CaseActionsPresenter extends AbstractCaseInstancePresenter<CaseActionsPresenter.CaseActionListView> {

    public static final String SCREEN_ID = "Case Actions";

    @Inject
    private NewActionView newActionView;

    //@Inject
    private CaseSimpleActionListView simpleActionListView;

    @Inject
    User identity;

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASE_ACTIONS);
    }

    @Override
    protected void clearCaseInstance() {
        simpleActionListView.removeAllTasks();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        refreshData(caseId);
        updateListHeader(null, view.getCaseActionSearchRequest().getFilterBy());
        setupNewActions(cis);
    }

    protected void setSimpleActionListView(final CaseSimpleActionListView listView) {
        this.simpleActionListView = listView;
    }

    protected void searchCaseTasks() {
        refreshData(caseId);
    }

    protected void updateListHeader(CaseActionsFilterBy filterByOldValue, CaseActionsFilterBy filterByNewValue) {
        simpleActionListView.updateListHeader(filterByOldValue, filterByNewValue);
    }

    protected void refreshData(String caseId) {
        caseService.call((List<CaseActionSummary> actions) -> {
            simpleActionListView.setCaseActionList(actions);
        }).getCaseActions(containerId, caseId, view.getCaseActionSearchRequest(), identity.getIdentifier());
    }

    protected void setupNewActions(final CaseInstanceSummary cis) {
        caseService.call(
                (CaseDefinitionSummary cds) -> {
                    if (cds.getRoles() == null || cds.getRoles().isEmpty()) {
                        return;
                    }

                    final Set<String> roles = getRolesAvailableForAssignment(cis, cds);
                    if (roles.isEmpty()) {
                        return;
                    }

                    view.setTaskAddCommand(
                            () -> newActionView.show(true, roles,
                                    () -> addDynamicAction(newActionView.getTaskName(), newActionView.getDescription(),
                                            newActionView.getActors(), newActionView.getGroups())));
                    view.setProcessAddCommand(
                            () -> newActionView.show(true, roles,
                                    () -> addDynamicAction(newActionView.getTaskName(), newActionView.getDescription(),
                                            newActionView.getActors(), newActionView.getGroups())));

                }
        ).getCaseDefinition(serverTemplateId, containerId, cis.getCaseDefinitionId());
    }

    public Set<String> getRolesAvailableForAssignment(final CaseInstanceSummary cis, final CaseDefinitionSummary cds) {
        return cds.getRoles().keySet().stream().filter(
                role -> {
                    if ("owner".equals(role)) {
                        return false;
                    }
                    final Integer roleCardinality = cds.getRoles().get(role);
                    if (roleCardinality == -1) {
                        return true;
                    }
                    final Integer roleInstanceCardinality = cis.getRoleAssignments().stream().filter(ra -> role.equals(ra.getName())).findFirst().map(ra -> ra.getGroups().size() + ra.getUsers().size()).orElse(0);
                    return roleInstanceCardinality < roleCardinality;
                }
        ).collect(Collectors.toSet());
    }

    protected void addDynamicAction(final String taskName, final String taskDescription, String actors, String groups) {
        caseService.call((r) -> {
                    refreshData(caseId);
                }
        ).addDynamicUserTask(containerId, caseId, taskName, taskDescription, actors, groups, null);
    }

    protected void triggerAdhocFragment(final String actionName) {
        caseService.call((r) -> {
                    refreshData(caseId);
                }
        ).triggerAdHocFragmentInStage(containerId, caseId, "", actionName, null);
    }

    public interface CaseActionListView extends UberElement<CaseActionsPresenter> {

        CaseActionSearchRequest getCaseActionSearchRequest();

        void setTaskAddCommand(Command command);

        void setProcessAddCommand(Command command);

    }

    public interface CaseSimpleActionListView extends UberElement<CaseActionsPresenter> {

        void removeAllTasks();

        void setCaseActionList(List<CaseActionSummary> caseActionList);

        void updateListHeader(CaseActionsFilterBy oldFilterBy, CaseActionsFilterBy filterBy);

    }

    public interface NewActionView extends UberElement<CaseActionsPresenter> {

        void show(boolean forUser, Set<String> roles, Command okCommand);

        void hide();

        String getTaskName();

        String getDescription();

        String getActors();

        String getGroups();

    }

    public interface CaseActionAction extends Command {

        String label();

    }

}