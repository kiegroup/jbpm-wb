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
import com.google.gwtmockito.GwtMock;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetEditorManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.*;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.TAB_SEARCH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractMultiGridViewTest<T extends GenericSummary> {

    protected static final String TEST_KEY = "TEST";
    protected static final String TEST_KEY_GRID1 = "TEST_1";
    protected static final String TEST_KEY_GRID2 = "TEST_2";
    protected static final String TEST_USER_ID = "testUser";

    @GwtMock
    protected FilterPagedTable filterPagedTable;

    @Spy
    protected FilterSettings filterSettings;

    @Mock
    protected DataSetEditorManager dataSetEditorManager;

    @Mock
    protected AsyncDataProvider dataProviderMock;

    protected CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    @Spy
    protected MultiGridPreferencesStore multiGridPreferencesStore;

    @Spy
    protected GridPreferencesStore gridPreferencesStore;

    @Mock
    protected User identity;

    protected abstract AbstractMultiGridView getView();

    protected abstract AbstractMultiGridPresenter getPresenter();

    public abstract List<String> getExpectedTabs();

    public abstract List<String> getExpectedInitialColumns();

    public abstract List<String> getExpectedBannedColumns();

    public abstract Integer getExpectedNumberOfColumns();

    @Before
    public void setupMocks() {
        userPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        getView().setUserPreferencesService(userPreferencesService);
        getView().setDataSetEditorManager(dataSetEditorManager);
        when(userPreferencesServiceMock.loadUserPreferences(getView().getGridGlobalPreferencesKey(),
                                                            UserPreferencesType.MULTIGRIDPREFERENCES)).thenReturn(multiGridPreferencesStore);
        when(getPresenter().getDataProvider()).thenReturn(dataProviderMock);
        when(getPresenter().createTableSettingsPrototype()).thenReturn(filterSettings);
        when(getPresenter().createSearchTabSettings()).thenReturn(filterSettings);
        when(filterPagedTable.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStore);
        when(userPreferencesServiceMock.loadUserPreferences(anyString(),
                                                            eq(UserPreferencesType.GRIDPREFERENCES))).thenReturn(new GridPreferencesStore(new GridGlobalPreferences()));
        when(identity.getIdentifier()).thenReturn(TEST_USER_ID);
    }

    @Test
    public void initWithoutFiltersDefinedTest() {
        when(multiGridPreferencesStore.getSelectedGrid()).thenReturn("");
        when(multiGridPreferencesStore.getGridsId()).thenReturn(new ArrayList<String>());

        getView().init(getPresenter());

        verify(userPreferencesServiceMock).loadUserPreferences(getView().getGridGlobalPreferencesKey(),
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(getView()).initDefaultFilters();
    }

    @Test
    public void initWithFilterStoredTest() {
        ArrayList<String> existingFilters = new ArrayList<String>();
        existingFilters.add(TEST_KEY_GRID1);
        existingFilters.add(TEST_KEY_GRID2);

        when(multiGridPreferencesStore.getGridsId()).thenReturn(existingFilters);

        getView().init(getPresenter());

        verify(userPreferencesServiceMock).loadUserPreferences(getView().getGridGlobalPreferencesKey(),
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);

        verify(getPresenter()).setAddingDefaultFilters(true);
        verify(getView()).loadGridInstance(TEST_KEY_GRID1);
        verify(getView()).loadGridInstance(TEST_KEY_GRID2);
    }

    @Test
    public void validKeyForAdditionalFilterIncludesUserDefinedTest() {
        getView().setFilterPagedTable(filterPagedTable);
        getView().getValidKeyForAdditionalListGrid(TEST_KEY);

        verify(filterPagedTable).getValidKeyForAdditionalListGrid(TEST_KEY + AbstractMultiGridView.USER_DEFINED);
    }

    @Test
    public void selectFirstTabAndEnableQueriesTest() {
        getView().init(getPresenter());
        getView().getSelectFirstTabAndEnableQueriesCommand().execute();

        verify(getPresenter()).setAddingDefaultFilters(false);
        verify(multiGridPreferencesStore,
               times(2)).setSelectedGrid(TAB_SEARCH);
        verify(filterPagedTable).setSelectedTab();
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
    public void testCreateExtendedPagedTable_PreferenceKeySet() {
        String filterKey = "filterKey";

        getView().init(getPresenter());

        reset(getPresenter());
        ExtendedPagedTable table = getView().createExtendedPagedTable(filterKey);

        assertEquals(filterKey,
                     table.getGridPreferencesStore().getPreferenceKey());

        table = getView().createExtendedPagedTable(TAB_SEARCH);

        assertEquals(getView().getGridGlobalPreferencesKey() + TAB_SEARCH,
                     table.getGridPreferencesStore().getPreferenceKey());
    }

    @Test
    public void testSearchTabIsAddedSavedGrids() {
        final String tab1 = "tab1";
        multiGridPreferencesStore.getGridsId().add(tab1);

        getView().init(getPresenter());

        assertEquals(2,
                     multiGridPreferencesStore.getGridsId().size());
        assertEquals(TAB_SEARCH,
                     multiGridPreferencesStore.getGridsId().get(0));
        assertEquals(tab1,
                     multiGridPreferencesStore.getGridsId().get(1));
        assertTabAdded(TAB_SEARCH,
                       tab1);
    }

    @Test
    public void testSearchTabIsFirst() {
        final String tab1 = "tab1";
        final String tab2 = "tab2";
        multiGridPreferencesStore.getGridsId().add(tab1);
        multiGridPreferencesStore.getGridsId().add(tab2);
        multiGridPreferencesStore.getGridsId().add(TAB_SEARCH);

        getView().init(getPresenter());

        assertEquals(3,
                     multiGridPreferencesStore.getGridsId().size());
        assertEquals(TAB_SEARCH,
                     multiGridPreferencesStore.getGridsId().get(0));
        assertEquals(tab1,
                     multiGridPreferencesStore.getGridsId().get(1));
        assertEquals(tab2,
                     multiGridPreferencesStore.getGridsId().get(2));
        assertTabAdded(TAB_SEARCH,
                       tab1,
                       tab2);
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
    public void testColumnPreferences() {
        final List<String> expectedInitColumns = getExpectedInitialColumns();

        getView().init(getPresenter());

        List<GridColumnPreference> columnPreferences = getView().getListGrid().getGridPreferencesStore().getColumnPreferences();

        assertEquals(expectedInitColumns.size(),
                     columnPreferences.size());

        for (int i = 0; i < expectedInitColumns.size(); i++) {
            assertEquals(expectedInitColumns.get(i),
                         columnPreferences.get(i).getName());
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
    public void testInitDefaultFilters() {
        getView().initDefaultFilters();

        assertTabAdded(getExpectedTabs().toArray(new String[]{}));
    }

    protected void assertTabAdded(final String... keys) {
        for (String key : keys) {
            verify(filterPagedTable)
                    .addTab(any(ExtendedPagedTable.class),
                            eq(key),
                            any(Command.class),
                            eq(false));
        }
        verify(filterPagedTable,
               times(keys.length))
                .addTab(any(ExtendedPagedTable.class),
                        anyString(),
                        any(Command.class),
                        eq(false));
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