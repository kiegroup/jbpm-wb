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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.COL_ID_ACTIONS;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskListViewImplTest extends AbstractTaskListViewTest {

    @InjectMocks
    @Spy
    private TaskListViewImpl view;

    @Mock
    private TaskListPresenter presenter;

    @Override
    public AbstractTaskListView getView() {
        return view;
    }

    @Override
    public AbstractTaskListPresenter getPresenter() {
        return presenter;
    }

    @Override
    public List<String> getExpectedInitialColumns() {
        return Arrays.asList(COLUMN_NAME,
                             COLUMN_PROCESS_ID,
                             COLUMN_STATUS,
                             COLUMN_CREATED_ON,
                             COL_ID_ACTIONS);
    }

    @Test
    public void initColumnsWithTaskVarColumnsTest() {
        final ListTable<TaskSummary> currentListGrid = spy(new ListTable<>(new GridGlobalPreferences()));
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                assertEquals(18,
                             columns.size());
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        columnPreferences.add(new GridColumnPreference("var1",
                                                       0,
                                                       "40"));
        columnPreferences.add(new GridColumnPreference("var2",
                                                       1,
                                                       "40"));
        columnPreferences.add(new GridColumnPreference("var3",
                                                       1,
                                                       "40"));
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStore);
        when(gridPreferencesStore.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void testRenameProcessVariableForInitColumns(){
        TaskListViewImpl taskListView = view;
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test",taskListView.getInitColumns(),taskListView.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);
        extendedPagedTable.getGridPreferencesStore().getColumnPreferences().add(new GridColumnPreference("Id",-1,""));
        extendedPagedTable.getGridPreferencesStore().getColumnPreferences().add(new GridColumnPreference("performance",-1,""));

        taskListView.initCellPreview(extendedPagedTable);

        final Column<TaskSummary, String> createdOnColumn = taskListView.createTextColumn(COLUMN_CREATED_ON,
                                                                                          task -> DateUtils.getDateTimeStr(task.getCreatedOn()));
        ColumnMeta<TaskSummary> actionsColumnMeta = taskListView.initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());

        List<ColumnMeta<TaskSummary>> columnMetas = taskListView.getGeneralColumnMetas(extendedPagedTable,
                                                                                       createdOnColumn,
                                                                                       actionsColumnMeta);

        assertEquals(15,columnMetas.size());
        assertEquals(0,columnMetas.stream().filter(column -> column.getCaption().equals("Var_Id"))
                .collect(Collectors.toList()).size());

        List<ColumnMeta<TaskSummary>> tmp = taskListView.renameProcessVariables(extendedPagedTable, columnMetas);

        columnMetas.addAll(tmp);

        assertEquals(17,columnMetas.size());
        assertEquals(1, columnMetas.stream()
                .filter(column -> column.getCaption().equals("Var_Id"))
                .collect(Collectors.toList()).size());
    }

    @Test
    public void testRenameProcessVariableForAddDomainSpecifColumns() {
        TaskListViewImpl taskListView = view;
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", taskListView.getInitColumns(), taskListView.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);

        Set<String> set = Collections.singleton("Id");
        taskListView.initColumns(extendedPagedTable);

        assertEquals(15, extendedPagedTable.getColumnMetaList().size());
        assertEquals(0, extendedPagedTable.getColumnMetaList().stream()
                .filter(column -> column.getCaption().equals("Var_Id"))
                .collect(Collectors.toList()).size());

        taskListView.addDomainSpecifColumns(extendedPagedTable, set);

        assertEquals(16, extendedPagedTable.getColumnMetaList().size());
        assertEquals(1, extendedPagedTable.getColumnMetaList().stream()
                .filter(column -> column.getCaption().equals("Var_Id"))
                .collect(Collectors.toList()).size());
    }

    @Test
    public void testRemoveColumnMetaFromColumnsForAddDomainSpecifColumns() {
        TaskListViewImpl taskListView = view;
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", taskListView.getInitColumns(), taskListView.getBannedColumns());


        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);

        extendedPagedTable.getGridPreferencesStore().getColumnPreferences().add(new GridColumnPreference("Extra",-1,""));

        Set<String> set = new HashSet<String>();
        set.add("Extra");

        taskListView.initColumns(extendedPagedTable);
        assertEquals(16, extendedPagedTable.getColumnMetaList().size());

        taskListView.addDomainSpecifColumns(extendedPagedTable, set);

        assertEquals(0, set.size());
    }

    @Test
    public void testRemoveColumnMetaFromExtendedPagedTableForAddDomainSpecifColumns() {
        TaskListViewImpl taskListView = view;
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", taskListView.getInitColumns(), taskListView.getBannedColumns());


        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);

        extendedPagedTable.getGridPreferencesStore().getColumnPreferences().add(new GridColumnPreference("Extra",-1,""));

        Column<TaskSummary, String> column = taskListView.createTextColumn("Extra", taskSummary -> taskSummary.getName());
        ColumnMeta<TaskSummary> columnMeta = new ColumnMeta<TaskSummary>(column,"Extra",true,true);
        Set<String> set = new HashSet<String>();
        set.add("Extra_test");

        taskListView.initColumns(extendedPagedTable);
        assertEquals(16, extendedPagedTable.getColumnMetaList().size());

        extendedPagedTable.addColumns(Collections.singletonList(columnMeta));

        assertEquals(17, extendedPagedTable.getColumnMetaList().size());
        taskListView.addDomainSpecifColumns(extendedPagedTable, set);

        assertEquals(16, extendedPagedTable.getColumnMetaList().size());
        assertEquals(1, set.size());
    }
}
