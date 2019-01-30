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

import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
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
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRolesPresenterTest extends AbstractCaseInstancePresenterTest {

    private static final String USER = "User";
    private static final String GROUP = "Group";
    private static final String CASE_ROLE = "Role";
    private static final String CASE_DEFINITION_ID = "org.jbpm.case";

    final CaseInstanceSummary caseInstance = newCaseInstanceSummary();

    @Mock
    CaseRolesPresenter.CaseRolesView view;

    @Mock
    CaseRolesValidations caseRolesValidations;

    @Mock
    CaseRolesPresenter.EditRoleAssignmentView editRoleAssignmentView;

    @Spy
    @InjectMocks
    CaseRolesPresenter presenter;

    CaseDefinitionSummary caseDefinition;

    private CaseRoleItemView caseRole;


    @Override
    public CaseRolesPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void setUp() {
        caseRole = mock(CaseRoleItemView.class);
    }

    @Test
    public void testClearCaseInstance() {
        List<CaseRoleAssignmentSummary> caseRoleAssignmentSummaryList = new ArrayList<>();
        caseRoleAssignmentSummaryList.add(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).build());
        presenter.setCaseRolesAssignments(caseRoleAssignmentSummaryList);
        assertEquals(1, presenter.getCaseRolesAssignments().size());

        presenter.clearCaseInstance();

        assertEquals(0, presenter.getCaseRolesAssignments().size());
        verify(view).removeAllRoles();
        verify(view).setBadge(0);

    }

    @Test
    public void testLoadCaseInstance_nonExistentCaseInstance() {
        presenter.loadCaseInstance(null);

        assertEquals(0, presenter.getCaseRolesAssignments().size());
        verifyZeroInteractions(view);
        verifyZeroInteractions(caseManagementService);
    }

    @Test
    public void testLoadCaseInstance_caseInstanceWithoutAssignments() {
        presenter.loadCaseInstance(caseInstance);

        assertEquals(0, presenter.getCaseRolesAssignments().size());
        verifyZeroInteractions(view);
        verifyZeroInteractions(caseManagementService);
    }

    @Test
    public void testLoadCaseInstance_caseInstanceWithoutCaseDefinitionId() {
        caseInstance.setCaseDefinitionId(null);
        caseInstance.setRoleAssignments(singletonList(CaseRoleAssignmentSummary.builder()
                                                                               .name(CASE_ROLE)
                                                                               .groups(singletonList(GROUP))
                                                                               .users(singletonList(USER))
                                                                               .build()));
        presenter.loadCaseInstance(caseInstance);

        assertEquals(0, presenter.getCaseRolesAssignments().size());
        verifyZeroInteractions(view);
        verifyZeroInteractions(caseManagementService);
    }

    @Test
    public void testLoadCaseInstance_nonExistentCaseDefinition() {
        caseInstance.setRoleAssignments(singletonList(CaseRoleAssignmentSummary.builder()
                                                                               .name(CASE_ROLE)
                                                                               .groups(singletonList(GROUP))
                                                                               .users(singletonList(USER))
                                                                               .build()));
        when(caseManagementService.getCaseDefinition(anyString(), anyString())).thenReturn(null);
        presenter.loadCaseInstance(caseInstance);

        assertEquals(0, presenter.getCaseRolesAssignments().size());
        verifyZeroInteractions(view);

    }

    @Test
    public void testLoadCaseInstance() {
        caseDefinition = CaseDefinitionSummary.builder()
                .roles(singletonMap(CASE_ROLE,
                                    3))
                .build();
        caseInstance.setRoleAssignments
                (singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).groups(singletonList(GROUP)).users(singletonList(USER)).build()));
        setCaseDefinitionID(CASE_DEFINITION_ID,
                            caseDefinition,
                            caseInstance);
        when(caseManagementService.getCaseDefinition(caseInstance.getContainerId(), caseInstance.getCaseDefinitionId())).thenReturn(caseDefinition);
        when(view.getFilterValue()).thenReturn("All");

        setupCaseInstance(caseInstance);

        verify(view).removeAllRoles();
        verify(view).setBadge(0);

        ArgumentCaptor<List> allRoleAssignments = ArgumentCaptor.forClass(List.class);
        verify(presenter).setCaseRolesAssignments(allRoleAssignments.capture());
        verify(view).setBadge(caseInstance.getRoleAssignments().size());
        verify(presenter).filterCaseRoles();

        ArgumentCaptor<List> displayedRoleAssignments = ArgumentCaptor.forClass(List.class);
        verify(view).displayCaseRolesList(displayedRoleAssignments.capture());

        assertEquals(caseInstance.getRoleAssignments().size(), allRoleAssignments.getValue().size());
        assertEquals(allRoleAssignments.getValue().size(), displayedRoleAssignments.getValue().size());
        assertEquals(CASE_ROLE, ((CaseRoleAssignmentSummary) displayedRoleAssignments.getValue().get(0)).getName());
        assertEquals(USER, ((CaseRoleAssignmentSummary) displayedRoleAssignments.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP, ((CaseRoleAssignmentSummary) displayedRoleAssignments.getValue().get(0)).getGroups().get(0));
    }

    private void setCaseDefinitionID(String caseDefinitionID,
                                     CaseDefinitionSummary caseDefinition,
                                     CaseInstanceSummary caseInstance) {
        caseDefinition.setId(caseDefinitionID);
        caseInstance.setCaseDefinitionId(caseDefinitionID);
    }

    @Test
    public void testFilterCaseRoles_withoutResult() {
        caseInstance.setRoleAssignments(singletonList(CaseRoleAssignmentSummary.builder().name(CASE_ROLE).build()));
        presenter.setCaseRolesAssignments(caseInstance.getRoleAssignments());
        when(view.getFilterValue()).thenReturn("Assigned");

        presenter.filterCaseRoles();

        verify(view).displayEmptyList();
    }

    @Test
    public void testFilterCaseRoles_withResult() {
        final String caseRole_1 = "Role_1";
        final String caseRole_2 = "Role_2";
        final String caseRole_3 = "Role_3";

        caseInstance.setRoleAssignments(
                Arrays.asList(
                        createTestCaseRoleAssignmentSummary(caseRole_1,
                                                            singletonList(USER),
                                                            singletonList(GROUP)),
                        createTestCaseRoleAssignmentSummary(caseRole_2,
                                                            EMPTY_LIST,
                                                            EMPTY_LIST),
                        createTestCaseRoleAssignmentSummary(caseRole_3,
                                                            EMPTY_LIST,
                                                            singletonList(GROUP))
                ));
        presenter.setCaseRolesAssignments(caseInstance.getRoleAssignments());
        when(view.getFilterValue()).thenReturn("All");

        presenter.filterCaseRoles();

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(view).displayCaseRolesList(captor.capture());
        assertEquals(3,
                     captor.getValue().size());
        assertEquals(caseRole_1,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getName());
        assertEquals(caseRole_2,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(1)).getName());
        assertEquals(caseRole_3,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(2)).getName());
        assertEquals(USER,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(0)).getGroups().get(0));
        assertEquals(GROUP,
                     ((CaseRoleAssignmentSummary) captor.getValue().get(2)).getGroups().get(0));

        when(view.getFilterValue()).thenReturn("Assigned");

        presenter.filterCaseRoles();

        final ArgumentCaptor<List> captor2 = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(2)).displayCaseRolesList(captor2.capture());
        assertEquals(2,
                     captor2.getValue().size());
        assertEquals(caseRole_1,
                     ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getName());
        assertEquals(caseRole_3,
                     ((CaseRoleAssignmentSummary) captor2.getValue().get(1)).getName());
        assertEquals(USER,
                     ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getUsers().get(0));
        assertEquals(GROUP,
                     ((CaseRoleAssignmentSummary) captor2.getValue().get(0)).getGroups().get(0));
        assertEquals(GROUP,
                     ((CaseRoleAssignmentSummary) captor2.getValue().get(1)).getGroups().get(0));

        when(view.getFilterValue()).thenReturn("Unassigned");

        presenter.filterCaseRoles();

        final ArgumentCaptor<List> captor3 = ArgumentCaptor.forClass(List.class);
        verify(view,
               times(3)).displayCaseRolesList(captor3.capture());
        assertEquals(1,
                     captor3.getValue().size());
        assertEquals(caseRole_2,
                     ((CaseRoleAssignmentSummary) captor3.getValue().get(0)).getName());
    }

    private CaseRoleAssignmentSummary createTestCaseRoleAssignmentSummary(String roleName,
                                                                          List<String> users,
                                                                          List<String> groups) {
        return CaseRoleAssignmentSummary.builder()
                .name(roleName)
                .users(users)
                .groups(groups).build();
    }

    @Test
    public void testSetCaseRoleActions_ownerRole() {
        when(caseRole.getValue()).thenReturn(CaseRoleAssignmentSummary.builder()
                                                                      .name(CaseRolesPresenter.CASE_OWNER_ROLE)
                                                                      .users(singletonList(USER))
                                                                      .build());
        presenter.setCaseRoleActions(caseRole);
        verify(caseRole).displayOwnerActions();
    }

    @Test
    public void testSetCaseRoleActions_roleWithExistingAssignments() {
        when(caseRole.getValue()).thenReturn(CaseRoleAssignmentSummary.builder()
                                                                      .name(CASE_ROLE)
                                                                      .users(singletonList(USER))
                                                                      .build());
        presenter.setCaseRoleActions(caseRole);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> caseRoleAction = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(presenter,
               times(2)).addCaseRoleAction(eq(caseRole),
                                           caseRoleAction.capture());
        CaseRolesPresenter.CaseRoleAction editAction = caseRoleAction.getAllValues().get(0);
        assertEquals(EDIT,
                     editAction.label());
        assertTrue(editAction.isEnabled());
        editAction.execute();
        verify(caseRole).showEditRoleAssignmentDialog();

        CaseRolesPresenter.CaseRoleAction removeAllAssignmentsAction = caseRoleAction.getAllValues().get(1);
        assertEquals(REMOVE_ALL_ASSIGNMENTS,
                     removeAllAssignmentsAction.label());
        assertTrue(removeAllAssignmentsAction.isEnabled());
        removeAllAssignmentsAction.execute();
        verify(caseRole).showRemoveAllAssignmentsPopup();

        verify(caseRole,
               never()).displayOwnerActions();
        verify(caseRole,
               times(2)).displayEnabledAction(any(CaseRolesPresenter.CaseRoleAction.class));
    }

    @Test
    public void testSetCaseRoleActions_roleWithoutAssignments() {
        when(caseRole.getValue()).thenReturn(CaseRoleAssignmentSummary.builder()
                                                                      .name(CASE_ROLE)
                                                                      .build());
        presenter.setCaseRoleActions(caseRole);

        final ArgumentCaptor<CaseRolesPresenter.CaseRoleAction> caseRoleAction = ArgumentCaptor.forClass(CaseRolesPresenter.CaseRoleAction.class);
        verify(presenter,
               times(2)).addCaseRoleAction(eq(caseRole),
                                           caseRoleAction.capture());
        CaseRolesPresenter.CaseRoleAction assignAction = caseRoleAction.getAllValues().get(0);
        assertEquals(ASSIGN,
                     assignAction.label());
        assertTrue(assignAction.isEnabled());
        assignAction.execute();
        verify(caseRole).showEditRoleAssignmentDialog();

        CaseRolesPresenter.CaseRoleAction removeAllAssignmentsAction = caseRoleAction.getAllValues().get(1);
        assertEquals(REMOVE_ALL_ASSIGNMENTS,
                     removeAllAssignmentsAction.label());
        assertFalse(removeAllAssignmentsAction.isEnabled());
        removeAllAssignmentsAction.execute();
        verify(caseRole).showRemoveAllAssignmentsPopup();

        verify(caseRole,
                never()).displayOwnerActions();
        verify(caseRole).displayEnabledAction(any(CaseRolesPresenter.CaseRoleAction.class));
        verify(caseRole).displayDisabledAction(any(CaseRolesPresenter.CaseRoleAction.class));
    }

    @Test
    public void testSetCaseRoleAssignments_ownerRole() {
        final int assignmentsListMaxWidth = 70;
        when(caseRole.getValue()).thenReturn(CaseRoleAssignmentSummary.builder()
                                                                      .name(CaseRolesPresenter.CASE_OWNER_ROLE)
                                                                      .users(singletonList(USER))
                                                                      .build());
        presenter.setCaseRoleAssignments(caseRole,
                                         assignmentsListMaxWidth);
        verify(caseRole).displayOwnerAssignment();
    }

    @Test
    public void testSetCaseRoleAssignments_roleWithoutAssignments() {
        final int assignmentsListMaxWidth = 70;
        when(caseRole.getValue()).thenReturn(CaseRoleAssignmentSummary.builder()
                                                                      .name(CASE_ROLE)
                                                                      .build());
        presenter.setCaseRoleAssignments(caseRole,
                                         assignmentsListMaxWidth);

        verify(caseRole).displayUnassigned();
    }


    @Test
    public void testSetCaseRoleAssignments_roleWithUserAssignmentsOnly() {
        final int assignmentsListMaxWidth = 70;
        final CaseRoleAssignmentSummary caseRoleAssignmentSummary = CaseRoleAssignmentSummary.builder()
                                                                                             .name(CASE_ROLE)
                                                                                             .users(singletonList(USER))
                                                                                             .build();
        when(caseRole.getValue()).thenReturn(caseRoleAssignmentSummary);

        presenter.setCaseRoleAssignments(caseRole,
                                         assignmentsListMaxWidth);

        verify(caseRole,
               never()).hideUserAssignments();
        verify(caseRole).hideGroupAssignments();

        final ArgumentCaptor<List> itemsList = ArgumentCaptor.forClass(List.class);
        verify(caseRole).displayAssignmentsList(eq(CaseRoleItemView.USERS_LINE_ID),
                                                eq(assignmentsListMaxWidth),
                                                itemsList.capture());
        assertEquals(caseRoleAssignmentSummary.getUsers().size(),
                     itemsList.getValue().size());
        CaseRolesPresenter.CaseAssignmentItem assignmentItem = (CaseRolesPresenter.CaseAssignmentItem)itemsList.getValue().get(0);
        final String assignedUser = caseRoleAssignmentSummary.getUsers().get(0);
        assertEquals(assignedUser,
                     assignmentItem.label());
        assignmentItem.execute();
        verify(caseRole).showRemoveUserAssignmentPopup(assignedUser);
    }

    @Test
    public void testSetCaseRoleAssignments_roleWithGroupAssignmentsOnly() {
        final int assignmentsListMaxWidth = 70;
        final CaseRoleAssignmentSummary caseRoleAssignmentSummary = CaseRoleAssignmentSummary.builder()
                                                                                             .name(CASE_ROLE)
                                                                                             .groups(singletonList(GROUP))
                                                                                             .build();
        when(caseRole.getValue()).thenReturn(caseRoleAssignmentSummary);

        presenter.setCaseRoleAssignments(caseRole,
                                         assignmentsListMaxWidth);

        verify(caseRole).hideUserAssignments();
        verify(caseRole,
               never()).hideGroupAssignments();

        final ArgumentCaptor<List> itemsList = ArgumentCaptor.forClass(List.class);
        verify(caseRole).displayAssignmentsList(eq(CaseRoleItemView.GROUP_LINE_ID),
                                                eq(assignmentsListMaxWidth),
                                                itemsList.capture());
        assertEquals(caseRoleAssignmentSummary.getGroups().size(),
                     itemsList.getValue().size());
        CaseRolesPresenter.CaseAssignmentItem assignmentItem = (CaseRolesPresenter.CaseAssignmentItem)itemsList.getValue().get(0);
        final String assignedGroup = caseRoleAssignmentSummary.getGroups().get(0);
        assertEquals(assignedGroup,
                     assignmentItem.label());
        assignmentItem.execute();
        verify(caseRole).showRemoveGroupAssignmentPopup(assignedGroup);
    }

    @Test
    public void testSetCaseRoleAssignments_roleWithUserAndGroupAssignments() {
        final int assignmentsListMaxWidth = 70;
        final CaseRoleAssignmentSummary caseRoleAssignmentSummary = CaseRoleAssignmentSummary.builder()
                                                                                             .name(CASE_ROLE)
                                                                                             .users(singletonList(USER))
                                                                                             .groups(singletonList(GROUP))
                                                                                             .build();
        when(caseRole.getValue()).thenReturn(caseRoleAssignmentSummary);

        presenter.setCaseRoleAssignments(caseRole,
                                         assignmentsListMaxWidth);

        verify(caseRole,
               never()).hideUserAssignments();
        verify(caseRole,
                never()).hideGroupAssignments();
        verify(caseRole,
               times(2)).displayAssignmentsList(anyString(),
                                                eq(assignmentsListMaxWidth),
                                                anyListOf(CaseRolesPresenter.CaseAssignmentItem.class));
    }

    @Test
    public void testAssignToRole_noAssignmentsProvided() {
        final CaseRoleAssignmentSummary newRoleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                      .name(CASE_ROLE)
                                                                                      .build();
        presenter.assignToRole(caseRole,
                               newRoleAssignments);

        verify(caseRole).showErrorState();
        final ArgumentCaptor<List> errorList = ArgumentCaptor.forClass(List.class);
        verify(caseRole).showAssignmentErrors(errorList.capture());
        final int errorsNumber = 1;
        assertEquals(errorsNumber,
                     errorList.getValue().size());
        assertEquals(PLEASE_INTRO_USER_OR_GROUP_TO_CREATE_ASSIGNMENT,
                     errorList.getValue().get(0));
    }

    @Test
    public void testAssignToRole_providedAssignmentsInvalid() {
        final CaseRoleAssignmentSummary newRoleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                      .name(CASE_ROLE)
                                                                                      .users(singletonList(USER))
                                                                                      .build();
        final String error = "testError";
        when(caseRolesValidations.validateRolesAssignments(caseDefinition,
                                                           singletonList(newRoleAssignments))).thenReturn(singletonList(error));

        presenter.assignToRole(caseRole,
                               newRoleAssignments);

        final ArgumentCaptor<List> caseRoleAssignmentErrors = ArgumentCaptor.forClass(List.class);
        verify(caseRole).showAssignmentErrors(caseRoleAssignmentErrors.capture());
        final int errorsNumber = 1;
        assertEquals(errorsNumber,
                     caseRoleAssignmentErrors.getValue().size());
        assertEquals(error,
                     caseRoleAssignmentErrors.getValue().get(0));
        verify(caseRole,
               never()).hideEditRoleAssignmentDialog();
    }

    @Test
    public void testAssignToRole_providedAssignmentsCorrect() {
        final CaseRoleAssignmentSummary currentRoleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                          .name(CASE_ROLE)
                                                                                          .build();
        final CaseRoleAssignmentSummary newRoleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                      .name(CASE_ROLE)
                                                                                      .users(singletonList(USER))
                                                                                      .build();
        when(caseRolesValidations.validateRolesAssignments(caseDefinition,
                                                           singletonList(newRoleAssignments))).thenReturn(emptyList());
        when(caseRole.getValue()).thenReturn(currentRoleAssignments);
        when(view.getFilterValue()).thenReturn("All");

        presenter.assignToRole(caseRole,
                               newRoleAssignments);

        verify(caseRole).hideEditRoleAssignmentDialog();
        verify(presenter).storeRoleAssignments(any(CaseRoleAssignmentSummary.class),
                                               anyListOf(String.class),
                                               anyListOf(String.class));
    }

    @Test
    public void testStoreRoleAssignments_noChangesInRoleAssignments() {
        CaseRoleAssignmentSummary cras =
                CaseRoleAssignmentSummary.builder()
                        .name(CASE_ROLE)
                        .users(singletonList(USER))
                        .groups(singletonList(GROUP)).build();
        when(view.getFilterValue()).thenReturn("All");

        presenter.storeRoleAssignments(cras,
                                       new ArrayList<>(Arrays.asList(USER)),
                                       new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(0,
                                        0,
                                        0,
                                        0);
    }

    @Test
    public void testStoreRoleAssignments_removePreviousUser() {
        CaseRoleAssignmentSummary cras =
                CaseRoleAssignmentSummary.builder()
                        .name(CASE_ROLE)
                        .users(Arrays.asList(USER,
                                             "test_user"))
                        .groups(Arrays.asList(GROUP,
                                              "test_group")).build();
        when(view.getFilterValue()).thenReturn("All");

        presenter.storeRoleAssignments(cras, new ArrayList<>(Arrays.asList(USER)), new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(0,
                                        0,
                                        1,
                                        1);
        verify(caseManagementService).removeUserFromRole(anyString(),
                                                         anyString(),
                                                         anyString(),
                                                         eq("test_user"));
        verify(caseManagementService).removeGroupFromRole(anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          eq("test_group"));
    }

    @Test
    public void testStoreRoleAssignments_replaceAssignment() {
        CaseRoleAssignmentSummary cras =
                createTestCaseRoleAssignmentSummary(CASE_ROLE,
                                                    singletonList("test_user"),
                                                    singletonList("test_group"));
        when(view.getFilterValue()).thenReturn("All");

        presenter.storeRoleAssignments(cras,
                                       new ArrayList<>(Arrays.asList(USER)),
                                       new ArrayList<>(Arrays.asList(GROUP)));

        verifyStoreRoleAssignmentsCalls(1,
                                        1,
                                        1,
                                        1);

        verify(caseManagementService).assignUserToRole(anyString(),
                                                       anyString(),
                                                       anyString(),
                                                       eq(USER));
        verify(caseManagementService).assignGroupToRole(anyString(),
                                                        anyString(),
                                                        anyString(),
                                                        eq(GROUP));
        verify(caseManagementService).removeUserFromRole(anyString(),
                                                         anyString(),
                                                         anyString(),
                                                         eq("test_user"));
        verify(caseManagementService).removeGroupFromRole(anyString(),
                                                          anyString(),
                                                          anyString(),
                                                          eq("test_group"));
    }

    private void verifyStoreRoleAssignmentsCalls(int timesAddUser,
                                                 int timesAddGroup,
                                                 int timesRemoveUser,
                                                 int timesRemoveGroup) {
        verify(caseManagementService, times(timesAddUser)).assignUserToRole(anyString(),
                                                                            anyString(),
                                                                            anyString(),
                                                                            anyString());
        verify(caseManagementService, times(timesAddGroup)).assignGroupToRole(anyString(),
                                                                              anyString(),
                                                                              anyString(),
                                                                              anyString());
        verify(caseManagementService, times(timesRemoveUser)).removeUserFromRole(anyString(),
                                                                                 anyString(),
                                                                                 anyString(),
                                                                                 anyString());
        verify(caseManagementService, times(timesRemoveGroup)).removeGroupFromRole(anyString(),
                                                                                   anyString(),
                                                                                   anyString(),
                                                                                   anyString());
    }

    @Test
    public void testRemoveUserFromRole() {
        List<String> assignedUsersList = new ArrayList<>();
        assignedUsersList.add(USER);
        final CaseRoleAssignmentSummary roleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                      .name(CASE_ROLE)
                                                                                      .users(assignedUsersList)
                                                                                      .build();
        when(view.getFilterValue()).thenReturn("All");

        presenter.removeUserFromRole(USER, roleAssignments);

        verify(caseManagementService).removeUserFromRole(anyString(),
                                                         anyString(),
                                                         eq(CASE_ROLE),
                                                         eq(USER));
        assertTrue(roleAssignments.getUsers().isEmpty());
        verify(presenter).filterCaseRoles();
    }

    @Test
    public void testRemoveGroupFromRole() {
        List<String> assignedGroupsList = new ArrayList<>();
        assignedGroupsList.add(GROUP);
        final CaseRoleAssignmentSummary roleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                   .name(CASE_ROLE)
                                                                                   .groups(assignedGroupsList)
                                                                                   .build();
        when(view.getFilterValue()).thenReturn("All");

        presenter.removeGroupFromRole(GROUP, roleAssignments);

        verify(caseManagementService).removeGroupFromRole(anyString(),
                                                          anyString(),
                                                          eq(CASE_ROLE),
                                                          eq(GROUP));
        assertTrue(roleAssignments.getGroups().isEmpty());
        verify(presenter).filterCaseRoles();
    }
}