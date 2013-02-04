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
package org.jbpm.console.ng.pm.client.editors.newprocess;

import com.google.gwt.user.client.Window;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;


import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.KnowledgeDomainServiceEntryPoint;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "New Process Definition")
public class NewProcessDefinitionPresenter {

    public interface NewProcessDefinitionView
            extends
            UberView<NewProcessDefinitionPresenter> {

        void displayNotification(String text);

       
    }
    @Inject
    NewProcessDefinitionView view;
    @Inject
    Identity identity;
    @Inject
    Caller<KnowledgeDomainServiceEntryPoint> domainService;
    
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    
    private PlaceRequest place;
    
    @Inject
    private PlaceManager placeManager;

    public NewProcessDefinitionPresenter() {
    }
    
    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Process Definition";
    }

    @WorkbenchPartView
    public UberView<NewProcessDefinitionPresenter> getView() {
        return view;
    }

   

    @PostConstruct
    public void init() {
    }

    public void createNewProcess(final String path) {
        
            domainService.call(new RemoteCallback<String>() {
                @Override
                public void callback(String path) {
                    view.displayNotification("File Created "+path.toString());
                    Window.open("http://localhost:8080/designer/editor?profile=jbpm&pp=&uuid=git://jbpm-playground"+path.toString(), "_blank", "");
                }
            }).createProcessDefinitionFile(path);
        

    }

     public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
   

   
}
