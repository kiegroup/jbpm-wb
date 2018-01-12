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

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.events.CaseRoleAssignmentListLoadEvent;
import org.jbpm.workbench.cm.client.events.CaseRoleAssignmentListOpenEvent;
import org.jbpm.workbench.cm.client.roles.util.ItemsLine;
import org.jbpm.workbench.cm.client.util.AbstractView;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;

@Dependent
@Templated
public class CaseRoleItemView extends AbstractView<CaseRolesPresenter> implements TakesValue<CaseRoleAssignmentSummary>,
                                                                                  IsElement {

    public static String USERS_LINE_ID = "_users";
    public static String GROUP_LINE_ID = "_groups";

    @Inject
    protected TranslationService translationService;

    @Inject
    @DataField("list-group-item")
    Div listGroupItem;

    @Inject
    @DataField("actions-dropdown")
    Div actions;

    @Inject
    @DataField("actions-button")
    Button actionsButton;

    @Inject
    @DataField("actions-items")
    UnorderedList actionsItems;

    @Inject
    @DataField("role-name")
    @Bound
    Span name;

    @Inject
    @DataField("div-unassigned")
    Div unassignedDiv;

    @Inject
    @DataField("line-users")
    ItemsLine usersItemsLine;

    @Inject
    @DataField("div-users")
    Div usersDiv;

    @Inject
    @DataField("line-groups")
    ItemsLine groupsItemsLine;

    @Inject
    @DataField("div-groups")
    Div groupsDiv;

    @Inject
    ConfirmPopup confirmPopup;

    @Inject
    private CaseRolesPresenter.EditRoleAssignmentView editRoleAssignmentDialog;

    @Inject
    @AutoBound
    private DataBinder<CaseRoleAssignmentSummary> caseRoleAssignmentSummary;

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    @Override
    public CaseRoleAssignmentSummary getValue() {
        return caseRoleAssignmentSummary.getModel();
    }

    @Override
    public void setValue(final CaseRoleAssignmentSummary model) {
        caseRoleAssignmentSummary.setModel(model);
    }

    public void displayOwnerActions() {
        removeCSSClass(actions,
                       "hidden");
        addCSSClass(actionsItems,
                    "hidden");
        addCSSClass(actionsButton,
                    "disabled");
    }

    public void displayEnabledAction(final CaseRolesPresenter.CaseRoleAction action) {
        removeCSSClass(actions,
                       "hidden");
        final HTMLElement li = getDocument().createElement("li");
        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        addCSSClass(a,
                    "kie-more-info-link");
        a.setOnclick(e -> action.execute());
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

    public void displayDisabledAction(final CaseRolesPresenter.CaseRoleAction action) {
        removeCSSClass(actions,
                       "hidden");
        final HTMLElement li = getDocument().createElement("li");
        final HTMLElement a = getDocument().createElement("a");
        a.setTextContent(action.label());
        addCSSClass(li,
                    "disabled");
        li.appendChild(a);
        actionsItems.appendChild(li);
    }

    public void displayOwnerAssignment() {
        hideGroupAssignments();
        final String owner = getValue().getUsers().get(0);
        usersItemsLine.initWithSingleItem(CaseRolesPresenter.CASE_OWNER_ROLE,
                                          owner);
    }

    public void hideGroupAssignments() {
        displaySingleAssignment(usersDiv,
                                groupsDiv);
    }

    public void hideUserAssignments() {
        displaySingleAssignment(groupsDiv,
                                usersDiv);
    }

    private void displaySingleAssignment(Div divToExpand,
                                         Div divToHide) {
        addCSSClass(divToExpand,
                    "kie-single-role-assignment-height");
        addCSSClass(divToHide,
                    "hidden");
    }

    public void displayUnassigned() {
        addCSSClass(usersDiv,
                    "hidden");
        addCSSClass(groupsDiv,
                    "hidden");
        removeCSSClass(unassignedDiv,
                       "hidden");
    }

    public void displayAssignmentsList(final String listId,
                                       final int listWidth,
                                       final List<CaseRolesPresenter.CaseAssignmentItem> assignmentsList) {
        if (USERS_LINE_ID.equals(listId)) {
            usersItemsLine.initWithItemsLine(listWidth,
                                             name.getTextContent() + USERS_LINE_ID,
                                             assignmentsList);
        } else if (GROUP_LINE_ID.equals(listId)) {
            groupsItemsLine.initWithItemsLine(listWidth,
                                              name.getTextContent() + GROUP_LINE_ID,
                                              assignmentsList);
        }
    }

    public void showEditRoleAssignmentDialog() {
        final String roleName = getValue().getName();
        final CaseRoleAssignmentSummary currentRoleAssignments = CaseRoleAssignmentSummary.builder()
                                                                                          .name(roleName)
                                                                                          .users(getValue().getUsers())
                                                                                          .groups(getValue().getGroups())
                                                                                          .build();
        editRoleAssignmentDialog.setValue(currentRoleAssignments);
        editRoleAssignmentDialog.show(() -> presenter.assignToRole(this,
                                                                   getNewRoleAssignments()));
    }

    private CaseRoleAssignmentSummary getNewRoleAssignments() {
        return editRoleAssignmentDialog.getValue();
    }

    public void showErrorState() {
        editRoleAssignmentDialog.setErrorState();
    }

    public void showAssignmentErrors(List<String> caseRoleAssignmentErrors) {
        editRoleAssignmentDialog.showValidationError(caseRoleAssignmentErrors);
    }

    public void hideEditRoleAssignmentDialog() {
        editRoleAssignmentDialog.hide();
    }

    public void showRemoveAllAssignmentsPopup() {
        confirmPopup.show(translationService.format(REMOVE_ASSIGNMENT),
                          translationService.format(REMOVE),
                          translationService.format(REMOVE_ALL_USERS_GROUPS_FROM_ROLE,
                                                    getValue().getName()),
                          () -> presenter.storeRoleAssignments(getValue(),
                                                               Collections.emptyList(),
                                                               Collections.emptyList()));
    }

    public void showRemoveUserAssignmentPopup(final String user) {
        confirmPopup.show(translationService.format(REMOVE_ASSIGNMENT),
                          translationService.format(REMOVE),
                          translationService.format(REMOVE_USER_ASSIGNMENT_FROM_ROLE,
                                                    user,
                                                    getValue().getName()),
                          () -> presenter.removeUserFromRole(user,
                                                             getValue()));
    }

    public void showRemoveGroupAssignmentPopup(final String group) {
        confirmPopup.show(translationService.format(REMOVE_ASSIGNMENT),
                          translationService.format(REMOVE),
                          translationService.format(REMOVE_GROUP_ASSIGNMENT_FROM_ROLE,
                                                    group,
                                                    getValue().getName()),
                          () -> presenter.removeGroupFromRole(group,
                                                              getValue()));
    }

    public void setLastElementStyle() {
        addCSSClass(actions,
                    "dropup");
    }

    public void onCaseRoleAssignmentListOpenEvent(@Observes CaseRoleAssignmentListOpenEvent event) {
        String lineId = event.getAssignmentLineId();
        if (lineId.equals(name.getTextContent() + USERS_LINE_ID)) {
            groupsItemsLine.hideAllItems();
        } else if (lineId.equals(name.getTextContent() + GROUP_LINE_ID)) {
            usersItemsLine.hideAllItems();
        } else {
            usersItemsLine.hideAllItems();
            groupsItemsLine.hideAllItems();
        }
    }

    public void onCaseRoleAssignmentListLoadEvent(@Observes CaseRoleAssignmentListLoadEvent event) {
        displayCaseRoleActions();
        displayCaseRoleAssignments(event.getMaxWidth());
    }

    private void displayCaseRoleActions() {
        removeCSSClass(actions,
                       "dropup");
        presenter.setCaseRoleActions(this);
    }

    private void displayCaseRoleAssignments(int maxWidth) {
        presenter.setCaseRoleAssignments(this,
                                         maxWidth);
    }
}
