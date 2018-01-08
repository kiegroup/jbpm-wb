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

package org.jbpm.workbench.cm.client.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRolesValidationsTest {

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private CaseRolesValidations caseRolesValidations;

    @Test
    public void testValidateRolesAssignments_roleCardinalityReached() {
        final Map<String, Integer> role = Collections.singletonMap("test",
                                                                   1);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").users(singletonList("user1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        verify(translationService,
               never()).format(anyString(),
                               anyString(),
                               anyInt());
        assertThat(errors).isEmpty();
    }

    @Test
    public void testValidateRolesAssignments_roleCardinalityExceeded() {
        final String caseRole = "test";
        final Integer caseRoleCardinality = 1;
        final Map<String, Integer> role = Collections.singletonMap(caseRole,
                                                                   caseRoleCardinality);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").users(singletonList("user1")).groups(singletonList("group1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        verify(translationService).format(eq("InvalidRoleAssignment"),
                                          eq(caseRole),
                                          eq(caseRoleCardinality));
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testValidateRolesAssignments_multipleRolesCardinalityExceeded() {
        final Map<String, Integer> roles = new HashMap<>();
        final Integer roleCardinality = 1;
        roles.put("caseRole1",
                  roleCardinality);
        roles.put("caseRole2",
                  roleCardinality);

        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(roles).build();

        final List<CaseRoleAssignmentSummary> roleAssignments = new ArrayList<>();
        roleAssignments.add(CaseRoleAssignmentSummary.builder().name("caseRole1").users(singletonList("user1")).groups(singletonList("group1")).build());
        final List<String> assignedUsers = new ArrayList<>();
        assignedUsers.add("user2");
        assignedUsers.add("user3");
        roleAssignments.add(CaseRoleAssignmentSummary.builder().name("caseRole2").users(assignedUsers).build());

        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roleAssignments);
        verify(translationService,
               times(2)).format(eq("InvalidRoleAssignment"),
                                anyString(),
                                eq(roleCardinality));
        assertThat(errors).hasSize(2);
    }

    @Test
    public void testValidateRolesAssignments_roleWithInfiniteCardinality() {
        final String caseRole = "test";
        final Integer caseRoleCardinality = -1;
        final Map<String, Integer> role = Collections.singletonMap(caseRole,
                                                                   caseRoleCardinality);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final CaseRoleAssignmentSummary caseRoleAssignmentSummaryMock = mock(CaseRoleAssignmentSummary.class);
        final List<CaseRoleAssignmentSummary> roles = singletonList(caseRoleAssignmentSummaryMock);
        when(caseRoleAssignmentSummaryMock.getName()).thenReturn(caseRole);
        when(caseRoleAssignmentSummaryMock.getUsers()).thenReturn(null);

        try{
            caseRolesValidations.validateRolesAssignments(cds,
                                                          roles);
        } catch (NullPointerException e) {
            Assert.fail("Case role cardinality = " + caseRoleCardinality
                        + ", role assignments check should be skipped");
        }
    }
}