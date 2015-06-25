/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.ht.client.editors.taskprocesscontext;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "TaskProcessContextViewImpl.html")
public class TaskProcessContextViewImpl extends Composite implements TaskProcessContextPresenter.TaskProcessContextView {
    
    private TaskProcessContextPresenter presenter;
    
    @Inject
    @DataField
    public Label processContextLabel;
    
    @Inject
    @DataField
    public Button pIDetailsButton;
    
    @Inject
    @DataField
    public ControlLabel processInstanceIdLabel;

    @Inject
    @DataField
    public ControlLabel processIdLabel;

    @Inject
    @DataField
    public ControlLabel pIDetailsLabel;
    
    @Inject
    @DataField
    public TextBox processInstanceIdText;

    @Inject
    @DataField
    public TextBox processIdText;
    
    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );
    
    
    @Override
    public void init( TaskProcessContextPresenter presenter ) {
        this.presenter = presenter;
        processInstanceIdLabel.add( new HTMLPanel( constants.Process_Instance_Id() ) );
        processIdLabel.add( new HTMLPanel( constants.Process_Definition_Id() ) );
        pIDetailsLabel.add( new HTMLPanel( constants.Process_Instance_Details() ) );
        processContextLabel.setText( constants.Process_Context() );
        processContextLabel.setStyleName( "" );
        
        pIDetailsButton.setText( constants.Process_Instance_Details() );
        
        processIdText.setReadOnly(true);
    }
    
    @EventHandler("pIDetailsButton")
    public void pIDetailsButton( ClickEvent e ) {
        presenter.goToProcessInstanceDetails();
    }


    @Override
    public TextBox getProcessInstanceIdText() {
        return this.processInstanceIdText;
    }

    @Override
    public TextBox getProcessIdText() {
        return this.processIdText;
    }

    @Override
    public Button getpIDetailsButton() {
        return this.pIDetailsButton;
    }
    
    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }
}
