/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;

import com.google.gwt.view.client.Range;
import org.apache.commons.lang3.RandomStringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.events.SearchEvent;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.ht.client.editors.taskslist.AbstractTaskListPresenter;
import org.jbpm.workbench.ht.client.editors.taskslist.TaskListViewImpl;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public abstract class AbstractTaskListPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String TASK_DEPLOYMENT_ID = "deploymentId";

    @Mock
    private TaskListViewImpl viewMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelper;

    @Mock
    DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Mock
    private ExtendedPagedTable<TaskSummary> extendedPagedTable;

    @Mock
    private DataSet dataSetMock;

    @Mock
    private DataSet dataSetTaskVarMock;

    @Mock
    private FilterSettings filterSettings;

    @Mock
    protected User identity;

    @Mock
    protected TaskService taskService;

    protected CallerMock<TaskService> callerMockRemoteTaskService;

    @Mock
    protected ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilder;

    @Spy
    private Event<TaskSelectionEvent> taskSelected = new EventSourceMock<TaskSelectionEvent>();

    @Before
    public void setupMocks() {
        callerMockRemoteTaskService = new CallerMock<TaskService>(taskService);
        getPresenter().setTaskService(callerMockRemoteTaskService);

        doNothing().when(taskSelected).fire(any(TaskSelectionEvent.class));

        //Mock that actually calls the callbacks
        DataSetLookup dataSetLookup = new DataSetLookup();
        dataSetLookup.setDataSetUUID(HUMAN_TASKS_DATASET);

        when(filterSettings.getDataSetLookup()).thenReturn(dataSetLookup);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(viewMock.getVariablesTableSettings(anyString())).thenReturn(new TaskListViewImpl().getVariablesTableSettings("taskName"));
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(filterSettings.getKey()).thenReturn("key");


        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetMock);
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetTaskVarMock);
                return null;
            }
        }).when(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    protected abstract AbstractTaskListPresenter getPresenter();

    @Test
    public void getDataTest() {
        getPresenter().setAddingDefaultFilters(false);
        getPresenter().getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_CREATED_ON);
        verify(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific, never()).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    @Test
    public void releaseTaskTest() {
        final TaskSummary task = new TaskSummary(TASK_ID, null, null, null, 0, null, null, null, null, null, null, -1, -1, TASK_DEPLOYMENT_ID, -1, new Date(), null, null);

        getPresenter().releaseTask(task);

        verify(taskService).releaseTask("", TASK_DEPLOYMENT_ID, TASK_ID);
    }

    @Test
    public void claimTaskTest() {
        final TaskSummary task = new TaskSummary(TASK_ID, null, null, null, 0, null, null, null, null, null, null, -1, -1, TASK_DEPLOYMENT_ID, -1, new Date(), null, null);

        getPresenter().claimTask(task);

        verify(taskService).claimTask("", TASK_DEPLOYMENT_ID, TASK_ID);
    }

    @Test
    public void isFilteredByTaskNameTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME, taskName));

        final String filterTaskName = getPresenter().isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertEquals(taskName, filterTaskName);
    }

    @Test
    public void isFilteredByTaskNameInvalidTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_DESCRIPTION, taskName));

        final String filterTaskName = getPresenter().isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterTaskName);
    }

    @Test
    public void getDomainSpecificDataForTasksTest() {
        getPresenter().setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME, "taskName"));
        filterSettings.getDataSetLookup().addOperation(filter);

        when(dataSetMock.getRowCount()).thenReturn(1);//1 task
        //Task summary creation
        when(dataSetQueryHelper.getColumnLongValue(dataSetMock, COLUMN_TASK_ID, 0)).thenReturn(Long.valueOf(1));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(2); //two domain variables associated
        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASK_ID, 0)).thenReturn(Long.valueOf(1));
        String taskVariable1 = "var1";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 0)).thenReturn(taskVariable1);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 0)).thenReturn("value1");

        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASK_ID, 1)).thenReturn(Long.valueOf(1));
        String taskVariable2 = "var2";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 1)).thenReturn(taskVariable2);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 1)).thenReturn("value2");

        Set<String> expectedColumns = new HashSet<String>();
        expectedColumns.add(taskVariable1);
        expectedColumns.add(taskVariable2);

        getPresenter().getData(new Range(0, 5));

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());

        verify(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(1); //one domain variables associated
        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetTaskVarMock, COLUMN_TASK_ID, 0)).thenReturn(Long.valueOf(1));
        taskVariable1 = "varTest1";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_NAME, 0)).thenReturn(taskVariable1);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetTaskVarMock, COLUMN_TASK_VARIABLE_VALUE, 0)).thenReturn("value1");

        expectedColumns = Collections.singleton(taskVariable1);

        getPresenter().getData(new Range(0, 5));

        argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock, times(2)).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());
        verify(dataSetQueryHelper, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
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

            final TaskSummary summary = getPresenter().createTaskSummaryFromDataSet(dataSetMock, 0);

            assertNotNull(summary);
            assertEquals(HUMAN_TASKS_WITH_ADMIN_DATASET.equals(dataSet), summary.isForAdmin());
        }
    }

    @Test
    public void testEmptySearchString() {
        final SearchEvent searchEvent = new SearchEvent("");

        getPresenter().onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), getPresenter().getTextSearchStr());
    }

    @Test
    public void testSearchString() {
        final SearchEvent searchEvent = new SearchEvent(RandomStringUtils.random(10));

        getPresenter().onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), getPresenter().getTextSearchStr());
    }

    @Test
    public void testSearchFilterEmpty() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters("");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterNull() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters(null);

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterEmptyTrim() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters("     ");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterId() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters("1");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_TASK_ID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterIdTrim() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters(" 1 ");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_TASK_ID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterString() {
        final List<ColumnFilter> filters = getPresenter().getColumnFilters("taskName");

        assertEquals(3, filters.size());
        assertEquals(COLUMN_NAME, filters.get(0).getColumnId());
        assertEquals(COLUMN_DESCRIPTION, filters.get(1).getColumnId());
        assertEquals(COLUMN_PROCESS_ID, filters.get(2).getColumnId());
    }

    @Test
    public void testGetUserGroupFilters() {
        Group group1 = new Group() {
            @Override
            public String getName() {
                return "group1";
            }
        };
        Group group2 = new Group() {
            @Override
            public String getName() {
                return "group2";
            }
        };
        HashSet<Group> groups = new HashSet<Group>();
        groups.add(group1);
        groups.add(group2);
        when(identity.getGroups()).thenReturn(groups);
        when(identity.getIdentifier()).thenReturn("userId");

        final ColumnFilter userTaskFilter = getPresenter().getUserGroupFilters(false);

        List<ColumnFilter> columnFilters = ((LogicalExprFilter) userTaskFilter).getLogicalTerms();

        assertEquals(columnFilters.size(), 2);
        assertEquals(((LogicalExprFilter) userTaskFilter).getLogicalOperator(), LogicalExprType.OR);

        assertEquals(((LogicalExprFilter) columnFilters.get(0)).getLogicalOperator(), LogicalExprType.AND);
        List<ColumnFilter> userGroupFilter = ((LogicalExprFilter) columnFilters.get(0)).getLogicalTerms();
        assertEquals(userGroupFilter.size(), 2);
        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(), LogicalExprType.OR);

        List<ColumnFilter> groupFilter = ((LogicalExprFilter) userGroupFilter.get(0)).getLogicalTerms();
        List<ColumnFilter> withoutActualOwnerFilter = ((LogicalExprFilter) userGroupFilter.get(1)).getLogicalTerms();

        assertEquals(((LogicalExprFilter) userGroupFilter.get(1)).getLogicalOperator(), LogicalExprType.OR);
        assertEquals(withoutActualOwnerFilter.size(), 2);
        assertEquals(COLUMN_ACTUAL_OWNER, withoutActualOwnerFilter.get(0).getColumnId());
        assertEquals(COLUMN_ACTUAL_OWNER, withoutActualOwnerFilter.get(1).getColumnId());

        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(), LogicalExprType.OR);
        assertEquals(groupFilter.size(), 3);
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(0).getColumnId());
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(1).getColumnId());
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(2).getColumnId());

        ColumnFilter userOwnerFilter = columnFilters.get(1);
        assertEquals(userOwnerFilter.getColumnId(), COLUMN_ACTUAL_OWNER);
    }
    
    @Test
    public void testMenus() {
        final Menus menus = getPresenter().getMenus();
        assertEquals(4, menus.getItems().size());
    }


}