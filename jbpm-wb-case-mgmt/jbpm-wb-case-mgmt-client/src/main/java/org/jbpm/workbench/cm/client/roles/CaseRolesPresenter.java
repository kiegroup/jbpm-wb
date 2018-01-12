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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.client.util.CaseRolesValidations;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static java.util.stream.Collectors.toList;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;
import static org.jbpm.workbench.cm.client.util.CaseRolesAssignmentFilterBy.valueOf;
import static java.util.Collections.singletonList;

@Dependent
@WorkbenchScreen(identifier = CaseRolesPresenter.SCREEN_ID)
public class CaseRolesPresenter extends AbstractCaseInstancePresenter<CaseRolesPresenter.CaseRolesView> {

    public static final String SCREEN_ID = "Case Roles";
    public static final String CASE_OWNER_ROLE = "owner";

    private CaseDefinitionSummary caseDefinition;

    private List<CaseRoleAssignmentSummary> caseRolesAssignments = new ArrayList<>();

    @Inject
    private CaseRolesValidations caseRolesValidations;

    @WorkbenchPartTitle
    public String getTittle() {
        return translationService.format(ROLES);
    }

    void setCaseRolesAssignments(List<CaseRoleAssignmentSummary> caseRolesAssignments) {
        this.caseRolesAssignments = caseRolesAssignments;
    }

    List<CaseRoleAssignmentSummary> getCaseRolesAssignments() {
        return caseRolesAssignments;
    }

    @Override
    protected void clearCaseInstance() {
        getCaseRolesAssignments().clear();
        view.removeAllRoles();
        view.setBadge(getCaseRolesAssignments().size());
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        loadCaseRoles(cis);
    }

    protected void loadCaseRoles(final CaseInstanceSummary cis) {
        if (cis == null || cis.getCaseDefinitionId() == null || cis.getRoleAssignments().isEmpty()) {
            return;
        }
        caseService.call(
                (CaseDefinitionSummary cds) -> {
                    if (cds == null) {
                        return;
                    }
                    caseDefinition = cds;
                    setCaseRolesAssignments(cis.getRoleAssignments());
                    view.setBadge(getCaseRolesAssignments().size());
                    filterCaseRoles();
                }
        ).getCaseDefinition(serverTemplateId,
                            containerId,
                            cis.getCaseDefinitionId());
    }

    public void filterCaseRoles() {
        final List<CaseRoleAssignmentSummary> filteredCaseRoles;

        switch (valueOf(view.getFilterValue().toUpperCase())) {
            case ASSIGNED:
                filteredCaseRoles = getCaseRolesAssignments().stream()
                                                             .filter(CaseRoleAssignmentSummary::hasAssignment)
                                                             .collect(toList());
                break;
            case UNASSIGNED:
                filteredCaseRoles = getCaseRolesAssignments().stream()
                                                             .filter(caseRoleAssignmentSummary -> !caseRoleAssignmentSummary.hasAssignment())
                                                             .collect(toList());
                break;
            default:
                filteredCaseRoles = getCaseRolesAssignments();
        }

        if (filteredCaseRoles.isEmpty()) {
            view.displayEmptyList();
        } else {
            view.displayCaseRolesList(filteredCaseRoles);
        }
    }

    public void setCaseRoleActions(CaseRoleItemView caseRole){
        final CaseRoleAssignmentSummary caseRoleAssignmentSummary = caseRole.getValue();

        if (CASE_OWNER_ROLE.equals(caseRoleAssignmentSummary.getName())) {
            caseRole.displayOwnerActions();
        } else {
            boolean hasAssignment = caseRoleAssignmentSummary.hasAssignment();
            addCaseRoleAction(caseRole,
                              new CaseRoleAction() {
                                  @Override
                                  public String label() {
                                      if (hasAssignment) {
                                          return translationService.format(EDIT);
                                      }
                                      return translationService.format(ASSIGN);
                                  }

                                  @Override
                                  public boolean isEnabled() {
                                      return true;
                                  }

                                  @Override
                                  public void execute() {
                                      caseRole.showEditRoleAssignmentDialog();
                                  }
            });
            addCaseRoleAction(caseRole,
                              new CaseRoleAction() {
                                  @Override
                                  public String label() {
                                      return translationService.format(REMOVE_ALL_ASSIGNMENTS);
                                  }

                                  @Override
                                  public boolean isEnabled() {
                                      return hasAssignment;
                                  }

                                  @Override
                                  public void execute() {
                                      caseRole.showRemoveAllAssignmentsPopup();
                                  }
            });
        }
    }

