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

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.renderer.client.metric.MetricDisplayer;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.dashboard.renderer.client.panel.ProcessDashboard;
import org.jbpm.dashboard.renderer.client.panel.events.ProcessDashboardFocusEvent;
import org.jbpm.dashboard.renderer.client.panel.widgets.ProcessBreadCrumb;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.jbpm.dashboard.renderer.model.DashboardData.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessDashboardTest extends AbstractDashboardTest {

    @Mock
    ProcessDashboard.View view;

    @Mock
    ProcessBreadCrumb processBreadCrumb;

    @Mock
    Event<ProcessInstanceSelectionEvent> instanceSelectionEvent;

    @Mock
    Event<ProcessDashboardFocusEvent> processDashboardFocusEvent;

    ProcessDashboard presenter;
    DataSet dataSet;

    public void registerDataset() throws Exception {
        dataSet = ProcessDashboardData.INSTANCE.toDataSet();
        dataSet.setUUID(DATASET_PROCESS_INSTANCES);
        clientDataSetManager.registerDataSet(dataSet);
    }

    @Before
    public void init() throws Exception {
        super.init();
        registerDataset();
        when(view.getI18nService()).thenReturn(i18n);

        presenter = new ProcessDashboard(view,
                processBreadCrumb,
                dashboardFactory,
                clientServices,
                displayerLocator,
                displayerCoordinator,
                placeManager,
                instanceSelectionEvent,
                processDashboardFocusEvent);
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
        verify(presenter.getTotalMetric().getView()).setFilterActive(true);
        assertEquals(presenter.getTotalMetric().isFilterOn(), true);
    }

    @Test
    public void testShowInstances() {
        reset(displayerListener);
        presenter.showProcessesTable();
        verify(view).showInstances();
        verify(processDashboardFocusEvent).fire(any(ProcessDashboardFocusEvent.class));
        verify(displayerListener).onRedraw(presenter.getProcessesTable());
    }

    @Test
    public void testShowDashboard() {
        reset(displayerListener);
        presenter.showDashboard();
        verify(view).showDashboard();
        verify(processDashboardFocusEvent).fire(any(ProcessDashboardFocusEvent.class));
        verify(displayerListener, never()).onRedraw(presenter.getProcessesTable());
    }

    @Test
    public void testTotalMetric() {
        Displayer displayer = presenter.getTotalMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 4d);
    }

    @Test
    public void testActiveMetric() {
        Displayer displayer = presenter.getActiveMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 3d);
    }

    @Test
    public void testCompletedMetric() {
        Displayer displayer = presenter.getCompletedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 1d);
    }

    @Test
    public void testAbortedTotalMetric() {
        Displayer displayer = presenter.getAbortedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 0d);

    }

    @Test
    public void testPendingMetric() {
        Displayer displayer = presenter.getPendingMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 0d);
    }

    @Test
    public void testSuspendedMetric() {
        Displayer displayer = presenter.getSuspendedMetric();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getValueAt(0, 0), 0d);
    }

    @Test
    public void testProcessesByEndDate() {
        Displayer displayer = presenter.getProcessesByEndDate();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"2019-01-02", "1.00"}
        }, 0);
    }

    @Test
    public void testProcessesByStartDate() {
        Displayer displayer = presenter.getProcessesByStartDate();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"2019-01-01", "4.00"}
        }, 0);
    }

    @Test
    public void testProcessesByRunningTime() {
        Displayer displayer = presenter.getProcessesByRunningTime();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"Process B", "1.00", "100,000.00", "Process B", "1.00"}
        }, 0);
    }

    @Test
    public void testProcessesByType() {
        Displayer displayer = presenter.getProcessesByType();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"Process A", "2.00"},
                {"Process B", "2.00"}
        }, 0);
    }

    @Test
    public void testProcessesByUser() {
        Displayer displayer = presenter.getProcessesByUser();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"user1", "2.00"},
                {"user2", "2.00"}
        }, 0);
    }

    @Test
    public void testProcessesByVersion() {
        Displayer displayer = presenter.getProcessesByVersion();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"1", "4.00"}
        }, 0);
    }

    @Test
    public void testProcessesTable() {
        Displayer displayer = presenter.getProcessesTable();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet, new String[][]{
                {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
        }, 0);
    }

    @Test
    public void testSelectProcess() {
        reset(view);
        presenter.changeCurrentProcess("Process B");
        assertEquals(presenter.getSelectedProcess(), "Process B");
        verify(view).showBreadCrumb("Process B");
        verify(view).setHeaderText(anyString());
    }

    @Test
    public void testResetProcess() {
        reset(view);
        presenter.resetCurrentProcess();
        assertNull(presenter.getSelectedProcess());
        verify(view).hideBreadCrumb();
        verify(view).setHeaderText(anyString());
    }

    @Test
    public void testSelectMetric() {
        presenter.resetCurrentMetric();
        reset(view);
        reset(displayerListener);

        MetricDisplayer activeMetric = presenter.getActiveMetric();
        activeMetric.filterApply();

        assertEquals(presenter.getSelectedMetric(), activeMetric);
        verify(view).setHeaderText(anyString());
        verify(displayerListener).onFilterEnabled(eq(activeMetric), any(DataSetFilter.class));
        verify(displayerListener, times(1)).onFilterEnabled(any(Displayer.class), any(DataSetFilter.class));
        verify(displayerListener, never()).onFilterReset(any(Displayer.class), any(DataSetFilter.class));

        // Check that only processes with status=active are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet, new String[][]{
                {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""}
        }, 0);
    }

    @Test
    public void testResetMetric() {
        MetricDisplayer activeMetric = presenter.getActiveMetric();
        activeMetric.filterApply();

        reset(displayerListener, view);
        activeMetric.filterReset();

        assertNull(presenter.getSelectedMetric());
        verify(view).setHeaderText(anyString());
        verify(displayerListener).onFilterReset(eq(activeMetric), any(DataSetFilter.class));
        verify(displayerListener, times(1)).onFilterReset(any(Displayer.class), any(DataSetFilter.class));

        // Check that only processes with status=active are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet, new String[][]{
                {"1.00", "org.jbpm.test", "1", "Process A", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                {"2.00", "org.jbpm.test", "1", "Process A", "user2", "1.00", "1", "01/01/19 12:00", "", ""},
                {"3.00", "org.jbpm.test", "1", "Process B", "user1", "1.00", "1", "01/01/19 12:00", "", ""},
                {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
        }, 0);
    }

    @Test
    public void testSwitchMetric() {
        MetricDisplayer activeMetric = presenter.getActiveMetric();
        MetricDisplayer completedMetric = presenter.getCompletedMetric();
        activeMetric.filterApply();

        reset(displayerListener, view);
        completedMetric.filterApply();

        assertEquals(presenter.getSelectedMetric(), completedMetric);
        verify(displayerListener).onFilterReset(eq(activeMetric), any(DataSetFilter.class));
        verify(displayerListener).onFilterEnabled(eq(completedMetric), any(DataSetFilter.class));

        // Check that only processes with status=completed are shown
        DataSet dataSet = presenter.getProcessesTable().getDataSetHandler().getLastDataSet();
        assertDataSetValues(dataSet, new String[][]{
                {"4.00", "org.jbpm.test", "1", "Process B", "user2", "2.00", "1", "01/01/19 12:00", "01/02/19 10:00", "100,000.00"}
        }, 0);
    }

    @Test
    public void testOpenInstanceDetails() {
        when(placeManager.getStatus(ProcessDashboard.PROCESS_DETAILS_SCREEN_ID)).thenReturn(PlaceStatus.CLOSE);
        presenter.tableCellSelected(COLUMN_PROCESS_INSTANCE_ID, 3);
        verify(instanceSelectionEvent).fire(any(ProcessInstanceSelectionEvent.class));
        verify(processDashboardFocusEvent).fire(any(ProcessDashboardFocusEvent.class));
        verify(placeManager).goTo(ProcessDashboard.PROCESS_DETAILS_SCREEN_ID);
    }
}