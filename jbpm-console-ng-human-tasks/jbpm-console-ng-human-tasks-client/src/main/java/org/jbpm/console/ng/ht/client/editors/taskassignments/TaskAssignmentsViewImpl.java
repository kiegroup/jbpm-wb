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

package org.jbpm.console.ng.ht.client.editors.taskassignments;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAssignmentsViewImpl.html")
public class TaskAssignmentsViewImpl extends Composite implements TaskAssignmentsPresenter.TaskAssignmentsView {

    private TaskAssignmentsPresenter presenter;

    @Inject
    @DataField
    public FormLabel assignmentsAccordionLabel;

    @Inject
    @DataField
    public Label userOrGroupLabel;

    @Inject
    @DataField
    public Label usersGroupsControlsLabel;

    @Inject
    @DataField
    public TextBox userOrGroupText;

    @Inject
    @DataField
    public Button delegateButton;

    @Inject
    @DataField
    public Label usersGroupsControlsPanel;

    @Inject
    @DataField
    public HelpBlock userOrGroupHelpBlock;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskAssignmentsPresenter presenter ) {
        this.presenter = presenter;

        userOrGroupLabel.setText( constants.Delegate_User() );
        assignmentsAccordionLabel.setText( constants.Assignments() );
        delegateButton.setText( constants.Delegate() );
        usersGroupsControlsLabel.setText( constants.Potential_Owners() );
        usersGroupsControlsPanel.setStyleName( "" );
        userOrGroupHelpBlock.setText( "" );
    }

    @EventHandler("delegateButton")
    public void delegateButton( ClickEvent e ) {
        String userOrGroup = userOrGroupText.getText();
        if ( userOrGroup.equals( "" ) ) {
            userOrGroupHelpBlock.setText( Constants.INSTANCE.DelegationUserInputRequired() );
        } else {
            presenter.delegateTask( userOrGroup );
        }
    }

    @Override
    public Label getUsersGroupsControlsPanel() {
        return usersGroupsControlsPanel;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public Button getDelegateButton() {
        return delegateButton;
    }

    @Override
    public TextBox getUserOrGroupText() {
        return userOrGroupText;
    }

    @Override
    public HelpBlock getUserOrGroupHelpBlock() {
        return userOrGroupHelpBlock;
    }

}
