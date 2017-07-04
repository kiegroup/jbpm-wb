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
package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorListViewImplTest {

    @GwtMock
    protected ExtendedPagedTable<ExecutionErrorSummary> currentListGrid;

    @Mock
    protected GridPreferencesStore gridPreferencesStoreMock;

    @Mock
    protected ExecutionErrorListPresenter presenter;

    @Mock
    protected UserPreferencesService userPreferencesService;

    @Mock
    FilterPagedTable filterPagedTableMock;

    @Mock
    MultiGridPreferencesStore multiGridPreferencesStoreMock;

    @Spy
    private FilterSettings filterSettings;

    @InjectMocks
    private ExecutionErrorListViewImpl executionErrorListView;

    @Before
    public void setup() {
        when(presenter.getDataProvider()).thenReturn(mock(AsyncDataProvider.class));
        when(presenter.createTableSettingsPrototype()).thenReturn(filterSettings);
        when(presenter.createSearchTabSettings()).thenReturn(filterSettings);

        when(presenter.createAllTabSettings()).thenReturn(filterSettings);
        when(presenter.createNewTabSettings()).thenReturn(filterSettings);
        when(presenter.createAcknowledgedTabSettings()).thenReturn(filterSettings);

        final CallerMock<UserPreferencesService> caller = new CallerMock<>(userPreferencesService);
        executionErrorListView.setPreferencesService(caller);
    }

    @Test
    public void testDataStoreNameIsSet() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                for (ColumnMeta columnMeta : columns) {
                    assertNotNull(columnMeta.getColumn().getDataStoreName());
                }
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());

        executionErrorListView.initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void testSetDefaultFilterTitleAndDescription() {
        when(filterPagedTableMock.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStoreMock);
        executionErrorListView.resetDefaultFilterTitleAndDescription();

        verify(filterPagedTableMock,
               times(4)).getMultiGridPreferencesStore();
        verify(filterPagedTableMock,
               times(4)).saveTabSettings(anyString(),
                                         any(HashMap.class));
        verify(filterPagedTableMock).saveTabSettings(eq(ExecutionErrorListViewImpl.TAB_SEARCH),
                                                     any(HashMap.class));
        verify(filterPagedTableMock).saveTabSettings(eq(ExecutionErrorListViewImpl.TAB_ACK),
                                                     any(HashMap.class));
        verify(filterPagedTableMock).saveTabSettings(eq(ExecutionErrorListViewImpl.TAB_ALL),
                                                     any(HashMap.class));
        verify(filterPagedTableMock).saveTabSettings(eq(ExecutionErrorListViewImpl.TAB_NEW),
                                                     any(HashMap.class));
    }

    @Test
    public void testInitColumns() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                assertTrue(columns.size() == 15);
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        executionErrorListView.initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void testInitialColumns() {
        List<String> initColumns = executionErrorListView.getInitColumns();

        assertEquals(AbstractMultiGridView.COL_ID_SELECT,
                     initColumns.get(0));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_ERROR_TYPE,
                     initColumns.get(1));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_PROCESS_INST_ID,
                     initColumns.get(2));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_ERROR_DATE,
                     initColumns.get(3));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_DEPLOYMENT_ID,
                     initColumns.get(4));
        assertEquals(ExecutionErrorDataSetConstants.COL_ID_ACTIONS,
                     initColumns.get(5));
        assertEquals(6,initColumns.size());
    }

    @Test
    public void testBannedColumns() {
        List<String> bannedColumns = executionErrorListView.getBannedColumns();
        assertEquals(AbstractMultiGridView.COL_ID_SELECT,
                     bannedColumns.get(0));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_ERROR_TYPE,
                     bannedColumns.get(1));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_PROCESS_INST_ID,
                     bannedColumns.get(2));
        assertEquals(ExecutionErrorDataSetConstants.COLUMN_ERROR_DATE,
                     bannedColumns.get(3));
        assertEquals(ExecutionErrorDataSetConstants.COL_ID_ACTIONS,
                     bannedColumns.get(4));
        assertEquals(5,bannedColumns.size());
    }
}
