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
package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetProcessInstanceWithVariablesListPresenterTest {
    private static final Long PROCESSINSTANCE_ID = 1L;

    @Inject
    private CallerMock<ProcessInstanceService> callerMockProcessInstanceService;

    @Mock
    private ProcessInstanceService processInstanceServiceMock;


    @Inject
    private CallerMock<KieSessionEntryPoint> callerMockKieSessionServices;

    @Mock
    private KieSessionEntryPoint kieSessionEntryPointMock;


    @Mock
    private DataSetProcessInstanceWithVariablesListViewImpl viewMock;

    @Mock
    DataSetQueryHelper dataSetQueryHelper;

    @Mock
    DataSet dataSet;

    @Mock
    DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Mock
    private ExtendedPagedTable extendedPagedTable;

    private FilterSettings filterSettings;
    private FilterSettings variablesTableSettings;
    private ArrayList<ProcessInstanceSummary> processInstanceSummaries;
    //Thing under test
    private DataSetProcessInstanceWithVariablesListPresenter presenter;


    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockKieSessionServices = new CallerMock<KieSessionEntryPoint>(kieSessionEntryPointMock);
        callerMockProcessInstanceService = new CallerMock<ProcessInstanceService>(processInstanceServiceMock);

        filterSettings= createTableSettingsPrototype(null);

        processInstanceSummaries = createProcessInstanceSummaryList(5);


        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(viewMock.getVariablesTableSettings("testProc")).thenReturn(filterSettings);

        variablesTableSettings = createTableSettingsPrototype(null);
        variablesTableSettings.setTablePageSize(-1);

        dataSetQueryHelperDomainSpecific.setDataSetHandler(variablesTableSettings);
        //dataSetQueryHelper.setCurrentTableSettings(createTableSettingsPrototype());
        presenter = new DataSetProcessInstanceWithVariablesListPresenter(viewMock,callerMockProcessInstanceService,callerMockKieSessionServices,
                dataSetQueryHelper,dataSetQueryHelperDomainSpecific);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        //verify(viewMock).hideBusyIndicator();
    }

    @Test
    public void isFilteredByProcessIdTest(){
        filterSettings = createTableSettingsPrototype("testProc");
        String processId =presenter.isFilteredByProcessId(filterSettings.getDataSetLookup().getOperationList());
        assertEquals(processId,"testProc");

        filterSettings = createTableSettingsPrototype(null);
        processId =presenter.isFilteredByProcessId(filterSettings.getDataSetLookup().getOperationList());
        assertNotEquals(processId, "testProc");

    }

    @Test
    public void getDomainSpecifDataForProcessInstancesTest(){

        presenter.getDomainSpecifDataForProcessInstances(0, dataSet, "testProc", processInstanceSummaries);

        verify(dataSetQueryHelperDomainSpecific).setLastOrderedColumn(DataSetProcessInstanceWithVariablesListViewImpl.PROCESS_INSTANCE_ID);
        verify(dataSetQueryHelperDomainSpecific).setLastSortOrder(SortOrder.ASCENDING);

    }

    @Test
    public void abortProcessInstanceTest() {

        presenter.abortProcessInstance(PROCESSINSTANCE_ID);
        verify(kieSessionEntryPointMock).abortProcessInstance(PROCESSINSTANCE_ID);

        ArrayList<Long> pIds= new ArrayList<Long>();
        pIds.add(new Long(2));
        pIds.add(new Long(3));

        presenter.abortProcessInstance(pIds);
        verify(kieSessionEntryPointMock).abortProcessInstances(pIds);

        presenter.bulkAbort(processInstanceSummaries);
        verify(kieSessionEntryPointMock).abortProcessInstance(anyLong());

    }

    public FilterSettings createTableSettingsPrototype(String processId) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        if(processId!=null) {
            builder.filter(DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSID, equalsTo(processId));
        }

        builder.dataset( DataSetProcessInstanceWithVariablesListViewImpl.PROCESS_INSTANCES_DATASET_ID );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSINSTANCEID, "processInstanceId" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSID, "processId" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_START, "start", "MMM dd E, yyyy" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_END, "end", "MMM dd E, yyyy" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_STATUS, "status" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PARENTPROCESSINSTANCEID, "parentProcessInstanceId" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_OUTCOME, "outcome" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_DURATION, "duration" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_IDENTITY, "identity" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSVERSION, "processVersion" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSNAME, "processName" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_CORRELATIONKEY, "CorrelationKey" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_EXTERNALID, "externalId" );
        builder.setColumn( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION, "processInstanceDescription" );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_START, DESCENDING );
        builder.tableWidth( 1000 );

        return builder.buildSettings();
    }

    public ArrayList<ProcessInstanceSummary> createProcessInstanceSummaryList(int listSize){
        ArrayList<ProcessInstanceSummary> pIList=new ArrayList<ProcessInstanceSummary>();
        for(int i=1; i <= listSize; i++ ) {
            pIList.add(createProcessInstanceSummary(i));
        }
        return pIList;
    }


    public ProcessInstanceSummary createProcessInstanceSummary(int key){
        return new ProcessInstanceSummary(key, "procTest", "test.0.1", "Test Proc", "1.0",
        1, new Date(), "intiatior", "procTestInstanceDesc", "cKey", Long.valueOf(0));
    }


}
