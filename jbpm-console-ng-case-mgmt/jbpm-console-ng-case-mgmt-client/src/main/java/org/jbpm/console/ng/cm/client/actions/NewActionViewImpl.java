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

package org.jbpm.console.ng.cm.client.actions;

import java.util.Set;
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
import org.jbpm.console.ng.cm.client.util.FormGroup;
import org.jbpm.console.ng.cm.client.util.FormLabel;
import org.jbpm.console.ng.cm.client.util.Modal;
import org.jbpm.console.ng.cm.client.util.Select;
import org.jbpm.console.ng.cm.client.util.ValidationState;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

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
    @DataField("action-desc-group")
    FormGroup actionDescGroup;

    @Inject
    @DataField("action-desc-label")
    FormLabel actionDescLabel;

    @Inject
    @DataField("action-desc-input")
    TextInput actionDescInput;

    @Inject
    @DataField("action-desc-help")
    Span actionDescHelp;

    @Inject
    @DataField("role-name-group")
    FormGroup roleNameGroup;

    @Inject
    @DataField("role-name-label")
    FormLabel roleNameLabel;

    @Inject
    @DataField("role-name-select")
    Select roleNameList;

    @Inject
    @DataField("role-name-help")
    Span roleNameHelp;

    @Inject
    @DataField("user-name-group")
    FormGroup userNameGroup;

    @Inject
    @DataField("user-name-label")
    FormLabel userNameLabel;

    @Inject
    @DataField("user-name-input")
    TextInput userNameInput;

    @Inject
    @DataField("user-name-help")
    Span userNameHelp;

    @Inject
    @DataField("modal")
    private Modal modal;

    private Boolean forUser;

    private Command okCommand;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.actionNameLabel.addRequiredIndicator();
        this.roleNameLabel.addRequiredIndicator();
    }

    @Override
    public void init(final CaseActionsPresenter presenter) {
    }

    @Override
    public void show(boolean forUser, final Set<String> roles, final Command okCommand) {
        clearErrorMessages();
        clearValues();

        this.forUser = forUser;

        userNameLabel.getElement().setTextContent(translationService.format(forUser ? USER : GROUP));
        userNameLabel.addRequiredIndicator();

        roles.forEach(r -> roleNameList.addOption(r));
        roleNameList.refresh();

        this.okCommand = okCommand;

        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    private boolean validateForm() {
        clearErrorMessages();

        final boolean roleNameEmpty = isNullOrEmpty(roleNameList.getValue());
        final boolean userNameEmpty = isNullOrEmpty(userNameInput.getValue());
        if (roleNameEmpty && userNameEmpty) {
            roleNameList.getElement().focus();
            roleNameHelp.setTextContent(translationService.format(PLEASE_SELECT_ROLE));
            roleNameGroup.setValidationState(ValidationState.ERROR);
            userNameInput.focus();
            userNameHelp.setTextContent(translationService.format(forUser ? USER_REQUIRED : GROUP_REQUIRED));
            userNameGroup.setValidationState(ValidationState.ERROR);
            return false;
        }

        roleNameGroup.setValidationState(ValidationState.SUCCESS);
        userNameGroup.setValidationState(ValidationState.SUCCESS);

        return true;
    }

    private void clearValues() {
        roleNameList.setValue("");
        roleNameList.removeAllOptions();
        roleNameList.refresh();
        userNameInput.setValue("");
        actionNameInput.setValue("");
        actionDescInput.setValue("");
    }

    private void clearErrorMessages() {
        roleNameHelp.setTextContent("");
        userNameHelp.setTextContent("");
        userNameGroup.clearValidationState();
        roleNameGroup.clearValidationState();
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
        return userNameInput.getValue();
    }

    @Override
    public String getGroups() {
        return null;
    }


    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    @EventHandler("addDynamicTask")
    public void onAssignClick(final @ForEvent("click") MouseEvent event) {
        if (validateForm() == false) {
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