    void addCaseRoleAction(CaseRoleItemView caseRole,
                           CaseRoleAction caseRoleAction) {
        if (caseRoleAction.isEnabled()) {
            caseRole.displayEnabledAction(caseRoleAction);
        } else {
            caseRole.displayDisabledAction(caseRoleAction);
        }
    }

    public void setCaseRoleAssignments(CaseRoleItemView caseRole,
                                       final int assignmentsListMaxWidth) {
        final CaseRoleAssignmentSummary caseRoleAssignmentSummary = caseRole.getValue();

        if (!caseRoleAssignmentSummary.hasAssignment()) {
            caseRole.displayUnassigned();
            return;
        }

        if (CASE_OWNER_ROLE.equals(caseRoleAssignmentSummary.getName())) {
            caseRole.displayOwnerAssignment();
        } else {
            if (caseRoleAssignmentSummary.getUsers().size() == 0) {
                caseRole.hideUserAssignments();
            } else {
                List<CaseAssignmentItem> itemsList = new ArrayList<>();
                caseRoleAssignmentSummary.getUsers().forEach(user -> itemsList.add(new CaseAssignmentItem() {
                                                                                       @Override
                                                                                       public String label() {
                                                                                           return user;
                                                                                       }

                                                                                       @Override
                                                                                       public void execute() {
                                                                                           caseRole.showRemoveUserAssignmentPopup(user);
                                                                                       }
                }));
                caseRole.displayAssignmentsList(CaseRoleItemView.USERS_LINE_ID,
                                                assignmentsListMaxWidth,
                                                itemsList);
            }
            if (caseRoleAssignmentSummary.getGroups().size() == 0) {
                caseRole.hideGroupAssignments();
            } else {
                List<CaseRolesPresenter.CaseAssignmentItem> itemsList = new ArrayList<>();
                caseRoleAssignmentSummary.getGroups().forEach(group -> itemsList.add(new CaseAssignmentItem() {
                                                                                         @Override
                                                                                         public String label() {
                                                                                             return group;
                                                                                         }

                                                                                         @Override
                                                                                         public void execute() {
                                                                                             caseRole.showRemoveGroupAssignmentPopup(group);
                                                                                         }
                }));
                caseRole.displayAssignmentsList(CaseRoleItemView.GROUP_LINE_ID,
                                                assignmentsListMaxWidth,
                                                itemsList);
            }
        }
    }

    void assignToRole(final CaseRoleItemView caseRole,
                      final CaseRoleAssignmentSummary newRoleAssignments) {
        if (!newRoleAssignments.hasAssignment()) {
            caseRole.showErrorState();
            caseRole.showAssignmentErrors(singletonList(translationService.format(PLEASE_INTRO_USER_OR_GROUP_TO_CREATE_ASSIGNMENT)));
            return;
        }
        List<String> caseRoleAssignmentErrors = caseRolesValidations.validateRolesAssignments(caseDefinition,
                                                                                              singletonList(newRoleAssignments));
        if (!caseRoleAssignmentErrors.isEmpty()) {
            caseRole.showAssignmentErrors(caseRoleAssignmentErrors);
        } else {
            caseRole.hideEditRoleAssignmentDialog();
            final CaseRoleAssignmentSummary currentRoleAssignments = caseRole.getValue();
            storeRoleAssignments(currentRoleAssignments,
                                 newRoleAssignments.getUsers(),
                                 newRoleAssignments.getGroups());
        }
    }

