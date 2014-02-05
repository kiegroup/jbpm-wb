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

package org.jbpm.console.ng.pr.client.editors.instance.signal;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import java.util.List;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Signal Process Popup")
public class ProcessInstanceSignalPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface PopupView extends UberView<ProcessInstanceSignalPresenter> {

        void displayNotification( String text );

        void addProcessInstanceId( long processInstanceId );

        String getSignalRefText();

        String getEventText();

        void setAvailableSignals( Collection<String> signals );

    }

    @Inject
    private PopupView view;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;
    
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @PostConstruct
    public void init() {

    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Signaling_Process_Instance();
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceSignalPresenter> getView() {
        return view;
    }

    public void signalProcessInstances( List<Long> processInstanceIds ) {

        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                processInstancesUpdatedEvent.fire(new ProcessInstancesUpdateEvent(0L));
                close();
                
            }
        }, new ErrorCallback<Message>() {
             @Override
             public boolean error( Message message, Throwable throwable ) {
                 ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                 return true;
             }
         } ).signalProcessInstances( processInstanceIds, view.getSignalRefText(), view.getEventText() );
    }

    @OnOpen
    public void onOpen() {
        String processInstanceIds = place.getParameter( "processInstanceId", "-1" ).toString();
        String[] ids = processInstanceIds.split( "," );
        for ( String id : ids ) {
            long processInstanceId = Long.parseLong( id );
            view.addProcessInstanceId( processInstanceId );
        }

        // for single process instance load available signals
        if ( ids.length == 1 && Long.parseLong( ids[ 0 ] ) != -1 ) {
            getAvailableSignals( Long.parseLong( ids[ 0 ] ) );
        }
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void getAvailableSignals( long processInstanceId ) {
        kieSessionServices.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> signals ) {
                view.setAvailableSignals( signals );

            }
        }, new ErrorCallback<Message>() {
             @Override
             public boolean error( Message message, Throwable throwable ) {
                 ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                 return true;
             }
         } ).getAvailableSignals( processInstanceId );
    }

}
