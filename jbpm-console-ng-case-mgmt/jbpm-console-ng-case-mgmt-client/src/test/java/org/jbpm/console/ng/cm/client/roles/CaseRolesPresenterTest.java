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

package org.jbpm.console.ng.cm.client.roles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.model.CaseRoleAssignmentSummary;
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


@RunWith(MockitoJUnitRunner.class)
public class CaseRolesPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock
    CaseRolesPresenter.CaseRolesView caseRolesView;

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
        verify(caseRolesView).removeAllRoles();
        verify(caseRolesView).disableNewRoleAssignments();
    }

    @Test
    public void testLoadCaseInstance() {
        final String serverTemplateId = "serverTemplateId";
        final String roleName = "role";
        final String groupName = "group";
        final String userName = "user";
        final CaseRoleAssignmentSummary cras = CaseRoleAssignmentSummary.builder().name(roleName).groups(singletonList(groupName)).users(singletonList(userName)).build();
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().roleAssignments(singletonList(cras)).build();
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(singletonMap(roleName, 3)).build();
        when(caseManagementService.getCaseDefinition(serverTemplateId, cis.getContainerId(), cis.getCaseDefinitionId())).thenReturn(cds);

        setupCaseInstance(cis, serverTemplateId);

        verifyClearCaseInstance();
        verify(caseRolesView).addUser(cis.getOwner(), "Owner");

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(caseRolesView).addUser(eq(userName), eq(roleName), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(caseRolesView).addGroup(eq(groupName), eq(roleName), captor.capture());
        assertEquals("Remove", captor.getValue().label());

        verify(caseRolesView).enableNewRoleAssignments();
        verify(caseRolesView).setUserAddCommand(any(Command.class));
        verify(caseRolesView).setGroupAddCommand(any(Command.class));
    }

    @Test
    public void testSetupRoleAssignmentsEmpty() {
        presenter.setupRoleAssignments(CaseInstanceSummary.builder().build());

        verify(caseRolesView, never()).addUser(anyString(), anyString());
        verify(caseRolesView, never()).addGroup(anyString(), anyString());
        verifyNoMoreInteractions(caseRolesView);
    }

    @Test
    public void testSetupRoleAssignmentsUser() {
        final String roleName = "role";
        final String userName = "user";
        final CaseRoleAssignmentSummary cras = CaseRoleAssignmentSummary.builder().name(roleName).groups(emptyList()).users(singletonList(userName)).build();
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().roleAssignments(singletonList(cras)).build();

        presenter.setupRoleAssignments(cis);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(caseRolesView).addUser(eq(userName), eq(roleName), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(caseRolesView, never()).addGroup(anyString(), anyString());
        verifyNoMoreInteractions(caseRolesView);

        captor.getValue().execute();
        verify(caseManagementService).removeUserFromRole(anyString(), anyString(), anyString(), eq(roleName), eq(userName));
    }

    @Test
    public void testSetupRoleAssignmentsGroup() {
        final String roleName = "role";
        final String groupName = "group";

        final CaseRoleAssignmentSummary cras = CaseRoleAssignmentSummary.builder().name(roleName).groups(singletonList(groupName)).users(emptyList()).build();
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().roleAssignments(singletonList(cras)).build();

        presenter.setupRoleAssignments(cis);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> captor = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(caseRolesView).addGroup(eq(groupName), eq(roleName), captor.capture());
        assertEquals("Remove", captor.getValue().label());
        verify(caseRolesView, never()).addUser(anyString(), anyString());
        verifyNoMoreInteractions(caseRolesView);

        captor.getValue().execute();
        verify(caseManagementService).removeGroupFromRole(anyString(), anyString(), anyString(), eq(roleName), eq(groupName));
    }

    @Test
    public void testSetupNewRoleAssignmentsEmpty() {
        final String caseDefinitionId = "org.jbpm.case";
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefinitionId).roles(emptyMap()).build();
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().caseDefinitionId(cds.getId()).build();
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(caseDefinitionId))).thenReturn(cds);

        presenter.setupNewRoleAssignments(cis);

        verify(caseRolesView, never()).enableNewRoleAssignments();
        verifyNoMoreInteractions(caseRolesView);
    }

    @Test
    public void testSetupNewRoleAssignments() {
        final String caseDefinitionId = "org.jbpm.case";
        final String roleName = "role";
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefinitionId).roles(singletonMap(roleName, 1)).build();
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().caseDefinitionId(cds.getId()).roleAssignments(emptyList()).build();
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(caseDefinitionId))).thenReturn(cds);

        presenter.setupNewRoleAssignments(cis);

        verify(caseRolesView).enableNewRoleAssignments();
        final ArgumentCaptor<Command> captor = ArgumentCaptor.forClass(Command.class);
        verify(caseRolesView).setUserAddCommand(captor.capture());
        captor.getValue().execute();
        final ArgumentCaptor<Command> okCommandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(assignmentView).show(eq(true), eq(singleton(roleName)), okCommandCaptor.capture());
        okCommandCaptor.getValue().execute();
        verify(caseManagementService).assignUserToRole(anyString(), anyString(), anyString(), anyString(), anyString());

        verify(caseRolesView).setGroupAddCommand(captor.capture());
        captor.getValue().execute();
        verify(assignmentView).show(eq(false), eq(singleton(roleName)), okCommandCaptor.capture());
        okCommandCaptor.getValue().execute();
        verify(caseManagementService).assignGroupToRole(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testGetRolesAvailableForAssignment() {
        final String caseDefinitionId = "org.jbpm.case";
        final String roleUnlimited = "roleUnlimited";
        final String roleFull = "roleFull";
        final String roleAvailable = "roleAvailable";
        final Map<String, Integer> roles = new HashMap<>();
        roles.put(roleUnlimited, -1);
        roles.put(roleFull, 2);
        roles.put(roleAvailable, 2);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().id(caseDefinitionId).roles(roles).build();
        final List<CaseRoleAssignmentSummary> roleAssignments = Arrays.asList(
                CaseRoleAssignmentSummary.builder().name(roleUnlimited).groups(singletonList("group")).users(singletonList("user")).build(),
                CaseRoleAssignmentSummary.builder().name(roleFull).groups(singletonList("group")).users(singletonList("user")).build(),
                CaseRoleAssignmentSummary.builder().name(roleAvailable).groups(emptyList()).users(singletonList("user")).build()
        );
        final CaseInstanceSummary cis = CaseInstanceSummary.builder().caseDefinitionId(cds.getId()).roleAssignments(roleAssignments).build();

        final Set<String> availableRoles = presenter.getRolesAvailableForAssignment(cis, cds);

        assertTrue(availableRoles.contains(roleUnlimited));
        assertFalse(availableRoles.contains(roleFull));
        assertTrue(availableRoles.contains(roleAvailable));
    }

}