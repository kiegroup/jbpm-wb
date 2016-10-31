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
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
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
import org.jbpm.console.ng.gc.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.events.TaskDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.Command;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;

@Dependent
public class ProcessDashboard extends AbstractDashboard implements IsWidget {

    public static final String PROCESS_DETAILS_SCREEN_ID = "Process Instance Details Multi";

    public interface View extends AbstractDashboard.View {

        void init(ProcessDashboard presenter,
                  Displayer totalMetric,
                  Displayer activeMetric,
                  Displayer pendingMetric,
                  Displayer suspendedMetric,
                  Displayer abortedMetric,
                  Displayer completedMetric,
                  Displayer processesByType,
                  Displayer processesByUser,
                  Displayer processesByStartDate,
                  Displayer processesByEndDate,
                  Displayer processesByRunningTime,
                  Displayer processesByVersion,
                  Displayer processesTable);

    }

    protected View view;
    protected Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;
    protected Event<ProcessDashboardFocusEvent> processDashboardFocusEvent;

    protected MetricDisplayer totalMetric;
    protected MetricDisplayer activeMetric;
    protected MetricDisplayer pendingMetric;
    protected MetricDisplayer suspendedMetric;
    protected MetricDisplayer abortedMetric;
    protected MetricDisplayer completedMetric;
    protected AbstractDisplayer processesByType;
    protected AbstractDisplayer processesByUser;
    protected AbstractDisplayer processesByStartDate;
    protected AbstractDisplayer processesByEndDate;
    protected AbstractDisplayer processesByRunningTime;
    protected AbstractDisplayer processesByVersion;
    protected TableDisplayer processesTable;

    protected List<Displayer> metricsGroup = new ArrayList<>();
    protected List<Displayer> chartsGroup = new ArrayList<>();
    protected String totalProcessesTitle;

    @Inject
    public ProcessDashboard(final View view,
                            final ProcessBreadCrumb processBreadCrumb,
                            final DataSetClientServices dataSetClientServices,
                            final DisplayerLocator displayerLocator,
                            final DisplayerCoordinator displayerCoordinator,
                            final PlaceManager placeManager,
                            final Event<ProcessInstanceSelectionEvent> instanceSelectionEvent,
                            final Event<ProcessDashboardFocusEvent> processDashboardFocusEvent,
                            final ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder) {

        super(dataSetClientServices, placeManager, view.getI18nService(), processBreadCrumb, displayerLocator, displayerCoordinator, serverTemplateSelectorMenuBuilder);
        this.view = view;
        this.instanceSelectionEvent = instanceSelectionEvent;
        this.processDashboardFocusEvent = processDashboardFocusEvent;

        this.init();
    }

