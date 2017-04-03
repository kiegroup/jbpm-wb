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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.view.client.AsyncDataProvider;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.ht.client.editors.taskslist.TaskListViewImpl;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListViewTest {

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
    public User identity;

    @Mock @SuppressWarnings("unused")
    private DataSetEditorManager dataSetEditorManagerMock;
    
    @Mock
    protected HashMap mockGridSettingsMap;
    
    protected abstract AbstractTaskListView getView();

    protected abstract AbstractTaskListPresenter<?> getPresenter();
    
    protected abstract int getInitialTabCount();
    
    protected abstract String getDatasetId();
    
    @Before
    public void setupMocks() {
        when(getPresenter().getDataProvider()).thenReturn(mock(AsyncDataProvider.class));

        when(filterPagedTableMock.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStoreMock);
        when(multiGridPreferencesStoreMock.getGridSettings(anyString())).thenReturn(mockGridSettingsMap);
        
        when(currentListGrid.getGridPreferencesStore()).thenReturn(new GridPreferencesStore());
        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        getView().setPreferencesService(callerMockUserPreferencesService);
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

        getView().initColumns(currentListGrid);

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void testIsNullTableSettingsPrototype(){
        when(identity.getIdentifier()).thenReturn("user");
        getView().setIdentity(identity);
        FilterSettings filterSettings = getView().createTableSettingsPrototype();
        List <DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for(DataSetOp op : ops){
            if(op.getType().equals(DataSetOpType.FILTER)){
                List<ColumnFilter> columnFilters = ((DataSetFilter)op).getColumnFilterList();
                for(ColumnFilter columnFilter : columnFilters){
                    assertTrue((columnFilter).toString().contains(COLUMN_ACTUAL_OWNER + " is_null"));
                }
            }
        }
    }

    @Test
    public void getVariablesTableSettingsTest(){
        FilterSettings filterSettings = getView().getVariablesTableSettings("Test");
        List <DataSetOp> ops = filterSettings.getDataSetLookup().getOperationList();
        for(DataSetOp op : ops){
            if(op.getType().equals(DataSetOpType.FILTER)){
                List<ColumnFilter> columnFilters = ((DataSetFilter)op).getColumnFilterList();
                for(ColumnFilter columnFilter : columnFilters){
                    assertTrue((columnFilter).toString().contains(COLUMN_TASK_VARIABLE_TASK_NAME + " = Test"));
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
                assertTrue(columns.size()==14);
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void initColumnsWithTaskVarColumnsTest() {
        doAnswer( new Answer() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[ 0 ];
                assertTrue(columns.size()==17);
                return null;
            }
        } ).when( currentListGrid ).addColumns(anyList());

        ArrayList<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
        columnPreferences.add(new GridColumnPreference( "var1",0,"40" ));
        columnPreferences.add(new GridColumnPreference( "var2",1,"40" ));
        columnPreferences.add(new GridColumnPreference("var3", 1, "40"));
        when(currentListGrid.getGridPreferencesStore()).thenReturn(gridPreferencesStoreMock);
        when(gridPreferencesStoreMock.getColumnPreferences()).thenReturn(columnPreferences);

        getView().initColumns( currentListGrid );

        verify( currentListGrid ).addColumns( anyList() );
    }

    @Test
    public void initDefaultFiltersOwnTaskFilter() {
        int initTabCount = getInitialTabCount();
        getView().initDefaultFilters(new GridGlobalPreferences(), mockButton);

        verify(filterPagedTableMock, times(initTabCount)).addTab(any(ExtendedPagedTable.class), anyString(), any(Command.class));
        verify(filterPagedTableMock).addAddTableButton(mockButton);
        verify(getPresenter()).setAddingDefaultFilters(true);
        verify(getPresenter()).setAddingDefaultFilters(false);
    }

    @Test
    public void addDomainSpecifColumnsTest() {
        final Set<String> domainColumns = new HashSet<String>();
        domainColumns.add("var1");
        domainColumns.add("var2");
        domainColumns.add("var3");

        getView().addDomainSpecifColumns(currentListGrid, domainColumns);

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

    @Test
    public void setDefaultFilterTitleAndDescriptionTest() {
        int initTabCount = getInitialTabCount();
        getView().resetDefaultFilterTitleAndDescription();

        verify(filterPagedTableMock, times(initTabCount)).getMultiGridPreferencesStore();
        verify(filterPagedTableMock, times(initTabCount)).saveTabSettings(anyString(), eq(mockGridSettingsMap));
        assertEquals(multiGridPreferencesStoreMock.getGridSettings(anyString()), mockGridSettingsMap);
    }

    @Test
    public void initialColumsTest(){
        getView().init(getPresenter());
        List<GridColumnPreference> columnPreferences = getView().getListGrid().getGridPreferencesStore().getColumnPreferences();
        assertEquals(COLUMN_NAME,columnPreferences.get(0).getName());
        assertEquals(COLUMN_PROCESS_ID,columnPreferences.get(1).getName());
        assertEquals(COLUMN_STATUS,columnPreferences.get(2).getName());
        assertEquals(COLUMN_CREATED_ON,columnPreferences.get(3).getName());
        assertEquals(TaskListViewImpl.COL_ID_ACTIONS,columnPreferences.get(4).getName());
    }

    @Test
    public void testDatasetName(){
        assertEquals(getDatasetId(), getView().createTableSettingsPrototype().getDataSetLookup().getDataSetUUID());
    }

}
