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
package org.jbpm.console.ng.bd.client.editors.session.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
@Templated(value = "SessionNotificationsPopupViewImpl.html")
public class SessionNotificationsPopupViewImpl extends Composite
        implements
        SessionNotificationsPopupPresenter.InboxView {

    private SessionNotificationsPopupPresenter presenter;
  
    @Inject
    @DataField
    public TextBox sessionIdText;

    @Inject
    @DataField
    public TextArea sessionNotificationsTextArea;

    @Inject
    @DataField
    public Button updateButton;

    
    @Inject
    @DataField
    public Button closeButton;

   
    
    @Inject
    private PlaceManager placeManager;
   
    @Inject
    private Event<NotificationEvent> notification;
    
    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(SessionNotificationsPopupPresenter presenter) {
        this.presenter = presenter;


    }

    @EventHandler("updateButton")
    public void updateTaskButton(ClickEvent e) {
        presenter.getSessionNotifications(Integer.parseInt(sessionIdText.getText()));
    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        presenter.close();
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public TextBox getSessionIdText() {
        return sessionIdText;
    }

    @Override
    public TextArea getSessionNotificationsTextArea() {
        return sessionNotificationsTextArea;
    }

    @Override
    public Button getUpdateButton() {
        return updateButton;
    }



  

    
    
    
}
