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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;

import static org.jbpm.workbench.cm.client.resources.i18n.Constants.INVALID_ROLE_ASSIGNMENT;

@Dependent
public class CaseRolesValidations {

    @Inject
    TranslationService translationService;

    public List<String> validateRolesAssignments(final CaseDefinitionSummary caseDefinition,
                                                 final List<CaseRoleAssignmentSummary> assignments) {
        final List<String> errors = new ArrayList<>();
        assignments.forEach(a -> {
            final Integer roleCardinality = caseDefinition.getRoles().get(a.getName());
            if (roleCardinality == -1) {
                return;
            }
            final Integer roleAssignments = a.getUsers().size() + a.getGroups().size();
            if (roleAssignments > roleCardinality) {
                errors.add(translationService.format(INVALID_ROLE_ASSIGNMENT,
                                                     a.getName(),
                                                     roleCardinality));
            }
        });
        return errors;
    }
}