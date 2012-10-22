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
package org.jbpm.console.ng.client.editors.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;

@Dependent
@Templated(value = "HomeViewImpl.html")
public class HomeViewImpl extends Composite
        implements
        HomePresenter.InboxView {

    private HomePresenter presenter;
    @DataField
    public SuggestBox actionText;
    @Inject
    public Identity identity;
//    @Inject
//    @DataField
//    public Label userLabel;
    @DataField
    public Image avatar;
    @Inject
    @DataField
    public Button goButton;
    @Inject
    private Event<NotificationEvent> notification;
    

    public HomeViewImpl() {
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        String[] words = {"Show me my pending Tasks",
            "I want to start a new Process",
            "I want to design a new Process Model",
            "I want to design a new Form",
            "I want to create a Task",
            "Show me all the pending tasks in my Group",
            "Show me my Inbox"
        };
        for (int i = 0; i < words.length; ++i) {
            oracle.add(words[i]);
        }
        // Create the suggest box
        actionText = new SuggestBox(oracle);
        avatar = new Image();

    }

    @Override
    public void init(HomePresenter presenter) {
        this.presenter = presenter;
        String url = GWT.getHostPageBaseURL();
        avatar.setUrl(url+"images/avatars/"+identity.getName()+".png");
        avatar.setSize("64px", "64px");
        //userLabel.setText(identity.getName());

    }

    @EventHandler("goButton")
    public void goButton(ClickEvent e) {
        presenter.doAction(actionText.getText());
    }

    @EventHandler("actionText")
    public void enterHit(KeyPressEvent e) {
        if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            presenter.doAction(actionText.getText());
        }
    }

    public SuggestBox getActionText() {
        return actionText;
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
}
