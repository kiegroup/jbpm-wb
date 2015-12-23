/**
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.

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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.dashbuilder.renderer.client.table.TableDisplayer;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.events.TaskDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardI18n;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.Command;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class TaskDashboard implements IsWidget {

    public interface View extends IsWidget {

        void init(TaskDashboard presenter,
                  Displayer totalMetric,
                  Displayer createdMetric,
                  Displayer readyMetric,
                  Displayer reservedMetric,
                  Displayer inProgressMetric,
                  Displayer suspendedMetric,
                  Displayer completedMetric,
                  Displayer failedMetric,
                  Displayer errorMetric,
                  Displayer exitedMetric,
                  Displayer obsoleteMetric,
                  Displayer tasksByProcess,
                  Displayer tasksByOwner,
                  Displayer tasksByCreationDate,
                  Displayer tasksByEndDate,
                  Displayer tasksByRunningTime,
                  Displayer tasksByStatus,
                  Displayer tasksTable);

        void showBreadCrumb(String processName);

        void hideBreadCrumb();

        void setHeaderText(String text);

        void showLoading();

        void hideLoading();

        void showDashboard();

        void showInstances();

        DashboardI18n getI18nService();
    }

    protected View view;
    protected ProcessBreadCrumb processBreadCrumb;
    protected DashboardFactory dashboardFactory;
    protected DataSetClientServices dataSetClientServices;
    protected DisplayerLocator displayerLocator;
    protected DisplayerCoordinator displayerCoordinator;
    protected PlaceManager placeManager;
    protected Event<TaskSelectionEvent> taskSelectionEvent;
    protected Event<TaskDashboardFocusEvent> taskDashboardFocusEvent;
    protected DashboardI18n i18n;

    protected MetricDisplayer totalMetric;
    protected MetricDisplayer createdMetric;
    protected MetricDisplayer readyMetric;
    protected MetricDisplayer reservedMetric;
    protected MetricDisplayer inProgressMetric;
    protected MetricDisplayer suspendedMetric;
    protected MetricDisplayer completedMetric;
    protected MetricDisplayer failedMetric;
    protected MetricDisplayer errorMetric;
    protected MetricDisplayer exitedMetric;
    protected MetricDisplayer obsoleteMetric;
    protected Displayer tasksByProcess;
    protected Displayer tasksByOwner;
    protected Displayer tasksByCreationDate;
    protected Displayer tasksByEndDate;
    protected Displayer tasksByRunningTime;
    protected Displayer tasksByStatus;
    protected TableDisplayer tasksTable;

    protected MetricDisplayer selectedMetric = null;
    protected String selectedProcess = null;
    protected List<Displayer> metricsGroup = new ArrayList<Displayer>();
    protected List<Displayer> metricsGroupOptional = new ArrayList<Displayer>();
    protected List<Displayer> chartsGroup = new ArrayList<Displayer>();
    protected String totalTasksTitle;

    @Inject
    public TaskDashboard(final View view,
                         final ProcessBreadCrumb processBreadCrumb,
                         final DashboardFactory dashboardFactory,
                         final DataSetClientServices dataSetClientServices,
                         final DisplayerLocator displayerLocator,
                         final DisplayerCoordinator displayerCoordinator,
                         final PlaceManager placeManager,
                         final Event<TaskSelectionEvent> taskSelectionEvent,
                         final Event<TaskDashboardFocusEvent> taskDashboardFocusEvent) {

        this.view = view;
        this.i18n = view.getI18nService();
        this.processBreadCrumb = processBreadCrumb;
        this.dashboardFactory = dashboardFactory;
        this.dataSetClientServices = dataSetClientServices;
        this.displayerLocator = displayerLocator;
        this.displayerCoordinator = displayerCoordinator;
        this.placeManager = placeManager;
        this.taskSelectionEvent = taskSelectionEvent;
        this.taskDashboardFocusEvent = taskDashboardFocusEvent;

        this.init();
    }

    protected void init() {

        processBreadCrumb.setOnRootSelectedCommand(new Command() {
            public void execute() {
                resetProcessBreadcrumb();
            }
        });

        view.showLoading();

        DisplayerSettings totalTasks = DashboardKpis.tasksTotal(i18n);
        totalTasksTitle = totalTasks.getTitle();

        view.init(this,
                totalMetric = createMetricDisplayer(totalTasks),
                createdMetric = createMetricDisplayer(DashboardKpis.tasksCreated(i18n)),
                readyMetric = createMetricDisplayer(DashboardKpis.tasksReady(i18n)),
                reservedMetric = createMetricDisplayer(DashboardKpis.tasksReserved(i18n)),
                inProgressMetric = createMetricDisplayer(DashboardKpis.tasksInProgress(i18n)),
                suspendedMetric = createMetricDisplayer(DashboardKpis.tasksSuspended(i18n)),
                completedMetric = createMetricDisplayer(DashboardKpis.tasksCompleted(i18n)),
                failedMetric = createMetricDisplayer(DashboardKpis.tasksFailed(i18n)),
                errorMetric = createMetricDisplayer(DashboardKpis.tasksError(i18n)),
                exitedMetric = createMetricDisplayer(DashboardKpis.tasksExited(i18n)),
                obsoleteMetric = createMetricDisplayer(DashboardKpis.tasksObsolete(i18n)),
                tasksByProcess = createDisplayer(DashboardKpis.tasksByProcess(i18n)),
                tasksByOwner = createDisplayer(DashboardKpis.tasksByOwner(i18n)),
                tasksByCreationDate = createDisplayer(DashboardKpis.tasksByCreationDate(i18n)),
                tasksByEndDate = createDisplayer(DashboardKpis.tasksByEndDate(i18n)),
                tasksByRunningTime = createDisplayer(DashboardKpis.tasksByRunningTime(i18n)),
                tasksByStatus = createDisplayer(DashboardKpis.tasksByStatus(i18n)),
                tasksTable = createTableDisplayer(DashboardKpis.tasksTable(i18n)));

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

        displayerCoordinator.drawAll(
                // On success
                new Command() {
                    public void execute() {
                        view.hideLoading();
                        totalMetric.filterApply();
                    }
                },
                // On Failure
                new Command() {
                    public void execute() {
                        view.hideLoading();
                    }
                }
        );
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public ProcessBreadCrumb getProcessBreadCrumb() {
        return processBreadCrumb;
    }

    public MetricDisplayer getTotalMetric() {
        return totalMetric;
    }

    public MetricDisplayer getCreatedMetric() {
        return createdMetric;
    }

    public MetricDisplayer getReadyMetric() {
        return readyMetric;
    }

    public MetricDisplayer getReservedMetric() {
        return reservedMetric;
    }

    public MetricDisplayer getInProgressMetric() {
        return inProgressMetric;
    }

    public MetricDisplayer getSuspendedMetric() {
        return suspendedMetric;
    }

    public MetricDisplayer getCompletedMetric() {
        return completedMetric;
    }

    public MetricDisplayer getFailedMetric() {
        return failedMetric;
    }

    public MetricDisplayer getErrorMetric() {
        return errorMetric;
    }

    public MetricDisplayer getExitedMetric() {
        return exitedMetric;
    }

    public MetricDisplayer getObsoleteMetric() {
        return obsoleteMetric;
    }

    public Displayer getTasksByProcess() {
        return tasksByProcess;
    }

    public Displayer getTasksByOwner() {
        return tasksByOwner;
    }

    public Displayer getTasksByCreationDate() {
        return tasksByCreationDate;
    }

    public Displayer getTasksByEndDate() {
        return tasksByEndDate;
    }

    public Displayer getTasksByRunningTime() {
        return tasksByRunningTime;
    }

    public Displayer getTasksByStatus() {
        return tasksByStatus;
    }

    public TableDisplayer getTasksTable() {
        return tasksTable;
    }

    public MetricDisplayer getSelectedMetric() {
        return selectedMetric;
    }

    public String getSelectedProcess() {
        return selectedProcess;
    }

    public MetricDisplayer createMetricDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        MetricDisplayer metricDisplayer = dashboardFactory.createMetricDisplayer();
        metricDisplayer.setDisplayerSettings(settings);
        metricDisplayer.setDataSetHandler(new DataSetHandlerImpl(dataSetClientServices, settings.getDataSetLookup()));
        return metricDisplayer;
    }

    public Displayer createDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        return displayerLocator.lookupDisplayer(settings);
    }

    public TableDisplayer createTableDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        final TableDisplayer tableDisplayer = dashboardFactory.createTableDisplayer();
        tableDisplayer.setDisplayerSettings(settings);
        tableDisplayer.setDataSetHandler(new DataSetHandlerImpl(dataSetClientServices, settings.getDataSetLookup()));
        tableDisplayer.addFormatter(COLUMN_TASK_DURATION, new DurationFormatter(COLUMN_TASK_CREATED_DATE, COLUMN_TASK_END_DATE));
        tableDisplayer.setOnCellSelectedCommand(new Command() {
            public void execute() {
                tableCellSelected(tableDisplayer.getSelectedCellColumn(), tableDisplayer.getSelectedCellRow());
            }
        });
        return tableDisplayer;
    }

    public void resetCurrentMetric() {
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

    public void resetProcessBreadcrumb() {
        ((AbstractDisplayer) tasksByProcess).filterReset();
        ((AbstractDisplayer) tasksByRunningTime).filterReset();
        tasksByProcess.redraw();
        tasksByRunningTime.redraw();
        view.hideBreadCrumb();
    }

    public void showBlankDashboard() {
        // TODO
    }

    public void updateHeaderText() {
        if (selectedProcess == null) {
            String selected = selectedMetric != null ? selectedMetric.getDisplayerSettings().getTitle() : null;
            if (selected == null || totalTasksTitle.equals(selected)) {
                view.setHeaderText(i18n.allTasks());
            } else {
                view.setHeaderText(selected);
            }
        } else {
            String status = "";
            if (createdMetric == selectedMetric) {
                status = i18n.taskStatusCreated();
            }
            else if (readyMetric == selectedMetric) {
                status = i18n.taskStatusReady();
            }
            else if (reservedMetric == selectedMetric) {
                status = i18n.taskStatusReserved();
            }
            else if (inProgressMetric == selectedMetric) {
                status = i18n.taskStatusInProgress();
            }
            else if (suspendedMetric == selectedMetric) {
                status = i18n.taskStatusSuspended();
            }
            else if (completedMetric == selectedMetric) {
                status = i18n.taskStatusCompleted();
            }
            else if (failedMetric == selectedMetric) {
                status = i18n.taskStatusFailed();
            }
            else if (errorMetric == selectedMetric) {
                status = i18n.taskStatusError();
            }
            else if (exitedMetric == selectedMetric) {
                status = i18n.taskStatusExited();
            }
            else if (obsoleteMetric == selectedMetric) {
                status = i18n.taskStatusObsolete();
            }
            view.setHeaderText(i18n.selectedTaskStatusHeader(status , selectedProcess));
        }
    }

    public static final String TASK_DETAILS_SCREEN_ID = "Task Details Multi";

    public void tableCellSelected(String columnId, int rowIndex) {
        DataSet ds = tasksTable.getDataSetHandler().getLastDataSet();
        Long taskId = Double.valueOf(ds.getValueAt(rowIndex, COLUMN_TASK_ID).toString()).longValue();
        String taskName = ds.getValueAt(rowIndex, COLUMN_TASK_NAME).toString();

        openTaskDetailsScreen();

        taskSelectionEvent.fire(new TaskSelectionEvent(taskId, taskName, false, true));
    }

    public void showDashboard() {
        view.showDashboard();
        taskDashboardFocusEvent.fire(new TaskDashboardFocusEvent());
        closeTaskDetailsScreen();
    }

    public void showTasksTable() {
        view.showInstances();
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
                resetCurrentMetric();
            }
        }
    };
}
