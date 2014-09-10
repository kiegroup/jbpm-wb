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

package org.jbpm.console.ng.dm.client.document.newdocument;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.dm.model.DocumentSummary;
import org.jbpm.console.ng.dm.model.events.NewDocumentEvent;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "New Document")
public class NewDocumentPresenter {

    public interface NewDocumentView extends UberView<NewDocumentPresenter> {

    	void setFolder(String folder);

        void displayNotification( String notification );
    }
    
    private String folder;
    @Inject
    NewDocumentView view;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    @Inject
    private Event<NewDocumentEvent> documentAddedEvent;
    @Inject
    private Caller<DocumentServiceEntryPoint> documentServices;
    private PlaceRequest place;

    public NewDocumentPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Document";
    }

    @WorkbenchPartView
    public UberView<NewDocumentPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        Map<String, String> params = place.getParameters();
        this.folder = params.get("folder");
        this.view.setFolder(folder);
    }

    @OnOpen
    public void onOpen() {
//        view.getJobNameText().setFocus( true );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }
    
    public void createDocument(DocumentSummary doc) {
    	this.documentServices.call(new RemoteCallback<Void>() {
    		@Override
    		public void callback(Void response) {
    			close();
    			documentAddedEvent.fire(new NewDocumentEvent());
    			System.out.println("Hello");
    		}
		}).addDocument(doc);
    }
}
