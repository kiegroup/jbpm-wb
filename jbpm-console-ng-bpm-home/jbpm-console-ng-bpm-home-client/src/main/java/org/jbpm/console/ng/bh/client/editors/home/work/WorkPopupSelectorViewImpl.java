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
package org.jbpm.console.ng.bh.client.editors.home.work;

import com.google.gwt.core.client.GWT;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

//
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;

import org.jbpm.console.ng.bh.client.i18n.Constants;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "WorkPopupSelectorViewImpl.html")
public class WorkPopupSelectorViewImpl extends Composite
        implements
        WorkPopupSelectorPresenter.InboxView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private WorkPopupSelectorPresenter presenter;
    @Inject
    @DataField
    public Button inboxButton;
    @Inject
    @DataField
    public Button processMgmtButton;
    @Inject
    @DataField
    public Button closeButton;
    @Inject
    private Event<NotificationEvent> notification;
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(WorkPopupSelectorPresenter presenter) {
        this.presenter = presenter;
        
        KeyPressHandler keyPressHandlerInbox = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    goToInbox();
                    close();
                }
            }
        };
        inboxButton.addKeyPressHandler(keyPressHandlerInbox);
        
         KeyPressHandler keyPressHandlerProcess = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    goToProcessMgmt();
                    close();
                }
            }
        };
        processMgmtButton.addKeyPressHandler(keyPressHandlerProcess);
        
    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        close();
    }

    @EventHandler("inboxButton")
    public void inboxButton(ClickEvent e) {
        goToInbox();
        close();
    }

    @EventHandler("processMgmtButton")
    public void processMgmtButton(ClickEvent e) {
        goToProcessMgmt();
        close();
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    private void close() {
        presenter.close();
    }

    public Button getInboxButton() {
        return inboxButton;
    }

    private void goToInbox() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Tasks List");
        placeManager.goTo(placeRequestImpl);
    }

    private void goToProcessMgmt() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Runtime Perspective");
        placeManager.goTo(placeRequestImpl);
    }
    
    
}
