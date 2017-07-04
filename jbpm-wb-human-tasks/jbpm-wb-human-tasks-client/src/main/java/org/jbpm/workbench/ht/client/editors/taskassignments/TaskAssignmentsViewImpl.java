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
package org.jbpm.workbench.ht.client.editors.taskassignments;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated(value = "TaskAssignmentsViewImpl.html")
public class TaskAssignmentsViewImpl extends Composite implements TaskAssignmentsPresenter.TaskAssignmentsView {

    @Inject
    @DataField
    public FormLabel userOrGroupLabel;

    @Inject
    @DataField
    public FormLabel usersGroupsControlsLabel;

    @Inject
    @DataField
    public TextBox userOrGroupText;

    @Inject
    @DataField
    public Button delegateButton;

    @Inject
    @DataField
    public UnorderedList usersGroupsControlsPanel;

    @Inject
    @DataField
    public HelpBlock userOrGroupHelpBlock;

    private TaskAssignmentsPresenter presenter;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(TaskAssignmentsPresenter presenter) {
        this.presenter = presenter;

        userOrGroupLabel.setText(constants.Delegate_User());
        delegateButton.setText(constants.Delegate());
        usersGroupsControlsLabel.setText(constants.Potential_Owners());
        userOrGroupHelpBlock.setText("");
    }

    @EventHandler("delegateButton")
    public void delegateButton(ClickEvent e) {
        String userOrGroup = userOrGroupText.getText();
        presenter.delegateTask(userOrGroup);
    }

    @Override
    public void setPotentialOwnersInfo(final List<String> owners) {
        removeAllChildren(usersGroupsControlsPanel);
        owners.forEach(owner -> {
            HTMLElement li = getDocument().createElement("li");
            li.setTextContent(owner);
            usersGroupsControlsPanel.appendChild(li);
        });
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void clearUserOrGroupInput() {
        userOrGroupText.clear();
    }

    @Override
    public void setHelpText(String text) {
        userOrGroupHelpBlock.setText(text);
    }

    @Override
    public void enableDelegateButton(boolean enable) {
        delegateButton.setEnabled(enable);
    }

    @Override
    public void enableUserOrGroupInput(boolean enable) {
        userOrGroupText.setEnabled(enable);
    }

    @Override
    public void setDelegateButtonActive(boolean enable) {
        delegateButton.setActive(enable);
    }
}
