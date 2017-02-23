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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRolesPresenterTest extends AbstractCaseInstancePresenterTest {

    private static final String USER = "User";
    private static final String GROUP = "Group";
    private static final String CASE_ROLE = "Role";
    private static final String CASE_DEFINITION_ID = "org.jbpm.case";

    @Mock
    CaseRolesPresenter.CaseRolesView view;

    @Mock
    CaseRolesPresenter.NewRoleAssignmentView assignmentView;

    @InjectMocks
    CaseRolesPresenter presenter;

    @Override
    public CaseRolesPresenter getPresenter() {
        return presenter;
    }

    @Test
    public void testClearCaseInstance() {
        presenter.clearCaseInstance();

        verifyClearCaseInstance();
    }

    private void verifyClearCaseInstance() {
        verify(view).removeAllRoles();
        verify(view).disableNewRoleAssignments();
    }

    @Test
    public void testLoadCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE, 3))
                .build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(singletonList(GROUP)).users(singletonList(USER)).build()));
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(serverTemplateId, caseInstance.getContainerId(), caseInstance.getCaseDefinitionId()))
                .thenReturn(caseDefinition);

        setupCaseInstance(caseInstance, serverTemplateId);

        verifyClearCaseInstance();
        verify(view).addUser("admin", "Owner");

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(view).addUser(eq(USER), eq(CASE_ROLE), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(view).addGroup(eq(GROUP), eq(CASE_ROLE), captor.capture());
        assertEquals("Remove", captor.getValue().label());

        verify(view).enableNewRoleAssignments();
        verify(view).setUserAddCommand(any(Command.class));

    }

    @Test
    public void testSetupRoleAssignments_whenNoAssignmentsAreMade() {
        presenter.setupRoleAssignments(newCaseInstanceSummary());

        verifyZeroInteractions(view);
    }

    @Test
    public void testSetupRoleAssignments_onlyUsersAssigned() {
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(emptyList()).users(singletonList(USER)).build()));

        presenter.setupRoleAssignments(caseInstance);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(view).addUser(eq(USER), eq(CASE_ROLE), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(view, never()).addGroup(anyString(), anyString(), anyVararg());

        captor.getValue().execute();
        verify(caseManagementService).removeUserFromRole(anyString(), anyString(), anyString(), eq(CASE_ROLE), eq(USER));
    }

    @Test
    public void testSetupRoleAssignments_onlyGroupsAssigned() {
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(singletonList(GROUP)).users(emptyList()).build()));

        presenter.setupRoleAssignments(caseInstance);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(view).addGroup(eq(GROUP), eq(CASE_ROLE), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(view, never()).addUser(anyString(), anyString(), anyVararg());

        captor.getValue().execute();
        verify(caseManagementService).removeGroupFromRole(anyString(), anyString(), anyString(), eq(CASE_ROLE), eq(GROUP));
    }

    @Test
    public void testSetupNewRoleAssignments_rolesNotDefined() {
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder().build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(CASE_DEFINITION_ID))).thenReturn(caseDefinition);

        presenter.setupNewRoleAssignments(caseInstance);

        verifyZeroInteractions(view);
    }

    @Test
    public void testSetupNewRoleAssignments_noRolesAvailableForAssignment() {
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE, 1))
                .build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(emptyList()).users(singletonList(USER)).build()));
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(CASE_DEFINITION_ID))).thenReturn(caseDefinition);

        presenter.setupNewRoleAssignments(caseInstance);

        verifyZeroInteractions(view);
    }

    @Test
    public void testSetupNewRoleAssignments_rolesAvailableForAssignment() {
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE, 1))
                .build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(CASE_DEFINITION_ID))).thenReturn(caseDefinition);

        presenter.setupNewRoleAssignments(caseInstance);

        verify(view).enableNewRoleAssignments();

        final ArgumentCaptor<Command> captor = ArgumentCaptor.forClass(Command.class);
        verify(view).setUserAddCommand(captor.capture());
        captor.getValue().execute();
        verify(assignmentView).show( eq(singleton(CASE_ROLE)), captor.capture());

        when(assignmentView.getUserName()).thenReturn("user1");
        when(assignmentView.getGroupName()).thenReturn("");
        captor.getValue().execute();
        verify(caseManagementService).assignUserToRole(anyString(), anyString(), anyString(), anyString(), anyString());

        when(assignmentView.getUserName()).thenReturn("");
        when(assignmentView.getGroupName()).thenReturn("groupName");
        captor.getValue().execute();
        verify(caseManagementService).assignGroupToRole(anyString(), anyString(), anyString(), anyString(), anyString());

        when(assignmentView.getUserName()).thenReturn("user1");
        when(assignmentView.getGroupName()).thenReturn("groupName");
        captor.getValue().execute();
        verify(caseManagementService).assignGroupAndUserToRole(anyString(), anyString(), anyString(), anyString(), anyString(),anyString());
    }

    @Test
    public void testGetRolesAvailableForAssignment_excludeOwnerRole() {
        final String ownerRole = "owner";
        final Map<String, Integer> roles = new HashMap<>();
        roles.put(ownerRole, 1);

        final String[] rolesNames = {"Owner", " owner ", "OWNER"};
        final Integer rolesCardinality = -1;
        Arrays.stream(rolesNames).forEach(role -> roles.put(role, rolesCardinality));

        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder()
                .roles(roles)
                .build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);

        final Set<String> availableRoles = presenter.getRolesAvailableForAssignment(caseInstance, caseDefinition);

        assertThat(availableRoles)
                .doesNotContain(ownerRole)
                .containsAll(Arrays.asList(rolesNames));
    }

    @Test
    public void testGetRolesAvailableForAssignment_rolesWithDifferentCardinality() {
        final String caseRole_1 = "Role_1";
        final String caseRole_2 = "Role_2";
        final String caseRole_3 = "Role_3";
        final Map<String, Integer> roles = new HashMap<>();
        roles.put(caseRole_1, -1);
        roles.put(caseRole_2, 2);
        roles.put(caseRole_3, 1);
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder().
                roles(roles).
                build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        caseInstance.setRoleAssignments(caseDefinition.getRoles().keySet().stream().map(
                role -> CaseRoleAssignmentSummary.builder()
                        .name(role)
                        .groups(emptyList())
                        .users(emptyList())
                        .build()
        ).collect(Collectors.toList()));
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);

        final Set<String> availableRolesFirstPass = presenter.getRolesAvailableForAssignment(caseInstance, caseDefinition);

        assertThat(availableRolesFirstPass)
                .contains(caseRole_1, caseRole_2, caseRole_3);


        caseInstance.getRoleAssignments().stream()
                .filter(roleAssignment -> availableRolesFirstPass.contains(roleAssignment.getName()))
                .forEach(roleAssignment -> roleAssignment.setUsers(singletonList(USER)));

        final Set<String> availableRolesSecondPass = presenter.getRolesAvailableForAssignment(caseInstance, caseDefinition);

        assertThat(availableRolesSecondPass)
                .contains(caseRole_1, caseRole_2)
                .doesNotContain(caseRole_3);

        caseInstance.getRoleAssignments().stream()
                .filter(roleAssignment -> availableRolesSecondPass.contains(roleAssignment.getName()))
                .forEach(roleAssignment -> roleAssignment.setGroups(singletonList(GROUP)));

        final Set<String> availableRolesThirdPass = presenter.getRolesAvailableForAssignment(caseInstance, caseDefinition);

        assertThat(availableRolesThirdPass)
                .contains(caseRole_1)
                .doesNotContain(caseRole_2, caseRole_3);
    }

    private void setCaseDefinitionID(String caseDefinitionID, CaseDefinitionSummary caseDefinition, CaseInstanceSummary caseInstance) {
        caseDefinition.setId(caseDefinitionID);
        caseInstance.setCaseDefinitionId(caseDefinitionID);
    }
}