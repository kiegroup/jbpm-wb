/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.taskslist.popup;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Dependent
@Templated(value = "TasksReassignmentViewImpl.html")
public class TasksReassignmentViewImpl extends Composite implements TasksReassignmentPresenter.TasksReassignmentView {

    @Inject
    @DataField
    public Button reassignButton;

    @Inject
    @DataField
    public Button clearButton;

    @Inject
    @DataField
    public FormLabel userIdLabel;

    @Inject
    @DataField
    public TextBox userIdInput;

    @Inject
    @DataField
    public HelpBlock userIdHelpBlock;

    @Inject
    @DataField("userid-input-help")
    org.jboss.errai.common.client.dom.Button userIdInputHelp;

    @Inject
    @DataField
    private FormGroup userIdFormGroup;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    private Constants constants = GWT.create(Constants.class);

    private TasksReassignmentPresenter presenter;

    @Override
    public void init(TasksReassignmentPresenter presenter) {
        this.presenter = presenter;
        userIdInputHelp.setAttribute("data-content",
                                     getInputStringHelpHtml());

        jQueryPopover.wrap(userIdInputHelp).popover();
        userIdLabel.setText(constants.Delegate_User());
        reassignButton.setText(constants.Delegate());
        clearButton.setText(constants.Clear());
        userIdLabel.setShowRequiredIndicator(true);
        clearFields();
    }

    private String getInputStringHelpHtml() {
        return "<p>" + constants.To_Reassign_Selected_Tasks_Introduce_UserId() + "</p>\n";
    }

    @EventHandler("reassignButton")
    public void reassignTasks(ClickEvent e) {
        String userOrGroup = userIdInput.getText();
        if (!userOrGroup.isEmpty()) {
            presenter.reassignTasksToUser(userOrGroup);
            reassignButton.setEnabled(false);
        } else {
            showErrorMessage(constants.PleaseEnterUserIdToPerformDelegation());
        }
    }

    @EventHandler("clearButton")
    public void clearButton(ClickEvent e) {
        clearFields();
    }

    private void clearFields() {
        userIdInput.setValue("");
        userIdHelpBlock.setText("");
        userIdFormGroup.clearValidationState();
    }

    public void showErrorMessage(String text) {
        userIdFormGroup.setValidationState(ValidationState.ERROR);
        userIdHelpBlock.setText(text);
    }
}
