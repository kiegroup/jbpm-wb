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

package org.jbpm.console.ng.pr.client.editors.variables.edit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
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
@WorkbenchPopup(identifier = "Edit Variable Popup")
public class VariableEditPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface PopupView extends UberView<VariableEditPresenter> {

        void displayNotification( String text );

        void setProcessInstanceId( long processInstanceId );

        long getProcessInstanceId();

        String getVariableText();

        void setVariableText( String value );

        void setVariableId( String variableId );

        void setVariableIdLabel( String variableId );

        String getVariableId();

    }

    @Inject
    private PopupView view;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

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
        return constants.Edit_Variable();
    }

    @WorkbenchPartView
    public UberView<VariableEditPresenter> getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {
        view.setProcessInstanceId( Long.parseLong( place.getParameter( "processInstanceId", "-1" ).toString() ) );
        view.setVariableId( place.getParameter( "variableId", "-1" ).toString() );
        view.setVariableIdLabel( view.getVariableId() );
        view.setVariableText( place.getParameter( "value", "-1" ).toString() );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void setProcessVariable( Object value ) {

        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                close();

            }
        }, new ErrorCallback<Message>() {
             @Override
             public boolean error( Message message, Throwable throwable ) {
                 ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                 return true;
             }
         } ).setProcessVariable( view.getProcessInstanceId(), view.getVariableId(), value );
    }

}
