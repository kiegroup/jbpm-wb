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
package org.jbpm.console.ng.client.editors.home.work;

import com.google.gwt.user.client.ui.Button;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;


import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.annotations.OnReveal;
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
@WorkbenchPopup(identifier = "Work Popup Selector")
public class WorkPopupSelectorPresenter {

    public interface InboxView
            extends
            UberView<WorkPopupSelectorPresenter> {

        void displayNotification(String text);
        Button getInboxButton();
       
    }
    @Inject
    InboxView view;
    @Inject
    Identity identity;
    @Inject
    Caller<TaskServiceEntryPoint> taskServices;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest place;
    
    @Inject
    private PlaceManager placeManager;

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Work Popup Seletor";
    }

    @WorkbenchPartView
    public UberView<WorkPopupSelectorPresenter> getView() {
        return view;
    }

    public WorkPopupSelectorPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    

    @OnReveal
    public void onReveal() {
        view.getInboxButton().setFocus(true);
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }
}
