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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.jbpm.process.instance.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.pr.model.ProcessInstanceDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetProcessInstanceWithVariablesListPresenterTest {

    private CallerMock<ProcessInstanceService> callerMockProcessInstanceService;

    @Mock
    private ProcessInstanceService processInstanceServiceMock;

    private CallerMock<KieSessionEntryPoint> callerMockKieSessionServices;

    @Mock
    private KieSessionEntryPoint kieSessionEntryPointMock;

    @Mock
    private DataSetProcessInstanceWithVariablesListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private DataSet dataSet;

    @Mock
    private DataSet dataSetProcessVar;

    @Mock
    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Mock
    private ExtendedPagedTable extendedPagedTable;

    @Mock
    protected PlaceManager placeManager;

    @Mock
    private FilterSettings filterSettings;

    @Mock
    private FilterSettings variablesTableSettings;

    private ArrayList<ProcessInstanceSummary> processInstanceSummaries;
    //Thing under test
    private DataSetProcessInstanceWithVariablesListPresenter presenter;


    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockKieSessionServices = new CallerMock<KieSessionEntryPoint>(kieSessionEntryPointMock);
        callerMockProcessInstanceService = new CallerMock<ProcessInstanceService>(processInstanceServiceMock);

        processInstanceSummaries = createProcessInstanceSummaryList(5);

        when(filterSettings.getDataSetLookup()).thenReturn(new DataSetLookup());
        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(viewMock.getVariablesTableSettings("testProc")).thenReturn(filterSettings);

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSetProcessVar);
                return null;
            }
        }).when(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));

        presenter = new DataSetProcessInstanceWithVariablesListPresenter(viewMock, callerMockProcessInstanceService, callerMockKieSessionServices,
                dataSetQueryHelper, dataSetQueryHelperDomainSpecific, placeManager);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        //verify(viewMock).hideBusyIndicator();
    }

    @Test
    public void isFilteredByProcessIdTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID, processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertEquals(processId, filterProcessId);
    }

    @Test
    public void isFilteredByProcessIdInvalidTest() {
        final String processId = "testProc";
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(likeTo(COLUMN_PROCESS_ID, processId));

        final String filterProcessId = presenter.isFilteredByProcessId(Collections.<DataSetOp>singletonList(filter));
        assertNull(filterProcessId);
    }

    @Test
    public void abortProcessInstanceTest() {
        final Long processInstanceId = new Random().nextLong();

        presenter.abortProcessInstance(processInstanceId);

        verify(kieSessionEntryPointMock).abortProcessInstance(processInstanceId);
    }

    @Test
    public void abortProcessInstancesTest() {
        final Random random = new Random();
        final List<Long> pIds = new ArrayList<Long>();
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());

        presenter.abortProcessInstance(pIds);

        verify(kieSessionEntryPointMock).abortProcessInstances(pIds);
    }

    @Test
    public void bulkAbortProcessInstancesTest() {
        final List<Long> pIds = new ArrayList<Long>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            pIds.add(summary.getProcessInstanceId());
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(kieSessionEntryPointMock).abortProcessInstances(pIds);
    }

    @Test
    public void bulkAbortProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextInt(), ProcessInstance.STATE_ABORTED));
        final List<Long> pIds = new ArrayList<Long>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                pIds.add(summary.getProcessInstanceId());
            }
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(kieSessionEntryPointMock).abortProcessInstances(pIds);
    }

    @Test
    public void bulkSignalProcessInstanceSingleAbortedTest() {
        ArrayList<ProcessInstanceSummary> processInstanceSummaries = new ArrayList<ProcessInstanceSummary>();
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextInt(), ProcessInstance.STATE_ABORTED));

        presenter.bulkSignal(processInstanceSummaries);

        verify(placeManager, never()).goTo(any(PlaceRequest.class));
    }

    @Test
    public void bulkSignalProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextInt(), ProcessInstance.STATE_ABORTED));
        final List<Long> pIds = new ArrayList<Long>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                pIds.add(summary.getProcessInstanceId());
            }
        }

        presenter.bulkSignal(processInstanceSummaries);

        final ArgumentCaptor<PlaceRequest> placeRequest = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(placeRequest.capture());

        assertEquals(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP, placeRequest.getValue().getIdentifier());
        assertEquals(StringUtils.join(pIds, ","), placeRequest.getValue().getParameter("processInstanceId", null));
    }

    @Test
    public void getDomainSpecifDataForProcessInstancesTest() {
        presenter.setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID, "testProc"));
        filterSettings.getDataSetLookup().addOperation(filter);

        when(dataSet.getRowCount()).thenReturn(1);//1 process instance
        when(dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_PROCESS_INSTANCE_ID, 0)).thenReturn(Long.valueOf(1));

        when(dataSetProcessVar.getRowCount()).thenReturn(2); //two domain variables associated
        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetProcessVar, PROCESS_INSTANCE_ID, 0)).thenReturn(Long.valueOf(1));
        String processVariable1 = "var1";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_NAME, 0)).thenReturn(processVariable1);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_VALUE, 0)).thenReturn("value1");

        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetProcessVar, PROCESS_INSTANCE_ID, 1)).thenReturn(Long.valueOf(1));
        String processVariable2 = "var2";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_NAME, 1)).thenReturn(processVariable2);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_VALUE, 1)).thenReturn("value2");

        Set<String> expectedColumns = new HashSet<String>();
        expectedColumns.add(processVariable1);
        expectedColumns.add(processVariable2);

        presenter.getData(new Range(0, 5));

        ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());

        verify(dataSetQueryHelper).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific).setLastOrderedColumn(PROCESS_INSTANCE_ID);
        verify(dataSetQueryHelperDomainSpecific).setLastSortOrder(SortOrder.ASCENDING);

        when(dataSetProcessVar.getRowCount()).thenReturn(1); //one domain variables associated
        when(dataSetQueryHelperDomainSpecific.getColumnLongValue(dataSetProcessVar, PROCESS_INSTANCE_ID, 0)).thenReturn(Long.valueOf(1));
        processVariable1 = "varTest1";
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_NAME, 0)).thenReturn(processVariable1);
        when(dataSetQueryHelperDomainSpecific.getColumnStringValue(dataSetProcessVar, VARIABLE_VALUE, 0)).thenReturn("value1");

        expectedColumns = Collections.singleton(processVariable1);

        presenter.getData(new Range(0, 5));

        argument = ArgumentCaptor.forClass(Set.class);
        verify(viewMock, times(2)).addDomainSpecifColumns(any(ExtendedPagedTable.class), argument.capture());

        assertEquals(expectedColumns, argument.getValue());
        verify(dataSetQueryHelper, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
        verify(dataSetQueryHelperDomainSpecific, times(2)).lookupDataSet(anyInt(), any(DataSetReadyCallback.class));
    }

    public ArrayList<ProcessInstanceSummary> createProcessInstanceSummaryList(int listSize) {
        ArrayList<ProcessInstanceSummary> pIList = new ArrayList<ProcessInstanceSummary>();
        for (int i = 1; i <= listSize; i++) {
            pIList.add(createProcessInstanceSummary(i));
        }
        return pIList;
    }


    public static ProcessInstanceSummary createProcessInstanceSummary(int key) {
        return createProcessInstanceSummary(key, ProcessInstance.STATE_ACTIVE);
    }

    public static ProcessInstanceSummary createProcessInstanceSummary(int key, int status) {
        return new ProcessInstanceSummary(key, "procTest", "test.0.1", "Test Proc", "1.0",
                status, new Date(), "intiatior", "procTestInstanceDesc", "cKey", Long.valueOf(0));
    }

    @Test
    public void testEmptySearchString() {
        final SearchEvent searchEvent = new SearchEvent("");

        presenter.onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), presenter.getTextSearchStr());
    }

    @Test
    public void testSearchString() {
        final SearchEvent searchEvent = new SearchEvent(RandomStringUtils.random(10));

        presenter.onSearchEvent(searchEvent);

        verify(viewMock).applyFilterOnPresenter(anyString());
        assertEquals(searchEvent.getFilter(), presenter.getTextSearchStr());
    }

}