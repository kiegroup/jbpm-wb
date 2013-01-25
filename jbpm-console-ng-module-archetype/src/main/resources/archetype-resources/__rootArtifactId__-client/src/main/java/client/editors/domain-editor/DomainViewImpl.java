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
package org.jbpm.console.ng.ht.client.editors.quicknewtask;

import com.google.gwt.core.client.GWT;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

//
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import java.util.Date;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;



@Dependent
@Templated(value = "DomainViewImpl.html")
public class DomainViewImpl extends Composite
        implements
        DomainPresenter.DomainView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private DomainPresenter presenter;
    @Inject
    @DataField
    public Button sayHelloButton;
    
    @Inject
    @DataField
    public TextBox helloText;
    
    @Inject
    private Event<NotificationEvent> notification;
    

    @Override
    public void init(DomainPresenter presenter) {
        this.presenter = presenter;
        KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    sayHello();
                }
            }
        };
        helloText.addKeyPressHandler(keyPressHandlerText);
        
        KeyPressHandler keyPressHandlerCheck = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    sayHello();
                }
            }
        };
        
        
    }

    @EventHandler("sayHelloButton")
    public void sayHelloButton(ClickEvent e) {
        sayHello();
    }

    

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public TextBox getHelloText() {
        return helloText;
    }

    private void sayHello() {
        presenter.sayHello(identity.getName(),
                helloText.getText(), new Date());

    }

   
}
