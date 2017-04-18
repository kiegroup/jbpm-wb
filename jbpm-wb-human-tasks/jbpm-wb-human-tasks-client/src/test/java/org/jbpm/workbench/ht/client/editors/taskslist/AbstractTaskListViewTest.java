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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.view.client.AsyncDataProvider;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import org.apache.commons.lang.ArrayUtils;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ButtonActionCell;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;
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
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListViewTest {
    
    public static final String TEST_USER_ID = "testUser";
    public static final String[] TASK_STATUS_LIST =  new String[]{
            TASK_STATUS_COMPLETED, TASK_STATUS_CREATED, TASK_STATUS_ERROR, TASK_STATUS_EXITED, TASK_STATUS_FAILED,
            TASK_STATUS_INPROGRESS, TASK_STATUS_OBSOLETE, TASK_STATUS_READY, TASK_STATUS_RESERVED, TASK_STATUS_SUSPENDED };

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
    protected  User identity;
    
    @Mock
    protected Cell.Context cellContext;
    
    @Mock
    protected Element cellParent;
    
    @Mock
    protected ActionCell.Delegate<TaskSummary> cellDelegate;

    @Mock @SuppressWarnings("unused")
    protected DataSetEditorManager dataSetEditorManagerMock;
    
    public abstract AbstractTaskListView getView();
    
    public abstract AbstractTaskListPresenter getPresenter();
    
    public abstract String getDataSetId();
    
    public abstract int getExpectedDefaultTabFilterCount();
    
    private String[] positivePotOwners = new String[]{ "otheruser1", "otheruser2", "otheruser3", TEST_USER_ID };
    private String[] negativePotOwners = new String[]{ "otheruser1", "otheruser2", "otheruser3", "otheruser4" };

    @Before
    public void setupMocks() {
        when(getPresenter().getDataProvider()).thenReturn(mock(AsyncDataProvider.class));

        when(filterPagedTableMock.getMultiGridPreferencesStore()).thenReturn(multiGridPreferencesStoreMock);
        when(currentListGrid.getGridPreferencesStore()).thenReturn(new GridPreferencesStore());
        callerMockUserPreferencesService = new CallerMock<UserPreferencesService>(userPreferencesServiceMock);
        getView().setPreferencesService(callerMockUserPreferencesService);
        
        when(identity.getIdentifier()).thenReturn(TEST_USER_ID);
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
        AbstractTaskListView view = getView();
        when(identity.getIdentifier()).thenReturn("user");
        view.setIdentity(identity);
        FilterSettings filterSettings = view.createTableSettingsPrototype();
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
        AbstractTaskListPresenter<?> presenter = getPresenter();
        getView().initDefaultFilters(new GridGlobalPreferences(), mockButton);

        verify(filterPagedTableMock, times(getExpectedDefaultTabFilterCount()))
            .addTab(any(ExtendedPagedTable.class), anyString(), any(Command.class));
        verify(filterPagedTableMock).addAddTableButton(mockButton);
        verify(presenter).setAddingDefaultFilters(true);
        verify(presenter).setAddingDefaultFilters(false);
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
        getView().resetDefaultFilterTitleAndDescription();
        int filterCount = getExpectedDefaultTabFilterCount();
        verify(filterPagedTableMock, times(filterCount)).getMultiGridPreferencesStore();
        verify(filterPagedTableMock, times(filterCount)).saveTabSettings(anyString(), any(HashMap.class));
    }

    @Test
    public void initialColumsTest(){
        AbstractTaskListView view = getView();
        view.init(getPresenter());
        List<GridColumnPreference> columnPreferences = view.getListGrid().getGridPreferencesStore().getColumnPreferences();
        assertEquals(COLUMN_NAME, columnPreferences.get(0).getName());
        assertEquals(COLUMN_PROCESS_ID, columnPreferences.get(1).getName());
        assertEquals(COLUMN_STATUS, columnPreferences.get(2).getName());
        assertEquals(COLUMN_CREATED_ON, columnPreferences.get(3).getName());
        assertEquals(AbstractTaskListView.COL_ID_ACTIONS, columnPreferences.get(4).getName());
    }
    
    @Test
    public void testDatasetName(){
        assertEquals(getDataSetId(), getView().createTableSettingsPrototype().getDataSetLookup().getDataSetUUID());
    }
    
    @Test
    public void testResumeActionHasCell(){
        AbstractTaskListView.ResumeActionHasCell resumeCell = new AbstractTaskListView.ResumeActionHasCell(identity, "", cellDelegate);

        //Status test
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_SUSPENDED);
            
            //Actual owner
            runActionHasCellTest(taskStatus, TEST_USER_ID, null, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", null, resumeCell, false);
            runActionHasCellTest(taskStatus, null, null, resumeCell, false);
            
            //Potential owners
            runActionHasCellTest(taskStatus, null, positivePotOwners, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", positivePotOwners, resumeCell, shouldRender);
            runActionHasCellTest(taskStatus, null, negativePotOwners, resumeCell, false);
            runActionHasCellTest(taskStatus, "otheruser", negativePotOwners, resumeCell, false);
        }
    }
    
    @Test
    public void testSuspendActionHasCell(){
        AbstractTaskListView.SuspendActionHasCell suspendCell = new AbstractTaskListView.SuspendActionHasCell(identity, "", cellDelegate);

        //Actual owner vs status tests
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_RESERVED || taskStatus == TASK_STATUS_INPROGRESS);
            runActionHasCellTest(taskStatus, TEST_USER_ID, null, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", null, suspendCell, false);
            runActionHasCellTest(taskStatus, null, null, suspendCell, false);
        }
        
        //Potential owners vs status tests
        for(String taskStatus : TASK_STATUS_LIST){
            boolean shouldRender = (taskStatus == TASK_STATUS_READY);
            runActionHasCellTest(taskStatus, null, positivePotOwners, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, "otheruser", positivePotOwners, suspendCell, shouldRender);
            runActionHasCellTest(taskStatus, null, negativePotOwners, suspendCell, false);
            runActionHasCellTest(taskStatus, "otheruser", negativePotOwners, suspendCell, false);
        }
    }
    
    protected void runActionHasCellTest(
            String taskStatus,
            String taskOwner,
            String potOwners[],
            ButtonActionCell<TaskSummary> cellObject,
            final boolean isRenderExpected)
    {
        TaskSummary testTask = createTestTaskSummary(taskStatus, taskOwner, potOwners);
        ButtonActionCell<TaskSummary> cellMock = spy(cellObject);
        SafeHtmlBuilder cellHtmlBuilder = mock(SafeHtmlBuilder.class);
        doAnswer( invocationOnMock -> {
            invocationOnMock.callRealMethod();
            verify(cellHtmlBuilder, times(isRenderExpected ? 1 : 0)).append(any());
            return null;
        }).when(cellMock).render(any(), any(), eq(cellHtmlBuilder));
        
        cellMock.render(cellContext, testTask, cellHtmlBuilder);
        
        verify(cellMock).render(cellContext, testTask, cellHtmlBuilder);
    }
    
    protected TaskSummary createTestTaskSummary(String status, String actualOwner, String potOwners[]){
        return new TaskSummary(10L, "Test task name", "Test task description", status, 1, actualOwner,
                "TestCreator", new Date(), new Date(), new Date(new Date().getTime() + (60*60*1000)),
                null, 0, 0, null, 0, new Date(), null, null, false,
                potOwners == null ? new ArrayList<String>() : Arrays.asList(potOwners)
        );
    }

}
