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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.FormListEntry;
import com.googlecode.mgwt.ui.client.widget.MCheckBox;
import com.googlecode.mgwt.ui.client.widget.MDateBox;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateParser;
import com.googlecode.mgwt.ui.client.widget.MDateBox.DateRenderer;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.mobile.core.client.MGWTPlaceManager;
import org.jbpm.console.ng.mobile.ht.client.AbstractTaskView;
import org.uberfire.security.Identity;

/**
 *
 * @author livthomas
 * @author salaboy
 */
@Dependent
public class NewTaskViewGwtImpl extends AbstractTaskView implements NewTaskPresenter.NewTaskView {

    private final MTextBox taskNameTextBox = new MTextBox();

    private final MCheckBox assignToMeCheckBox = new MCheckBox();

    private final MDateBox dueOnDateBox = new MDateBox();

    private final MListBox priorityListBox = new MListBox();

    private final MTextBox userTextBox = new MTextBox();

    private final Button addTaskButton;

    private NewTaskPresenter presenter;

    @Inject
    private MGWTPlaceManager placeManager;

    @Inject
    protected Identity identity;

    public NewTaskViewGwtImpl() {
        GWT.log("NEW NewTaskViewGwtImpl " + this.hashCode());
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
    public void init(final NewTaskPresenter presenter) {
        this.presenter = presenter;
        getAssignToMeCheckBox().setValue(false);
        getDueOnDateBox().setText(new DateRenderer().render(new Date()));
        getUserTextBox().setText(identity.getName());

        getAddTaskButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                try {
                    List<String> users = new ArrayList<String>();
                    users.add(getUserTextBox().getText());
                    List<String> groups = new ArrayList<String>();
                    String taskName = getTaskNameTextBox().getText();
                    int priority = getPriorityListBox().getSelectedIndex();
                    boolean assignToMe = getAssignToMeCheckBox().getValue();
                    long date = new DateParser().parse(getDueOnDateBox().getText()).getTime();
                    long time = 0;

                    presenter.addTask(users, groups, taskName, priority, assignToMe, date, time);

                    getTaskNameTextBox().setText("");
                    getPriorityListBox().setSelectedIndex(0);
                    getAssignToMeCheckBox().setValue(false);
                    getDueOnDateBox().setText(new DateRenderer().render(new Date()));

                    placeManager.goTo("New Task", Animation.SLIDE);
                } catch (ParseException ex) {
                    displayNotification("Wrong date format", "Enter the date in the correct format!");
                }
            }
        });

        getBackButton().addTapHandler(new TapHandler() {
            @Override
            public void onTap(TapEvent event) {
                getTaskNameTextBox().setText("");
                placeManager.goTo("Tasks List", Animation.SLIDE_REVERSE);
            }
        });
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

    @Override
    public void goBackToTaskList() {
        placeManager.goTo("Tasks List", Animation.SLIDE_REVERSE);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setParameters(Map<String, Object> params) {

    }

}
