/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetTaskListGridViewTest {

    private CallerMock<UserPreferencesService> callerMockUserPreferencesService;

    @Mock
    private UserPreferencesService userPreferencesServiceMock;

    @Mock
    protected ExtendedPagedTable<TaskSummary> currentListGrid;

    @Mock
    protected GridPreferencesStore gridPreferencesStoreMock;

    @Mock
    MultiGridPreferencesStore multiGridPreferencesStoreMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelperMock;

    @Mock
    FilterPagedTable filterPagedTableMock;

    @Mock
    protected Button mockButton;

    @InjectMocks
    private DataSetTasksListGridViewImpl view;

    @Mock
    private DataSetTasksListGridPresenter presenter;

    @Mock
    public User identity;

    @Mock
    private DataSetEditorManager dataSetEditorManagerMock;

    @Before
    public void setupMocks() {
        when(presenter.getDataProvider()).thenReturn(mock(AsyncDataProvider.class));

        when(filterPagedTableMock.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStoreMock);

        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        view.setPreferencesService(callerMockUserPreferencesService);
    }

    @Test
    public void testDataStoreNameIsSet() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                for ( ColumnMeta columnMeta : columns ) {
                    assertNotNull( columnMeta.getColumn().getDataStoreName() );
                }
                return null;
            }
        } ).when( currentListGrid ).addColumns( anyList() );

        when(currentListGrid.getGridPreferencesStore()).thenReturn(new GridPreferencesStore());

        view.initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void testIsNullTableSettingsPrototype(){
        when(identity.getIdentifier()).thenReturn("user");
        view.setIdentity(identity);
        FilterSettings filterSettings = view.createTableSettingsPrototype();
        List <DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for(DataSetOp op : ops){
            if(op.getType().equals(DataSetOpType.FILTER)){
                List<ColumnFilter> columnFilters = ((DataSetFilter)op).getColumnFilterList();
                for(ColumnFilter columnFilter : columnFilters){
                    assertTrue((columnFilter).toString().contains("actualOwner is_null"));
                }
            }
        }
    }

    @Test
    public void getVariablesTableSettingsTest(){
        FilterSettings filterSettings = view.getVariablesTableSettings("Test");
        List <DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for(DataSetOp op : ops){
            if(op.getType().equals(DataSetOpType.FILTER)){
                List<ColumnFilter> columnFilters = ((DataSetFilter)op).getColumnFilterList();
                for(ColumnFilter columnFilter : columnFilters){
                    assertTrue((columnFilter).toString().contains("name = Test"));
                }
            }
        }
    }

    @Test
    public void initColumnsTest() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                assertTrue(columns.size()==10);
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        view.initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void initColumnsWithTaskVarColumnsTest() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                assertTrue(columns.size()==13);
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        columnPreferences.add(new GridColumnPreference( "var1",0,"40" ));
        columnPreferences.add(new GridColumnPreference( "var2",1,"40" ));
        columnPreferences.add(new GridColumnPreference("var3", 1, "40"));
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        view.initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void initDefaultFiltersOwnTaskFilter() {
        view.initDefaultFilters(new GridGlobalPreferences(), mockButton);

        verify(filterPagedTableMock, times(5)).addTab(any(ExtendedPagedTable.class), anyString(), any(Command.class));
        verify(filterPagedTableMock).addAddTableButton(mockButton);
    }

    @Test
    public void addDomainSpecifColumnsTest() {
        final Set<String> domainColumns = new HashSet<String>();
        domainColumns.add("var1");
        domainColumns.add("var2");
        domainColumns.add("var3");

        view.addDomainSpecifColumns(currentListGrid, domainColumns);

        final ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(currentListGrid).addColumns(argument.capture());

        final List<ColumnMeta> columns = argument.getValue();
        assertTrue(columns.size() == 3);
        int i = 0;
        for (String domainColumn : domainColumns) {
            assertEquals(columns.get(i).getCaption(), domainColumn);
            i++;
        }

    }

}
