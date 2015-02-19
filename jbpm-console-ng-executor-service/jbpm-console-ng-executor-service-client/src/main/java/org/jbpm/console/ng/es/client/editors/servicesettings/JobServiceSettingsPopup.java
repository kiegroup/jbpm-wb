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

package org.jbpm.console.ng.es.client.editors.servicesettings;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class JobServiceSettingsPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, JobServiceSettingsPopup> {

    }

    @UiField
    public IntegerBox numberOfExecutorsIntegerText;

    @UiField
    public TextBox frequencyText;

    @UiField
    public Label startedLabel;

    @UiField
    public ControlGroup numberOfExecutorsControlGroup;

    @UiField
    public ControlGroup frequencyControlGroup;

    @UiField
    public ControlGroup startedControlGroup;

    @UiField
    public HelpBlock frequencyHelpInline;

    @UiField
    public HelpBlock numberOfExecutorsHelpInline;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;

    final GenericModalFooter footer = new GenericModalFooter();

    private static Binder uiBinder = GWT.create( Binder.class );

    private boolean serviceStarted = false;

    public JobServiceSettingsPopup() {
        setTitle( Constants.INSTANCE.Job_Service_Settings() );

        add( uiBinder.createAndBindUi( this ) );

        footer.addButton( Constants.INSTANCE.Start(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, null,
                ButtonType.PRIMARY );

        add( footer );
    }

    public void show() {
        cleanForm();
        cleanErrorMessages();
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            startStopService();
        }
    }

    private void cleanErrorMessages() {
        numberOfExecutorsHelpInline.setText( "" );
        numberOfExecutorsControlGroup.setType( ControlGroupType.NONE );
        frequencyHelpInline.setText( "" );
        frequencyControlGroup.setType( ControlGroupType.NONE );
        errorMessages.setText( "" );
        errorMessagesGroup.setType( ControlGroupType.NONE );
    }

    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        cleanErrorMessages();
        if ( !serviceStarted ) {
            numberOfExecutorsControlGroup.setType( ControlGroupType.SUCCESS );
            frequencyControlGroup.setType( ControlGroupType.SUCCESS );

            if ( numberOfExecutorsIntegerText.getText() == null || numberOfExecutorsIntegerText.getText().trim().isEmpty() ) {
                numberOfExecutorsControlGroup.setType( ControlGroupType.ERROR );
                numberOfExecutorsHelpInline.setText( Constants.INSTANCE.Please_Provide_The_Number_Of_Executors() );
                valid = false;
            } else {
                 if (!(numberOfExecutorsIntegerText.getValue() != null && numberOfExecutorsIntegerText.getValue() > 0 )){
                    numberOfExecutorsControlGroup.setType( ControlGroupType.ERROR );
                    numberOfExecutorsHelpInline.setText( Constants.INSTANCE.Please_Provide_A_Valid_Number_Of_Executors() );
                    valid = false;
                }
            }
            if ( frequencyText.getText() == null || frequencyText.getText().trim().isEmpty() ) {
                frequencyControlGroup.setType( ControlGroupType.ERROR );
                frequencyHelpInline.setText( Constants.INSTANCE.Please_Provide_A_Valid_Frequency() );
                valid = false;
            } else {
                try{
                    if( fromFrequencyToInterval( frequencyText.getText())< 0){
                        throw new NumberFormatException();
                    }
                }catch (Exception e ){
                    frequencyControlGroup.setType( ControlGroupType.ERROR );
                    frequencyHelpInline.setText( Constants.INSTANCE.Please_Provide_A_Valid_Frequency() );
                }
            }
        }
        return valid;
    }


    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    public void cleanForm() {
        executorServices.call( new RemoteCallback<Integer>() {
            @Override
            public void callback( Integer interval ) {
                frequencyText.setText( fromIntervalToFrequency( interval ) );
            }
        } ).getInterval();
        executorServices.call( new RemoteCallback<Integer>() {
            @Override
            public void callback( Integer threadPoolSize ) {
                numberOfExecutorsIntegerText.setValue( threadPoolSize );
            }
        } ).getThreadPoolSize();
        executorServices.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean started ) {
                setStartedLabel( started );

                if ( started ) {
                    ( ( Button ) ( ( ModalFooter ) footer.getWidget( 0 ) ).getWidget( 0 ) ).setText( Constants.INSTANCE.Stop() );
                    frequencyText.setEnabled( false );
                    numberOfExecutorsIntegerText.setEnabled( false );
                } else {
                    frequencyText.setEnabled( true );
                    numberOfExecutorsIntegerText.setEnabled( true );
                    ( ( Button ) ( ( ModalFooter ) footer.getWidget( 0 ) ).getWidget( 0 ) ).setText( Constants.INSTANCE.Start() );
                }
            }
        } ).isActive();
    }


    public void startStopService() {
        Integer frequency = fromFrequencyToInterval( frequencyText.getText() );
        Integer numberOfExecutors = numberOfExecutorsIntegerText.getValue();

        executorServices.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean serviceStatus ) {
                displayNotification( serviceStatus ? Constants.INSTANCE.ServiceStarted() : Constants.INSTANCE.ServiceStopped() );
                closePopup();
            }
        } ).startStopService( frequency, numberOfExecutors );

    }

    public String fromIntervalToFrequency( Integer interval ) {
        int seconds = interval % 60;
        int minutes = ( interval / 60 ) % 60;
        int hours = ( interval / 3600 ) % 24;
        int days = ( interval / 86400 );
        StringBuilder frequencyText = new StringBuilder();
        if ( days > 0 ) {
            frequencyText.append( days ).append( "d " );
        }
        if ( hours > 0 ) {
            frequencyText.append( hours ).append( "h " );
        }
        if ( minutes > 0 ) {
            frequencyText.append( minutes ).append( "m " );
        }
        if ( seconds > 0 ) {
            frequencyText.append( seconds ).append( "s" );
        }
        return frequencyText.toString();
    }

    public Integer fromFrequencyToInterval( String frequency ) throws NumberFormatException {
        String[] sections = frequency.split( " " );
        int interval = 0;

        for ( String section : sections ) {
            if ( section.trim().endsWith( "d" ) ) {
                int value = Integer.parseInt( section.replace( "d", "" ) );
                interval += ( value * 86400 );
            } else if ( section.trim().endsWith( "h" ) ) {
                int value = Integer.parseInt( section.replace( "h", "" ) );
                interval += ( value * 3600 );
            } else if ( section.trim().endsWith( "m" ) ) {
                int value = Integer.parseInt( section.replace( "m", "" ) );
                interval += ( value * 60 );
            } else if ( section.trim().endsWith( "s" ) ) {
                int value = Integer.parseInt( section.replace( "s", "" ) );
                interval += value;
            } else {
                throw new NumberFormatException();
            }
        }
        return interval;
    }

    public void setStartedLabel( Boolean started ) {
        this.startedLabel.setText( started ? Constants.INSTANCE.Started() : Constants.INSTANCE.Stopped() );
        this.serviceStarted = started;
    }


}
