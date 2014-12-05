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
import com.github.gwtbootstrap.client.ui.IntegerBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.shared.GWT;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Focusable;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "Job Service Settings")
public class JobServiceSettingsPresenter {

    public interface JobServiceSettingsView extends UberView<JobServiceSettingsPresenter> {

        IntegerBox getNumberOfExecutorsText();

        void displayNotification( String notification );

        void setFrequencyText( String frequency );

        void setNumberOfExecutors( Integer numberOfExecutors );

        void setStartedLabel( Boolean started );

        void alert( String message );
        
        Button getStartStopButton();
        
        TextBox getFrequencyText();
    }

    @Inject
    private PlaceManager placeManager;
    @Inject
    JobServiceSettingsView view;
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    private PlaceRequest place;
    
    private Constants constants = GWT.create( Constants.class );

    public JobServiceSettingsPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Job Service Settings";
    }

    @WorkbenchPartView
    public UberView<JobServiceSettingsPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
        executorServices.call( new RemoteCallback<Integer>() {
            @Override
            public void callback( Integer interval ) {
                view.setFrequencyText( fromIntervalToFrequency( interval ) );
            }
        } ).getInterval();
        executorServices.call( new RemoteCallback<Integer>() {
            @Override
            public void callback( Integer threadPoolSize ) {
                view.setNumberOfExecutors( threadPoolSize );
            }
        } ).getThreadPoolSize();
        executorServices.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean started ) {
                view.setStartedLabel( started );
                 if(started){
                        view.getStartStopButton().setText(constants.Stop());
                        view.getFrequencyText().setEnabled(false);
                        view.getNumberOfExecutorsText().setEnabled(false);
                    }else{
                        view.getStartStopButton().setText(constants.Start());
                        view.getFrequencyText().setEnabled(true);
                        view.getNumberOfExecutorsText().setEnabled(true);
                    }
            }
        } ).isActive();
    }

    public void initService( final Integer numberOfExecutors,
                             Integer frequency ) {
        
            
            executorServices.call( new RemoteCallback<Boolean>() {
                @Override
                public void callback( Boolean serviceStatus ) {
                    view.displayNotification( serviceStatus ? "Service started" : "Service stopped" );
                    if(serviceStatus){
                        view.getStartStopButton().setText(constants.Stop());
                        view.getFrequencyText().setEnabled(false);
                        view.getNumberOfExecutorsText().setEnabled(false);
                    }else{
                        view.getFrequencyText().setEnabled(true);
                        view.getNumberOfExecutorsText().setEnabled(true);
                        view.getStartStopButton().setText(constants.Start());
                    }
                    placeManager.closePlace( place );
                }
            } ).startStopService( frequency, numberOfExecutors );
        
    }

    @OnOpen
    public void onOpen() {
        view.getNumberOfExecutorsText().setFocus( true );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
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
            }
        }
        return interval;
    }

}
