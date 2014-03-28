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
package org.jbpm.console.ng.ht.admin.client.editors.settings;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.admin.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAdminSettingsViewImpl.html")
public class TaskAdminSettingsViewImpl extends Composite implements TaskAdminSettingsPresenter.TaskAdminSettingsView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private TaskAdminSettingsPresenter presenter;

    @Inject
    @DataField
    public Button generateMockTasksButton;

    @Inject
    @DataField
    public Label userNameLabel;
    
    @Inject
    @DataField
    public TextBox userNameText;
    
     @Inject
    @DataField
    public Label amountOfTasksLabel;
    
    @Inject
    @DataField
    public TextBox amountOfTasksText;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(TaskAdminSettingsPresenter presenter) {
        this.presenter = presenter;
        
        amountOfTasksLabel.setText(constants.Amount_Of_Tasks());
        userNameLabel.setText(constants.User_Name());
        generateMockTasksButton.setText(constants.Generate_Mock_Tasks());
    }

    

    @EventHandler("generateMockTasksButton")
    public void generateMockTasksButton(ClickEvent e) {
        
            presenter.generateMockTasks(userNameText.getText(), Integer.parseInt(amountOfTasksText.getText()));
       
    }

   

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
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
