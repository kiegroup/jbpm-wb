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
import org.jbpm.workbench.cm.client.util.FormGroup;
import org.jbpm.workbench.cm.client.util.FormLabel;
import org.jbpm.workbench.cm.client.util.Modal;
import org.jbpm.workbench.cm.client.util.Select;
import org.jbpm.workbench.cm.client.util.ValidationState;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class NewRoleAssignmentViewImpl implements CaseRolesPresenter.NewRoleAssignmentView {

    @Inject
    @DataField("role-name-group")
    FormGroup roleNameGroup;

    @Inject
    @DataField("role-name-help")
    Span roleNameHelp;

    @Inject
    @DataField("role-name-select")
    Select roleNameList;

    @Inject
    @DataField("role-name-label")
    FormLabel roleNameLabel;

    @Inject
    @DataField("assignment-label")
    FormLabel assignmentLabel;

    @Inject
    @DataField("user-name-input")
    TextInput userNameInput;

    @Inject
    @DataField("user-name-group")
    FormGroup userNameGroup;

    @Inject
    @DataField("group-name-input")
    TextInput groupNameInput;

    @Inject
    @DataField("group-name-help")
    Span groupNameHelp;

    @Inject
    @DataField("group-name-group")
    FormGroup groupNameGroup;

    @Inject
    @DataField("modal")
    private Modal modal;

    private Command okCommand;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.roleNameLabel.addRequiredIndicator();
        this.assignmentLabel.addRequiredIndicator();
    }

    @Override
    public void init(final CaseRolesPresenter presenter) {
    }

    @Override
    public void show( final Set<String> roles, final Command okCommand) {
        clearErrorMessages();
        clearValues();

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
        boolean validForm=true;
        clearErrorMessages();

        final boolean roleNameEmpty = isNullOrEmpty(roleNameList.getValue());
        if (roleNameEmpty) {
            roleNameList.getElement().focus();
            roleNameHelp.setTextContent(translationService.format(PLEASE_SELECT_ROLE));
            roleNameGroup.setValidationState(ValidationState.ERROR);
            validForm=false;
        }

        if (isNullOrEmpty(userNameInput.getValue()) && isNullOrEmpty(groupNameInput.getValue())) {
            userNameInput.focus();
            userNameGroup.setValidationState(ValidationState.ERROR);
            groupNameGroup.setValidationState(ValidationState.ERROR);
            groupNameHelp.setTextContent(translationService.format(PLEASE_INTRO_USER_OR_GROUP_TO_CREATE_ASSIGNMENT));
            validForm = false;
        }

        if (validForm) {
            roleNameGroup.setValidationState(ValidationState.SUCCESS);
            userNameGroup.setValidationState(ValidationState.SUCCESS);
            groupNameGroup.setValidationState(ValidationState.SUCCESS);

        }
        return validForm;
    }

    private void clearValues() {
        roleNameList.setValue("");
        roleNameList.removeAllOptions();
        roleNameList.refresh();
        userNameInput.setValue("");
        groupNameInput.setValue("");
    }

    private void clearErrorMessages() {
        roleNameHelp.setTextContent("");
        groupNameHelp.setTextContent("");
        roleNameGroup.clearValidationState();
        userNameGroup.clearValidationState();
        groupNameGroup.clearValidationState();
    }

    @Override
    public String getRoleName() {
        return roleNameList.getValue();
    }

    @Override
    public String getUserName() {
        return userNameInput.getValue();
    }


    @Override
    public String getGroupName() {
        return groupNameInput.getValue();
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    @EventHandler("assign")
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