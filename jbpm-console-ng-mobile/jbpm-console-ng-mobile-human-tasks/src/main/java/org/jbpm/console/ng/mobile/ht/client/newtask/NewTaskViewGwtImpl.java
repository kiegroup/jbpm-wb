/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.mobile.ht.client.newtask;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.FormListEntry;
import com.googlecode.mgwt.ui.client.widget.MCheckBox;
import com.googlecode.mgwt.ui.client.widget.MDateBox;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskView;


/**
 *
 * @author livthomas
 * @author salaboy
 */
public class NewTaskViewGwtImpl extends AbstractTaskView implements NewTaskPresenter.NewTaskView {

    private final MTextBox taskNameTextBox = new MTextBox();

    private final MCheckBox assignToMeCheckBox = new MCheckBox();

    private final MDateBox dueOnDateBox = new MDateBox();

    private final MListBox priorityListBox = new MListBox();

    private final MTextBox userTextBox = new MTextBox();

    private final Button addTaskButton;

    public NewTaskViewGwtImpl() {
        title.setHTML("New Task");

        ScrollPanel scrollPanel = new ScrollPanel();
        layoutPanel.add(scrollPanel);

        RoundPanel newTaskPanel = new RoundPanel();

        for (String priority : priorities) {
            priorityListBox.addItem(priority);
        }

        WidgetList newTaskForm = new WidgetList();
        newTaskForm.setRound(true);
        newTaskForm.add(new FormListEntry("Task Name", taskNameTextBox));
        newTaskForm.add(new FormListEntry("Auto Assign To Me", assignToMeCheckBox));
        newTaskForm.add(new FormListEntry("Due On", dueOnDateBox));
        newTaskForm.add(new FormListEntry("Priority", priorityListBox));
        newTaskForm.add(new FormListEntry("User", userTextBox));
        newTaskPanel.add(newTaskForm);

        addTaskButton = new Button("Add");
        addTaskButton.setConfirm(true);
        newTaskPanel.add(addTaskButton);

        scrollPanel.add(newTaskPanel);
    }

    @Override
    public HasText getTaskNameTextBox() {
        return taskNameTextBox;
    }

    @Override
    public HasTapHandlers getAddTaskButton() {
        return addTaskButton;
    }

    @Override
    public HasValue<Boolean> getAssignToMeCheckBox() {
        return assignToMeCheckBox;
    }

    @Override
    public HasText getDueOnDateBox() {
        return dueOnDateBox;
    }

    @Override
    public MListBox getPriorityListBox() {
        return priorityListBox;
    }

    @Override
    public HasText getUserTextBox() {
        return userTextBox;
    }

}
