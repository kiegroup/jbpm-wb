/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetTasksListGridPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String USR_ID = "admin";

    private CallerMock<TaskLifeCycleService> callerMockTaskOperationsService;

    @Mock
    private TaskLifeCycleService taskLifeCycleServiceMock;

    @Mock
    private DataSetTasksListGridViewImpl viewMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelperMock;

    @Mock
    DataSetQueryHelper dataSetDomainDataQueryHelperMock;

    @Mock
    private ExtendedPagedTable<TaskSummary> extendedPagedTable;

    @Mock
    private DataSet dataSetMock;

    @Mock
    private DataSet dataSetTaskVarMock;

    @Mock
    private FilterSettings filterSettings;

    //Thing under test
    private DataSetTasksListGridPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockTaskOperationsService = new CallerMock<TaskLifeCycleService>(taskLifeCycleServiceMock);
        when(filterSettings.getDataSetLookup()).thenReturn(new DataSetLookup());

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(viewMock.getVariablesTableSettings(anyString())).thenReturn(new DataSetTasksListGridViewImpl().getVariablesTableSettings("taskName"));
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(dataSetQueryHelperMock.getCurrentTableSettings()).thenReturn(filterSettings);


        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetMock);
                return null;
            }
        }).when(dataSetQueryHelperMock).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetTaskVarMock);
                return null;
            }
        }).when(dataSetDomainDataQueryHelperMock).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        presenter = new DataSetTasksListGridPresenter(viewMock, callerMockTaskOperationsService, dataSetQueryHelperMock, dataSetDomainDataQueryHelperMock);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelperMock).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelperMock).setLastOrderedColumn(COLUMN_CREATEDON);
        verify(dataSetQueryHelperMock).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetDomainDataQueryHelperMock, never()).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    @Test
    public void releaseTaskTest() {
        presenter.releaseTask(TASK_ID, USR_ID);

        verify(taskLifeCycleServiceMock).release(TASK_ID, USR_ID);
    }

    @Test
    public void claimTaskTest() {
        presenter.claimTask(TASK_ID, USR_ID, "deploymentId");

        verify(taskLifeCycleServiceMock).claim(TASK_ID, USR_ID, "deploymentId");
    }

    public void isFilteredByTaskNameTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME, taskName));

        final String filterTaskName = presenter.isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertEquals(taskName, filterTaskName);
    }

    public void isFilteredByTaskNameInvalidTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_DESCRIPTION, taskName));

        final String filterTaskName = presenter.isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterTaskName);
    }

    @Test
    public void getDomainSpecificDataForTasksTest() {
        presenter.setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME, "taskName"));
        filterSettings.getDataSetLookup().addOperation(filter);

        when(dataSetMock.getRowCount()).thenReturn(1);//1 task
        //Task summary creation
        when(dataSetQueryHelperMock.getColumnLongValue(dataSetMock, COLUMN_TASKID, 0)).thenReturn(Long.valueOf(1));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(2); //two domain variables associated
        when(dataSetDomainDataQueryHelperMock.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASKID, 0)).thenReturn(Long.valueOf(1));
        String taskVariable1 = "var1";
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 0)).thenReturn(taskVariable1);
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 0)).thenReturn("value1");

        when(dataSetDomainDataQueryHelperMock.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASKID, 1)).thenReturn(Long.valueOf(1));
        String taskVariable2 = "var2";
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 1)).thenReturn(taskVariable2);
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 1)).thenReturn("value2");

        Set<String> expectedColumns = new HashSet<String>();
        expectedColumns.add(taskVariable1);
        expectedColumns.add(taskVariable2);

        presenter.getData(new Range(0, 5));

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());

        verify(dataSetQueryHelperMock).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetDomainDataQueryHelperMock).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(1); //one domain variables associated
        when(dataSetDomainDataQueryHelperMock.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASKID, 0)).thenReturn(Long.valueOf(1));
        taskVariable1 = "varTest1";
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 0)).thenReturn(taskVariable1);
        when(dataSetDomainDataQueryHelperMock.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 0)).thenReturn("value1");

        expectedColumns = Collections.singleton(taskVariable1);

        presenter.getData(new Range(0, 5));

        argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock, times(2)).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());
        verify(dataSetQueryHelperMock, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetDomainDataQueryHelperMock, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    @Test
    public void testTaskSummaryAdmin() {
        final List<String> dataSets = Arrays.asList(
                HUMAN_TASKS_WITH_ADMIN_DATASET,
                HUMAN_TASKS_WITH_USER_DATASET,
                HUMAN_TASKS_DATASET,
                HUMAN_TASKS_WITH_VARIABLES_DATASET);

        for (final String dataSet : dataSets) {
            when(dataSetMock.getUUID()).thenReturn(dataSet);

            final TaskSummary summary = presenter.createTaskSummaryFromDataSet(dataSetMock, 0);

            assertNotNull(summary);
            assertEquals(HUMAN_TASKS_WITH_ADMIN_DATASET.equals(dataSet), summary.isForAdmin());
        }
    }

    @Test
    public void testEmptySearchString() {
        final SearchEvent searchEvent = new SearchEvent("");

        presenter.onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), presenter.getTextSearchStr());
    }

    @Test
    public void testSearchString() {
        final SearchEvent searchEvent = new SearchEvent(RandomStringUtils.random(10));

        presenter.onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), presenter.getTextSearchStr());
    }

    @Test
    public void testSearchFilterEmpty() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterNull() {
        final List<ColumnFilter> filters = presenter.getColumnFilters(null);

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterEmptyTrim() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("     ");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterId() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("1");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_TASKID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterIdTrim() {
        final List<ColumnFilter> filters = presenter.getColumnFilters(" 1 ");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_TASKID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterString() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("taskName");

        assertEquals(3, filters.size());
        assertEquals(COLUMN_NAME, filters.get(0).getColumnId());
        assertEquals(COLUMN_DESCRIPTION, filters.get(1).getColumnId());
        assertEquals(COLUMN_PROCESSID, filters.get(2).getColumnId());
    }

}