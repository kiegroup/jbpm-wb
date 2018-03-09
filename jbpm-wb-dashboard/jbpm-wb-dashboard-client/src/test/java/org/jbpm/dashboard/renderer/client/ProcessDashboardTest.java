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
package org.jbpm.dashboard.renderer.client;

import javax.enterprise.event.Event;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.jbpm.dashboard.renderer.client.panel.DashboardKpis;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.AbstractDashboard;
import org.jbpm.dashboard.renderer.client.panel.ProcessDashboard;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;

import static org.dashbuilder.dataset.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_DETAILS_SCREEN;

@RunWith(MockitoJUnitRunner.class)
public class ProcessDashboardTest extends AbstractDashboardTest {

    @Mock
    ProcessDashboard.View view;

    @Mock
    ProcessBreadCrumb processBreadCrumb;

    @Mock
    Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;

    @Mock
    DisplayerListener totalMetricListener;

    @Mock
    PerspectiveManager perspectiveManagerMock;

    @Mock
    UberfireBreadcrumbs uberfireBreadcrumbsMock;

    ProcessDashboard presenter;
    DataSet dataSet;

    @Override
    public void registerDataset() throws Exception {
        dataSet = ProcessDashboardData.INSTANCE.toDataSet();
        dataSet.setUUID(DATASET_PROCESS_INSTANCES);
        clientDataSetManager.registerDataSet(dataSet);
    }

    @Override
    protected AbstractDashboard.View getView() {
        return view;
    }

    @Override
    protected AbstractDashboard getPresenter() {
        return presenter;
    }

    @Override
    public AbstractDisplayer createNewDisplayer(DisplayerSettings settings) {
        AbstractDisplayer displayer = super.createNewDisplayer(settings);
        if (DashboardKpis.TOTAL_PROCESSES_METRIC.equals(settings.getUUID())) {
            displayer.addListener(totalMetricListener);
        }
        return displayer;
    }

    @Before
    public void init() throws Exception {
        super.init();

        presenter = new ProcessDashboard(view,
                                         processBreadCrumb,
                                         clientServices,
                                         displayerLocator,
                                         displayerCoordinator,
                                         placeManager,
                                         instanceSelectionEvent,
                                         serverTemplateSelectorMenuBuilder);
        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(mock(PerspectiveActivity.class));
        presenter.setPerspectiveManager(perspectiveManagerMock);
        presenter.setUberfireBreadcrumbs(uberfireBreadcrumbsMock);
        presenter.init();
    }

    @Test
    public void testDrawAll() {

        verify(view).init(presenter,
                          presenter.getTotalMetric(),
                          presenter.getActiveMetric(),
                          presenter.getPendingMetric(),
                          presenter.getSuspendedMetric(),
                          presenter.getAbortedMetric(),
                          presenter.getCompletedMetric(),
                          presenter.getProcessesByType(),
                          presenter.getProcessesByUser(),
                          presenter.getProcessesByStartDate(),
                          presenter.getProcessesByEndDate(),
                          presenter.getProcessesByRunningTime(),
                          presenter.getProcessesByVersion(),
                          presenter.getProcessesTable());

        verify(view).showLoading();

        verify(displayerListener).onDraw(presenter.getTotalMetric());
        verify(displayerListener).onDraw(presenter.getActiveMetric());
        verify(displayerListener).onDraw(presenter.getPendingMetric());
        verify(displayerListener).onDraw(presenter.getSuspendedMetric());
        verify(displayerListener).onDraw(presenter.getAbortedMetric());
        verify(displayerListener).onDraw(presenter.getCompletedMetric());
        verify(displayerListener).onDraw(presenter.getProcessesByType());
        verify(displayerListener).onDraw(presenter.getProcessesByUser());
        verify(displayerListener).onDraw(presenter.getProcessesByStartDate());
        verify(displayerListener).onDraw(presenter.getProcessesByEndDate());
        verify(displayerListener).onDraw(presenter.getProcessesByRunningTime());
        verify(displayerListener).onDraw(presenter.getProcessesByVersion());
        verify(displayerListener).onDraw(presenter.getProcessesTable());

        verify(view).hideLoading();
    }

    @Test
    public void test_JBPM_4851_Fix() {
        assertEquals(presenter.getTotalMetric().isFilterOn(),
                     true);
    }

