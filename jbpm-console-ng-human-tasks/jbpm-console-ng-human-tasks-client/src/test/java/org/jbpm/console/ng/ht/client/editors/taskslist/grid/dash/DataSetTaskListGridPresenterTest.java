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
import java.util.Arrays;
import java.util.List;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;

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
import org.uberfire.mocks.CallerMock;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
    private DataSet dataSetMock;

    @Mock
    private DataSetLookup dataSetLookupMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private ExtendedPagedTable<TaskSummary> extendedPagedTable;

    @Mock
    private FilterSettings filterSettingsMock;

    private FilterSettings filterSettings;

    //Thing under test
    private DataSetTasksListGridPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockTaskOperationsService = new CallerMock<TaskLifeCycleService>(taskLifeCycleServiceMock);
        filterSettings= createTableSettingsPrototype();

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);

        //dataSetQueryHelper.setCurrentTableSettings(createTableSettingsPrototype());
        presenter = new DataSetTasksListGridPresenter(viewMock, callerMockTaskOperationsService,dataSetQueryHelper);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(viewMock).hideBusyIndicator();
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
    public void testTaskSummaryAdmin() {
        final List<String> dataSets = Arrays.asList(
                DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_ADMINS_DATASET,
                DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_USERS_DATASET,
                DataSetTasksListGridViewImpl.HUMAN_TASKS_DATASET);

        for (final String dataSet : dataSets) {
            when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettingsMock);
            when(filterSettingsMock.getDataSetLookup()).thenReturn(dataSetLookupMock);
            when(dataSetLookupMock.getDataSetUUID()).thenReturn(dataSet);

            final TaskSummary summary = presenter.createTaskSummaryFromDataSet(dataSetMock, 0);

            assertNotNull(summary);
            assertEquals(DataSetTasksListGridViewImpl.HUMAN_TASKS_WITH_ADMINS_DATASET.equals(dataSet), summary.isForAdmin());
        }
    }

}
