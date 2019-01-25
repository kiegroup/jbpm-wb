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

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.client.events.CaseCreatedEvent;
import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractPresenter;
import org.jbpm.workbench.cm.client.util.CaseRolesValidations;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.Optional.ofNullable;

@Dependent
public class NewCaseInstancePresenter extends AbstractPresenter<NewCaseInstancePresenter.NewCaseInstanceView> {

    @Inject
    CaseRolesValidations caseRolesValidations;

    private List<CaseDefinitionSummary> caseDefinitions = emptyList();

    private Caller<CaseManagementService> caseService;

    private Event<NotificationEvent> notification;

    private Event<CaseCreatedEvent> newCaseEvent;

    @Inject
    private TranslationService translationService;

    @Inject
    private User identity;

    void setCaseDefinitions(List<CaseDefinitionSummary> caseDefinitions) {
        this.caseDefinitions = caseDefinitions;
    }

    protected void loadCaseRoles(final String caseDefinitionId) {
        view.clearRoles();
        final Optional<CaseDefinitionSummary> cds = getCaseDefinitionSummary(caseDefinitionId);
        if (cds.isPresent()) {
            List<CaseRoleAssignmentSummary> roles = cds.get().getRoles().keySet().stream()
                                                                                 .filter(r -> !"owner".equals(r))
                                                                                 .map(r -> CaseRoleAssignmentSummary.builder().name(r).build())
                                                                                 .collect(toList());
            if (!roles.isEmpty()) {
                view.setRoles(roles);
            }
        }
    }

    private Optional<CaseDefinitionSummary> getCaseDefinitionSummary(final String caseDefinitionId) {
        return caseDefinitions.stream().filter(d -> d.getUniqueId().equals(caseDefinitionId)).findFirst();
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

                    setCaseDefinitions(definitions);
                    view.show();
                    view.setCaseDefinitions(caseDefinitions);
                    view.setOwner(identity.getIdentifier());
                }
        ).getCaseDefinitions();
    }

    protected void createCaseInstance(final String caseDefinitionId,
                                      final String owner,
                                      final List<CaseRoleAssignmentSummary> assignments) {
        final Optional<CaseDefinitionSummary> cds = getCaseDefinitionSummary(caseDefinitionId);
        if (cds.isPresent()) {
            final CaseDefinitionSummary caseDefinition = cds.get();
            final List<String> assignmentErrors = caseRolesValidations.validateRolesAssignments(caseDefinition,
                                                                                                assignments);
            if (assignmentErrors.isEmpty() == false) {
                view.showError(assignmentErrors);
                return;
            }

            caseService.call(
                    (String caseId) -> {
                        view.hide();
                        notification.fire(new NotificationEvent(translationService.format(Constants.CASE_CREATED_WITH_ID,
                                                                                          caseId),
                                                                NotificationEvent.NotificationType.SUCCESS));
                        newCaseEvent.fire(new CaseCreatedEvent(caseId));
                    },
                    (Message message, Throwable t) -> {
                        view.showError(singletonList(t.getMessage()));
                        return false;
                    }
            ).startCaseInstance(caseDefinition.getContainerId(),
                                caseDefinition.getId(),
                                owner,
                                assignments);
        } else {
            notification.fire(new NotificationEvent(translationService.format(Constants.INVALID_CASE_DEFINITION),
                                                    NotificationEvent.NotificationType.ERROR));
        }
    }

    protected boolean validateCaseOwnerAssignment(String ownerInput) {
        final Integer ownerRoleCardinality = 1;
        boolean isAssignmentValid = true;

        final List<String> ownerInputList = Splitter.on(",")
                                                    .trimResults()
                                                    .omitEmptyStrings()
                                                    .splitToList(ofNullable(ownerInput).orElse(""));
        final String ownerInputFiltered = Joiner.on(", ").join(ownerInputList);

        if (!ownerInputFiltered.equals(ownerInput)) {
            view.setOwner(ownerInputFiltered);
        }

        if (ownerInputFiltered.isEmpty()) {
            view.showCaseOwnerError(translationService.format(Constants.PLEASE_PROVIDE_CASE_OWNER));
            isAssignmentValid = false;
        } else if (ownerInputList.size() > ownerRoleCardinality) {
            view.showCaseOwnerError(translationService.format(Constants.INVALID_ROLE_ASSIGNMENT,
                                                              "owner",
                                                              ownerRoleCardinality));
            isAssignmentValid = false;
        }

        return isAssignmentValid;
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

        void setCaseDefinitions(List<CaseDefinitionSummary> definitions);

        void clearRoles();

        void setRoles(List<CaseRoleAssignmentSummary> roles);

        void setOwner(String owner);

        void showError(List<String> messages);

        void showCaseOwnerError(String message);
    }
}