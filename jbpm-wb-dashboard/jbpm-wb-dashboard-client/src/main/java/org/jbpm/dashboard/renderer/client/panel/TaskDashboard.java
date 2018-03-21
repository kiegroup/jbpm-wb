/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerListener;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.dashbuilder.renderer.client.table.TableDisplayer;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.common.events.ServerTemplateSelected;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.TASK_DASHBOARD_SCREEN)
public class TaskDashboard extends AbstractDashboard {

    protected View view;
    protected Event<TaskSelectionEvent> taskSelectionEvent;

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
    protected AbstractDisplayer tasksByProcess;
    protected AbstractDisplayer tasksByOwner;
    protected AbstractDisplayer tasksByCreationDate;
    protected AbstractDisplayer tasksByEndDate;
    protected AbstractDisplayer tasksByRunningTime;
    protected AbstractDisplayer tasksByStatus;
    protected TableDisplayer tasksTable;

    protected List<Displayer> metricsGroup = new ArrayList<>();
    protected List<Displayer> metricsGroupOptional = new ArrayList<>();
    protected List<Displayer> chartsGroup = new ArrayList<>();
    protected String totalTasksTitle;
    DisplayerListener dashboardListener = new AbstractDisplayerListener() {

        @Override
        public void onDraw(Displayer displayer) {
            if (totalMetric == displayer) {
                // The dashboard can be considered empty if the total task count is 0
                DataSet ds = displayer.getDataSetHandler().getLastDataSet();
                if (ds.getRowCount() == 0) {
                    showBlankDashboard();
                }
            }
        }

        @Override
        public void onFilterEnabled(Displayer displayer,
                                    DataSetGroup groupOp) {
            if (COLUMN_PROCESS_NAME.equals(groupOp.getColumnGroup().getSourceId())) {
                Interval interval = groupOp.getSelectedIntervalList().get(0);
                changeCurrentProcess(interval.getName());
            }
        }

        @Override
        public void onFilterReset(Displayer displayer,
                                  List<DataSetGroup> groupOps) {
            for (DataSetGroup groupOp : groupOps) {
                if (COLUMN_PROCESS_NAME.equals(groupOp.getColumnGroup().getSourceId())) {
                    resetCurrentProcess();
                    return;
                }
            }
        }

        @Override
        public void onFilterEnabled(Displayer displayer,
                                    DataSetFilter filter) {
            if (metricsGroup.contains(displayer)) {
                changeCurrentMetric((MetricDisplayer) displayer);
            }
        }

        @Override
        public void onFilterReset(Displayer displayer,
                                  DataSetFilter filter) {
            if (metricsGroup.contains(displayer)) {
                resetCurrentMetric();
            }
        }
    };
    private Caller<TaskService> taskDataService;
    private Event<NotificationEvent> notificationEvent;

    @Inject
    public TaskDashboard(final View view,
                         final ProcessBreadCrumb processBreadCrumb,
                         final DataSetClientServices dataSetClientServices,
                         final DisplayerLocator displayerLocator,
                         final DisplayerCoordinator displayerCoordinator,
                         final PlaceManager placeManager,
                         final Event<TaskSelectionEvent> taskSelectionEvent,
                         final ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder,
                         final Caller<TaskService> taskDataService,
                         final Event<NotificationEvent> notificationEvent) {
        super(dataSetClientServices,
              placeManager,
              view.getI18nService(),
              processBreadCrumb,
              displayerLocator,
              displayerCoordinator,
              serverTemplateSelectorMenuBuilder);

        this.view = view;
        this.taskSelectionEvent = taskSelectionEvent;
        this.taskDataService = taskDataService;
        this.notificationEvent = notificationEvent;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.taskDashboardName();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(i18n.taskDashboardName());
    }

    @Override
    public void tableRedraw() {
        tasksTable.filterReset();
        tasksTable.redraw();
    }

    public void onServerTemplateSelected(@Observes final ServerTemplateSelected serverTemplateSelected) {
        //Refresh view
        placeManager.closePlace(PerspectiveIds.TASK_DASHBOARD_SCREEN);
        placeManager.goTo(PerspectiveIds.TASK_DASHBOARD_SCREEN);
    }

    @PostConstruct
    public void init() {

        processBreadCrumb.setOnRootSelectedCommand(this::resetProcessBreadcrumb);

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
                  tasksTable = createTableDisplayer(DashboardKpis.tasksTable(i18n),
                                                    COLUMN_TASK_DURATION,
                                                    new DurationFormatter(COLUMN_TASK_CREATED_DATE,
                                                                          COLUMN_TASK_END_DATE)));

