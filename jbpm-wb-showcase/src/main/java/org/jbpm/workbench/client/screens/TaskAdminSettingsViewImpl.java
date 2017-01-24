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
package org.jbpm.workbench.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.client.i18n.TaskAdminConstants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAdminSettingsViewImpl.html")
public class TaskAdminSettingsViewImpl extends Composite implements TaskAdminSettingsPresenter.TaskAdminSettingsView {

    private TaskAdminSettingsPresenter presenter;

    @Inject
    @DataField
    public Button generateMockTasksButton;

    @Inject
    @DataField
    public FormLabel userNameLabel;

    @Inject
    @DataField
    public TextBox userNameText;

    @Inject
    @DataField
    public FormLabel amountOfTasksLabel;

    @Inject
    @DataField
    public TextBox amountOfTasksText;

    @Inject
    private Event<NotificationEvent> notification;

    private TaskAdminConstants constants = GWT.create( TaskAdminConstants.class );

    @Override
    public void init( TaskAdminSettingsPresenter presenter ) {
        this.presenter = presenter;

        amountOfTasksLabel.setText( constants.Amount_Of_Tasks() );
        userNameLabel.setText( constants.User_Name() );
        generateMockTasksButton.setText( constants.Generate_Mock_Tasks() );
    }

    @EventHandler("generateMockTasksButton")
    public void generateMockTasksButton( ClickEvent e ) {
        presenter.generateMockTasks( userNameText.getText(), Integer.parseInt( amountOfTasksText.getText() ) );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public TextBox getUserNameText() {
        return userNameText;
    }

    @Override
    public Button getGenerateMockTasksButton() {
        return generateMockTasksButton;
    }

}
