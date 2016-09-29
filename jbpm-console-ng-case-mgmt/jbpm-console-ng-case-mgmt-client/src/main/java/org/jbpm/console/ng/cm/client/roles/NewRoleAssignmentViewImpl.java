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

package org.jbpm.console.ng.cm.client.roles;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class NewRoleAssignmentViewImpl extends Composite implements CaseRolesPresenter.NewRoleAssignmentView {

    @DataField("role-name-group")
    Element roleNameGroup = DOM.createDiv();

    @Inject
    @DataField("role-name-help")
    Span roleNameHelp;

    @DataField("role-name-select")
    Select roleNameList = GWT.create(Select.class);

    @Inject
    @DataField("role-name-label")
    FormLabel roleNameLabel;

    @Inject
    @DataField("user-name-label")
    FormLabel userNameLabel;

    @Inject
    @DataField("user-name-input")
    TextInput userNameInput;

    @Inject
    @DataField("user-name-help")
    Span userNameHelp;

    @DataField("user-name-group")
    Element userNameGroup = DOM.createDiv();

    private BaseModal modal = GWT.create(BaseModal.class);

    private Boolean forUser;

    private Command okCommand;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        this.modal.setTitle(translationService.format(NEW_ROLE_ASSIGNMENT));
        this.modal.setBody(this);
        final GenericModalFooter footer = GWT.create(GenericModalFooter.class);
        footer.addButton(
                translationService.format(ASSIGN),
                () -> okButton(),
                IconType.PLUS,
                ButtonType.PRIMARY
        );
        this.modal.add(footer);
        this.roleNameLabel.setShowRequiredIndicator(true);
        this.userNameLabel.setShowRequiredIndicator(true);
    }

    @Override
    public void init(final CaseRolesPresenter presenter) {
    }

    @Override
    public void show(final Boolean forUser, final Set<String> roles, final Command okCommand) {
        clearErrorMessages();
        clearValues();

        this.forUser = forUser;

        userNameLabel.setText(translationService.format(forUser ? USER : GROUP));

        for (final String role : roles) {
            final Option option = GWT.create(Option.class);
            option.setValue(role);
            option.setText(role);
            roleNameList.add(option);
        }
        roleNameList.refresh();

        this.okCommand = okCommand;

        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    private void okButton() {
        if (validateForm() == false) {
            return;
        }

        if (okCommand != null) {
            okCommand.execute();
        }

        hide();
    }

    private boolean validateForm() {
        clearErrorMessages();

        final boolean roleNameEmpty = isNullOrEmpty(roleNameList.getValue());
        if (roleNameEmpty) {
            roleNameList.setFocus(true);
            roleNameHelp.setTextContent(translationService.format(PLEASE_SELECT_ROLE));
            setFormGroupStyle(roleNameGroup, ValidationState.ERROR);
        }

        final boolean userNameEmpty = isNullOrEmpty(userNameInput.getValue());
        if (userNameEmpty) {
            userNameInput.focus();
            userNameHelp.setTextContent(translationService.format(forUser ? USER_REQUIRED : GROUP_REQUIRED));
            setFormGroupStyle(userNameGroup, ValidationState.ERROR);
        }

        if (roleNameEmpty || userNameEmpty) {
            return false;
        }

        setFormGroupStyle(roleNameGroup, ValidationState.SUCCESS);
        setFormGroupStyle(userNameGroup, ValidationState.SUCCESS);

        return true;
    }

    private void clearValues() {
        roleNameList.setValue("");
        roleNameList.clear();
        roleNameList.refresh();
        userNameInput.setValue("");
    }

    private void clearErrorMessages() {
        roleNameHelp.setTextContent("");
        userNameHelp.setTextContent("");
        setFormGroupStyle(userNameGroup, ValidationState.NONE);
        setFormGroupStyle(roleNameGroup, ValidationState.NONE);
    }

    private void setFormGroupStyle(final Element element, final ValidationState validationState) {
        StyleHelper.addUniqueEnumStyleName(element, ValidationState.class, validationState);
    }

    @Override
    public String getRoleName() {
        return roleNameList.getValue();
    }

    @Override
    public String getUserName() {
        return userNameInput.getValue();
    }
}