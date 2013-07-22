/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.client.editors.variables.edit;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "VariableEditViewImpl.html")
public class VariableEditViewImpl extends Composite implements VariableEditPresenter.PopupView {

    private Constants constants = GWT.create( Constants.class );

    private long processInstanceId;
    private String variableId;
    private String variableText;

    private VariableEditPresenter presenter;

    @Inject
    @DataField
    public TextBox variableTextBox;

    @Inject
    @DataField
    public TextBox variableIdLabel;

    @Inject
    @DataField
    public Label variableIdUILabel;

    @Inject
    @DataField
    public Label variableTextLabel;

    @Inject
    @DataField
    public Button saveButton;

    @Inject
    @DataField
    public Button clearButton;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init( VariableEditPresenter presenter ) {
        this.presenter = presenter;
        clearButton.setText( constants.Clear() );
        saveButton.setText( constants.Save() );
        variableIdLabel.setReadOnly(true);
        variableIdUILabel.setText( constants.Variables_Name() );
        variableTextLabel.setText( constants.Variable_Value() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void setProcessInstanceId( long processInstanceId ) {
        this.processInstanceId = processInstanceId;

    }

    @Override
    public long getProcessInstanceId() {
        return this.processInstanceId;
    }

    @Override
    public String getVariableText() {
        return this.variableText;
    }

    @Override
    public void setVariableText( String value ) {
        this.variableText = value;
        this.variableTextBox.setText( value );
    }

    @Override
    public void setVariableId( String variableId ) {
        this.variableId = variableId;
    }

    @Override
    public String getVariableId() {
        return this.variableId;
    }

    @EventHandler("clearButton")
    public void clearButton( ClickEvent e ) {
        variableTextBox.setValue( "" );
    }

    @EventHandler("saveButton")
    public void saveButton( ClickEvent e ) {
        // TODO do not hardcode business key for session
        presenter.setProcessVariable( variableTextBox.getText() );
        displayNotification( "Variable updated " + variableId );
    }

    @Override
    public void setVariableIdLabel( String variableId ) {
        variableIdLabel.setText( variableId );
    }

}
