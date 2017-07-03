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

import java.util.*;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.view.client.AsyncDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetEditorManager;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractTaskListViewTest {

    public static final String TEST_USER_ID = "testUser";

    protected CallerMock<UserPreferencesService> callerMockUserPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    @Mock
    protected ExtendedPagedTable<TaskSummary> currentListGrid;

    @Mock
    protected GridPreferencesStore gridPreferencesStoreMock;

    @Mock
    protected MultiGridPreferencesStore multiGridPreferencesStoreMock;

    @Mock
    protected DataSetQueryHelper dataSetQueryHelperMock;

    @Mock
    protected FilterPagedTable filterPagedTableMock;

    @Mock
    protected Button mockButton;

    @Mock
    protected User identity;

    @Mock
    protected Cell.Context cellContext;

    @Mock
    protected Element cellParent;

    @Mock
    protected ActionCell.Delegate<TaskSummary> cellDelegate;

    @Mock
    @SuppressWarnings("unused")
    protected DataSetEditorManager dataSetEditorManagerMock;

    @Spy
    protected FilterSettings filterSettings;

    public abstract AbstractTaskListView getView();

    public abstract AbstractTaskListPresenter getPresenter();

    public abstract int getExpectedDefaultTabFilterCount();

    @Before
    public void setupMocks() {
        when(getPresenter().getDataProvider()).thenReturn(mock(AsyncDataProvider.class));
        when(getPresenter().createTableSettingsPrototype()).thenReturn(filterSettings);
        when(getPresenter().createSearchTabSettings()).thenReturn(filterSettings);

        when(filterPagedTableMock.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStoreMock);
        when(currentListGrid.getGridPreferencesStore()).thenReturn(new GridPreferencesStore());
        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        getView().setPreferencesService(callerMockUserPreferencesService);

        when(identity.getIdentifier()).thenReturn(TEST_USER_ID);
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

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void initColumnsTest() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                assertTrue(columns.size() == 14);
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void initColumnsWithTaskVarColumnsTest() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                assertTrue(columns.size() == 17);
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
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void initDefaultFiltersOwnTaskFilter() {
        AbstractTaskListPresenter<?> presenter = getPresenter();
        getView().initDefaultFilters(new GridGlobalPreferences(),
                                     mockButton);

        verify(filterPagedTableMock,
               times(getExpectedDefaultTabFilterCount()))
                .addTab(any(ExtendedPagedTable.class),
                        anyString(),
                        any(Command.class),
                        eq(false));
        verify(filterPagedTableMock).addAddTableButton(mockButton);
        verify(presenter).setAddingDefaultFilters(true);
    }

    @Test
    public void addDomainSpecifColumnsTest() {
        final Set<String> domainColumns = new HashSet<String>();
        domainColumns.add("var1");
        domainColumns.add("var2");
        domainColumns.add("var3");

        getView().addDomainSpecifColumns(currentListGrid,
                                         domainColumns);

        final ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(currentListGrid).addColumns(argument.capture());

        final List<ColumnMeta> columns = argument.getValue();
        assertTrue(columns.size() == 3);
        int i = 0;
        for (String domainColumn : domainColumns) {
            assertEquals(columns.get(i).getCaption(),
                         domainColumn);
            i++;
        }
    }

    @Test
    public void setDefaultFilterTitleAndDescriptionTest() {
        getView().resetDefaultFilterTitleAndDescription();
        int filterCount = getExpectedDefaultTabFilterCount();
        verify(filterPagedTableMock,
               times(filterCount)).getMultiGridPreferencesStore();
        verify(filterPagedTableMock,
               times(1)).saveTabSettings(eq(AbstractTaskListView.TAB_SEARCH),
                                         any(HashMap.class));
        verify(filterPagedTableMock,
               times(filterCount)).saveTabSettings(anyString(),
                                                   any(HashMap.class));
    }

    @Test
    public void initialColumsTest() {
        AbstractTaskListView view = getView();
        view.init(getPresenter());
        List<GridColumnPreference> columnPreferences = view.getListGrid().getGridPreferencesStore().getColumnPreferences();
        assertEquals(COLUMN_NAME,
                     columnPreferences.get(0).getName());
        assertEquals(COLUMN_PROCESS_ID,
                     columnPreferences.get(1).getName());
        assertEquals(COLUMN_STATUS,
                     columnPreferences.get(2).getName());
        assertEquals(COLUMN_CREATED_ON,
                     columnPreferences.get(3).getName());
        assertEquals(AbstractTaskListView.COL_ID_ACTIONS,
                     columnPreferences.get(4).getName());
    }
}