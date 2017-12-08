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
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.resources.i18n.Constants;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.uberfire.client.views.pfly.widgets.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

@Dependent
@Templated
public class NewCaseInstanceViewImpl extends AbstractView<NewCaseInstancePresenter> implements NewCaseInstancePresenter.NewCaseInstanceView {

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

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
    @DataField("roles-form-group")
    private Div rolesFormGroup;

    @Inject
    @DataField("alert")
    private InlineNotification notification;

    @Inject
    @Bound
    @ListContainer("tbody")
    @DataField("roles")
    @SuppressWarnings("unused")
    private ListComponent<CaseRoleAssignmentSummary, RoleAssignmentViewImpl> roleAssignments;

    @Inject
    @AutoBound
    private DataBinder<List<CaseRoleAssignmentSummary>> roleAssignmentList;

    @Inject
    @DataField("roles-help")
    private Anchor rolesHelp;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        caseDefinitionNameLabel.addRequiredIndicator();
        ownerNameLabel.addRequiredIndicator();
        rolesHelp.setAttribute("data-content",
                               translationService.getTranslation(Constants.ROLES_INFO_TEXT));
        jQueryPopover.wrap(rolesHelp).popover();
        notification.setType(InlineNotification.InlineNotificationType.DANGER);
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
    public void setCaseDefinitions(final List<CaseDefinitionSummary> definitions) {
        clearCaseDefinitions();
        caseTemplatesList.setValue("");

        final Map<String, List<CaseDefinitionSummary>> nameToDefinitions = definitions.stream().collect(Collectors.groupingBy(CaseDefinitionSummary::getName));

        nameToDefinitions.entrySet().forEach(entry -> {
            if (entry.getValue().size() > 1) {
                entry.getValue().forEach(def -> caseTemplatesList.addOption(def.getName(),
                                                                            def.getContainerId(),
                                                                            def.getUniqueId(),
                                                                            false));
            } else {
                final CaseDefinitionSummary def = entry.getValue().get(0);
                caseTemplatesList.addOption(def.getName(),
                                            def.getUniqueId());
            }
        });

        caseTemplatesList.refresh();
        loadCaseRoles();
    }

    @Override
    public void clearRoles() {
        roleAssignmentList.setModel(emptyList());
        addCSSClass(rolesFormGroup,
                    "hidden");
    }

    @Override
    public void setRoles(final List<CaseRoleAssignmentSummary> roles) {
        roleAssignmentList.setModel(roles);
        removeCSSClass(rolesFormGroup,
                       "hidden");
    }

    public void cleanForm() {
        caseTemplatesList.setValue("");
        caseTemplatesList.getElement().focus();
        caseTemplatesList.enable();
        ownerNameInput.setValue("");
        addCSSClass(notification.getElement(),
                    "hidden");
        clearErrorMessages();
    }

    private boolean validateForm() {
        clearErrorMessages();

        boolean valid = true;

        if (isNullOrEmpty(caseTemplatesList.getValue())) {
            caseTemplatesList.getElement().focus();
            definitionNameHelp.setTextContent(translationService.format(Constants.PLEASE_SELECT_CASE));
            caseDefinitionNameGroup.setValidationState(ValidationState.ERROR);
            valid = false;
        }

        if (isNullOrEmpty(ownerNameInput.getValue())) {
            ownerNameInput.focus();
            ownerNameHelp.setTextContent(translationService.format(Constants.PLEASE_PROVIDE_CASE_OWNER));
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
    public void showError(final List<String> messages) {
        if (messages.isEmpty()) {
            return;
        }

        notification.setMessage(messages);
        removeCSSClass(notification.getElement(),
                       "hidden");
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    private void createCaseInstance() {
        presenter.createCaseInstance(caseTemplatesList.getValue(),
                                     ownerNameInput.getValue(),
                                     roleAssignmentList.getModel());
    }

    private void clearErrorMessages() {
        definitionNameHelp.setTextContent("");
        ownerNameHelp.setTextContent("");
        notification.setMessage("");
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

    @EventHandler("definition-name-select")
    public void onCaseChanged(final @ForEvent("change") Event event) {
        loadCaseRoles();
    }

    private void loadCaseRoles() {
        presenter.loadCaseRoles(caseTemplatesList.getValue());
    }
}