    public void storeRoleAssignments(final CaseRoleAssignmentSummary currentRoleAssignments,
                                     List<String> users,
                                     List<String> groups) {
        final String roleName = currentRoleAssignments.getName();
        List<String> prevUserAssignments = currentRoleAssignments.getUsers();
        List<String> prevGroupsAssignments = currentRoleAssignments.getGroups();
        List<String> newUserAssignments = users.stream().distinct().collect(toList());
        List<String> newGroupsAssignments = groups.stream().distinct().collect(toList());

        currentRoleAssignments.setUsers(new ArrayList<>(newUserAssignments));
        currentRoleAssignments.setGroups(new ArrayList<>(newGroupsAssignments));
        filterCaseRoles();

        List<String> usersToRemove = new ArrayList<>(prevUserAssignments);
        usersToRemove.removeAll(newUserAssignments);
        usersToRemove.forEach(
                user -> caseService.call().removeUserFromRole(serverTemplateId,
                                                              containerId,
                                                              caseId,
                                                              roleName,
                                                              user));

        newUserAssignments.removeAll(prevUserAssignments);
        newUserAssignments.forEach(
                user -> caseService.call().assignUserToRole(serverTemplateId,
                                                            containerId,
                                                            caseId,
                                                            roleName,
                                                            user));

        List<String> groupsToRemove = new ArrayList<>(prevGroupsAssignments);
        groupsToRemove.removeAll(newGroupsAssignments);
        groupsToRemove.forEach(
                group -> caseService.call().removeGroupFromRole(serverTemplateId,
                                                                containerId,
                                                                caseId,
                                                                roleName,
                                                                group));

        newGroupsAssignments.removeAll(prevGroupsAssignments);
        newGroupsAssignments.forEach(
                group -> caseService.call().assignGroupToRole(serverTemplateId,
                                                              containerId,
                                                              caseId,
                                                              roleName,
                                                              group));
    }

    protected void removeUserFromRole(final String userName,
                                      final CaseRoleAssignmentSummary caseRoleAssignmentSummary) {
        caseService.call(
                (Void) -> {
                    List<String> users = caseRoleAssignmentSummary.getUsers();
                    users.remove(userName);
                    caseRoleAssignmentSummary.setUsers(users);
                    filterCaseRoles();
                }
        ).removeUserFromRole(serverTemplateId,
                             containerId,
                             caseId,
                             caseRoleAssignmentSummary.getName(),
                             userName);
    }

    protected void removeGroupFromRole(final String groupName,
                                       final CaseRoleAssignmentSummary caseRoleAssignmentSummary) {
        caseService.call(
                (Void) -> {
                    List<String> groups = caseRoleAssignmentSummary.getGroups();
                    groups.remove(groupName);
                    caseRoleAssignmentSummary.setGroups(groups);
                    filterCaseRoles();
                }
        ).removeGroupFromRole(serverTemplateId,
                              containerId,
                              caseId,
                              caseRoleAssignmentSummary.getName(),
                              groupName);
    }

    public interface CaseRolesView extends UberElement<CaseRolesPresenter> {

        void removeAllRoles();

        void displayCaseRolesList(List<CaseRoleAssignmentSummary> caseRolesList);

        void displayEmptyList();

        String getFilterValue();

        void setBadge(int caseRolesNumber);
    }

    public interface EditRoleAssignmentView extends UberElement<CaseRolesPresenter>,
                                                    TakesValue<CaseRoleAssignmentSummary> {

        void show(Command command);

        void hide();

        void setErrorState();

        void showValidationError(List<String> messages);
    }

    public interface CaseRoleAction extends Command {

        String label();

        boolean isEnabled();
    }

    public interface CaseAssignmentItem extends Command {

        String label();
    }
}