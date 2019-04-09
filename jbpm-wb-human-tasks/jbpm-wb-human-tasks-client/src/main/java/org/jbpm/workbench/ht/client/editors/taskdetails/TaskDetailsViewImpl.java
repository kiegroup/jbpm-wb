/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.taskdetails;

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.common.client.util.SlaStatusConverter;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePickerOptions;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
@Templated(value = "TaskDetailsViewImpl.html")
public class TaskDetailsViewImpl extends Composite implements TaskDetailsPresenter.TaskDetailsView {

    private static Constants constants = Constants.INSTANCE;

    @Inject
    @DataField
    public Paragraph userText;

    @Inject
    @DataField("taskStatusText")
    public Paragraph taskStatusText;

    @Inject
    @DataField
    public TextArea taskDescriptionTextArea;

    @Inject
    @DataField
    public Select taskPriorityListBox;

    @Inject
    @DataField("processInstanceIdText")
    public Paragraph processInstanceIdText;

    @Inject
    @DataField("processIdText")
    public Paragraph processIdText;

    @Inject
    @DataField("slaComplianceText")
    public Paragraph slaComplianceText;

    @Inject
    @DataField
    public Button updateTaskButton;

    @Inject
    @DataField
    public FormLabel taskStatusLabel;

    @Inject
    @DataField
    public FormLabel userLabel;

    @Inject
    @DataField
    public FormLabel dueDateLabel;

    @Inject
    @DataField
    public FormLabel taskPriorityLabel;

    @Inject
    @DataField
    public FormLabel taskDescriptionLabel;

    @Inject
    @DataField
    public FormLabel processInstanceIdLabel;

    @Inject
    @DataField
    public FormLabel processIdLabel;

    @Inject
    @DataField
    public FormLabel slaComplianceLabel;

    @Inject
    @DataField("date-filters-input")
    public FlowPanel dateRangePickerInput;

    @Inject
    @DataField("dueDateText")
    public Paragraph dueDateText;

    @Inject
    private ManagedInstance<DateRangePicker> dateRangePickerProvider;

    private DateRangePicker dateRangePicker;

    private Date selectedDate;

    private TaskDetailsPresenter presenter;

    private String[] priorities = {"0 - " + constants.High(), "1", "2", "3", "4", "5 - " + constants.Medium(), "6", "7", "8", "9", "10 - " + constants.Low()};

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(TaskDetailsPresenter presenter) {
        this.presenter = presenter;

        for (int i = 0; i < priorities.length; i++) {
            final Option option = new Option();
            option.setText(priorities[i]);
            option.setValue(String.valueOf(i));
            taskPriorityListBox.add(option);
        }
        refreshPriorities();

        taskStatusLabel.setText(constants.Status());
        userLabel.setText(constants.User());
        dueDateLabel.setText(constants.Due_On());

        taskPriorityLabel.setText(constants.Priority());

        slaComplianceLabel.setText(constants.SlaCompliance());

        taskDescriptionLabel.setText(constants.Description());

        processInstanceIdLabel.setText(constants.Process_Instance_Id());

        processIdLabel.setText(constants.Process_Definition_Id());

        updateTaskButton.setText(constants.Update());

        setDueDateEnabled(true);
        initDateTimePicker();

    }

    protected void initDateTimePicker() {
        this.dateRangePicker = dateRangePickerProvider.get();
        dateRangePicker.getElement().setReadOnly(true);
        dateRangePicker.getElement().setAttribute("placeholder",
                                                  Constants.INSTANCE.Due_On());
        dateRangePicker.getElement().getClassList().add("form-control");
        setupDateTimePicker();
        dateRangePickerInput.add(ElementWrapperWidget.getWidget(dateRangePicker.getElement()));
    }

    protected void setupDateTimePicker() {
        final DateRangePickerOptions options = getDateRangePickerOptions();
        dateRangePicker.setup(options,
                              (start, end, label) -> {
                                  selectedDate = start.milliseconds(0).asDate();
                                  dateRangePicker.getElement().setAttribute("placeholder",
                                                                            DateUtils.getDateTimeStr(selectedDate));
                              });
    }

    protected DateRangePickerOptions getDateRangePickerOptions() {
        final DateRangePickerOptions options = DateRangePickerOptions.create();
        options.setAutoUpdateInput(false);
        options.setAutoApply(true);
        options.setTimePicker(true);
        options.setTimePickerIncrement(1);
        options.setSingleDatePicker(true);
        options.setMinDate(moment());
        options.setParentEl("[data-field='modal']");
        if (selectedDate != null) {
            options.setStartDate(moment(selectedDate.getTime()));
        }

        return options;
    }

    @EventHandler("updateTaskButton")
    public void updateTaskButton(ClickEvent e) {
        presenter.updateTask(taskDescriptionTextArea.getText(),
                             selectedDate != null ? selectedDate : null,
                             Integer.valueOf(taskPriorityListBox.getValue()));
    }

    @Override
    public void setTaskDescription(final String text) {
        taskDescriptionTextArea.setText(text);
    }

    @Override
    public void setSelectedDate(final Date date) {
        if (date != null) {
            selectedDate = date;
            setupDateTimePicker();
            dateRangePicker.getElement().setAttribute("placeholder",
                                                      DateUtils.getDateTimeStr(selectedDate));
        }
    }


    @Override
    public void setUser(final String user) {
        userText.setText(user);
    }

    @Override
    public void setTaskStatus(final String status) {
        taskStatusText.setText(status);
    }

    @Override
    public void setSlaCompliance(final Integer slaCompliance) {
        slaComplianceText.setText(new SlaStatusConverter().toWidgetValue(slaCompliance));
    }

    @Override
    public void setTaskPriority(final String priority) {
        taskPriorityListBox.setValue(priority);
    }

    @Override
    public void setTaskDescriptionEnabled(final Boolean enabled) {
        taskDescriptionTextArea.setEnabled(enabled);
    }

    @Override
    public void setDueDateEnabled(final Boolean enabled) {
        dateRangePickerInput.setVisible(enabled);
        dueDateText.setVisible(!enabled);
        dueDateText.setText(DateUtils.getDateTimeStr(selectedDate));
    }

    @Override
    public void setTaskPriorityEnabled(final Boolean enabled) {
        taskPriorityListBox.setEnabled(enabled);
        refreshPriorities();
    }

    @Override
    public void setProcessInstanceId(String piid) {
        processInstanceIdLabel.setVisible(!piid.isEmpty());
        processInstanceIdText.setText(piid);
    }

    @Override
    public void setProcessId(String pid) {
        processIdLabel.setVisible(!pid.isEmpty());
        processIdText.setText(pid);
    }

    @Override
    public void setUpdateTaskVisible(final Boolean enabled) {
        updateTaskButton.setVisible(enabled);
    }

    private void refreshPriorities() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                taskPriorityListBox.refresh();
            }
        });
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }
}