        totalMetric.setFilterOn(true);
        selectedMetric = totalMetric;

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
        displayerCoordinator.drawAll(() -> view.hideLoading(),
                                     () -> view.hideLoading());
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

    public AbstractDisplayer getTasksByProcess() {
        return tasksByProcess;
    }

    public AbstractDisplayer getTasksByOwner() {
        return tasksByOwner;
    }

    public AbstractDisplayer getTasksByCreationDate() {
        return tasksByCreationDate;
    }

    public AbstractDisplayer getTasksByEndDate() {
        return tasksByEndDate;
    }

    public AbstractDisplayer getTasksByRunningTime() {
        return tasksByRunningTime;
    }

    public AbstractDisplayer getTasksByStatus() {
        return tasksByStatus;
    }

    public TableDisplayer getTasksTable() {
        return tasksTable;
    }

    @Override
    @WorkbenchPartView
    public AbstractDashboard.View getView() {
        return view;
    }

    public void resetProcessBreadcrumb() {
        tasksByProcess.filterReset();
        tasksByRunningTime.filterReset();
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
            } else if (readyMetric == selectedMetric) {
                status = i18n.taskStatusReady();
            } else if (reservedMetric == selectedMetric) {
                status = i18n.taskStatusReserved();
            } else if (inProgressMetric == selectedMetric) {
                status = i18n.taskStatusInProgress();
            } else if (suspendedMetric == selectedMetric) {
                status = i18n.taskStatusSuspended();
            } else if (completedMetric == selectedMetric) {
                status = i18n.taskStatusCompleted();
            } else if (failedMetric == selectedMetric) {
                status = i18n.taskStatusFailed();
            } else if (errorMetric == selectedMetric) {
                status = i18n.taskStatusError();
            } else if (exitedMetric == selectedMetric) {
                status = i18n.taskStatusExited();
            } else if (obsoleteMetric == selectedMetric) {
                status = i18n.taskStatusObsolete();
            }
            view.setHeaderText(i18n.selectedTaskStatusHeader(status,
                                                             selectedProcess));
        }
    }

    public void tableCellSelected(String columnId,
                                  int rowIndex) {
        final DataSet ds = tasksTable.getDataSetHandler().getLastDataSet();
        final String status = ds.getValueAt(rowIndex,
                                            COLUMN_TASK_STATUS).toString();
        if (TASK_STATUS_EXITED.equalsIgnoreCase(status) || TASK_STATUS_COMPLETED.equals(status)) {
            notificationEvent.fire(new NotificationEvent(i18n.taskDetailsNotAvailable(),
                                                         NotificationEvent.NotificationType.WARNING));
            return;
        }

        final Long taskId = Double.valueOf(ds.getValueAt(rowIndex,
                                                         COLUMN_TASK_ID).toString()).longValue();
        final String deploymentId = ds.getValueAt(rowIndex,
                                                  COLUMN_PROCESS_EXTERNAL_ID).toString();

        final String serverTemplateId = serverTemplateSelectorMenuBuilder.getSelectedServerTemplate();

        taskDataService.call((TaskSummary taskSummary) -> {
            openTaskDetailsScreen();
            setupDetailBreadcrumb(i18n.taskDashboardName(),
                                  i18n.TaskBreadcrumb(taskId),
                                  TASK_DETAILS_SCREEN);
            taskSelectionEvent.fire(new TaskSelectionEvent(serverTemplateId,
                                                           taskSummary.getDeploymentId(),
                                                           taskSummary.getId(),
                                                           taskSummary.getName(),
                                                           false,
                                                           true,
                                                           taskSummary.getDescription(),
                                                           taskSummary.getExpirationTime(),
                                                           taskSummary.getStatus(),
                                                           taskSummary.getActualOwner(),
                                                           taskSummary.getPriority(),
                                                           taskSummary.getProcessInstanceId(),
                                                           taskSummary.getProcessId()));
        }).getTask(serverTemplateId,
                   deploymentId,
                   taskId);
    }

    public void showDashboard() {
        view.showDashboard();
        closeTaskDetailsScreen();
    }

    public void showTable() {
        view.showInstances();
        tasksTable.redraw();
    }

    public void openTaskDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(TASK_DETAILS_SCREEN);
        if (status == PlaceStatus.CLOSE) {
            placeManager.goTo(TASK_DETAILS_SCREEN);
        }
    }

    public void closeTaskDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(TASK_DETAILS_SCREEN);
        if (status == PlaceStatus.OPEN) {
            placeManager.closePlace(TASK_DETAILS_SCREEN);
        }
    }

    public interface View extends AbstractDashboard.View {

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
    }
}
