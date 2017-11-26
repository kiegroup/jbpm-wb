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

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseRolesValidationsTest {

    @Mock
    TranslationService translationService;

    @InjectMocks
    CaseRolesValidations caseRolesValidations;

    @Test
    public void testValidateRolesAssignments_SingleAssignmentUser() {
        final Map<String, Integer> role = Collections.singletonMap("test",
                                                                   1);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").users(singletonList("user1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateRolesAssignments_SingleAssignmentGroup() {
        final Map<String, Integer> role = Collections.singletonMap("test",
                                                                   1);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").groups(singletonList("group1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateRolesAssignments_InvalidAssignment() {
        final Map<String, Integer> role = Collections.singletonMap("test",
                                                                   1);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").users(singletonList("user1")).groups(singletonList("group1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        assertFalse(errors.isEmpty());
    }

    @Test
    public void testValidateRolesAssignments_MultipleAssignments() {
        final Map<String, Integer> role = Collections.singletonMap("test",
                                                                   -1);
        final CaseDefinitionSummary cds = CaseDefinitionSummary.builder().roles(role).build();

        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name("test").users(singletonList("user1")).groups(singletonList("group1")).build());
        final List<String> errors = caseRolesValidations.validateRolesAssignments(cds,
                                                                                  roles);
        assertTrue(errors.isEmpty());
    }
}