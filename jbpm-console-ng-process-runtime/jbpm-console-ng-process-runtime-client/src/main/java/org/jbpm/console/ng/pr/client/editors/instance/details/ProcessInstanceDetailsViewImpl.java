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

package org.jbpm.console.ng.pr.client.editors.instance.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class ProcessInstanceDetailsViewImpl extends Composite implements
                                                              ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {
    interface ProcessInstanceDetailsViewImplBinder extends UiBinder<Widget, ProcessInstanceDetailsViewImpl> {

    }

    private static ProcessInstanceDetailsViewImplBinder uiBinder = GWT.create( ProcessInstanceDetailsViewImplBinder.class );

    @UiField
    public FormControlStatic processDefinitionIdText;

    @UiField
    FormGroup processDefinitionIdGroup;
    
    @UiField
    public FormControlStatic processDeploymentText;

    @UiField
    FormGroup processDeploymentGroup;
    
    @UiField
    public FormControlStatic processVersionText;

    @UiField
    FormGroup  processVersionGroup;
    
    @UiField
    public FormControlStatic correlationKeyText;

    @UiField
    FormGroup  correlationKeyGroup;

    @UiField
    public FormControlStatic parentProcessInstanceIdText;

    @UiField
    FormGroup  parentProcessInstanceIdGroup;
    
    @UiField
    public FormControlStatic stateText;
    
    @UiField
    FormGroup  stateGroup;

    @Inject
    private Event<NotificationEvent> notification;

    private Path processAssetPath;
    private String encodedProcessSource;

    @Override
    public FormControlStatic getProcessDefinitionIdText() {
        return processDefinitionIdText;
    }

    @Override
    public FormControlStatic getStateText() {
        return this.stateText;
    }

    @Override
    public FormControlStatic getProcessDeploymentText() {
        return processDeploymentText;
    }

    @Override
    public FormControlStatic getCorrelationKeyText() {
        return correlationKeyText;
    }

    @Override
    public FormControlStatic getParentProcessInstanceIdText() {
        return parentProcessInstanceIdText;
    }

    @Override
    public FormControlStatic getProcessVersionText() {
        return processVersionText;
    }

    @Override
    public void setProcessAssetPath( Path processAssetPath ) {
        this.processAssetPath = processAssetPath;
    }


    @Override
    public void setEncodedProcessSource( String encodedProcessSource ) {
        this.encodedProcessSource = encodedProcessSource;
    }

    public Path getProcessAssetPath() {
        return processAssetPath;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }


    @Override
    public void initLables() {
    }


    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    @PostConstruct
    public void init( ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        
    }
    
}
