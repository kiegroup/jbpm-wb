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

package org.jbpm.console.ng.pr.client.editors.instance.log;

import com.github.gwtbootstrap.client.ui.Button;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "RuntimeLogViewImpl.html")
public class RuntimeLogViewImpl extends Composite 
                                implements RuntimeLogPresenter.RuntimeLogView {

    private RuntimeLogPresenter presenter;

    
    @Inject
    @DataField
    public TextBox processInstanceIdBox;

    @Inject
    @DataField
    public Label processInstanceIdLabel;
    
    
    @Inject
    @DataField
    public HTML logTextArea;

    @Inject
    @DataField
    public Label logTextLabel;
    
   

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;
    
    @Inject
    @DataField
    public Button getInstanceDataButton;


    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );
    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );


    @Override
    public void init( final RuntimeLogPresenter presenter ) {
        this.presenter = presenter;
        logTextLabel.setText( constants.Process_Instance_Log() );
        getInstanceDataButton.setText(constants.Get_Instance_Data());
        processInstanceIdLabel.setText(constants.Process_Instance_ID());

    }

    @EventHandler(value = "getInstanceDataButton")
    public void getInstanceData(ClickEvent e){
        presenter.refreshProcessInstanceData(Long.valueOf(processInstanceIdBox.getText()));
    }
    
    @Override
    public HTML getLogTextArea() {
        return logTextArea;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }


    
    
}
