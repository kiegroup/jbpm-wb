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

import java.util.List;
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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;

import static com.google.common.base.Strings.*;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class NewCaseInstanceViewImpl extends Composite implements NewCaseInstancePresenter.NewCaseInstanceView {

    private final BaseModal modal = GWT.create(BaseModal.class);

    @DataField("definition-name-group")
    Element caseDefinitionNameGroup = DOM.createDiv();

    @Inject
    @DataField("definition-name-help")
    Span definitionNameHelp;

    @DataField("definition-name-select")
    Select caseTemplatesList = GWT.create(Select.class);

    @Inject
    @DataField("definition-name-label")
    FormLabel caseDefinitionNameLabel;

    @Inject
    private TranslationService translationService;

    private NewCaseInstancePresenter presenter;

    @Override
    public void init(final NewCaseInstancePresenter presenter) {
        this.presenter = presenter;

        this.modal.setTitle(translationService.format(NEW_CASE_INSTANCE));
        this.modal.setBody(this);
        final GenericModalFooter footer = GWT.create(GenericModalFooter.class);
        footer.addButton(
                translationService.format(CREATE),
                () -> okButton(),
                IconType.PLUS,
                ButtonType.PRIMARY
        );
        this.modal.add(footer);

        caseDefinitionNameLabel.setShowRequiredIndicator(true);
    }

    public void show() {
        cleanForm();
        this.modal.show();
    }

    private void okButton() {
        if (validateForm()) {
            createCaseInstance();
        }
    }

    @Override
    public void clearCaseDefinitions() {
        caseTemplatesList.clear();
    }

    @Override
    public void addCaseDefinitions(final List<String> definitions) {
        for (final String definition : definitions) {
            final Option option = GWT.create(Option.class);
            option.setText(definition);
            option.setValue(definition);
            caseTemplatesList.add(option);
        }

        caseTemplatesList.refresh();
    }

    public void cleanForm() {
        caseTemplatesList.setValue("");
        caseTemplatesList.setFocus(true);
        clearErrorMessages();
    }

    @Override
    public void hide() {
        cleanForm();
        this.modal.hide();
    }

    private boolean validateForm() {
        clearErrorMessages();

        if (isNullOrEmpty(caseTemplatesList.getValue())) {
            caseTemplatesList.setFocus(true);
            definitionNameHelp.setTextContent(translationService.format(PLEASE_SELECT_CASE_DEFINITION));
            setCaseDefinitionNameGroupStyle(ValidationState.ERROR);
            return false;
        } else {
            setCaseDefinitionNameGroupStyle(ValidationState.SUCCESS);
            return true;
        }
    }

    private void setCaseDefinitionNameGroupStyle(final ValidationState error) {
        StyleHelper.addUniqueEnumStyleName(caseDefinitionNameGroup, ValidationState.class, error);
    }

    private void createCaseInstance() {
        presenter.createCaseInstance(caseTemplatesList.getValue());
    }

    private void clearErrorMessages() {
        definitionNameHelp.setTextContent("");
        setCaseDefinitionNameGroupStyle(ValidationState.NONE);
    }

}