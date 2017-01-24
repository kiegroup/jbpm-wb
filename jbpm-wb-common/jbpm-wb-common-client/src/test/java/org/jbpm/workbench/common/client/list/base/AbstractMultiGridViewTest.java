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
package org.jbpm.workbench.common.client.list.base;


import java.util.ArrayList;


import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.jbpm.workbench.common.client.experimental.grid.base.ExtendedPagedTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;


@RunWith(GwtMockitoTestRunner.class)
public class AbstractMultiGridViewTest {

    private static final String TEST_KEY = "TEST";

    private static final String TEST_KEY_GRID1 = "TEST_1";

    private static final String TEST_KEY_GRID2 = "TEST_2";

    private AbstractMultiGridView testListView;


    @Mock
    private AbstractListPresenter presenter;

    @Mock
    protected Button mockButton;

    private CallerMock<UserPreferencesService> callerMockUserPreferencesService;

    @Mock
    private UserPreferencesService userPreferencesServiceMock;

    @Mock
    private MultiGridPreferencesStore multiGridPreferencesStore;

    @Mock
    AsyncDataProvider dataProviderMock;

    @Mock
    protected FilterPagedTable filterPagedTable;


    @Before
    public void setupMocks() {
        testListView = spy(AbstractMultiGridView.class);
        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        testListView.setPreferencesService(callerMockUserPreferencesService);
        when(userPreferencesServiceMock.loadUserPreferences(TEST_KEY, UserPreferencesType.MULTIGRIDPREFERENCES)).thenReturn(multiGridPreferencesStore);
        when(presenter.getDataProvider()).thenReturn(dataProviderMock);

    }

    @Test
    public void initWithoutFiltersDefinedTest() {
        when(multiGridPreferencesStore.getSelectedGrid()).thenReturn("");
        when(multiGridPreferencesStore.getGridsId()).thenReturn(new ArrayList<String>());
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY, new ArrayList(), new ArrayList<String>());
        testListView.init(presenter, ggp, mockButton);
        verify(userPreferencesServiceMock).loadUserPreferences(TEST_KEY, UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(testListView).initDefaultFilters(ggp, mockButton);
    }

    @Test
    public void initWithFilterStoredTest() {
        ArrayList<String> existingFilters = new ArrayList<String>();
        existingFilters.add(TEST_KEY_GRID1);
        existingFilters.add(TEST_KEY_GRID2);

        String selectedGrid = TEST_KEY_GRID1;

        when(multiGridPreferencesStore.getSelectedGrid()).thenReturn(selectedGrid);
        when(multiGridPreferencesStore.getGridsId()).thenReturn(existingFilters);
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY, new ArrayList(), new ArrayList<String>());
        testListView.init(presenter, ggp, mockButton);

        verify(userPreferencesServiceMock).loadUserPreferences(TEST_KEY, UserPreferencesType.MULTIGRIDPREFERENCES);
        verify(testListView).resetDefaultFilterTitleAndDescription();
        verify(presenter).setAddingDefaultFilters(true);
        verify(testListView).loadGridInstance(ggp, TEST_KEY_GRID1);
        verify(testListView).loadGridInstance(ggp, TEST_KEY_GRID2);

        verify(presenter).setAddingDefaultFilters(false);

        ExtendedPagedTable listGrid1 = new ExtendedPagedTable(10, ggp);
        ExtendedPagedTable listGrid2 = new ExtendedPagedTable(10, ggp);
        when(testListView.loadGridInstance(ggp, TEST_KEY_GRID1)).thenReturn(listGrid1);
        when(testListView.loadGridInstance(ggp, TEST_KEY_GRID2)).thenReturn(listGrid2);
        verify(multiGridPreferencesStore).setSelectedGrid(selectedGrid);
        verify(userPreferencesServiceMock).saveUserPreferences(multiGridPreferencesStore);

    }

    @Test
    public void validKeyForAdditionalFilterIncludesUserDefinedTest() {
        testListView.setFilterPagedTable(filterPagedTable);
        testListView.getValidKeyForAdditionalListGrid(TEST_KEY);
        verify(filterPagedTable).getValidKeyForAdditionalListGrid(TEST_KEY + AbstractMultiGridView.USER_DEFINED);

    }

    @Test
    public void selectFirstTabAndEnableQueries() {
        GridGlobalPreferences ggp = new GridGlobalPreferences(TEST_KEY, new ArrayList(), new ArrayList<String>());
        testListView.init(presenter,ggp,mockButton);
        testListView.selectFirstTabAndEnableQueries("defaultKey");

        verify(presenter).setAddingDefaultFilters(false);

    }
}