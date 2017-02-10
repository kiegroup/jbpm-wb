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
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.client.util.FormGroup;
import org.jbpm.workbench.cm.client.util.FormLabel;
import org.jbpm.workbench.cm.client.util.Modal;
import org.jbpm.workbench.cm.client.util.Select;
import org.jbpm.workbench.cm.client.util.ValidationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.PLEASE_PROVIDE_CASE_OWNER;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.PLEASE_SELECT_CASE_DEFINITION;

@Dependent
@Templated
public class NewCaseInstanceViewImpl extends AbstractView<NewCaseInstancePresenter> implements NewCaseInstancePresenter.NewCaseInstanceView {

    @Inject
    @DataField("definition-name-group")
    private FormGroup caseDefinitionNameGroup;

    @Inject
    @DataField("modal")
    private Modal modal;

    @Inject
    @DataField("definition-name-help")
    private Span definitionNameHelp;

    @Inject
    @DataField("definition-name-select")
    private Select caseTemplatesList;

    @Inject
    @DataField("owner-name-input")
    private TextInput ownerNameInput;

    @Inject
    @DataField("owner-name-help")
    private Span ownerNameHelp;

    @Inject
    @DataField("owner-name-group")
    private FormGroup ownerNameGroup;

    @Inject
    @DataField("owner-name-label")
    private FormLabel ownerNameLabel;

    @Inject
    @DataField("definition-name-label")
    private FormLabel caseDefinitionNameLabel;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        caseDefinitionNameLabel.addRequiredIndicator();
        ownerNameLabel.addRequiredIndicator();
    }

    public void show() {
        cleanForm();
        modal.show();
    }

    @Override
    public void hide() {
        cleanForm();
        modal.hide();
    }

    @Override
    public void clearCaseDefinitions() {
        caseTemplatesList.removeAllOptions();
    }

    @Override
    public void setCaseDefinitions(final List<String> definitions) {
        for (final String definition : definitions) {
            caseTemplatesList.addOption(definition);
        }
        caseTemplatesList.refresh();
    }

    public void cleanForm() {
        caseTemplatesList.setValue("");
        caseTemplatesList.getElement().focus();
        caseTemplatesList.enable();
        ownerNameInput.setValue("");
        clearErrorMessages();
    }

    private boolean validateForm() {
        clearErrorMessages();

        boolean valid = true;

        if (isNullOrEmpty(caseTemplatesList.getValue())) {
            caseTemplatesList.getElement().focus();
            definitionNameHelp.setTextContent(translationService.format(PLEASE_SELECT_CASE_DEFINITION));
            caseDefinitionNameGroup.setValidationState(ValidationState.ERROR);
            valid = false;
        }

        if (isNullOrEmpty(ownerNameInput.getValue())) {
            ownerNameInput.focus();
            ownerNameHelp.setTextContent(translationService.format(PLEASE_PROVIDE_CASE_OWNER));
            ownerNameGroup.setValidationState(ValidationState.ERROR);
            valid = false;
        }

        if (valid) {
            caseDefinitionNameGroup.setValidationState(ValidationState.SUCCESS);
            ownerNameGroup.setValidationState(ValidationState.SUCCESS);
        }

        return valid;
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    private void createCaseInstance() {
        presenter.createCaseInstance(caseTemplatesList.getValue(), ownerNameInput.getValue());
    }

    private void clearErrorMessages() {
        definitionNameHelp.setTextContent("");
        ownerNameHelp.setTextContent("");
        caseDefinitionNameGroup.clearValidationState();
        ownerNameGroup.clearValidationState();
    }

    @Override
    public void setOwner(final String owner) {
        ownerNameInput.setValue(owner);
    }

    @EventHandler("start")
    public void onCreateClick(final @ForEvent("click") MouseEvent event) {
        if (validateForm()) {
            createCaseInstance();
        }
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