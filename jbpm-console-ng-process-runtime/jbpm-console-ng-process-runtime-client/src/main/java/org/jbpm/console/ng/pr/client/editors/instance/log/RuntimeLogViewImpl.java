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
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogOrder;
import org.jbpm.console.ng.pr.client.util.LogUtils.LogType;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "RuntimeLogViewImpl.html")
public class RuntimeLogViewImpl extends Composite 
                                implements RuntimeLogPresenter.RuntimeLogView {

    private RuntimeLogPresenter presenter;
    private LogOrder logOrder = LogOrder.ASC;
    private LogType logType = LogType.BUSINESS;
    
    @Inject
    @DataField
    public HTML logTextArea;

    @Inject
    @DataField
    public Label logTextLabel;
    
    @Inject
    @DataField
    public Button showBusinessLogButton;
    
    @Inject
    @DataField
    public Button showTechnicalLogButton;
    
    @Inject
    @DataField
    public Button showAscLogButton;
    
    @Inject
    @DataField
    public Button showDescLogButton;
     
    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );
    private ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );


    @Override
    public void init( final RuntimeLogPresenter presenter ) {
        this.presenter = presenter;
        logTextLabel.setText( constants.Process_Instance_Log() );  
        
        this.setFilters(showBusinessLogButton, constants.Business_Log(), LogType.BUSINESS);
        this.setFilters(showTechnicalLogButton, constants.Technical_Log(), LogType.TECHNICAL);
        this.setOrder(showAscLogButton, constants.Asc_Log_Order(), LogOrder.ASC);
        this.setOrder(showDescLogButton, constants.Desc_Log_Order(), LogOrder.DESC);
    }
    
    private void setFilters(Button button, String description, final LogType logType) {
        button.setSize(ButtonSize.SMALL);
        button.setText(description);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveLogTypeButton(logType);
                getInstanceData();
            }
        });
    }
    
    private void setOrder(Button button, String description, final LogOrder logOrder) {
        button.setSize(ButtonSize.SMALL);
        button.setText(description);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveLogOrderButton(logOrder);
                getInstanceData();
            }
        });
    }

    public void getInstanceData(){
        presenter.refreshProcessInstanceData(logOrder, logType);
    }
    
    @Override
    public HTML getLogTextArea() {
        return logTextArea;
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void setActiveLogTypeButton(LogType logType) {
        showBusinessLogButton.setStyleName("btn btn-small");
        showTechnicalLogButton.setStyleName("btn btn-small");
        this.logType = logType;
        switch (logType) {
            case TECHNICAL:
                showTechnicalLogButton.setStyleName(showTechnicalLogButton.getStyleName() + " active");                
                break;
            case BUSINESS:
                showBusinessLogButton.setStyleName(showBusinessLogButton.getStyleName() + " active");
                break;
        }            
    }
    
    public void setActiveLogOrderButton(LogOrder logOrder) {
        showAscLogButton.setStyleName("btn btn-small");
        showDescLogButton.setStyleName("btn btn-small");
        this.logOrder = logOrder;
        switch (logOrder) {
            case ASC:
                showAscLogButton.setStyleName(showAscLogButton.getStyleName() + " active");                
                break;
            case DESC:
                showDescLogButton.setStyleName(showDescLogButton.getStyleName() + " active");                
                break;
        }
    }
    
}
