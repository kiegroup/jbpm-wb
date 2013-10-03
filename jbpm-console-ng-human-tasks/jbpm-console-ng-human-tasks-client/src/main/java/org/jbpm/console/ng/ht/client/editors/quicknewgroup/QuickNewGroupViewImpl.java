/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.ht.client.editors.quicknewgroup;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

@Dependent
@Templated(value = "QuickNewGroupViewImpl.html")
public class QuickNewGroupViewImpl extends Composite implements QuickNewGroupPresenter.QuickNewGroupView {

    private Constants constants = GWT.create(Constants.class);

    private QuickNewGroupPresenter presenter;

    @Inject
    public TextBox descriptionText;

    @Inject
    @DataField
    public Button addGroupButton;

    @Inject
    @DataField
    public ControlGroup descriptionControlGroup;

    @Inject
    public HelpInline descriptionHelpLabel;

    @Inject
    public ControlLabel groupLabel;

    @Inject
    @DataField
    public Label parentGroupLabel;

    @Inject
    @DataField
    public ListBox parentGroupList;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(QuickNewGroupPresenter presenter) {
        this.presenter = presenter;
        initializeHtml();
    }

    private void initializeHtml() {
        groupLabel.add(new HTMLPanel(constants.Group()));
        parentGroupLabel.setText(constants.Parent_Group());

        Controls descriptionControl = new Controls();
        descriptionControl.add(descriptionText);
        descriptionControl.add(descriptionHelpLabel);
        descriptionControlGroup.add(groupLabel);
        descriptionControlGroup.add(descriptionControl);

        addGroupButton.setText(constants.Create());

        initializeListParentGroup();
    }

    private void initializeListParentGroup() {
        presenter.loadGroups();
    }

    @EventHandler("addGroupButton")
    public void addGroupButton(ClickEvent e) {
        if (!descriptionText.getText().isEmpty()) {
            addGroup();
        } else {
            descriptionText.setFocus(true);
            descriptionText.setErrorLabel(descriptionHelpLabel);
            descriptionControlGroup.setType(ControlGroupType.ERROR);
            descriptionHelpLabel.setText(constants.Text_Require());
        }

    }

    private void addGroup() {
        presenter.addGroup();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public TextBox getDescriptionText() {
        return descriptionText;
    }

    @Override
    public ListBox getParentGroupList() {
        return parentGroupList;
    }

}
