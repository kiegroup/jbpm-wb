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

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskAssignmentsPopupViewImpl.html")
public class TaskAssignmentsPopupViewImpl extends Composite implements TaskAssignmentsPopupPresenter.TaskAssignmentsPopupView {

    private TaskAssignmentsPopupPresenter presenter;

    @Inject
    @DataField
    public Label taskIdText;

    @Inject
    @DataField
    public Label taskNameText;

    @Inject
    @DataField
    public FlowPanel usersGroupsControlsPanel;

    @Inject
    @DataField
    public Button updateTaskButton;

    @Inject
    @DataField
    public ControlLabel descriptionAccordionLabel;

    @Inject
    private PlaceManager placeManager;

    @Inject
    @DataField
    public UnorderedList navBarUL;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskAssignmentsPopupPresenter presenter ) {
        this.presenter = presenter;

        descriptionAccordionLabel.add( new HTMLPanel( constants.Description() ) );
        
        updateTaskButton.setText( constants.Update() );
    }

    @EventHandler("updateTaskButton")
    public void updateTaskButton( ClickEvent e ) {
        
    }

    @Override
    public FlowPanel getUsersGroupsControlsPanel() {
        return usersGroupsControlsPanel;
    }


    @Override
    public Label getTaskIdText() {
        return taskIdText;
    }

    @Override
    public Label getTaskNameText() {
        return taskNameText;
    }


    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

   
    @Override
    public UnorderedList getNavBarUL() {
        return navBarUL;
    }

}
