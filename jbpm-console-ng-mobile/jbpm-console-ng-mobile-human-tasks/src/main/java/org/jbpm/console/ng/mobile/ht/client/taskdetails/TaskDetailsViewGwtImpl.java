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
package org.jbpm.console.ng.mobile.ht.client.taskdetails;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.FormListEntry;
import com.googlecode.mgwt.ui.client.widget.MDateBox;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateRenderer;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MTextArea;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;
import com.googlecode.mgwt.ui.client.widget.tabbar.TabBarButton;
import com.googlecode.mgwt.ui.client.widget.tabbar.TabPanel;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskView;
import org.jbpm.console.ng.mobile.ht.client.utils.TaskStatus;



/**
 *
 * @author livthomas
 */
public class TaskDetailsViewGwtImpl extends AbstractTaskView implements TaskDetailsPresenter.TaskDetailsView {

    private final Button saveButton;
    private final Button releaseButton;
    private final Button claimButton;
    private final Button startButton;
    private final Button completeButton;

    private final MTextArea descriptionTextArea = new MTextArea();
    private final MTextBox statusTextBox = new MTextBox();
    private final MDateBox dueOnDateBox = new MDateBox();
    private final MListBox priorityListBox = new MListBox();
    private final MTextBox userTextBox = new MTextBox();
    private final MTextBox processInstanceIdTextBox = new MTextBox();
    private final MTextBox processDefinitionIdTextBox = new MTextBox();
    private final Button processInstanceDetailsButton = new Button("Process Instance Details");
    private final Button updateButton;

    private final Label potentialOwnersLabel = new Label();
    private final MTextBox delegateTextBox = new MTextBox();
    private final Button delegateButton;

    public TaskDetailsViewGwtImpl() {
        title.setHTML("Task Details");

        TabPanel tabPanel = new TabPanel();
        tabPanel.setDisplayTabBarOnTop(true);
        layoutPanel.add(tabPanel);

        // Work tab
        RoundPanel workPanel = new RoundPanel();

        saveButton = new Button("Save");
        workPanel.add(saveButton);

        releaseButton = new Button("Release");
        workPanel.add(releaseButton);

        claimButton = new Button("Claim");
        claimButton.setConfirm(true);
        workPanel.add(claimButton);

        startButton = new Button("Start");
        startButton.setConfirm(true);
        workPanel.add(startButton);

        completeButton = new Button("Complete");
        completeButton.setConfirm(true);
        workPanel.add(completeButton);

        TabBarButton workTabButton = new TabBarButton(null);
        workTabButton.setText("Work");
        tabPanel.add(workTabButton, workPanel);

        // Details tab
        RoundPanel detailsPanel = new RoundPanel();

        for (String priority : priorities) {
            priorityListBox.addItem(priority);
        }
        statusTextBox.setReadOnly(true);
        userTextBox.setReadOnly(true);

        WidgetList detailsForm = new WidgetList();
        detailsForm.setRound(true);
        detailsForm.add(new FormListEntry("Description", descriptionTextArea));
        detailsForm.add(new FormListEntry("Status", statusTextBox));
        detailsForm.add(new FormListEntry("Due On", dueOnDateBox));
        detailsForm.add(new FormListEntry("Priority", priorityListBox));
        detailsForm.add(new FormListEntry("User", userTextBox));
        detailsPanel.add(detailsForm);

        processInstanceIdTextBox.setReadOnly(true);
        processDefinitionIdTextBox.setReadOnly(true);
        processInstanceDetailsButton.setSmall(true);

        WidgetList processContextForm = new WidgetList();
        processContextForm.setRound(true);
        processContextForm.add(new FormListEntry("Process Instance Id", processInstanceIdTextBox));
        processContextForm.add(new FormListEntry("Process Definition Id", processDefinitionIdTextBox));
        processContextForm.add(new FormListEntry("Process Instance Details", processInstanceDetailsButton));
        detailsPanel.add(processContextForm);

        updateButton = new Button("Update");
        updateButton.setConfirm(true);
        detailsPanel.add(updateButton);

        TabBarButton detailsTabButton = new TabBarButton(null);
        detailsTabButton.setText("Details");
        tabPanel.add(detailsTabButton, detailsPanel);

        // Assignments tab
        RoundPanel assignmentsPanel = new RoundPanel();

        WidgetList assignmentsForm = new WidgetList();
        assignmentsForm.setRound(true);
        assignmentsForm.add(new FormListEntry("Potential Owners", potentialOwnersLabel));
        assignmentsForm.add(new FormListEntry("User or Group", delegateTextBox));
        assignmentsPanel.add(assignmentsForm);

        delegateButton = new Button("Delegate");
        delegateButton.setConfirm(true);
        assignmentsPanel.add(delegateButton);

        TabBarButton assignmentsTabButton = new TabBarButton(null);
        assignmentsTabButton.setText("Assignments");
        tabPanel.add(assignmentsTabButton, assignmentsPanel);

        // Comments tab
        RoundPanel commentsPanel = new RoundPanel();

        TabBarButton commentsTabButton = new TabBarButton(null);
        commentsTabButton.setText("Comments");
        tabPanel.add(commentsTabButton, commentsPanel);

        tabPanel.setSelectedChild(1);
    }

