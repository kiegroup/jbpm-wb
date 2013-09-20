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

import com.github.gwtbootstrap.client.ui.Button;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAssignmentsViewImpl.html")
public class TaskAssignmentsViewImpl extends Composite implements TaskAssignmentsPresenter.TaskAssignmentsView {

    private TaskAssignmentsPresenter presenter;

    
    @Inject
    @DataField
    public ControlLabel detailsAccordionLabel;

    @Inject
    private PlaceManager placeManager;

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
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskAssignmentsPresenter presenter ) {
        this.presenter = presenter;

        userOrGroupLabel.setText(constants.UserOrGroup());
        detailsAccordionLabel.add( new HTMLPanel( constants.Details()) );
        delegateButton.setText(constants.Delegate());
        usersGroupsControlsLabel.setText(constants.Potential_Owners());
        usersGroupsControlsPanel.setStyleName("");

    }

    @EventHandler("delegateButton")
    public void delegateButton( ClickEvent e ) {
        presenter.delegateTask( userOrGroupText.getText());
    }

    @Override
    public Label getUsersGroupsControlsPanel() {
        return usersGroupsControlsPanel;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }


}
