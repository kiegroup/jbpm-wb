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
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.COL_ID_ACTIONS;
import static org.jbpm.workbench.ht.client.editors.taskslist.TaskListViewImpl.*;
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
}