    @Test
    public void test_JBPM_5834_Fix() {
        assertEquals(presenter.getTotalMetric().isFilterOn(),
                     true);
        verify(totalMetricListener,
               never()).onFilterEnabled(any(),
                                        any(DataSetFilter.class));
    }

    @Test
    public void testShowInstances() {
        reset(displayerListener);
        presenter.showProcessesTable();
        verify(view).showInstances();
        verify(displayerListener).onRedraw(presenter.getProcessesTable());
    }

    @Test
    public void testShowDashboard() {
        reset(displayerListener);
        presenter.showDashboard();
        verify(view).showDashboard();
        verify(displayerListener,
               never()).onRedraw(presenter.getProcessesTable());
    }

    @Test
    public void testTotalMetric() {
        Displayer displayer = presenter.getTotalMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     4d);
    }

    @Test
    public void testActiveMetric() {
        Displayer displayer = presenter.getActiveMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     3d);
    }

    @Test
    public void testCompletedMetric() {
        Displayer displayer = presenter.getCompletedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     1d);
    }

    @Test
    public void testAbortedTotalMetric() {
        Displayer displayer = presenter.getAbortedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     0d);
    }

    @Test
    public void testPendingMetric() {
        Displayer displayer = presenter.getPendingMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     0d);
    }

    @Test
    public void testSuspendedMetric() {
        Displayer displayer = presenter.getSuspendedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0,
                                        0),
                     0d);
    }

    @Test
    public void testProcessesByEndDate() {
        Displayer displayer = presenter.getProcessesByEndDate();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"2019-01-02", "1.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesByStartDate() {
        Displayer displayer = presenter.getProcessesByStartDate();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"2019-01-01", "4.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesByRunningTime() {
        Displayer displayer = presenter.getProcessesByRunningTime();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"Process B", "1.00", "100,000.00", "Process B", "1.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesByType() {
        Displayer displayer = presenter.getProcessesByType();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"Process A", "2.00"},
                                    {"Process B", "2.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesByUser() {
        Displayer displayer = presenter.getProcessesByUser();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"user1", "2.00"},
                                    {"user2", "2.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesByVersion() {
        Displayer displayer = presenter.getProcessesByVersion();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1", "4.00"}
                            },
                            0);
    }

    @Test
    public void testProcessesTable() {
        Displayer displayer = presenter.getProcessesTable();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
                            },
                            0);
    }

    @Test
    public void testSelectProcess() {
        reset(view);
        reset(displayerListener);

        presenter.getProcessesByType().filterUpdate(COLUMN_PROCESS_NAME,
                                                    1);
        final String process = "Process B";
        assertEquals(presenter.getSelectedProcess(),
                     process);

        verify(view).showBreadCrumb(process);
        verify(view).setHeaderText(i18n.selectedProcessStatusHeader("",
                                                                    process));
        verify(displayerListener,
               times(12)).onRedraw(any(Displayer.class));
        verify(displayerListener,
               never()).onError(any(Displayer.class),
                                any(ClientRuntimeError.class));
    }

    @Test
    public void testRedrawSelectedMetric() {

        MetricDisplayer activeMetric = presenter.getActiveMetric();
        activeMetric.filterApply();

        reset(view);
        reset(displayerListener);
        reset(presenter.getTotalMetric().getView());
        reset(presenter.getActiveMetric().getView());
        reset(presenter.getSuspendedMetric().getView());
        reset(presenter.getCompletedMetric().getView());
        reset(presenter.getPendingMetric().getView());
        presenter.getProcessesByType().filterUpdate(COLUMN_PROCESS_NAME,
                                                    1);

        verify(displayerListener).onRedraw(presenter.getTotalMetric());
        verify(displayerListener).onRedraw(presenter.getActiveMetric());
        verify(displayerListener).onRedraw(presenter.getSuspendedMetric());
        verify(displayerListener).onRedraw(presenter.getCompletedMetric());
        verify(displayerListener).onRedraw(presenter.getPendingMetric());

        verify(presenter.getTotalMetric().getView()).setHtml(anyString());
        verify(presenter.getActiveMetric().getView()).setHtml(anyString());
        verify(presenter.getSuspendedMetric().getView()).setHtml(anyString());
        verify(presenter.getCompletedMetric().getView()).setHtml(anyString());
        verify(presenter.getPendingMetric().getView()).setHtml(anyString());
    }

    @Test
    public void testResetProcess() {
        reset(view);
        presenter.resetCurrentProcess();
        assertNull(presenter.getSelectedProcess());
        verify(view).hideBreadCrumb();
        verify(view).setHeaderText(i18n.allProcesses());
    }

    @Test
    public void testSelectMetric() {
        presenter.resetCurrentMetric();
        reset(view);
        reset(displayerListener);

        MetricDisplayer activeMetric = presenter.getActiveMetric();
        activeMetric.filterApply();

        assertEquals(presenter.getSelectedMetric(),
                     activeMetric);
        verify(view).setHeaderText(i18n.activeProcesses());
        verify(displayerListener).onFilterEnabled(eq(activeMetric),
                                                  any(DataSetFilter.class));
        verify(displayerListener,
               times(1)).onFilterEnabled(any(Displayer.class),
                                         any(DataSetFilter.class));
        verify(displayerListener,
               never()).onFilterReset(any(Displayer.class),
                                      any(DataSetFilter.class));

        // Check that only processes with status=active are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""}
                            },
                            0);
    }

    @Test
    public void testResetMetric() {
        MetricDisplayer activeMetric = presenter.getActiveMetric();
        activeMetric.filterApply();

        reset(displayerListener,
              view);
        activeMetric.filterReset();

        assertNull(presenter.getSelectedMetric());
        verify(view).setHeaderText(i18n.allProcesses());
        verify(displayerListener).onFilterReset(eq(activeMetric),
                                                any(DataSetFilter.class));
        verify(displayerListener,
               times(1)).onFilterReset(any(Displayer.class),
                                       any(DataSetFilter.class));

        // Check that only processes with status=active are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                                    {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
                            },
                            0);
    }

    @Test
    public void testSwitchMetric() {
        MetricDisplayer activeMetric = presenter.getActiveMetric();
        MetricDisplayer completedMetric = presenter.getCompletedMetric();
        activeMetric.filterApply();

        reset(displayerListener,
              view,
              activeMetric.getView());
        completedMetric.filterApply();

        assertEquals(presenter.getSelectedMetric(),
                     completedMetric);
        verify(activeMetric.getView()).setHtml(anyString());
        verify(displayerListener).onFilterReset(eq(activeMetric),
                                                any(DataSetFilter.class));
        verify(displayerListener).onFilterEnabled(eq(completedMetric),
                                                  any(DataSetFilter.class));

        // Check that only processes with status=completed are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
                            },
                            0);
    }

    @Test
    public void testOpenInstanceDetails() {
        when(placeManager.getStatus(PROCESS_INSTANCE_DETAILS_SCREEN)).thenReturn(PlaceStatus.CLOSE);
        presenter.tableCellSelected(COLUMN_PROCESS_INSTANCE_ID,
                                    3);
        verify(instanceSelectionEvent).fire(any(ProcessInstanceSelectionEvent.class));
        verify(placeManager).goTo(PROCESS_INSTANCE_DETAILS_SCREEN);
    }

    @Test
    public void testHeaderText() {
        final String process = "Process Test";

        verifyMetricHeaderText(process,
                               presenter.getTotalMetric(),
                               i18n.selectedProcessStatusHeader("",
                                                                process));
        verifyMetricHeaderText(process,
                               presenter.getActiveMetric(),
                               i18n.selectedProcessStatusHeader(i18n.processStatusActive(),
                                                                process));
        verifyMetricHeaderText(process,
                               presenter.getPendingMetric(),
                               i18n.selectedProcessStatusHeader(i18n.processStatusPending(),
                                                                process));
        verifyMetricHeaderText(process,
                               presenter.getSuspendedMetric(),
                               i18n.selectedProcessStatusHeader(i18n.processStatusSuspended(),
                                                                process));
        verifyMetricHeaderText(process,
                               presenter.getAbortedMetric(),
                               i18n.selectedProcessStatusHeader(i18n.processStatusAborted(),
                                                                process));
        verifyMetricHeaderText(process,
                               presenter.getCompletedMetric(),
                               i18n.selectedProcessStatusHeader(i18n.processStatusCompleted(),
                                                                process));

        reset(view);
        presenter.resetCurrentProcess();
        presenter.resetCurrentMetric();
        verify(view).setHeaderText(i18n.allProcesses());
    }
}