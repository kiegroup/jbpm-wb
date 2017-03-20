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

package org.jbpm.workbench.cm.client.newcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.client.events.CaseCreatedEvent;
import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractPresenter;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.workbench.events.NotificationEvent;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Dependent
public class NewCaseInstancePresenter extends AbstractPresenter<NewCaseInstancePresenter.NewCaseInstanceView> {

    private Map<String, CaseDefinitionSummary> caseDefinitions = new HashMap<>();

    private Caller<CaseManagementService> caseService;

    private Event<NotificationEvent> notification;

    private Event<CaseCreatedEvent> newCaseEvent;

    @Inject
    private TranslationService translationService;

    @Inject
    private User identity;

    protected void loadCaseRoles(final String caseDefinition) {
        view.clearRoles();
        if (isNullOrEmpty(caseDefinition)) {
            return;
        }
        final CaseDefinitionSummary caseDefinitionSummary = caseDefinitions.get(caseDefinition);
        if (caseDefinitionSummary == null) {
            return;
        }
        final List<CaseRoleAssignmentSummary> roles = caseDefinitionSummary.getRoles().keySet().stream().filter(r -> "owner".equals(r) == false).map(r -> CaseRoleAssignmentSummary.builder().name(r).build()).collect(toList());
        view.setRoles(roles);
    }

    public void show() {
        loadCaseDefinitions();
    }

    protected void loadCaseDefinitions() {
        view.clearCaseDefinitions();
        caseDefinitions.clear();
        caseService.call(
                (List<CaseDefinitionSummary> definitions) -> {
                    if (definitions.isEmpty()) {
                        notification.fire(new NotificationEvent(translationService.format(Constants.NO_CASE_DEFINITION),
                                                                NotificationEvent.NotificationType.ERROR));
                        return;
                    }

                    caseDefinitions = definitions.stream().collect(toMap(s -> s.getName(),
                                                                         s -> s));

                    final List<String> caseDefinitions = this.caseDefinitions.values().stream().map(s -> s.getName()).sorted().collect(toList());
                    view.show();
                    view.setCaseDefinitions(caseDefinitions);
                    loadCaseRoles(Iterables.getFirst(caseDefinitions,
                                                     null));
                    view.setOwner(identity.getIdentifier());
                }
        ).getCaseDefinitions();
    }

    protected void createCaseInstance(final String caseDefinitionName,
                                      final String owner,
                                      final List<CaseRoleAssignmentSummary> assignments) {
        final CaseDefinitionSummary caseDefinition = caseDefinitions.get(caseDefinitionName);
        if (caseDefinition == null) {
            notification.fire(new NotificationEvent(translationService.format(Constants.INVALID_CASE_DEFINITION),
                                                    NotificationEvent.NotificationType.ERROR));
            return;
        }

        final List<String> assignmentErrors = validateRolesAssignments(caseDefinition,
                                                                       assignments);
        if (assignmentErrors.isEmpty() == false) {
            view.showValidationError(assignmentErrors);
            return;
        }

        caseService.call(
                (String caseId) -> {
                    view.hide();
                    notification.fire(new NotificationEvent(translationService.format(Constants.CASE_CREATED_WITH_ID,
                                                                                      caseId),
                                                            NotificationEvent.NotificationType.SUCCESS));
                    newCaseEvent.fire(new CaseCreatedEvent(caseId));
                }
        ).startCaseInstance(null,
                            caseDefinition.getContainerId(),
                            caseDefinition.getId(),
                            owner,
                            assignments);
    }

    protected List<String> validateRolesAssignments(final CaseDefinitionSummary caseDefinition,
                                                    final List<CaseRoleAssignmentSummary> assignments) {
        final List<String> errors = new ArrayList<>();
        assignments.forEach(a -> {
            final Integer roleCardinality = caseDefinition.getRoles().get(a.getName());
            if (roleCardinality == -1) {
                return;
            }
            final Integer roleAssignments = a.getUsers().size() + a.getGroups().size();
            if (roleAssignments > roleCardinality) {
                errors.add(translationService.format(Constants.INVALID_ROLE_ASSIGNMENT,
                                                     a.getName(),
                                                     roleCardinality));
            }
        });

        return errors;
    }

    @Inject
    public void setNotification(final Event<NotificationEvent> notification) {
        this.notification = notification;
    }

    @Inject
    public void setNewCaseEvent(final Event<CaseCreatedEvent> newCaseEvent) {
        this.newCaseEvent = newCaseEvent;
    }

    @Inject
    public void setCaseService(final Caller<CaseManagementService> caseService) {
        this.caseService = caseService;
    }

    public interface NewCaseInstanceView extends UberElement<NewCaseInstancePresenter> {

        void show();

        void hide();

        void clearCaseDefinitions();

        void setCaseDefinitions(List<String> definitions);

        void clearRoles();

        void setRoles(List<CaseRoleAssignmentSummary> roles);

        void setOwner(String owner);

        void showValidationError(List<String> messages);
    }
}