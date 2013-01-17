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
package org.jbpm.console.ng.ht.client.editors.domain-editor;

import com.google.gwt.user.client.ui.TextBox;
import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Say Hello")
public class DomainPresenter {

    public interface DomainView
            extends
            UberView<DomainPresenter> {

        void displayNotification(String text);

       
    }
    @Inject
    InboxView view;
    @Inject
    Identity identity;
    @Inject
    Caller<DomainService> domainService;
    
    private PlaceRequest place;
    
    @Inject
    private PlaceManager placeManager;

    public DomainPresenter() {
    }
    
    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Say Hello";
    }

    @WorkbenchPartView
    public UberView<DomainPresenter> getView() {
        return view;
    }

   

    @PostConstruct
    public void init() {
    }

    public void sayHello(final String userId, String name,   Date date) {
        
            domainService.call(new RemoteCallback<String>() {
                @Override
                public void callback(String greetings) {
                    view.displayNotification("Greetings Sent!  \n ="+greetings);
                    
                }
            }).sayHello(userId + " - " +name +" : " + identity.getName() + " - " date.toString() );
        

    }

   

   
}
