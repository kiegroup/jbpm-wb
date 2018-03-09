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
package org.jbpm.workbench.common.client.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.AsyncDataProvider;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractMultiGridViewTest<T extends GenericSummary> {

    @Spy
    protected FilterSettings filterSettings;

    @Mock
    protected AsyncDataProvider dataProviderMock;

    protected CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    @Spy
    protected MultiGridPreferencesStore multiGridPreferencesStore;

    @Spy
    protected GridPreferencesStore gridPreferencesStore;

    protected abstract AbstractMultiGridView getView();

    protected abstract AbstractMultiGridPresenter getPresenter();

    public abstract List<String> getExpectedInitialColumns();

    public abstract List<String> getExpectedBannedColumns();

    public abstract Integer getExpectedNumberOfColumns();

    @Before
    public void setupMocks() {
        userPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        getView().setUserPreferencesService(userPreferencesService);
        when(getPresenter().getDataProvider()).thenReturn(dataProviderMock);
        when(userPreferencesServiceMock.loadUserPreferences(anyString(),
                                                            eq(UserPreferencesType.GRIDPREFERENCES))).thenReturn(new GridPreferencesStore(new GridGlobalPreferences()));
    }

    @Test
    public void selectionIgnoreColumnTest() {
        ExtendedPagedTable<GenericSummary> extPagedTable = new ExtendedPagedTable<GenericSummary>(new GridGlobalPreferences());
        Column testCol = getView().createTextColumn("testCol",
                                                    (val -> val));

        extPagedTable.addSelectionIgnoreColumn(testCol);
        assertFalse(extPagedTable.isSelectionIgnoreColumn(extPagedTable.getColumnIndex(testCol)));
        assertTrue(extPagedTable.removeSelectionIgnoreColumn(testCol));

        extPagedTable.addColumn(testCol,
                                "");
        assertFalse(extPagedTable.isSelectionIgnoreColumn(extPagedTable.getColumnIndex(testCol)));
        extPagedTable.addSelectionIgnoreColumn(testCol);
        assertTrue(extPagedTable.isSelectionIgnoreColumn(extPagedTable.getColumnIndex(testCol)));
    }

    @Test
    public void testInitialColumns() {
        final List<String> expectedInitColumns = getExpectedInitialColumns();

        assertEquals(expectedInitColumns.size(),
                     getView().getInitColumns().size());

        for (int i = 0; i < expectedInitColumns.size(); i++) {
            assertEquals(expectedInitColumns.get(i),
                         getView().getInitColumns().get(i));
        }
    }

    @Test
    public void testBannedColumns() {
        List<String> bannedColumns = getView().getBannedColumns();

        assertEquals(getExpectedBannedColumns().size(),
                     bannedColumns.size());

        for (int i = 0; i < bannedColumns.size(); i++) {
            assertEquals(getExpectedBannedColumns().get(i),
                         bannedColumns.get(i));
        }
    }

    @Test
    public void testDataStoreNameIsSet() {
        final ListTable<T> currentListGrid = spy(new ListTable<T>(new GridGlobalPreferences()));
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

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void testInitColumns() {
        final ListTable<T> currentListGrid = spy(new ListTable<T>(new GridGlobalPreferences()));
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                assertTrue(columns.size() == getExpectedNumberOfColumns());
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStore);
        when(gridPreferencesStore.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }
}