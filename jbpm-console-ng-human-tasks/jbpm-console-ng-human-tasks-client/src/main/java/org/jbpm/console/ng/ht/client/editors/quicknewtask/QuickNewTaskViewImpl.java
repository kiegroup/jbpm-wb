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

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.base.HtmlWidget;
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.model.events.UserTaskEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;

import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.jbpm.console.ng.ht.client.i8n.Constants;

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
    public TextBox taskNameText;
    @Inject
    @DataField
    public DateTimeBox dueDate;
    @Inject
    @DataField
    public TextBox userText;
    @Inject
    @DataField
    public TextBox taskPriorityListBox;
    @Inject
    @DataField
    public CheckBox quickTaskCheck;
    @Inject
    @DataField
    public ControlLabel dueDateLabel;
    
    @Inject
    @DataField
    public ControlLabel userLabel;
    
    @Inject
    @DataField
    public ControlLabel advancedLabel;
    
    @Inject
    @DataField
    public ControlLabel taskNameLabel;
    
    @Inject
    @DataField
    public ControlLabel taskPriorityLabel;
    @Inject
    private Event<NotificationEvent> notification;
    @Inject
    private Event<UserTaskEvent> userTaskChanges;
    
    private HandlerRegistration textKeyPressHandler;
    
    private HandlerRegistration checkKeyPressHandler;
    
    private Constants constants = GWT.create(Constants.class);

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
        textKeyPressHandler = taskNameText.addKeyPressHandler(keyPressHandlerText);
        
        KeyPressHandler keyPressHandlerCheck = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 13) {
                    addTask();
                }
            }
        };
        checkKeyPressHandler = quickTaskCheck.addKeyPressHandler(keyPressHandlerCheck);
        quickTaskCheck.setHTML(" "+constants.Quick_Task());
        taskNameText.setFocus(true);
        
        userText.setText(identity.getName());
        taskPriorityListBox.setText("5");
        dueDate.setValue(new Date());
        
        addTaskButton.setText(constants.Create());
        dueDateLabel.add(new HTMLPanel(constants.Due_On()));
        userLabel.add(new HTMLPanel(constants.User()));
        taskPriorityLabel.add(new HTMLPanel(constants.Priority()));
        
        advancedLabel.add(new HTMLPanel(constants.Advanced()));
        taskNameLabel.add(new HTMLPanel(constants.Task_Name()));
        
        
    }

    @EventHandler("addTaskButton")
    public void addTaskButton(ClickEvent e) {
        addTask();
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
        userTaskChanges.fire(new UserTaskEvent(identity.getName()));
    }

    public TextBox getTaskNameText() {
        return taskNameText;
    }

    private void addTask() {
        if(!taskNameText.getText().equals("")){
          addTaskButton.setEnabled(false);
          checkKeyPressHandler.removeHandler();
          textKeyPressHandler.removeHandler();

          presenter.addTask(userText.getText(),
                    taskNameText.getText(), Integer.parseInt(taskPriorityListBox.getText()), quickTaskCheck.getValue(), dueDate.getValue());
        }else{
          displayNotification(constants.Task_Must_Have_A_Name());
        }
    }

    public Button getAddTaskButton() {
      return addTaskButton;
    }
}
