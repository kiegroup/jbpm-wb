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

package org.jbpm.console.ng.bd.client.editors.deployment.newunit;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;


import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.bd.client.i18n.Constants;

import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "NewDeploymentViewImpl.html")
public class NewDeploymentViewImpl extends Composite implements NewDeploymentPresenter.NewDeploymentView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private NewDeploymentPresenter presenter;


    @Inject
    @DataField
    public TextBox groupText;

    @Inject
    @DataField
    public TextBox artifactText;

    @Inject
    @DataField
    public TextBox versionText;

    @Inject
    @DataField
    public TextBox kbaseNameText;

    @Inject
    @DataField
    public TextBox kieSessionNameText;

    @Inject
    @DataField
    public Button deployUnitButton;

    @Inject
    @DataField
    public Label groupLabel;

    @Inject
    @DataField
    public Label artifactLabel;

    @Inject
    @DataField
    public Label versionLabel;
    
    @Inject
    @DataField
    public Label advancedLabel;

    @Inject
    @DataField
    public Label kbaseNameLabel;

    @Inject
    @DataField
    public Label kieSessionNameLabel;

    @Inject
    private Event<NotificationEvent> notification;


    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( NewDeploymentPresenter presenter ) {
        this.presenter = presenter;

        deployUnitButton.setText( constants.Deploy_Unit() );
        groupLabel.setText( constants.GroupID() );
        artifactLabel.setText( constants.Artifact() );
        versionLabel.setText( constants.Version() );
        kbaseNameLabel.setText( constants.KieBaseName() );
        kieSessionNameLabel.setText( constants.KieSessionName() );
        advancedLabel.setText(constants.KIE_Configurations());
    }

    @EventHandler("deployUnitButton")
    public void deployUnitButton( ClickEvent e ) {
        presenter.deployUnit(groupText.getText(), artifactText.getText(), versionText.getText(),
                              kbaseNameText.getText(), kieSessionNameText.getText() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
        
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void cleanForm() {
        this.artifactText.setText( "" );
        this.groupText.setText( "" );
        this.versionText.setText( "" );
        this.kbaseNameText.setText( "" );
        this.kieSessionNameText.setText( "" );
    }

    public TextBox getGroupText() {
        return groupText;
    }

    
}
