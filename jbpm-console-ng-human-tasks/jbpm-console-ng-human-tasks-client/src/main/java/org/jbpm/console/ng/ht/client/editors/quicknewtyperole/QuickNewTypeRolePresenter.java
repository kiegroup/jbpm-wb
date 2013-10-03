/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.ht.client.editors.quicknewtyperole;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;


import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TypeRole;
import org.jbpm.console.ng.ht.service.TypeRoleServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;

@Dependent
@WorkbenchPopup(identifier = "Quick New TypeRole")
public class QuickNewTypeRolePresenter {

	private Constants constants = GWT.create( Constants.class );
    
    @Inject
    QuickNewTypeRoleView view;

    @Inject
    Identity identity;

    @Inject
    Caller<TypeRoleServiceEntryPoint> typeRoleService;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    public interface QuickNewTypeRoleView extends UberView<QuickNewTypeRolePresenter> {

        void displayNotification( String text );
        
        TextBox getDescriptionText();

    }

    private PlaceRequest place;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Add_TypeRole();
    }

    @WorkbenchPartView
    public UberView<QuickNewTypeRolePresenter> getView() {
        return view;
    }

    public QuickNewTypeRolePresenter() {
    }

    @PostConstruct
    public void init() {
    }

    
    @OnOpen
    public void onOpen() {
        view.getDescriptionText().setFocus( true );
    }
    
    public void addTypeRole(  ){
    	typeRoleService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void nothing ) {
                view.displayNotification( "TypeRole Created (id = " + view.getDescriptionText().getText() + ")" );
                close();

            }
        } ).save(new TypeRole(view.getDescriptionText().getText()));
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }
}
