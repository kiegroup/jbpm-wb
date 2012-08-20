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
package org.jbpm.console.ng.client.editors.tasks.inbox.subtask;

import org.jbpm.console.ng.client.editors.tasks.inbox.taskdetails.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import javax.enterprise.event.Event;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class NewQuickSubTaskViewImpl extends Composite implements NewQuickSubTaskPresenter.InboxView {

    @Inject
    private UiBinder<Widget, NewQuickSubTaskViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    @Inject
    private NewQuickSubTaskPresenter presenter;

    @UiField
    public Button createSubTaskButton;
    @UiField
    public TextBox subTaskNameText;
    @UiField
    public TextBox subTaskAsigneeText;
    @UiField
    public TextBox parentTaskIdText;

    @Inject
    private Event<NotificationEvent> notification;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
        
    }

   
    
     @UiHandler("createSubTaskButton")
    public void createSubTaskButton(ClickEvent e) {
        presenter.addSubTask(Long.parseLong(parentTaskIdText.getText()), 
                subTaskAsigneeText.getText(),
                subTaskNameText.getText());
    }

   
    public TextBox getParentTaskIdText() {
        return parentTaskIdText;
    }

   
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

  
}
