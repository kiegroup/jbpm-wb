/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.client.editors.process.definition.details.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value = "ProcessDefDetailsViewImpl.html")
public class ProcessDefDetailsViewImpl extends Composite
        implements
        ProcessDefDetailsPresenter.InboxView {

    private ProcessDefDetailsPresenter presenter;
    @Inject
    @DataField
    public TextBox processNameText;
    @Inject
    @DataField
    public TextBox nroOfHumanTasksText;
    @Inject
    @DataField
    public ListBox humanTasksListBox;
    @Inject
    @DataField
    public ListBox usersGroupsListBox;
    @Inject
    @DataField
    public ListBox startDataListBox;
    @Inject
    @DataField
    public Button refreshButton;
    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(ProcessDefDetailsPresenter presenter) {
        this.presenter = presenter;
        this.humanTasksListBox.setVisibleItemCount(5);
        this.usersGroupsListBox.setVisibleItemCount(5);
        this.startDataListBox.setVisibleItemCount(5);
    }

    @EventHandler("refreshButton")
    public void refreshButton(ClickEvent e) {

        displayNotification(processNameText.getText());
    }

    public TextBox getProcessNameText() {
        return processNameText;
    }

    public TextBox getNroOfHumanTasksText() {
        return nroOfHumanTasksText;
    }

    public ListBox getHumanTasksListBox() {
        return humanTasksListBox;
    }

    public ListBox getUsersGroupsListBox() {
        return usersGroupsListBox;
    }

    public ListBox getStartDataListBox() {
        return startDataListBox;
    }
    
    

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
}
