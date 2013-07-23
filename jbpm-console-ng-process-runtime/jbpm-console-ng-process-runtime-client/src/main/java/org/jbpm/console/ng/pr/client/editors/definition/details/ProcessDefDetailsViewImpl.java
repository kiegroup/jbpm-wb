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

package org.jbpm.console.ng.pr.client.editors.definition.details;

import com.github.gwtbootstrap.client.ui.Heading;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessDefDetailsViewImpl.html")
public class ProcessDefDetailsViewImpl extends Composite implements ProcessDefDetailsPresenter.ProcessDefDetailsView {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    private ProcessDefDetailsPresenter presenter;

    @Inject
    @DataField
    public TextBox processIdText;
    
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
    public ListBox processDataListBox;

    @Inject
    @DataField
    public ListBox subprocessListBox;

    @Inject
    @DataField
    public TextBox deploymentIdText;

    @Inject
    public IconAnchor refreshIcon;

    @Inject
    @DataField
    public NavLink viewProcessInstancesButton;

    @Inject
    @DataField
    public NavLink createProcessInstanceButton;

    @Inject
    @DataField
    public NavLink openProcessDesignerButton;

    @DataField
    public Heading processDetailsLabel = new Heading(4);

    @Inject
    @DataField
    public Label processNameLabel;
    
    @Inject
    @DataField
    public Label processIdLabel;

    @Inject
    @DataField
    public Label nroOfHumanTasksLabel;

    @Inject
    @DataField
    public Label deploymentIdLabel;

    @Inject
    @DataField
    public Label humanTasksListLabel;

    @Inject
    @DataField
    public Label usersGroupsListLabel;

    @Inject
    @DataField
    public Label subprocessListLabel;

    @Inject
    @DataField
    public Label processDataListLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Path processAssetPath;

    private String encodedProcessSource;

    @Override
    public void init( final ProcessDefDetailsPresenter presenter ) {
        this.presenter = presenter;
        this.humanTasksListBox.setVisibleItemCount( 5 );
        this.humanTasksListBox.setEnabled( false );
        this.usersGroupsListBox.setVisibleItemCount( 5 );
        this.usersGroupsListBox.setEnabled( false );
        this.processDataListBox.setVisibleItemCount( 5 );
        this.processDataListBox.setEnabled( false );
        this.subprocessListBox.setEnabled(false);
        this.processIdText.setEnabled(false);
        this.processNameText.setEnabled( false );
        this.deploymentIdText.setEnabled( false );
        nroOfHumanTasksText.setEnabled( false );

        this.subprocessListBox.addDoubleClickHandler( new DoubleClickHandler() {
            @Override
            public void onDoubleClick( DoubleClickEvent event ) {
                ListBox source = (ListBox) event.getSource();
                String processId = source.getValue( source.getSelectedIndex() );
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Definition Details" );
                placeRequestImpl.addParameter( "processId", processId );
                placeManager.goTo( placeRequestImpl );
            }
        } );

        processIdLabel.setText( constants.Process_Definition_Id() );
        processNameLabel.setText( constants.Process_Definition_Name() );
        nroOfHumanTasksLabel.setText( constants.Human_Tasks_Count() );
        deploymentIdLabel.setText( constants.Deployment_Name() );
        humanTasksListLabel.setText( constants.Human_Tasks() );
        usersGroupsListLabel.setText( constants.User_And_Groups() );
        subprocessListLabel.setText( constants.SubProcesses() );
        processDataListLabel.setText( constants.Process_Variables() );

        refreshIcon.setTitle( constants.Refresh() );
        refreshIcon.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.refreshProcessDef( processNameText.getText() );
                displayNotification( constants.Process_Definition_Details_Refreshed() );
            }
        } );
        
        HTMLPanel span3 = new HTMLPanel(constants.Process_Definition_Details());
        span3.setStyleName("span3");
        processDetailsLabel.add(span3);
        refreshIcon.setCustomIconStyle("icon-jbpm-refresh");
        processDetailsLabel.add(refreshIcon);
        
        viewProcessInstancesButton.setText( constants.View_Process_Instances() );
        createProcessInstanceButton.setText( constants.New_Process_Instance() );
        openProcessDesignerButton.setText( constants.View_Process_Model() );

    }

    @EventHandler("createProcessInstanceButton")
    public void createProcessInstance( ClickEvent e ) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Form Display" );
        placeRequestImpl.addParameter( "processId", processIdText.getText() );
        placeRequestImpl.addParameter( "domainId", deploymentIdText.getText() );
        placeManager.goTo( placeRequestImpl );
    }

    @EventHandler("viewProcessInstancesButton")
    public void viewProcessInstancesButton( ClickEvent e ) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Process Instance List" );
        placeRequestImpl.addParameter( "processName", processNameText.getText() );
        placeManager.goTo( placeRequestImpl );

    }

    @EventHandler("openProcessDesignerButton")
    public void openProcessDesignerButton( ClickEvent e ) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Designer" );

        if ( encodedProcessSource != null ) {
            placeRequestImpl.addParameter( "readOnly", "true" );
            placeRequestImpl.addParameter( "encodedProcessSource", encodedProcessSource );
        }
        placeManager.goTo( processAssetPath, placeRequestImpl );

    }

    @Override
    public TextBox getProcessNameText() {
        return processNameText;
    }

    @Override
    public TextBox getNroOfHumanTasksText() {
        return nroOfHumanTasksText;
    }

    @Override
    public ListBox getHumanTasksListBox() {
        return humanTasksListBox;
    }

    @Override
    public ListBox getUsersGroupsListBox() {
        return usersGroupsListBox;
    }

    @Override
    public ListBox getProcessDataListBox() {
        return processDataListBox;
    }

    @Override
    public ListBox getSubprocessListBox() {
        return subprocessListBox;
    }

    @Override
    public TextBox getDeploymentIdText() {
        return deploymentIdText;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void setProcessAssetPath( Path processAssetPath ) {
        this.processAssetPath = processAssetPath;
    }

    @Override
    public void setEncodedProcessSource( String encodedProcessSource ) {
        this.encodedProcessSource = encodedProcessSource;
    }

    @Override
    public TextBox getProcessIdText() {
        return processIdText;
    }

}
