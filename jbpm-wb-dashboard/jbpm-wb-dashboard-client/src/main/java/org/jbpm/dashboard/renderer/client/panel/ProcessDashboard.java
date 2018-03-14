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
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.menu.PrimaryActionMenuBuilder;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.common.events.ServerTemplateSelected;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.formatter.DurationFormatter;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.PROCESS_DASHBOARD_SCREEN)
public class ProcessDashboard extends AbstractDashboard {

    protected View view;
    protected Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;
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

    @Inject
    public ProcessDashboard(final View view,
                            final ProcessBreadCrumb processBreadCrumb,
                            final DataSetClientServices dataSetClientServices,
                            final DisplayerLocator displayerLocator,
                            final DisplayerCoordinator displayerCoordinator,
                            final PlaceManager placeManager,
                            final Event<ProcessInstanceSelectionEvent> instanceSelectionEvent,
                            final ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder) {

        super(dataSetClientServices,
              placeManager,
              view.getI18nService(),
              processBreadCrumb,
              displayerLocator,
              displayerCoordinator,
              serverTemplateSelectorMenuBuilder);
        this.view = view;
        this.instanceSelectionEvent = instanceSelectionEvent;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.processDashboardName();
    }

    public void onServerTemplateSelected(@Observes final ServerTemplateSelected serverTemplateSelected) {
        //Refresh view
        placeManager.closePlace(PerspectiveIds.PROCESS_DASHBOARD_SCREEN);
        placeManager.goTo(PerspectiveIds.PROCESS_DASHBOARD_SCREEN);
    }

    @PostConstruct
    public void init() {
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
                  processesTable = createTableDisplayer(DashboardKpis.processesTable(i18n),
                                                        COLUMN_PROCESS_DURATION,
                                                        new DurationFormatter(COLUMN_PROCESS_START_DATE,
                                                                              COLUMN_PROCESS_END_DATE)));

        totalMetric.setFilterOn(true);
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
        displayerCoordinator.drawAll(() -> view.hideLoading(),
                                     () -> view.hideLoading());
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
    @WorkbenchPartView
    public AbstractDashboard.View getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new PrimaryActionMenuBuilder("",
                                                                    "fa-table",
                                                                    i18n.viewTable(),
                                                                    "",
                                                                    "fa-th",
                                                                    i18n.viewDashboard(),
                                                                    () -> {
                                                                        if (view.isDashboardPanelVisible()) {
                                                                            showProcessesTable();
                                                                        } else {
                                                                            showDashboard();
                                                                        }
                                                                    }
                ))
                .endMenu()
                .build();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(i18n.processDashboardName());
    }

    @Override
    public void tableRedraw() {
        processesTable.filterReset();
        processesTable.redraw();
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
            view.setHeaderText(i18n.selectedProcessStatusHeader(status,
                                                                selectedProcess));
        }
    }

    public void tableCellSelected(String columnId,
                                  int rowIndex) {
        DataSet ds = processesTable.getDataSetHandler().getLastDataSet();
        String deploymentId = ds.getValueAt(rowIndex,
                                            COLUMN_PROCESS_EXTERNAL_ID).toString();
        Long processInstanceId = Double.valueOf(ds.getValueAt(rowIndex,
                                                              COLUMN_PROCESS_INSTANCE_ID).toString()).longValue();
        String processDefId = ds.getValueAt(rowIndex,
                                            COLUMN_PROCESS_ID).toString();
        String processDefName = ds.getValueAt(rowIndex,
                                              COLUMN_PROCESS_NAME).toString();
        Integer processInstanceStatus = Double.valueOf(ds.getValueAt(rowIndex,
                                                                     COLUMN_PROCESS_STATUS).toString()).intValue();

        openProcessDetailsScreen();
        setupDetailBreadcrumb(i18n.processDashboardName(),
                              i18n.ProcessInstanceBreadcrumb(processInstanceId),
                              PROCESS_INSTANCE_DETAILS_SCREEN);

        instanceSelectionEvent.fire(new ProcessInstanceSelectionEvent(
                deploymentId,
                processInstanceId,
                processDefId,
                processDefName,
                processInstanceStatus,
                true,
                serverTemplateSelectorMenuBuilder.getSelectedServerTemplate()));
    }

    public void showDashboard() {
        view.showDashboard();
        closeProcessDetailsScreen();
    }

    public void openProcessDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(PROCESS_INSTANCE_DETAILS_SCREEN);
        if (status == PlaceStatus.CLOSE) {
            placeManager.goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
        }
    }

    public void closeProcessDetailsScreen() {
        PlaceStatus status = placeManager.getStatus(PROCESS_INSTANCE_DETAILS_SCREEN);
        if (status == PlaceStatus.OPEN) {
            placeManager.closePlace(PROCESS_INSTANCE_DETAILS_SCREEN);
        }
    }

    public void showProcessesTable() {
        view.showInstances();
        processesTable.redraw();
    }

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
}

