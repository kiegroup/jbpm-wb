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
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
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
public class ProcessDashboard implements IsWidget {

    public interface View extends IsWidget {

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
    protected Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;
    protected Event<ProcessDashboardFocusEvent> processDashboardFocusEvent;
    protected DashboardI18n i18n;

    protected Displayer totalMetric;
    protected Displayer activeMetric;
    protected Displayer pendingMetric;
    protected Displayer suspendedMetric;
    protected Displayer abortedMetric;
    protected Displayer completedMetric;
    protected Displayer processesByType;
    protected Displayer processesByUser;
    protected Displayer processesByStartDate;
    protected Displayer processesByEndDate;
    protected Displayer processesByRunningTime;
    protected Displayer processesByVersion;
    protected Displayer processesTable;

    protected MetricDisplayer selectedMetric = null;
    protected String selectedProcess = null;
    protected List<Displayer> metricsGroup = new ArrayList<Displayer>();
    protected List<Displayer> chartsGroup = new ArrayList<Displayer>();
    protected String totalProcessesTitle;

    @Inject
    public ProcessDashboard(final View view,
                            final ProcessBreadCrumb processBreadCrumb,
                            final DashboardFactory dashboardFactory,
                            final DataSetClientServices dataSetClientServices,
                            final DisplayerLocator displayerLocator,
                            final DisplayerCoordinator displayerCoordinator,
                            final PlaceManager placeManager,
                            final Event<ProcessInstanceSelectionEvent> instanceSelectionEvent,
                            final Event<ProcessDashboardFocusEvent> processDashboardFocusEvent) {

        this.view = view;
        this.i18n = view.getI18nService();
        this.processBreadCrumb = processBreadCrumb;
        this.dashboardFactory = dashboardFactory;
        this.dataSetClientServices = dataSetClientServices;
        this.displayerLocator = displayerLocator;
        this.displayerCoordinator = displayerCoordinator;
        this.placeManager = placeManager;
        this.instanceSelectionEvent = instanceSelectionEvent;
        this.processDashboardFocusEvent = processDashboardFocusEvent;

        this.init();
    }

    protected void init() {
        processBreadCrumb.setOnRootSelectedCommand(new Command() {
            public void execute() {
                resetProcessBreadcrumb();
            }
        });

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
                processesTable = createTableDisplayer(DashboardKpis.processesTable(i18n)));

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

    public Widget asWidget() {
        return view.asWidget();
    }

    public ProcessBreadCrumb getProcessBreadCrumb() {
        return processBreadCrumb;
    }

    public Displayer getTotalMetric() {
        return totalMetric;
    }

    public Displayer getActiveMetric() {
        return activeMetric;
    }

    public Displayer getPendingMetric() {
        return pendingMetric;
    }

    public Displayer getSuspendedMetric() {
        return suspendedMetric;
    }

    public Displayer getAbortedMetric() {
        return abortedMetric;
    }

    public Displayer getCompletedMetric() {
        return completedMetric;
    }

    public Displayer getProcessesByType() {
        return processesByType;
    }

    public Displayer getProcessesByUser() {
        return processesByUser;
    }

    public Displayer getProcessesByStartDate() {
        return processesByStartDate;
    }

    public Displayer getProcessesByEndDate() {
        return processesByEndDate;
    }

    public Displayer getProcessesByRunningTime() {
        return processesByRunningTime;
    }

    public Displayer getProcessesByVersion() {
        return processesByVersion;
    }

    public Displayer getProcessesTable() {
        return processesTable;
    }

    public MetricDisplayer getSelectedMetric() {
        return selectedMetric;
    }

    public String getSelectedProcess() {
        return selectedProcess;
    }

    public Displayer createMetricDisplayer(DisplayerSettings settings) {
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

    public Displayer createTableDisplayer(DisplayerSettings settings) {
        checkNotNull("displayerSettings", settings);
        final TableDisplayer tableDisplayer = dashboardFactory.createTableDisplayer();
        tableDisplayer.setDisplayerSettings(settings);
        tableDisplayer.setDataSetHandler(new DataSetHandlerImpl(dataSetClientServices, settings.getDataSetLookup()));
        tableDisplayer.addFormatter(COLUMN_PROCESS_DURATION, new DurationFormatter(COLUMN_PROCESS_START_DATE, COLUMN_PROCESS_END_DATE));
        tableDisplayer.setOnCellSelectedCommand(new Command() {
            public void execute() {
                tableCellSelected(tableDisplayer.getSelectedCellColumn(), tableDisplayer.getSelectedCellRow());
            }
        });
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
        }
        else {
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
        ((AbstractDisplayer) processesByType).filterReset();
        ((AbstractDisplayer) processesByRunningTime).filterReset();
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
            }
            else if (pendingMetric == selectedMetric) {
                status = i18n.processStatusPending();
            }
            else if (suspendedMetric == selectedMetric) {
                status = i18n.processStatusSuspended();
            }
            else if (abortedMetric == selectedMetric) {
                status = i18n.processStatusAborted();
            }
            else if (completedMetric == selectedMetric) {
                status = i18n.processStatusCompleted();
            }
            view.setHeaderText(i18n.selectedProcessStatusHeader(status, selectedProcess));
        }
    }

    public static final String PROCESS_DETAILS_SCREEN_ID = "Process Instance Details Multi";

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
                processInstanceStatus));
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
                // The dashboard can be considered empty if the total process count is 0;
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

