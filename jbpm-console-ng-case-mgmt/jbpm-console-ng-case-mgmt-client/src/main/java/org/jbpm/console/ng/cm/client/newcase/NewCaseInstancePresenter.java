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

package org.jbpm.console.ng.cm.client.newcase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.client.events.CaseCreatedEvent;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
public class NewCaseInstancePresenter {

    private final Map<String, CaseDefinitionSummary> caseDefinitions = new HashMap<>();

    @Inject
    private NewCaseInstanceView view;

    private Caller<CaseManagementService> caseService;

    private Event<NotificationEvent> notification;

    private Event<CaseCreatedEvent> newCaseEvent;

    @Inject
    private TranslationService translationService;

    private String serverTemplateId;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void show(final String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
        loadCaseDefinitions();
    }

    protected void loadCaseDefinitions() {
        view.clearCaseDefinitions();
        caseDefinitions.clear();
        caseService.call(
                (List<CaseDefinitionSummary> definitions) -> {
                    final List<String> definitionNames = new ArrayList<>();
                    for (CaseDefinitionSummary summary : definitions) {
                        definitionNames.add(summary.getName());
                        caseDefinitions.put(summary.getName(), summary);
                    }
                    Collections.sort(definitionNames);
                    view.addCaseDefinitions(definitionNames);
                    view.show();
                }
        ).getCaseDefinitions(serverTemplateId, 0, Integer.MAX_VALUE);
    }

    protected void createCaseInstance(final String caseDefinitionName) {
        final CaseDefinitionSummary caseDefinition = caseDefinitions.get(caseDefinitionName);
        if (caseDefinition == null) {
            notification.fire(new NotificationEvent(translationService.format(INVALID_CASE_DEFINITION), NotificationEvent.NotificationType.ERROR));
            return;
        }
        caseService.call(
                (String caseId) -> {
                    view.hide();
                    notification.fire(new NotificationEvent(translationService.format(CASE_CREATED_WITH_ID, caseId), NotificationEvent.NotificationType.SUCCESS));
                    newCaseEvent.fire(new CaseCreatedEvent(caseId));
                }
        ).startCaseInstance(serverTemplateId, caseDefinition.getContainerId(), caseDefinition.getCaseDefinitionId());
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

    public interface NewCaseInstanceView extends UberView<NewCaseInstancePresenter> {

        void show();

        void hide();

        void clearCaseDefinitions();

        void addCaseDefinitions(List<String> definitions);

    }

}