    protected void init() {
        processBreadCrumb.setOnRootSelectedCommand(this::resetProcessBreadcrumb);

        view.showLoading();

        DisplayerSettings totalProcesses = DashboardKpis.processTotal(i18n);
        totalProcessesTitle = totalProcesses.getTitle();

        view.init(this,
                totalMetric = createMetricDisplayer(totalProcesses),
                activeMetric = createMetricDisplayer(DashboardKpis.processesActive(i18n)),
                pendingMetric = createMetricDisplayer(DashboardKpis.processesPending(i18n)),
                suspendedMetric = createMetricDisplayer(DashboardKpis.processesSuspended(i18n)),
                abortedMetric = createMetricDisplayer(DashboardKpis.processesAborted(i18n)),
                completedMetric = createMetricDisplayer(DashboardKpis.processesCompleted(i18n)),
                processesByType = createDisplayer(DashboardKpis.processesByType(i18n)),
                processesByUser = createDisplayer(DashboardKpis.processesByUser(i18n)),
                processesByStartDate = createDisplayer(DashboardKpis.processesByStartDate(i18n)),
                processesByEndDate = createDisplayer(DashboardKpis.processesByEndDate(i18n)),
                processesByRunningTime = createDisplayer(DashboardKpis.processesByRunningTime(i18n)),
                processesByVersion = createDisplayer(DashboardKpis.processesByVersion(i18n)),
                processesTable = createTableDisplayer(DashboardKpis.processesTable(i18n), COLUMN_PROCESS_DURATION, new DurationFormatter(COLUMN_PROCESS_START_DATE, COLUMN_PROCESS_END_DATE)));

        totalMetric.filterApply();
        selectedMetric = totalMetric;

        metricsGroup.add(totalMetric);
        metricsGroup.add(activeMetric);
        metricsGroup.add(pendingMetric);
        metricsGroup.add(suspendedMetric);
        metricsGroup.add(abortedMetric);
        metricsGroup.add(completedMetric);

        chartsGroup.add(processesByType);
        chartsGroup.add(processesByStartDate);
        chartsGroup.add(processesByEndDate);
        chartsGroup.add(processesByUser);
        chartsGroup.add(processesByRunningTime);
        chartsGroup.add(processesByVersion);
        chartsGroup.add(processesTable);

        displayerCoordinator.addDisplayers(metricsGroup);
        displayerCoordinator.addDisplayers(chartsGroup);
        displayerCoordinator.addNotificationVeto(metricsGroup);
        displayerCoordinator.addListener(dashboardListener);

        displayerCoordinator.drawAll(
                // On success
                new Command() {
                    public void execute() {
                        view.hideLoading();
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

    public MetricDisplayer getTotalMetric() {
        return totalMetric;
    }

    public MetricDisplayer getActiveMetric() {
        return activeMetric;
    }

    public MetricDisplayer getPendingMetric() {
        return pendingMetric;
    }

    public MetricDisplayer getSuspendedMetric() {
        return suspendedMetric;
    }

    public MetricDisplayer getAbortedMetric() {
        return abortedMetric;
    }

    public MetricDisplayer getCompletedMetric() {
        return completedMetric;
    }

    public AbstractDisplayer getProcessesByType() {
        return processesByType;
    }

    public AbstractDisplayer getProcessesByUser() {
        return processesByUser;
    }

    public AbstractDisplayer getProcessesByStartDate() {
        return processesByStartDate;
    }

    public AbstractDisplayer getProcessesByEndDate() {
        return processesByEndDate;
    }

    public AbstractDisplayer getProcessesByRunningTime() {
        return processesByRunningTime;
    }

    public AbstractDisplayer getProcessesByVersion() {
        return processesByVersion;
    }

    public TableDisplayer getProcessesTable() {
        return processesTable;
    }

    @Override
    public AbstractDashboard.View getView() {
        return view;
    }

    public void resetProcessBreadcrumb() {
        processesByType.filterReset();
        processesByRunningTime.filterReset();
        processesByType.redraw();
        processesByRunningTime.redraw();
        view.hideBreadCrumb();
    }

    public void showBlankDashboard() {
        // TODO
    }

    public void updateHeaderText() {
        if (selectedProcess == null) {
            String selected = selectedMetric != null ? selectedMetric.getDisplayerSettings().getTitle() : null;
            if (selected == null || totalProcessesTitle.equals(selected)) {
                view.setHeaderText(i18n.allProcesses());
            } else {
                view.setHeaderText(selected);
            }
        } else {
            String status = "";
            if (activeMetric == selectedMetric) {
                status = i18n.processStatusActive();
            } else if (pendingMetric == selectedMetric) {
                status = i18n.processStatusPending();
            } else if (suspendedMetric == selectedMetric) {
                status = i18n.processStatusSuspended();
            } else if (abortedMetric == selectedMetric) {
                status = i18n.processStatusAborted();
            } else if (completedMetric == selectedMetric) {
                status = i18n.processStatusCompleted();
            }
            view.setHeaderText(i18n.selectedProcessStatusHeader(status, selectedProcess));
        }
    }

    public void tableCellSelected(String columnId, int rowIndex) {
        DataSet ds = processesTable.getDataSetHandler().getLastDataSet();
        String deploymentId = ds.getValueAt(rowIndex, COLUMN_PROCESS_EXTERNAL_ID).toString();
        Long processInstanceId = Double.valueOf(ds.getValueAt(rowIndex, COLUMN_PROCESS_INSTANCE_ID).toString()).longValue();
        String processDefId = ds.getValueAt(rowIndex, COLUMN_PROCESS_ID).toString();
        String processDefName = ds.getValueAt(rowIndex, COLUMN_PROCESS_NAME).toString();
        Integer processInstanceStatus = Double.valueOf(ds.getValueAt(rowIndex, COLUMN_PROCESS_STATUS).toString()).intValue();

        openProcessDetailsScreen();

        instanceSelectionEvent.fire(new ProcessInstanceSelectionEvent(
                deploymentId, processInstanceId,
                processDefId, processDefName,
                processInstanceStatus, true,
                serverTemplateSelectorMenuBuilder.getSelectedServerTemplate()));
    }

    public void showDashboard() {
        view.showDashboard();
        processDashboardFocusEvent.fire(new ProcessDashboardFocusEvent());
        closeProcessDetailsScreen();
    }

    public void openProcessDetailsScreen() {
        processDashboardFocusEvent.fire(new ProcessDashboardFocusEvent());
        PlaceStatus status = placeManager.getStatus(PROCESS_DETAILS_SCREEN_ID);
        if (status == PlaceStatus.CLOSE) {
            placeManager.goTo(PROCESS_DETAILS_SCREEN_ID);
        }
    }

    public void closeProcessDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(PROCESS_DETAILS_SCREEN_ID);
        if (status == PlaceStatus.OPEN) {
            placeManager.closePlace(PROCESS_DETAILS_SCREEN_ID);
        }
    }

    public void showProcessesTable() {
        view.showInstances();
        processesTable.redraw();
        processDashboardFocusEvent.fire(new ProcessDashboardFocusEvent());
    }

    public void onManagingTasks(@Observes TaskDashboardFocusEvent event) {
        closeProcessDetailsScreen();
    }

    DisplayerListener dashboardListener = new AbstractDisplayerListener() {

        @Override
        public void onDraw(Displayer displayer) {
            if (totalMetric == displayer) {
                // The dashboard can be considered empty if the total process count is 0
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

