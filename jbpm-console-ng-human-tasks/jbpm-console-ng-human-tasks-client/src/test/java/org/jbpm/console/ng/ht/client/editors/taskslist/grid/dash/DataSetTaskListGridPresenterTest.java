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

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.sort.SortOrder;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.junit.Assert.*;
import org.uberfire.mocks.CallerMock;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetTaskListGridPresenterTest {
    private static final Long TASK_ID = 1L;
    private static final String USR_ID = "admin";


    private CallerMock<TaskLifeCycleService> callerMockTaskOperationsService;

    @Mock
    private TaskLifeCycleService taskLifeCycleServiceMock;

    @Mock
    private DataSetTasksListGridViewImpl viewMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelper;

    @Mock
    public User identity;

    @Mock
    private ExtendedPagedTable<TaskSummary> extendedPagedTable;

    @Mock
    private FilterSettings filterSettings;

    //Thing under test
    private DataSetTasksListGridPresenter presenter;

    @Before
    public void setupMocks() {

        //Mock that actually calls the callbacks
        callerMockTaskOperationsService = new CallerMock<TaskLifeCycleService>(taskLifeCycleServiceMock);

        DataSetLookup dataSetLookup= new DataSetLookup();


        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(filterSettings.getDataSetLookup()).thenReturn(dataSetLookup);

        //dataSetQueryHelper.setCurrentTableSettings(createTableSettingsPrototype());
        presenter = new DataSetTasksListGridPresenter(viewMock, callerMockTaskOperationsService,
                dataSetQueryHelper, identity );

    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
    }

    @Test
    public void releaseTaskTest() {
        presenter.releaseTask(TASK_ID, USR_ID);

        verify(taskLifeCycleServiceMock).release(TASK_ID, USR_ID);
   }

    @Test
    public void claimTaskTest() {
        presenter.claimTask(TASK_ID, USR_ID, "deploymentId");

        verify(taskLifeCycleServiceMock).claim(TASK_ID, USR_ID, "deploymentId");

    }



    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_USERS_DATASET);

        //Set<Group> groups = identity.getGroups();
        List<ColumnFilter> condList = new ArrayList<ColumnFilter>();
        //for(Group g : groups){
        //    condList.add( FilterFactory.equalsTo(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, g.getName()));
        // }
        ColumnFilter myGroupFilter = FilterFactory.AND( FilterFactory.OR( condList ), FilterFactory.equalsTo( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, "" ) );

        builder.filter(OR(myGroupFilter, FilterFactory.equalsTo(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, "admin")));

        builder.group(DataSetTasksListGridViewImpl.COLUMN_TASKID);

        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME, "Activation Time", "MMM dd E, yyyy" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, "actual owner");
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_CREATEDBY,"CreatedBy" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_CREATEDON , "Created on", "MMM dd E, yyyy" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID, "DeploymentId" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION, "description" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_DUEDATE, "Due Date", "MMM dd E, yyyy" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_NAME, "tasks" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_PARENTID,  "ParentId");
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_PRIORITY, "Priority" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_PROCESSID, "ProcessId" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID, "ProcessInstanceId" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID, "ProcessSesionId" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_STATUS, "status" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_TASKID, "id" );
        builder.setColumn( DataSetTasksListGridViewImpl.COLUMN_WORKITEMID, "WorkItemId" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( DataSetTasksListGridViewImpl.COLUMN_CREATEDON, DESCENDING );
        builder.tableWidth(1000);


        return  builder.buildSettings();

    }

    @Test
    public void testGetUserGroupFilters() {
        Group group1 = new Group() {
            @Override
            public String getName() {
                return "group1";
            }
        };
        Group group2 = new Group() {
            @Override
            public String getName() {
                return "group2";
            }
        };
        HashSet<Group> groups = new HashSet<Group>();
        groups.add(group1);
        groups.add(group2);
        when(identity.getGroups()).thenReturn(groups);
        when(identity.getIdentifier()).thenReturn("userId");

        final ColumnFilter userTaskFilter = presenter.getUserGroupFilters();
        //(((id = group2 OR id = group1 OR id = userId) AND (actualOwner =  OR actualOwner is_null )) OR actualOwner = userId)

        List<ColumnFilter> columnFilters = ((LogicalExprFilter) userTaskFilter).getLogicalTerms();

        assertEquals(columnFilters.size(), 2);
        assertEquals(((LogicalExprFilter) userTaskFilter).getLogicalOperator(), LogicalExprType.OR); //userOwnerTask or userGroupTask

        //userGroupTask
        // ((id = group2 OR id = group1 OR id = userId) AND (actualOwner =  OR actualOwner is_null ))
        assertEquals(((LogicalExprFilter) columnFilters.get(0)).getLogicalOperator(), LogicalExprType.AND);
        List<ColumnFilter> userGroupFilter = ((LogicalExprFilter) columnFilters.get(0)).getLogicalTerms();
        assertEquals(userGroupFilter.size(), 2);
        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(), LogicalExprType.OR);

        List<ColumnFilter> groupFilter = ((LogicalExprFilter) userGroupFilter.get(0)).getLogicalTerms();
        List<ColumnFilter> withoutActualOwnerFilter = ((LogicalExprFilter) userGroupFilter.get(1)).getLogicalTerms();

        assertEquals(((LogicalExprFilter) userGroupFilter.get(1)).getLogicalOperator(), LogicalExprType.OR);
        assertEquals(withoutActualOwnerFilter.size(), 2); //actual_owner empty or null
        assertEquals(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, withoutActualOwnerFilter.get(0).getColumnId());
        assertEquals(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER, withoutActualOwnerFilter.get(1).getColumnId());

        assertEquals(((LogicalExprFilter) userGroupFilter.get(0)).getLogicalOperator(), LogicalExprType.OR);
        assertEquals(groupFilter.size(), 3); //(id = group2 OR id = group1 OR id = userId)
        assertEquals(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(0).getColumnId());
        assertEquals(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(1).getColumnId());
        assertEquals(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY, groupFilter.get(2).getColumnId());

        //userOwnerTask
        // (actualOwner = userId)
        ColumnFilter userOwnerFilter = columnFilters.get(1);//actualOwner = userId
        assertEquals(userOwnerFilter.getColumnId(),DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER);
    }

    @Test
    public void addDynamicUserRolesTest() {
        // Test with one group to avoid problems in test the the set of groups.
        Group group1 = new Group() {
            @Override
            public String getName() {
                return "group1";
            }
        };

        HashSet<Group> groups = new HashSet<Group>();
        groups.add(group1);
        when(identity.getGroups()).thenReturn(groups);
        when(identity.getIdentifier()).thenReturn("userId");
        presenter.setAddingDefaultFilters(false);
        filterSettings.getDataSetLookup().setDataSetUUID(DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_USERS_DATASET);

        //when(dataSetMock.getRowCount()).thenReturn(1);//1 task
        //Task summary creation
        //when(dataSetQueryHelperMock.getColumnLongValue(dataSetMock, COLUMN_TASK_ID, 0)).thenReturn(Long.valueOf(1));

        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        presenter.getData(new Range(0, 5));

        final ColumnFilter userTaskFilter = presenter.getUserGroupFilters();
        // Check the datasetLookup applied include the addition of the user groups restrictions

        assertEquals(filterSettings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().get(0).toString(),
                userTaskFilter.toString());

    }

}
