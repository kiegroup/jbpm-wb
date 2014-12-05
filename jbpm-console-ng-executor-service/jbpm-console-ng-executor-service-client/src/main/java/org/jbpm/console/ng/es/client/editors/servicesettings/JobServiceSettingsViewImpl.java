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

package org.jbpm.console.ng.es.client.editors.servicesettings;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent

public class JobServiceSettingsViewImpl extends Composite implements JobServiceSettingsPresenter.JobServiceSettingsView {

    private Constants constants = GWT.create( Constants.class );

     interface JobServiceSettingsViewWidgetBinder
            extends
            UiBinder<Widget, JobServiceSettingsViewImpl> {
    }
    
    private JobServiceSettingsViewWidgetBinder uiBinder = GWT.create(JobServiceSettingsViewWidgetBinder.class);
    
    @UiField
    public IntegerBox numberOfExecutorsText;

    @UiField
    public TextBox frequencyText;  

    @UiField
    public Button startStopButton;

    @UiField
    public Label startedLabel;
    
    @UiField
    public ControlGroup numberOfExecutorsControlGroup;
    
    @UiField
    public ControlGroup frequencyControlGroup;
    
    @UiField
    public ControlGroup startedControlGroup;
    
    @UiField
    public HelpInline frequencyHelpInline;
    
    @UiField
    public HelpInline numberOfExecutorsHelpInline;

    @Inject
    Event<NotificationEvent> notificationEvents;

    private JobServiceSettingsPresenter presenter;

    @Override
    public void init( JobServiceSettingsPresenter p ) {
        this.presenter = p;
        initWidget(uiBinder.createAndBindUi( this ) );
        
        
        this.presenter.init();
        
    }

    @UiHandler("startStopButton")
    public void startStopButton( ClickEvent e ) {
        Integer frequency = 0;   
        if(startedLabel.getText().equals("Stopped")){
            if(numberOfExecutorsText.getText() == null || 
                    numberOfExecutorsText.getText().equals("") || 
                    numberOfExecutorsText.getValue() < 0){
                numberOfExecutorsControlGroup.setType(ControlGroupType.ERROR);
                numberOfExecutorsHelpInline.setText(constants.Please_Provide_The_Number_Of_Executors());
                return;
            }else{
                numberOfExecutorsControlGroup.setType(ControlGroupType.SUCCESS);

            }
            try{
                if(frequencyText.getText() != null && !frequencyText.getText().equals("")){
                    frequency = presenter.fromFrequencyToInterval( frequencyText.getText() );
                    frequencyControlGroup.setType(ControlGroupType.SUCCESS);
                }else{
                    frequencyControlGroup.setType(ControlGroupType.ERROR);
                    frequencyHelpInline.setText(constants.Please_Provide_A_Valid_Frequency());
                    return;
                }
            }catch(NumberFormatException ex){
                frequencyControlGroup.setType(ControlGroupType.ERROR);
                frequencyHelpInline.setText(constants.Please_Provide_A_Valid_Frequency());
                return;
            }

            
        }
        
        presenter.initService( numberOfExecutorsText.getValue(), frequency );
    }

    @Override
    public void displayNotification( String notification ) {
        notificationEvents.fire( new NotificationEvent( notification ) );
    }

    

    @Override
    public void setFrequencyText( String frequency ) {
        this.frequencyText.setValue( frequency );
    }

    @Override
    public void setNumberOfExecutors( Integer numberOfExecutors ) {
        this.numberOfExecutorsText.setValue( numberOfExecutors );
    }

    @Override
    public void setStartedLabel( Boolean started ) {
        this.startedLabel.setText( started ? constants.Started() : constants.Stopped() );
    }

    @Override
    public void alert( String message ) {
        Window.alert( message ); // TODO improve??
    }

    @Override
    public Button getStartStopButton() {
        return startStopButton;
    }

    @Override
    public IntegerBox getNumberOfExecutorsText() {
        return numberOfExecutorsText;
    }

    @Override
    public TextBox getFrequencyText() {
        return frequencyText;
    }
    
    
    
}
