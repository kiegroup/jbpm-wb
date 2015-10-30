/**
 * Copyright (C) 2015 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerHelper;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.events.TaskDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.jbpm.dashboard.renderer.client.panel.widgets.MetricDisplayerExt;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.jbpm.dashboard.renderer.client.panel.widgets.TaskTableDisplayer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.jbpm.dashboard.renderer.client.panel.DashboardKpis.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class TaskDashboardPresenter implements ProcessBreadCrumb.Listener, IsWidget {

    protected TaskDashboardView view;
    protected DisplayerCoordinator displayerCoordinator;
    protected DisplayerSettingsJSONMarshaller jsonMarshaller;
    protected PlaceManager placeManager;
    protected Event<TaskSelectionEvent> taskSelectionEvent;
    protected Event<TaskDashboardFocusEvent> taskDashboardFocusEvent;

    protected Displayer totalMetric;
    protected Displayer createdMetric;
    protected Displayer readyMetric;
    protected Displayer reservedMetric;
    protected Displayer inProgressMetric;
    protected Displayer suspendedMetric;
    protected Displayer completedMetric;
    protected Displayer failedMetric;
    protected Displayer errorMetric;
    protected Displayer exitedMetric;
    protected Displayer obsoleteMetric;
    protected Displayer tasksByProcess;
    protected Displayer tasksByOwner;
    protected Displayer tasksByCreationDate;
    protected Displayer tasksByEndDate;
    protected Displayer tasksByRunningTime;
    protected Displayer tasksByStatus;
    protected Displayer tasksTable;

    protected MetricDisplayer selectedMetric = null;
    protected String selectedProcess = null;
    protected List<Displayer> metricsGroup = new ArrayList<Displayer>();
    protected List<Displayer> metricsGroupOptional = new ArrayList<Displayer>();
    protected List<Displayer> chartsGroup = new ArrayList<Displayer>();

    @Inject
    public TaskDashboardPresenter(final TaskDashboardView view,
                                  final DisplayerCoordinator displayerCoordinator,
                                  final DisplayerSettingsJSONMarshaller jsonMarshaller,
                                  final PlaceManager placeManager,
                                  final Event<TaskSelectionEvent> taskSelectionEvent,
                                  final Event<TaskDashboardFocusEvent> taskDashboardFocusEvent) {

        this.view = view;
        this.displayerCoordinator = displayerCoordinator;
        this.jsonMarshaller = jsonMarshaller;
        this.placeManager = placeManager;
        this.taskSelectionEvent = taskSelectionEvent;
        this.taskDashboardFocusEvent = taskDashboardFocusEvent;

        this.init();
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    protected void init() {
        view.showLoading();

        view.init(this,
                totalMetric = createMetricDisplayer(TASKS_TOTAL),
                createdMetric = createMetricDisplayer(TASKS_CREATED),
                readyMetric = createMetricDisplayer(TASKS_READY),
                reservedMetric = createMetricDisplayer(TASKS_RESERVED),
                inProgressMetric = createMetricDisplayer(TASKS_IN_PROGRESS),
                suspendedMetric = createMetricDisplayer(TASKS_SUSPENDED),
                completedMetric = createMetricDisplayer(TASKS_COMPLETED),
                failedMetric = createMetricDisplayer(TASKS_FAILED),
                errorMetric = createMetricDisplayer(TASKS_ERROR),
                exitedMetric = createMetricDisplayer(TASKS_EXITED),
                obsoleteMetric = createMetricDisplayer(TASKS_OBSOLETE),
                tasksByProcess = createDisplayer(TASKS_BY_PROCESS),
                tasksByOwner = createDisplayer(TASKS_BY_OWNER),
                tasksByCreationDate = createDisplayer(TASKS_BY_CREATION_DATE),
                tasksByEndDate = createDisplayer(TASKS_BY_END_DATE),
                tasksByRunningTime = createDisplayer(TASKS_BY_RUNNING_TIME),
                tasksByStatus = createDisplayer(TASKS_BY_STATUS),
                tasksTable = createTableDisplayer(TASKS_TABLE));

        metricsGroup.add(totalMetric);
        metricsGroup.add(readyMetric);
        metricsGroup.add(reservedMetric);
        metricsGroup.add(inProgressMetric);
        metricsGroup.add(suspendedMetric);
        metricsGroup.add(completedMetric);
        metricsGroupOptional.add(createdMetric);
        metricsGroupOptional.add(failedMetric);
        metricsGroupOptional.add(errorMetric);
        metricsGroupOptional.add(exitedMetric);
        metricsGroupOptional.add(obsoleteMetric);

        chartsGroup.add(tasksByProcess);
        chartsGroup.add(tasksByCreationDate);
        chartsGroup.add(tasksByEndDate);
        chartsGroup.add(tasksByOwner);
        chartsGroup.add(tasksByRunningTime);
        chartsGroup.add(tasksByStatus);
        chartsGroup.add(tasksTable);

        displayerCoordinator.addDisplayers(metricsGroup);
        displayerCoordinator.addDisplayers(metricsGroupOptional);
        displayerCoordinator.addDisplayers(chartsGroup);
        displayerCoordinator.addNotificationVeto(metricsGroup);
        displayerCoordinator.addListener(dashboardListener);

        displayerCoordinator.drawAll(new Callback() {
            @Override public void onFailure(Object o) {
                view.hideLoading();
            }

            @Override public void onSuccess(Object o) {
                view.hideLoading();
            }
        });
    }

    @Override
    public void rootSelected() {
        ((AbstractDisplayer) tasksByProcess).filterReset();
        ((AbstractDisplayer) tasksByRunningTime).filterReset();
        tasksByProcess.redraw();
        tasksByRunningTime.redraw();
        view.hideBreadCrumb();
    }

    public Displayer createMetricDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        return new MetricDisplayerExt(settings);
    }

    public Displayer createDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        return DisplayerHelper.lookupDisplayer(settings);
    }

    public Displayer createTableDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        TaskTableDisplayer tableDisplayer = new TaskTableDisplayer(settings, this);
        tableDisplayer.addFormatter(COLUMN_TASK_DURATION, new DurationFormatter(COLUMN_TASK_CREATED_DATE, COLUMN_TASK_END_DATE));
        return tableDisplayer;
    }

    public void resetCurrentMetric(MetricDisplayer metric) {
        selectedMetric = null;
        updateHeaderText();
    }

    public void changeCurrentMetric(MetricDisplayer metric) {
        if (metric.isFilterOn()) {

            // Reset existing metric selected as only a single metric can be filtered at the same time
            if (selectedMetric != null && selectedMetric != metric) {
                selectedMetric.filterReset();
            }
            // Set the selected metric as active
            selectedMetric = metric;

            // Update the header text
            updateHeaderText();

            // Update the selected metric look&feel
            DisplayerSettings clone = selectedMetric.getDisplayerSettings().cloneInstance();
            clone.setChartBackgroundColor("2491C8");
            selectedMetric.applySettings(clone);
        } else {
            selectedMetric = null;
            updateHeaderText();
        }
    }

    public void changeCurrentProcess(String name) {
        selectedProcess = name;
        updateHeaderText();
        view.showBreadCrumb(name);
    }

    public void resetCurrentProcess() {
        selectedProcess = null;
        updateHeaderText();
        view.hideBreadCrumb();
    }

    public void showBlankDashboard() {
        // TODO
    }

    public void updateHeaderText() {
        if (selectedProcess == null) {
            String total = DashboardKpis.TASKS_TOTAL.getTitle();
            String selected = selectedMetric != null ? selectedMetric.getDisplayerSettings().getTitle() : null;
            if (selected == null || total.equals(selected)) {
                view.setHeaderText(DashboardConstants.INSTANCE.allTasks());
            } else {
                view.setHeaderText(selected);
            }
        } else {
            String status = "";
            if (createdMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusCreated();
            else if (readyMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusReady();
            else if (reservedMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusReserved();
            else if (inProgressMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusInProgress();
            else if (suspendedMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusSuspended();
            else if (completedMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusCompleted();
            else if (failedMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusFailed();
            else if (errorMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusError();
            else if (exitedMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusExited();
            else if (obsoleteMetric == selectedMetric) status = DashboardConstants.INSTANCE.taskStatusObsolete();
            view.setHeaderText(DashboardConstants.INSTANCE.selectedTaskStatusHeader(status , selectedProcess));
        }
    }

    public static final String TASK_DETAILS_SCREEN_ID = "Task Details Multi";

    public void tableCellSelected(String columnId, int rowIndex) {
        DataSet ds = tasksTable.getDataSetHandler().getLastDataSet();
        Long taskId = Long.parseLong(ds.getValueAt(rowIndex, COLUMN_TASK_ID).toString());
        String taskName = ds.getValueAt(rowIndex, COLUMN_TASK_NAME).toString();

        openTaskDetailsScreen();

        taskSelectionEvent.fire(new TaskSelectionEvent(taskId, taskName, false, true));
    }

    public void showDashboard() {
        taskDashboardFocusEvent.fire(new TaskDashboardFocusEvent());
        closeTaskDetailsScreen();
    }

    public void showTasksTable() {
        taskDashboardFocusEvent.fire(new TaskDashboardFocusEvent());
        tasksTable.redraw();
    }

    public void openTaskDetailsScreen() {
        taskDashboardFocusEvent.fire(new TaskDashboardFocusEvent());
        PlaceStatus status = placeManager.getStatus(TASK_DETAILS_SCREEN_ID);
        if (status == PlaceStatus.CLOSE) {
            placeManager.goTo(TASK_DETAILS_SCREEN_ID);
        }
    }

    public void closeTaskDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(TASK_DETAILS_SCREEN_ID);
        if (status == PlaceStatus.OPEN) {
            placeManager.closePlace(TASK_DETAILS_SCREEN_ID);
        }
    }

    public void onManagingTasks(@Observes ProcessDashboardFocusEvent event) {
        closeTaskDetailsScreen();
    }

    DisplayerListener dashboardListener = new AbstractDisplayerListener() {

        @Override
        public void onDraw(Displayer displayer) {
            if (totalMetric == displayer) {
                // The dashboard can be considered empty if the total task count is 0;
                DataSet ds = displayer.getDataSetHandler().getLastDataSet();
                if (ds.getRowCount() == 0) {
                    showBlankDashboard();
                }
            }
        }

        @Override
        public void onFilterEnabled(Displayer displayer, DataSetGroup groupOp) {
            if (COLUMN_PROCESS_NAME.equals(groupOp.getColumnGroup().getSourceId())) {
                Interval interval = groupOp.getSelectedIntervalList().get(0);
                changeCurrentProcess(interval.getName());
            }
        }

        @Override
        public void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps) {
            for (DataSetGroup groupOp : groupOps) {
                if (COLUMN_PROCESS_NAME.equals(groupOp.getColumnGroup().getSourceId())) {
                    resetCurrentProcess();
                    return;
                }
            }
        }

        @Override
        public void onFilterEnabled(Displayer displayer, DataSetFilter filter) {
            if (metricsGroup.contains(displayer)) {
                changeCurrentMetric((MetricDisplayer) displayer);
            }
        }

        @Override
        public void onFilterReset(Displayer displayer, DataSetFilter filter) {
            if (metricsGroup.contains(displayer)) {
                resetCurrentMetric((MetricDisplayer) displayer);
            }
        }
    };
}
