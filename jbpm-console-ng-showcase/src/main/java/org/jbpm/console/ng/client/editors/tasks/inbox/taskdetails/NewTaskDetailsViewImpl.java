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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import javax.enterprise.event.Event;
import org.jbpm.console.ng.client.editors.tasks.inbox.events.TaskChangedEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

@Dependent
public class NewTaskDetailsViewImpl extends Composite implements NewTaskDetailsPresenter.InboxView {

    @Inject
    private UiBinder<Widget, NewTaskDetailsViewImpl> uiBinder;
    @Inject
    private PlaceManager placeManager;
    
    private NewTaskDetailsPresenter presenter;
    @UiField
    public Button updateTaskButton;
    @UiField
    public Button refreshButton;
    @UiField
    public ListBox subTaskStrategyListBox;
    @UiField
    public TextBox userText;
    @UiField
    public TextBox taskIdText;
    @UiField
    public TextBox groupText;
    @UiField
    public TextBox taskNameText;
    @UiField
    public TextArea taskDescriptionTextArea;
    @UiField
    public ListBox taskPriorityListBox;
    @UiField
    public DatePicker dueDate;
    
    private String[] subTaskStrategies = {"NoAction", "EndParentOnAllSubTasksEnd", "SkipAllSubTasksOnParentSkip"};
    
    private String[] priorities = {"0 - High", "1", "2", "3", "4", "5 - Medium" , "6", "7", "8", "9", "10 - Low"};
    
    @Inject
    private Event<NotificationEvent> notification;
    
    @Override
    public void init(NewTaskDetailsPresenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        int i = 0;
        for(String strategy : subTaskStrategies){
            subTaskStrategyListBox.addItem(strategy);
            i++;
        }
        i = 0;
        for(String priority : priorities){
            taskPriorityListBox.addItem(priority);
            i++;
        }
        taskDescriptionTextArea.setVisibleLines(5);
    }

    @UiHandler("updateTaskButton")
    public void updateTaskButton(ClickEvent e) {
        presenter.updateTask(Long.parseLong(taskIdText.getText()), 
                taskDescriptionTextArea.getText(), subTaskStrategyListBox.getItemText(subTaskStrategyListBox.getSelectedIndex()),
                dueDate.getValue(), taskPriorityListBox.getSelectedIndex());
        
        
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

    public TextArea getTaskDescriptionTextArea() {
        return taskDescriptionTextArea;
    }

    public ListBox getTaskPriorityListBox() {
        return taskPriorityListBox;
    }

    public DatePicker getDueDate() {
        return dueDate;
    }

    public ListBox getSubTaskStrategyListBox() {
        return subTaskStrategyListBox;
    }
    
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public String[] getSubTaskStrategies() {
        return subTaskStrategies;
    }

    public String[] getPriorities() {
        return priorities;
    }
    
    
    
}
