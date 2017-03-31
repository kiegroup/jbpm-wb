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
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.client.util.CaseRolesAssignmentFilterBy;
import org.jbpm.workbench.cm.client.util.CaseRolesValidations;
import org.jbpm.workbench.cm.client.util.ConfirmPopup;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static java.util.stream.Collectors.toList;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@WorkbenchScreen(identifier = CaseRolesPresenter.SCREEN_ID)
public class CaseRolesPresenter extends AbstractCaseInstancePresenter<CaseRolesPresenter.CaseRolesView> {

    public static final String SCREEN_ID = "Case Roles";
    public static final String CASE_OWNER_ROLE = "owner";

    @Inject
    private EditRoleAssignmentView editRoleAssignmentView;

    @Inject
    ConfirmPopup confirmPopup;

    @Inject
    CaseRolesValidations caseRolesValidations;

    List<CaseRoleAssignmentSummary> allUnfilteredElements = new ArrayList();

    @WorkbenchPartTitle
    public String getTittle() {
        return translationService.format(ROLES);
    }

    private CaseDefinitionSummary caseDefinition;

    @Override
    protected void clearCaseInstance() {
        view.removeAllRoles();
        allUnfilteredElements = new ArrayList();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        setupExistingAssignments(cis);
    }

    protected void setupExistingAssignments(final CaseInstanceSummary cis) {
        caseService.call(
                (CaseDefinitionSummary cds) -> {
                    if (cds == null || cds.getRoles() == null || cds.getRoles().isEmpty()) {
                        return;
                    }
                    caseDefinition = cds;
                    cds.getRoles()
                            .keySet()
                            .forEach(roleName -> allUnfilteredElements.add(getRoleAssignment(cis, roleName)));
                    filterElements();
                }
        ).getCaseDefinition(serverTemplateId, containerId, cis.getCaseDefinitionId());
    }

    private boolean hasAssignment(CaseRoleAssignmentSummary caseRoleAssignmentSummary) {
        return caseRoleAssignmentSummary.getUsers().size() + caseRoleAssignmentSummary.getGroups().size() > 0;

    }

    public void filterElements() {
        String filterBy = view.getFilterValue();

        if (filterBy.equals(CaseRolesAssignmentFilterBy.ASSIGNED.getLabel())) {
            view.setRolesAssignmentList(
                    allUnfilteredElements.stream()
                            .filter(caseRoleAssignmentSummary -> hasAssignment(caseRoleAssignmentSummary))
                            .collect(toList()));
        } else if (filterBy.equals(CaseRolesAssignmentFilterBy.UNASSIGNED.getLabel())) {
            view.setRolesAssignmentList(
                    allUnfilteredElements.stream()
                            .filter(caseRoleAssignmentSummary -> !hasAssignment(caseRoleAssignmentSummary))
                            .collect(toList()));
        } else {
            view.setRolesAssignmentList(allUnfilteredElements);
        }
    }

    public void editAction(CaseRoleAssignmentSummary caseRoleAssignmentSummary, final List<String> oldUserAssignments,
                           final List<String> oldGroupsAssignments) {
        editRoleAssignmentView.setValue(CaseRoleAssignmentSummary.builder()
                .name(caseRoleAssignmentSummary.getName())
                .users(caseRoleAssignmentSummary.getUsers())
                .groups(caseRoleAssignmentSummary.getGroups()).build());

        editRoleAssignmentView.show(() -> assignToRole(oldUserAssignments, oldGroupsAssignments));
    }

    protected CaseRoleAssignmentSummary getRoleAssignment(final CaseInstanceSummary cis, final String roleName) {
        if (CASE_OWNER_ROLE.equals(roleName)) {
            return CaseRoleAssignmentSummary.builder().name(roleName).users(Arrays.asList(cis.getOwner())).build();
        }
        return cis.getRoleAssignments()
                .stream()
                .filter(ra -> roleName.equals(ra.getName()))
                .findFirst().orElse(CaseRoleAssignmentSummary.builder().name(roleName).build());
    }

    protected void assignToRole(final List<String> oldUserAssignments,
                                final List<String> oldGroupsAssignments) {

        final List<String> assignmentErrors =
                caseRolesValidations.validateRolesAssignments(caseDefinition,
                        Arrays.asList(editRoleAssignmentView.getValue()));
        if (assignmentErrors.isEmpty() == false) {
            editRoleAssignmentView.showValidationError(assignmentErrors);
            return;
        }
        editRoleAssignmentView.hide();
        storeRoleAssignments(editRoleAssignmentView.getValue().getName(),
                editRoleAssignmentView.getValue().getUsers(),
                editRoleAssignmentView.getValue().getGroups(),
                oldUserAssignments,
                oldGroupsAssignments);
    }

    protected CaseRoleAssignmentSummary getRoleAssignmentByName(String roleName) {
        return allUnfilteredElements.stream()
                .filter(caseRoleAssignmentSummary -> roleName.equals(caseRoleAssignmentSummary.getName()))
                .findFirst().get();
    }

    protected void storeRoleAssignments(String roleName, List<String> users, List<String> groups,
                                        List<String> oldUserAssignments, List<String> oldGroupsAssignments) {

        CaseRoleAssignmentSummary cras = getRoleAssignmentByName(roleName);
        cras.setGroups(new ArrayList<>(groups));
        cras.setUsers(new ArrayList<>(users));
        filterElements();

        List<String> usersToRemove = new ArrayList<String>(oldUserAssignments);
        usersToRemove.removeAll(users);
        usersToRemove.forEach(
                user -> caseService.call().removeUserFromRole(serverTemplateId, containerId, caseId, roleName, user));

        users.removeAll(oldUserAssignments);
        users.stream().distinct().forEach(
                user -> caseService.call().assignUserToRole(serverTemplateId, containerId, caseId, roleName, user));

        List<String> groupsToRemove = new ArrayList<String>(oldGroupsAssignments);
        groupsToRemove.removeAll(groups);
        groupsToRemove.forEach(
                group -> caseService.call().removeGroupFromRole(serverTemplateId, containerId, caseId, roleName, group));

        groups.removeAll(oldGroupsAssignments);
        groups.stream().distinct().forEach(
                group -> caseService.call().assignGroupToRole(serverTemplateId, containerId, caseId, roleName, group));

    }

    protected void removeUserFromRole(final String userName, final String roleName) {
        CaseRoleAssignmentSummary cras = getRoleAssignmentByName(roleName);
        cras.getUsers().remove(userName);
        filterElements();
        caseService.call(
        ).removeUserFromRole(serverTemplateId, containerId, caseId, roleName, userName);
    }

    protected void removeGroupFromRole(final String groupName, final String roleName) {
        CaseRoleAssignmentSummary cras = getRoleAssignmentByName(roleName);
        cras.getGroups().remove(groupName);
        filterElements();
        caseService.call(
        ).removeGroupFromRole(serverTemplateId, containerId, caseId, roleName, groupName);
    }

    public interface CaseRolesView extends UberElement<CaseRolesPresenter> {

        void removeAllRoles();

        void setRolesAssignmentList(List<CaseRoleAssignmentSummary> caseRoleAssignmentSummaryList);

        void resetPagination();

        String getFilterValue();

    }

    public interface EditRoleAssignmentView extends UberElement<CaseRolesPresenter>, TakesValue<CaseRoleAssignmentSummary> {

        void show(Command command);

        void showValidationError(List<String> messages);

        void hide();

    }

    public interface CaseRoleAction extends Command {

        String label();

        boolean isEnabled();

    }

    public interface CaseAssignmentItem extends Command {

        String label();

    }
}