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

import org.jbpm.workbench.cm.client.util.FormGroup;
import org.jbpm.workbench.cm.client.util.FormLabel;
import org.jbpm.workbench.cm.client.util.Modal;
import org.jbpm.workbench.cm.client.util.Select;
import org.jbpm.workbench.cm.client.util.ValidationState;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.*;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class NewActionViewImpl implements CaseActionsPresenter.NewActionView {

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
    @DataField("action-desc-input")
    TextInput actionDescInput;

    @Inject
    @DataField("action-users-group")
    FormGroup usersGroup;

    @Inject
    @DataField("action-users-input")
    TextInput usersInput;

    @Inject
    @DataField("action-groups-group")
    FormGroup groupsGroup;

    @Inject
    @DataField("action-groups-input")
    TextInput groupsInput;

    @Inject
    @DataField("assignation-help")
    Span assignationHelp;

    @Inject
    @DataField("stages-select")
    Select stages;

    @Inject
    @DataField("modal")
    private Modal modal;

    private Command okCommand;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.actionNameLabel.addRequiredIndicator();
    }

    @Override
    public void init(final CaseActionsPresenter presenter) {
    }

    @Override
    public void show(final Command okCommand) {
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
                .forEach(e -> stages.addOption(e.getIdentifier(), e.getName()));
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
        if (isNullOrEmpty(usersInput.getValue()) && isNullOrEmpty(groupsInput.getValue())) {
            usersInput.focus();
            usersGroup.setValidationState(ValidationState.ERROR);
            groupsGroup.setValidationState(ValidationState.ERROR);
            assignationHelp.setTextContent(translationService.format(PLEASE_INTRO_USER_OR_GROUP_TO_ASSIGN_NEW_ACTION));
            validForm = false;
        }
        if (validForm) {
            groupsGroup.setValidationState(ValidationState.SUCCESS);
            usersGroup.setValidationState(ValidationState.SUCCESS);
            actionNameGroup.setValidationState(ValidationState.SUCCESS);
        }
        return validForm;
    }

    private void clearValues() {
        usersInput.setValue("");
        groupsInput.setValue("");
        actionNameInput.setValue("");
        actionDescInput.setValue("");
        stages.setValue("");
    }

    private void clearErrorMessages() {
        actionNameHelp.setTextContent("");
        assignationHelp.setTextContent("");

        usersGroup.clearValidationState();
        groupsGroup.clearValidationState();
        actionNameGroup.clearValidationState();
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
    public String getActors() {
        return usersInput.getValue();
    }

    @Override
    public String getGroups() {
        return groupsInput.getValue();
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
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