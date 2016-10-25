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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.cm.client.util.AbstractView;
import org.jbpm.console.ng.cm.client.util.FormGroup;
import org.jbpm.console.ng.cm.client.util.FormLabel;
import org.jbpm.console.ng.cm.client.util.Modal;
import org.jbpm.console.ng.cm.client.util.Select;
import org.jbpm.console.ng.cm.client.util.ValidationState;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.console.ng.cm.client.resources.i18n.Constants.PLEASE_SELECT_CASE_DEFINITION;

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
    @DataField("definition-name-label")
    private FormLabel caseDefinitionNameLabel;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        caseDefinitionNameLabel.addRequiredIndicator();
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
        clearErrorMessages();
    }

    private boolean validateForm() {
        clearErrorMessages();

        if (isNullOrEmpty(caseTemplatesList.getValue())) {
            caseTemplatesList.getElement().focus();
            definitionNameHelp.setTextContent(translationService.format(PLEASE_SELECT_CASE_DEFINITION));
            caseDefinitionNameGroup.setValidationState(ValidationState.ERROR);
            return false;
        } else {
            caseDefinitionNameGroup.setValidationState(ValidationState.SUCCESS);
            return true;
        }
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    private void createCaseInstance() {
        presenter.createCaseInstance(caseTemplatesList.getValue());
    }

    private void clearErrorMessages() {
        definitionNameHelp.setTextContent("");
        caseDefinitionNameGroup.clearValidationState();
    }

    @EventHandler("create")
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