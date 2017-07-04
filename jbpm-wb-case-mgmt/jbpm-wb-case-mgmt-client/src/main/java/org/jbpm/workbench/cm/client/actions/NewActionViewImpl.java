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

package org.jbpm.workbench.cm.client.actions;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.uberfire.client.views.pfly.widgets.*;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;
import static org.jboss.errai.common.client.dom.DOMUtil.*;

@Dependent
@Templated
public class NewActionViewImpl implements CaseActionsPresenter.NewActionView {

    @Inject
    @DataField("action-creation-title")
    Span modalTitle;

    @Inject
    @DataField("action-name-group")
    FormGroup actionNameGroup;

    @Inject
    @DataField("action-name-label")
    FormLabel actionNameLabel;

    @Inject
    @DataField("action-name-input")
    TextInput actionNameInput;

    @Inject
    @DataField("action-name-help")
    Span actionNameHelp;

    @Inject
    @DataField("action-desc-group")
    FormGroup actionDescGroup;

    @Inject
    @DataField("action-desc-input")
    TextInput actionDescInput;

    @Inject
    @DataField("action-users-group")
    FormGroup actionUsersGroup;

    @Inject
    @DataField("action-users-input")
    TextInput actionUsersInput;

    @Inject
    @DataField("action-groups-group")
    FormGroup actionGroupsGroup;

    @Inject
    @DataField("action-groups-input")
    TextInput actionGroupsInput;

    @Inject
    @DataField("assignation-help")
    Span assignationHelp;

    @Inject
    @DataField("action-process-definitions-group")
    FormGroup actionProcessDefinitionsGroup;

    @Inject
    @DataField("action-case-definitions-label")
    FormLabel processDefinitionsLabel;

    @Inject
    @DataField("process-definitions-help")
    Span processDefinitionsHelp;

    @Inject
    @DataField("stages-select")
    Select stages;

    @Inject
    @DataField("action-process-definitions-select")
    private Select processDefinitionsList;

    @Inject
    @DataField("modal")
    private Modal modal;

    private Command okCommand;

    private CaseActionType caseActionType;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.actionNameLabel.addRequiredIndicator();
        this.processDefinitionsLabel.addRequiredIndicator();
    }

    @Override
    public void init(final CaseActionsPresenter presenter) {
    }

    @Override
    public void show(final CaseActionType caseActionType,
                     final Command okCommand) {
        setCaseActionType(caseActionType);
        clearErrorMessages();
        clearValues();
        this.okCommand = okCommand;
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setCaseStagesList(List<CaseStageSummary> caseStagesList) {
        caseStagesList.stream()
                .filter(s -> s.getStatus().equals(CaseStageStatus.ACTIVE.getStatus()))
                .forEach(e -> stages.addOption(e.getIdentifier(),
                                               e.getName()));
        stages.refresh();
    }

    @Override
    public String getStageId() {
        return stages.getValue();
    }

    private boolean validateForm() {
        boolean validForm = true;
        clearErrorMessages();
        if (isNullOrEmpty(actionNameInput.getValue())) {
            actionNameInput.focus();
            actionNameHelp.setTextContent(translationService.format(PLEASE_INTRO_ACTION_NAME));
            actionNameGroup.setValidationState(ValidationState.ERROR);
            validForm = false;
        }
        switch (caseActionType) {
            case DYNAMIC_USER_TASK: {
                if (isNullOrEmpty(actionUsersInput.getValue()) && isNullOrEmpty(actionGroupsInput.getValue())) {
                    actionUsersInput.focus();
                    actionUsersGroup.setValidationState(ValidationState.ERROR);
                    actionGroupsGroup.setValidationState(ValidationState.ERROR);
                    assignationHelp.setTextContent(translationService.format(PLEASE_INTRO_USER_OR_GROUP_TO_ASSIGN_NEW_ACTION));
                    validForm = false;
                }
                break;
            }
            case DYNAMIC_SUBPROCESS_TASK: {
                if (isNullOrEmpty(processDefinitionsList.getValue())) {
                    processDefinitionsList.getElement().focus();
                    actionProcessDefinitionsGroup.setValidationState(ValidationState.ERROR);
                    processDefinitionsHelp.setTextContent(translationService.format(PLEASE_INTRO_SUBPROCESS_ID));
                    validForm = false;
                }
                break;
            }
        }

        if (validForm) {
            actionGroupsGroup.setValidationState(ValidationState.SUCCESS);
            actionUsersGroup.setValidationState(ValidationState.SUCCESS);
            actionNameGroup.setValidationState(ValidationState.SUCCESS);
            actionProcessDefinitionsGroup.setValidationState(ValidationState.SUCCESS);
        }
        return validForm;
    }

    private void clearValues() {
        actionUsersInput.setValue("");
        actionGroupsInput.setValue("");
        actionNameInput.setValue("");
        actionDescInput.setValue("");
        processDefinitionsList.setValue("");
        stages.setValue("");
    }

    private void clearErrorMessages() {
        actionNameHelp.setTextContent("");
        assignationHelp.setTextContent("");
        processDefinitionsHelp.setTextContent("");

        actionUsersGroup.clearValidationState();
        actionGroupsGroup.clearValidationState();
        actionNameGroup.clearValidationState();
        actionProcessDefinitionsGroup.clearValidationState();
    }

    @Override
    public void setProcessDefinitions(final List<String> definitions) {
        for (final String definition : definitions) {
            processDefinitionsList.addOption(definition);
        }
        processDefinitionsList.refresh();
    }

    @Override
    public String getTaskName() {
        return actionNameInput.getValue();
    }

    @Override
    public String getDescription() {
        return actionDescInput.getValue();
    }

    @Override
    public String getProcessDefinitionName() {
        return processDefinitionsList.getValue();
    }

    @Override
    public String getActors() {
        return actionUsersInput.getValue();
    }

    @Override
    public String getGroups() {
        return actionGroupsInput.getValue();
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    public void setCaseActionType(CaseActionType caseActionType) {
        this.caseActionType = caseActionType;
        switch (caseActionType) {
            case DYNAMIC_USER_TASK: {
                modalTitle.setTextContent(translationService.format(NEW_USER_TASK));
                removeCSSClass(this.actionDescGroup.getElement(),
                               "hidden");
                removeCSSClass(this.actionUsersGroup.getElement(),
                               "hidden");
                removeCSSClass(this.actionGroupsGroup.getElement(),
                               "hidden");
                addCSSClass(this.actionProcessDefinitionsGroup.getElement(),
                            "hidden");
                break;
            }
            case DYNAMIC_SUBPROCESS_TASK: {
                modalTitle.setTextContent(translationService.format(NEW_PROCESS_TASK));
                addCSSClass(this.actionDescGroup.getElement(),
                            "hidden");
                addCSSClass(this.actionUsersGroup.getElement(),
                            "hidden");
                addCSSClass(this.actionGroupsGroup.getElement(),
                            "hidden");
                removeCSSClass(this.actionProcessDefinitionsGroup.getElement(),
                               "hidden");
                break;
            }
        }
    }

    @EventHandler("addDynamicTask")
    public void onAssignClick(final @ForEvent("click") MouseEvent event) {
        if (!validateForm()) {
            return;
        }
        if (okCommand != null) {
            okCommand.execute();
        }
        hide();
    }

    @EventHandler("cancel")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    @EventHandler("close")
    public void onCloseClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }
}