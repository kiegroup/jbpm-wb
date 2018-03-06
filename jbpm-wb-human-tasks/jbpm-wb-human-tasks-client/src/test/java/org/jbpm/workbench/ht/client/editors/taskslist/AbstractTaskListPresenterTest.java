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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.enterprise.event.Event;

import com.google.gwt.view.client.Range;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
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
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.TAB_SEARCH;
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public abstract class AbstractTaskListPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String TASK_DEPLOYMENT_ID = "deploymentId";

    @Mock
    protected User identity;

    @Mock
    protected TaskService taskService;

    protected CallerMock<TaskService> callerMockRemoteTaskService;

    @Mock
    DataSetQueryHelper dataSetQueryHelper;

    @Mock
    DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Mock
    private TaskListViewImpl viewMock;

    @Mock
    private ListTable<TaskSummary> extendedPagedTable;

    @Mock
    private DataSet dataSetMock;

    @Mock
    private DataSet dataSetTaskVarMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private UberfireBreadcrumbs breadcrumbs;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private PerspectiveActivity perspectiveActivity;

    @Spy
    private FilterSettings filterSettings;

    @Spy
    private DataSetLookup dataSetLookup;

    @Spy
    private Event<TaskSelectionEvent> taskSelected = new EventSourceMock<TaskSelectionEvent>();

    protected static void testTaskStatusCondition(Predicate<TaskSummary> predicate,
                                                  String... validStatutes) {
        List<String> allStatus = TaskUtils.getStatusByType(TaskType.ALL);
        final List<String> validStatuses = Arrays.asList(validStatutes);
        allStatus.removeAll(validStatuses);

        allStatus.forEach(s -> assertFalse(predicate.test(TaskSummary.builder().status(s).build())));
        validStatuses.forEach(s -> assertTrue(predicate.test(TaskSummary.builder().status(s).build())));
    }

    public abstract String getDataSetId();

    @Before
    public void setupMocks() {
        callerMockRemoteTaskService = new CallerMock<TaskService>(taskService);
        getPresenter().setTaskService(callerMockRemoteTaskService);

        doNothing().when(taskSelected).fire(any(TaskSelectionEvent.class));

        //Mock that actually calls the callbacks
        dataSetLookup.setDataSetUUID(HUMAN_TASKS_DATASET);

        when(filterSettings.getDataSetLookup()).thenReturn(dataSetLookup);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(viewMock.getAdvancedSearchFilterSettings()).thenReturn(filterSettings);
        when(filterSettings.getKey()).thenReturn("key");
        when(perspectiveManager.getCurrentPerspective()).thenReturn(perspectiveActivity);

        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetMock);
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                  any(DataSetReadyCallback.class));

        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetTaskVarMock);
                return null;
            }
        }).when(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(),
                                                                any(DataSetReadyCallback.class));

        when(identity.getIdentifier()).thenReturn("userId");
    }

    protected abstract AbstractTaskListPresenter getPresenter();

    @Test
    public void getDataTest() {
        getPresenter().setAddingDefaultFilters(false);
        getPresenter().getData(new Range(0,
                                         5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(dataSetQueryHelper).setLastOrderedColumn(COLUMN_CREATED_ON);
        verify(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                 any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific,
               never()).lookupDataSet(anyInt(),
                                      any(DataSetReadyCallback.class));
    }

    @Test
    public void releaseTaskTest() {
        final TaskSummary task = TaskSummary.builder().id(TASK_ID).deploymentId(TASK_DEPLOYMENT_ID).build();

        getPresenter().releaseTask(task);

        verify(taskService).releaseTask("",
                                        TASK_DEPLOYMENT_ID,
                                        TASK_ID);
    }

    @Test
    public void claimTaskTest() {
        final TaskSummary task = TaskSummary.builder().id(TASK_ID).deploymentId(TASK_DEPLOYMENT_ID).build();

        getPresenter().claimTask(task);

        verify(taskService).claimTask("",
                                      TASK_DEPLOYMENT_ID,
                                      TASK_ID);
    }

    @Test
    public void resumeTaskTest() {
        final TaskSummary task = TaskSummary.builder().id(TASK_ID).deploymentId(TASK_DEPLOYMENT_ID).build();

        getPresenter().resumeTask(task);

        verify(taskService).resumeTask("",
                                       TASK_DEPLOYMENT_ID,
                                       TASK_ID);
    }

    @Test
    public void suspendTaskTest() {
        final TaskSummary task = TaskSummary.builder().id(TASK_ID).deploymentId(TASK_DEPLOYMENT_ID).build();

        getPresenter().suspendTask(task);

        verify(taskService).suspendTask("",
                                        TASK_DEPLOYMENT_ID,
                                        TASK_ID);
    }

    @Test
    public void isFilteredByTaskNameTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME,
                                        taskName));

        final String filterTaskName = getPresenter().isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertEquals(taskName,
                     filterTaskName);
    }

    @Test
    public void isFilteredByTaskNameInvalidTest() {
        final String taskName = "taskName";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_DESCRIPTION,
                                      taskName));

        final String filterTaskName = getPresenter().isFilteredByTaskName(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterTaskName);
    }

    @Test
    public void testSkipDomainSpecificColumnsForSearchTab() {
        getPresenter().setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME,
                                        "taskName"));
        filterSettings.getDataSetLookup().addOperation(filter);
        filterSettings.setKey(TAB_SEARCH);
        when(filterSettings.getKey()).thenReturn(TAB_SEARCH);

        when(dataSetMock.getRowCount()).thenReturn(1);//1 process instance
        when(dataSetMock.getValueAt(0,
                                    COLUMN_TASK_ID)).thenReturn(Long.valueOf(1));

        getPresenter().getData(new Range(0,
                                         5));

        verifyZeroInteractions(dataSetQueryHelperDomainSpecific);
        verify(viewMock,
               times(2)).hideBusyIndicator();
    }

    @Test
    public void getDomainSpecificDataForTasksTest() {
        getPresenter().setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_NAME,
                                        "taskName"));
        filterSettings.getDataSetLookup().addOperation(filter);

        when(dataSetMock.getRowCount()).thenReturn(1);//1 task
        //Task summary creation
        when(dataSetMock.getValueAt(0,
                                    COLUMN_TASK_ID)).thenReturn(Long.valueOf(1));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(2); //two domain variables associated
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_ID
        )).thenReturn(Long.valueOf(1));

        String taskVariable1 = "var1";
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_VARIABLE_NAME)).thenReturn(taskVariable1);
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_VARIABLE_VALUE)).thenReturn("value1");

        when(dataSetTaskVarMock.getValueAt(1,
                                           COLUMN_TASK_ID)).thenReturn(Long.valueOf(1));
        String taskVariable2 = "var2";
        when(dataSetTaskVarMock.getValueAt(1,
                                           COLUMN_TASK_VARIABLE_NAME)).thenReturn(taskVariable2);
        when(dataSetTaskVarMock.getValueAt(1,
                                           COLUMN_TASK_VARIABLE_VALUE)).thenReturn("value2");

        Set<String> expectedColumns = new HashSet<String>();
        expectedColumns.add(taskVariable1);
        expectedColumns.add(taskVariable2);

        getPresenter().getData(new Range(0,
                                         5));

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock).addDomainSpecifColumns(any(ListTable.class),
                                                argument.capture());

        assertEquals(expectedColumns,
                     argument.getValue());

        verify(dataSetQueryHelper).lookupDataSet(anyInt(),
                                                 any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(),
                                                               any(DataSetReadyCallback.class));

        when(dataSetTaskVarMock.getRowCount()).thenReturn(1); //one domain variables associated
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_ID)).thenReturn(Long.valueOf(1));
        taskVariable1 = "varTest1";
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_VARIABLE_NAME)).thenReturn(taskVariable1);
        when(dataSetTaskVarMock.getValueAt(0,
                                           COLUMN_TASK_VARIABLE_VALUE)).thenReturn("value1");

        expectedColumns = Collections.singleton(taskVariable1);

        getPresenter().getData(new Range(0,
                                         5));

        argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock,
               times(2)).addDomainSpecifColumns(any(ListTable.class),
                                                argument.capture());

        assertEquals(expectedColumns,
                     argument.getValue());
        verify(dataSetQueryHelper,
               times(2)).lookupDataSet(anyInt(),
                                       any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific,
               times(2)).lookupDataSet(anyInt(),
                                       any(DataSetReadyCallback.class));
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

            final TaskSummary summary = new TaskSummaryDataSetMapper().apply(dataSetMock,
                                                                             0);
            assertNotNull(summary);
            assertEquals(HUMAN_TASKS_WITH_ADMIN_DATASET.equals(dataSet),
                         summary.isForAdmin());
        }
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

        final ColumnFilter userTaskFilter = getPresenter().getUserGroupFilters(false);

        List<ColumnFilter> columnFilters = ((LogicalExprFilter) userTaskFilter).getLogicalTerms();

        assertEquals(columnFilters.size(),
                     2);
        assertEquals(((LogicalExprFilter) userTaskFilter).getLogicalOperator(),
                     LogicalExprType.OR);

        assertEquals(((LogicalExprFilter) columnFilters.get(0)).getLogicalOperator(),
                     LogicalExprType.AND);
        List<ColumnFilter> userGroupFilter = ((LogicalExprFilter) columnFilters.get(0)).getLogicalTerms();
        assertEquals(userGroupFilter.size(),
                     2);
        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(),
                     LogicalExprType.OR);

        List<ColumnFilter> groupFilter = ((LogicalExprFilter) userGroupFilter.get(0)).getLogicalTerms();
        List<ColumnFilter> withoutActualOwnerFilter = ((LogicalExprFilter) userGroupFilter.get(1)).getLogicalTerms();

        assertEquals(((LogicalExprFilter) userGroupFilter.get(1)).getLogicalOperator(),
                     LogicalExprType.OR);
        assertEquals(withoutActualOwnerFilter.size(),
                     2);
        assertEquals(COLUMN_ACTUAL_OWNER,
                     withoutActualOwnerFilter.get(0).getColumnId());
        assertEquals(COLUMN_ACTUAL_OWNER,
                     withoutActualOwnerFilter.get(1).getColumnId());

        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(),
                     LogicalExprType.OR);
        assertEquals(groupFilter.size(),
                     3);
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY,
                     groupFilter.get(0).getColumnId());
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY,
                     groupFilter.get(1).getColumnId());
        assertEquals(COLUMN_ORGANIZATIONAL_ENTITY,
                     groupFilter.get(2).getColumnId());

        ColumnFilter userOwnerFilter = columnFilters.get(1);
        assertEquals(userOwnerFilter.getColumnId(),
                     COLUMN_ACTUAL_OWNER);
    }

    @Test
    public void testDefaultActiveSearchFilters() {
        getPresenter().setupDefaultActiveSearchFilters();

        verify(viewMock).addActiveFilter(eq(Constants.INSTANCE.Status()),
                                         eq(TASK_STATUS_READY),
                                         eq(TASK_STATUS_READY),
                                         any(Consumer.class));
    }

    @Test
    public void testIsNullTableSettingsPrototype() {
        when(identity.getIdentifier()).thenReturn("user");
        getPresenter().setIdentity(identity);
        FilterSettings filterSettings = getPresenter().createTableSettingsPrototype();
        List<DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for (DataSetOp op : ops) {
            if (op.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> columnFilters = ((DataSetFilter) op).getColumnFilterList();
                for (ColumnFilter columnFilter : columnFilters) {
                    assertTrue((columnFilter).toString().contains(COLUMN_ACTUAL_OWNER + " is_null"));
                }
            }
        }
    }

    @Test
    public void getVariablesTableSettingsTest() {
        FilterSettings filterSettings = getPresenter().getVariablesTableSettings("Test");
        List<DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for (DataSetOp op : ops) {
            if (op.getType().equals(DataSetOpType.FILTER)) {
                List<ColumnFilter> columnFilters = ((DataSetFilter) op).getColumnFilterList();
                for (ColumnFilter columnFilter : columnFilters) {
                    assertTrue((columnFilter).toString().contains(COLUMN_TASK_VARIABLE_TASK_NAME + " = Test"));
                }
            }
        }
    }

    @Test
    public void testDatasetName() {
        assertEquals(getDataSetId(),
                     getPresenter().createTableSettingsPrototype().getDataSetLookup().getDataSetUUID());
    }

    @Test
    public void testCompleteActionCondition() {
        assertTrue(getPresenter().getCompleteActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_IN_PROGRESS).build()));
        assertFalse(getPresenter().getCompleteActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_READY).build()));
    }

    @Test
    public void testClaimActionCondition() {
        testTaskStatusCondition(getPresenter().getClaimActionCondition(),
                                TASK_STATUS_READY);
    }

    @Test
    public void testReleaseActionCondition() {
        assertTrue(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_RESERVED).build()));
        assertTrue(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_IN_PROGRESS).build()));
        assertFalse(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_COMPLETED).build()));
        assertFalse(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner(identity.getIdentifier()).status(TASK_STATUS_CREATED).build()));
    }

    @Test
    public void testProcessInstanceCondition() {
        assertTrue(getPresenter().getProcessInstanceCondition().test(TaskSummary.builder().processInstanceId(1l).build()));
        assertFalse(getPresenter().getProcessInstanceCondition().test(TaskSummary.builder().build()));
    }

    @Test
    public void testCreateDataSetTaskCallback() {
        final AbstractTaskListPresenter presenter = spy(getPresenter());
        final ClientRuntimeError error = new ClientRuntimeError("");
        final FilterSettings filterSettings = mock(FilterSettings.class);
        final DataSetReadyCallback callback = presenter.createDataSetTaskCallback(0,
                                                                                  filterSettings);

        doNothing().when(presenter).showErrorPopup(any());

        assertFalse(callback.onError(error));

        verify(viewMock).hideBusyIndicator();
        verify(presenter).showErrorPopup(Constants.INSTANCE.TaskListCouldNotBeLoaded());
    }

    @Test
    public void testStatusSettingsColumns() {
        final String dataSetId = "dataSetId";
        FilterSettings settings = getPresenter().createStatusSettings(dataSetId,
                                                                      null);

        assertEquals(dataSetId,
                     settings.getDataSetLookup().getDataSetUUID());
        getDataSetExpectedColumns().forEach(c -> assertNotNull(settings.getColumnSettings(c)));
    }

    protected List<String> getDataSetExpectedColumns() {
        return Arrays.asList(
                COLUMN_ACTIVATION_TIME,
                COLUMN_ACTUAL_OWNER,
                COLUMN_CREATED_BY,
                COLUMN_CREATED_ON,
                COLUMN_DEPLOYMENT_ID,
                COLUMN_DESCRIPTION,
                COLUMN_DUE_DATE,
                COLUMN_NAME,
                COLUMN_PARENT_ID,
                COLUMN_PRIORITY,
                COLUMN_PROCESS_ID,
                COLUMN_PROCESS_INSTANCE_ID,
                COLUMN_PROCESS_SESSION_ID,
                COLUMN_STATUS,
                COLUMN_TASK_ID,
                COLUMN_WORK_ITEM_ID,
                COLUMN_LAST_MODIFICATION_DATE,
                COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                COLUMN_PROCESS_INSTANCE_DESCRIPTION
        );
    }

    @Test
    public void testExitedTaskSelection() {
        TaskSummary taskSummary = TaskSummary.builder()
                .id(TASK_ID)
                .deploymentId(TASK_DEPLOYMENT_ID)
                .status(TASK_STATUS_EXITED)
                .build();

        getPresenter().selectTask(taskSummary);

        verify(placeManager).goTo(PerspectiveIds.TASK_DETAILS_SCREEN);
        final ArgumentCaptor<TaskSelectionEvent> captor = ArgumentCaptor.forClass(TaskSelectionEvent.class);
        verify(taskSelected).fire(captor.capture());
        assertTrue(captor.getValue().isForLog());
    }

    @Test
    public void testReadyTaskSelection() {
        TaskSummary taskSummary = TaskSummary.builder()
                .id(TASK_ID)
                .deploymentId(TASK_DEPLOYMENT_ID)
                .status(TASK_STATUS_READY)
                .build();

        getPresenter().selectTask(taskSummary);

        verify(placeManager).goTo(PerspectiveIds.TASK_DETAILS_SCREEN);
        final ArgumentCaptor<TaskSelectionEvent> captor = ArgumentCaptor.forClass(TaskSelectionEvent.class);
        verify(taskSelected).fire(captor.capture());
        assertFalse(captor.getValue().isForLog());
    }
}
