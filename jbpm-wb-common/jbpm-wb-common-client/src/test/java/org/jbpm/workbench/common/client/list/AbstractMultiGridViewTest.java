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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.mocks.CallerMock;

import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.TAB_SEARCH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(Scheduler.class)
public class AbstractMultiGridViewTest {

    private static final String TEST_KEY = "TEST";

    private static final String TEST_KEY_GRID1 = "TEST_1";

    private static final String TEST_KEY_GRID2 = "TEST_2";

    @GwtMock
    protected Button mockButton;

    @GwtMock
    protected FilterPagedTable filterPagedTable;

    @Spy
    protected FilterSettings filterSettings;

    @Mock
    protected DataSetEditorManager dataSetEditorManager;

    @Mock
    AsyncDataProvider dataProviderMock;

    @Spy
    private AbstractMultiGridView testListView;

    @Mock
    private AbstractMultiGridPresenter presenter;

    private CallerMock<UserPreferencesService> callerMockUserPreferencesService;

    @Mock
    private UserPreferencesService userPreferencesServiceMock;

    @Mock
    private MultiGridPreferencesStore multiGridPreferencesStore;

    @Before
    public void setupMocks() {
        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        testListView.setPreferencesService(callerMockUserPreferencesService);
        testListView.setDataSetEditorManager(dataSetEditorManager);
        when(userPreferencesServiceMock.loadUserPreferences(TEST_KEY,
                                                            UserPreferencesType.MULTIGRIDPREFERENCES)).thenReturn(multiGridPreferencesStore);
        when(presenter.getDataProvider()).thenReturn(dataProviderMock);
        when(presenter.createTableSettingsPrototype()).thenReturn(filterSettings);
        when(presenter.createSearchTabSettings()).thenReturn(filterSettings);
        when(filterPagedTable.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStore);
    }

    @Test
    public void initWithoutFiltersDefinedTest() {
        when(multiGridPreferencesStore.getSelectedGrid()).thenReturn("");
        when(multiGridPreferencesStore.getGridsId()).thenReturn(new ArrayList<String>());
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY,
                                                              new ArrayList(),
                                                              new ArrayList<String>());
        testListView.init(presenter,
                          ggp);

        verify(userPreferencesServiceMock).loadUserPreferences(TEST_KEY,
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(testListView).initDefaultFilters(ggp,
                                                mockButton);
    }

    @Test
    public void initWithFilterStoredTest() {
        ArrayList<String> existingFilters = new ArrayList<String>();
        existingFilters.add(TEST_KEY_GRID1);
        existingFilters.add(TEST_KEY_GRID2);

        when(multiGridPreferencesStore.getGridsId()).thenReturn(existingFilters);
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY,
                                                              new ArrayList(),
                                                              new ArrayList<String>());
        testListView.init(presenter,
                          ggp);

        verify(userPreferencesServiceMock).loadUserPreferences(TEST_KEY,
                                                               UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(testListView).resetDefaultFilterTitleAndDescription();
        verify(presenter).setAddingDefaultFilters(true);
        verify(testListView).loadGridInstance(ggp,
                                              TEST_KEY_GRID1);
        verify(testListView).loadGridInstance(ggp,
                                              TEST_KEY_GRID2);

        verify(presenter).setAddingDefaultFilters(false);
    }

    @Test
    public void validKeyForAdditionalFilterIncludesUserDefinedTest() {
        testListView.setFilterPagedTable(filterPagedTable);
        testListView.getValidKeyForAdditionalListGrid(TEST_KEY);

        verify(filterPagedTable).getValidKeyForAdditionalListGrid(TEST_KEY + AbstractMultiGridView.USER_DEFINED);
    }

    @Test
    public void selectFirstTabAndEnableQueriesTest() {
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY,
                                                              new ArrayList(),
                                                              new ArrayList<String>());
        testListView.init(presenter,
                          ggp);
        testListView.getSelectFirstTabAndEnableQueriesCommand().execute();

        verify(presenter).setAddingDefaultFilters(false);
        verify(multiGridPreferencesStore).setSelectedGrid(TAB_SEARCH);
        verify(filterPagedTable).setSelectedTab();
    }

    @Test
    public void selectionIgnoreColumnTest() {
        ExtendedPagedTable<GenericSummary> extPagedTable = new ExtendedPagedTable<GenericSummary>(new GridGlobalPreferences());
        Column testCol = testListView.createTextColumn("testCol",
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
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY,
                                                              new ArrayList(),
                                                              new ArrayList<String>());
        testListView.init(presenter,
                          ggp);

        reset(presenter);
        ExtendedPagedTable table = testListView.createExtendedPagedTable(ggp,
                                                                         filterKey);

        assertEquals(filterKey,
                     table.getGridPreferencesStore().getPreferenceKey());

        table = testListView.createExtendedPagedTable(ggp,
                                                      TAB_SEARCH);

        assertEquals(ggp.getKey() + TAB_SEARCH,
                     table.getGridPreferencesStore().getPreferenceKey());
    }
}