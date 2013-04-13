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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "TaskDetailsPopupViewImpl.html")
public class TaskDetailsPopupViewImpl extends Composite
        implements
        TaskDetailsPopupPresenter.TaskDetailsPopupView {

    private TaskDetailsPopupPresenter presenter;
    @Inject
    @DataField
    public Label taskIdText;
    @Inject
    @DataField
    public TextBox userText;
    @Inject
    @DataField
    public Label taskNameText;
    @Inject
    @DataField
    public TextBox processInstanceIdText;
    @Inject
    @DataField
    public TextBox processIdText;
    @Inject
    @DataField
    public TextBox taskStatusText;
    @Inject
    @DataField
    public TextArea taskDescriptionTextArea;
    @Inject
    @DataField
    public ListBox taskPriorityListBox;
    @Inject
    @DataField
    public ListBox subTaskStrategyListBox;
    @Inject
    @DataField
    public DateTimeBox dueDate;
     @Inject
    @DataField
    public Button updateTaskButton;
    @Inject
    @DataField
    public Button pIDetailsButton;
    @Inject
    private PlaceManager placeManager;
    private String[] subTaskStrategies = {"NoAction", "EndParentOnAllSubTasksEnd", "SkipAllSubTasksOnParentSkip"};
    private String[] priorities = {"0 - High", "1", "2", "3", "4", "5 - Medium", "6", "7", "8", "9", "10 - Low"};
    
    @Inject
    @DataField
    public UnorderedList navBarUL;
    
    @Inject
    private Event<NotificationEvent> notification;
    

    @Override
    public void init(TaskDetailsPopupPresenter presenter) {
        this.presenter = presenter;


        for (String strategy : subTaskStrategies) {
            subTaskStrategyListBox.addItem(strategy);

        }

        for (String priority : priorities) {
            taskPriorityListBox.addItem(priority);

        }


    }


    @EventHandler("updateTaskButton")
    public void updateTaskButton(ClickEvent e) {
        presenter.updateTask(Long.parseLong(taskIdText.getText()), taskNameText.getText(),
                taskDescriptionTextArea.getText(), userText.getText(),
                subTaskStrategyListBox.getItemText(subTaskStrategyListBox.getSelectedIndex()),
                dueDate.getValue(),
                taskPriorityListBox.getSelectedIndex());

    }

    @EventHandler("pIDetailsButton")
    public void pIDetailsButton(ClickEvent e) {
        presenter.close();
        presenter.goToProcessInstanceDetails();
    }

    public TextBox getUserText() {
        return userText;
    }

    public Label getTaskIdText() {
        return taskIdText;
    }

    public Label getTaskNameText() {
        return taskNameText;
    }

    public TextArea getTaskDescriptionTextArea() {
        return taskDescriptionTextArea;
    }

    public ListBox getTaskPriorityListBox() {
        return taskPriorityListBox;
    }

    public DateTimeBox getDueDate() {
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

    public TextBox getTaskStatusText() {
        return taskStatusText;
    }

    public TextBox getProcessInstanceIdText() {
        return processInstanceIdText;
    }

    public TextBox getProcessIdText() {
        return processIdText;
    }
    
    public Button getpIDetailsButton() {
        return pIDetailsButton;
    }

    public UnorderedList getNavBarUL() {
      return navBarUL;
    }
    
    
}
