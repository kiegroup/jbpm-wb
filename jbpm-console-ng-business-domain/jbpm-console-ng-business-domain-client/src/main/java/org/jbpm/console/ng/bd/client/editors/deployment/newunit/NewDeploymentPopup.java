/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.console.ng.bd.client.editors.deployment.newunit;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.events.DeployedUnitChangedEvent;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewDeploymentPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, NewDeploymentPopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public TabListItem basicTab;

    @UiField
    public TabListItem advancedTab;

    @UiField
    public TabPane basicTabPane;

    @UiField
    public TabPane advancedTabPane;

    @UiField
    public TextBox groupText;

    @UiField
    public HelpBlock groupTextErrorMessage;

    @UiField
    public FormGroup groupControlGroup;

    @UiField
    public TextBox artifactText;

    @UiField
    public HelpBlock artifactTextErrorMessage;

    @UiField
    public FormGroup artifactControlGroup;

    @UiField
    public TextBox versionText;

    @UiField
    public HelpBlock versionTextErrorMessage;

    @UiField
    public FormGroup versionControlGroup;

    @UiField
    public TextBox kbaseNameText;

    @UiField
    public TextBox kieSessionNameText;

    @UiField
    public ListBox strategyListBox;

    @UiField
    public ListBox mergeModeListBox;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<DeployedUnitChangedEvent> unitChanged;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    private static Binder uiBinder = GWT.create( Binder.class );

    public NewDeploymentPopup() {
        setTitle( Constants.INSTANCE.New_Deployment_Unit() );

        setBody( uiBinder.createAndBindUi( this ) );

        basicTab.setDataTargetWidget( basicTabPane );
        advancedTab.setDataTargetWidget( advancedTabPane );

        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Deploy_Unit(),
                          new Command() {
                              @Override
                              public void execute() {
                                  okButton();
                              }
                          }, IconType.PLUS,
                          ButtonType.PRIMARY );

        add( footer );
    }

    public void show() {
        cleanForm();
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            String strategy = strategyListBox.getValue( strategyListBox.getSelectedIndex() );
            String mergeMode = mergeModeListBox.getValue( mergeModeListBox.getSelectedIndex() );

            deployUnit( groupText.getText(), artifactText.getText(), versionText.getText(),
                        kbaseNameText.getText(), kieSessionNameText.getText(), strategy, mergeMode );
        }
    }

    public void init() {
        cleanForm();

        if ( strategyListBox == null ) {
            strategyListBox = new ListBox();
        }
        strategyListBox.addItem( Constants.INSTANCE.Singleton(), "SINGLETON" );
        strategyListBox.addItem( Constants.INSTANCE.Request(), "PER_REQUEST" );
        strategyListBox.addItem( Constants.INSTANCE.ProcessInstance(), "PER_PROCESS_INSTANCE" );

        if ( mergeModeListBox == null ) {
            mergeModeListBox = new ListBox();
        }
        mergeModeListBox.addItem( Constants.INSTANCE.MergeCollections(), "MERGE_COLLECTIONS" );
        mergeModeListBox.addItem( Constants.INSTANCE.KeepAll(), "KEEP_ALL" );
        mergeModeListBox.addItem( Constants.INSTANCE.OverrideAll(), "OVERRIDE_ALL" );
        mergeModeListBox.addItem( Constants.INSTANCE.OverrideEmpty(), "OVERRIDE_EMPTY" );
    }

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public void cleanForm() {
        basicTab.showTab();
        basicTab.setActive( true );
        advancedTab.setActive( false );

        groupTextErrorMessage.setText( "" );
        groupControlGroup.setValidationState( ValidationState.NONE );

        artifactTextErrorMessage.setText( "" );
        artifactControlGroup.setValidationState( ValidationState.NONE );

        versionTextErrorMessage.setText( "" );
        versionControlGroup.setValidationState( ValidationState.NONE );

        errorMessages.setText( "" );
        errorMessagesGroup.setValidationState( ValidationState.NONE );

        this.artifactText.setText( "" );
        this.groupText.setText( "" );
        this.versionText.setText( "" );
        this.kbaseNameText.setText( "" );
        this.kieSessionNameText.setText( "" );

    }

    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        if ( groupText.getText() != null && groupText.getText().trim().length() == 0 ) {
            groupControlGroup.setValidationState( ValidationState.ERROR );
            groupTextErrorMessage.setText( Constants.INSTANCE.ShouldProvide( Constants.INSTANCE.GroupID() ) );
            valid = false;
        } else {
            groupControlGroup.setValidationState( ValidationState.SUCCESS );
            groupTextErrorMessage.setText( "" );
        }
        if ( artifactText.getText() != null && artifactText.getText().trim().length() == 0 ) {
            artifactControlGroup.setValidationState( ValidationState.ERROR );
            artifactTextErrorMessage.setText( Constants.INSTANCE.ShouldProvide( Constants.INSTANCE.Artifact() ) );
            valid = false;
        } else {
            artifactControlGroup.setValidationState( ValidationState.SUCCESS );
            artifactTextErrorMessage.setText( "" );
        }
        if ( versionText.getText() != null && versionText.getText().trim().length() == 0 ) {
            versionControlGroup.setValidationState( ValidationState.ERROR );
            versionTextErrorMessage.setText( Constants.INSTANCE.ShouldProvide( Constants.INSTANCE.Version() ) );
            valid = false;
        } else {
            versionControlGroup.setValidationState( ValidationState.SUCCESS );
            versionTextErrorMessage.setText( "" );
        }
        if ( !valid ) {
            basicTab.showTab();
        }
        return valid;
    }

    public void deployUnit( final String group,
                            final String artifact,
                            final String version,
                            final String kbaseName,
                            final String kieSessionName,
                            final String strategy,
                            final String mergeMode ) {
        showBusyIndicator( Constants.INSTANCE.Please_Wait() );

        deploymentManager.call( new RemoteCallback<Void>() {
                                    @Override
                                    public void callback( Void nothing ) {
                                        cleanForm();
                                        hideBusyIndicator();
                                        displayNotification( " Kjar Deployed " + group + ":" + artifact + ":" + version );
                                        unitChanged.fire( new DeployedUnitChangedEvent() );
                                        closePopup();
                                    }
                                }, new ErrorCallback<Message>() {
                                    @Override
                                    public boolean error( Message message,
                                                          Throwable throwable ) {
                                        //cleanForm();
                                        hideBusyIndicator();
                                        //closePopup();
                                        errorMessagesGroup.setValidationState( ValidationState.ERROR );
                                        errorMessages.setText( Constants.INSTANCE.UnableCreateDeploymentUnit() );

                                        //displayNotification( Constants.INSTANCE.DeploymentFailed() );
                                        return true;
                                    }
                                }
                              ).deploy( new KModuleDeploymentUnitSummary( group + ":" + artifact + ":" + version, group, artifact, version, kbaseName, kieSessionName, strategy, mergeMode ) );
    }

}
