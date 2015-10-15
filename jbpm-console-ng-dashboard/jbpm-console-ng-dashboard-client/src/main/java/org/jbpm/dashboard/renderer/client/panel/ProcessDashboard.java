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
import org.dashbuilder.displayer.client.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.events.TaskDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.jbpm.dashboard.renderer.client.panel.widgets.MetricDisplayerExt;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessTableDisplayer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.jbpm.dashboard.renderer.client.panel.DashboardKpis.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ProcessDashboard implements ProcessBreadCrumb.Listener, IsWidget {

    protected ProcessDashboardView view;
    protected DisplayerCoordinator displayerCoordinator;
    protected DisplayerSettingsJSONMarshaller jsonMarshaller;
    protected PlaceManager placeManager;
    protected Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;
    protected Event<ProcessDashboardFocusEvent> processDashboardFocusEvent;

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

    @Inject
    public ProcessDashboard(final ProcessDashboardView view,
                            final DisplayerCoordinator displayerCoordinator,
                            final DisplayerSettingsJSONMarshaller jsonMarshaller,
                            final PlaceManager placeManager,
                            final Event<ProcessInstanceSelectionEvent> instanceSelectionEvent,
                            final Event<ProcessDashboardFocusEvent> processDashboardFocusEvent) {

        this.view = view;
        this.displayerCoordinator = displayerCoordinator;
        this.jsonMarshaller = jsonMarshaller;
        this.placeManager = placeManager;
        this.instanceSelectionEvent = instanceSelectionEvent;
        this.processDashboardFocusEvent = processDashboardFocusEvent;

        this.init();
    }

    protected void init() {
        view.showLoading();

        view.init(this,
                totalMetric = createMetricDisplayer(PROCESSES_TOTAL),
                activeMetric = createMetricDisplayer(PROCESSES_ACTIVE),
                pendingMetric = createMetricDisplayer(PROCESSES_PENDING),
                suspendedMetric = createMetricDisplayer(PROCESSES_SUSPENDED),
                abortedMetric = createMetricDisplayer(PROCESSES_ABORTED),
                completedMetric = createMetricDisplayer(PROCESSES_COMPLETED),
                processesByType = createDisplayer(PROCESSES_BY_TYPE),
                processesByUser = createDisplayer(PROCESSES_BY_USER),
                processesByStartDate = createDisplayer(PROCESSES_BY_START_DATE),
                processesByEndDate = createDisplayer(PROCESSES_BY_END_DATE),
                processesByRunningTime = createDisplayer(PROCESSES_BY_RUNNING_TIME),
                processesByVersion = createDisplayer(PROCESSES_BY_VERSION),
                processesTable = createTableDisplayer(PROCESSES_TABLE));

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

        displayerCoordinator.drawAll(new Callback() {
            @Override public void onFailure(Object o) {
                view.hideLoading();
            }

            @Override public void onSuccess(Object o) {
                view.hideLoading();
            }
        });
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void rootSelected() {
        ((AbstractDisplayer) processesByType).filterReset();
        ((AbstractDisplayer) processesByRunningTime).filterReset();
        processesByType.redraw();
        processesByRunningTime.redraw();
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
        ProcessTableDisplayer tableDisplayer = new ProcessTableDisplayer(settings, this);
        tableDisplayer.addFormatter(COLUMN_PROCESS_DURATION, new DurationFormatter(COLUMN_PROCESS_START_DATE, COLUMN_PROCESS_END_DATE));
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

    public void showBlankDashboard() {
        // TODO
    }

    public void updateHeaderText() {
        if (selectedProcess == null) {
            String total = DashboardKpis.PROCESSES_TOTAL.getTitle();
            String selected = selectedMetric != null ? selectedMetric.getDisplayerSettings().getTitle() : null;
            if (selected == null || total.equals(selected)) {
                view.setHeaderText(DashboardConstants.INSTANCE.allProcesses());
            } else {
                view.setHeaderText(selected);
            }
        } else {
            String status = "";
            if (activeMetric == selectedMetric) status = DashboardConstants.INSTANCE.processStatusActive();
            else if (pendingMetric == selectedMetric) status = DashboardConstants.INSTANCE.processStatusPending();
            else if (suspendedMetric == selectedMetric) status = DashboardConstants.INSTANCE.processStatusSuspended();
            else if (abortedMetric == selectedMetric) status = DashboardConstants.INSTANCE.processStatusAborted();
            else if (completedMetric == selectedMetric) status = DashboardConstants.INSTANCE.processStatusCompleted();
            view.setHeaderText(DashboardConstants.INSTANCE.selectedProcessStatusHeader(status, selectedProcess));
        }
    }

    public static final String PROCESS_DETAILS_SCREEN_ID = "Process Instance Details Multi";

    public void tableCellSelected(String columnId, int rowIndex) {
        DataSet ds = processesTable.getDataSetHandler().getLastDataSet();
        String deploymentId = ds.getValueAt(rowIndex, COLUMN_PROCESS_EXTERNAL_ID).toString();
        Long processInstanceId = Long.parseLong(ds.getValueAt(rowIndex, COLUMN_PROCESS_INSTANCE_ID).toString());
        String processDefId = ds.getValueAt(rowIndex, COLUMN_PROCESS_ID).toString();
        String processDefName = ds.getValueAt(rowIndex, COLUMN_PROCESS_NAME).toString();
        Integer processInstanceStatus = Integer.parseInt(ds.getValueAt(rowIndex, COLUMN_PROCESS_STATUS).toString());

        openProcessDetailsScreen();

        instanceSelectionEvent.fire(new ProcessInstanceSelectionEvent(
                deploymentId, processInstanceId,
                processDefId, processDefName,
                processInstanceStatus));
    }

    public void showDashboard() {
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

