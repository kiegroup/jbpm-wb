/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.ht.client.editors.taskprocesscontext;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskProcessContextViewImpl.html")
public class TaskProcessContextViewImpl extends Composite implements TaskProcessContextPresenter.TaskProcessContextView {

    private TaskProcessContextPresenter presenter;

    @Inject
    @DataField
    public Button pIDetailsButton;

    @Inject
    @DataField
    public FormLabel processInstanceIdLabel;

    @Inject
    @DataField
    public FormLabel processIdLabel;

    @Inject
    @DataField
    public Paragraph processInstanceIdText;

    @Inject
    @DataField
    public Paragraph processIdText;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskProcessContextPresenter presenter ) {
        this.presenter = presenter;
        // Instance id
        processInstanceIdLabel.setText( constants.Process_Instance_Id() );

        //Process Id
        processIdLabel.setText( constants.Process_Definition_Id() );

        pIDetailsButton.setText( constants.Process_Instance_Details() );
    }

    @EventHandler("pIDetailsButton")
    public void pIDetailsButton( ClickEvent e ) {
        presenter.goToProcessInstanceDetails();
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @Override
    public void setProcessInstanceId( String piid ) {
        processInstanceIdText.setText( piid );
    }

    @Override
    public void setProcessId( String pid ) {
        processIdText.setText( pid );
    }

    @Override
    public void enablePIDetailsButton( boolean enable ) {
        pIDetailsButton.setEnabled( enable );
    }
}
