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

package org.jbpm.workbench.cm.client.roles;

import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseRolesPresenter.SCREEN_ID)
public class CaseRolesPresenter extends AbstractCaseInstancePresenter<CaseRolesPresenter.CaseRolesView> {

    public static final String SCREEN_ID = "Case Roles";

    @Inject
    private NewRoleAssignmentView newRoleAssignmentView;

    @WorkbenchPartTitle
    public String getTittle() {
        return translationService.format(ROLES);
    }

    @Override
    protected void clearCaseInstance() {
        view.disableNewRoleAssignments();
        view.removeAllRoles();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.addUser(cis.getOwner(), translationService.format(OWNER));

        setupRoleAssignments(cis);

        setupNewRoleAssignments(cis);
    }

    protected void setupNewRoleAssignments(final CaseInstanceSummary cis) {
        caseService.call(
                (CaseDefinitionSummary cds) -> {
                    if (cds.getRoles() == null || cds.getRoles().isEmpty()) {
                        return;
                    }

                    final Set<String> roles = getRolesAvailableForAssignment(cis, cds);
                    if (roles.isEmpty()) {
                        return;
                    }

                    view.enableNewRoleAssignments();

                    view.setUserAddCommand(() -> newRoleAssignmentView.show(true, roles, () -> addUserToRole(newRoleAssignmentView.getUserName(), newRoleAssignmentView.getRoleName())));
                    view.setGroupAddCommand(() -> newRoleAssignmentView.show(false, roles, () -> addGroupToRole(newRoleAssignmentView.getUserName(), newRoleAssignmentView.getRoleName())));
                }
        ).getCaseDefinition(serverTemplateId, containerId, cis.getCaseDefinitionId());
    }

    protected Set<String> getRolesAvailableForAssignment(final CaseInstanceSummary cis, final CaseDefinitionSummary cds) {
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

    protected void setupRoleAssignments(final CaseInstanceSummary cis) {
        if (cis.getRoleAssignments() == null || cis.getRoleAssignments().isEmpty()) {
            return;
        }

        cis.getRoleAssignments().forEach(
                crs -> {
                    crs.getUsers().forEach(user -> view.addUser(user, crs.getName(), new CaseRoleAction() {

                        @Override
                        public String label() {
                            return translationService.format(REMOVE);
                        }

                        @Override
                        public void execute() {
                            removeUserFromRole(user, crs.getName());
                        }
                    }));
                    crs.getGroups().forEach(group -> view.addGroup(group, crs.getName(), new CaseRoleAction() {

                        @Override
                        public String label() {
                            return translationService.format(REMOVE);
                        }

                        @Override
                        public void execute() {
                            removeGroupFromRole(group, crs.getName());
                        }
                    }));
                }
        );
    }

    protected void addUserToRole(final String userName, final String roleName) {
        caseService.call(
                (Void) -> findCaseInstance()
        ).assignUserToRole(serverTemplateId, containerId, caseId, roleName, userName);
    }

    protected void addGroupToRole(final String groupName, final String roleName) {
        caseService.call(
                (Void) -> findCaseInstance()
        ).assignGroupToRole(serverTemplateId, containerId, caseId, roleName, groupName);
    }

    protected void removeUserFromRole(final String userName, final String roleName) {
        caseService.call(
                (Void) -> findCaseInstance()
        ).removeUserFromRole(serverTemplateId, containerId, caseId, roleName, userName);
    }

    protected void removeGroupFromRole(final String groupName, final String roleName) {
        caseService.call(
                (Void) -> findCaseInstance()
        ).removeGroupFromRole(serverTemplateId, containerId, caseId, roleName, groupName);
    }

    public interface CaseRolesView extends UberElement<CaseRolesPresenter> {

        void removeAllRoles();

        void addUser(String userName, String roleName, CaseRoleAction... actions);

        void addGroup(String groupName, String roleName, CaseRoleAction... actions);

        void setUserAddCommand(Command command);

        void setGroupAddCommand(Command command);

        void enableNewRoleAssignments();

        void disableNewRoleAssignments();
    }

    public interface NewRoleAssignmentView extends UberElement<CaseRolesPresenter> {

        void show(Boolean forUser, Set<String> roles, Command okCommand);

        void hide();

        String getRoleName();

        String getUserName();

    }

    public interface CaseRoleAction extends Command {

        String label();

    }

}