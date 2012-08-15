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
package org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.Date;
import javax.enterprise.event.Event;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class NewTaskDetailsViewImpl extends Composite implements NewTaskDetailsPresenter.InboxView {

    @Inject
    private UiBinder<Widget, NewTaskDetailsViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private NewTaskDetailsPresenter presenter;
    @UiField
    public Button updateTaskButton;
    @UiField
    public Button refreshButton;
    @UiField
    public TextBox userText;
    @UiField
    public TextBox taskIdText;
    @UiField
    public TextBox groupText;
    @UiField
    public TextBox taskNameText;
    @UiField
    public TextBox taskDescriptionText;
    @UiField
    public TextBox taskPriorityText;
    @UiField
    public DatePicker dueDate;
    @Inject
    private Event<NotificationEvent> notification;

    @PostConstruct
    public void init() {

        initWidget(uiBinder.createAndBindUi(this));

    }

    @UiHandler("updateTaskButton")
    public void addTaskButton(ClickEvent e) {
        presenter.updateTask(Long.parseLong(taskIdText.getText()), 
                taskDescriptionText.getText(),
                dueDate.getValue(), taskPriorityText.getText());
    }

    @UiHandler("refreshButton")
    public void refreshButton(ClickEvent e) {
        presenter.refreshTask(Long.parseLong(taskIdText.getText()));

    }

    public TextBox getUserText() {
        return userText;
    }

    public TextBox getTaskIdText() {
        return taskIdText;
    }

    public TextBox getGroupText() {
        return groupText;
    }

    public TextBox getTaskNameText() {
        return taskNameText;
    }

    public TextBox getTaskDescriptionText() {
        return taskDescriptionText;
    }

    public TextBox getTaskPriorityText() {
        return taskPriorityText;
    }

    public DatePicker getDueDate() {
        return dueDate;
    }
    
    
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
    
    
}
