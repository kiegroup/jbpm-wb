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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "ProcessDefDetailsViewImpl.html")
public class ProcessDefDetailsViewImpl extends Composite implements ProcessDefDetailsPresenter.ProcessDefDetailsView {

    private Constants constants = GWT.create( Constants.class );

    @Inject
    private PlaceManager placeManager;

    @Inject
    @DataField
    public HTML processIdText;

    @Inject
    @DataField
    public HTML processNameText;

    @Inject
    @DataField
    public HTML nroOfHumanTasksText;

    @Inject
    @DataField
    public HTML humanTasksListBox;

    @Inject
    @DataField
    public HTML usersGroupsListBox;

    @Inject
    @DataField
    public HTML processDataListBox;

    @Inject
    @DataField
    public HTML processServicesListBox;

    @Inject
    @DataField
    public HTML subprocessListBox;

    @Inject
    @DataField
    public HTML deploymentIdText;

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
    @DataField
    public Label processServicesListLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Path processAssetPath;

    private String encodedProcessSource;

    @PostConstruct
    public void init() {
        processIdLabel.setText( constants.Process_Definition_Id() );
        processNameLabel.setText( constants.Process_Definition_Name() );
        nroOfHumanTasksLabel.setText( constants.Human_Tasks_Count() );
        deploymentIdLabel.setText( constants.Deployment_Name() );
        humanTasksListLabel.setText( constants.Human_Tasks() );
        usersGroupsListLabel.setText( constants.User_And_Groups() );
        subprocessListLabel.setText( constants.SubProcesses() );
        processDataListLabel.setText( constants.Process_Variables() );
        processServicesListLabel.setText( constants.Services() );
    }

    @Override
    public HTML getProcessNameText() {
        return processNameText;
    }

    @Override
    public HTML getNroOfHumanTasksText() {
        return nroOfHumanTasksText;
    }

    @Override
    public HTML getHumanTasksListBox() {
        return humanTasksListBox;
    }

    @Override
    public HTML getUsersGroupsListBox() {
        return usersGroupsListBox;
    }

    @Override
    public HTML getProcessDataListBox() {
        return processDataListBox;
    }

    @Override
    public HTML getProcessServicesListBox() {
        return processServicesListBox;
    }

    @Override
    public HTML getSubprocessListBox() {
        return subprocessListBox;
    }

    @Override
    public HTML getDeploymentIdText() {
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
    public HTML getProcessIdText() {
        return processIdText;
    }

    public Path getProcessAssetPath() {
        return processAssetPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }

}