    @Override
    public void refreshTask(TaskSummary task, boolean owned) {
        TaskStatus status = TaskStatus.valueOf(task.getStatus());

        switch (status) {
            case Ready:
                saveButton.setVisible(false);
                releaseButton.setVisible(false);
                claimButton.setVisible(true);
                startButton.setVisible(false);
                completeButton.setVisible(false);
                break;
            case Reserved:
                saveButton.setVisible(false);
                releaseButton.setVisible(true);
                claimButton.setVisible(false);
                startButton.setVisible(true);
                completeButton.setVisible(false);
                break;
            case InProgress:
                saveButton.setVisible(true);
                releaseButton.setVisible(true);
                claimButton.setVisible(false);
                startButton.setVisible(false);
                completeButton.setVisible(true);
                break;
            default:
                saveButton.setVisible(false);
                releaseButton.setVisible(false);
                claimButton.setVisible(false);
                startButton.setVisible(false);
                completeButton.setVisible(false);
        }

        descriptionTextArea.setText(task.getDescription());
        statusTextBox.setText(task.getStatus());
        dueOnDateBox.setText(new DateRenderer().render(task.getExpirationTime()));
        priorityListBox.setSelectedIndex(task.getPriority());
        userTextBox.setText(task.getActualOwner());

        if (status.equals(TaskStatus.Completed)) {
            descriptionTextArea.setReadOnly(true);
            dueOnDateBox.setReadOnly(true);
            priorityListBox.setEnabled(false);
            updateButton.setVisible(false);
        } else {
            descriptionTextArea.setReadOnly(false);
            dueOnDateBox.setReadOnly(false);
            priorityListBox.setEnabled(true);
            updateButton.setVisible(true);
        }

        String instanceId = (task.getProcessInstanceId() == -1) ? "None" : Long.toString(task.getProcessInstanceId());
        String definitionId = (task.getProcessId() == null) ? "None" : task.getProcessId();
        processInstanceIdTextBox.setText(instanceId);
        processDefinitionIdTextBox.setText(definitionId);

//        potentialOwnersLabel.setText(task.getPotentialOwners().toString());
        if (owned && !status.equals(TaskStatus.Completed)) {
            delegateTextBox.setReadOnly(false);
            delegateButton.setVisible(true);
        } else {
            delegateTextBox.setReadOnly(true);
            delegateButton.setVisible(false);
        }
    }

    @Override
    public HasTapHandlers getUpdateButton() {
        return updateButton;
    }

    @Override
    public HasTapHandlers getDelegateButton() {
        return delegateButton;
    }

    @Override
    public HasText getDescriptionTextArea() {
        return descriptionTextArea;
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
    public HasText getPotentialOwnersText() {
        return potentialOwnersLabel;
    }

    @Override
    public HasText getDelegateTextBox() {
        return delegateTextBox;
    }

    @Override
    public HasTapHandlers getSaveButton() {
        return saveButton;
    }

    @Override
    public HasTapHandlers getReleaseButton() {
        return releaseButton;
    }

    @Override
    public HasTapHandlers getCompleteButton() {
        return completeButton;
    }

    @Override
    public HasTapHandlers getClaimButton() {
        return claimButton;
    }

    @Override
    public HasTapHandlers getStartButton() {
        return startButton;
    }

}
