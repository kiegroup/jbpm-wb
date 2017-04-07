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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.client.util.CaseRolesAssignmentFilterBy;
import org.jbpm.workbench.cm.client.util.CaseRolesValidations;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRolesPresenterTest extends AbstractCaseInstancePresenterTest {

    private static final String USER = "User";
    private static final String GROUP = "Group";
    private static final String CASE_ROLE = "Role";
    private static final String CASE_DEFINITION_ID = "org.jbpm.case";

    @Mock
    CaseRolesPresenter.CaseRolesView view;

    @Mock
    CaseRolesValidations caseRolesValidations;

    @Mock
    CaseRolesPresenter.EditRoleAssignmentView editRoleAssignmentView;

    @InjectMocks
    CaseRolesPresenter presenter;

    @Override
    public CaseRolesPresenter getPresenter() {
        return presenter;
    }


    final String serverTemplateId = "serverTemplateId";
    final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
    CaseDefinitionSummary caseDefinition;

    @Before
    public void setUp() {
        when(view.getFilterValue()).thenReturn(CaseRolesAssignmentFilterBy.ALL.getLabel());
    }

    @Test
    public void testClearCaseInstance() {
        presenter.clearCaseInstance();

        verifyClearCaseInstance();
    }

    private void verifyClearCaseInstance() {
        verify(view).removeAllRoles();
    }

    @Test
    public void testLoadCaseInstance() {
        caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE, 3))
                .build();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(singletonList(GROUP)).users(singletonList(USER)).build()));
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(serverTemplateId, caseInstance.getContainerId(), caseInstance.getCaseDefinitionId()))
                .thenReturn(caseDefinition);

        setupCaseInstance(caseInstance, serverTemplateId);

        verifyClearCaseInstance();

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        verify(view).setRolesAssignmentList(captor.capture());

        assertEquals(1, captor.getValue().size());
        assertEquals(CASE_ROLE, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getName());
        assertEquals(USER, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getGroups().get(0));
    }

    @Test
    public void testFiltering() {
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
        caseInstance.setRoleAssignments(
                Arrays.asList(
                        createTestCaseRoleAssignmentSummary(caseRole_1, Arrays.asList(USER), Arrays.asList(GROUP)),
                        createTestCaseRoleAssignmentSummary(caseRole_2, EMPTY_LIST,EMPTY_LIST),
                        createTestCaseRoleAssignmentSummary(caseRole_3, EMPTY_LIST,Arrays.asList(GROUP))
                ));

        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(serverTemplateId, caseInstance.getContainerId(), caseInstance.getCaseDefinitionId()))
                .thenReturn(caseDefinition);
        setupCaseInstance(caseInstance, serverTemplateId);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).setRolesAssignmentList(captor.capture());
        assertEquals(3, captor.getValue().size());
        assertEquals(caseRole_1, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getName());
        assertEquals(caseRole_2, ((CaseRoleAssignmentSummary) captor.getValue().get(1)).getName());
        assertEquals(caseRole_3, ((CaseRoleAssignmentSummary) captor.getValue().get(2)).getName());
        assertEquals(USER, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getGroups().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) captor.getValue().get(2)).getGroups().get(0));

        when(view.getFilterValue()).thenReturn(CaseRolesAssignmentFilterBy.ASSIGNED.getLabel());
        presenter.filterElements();

        final ArgumentCaptor<List> captor2 = ArgumentCaptor.forClass(List.class);
        verify(view,times(2)).setRolesAssignmentList(captor2.capture());
        assertEquals(2, captor2.getValue().size());
        assertEquals(caseRole_1, ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getName());
        assertEquals(caseRole_3, ((CaseRoleAssignmentSummary) captor2.getValue().get(1)).getName());
        assertEquals(USER, ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getGroups().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) captor2.getValue().get(1)).getGroups().get(0));

        when(view.getFilterValue()).thenReturn(CaseRolesAssignmentFilterBy.UNASSIGNED.getLabel());
        presenter.filterElements();

        final ArgumentCaptor<List> captor3 = ArgumentCaptor.forClass(List.class);
        verify(view,times(3)).setRolesAssignmentList(captor3.capture());
        assertEquals(1, captor3.getValue().size());
        assertEquals(caseRole_2, ((CaseRoleAssignmentSummary) captor3.getValue().get(0)).getName());

    }

    @Test
    public void testEditAction() {
        String newUser="userTest";
        String newGroup="groupTest";
        loadTestCase();
        CaseRoleAssignmentSummary caseRoleAssignmentSummary =
                createTestCaseRoleAssignmentSummary(CASE_ROLE, new ArrayList<>(Arrays.asList(USER,newUser)),
                        new ArrayList<>(Arrays.asList(GROUP,newGroup)));

        CaseRoleAssignmentSummary previous =
                createTestCaseRoleAssignmentSummary(CASE_ROLE,singletonList(USER), singletonList(GROUP));

        when(editRoleAssignmentView.getValue()).thenReturn(caseRoleAssignmentSummary) ;

        presenter.editAction(previous);

        final ArgumentCaptor<CaseRoleAssignmentSummary> captor = ArgumentCaptor.forClass(CaseRoleAssignmentSummary.class);
        verify(editRoleAssignmentView).setValue(captor.capture());
        assertTrue(captor.getValue() != caseRoleAssignmentSummary);
        assertEquals(captor.getValue().getName(),caseRoleAssignmentSummary.getName());

        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(editRoleAssignmentView).show(commandArgumentCaptor.capture());
        Command cmd = commandArgumentCaptor.getValue();
        cmd.execute();
        verify(editRoleAssignmentView).hide();
        verify(caseManagementService).assignUserToRole(eq(serverTemplateId),eq(caseInstance.getContainerId()),
                eq(caseInstance.getCaseId()), eq(CASE_ROLE),eq(newUser));
        verify(caseManagementService).assignGroupToRole(eq(serverTemplateId),eq(caseInstance.getContainerId()),
                eq(caseInstance.getCaseId()), eq(CASE_ROLE),eq(newGroup));
    }

    @Test
    public void testSetupNewRoleAssignments_rolesNotDefined() {
        final CaseDefinitionSummary caseDefinition = CaseDefinitionSummary.builder().build();
        final CaseInstanceSummary caseInstance = newCaseInstanceSummary();
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(anyString(), anyString(), eq(CASE_DEFINITION_ID))).thenReturn(caseDefinition);

        presenter.setupExistingAssignments(caseInstance);

        verify(view,never()).setRolesAssignmentList(anyList());
    }


    @Test
    public void testAssignToRole_rolesAvailableForAssignment() {
        CaseRoleAssignmentSummary previous = createTestCaseRoleAssignmentSummary(CASE_ROLE,EMPTY_LIST,EMPTY_LIST);

        CaseRoleAssignmentSummary editedRoleAssignmentSummary =
                createTestCaseRoleAssignmentSummary(CASE_ROLE,new ArrayList<>(Arrays.asList("user1", "user2","user3")),EMPTY_LIST);
        when(editRoleAssignmentView.getValue()).thenReturn(editedRoleAssignmentSummary);
        when(caseRolesValidations.validateRolesAssignments(any(CaseDefinitionSummary.class),anyList())).thenReturn(Arrays.asList("error"));

        presenter.setupExistingAssignments(caseInstance);
        presenter.assignToRole(previous);

        verify(editRoleAssignmentView).showValidationError(anyList());
        verify(caseManagementService,never()).assignUserToRole(anyString(),anyString(),anyString(),anyString(),anyString());

        when(caseRolesValidations.validateRolesAssignments(any(CaseDefinitionSummary.class),anyList())).thenReturn(EMPTY_LIST);
        CaseRoleAssignmentSummary editedRoleAssignmentSummary2 =
                createTestCaseRoleAssignmentSummary(CASE_ROLE, new ArrayList<>(Arrays.asList("user1", "user2")),EMPTY_LIST);
        when(editRoleAssignmentView.getValue()).thenReturn(editedRoleAssignmentSummary2);
        presenter.assignToRole(previous);

        verify(caseManagementService,times(2)).assignUserToRole(anyString(),anyString(),anyString(),anyString(),anyString());
    }

    @Test
    public void testStoreRoleAssignments_noChangesInRoleAssignments() {
        CaseRoleAssignmentSummary cras =
                CaseRoleAssignmentSummary.builder()
                        .name(CASE_ROLE)
                        .users(singletonList(USER))
                        .groups(singletonList(GROUP)).build();

        presenter.storeRoleAssignments(cras,
                new ArrayList<>(Arrays.asList(USER)),
                new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(0,0,0,0);
    }

    @Test
    public void testStoreRoleAssignments_removePreviousUser() {
        CaseRoleAssignmentSummary cras =
                CaseRoleAssignmentSummary.builder()
                        .name(CASE_ROLE)
                        .users(Arrays.asList(USER,"test_user"))
                        .groups(Arrays.asList(GROUP,"test_group")).build();

        presenter.storeRoleAssignments(cras,
                new ArrayList<>(Arrays.asList(USER)),
                new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(0,0,1,1);
        verify(caseManagementService).removeUserFromRole(anyString(),anyString(),anyString(),anyString(),eq("test_user"));
        verify(caseManagementService).removeGroupFromRole(anyString(),anyString(),anyString(),anyString(),eq("test_group"));
    }

    @Test
    public void testStoreRoleAssignments_replaceAssignment() {
        CaseRoleAssignmentSummary cras =
                createTestCaseRoleAssignmentSummary(CASE_ROLE,singletonList("test_user"),singletonList("test_group"));

        presenter.storeRoleAssignments(cras,
                new ArrayList<>(Arrays.asList(USER)),
                new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(1,1,1,1);

        verify(caseManagementService).assignUserToRole(anyString(),anyString(),anyString(),anyString(),eq(USER));
        verify(caseManagementService).assignGroupToRole(anyString(),anyString(),anyString(),anyString(),eq(GROUP));
        verify(caseManagementService).removeUserFromRole(anyString(),anyString(),anyString(),anyString(),eq("test_user"));
        verify(caseManagementService).removeGroupFromRole(anyString(),anyString(),anyString(),anyString(),eq("test_group"));
    }

    private void verifyStoreRoleAssignmentsCalls(int timesAddUser, int timesAddGroup, int timesRemoveUser, int timesRemoveGroup) {
        verify(caseManagementService, times(timesAddUser)).assignUserToRole(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(caseManagementService, times(timesAddGroup)).assignGroupToRole(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(caseManagementService, times(timesRemoveUser)).removeUserFromRole(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(caseManagementService, times(timesRemoveGroup)).removeGroupFromRole(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private void setCaseDefinitionID(String caseDefinitionID, CaseDefinitionSummary caseDefinition, CaseInstanceSummary caseInstance) {
        caseDefinition.setId(caseDefinitionID);
        caseInstance.setCaseDefinitionId(caseDefinitionID);
    }

    private CaseRoleAssignmentSummary createTestCaseRoleAssignmentSummary(String roleName, List<String > users, List<String> groups){
        return   CaseRoleAssignmentSummary.builder()
                        .name(roleName)
                        .users(users)
                        .groups(groups).build();
    }

    private void loadTestCase(){
        caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE, 3))
                .build();
        setCaseDefinitionID(CASE_DEFINITION_ID, caseDefinition, caseInstance);
        when(caseManagementService.getCaseDefinition(serverTemplateId, caseInstance.getContainerId(), caseInstance.getCaseDefinitionId()))
                .thenReturn(caseDefinition);

        setupCaseInstance(caseInstance, serverTemplateId);

    }

}