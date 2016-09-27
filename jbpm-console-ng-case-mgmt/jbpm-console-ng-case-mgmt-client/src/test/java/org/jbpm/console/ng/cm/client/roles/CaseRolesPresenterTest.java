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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.console.ng.cm.client.AbstractCaseInstancePresenterTest;
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
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final String roleName = "role";
        final String groupName = "group";
        final String userName = "user";
        final CaseRoleAssignmentSummary cras = new CaseRoleAssignmentSummary(roleName, singletonList(groupName), singletonList(userName));
        cis.setRoleAssignments(singletonList(cras));
        final CaseDefinitionSummary cds = new CaseDefinitionSummary();
        cds.setRoles(singletonMap(roleName, 3));
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
    public void testSetupRoleAssignmentsEmpty(){
        presenter.setupRoleAssignments(new CaseInstanceSummary());

        verify(caseRolesView, never()).addUser(anyString(), anyString());
        verify(caseRolesView, never()).addGroup(anyString(), anyString());
        verifyNoMoreInteractions(caseRolesView);
    }

    @Test
    public void testSetupRoleAssignmentsUser(){
        final String roleName = "role";
        final String userName = "user";
        final CaseInstanceSummary cis = new CaseInstanceSummary();
        final CaseRoleAssignmentSummary cras = new CaseRoleAssignmentSummary(roleName, emptyList(), singletonList(userName));
        cis.setRoleAssignments(singletonList(cras));

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
    public void testSetupRoleAssignmentsGroup(){
        final String roleName = "role";
        final String groupName = "group";
        final CaseInstanceSummary cis = new CaseInstanceSummary();
        final CaseRoleAssignmentSummary cras = new CaseRoleAssignmentSummary(roleName, singletonList(groupName), emptyList());
        cis.setRoleAssignments(singletonList(cras));

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
    public void testSetupNewRoleAssignmentsEmpty(){
        final String caseDefinitionId = "org.jbpm.case";
        final CaseDefinitionSummary cds = new CaseDefinitionSummary();
        cds.setRoles(emptyMap());
        cds.setCaseDefinitionId(caseDefinitionId);
        final CaseInstanceSummary cis = new CaseInstanceSummary();
        cis.setCaseDefinitionId(cds.getCaseDefinitionId());
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(caseDefinitionId))).thenReturn(cds);

        presenter.setupNewRoleAssignments(cis);

        verify(caseRolesView, never()).enableNewRoleAssignments();
        verifyNoMoreInteractions(caseRolesView);
    }

    @Test
    public void testSetupNewRoleAssignments(){
        final String caseDefinitionId = "org.jbpm.case";
        final String roleName = "role";
        final CaseDefinitionSummary cds = new CaseDefinitionSummary();
        cds.setCaseDefinitionId(caseDefinitionId);
        cds.setRoles(singletonMap(roleName, 1));
        final CaseInstanceSummary cis = new CaseInstanceSummary();
        cis.setCaseDefinitionId(cds.getCaseDefinitionId());
        cis.setRoleAssignments(emptyList());
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
    public void testGetRolesAvailableForAssignment(){
        final String roleUnlimited = "roleUnlimited";
        final String roleFull = "roleFull";
        final String roleAvailable = "roleAvailable";
        final CaseDefinitionSummary cds = new CaseDefinitionSummary();
        final Map<String, Integer> roles = new HashMap<>();
        roles.put(roleUnlimited, -1);
        roles.put(roleFull, 2);
        roles.put(roleAvailable, 2);
        cds.setRoles(roles);
        final CaseInstanceSummary cis = new CaseInstanceSummary();
        cis.setCaseDefinitionId(cds.getCaseDefinitionId());
        final List<CaseRoleAssignmentSummary> roleAssignments = new ArrayList<>();
        roleAssignments.add(new CaseRoleAssignmentSummary(roleUnlimited, singletonList("group"), singletonList("user")));
        roleAssignments.add(new CaseRoleAssignmentSummary(roleFull, singletonList("group"), singletonList("user")));
        roleAssignments.add(new CaseRoleAssignmentSummary(roleAvailable, emptyList(), singletonList("user")));
        cis.setRoleAssignments(roleAssignments);

        final Set<String> availableRoles = presenter.getRolesAvailableForAssignment(cis, cds);

        assertTrue(availableRoles.contains(roleUnlimited));
        assertFalse(availableRoles.contains(roleFull));
        assertTrue(availableRoles.contains(roleAvailable));
    }

}