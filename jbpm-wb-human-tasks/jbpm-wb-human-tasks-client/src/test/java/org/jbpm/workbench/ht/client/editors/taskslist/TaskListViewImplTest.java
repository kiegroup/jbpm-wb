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

import com.google.common.collect.Sets;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.data.Index;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import static org.assertj.core.api.Assertions.assertThat;

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
    public void addDomainSpecifColumnsTest() {
        final ListTable<TaskSummary> currentListGrid = spy(new ListTable<>(new GridGlobalPreferences()));
        when(view.getListGrid()).thenReturn(currentListGrid);
        final List<String> resultList = Arrays.asList("var1", "var2", "var3");
        final Set<String> domainColumns = new HashSet<String>(resultList);
        getView().addDomainSpecifColumns(domainColumns);

        final ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(currentListGrid, times(3)).addColumns(argument.capture());

        final List<List> columnsList = argument.getAllValues();

        assertThat(columnsList.size()).isEqualTo(3);
        assertThat(columnsList.stream()).extracting(columns -> ((ColumnMeta) columns.get(0)).getCaption())
                .contains("var3", Index.atIndex(0))
                .contains("var2", Index.atIndex(1))
                .contains("var1", Index.atIndex(2));
    }

    @Test
    public void testRenameProcessVariableForInitColumns(){
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test",view.getInitColumns(),view.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);
        List<GridColumnPreference> gridColumnPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();
        gridColumnPreferenceList.add(new GridColumnPreference("Id",-1,""));
        gridColumnPreferenceList.add(new GridColumnPreference("performance",-1,""));

        view.initCellPreview(extendedPagedTable);

        final Column<TaskSummary, String> createdOnColumn = view.createTextColumn(COLUMN_CREATED_ON,
                                                                                          task -> DateUtils.getDateTimeStr(task.getCreatedOn()));
        ColumnMeta<TaskSummary> actionsColumnMeta = view.initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());

        List<ColumnMeta<TaskSummary>> columnMetas = view.getGeneralColumnMetas(extendedPagedTable,
                                                                                       createdOnColumn,
                                                                                       actionsColumnMeta);

        assertThat(columnMetas.stream()).extracting(c -> c.getCaption()).hasSize(15).doesNotContain("Var_Id");

        List<ColumnMeta<TaskSummary>> tmp = view.renameVariables(extendedPagedTable, columnMetas);

        columnMetas.addAll(tmp);

        assertThat(columnMetas.stream()).extracting(c -> c.getCaption()).hasSize(17).containsOnlyOnce("Var_Id");
    }

    @Test
    public void testRenameProcessVariableForAddDomainSpecifColumns() {
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", view.getInitColumns(), view.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);
        when(view.getListGrid()).thenReturn(extendedPagedTable);

        Set<String> set = Collections.singleton("Id");
        view.initColumns(extendedPagedTable);

        assertThat(extendedPagedTable.getColumnMetaList().stream()).extracting(c -> c.getCaption()).hasSize(15).doesNotContain("Var_Id");

        view.addDomainSpecifColumns(set);

        assertThat(extendedPagedTable.getColumnMetaList().stream()).extracting(c -> c.getCaption()).hasSize(16).containsOnlyOnce("Var_Id");
    }

    @Test
    public void testRemoveColumnMetaFromColumnsForAddDomainSpecifColumns() {
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", view.getInitColumns(), view.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = new ListTable<TaskSummary>(gridPreferences);
        when(view.getListGrid()).thenReturn(extendedPagedTable);

        extendedPagedTable.getGridPreferencesStore().getColumnPreferences().add(new GridColumnPreference("Extra",-1,""));

        Set<String> set = Sets.newHashSet("Extra");

        view.initColumns(extendedPagedTable);
        assertThat(extendedPagedTable.getColumnMetaList().size()).isEqualTo(16);
        view.addDomainSpecifColumns(set);

        assertThat(set.size()).isEqualTo(0);
    }

    @Test
    public void testRemoveColumnMetaFromExtendedPagedTableForAddDomainSpecifColumns() {
        GridGlobalPreferences gridPreferences = new GridGlobalPreferences("test", view.getInitColumns(), view.getBannedColumns());

        ListTable<TaskSummary> extendedPagedTable = spy(new ListTable<TaskSummary>(gridPreferences));
        when(view.getListGrid()).thenReturn(extendedPagedTable);

        Column<TaskSummary, String> column = view.createTextColumn("Extra", taskSummary -> taskSummary.getName());
        ColumnMeta<TaskSummary> columnMeta = new ColumnMeta<TaskSummary>(column,"Extra",true,true);
        Set<String> set = Collections.singleton("Extra_test");

        view.initColumns(extendedPagedTable);

        doAnswer(handler -> {
            extendedPagedTable.getColumnMetaList().remove(extendedPagedTable.getColumnMetaList().stream().filter(c -> c.getCaption().equals("Extra")).findFirst().get());
            return extendedPagedTable;
        }).when(extendedPagedTable).removeColumnMeta(any());

        assertThat(extendedPagedTable.getColumnMetaList().size()).isEqualTo(15);
        extendedPagedTable.addColumns(Collections.singletonList(columnMeta));

        assertThat(extendedPagedTable.getColumnMetaList().stream()).extracting(c -> c.getCaption()).hasSize(16).containsOnlyOnce("Extra");
        view.addDomainSpecifColumns(set);

        assertThat(extendedPagedTable.getColumnMetaList().stream()).extracting(c -> c.getCaption()).hasSize(16).doesNotContain("Extra");

        assertThat(set.size()).isEqualTo(1);
    }
}
