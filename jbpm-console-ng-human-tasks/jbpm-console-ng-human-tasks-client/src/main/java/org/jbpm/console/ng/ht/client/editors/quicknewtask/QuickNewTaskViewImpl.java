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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TextBox;
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

import com.google.gwt.user.client.ui.Composite;
import java.util.Date;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;



@Dependent
@Templated(value = "QuickNewTaskViewImpl.html")
public class QuickNewTaskViewImpl extends Composite
        implements
        QuickNewTaskPresenter.QuickNewTaskView {

    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    private QuickNewTaskPresenter presenter;
    @Inject
    @DataField
    public Button addTaskButton;
    @Inject
    @DataField
    public Button closeButton;
    @Inject
    @DataField
    public TextBox taskNameText;
    @Inject
    @DataField
    public CheckBox quickTaskCheck;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<UserTaskEvent> userTaskChanges;

    @Override
    public void init(QuickNewTaskPresenter presenter) {
        this.presenter = presenter;
        KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    addTask();
                }
            }
        };
        taskNameText.addKeyPressHandler(keyPressHandlerText);
        
        KeyPressHandler keyPressHandlerCheck = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    addTask();
                }
            }
        };
        quickTaskCheck.addKeyPressHandler(keyPressHandlerCheck);
        
    }

    @EventHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        addTask();
    }

    @EventHandler("closeButton")
    public void closeButton(ClickEvent e) {
        close();
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
        userTaskChanges.fire(new UserTaskEvent(identity.getName()));
    }

    public TextBox getTaskNameText() {
        return taskNameText;
    }

    private void addTask() {
        presenter.addTask(identity.getName(),
                taskNameText.getText(), quickTaskCheck.getValue(), new Date());

    }

    private void close() {
        presenter.close();
    }
}
