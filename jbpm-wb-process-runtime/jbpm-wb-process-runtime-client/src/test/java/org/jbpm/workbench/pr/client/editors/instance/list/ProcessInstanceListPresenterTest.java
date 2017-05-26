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
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.*;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.events.SearchEvent;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.jbpm.workbench.common.client.list.AbstractMultiGridView.TAB_SEARCH;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListPresenterTest {

    private CallerMock<ProcessService> remoteProcessServiceCaller;

    @Mock
    private ProcessService processService;

    @Mock
    private ProcessInstanceListViewImpl viewMock;

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

    @Spy
    private FilterSettings filterSettings;

    @Mock
    private DataSetEditorManager dataSetEditorManager;

    @Spy
    private DataSetLookup dataSetLookup;

    private ArrayList<ProcessInstanceSummary> processInstanceSummaries;

    @InjectMocks
    private ProcessInstanceListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        remoteProcessServiceCaller = new CallerMock<ProcessService>(processService);

        processInstanceSummaries = createProcessInstanceSummaryList(5);

        when(filterSettings.getDataSetLookup()).thenReturn(dataSetLookup);
        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(dataSetEditorManager.getStrToTableSettings(anyString())).thenReturn(filterSettings);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);
        when(viewMock.getAdvancedSearchFilterSettings()).thenReturn(filterSettings);
        when(filterSettings.getKey()).thenReturn("key");

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

        presenter.setProcessService(remoteProcessServiceCaller);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(viewMock, times(2)).hideBusyIndicator();
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
        final String containerId = "container";

        presenter.abortProcessInstance(containerId, processInstanceId);

        verify(processService).abortProcessInstance(anyString(), eq(containerId), eq(processInstanceId));
    }

    @Test
    public void abortProcessInstancesTest() {
        final Random random = new Random();
        final List<String> containers = new ArrayList<String>();
        containers.add("container");

        final List<Long> pIds = new ArrayList<Long>();
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());
        pIds.add(random.nextLong());

        presenter.abortProcessInstance(containers, pIds);

        verify(processService).abortProcessInstances(anyString(), eq(containers), eq(pIds));
    }

    @Test
    public void bulkAbortProcessInstancesTest() {
        final List<Long> pIds = new ArrayList<Long>();
        final List<String> containers = new ArrayList<String>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            pIds.add(summary.getProcessInstanceId());
            containers.add(summary.getDeploymentId());
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(), eq(containers), eq(pIds));
    }

    @Test
    public void bulkAbortProcessInstancesStateTest() {
        processInstanceSummaries.add(createProcessInstanceSummary(new Random().nextInt(), ProcessInstance.STATE_ABORTED));
        final List<Long> pIds = new ArrayList<Long>();
        final List<String> containers = new ArrayList<String>();
        for (ProcessInstanceSummary summary : processInstanceSummaries) {
            if (summary.getState() == ProcessInstance.STATE_ACTIVE) {
                pIds.add(summary.getProcessInstanceId());
                containers.add(summary.getDeploymentId());
            }
        }

        presenter.bulkAbort(processInstanceSummaries);

        verify(processService).abortProcessInstances(anyString(), eq(containers), eq(pIds));
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
    public void testSkipDomainSpecificColumnsForSearchTab() {
        presenter.setAddingDefaultFilters(false);
        final DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(equalsTo(COLUMN_PROCESS_ID, "testProc"));
        filterSettings.getDataSetLookup().addOperation(filter);
        filterSettings.setKey(TAB_SEARCH);
        when(filterSettings.getKey()).thenReturn(TAB_SEARCH);

        when(dataSet.getRowCount()).thenReturn(1);//1 process instance
        when(dataSetQueryHelper.getColumnLongValue(dataSet, COLUMN_PROCESS_INSTANCE_ID, 0)).thenReturn(Long.valueOf(1));

        presenter.getData(new Range(0, 5));

        verifyZeroInteractions(dataSetQueryHelperDomainSpecific);
        verify(viewMock, times(2)).hideBusyIndicator();
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
                status, new Date(), new Date(), "intiatior", "procTestInstanceDesc", "cKey",
                Long.valueOf(0), new Date(), 0);
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

    @Test
    public void testSearchFilterEmpty() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterNull() {
        final List<ColumnFilter> filters = presenter.getColumnFilters(null);

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterEmptyTrim() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("     ");

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterId() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("1");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_PROCESS_INSTANCE_ID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterIdTrim() {
        final List<ColumnFilter> filters = presenter.getColumnFilters(" 1 ");

        assertEquals(1, filters.size());
        assertEquals(COLUMN_PROCESS_INSTANCE_ID, filters.get(0).getColumnId());
    }

    @Test
    public void testSearchFilterString() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("processName");

        assertEquals(4, filters.size());
        assertEquals(COLUMN_PROCESS_ID, filters.get(0).getColumnId());
        assertEquals(COLUMN_PROCESS_NAME, filters.get(1).getColumnId());
        assertEquals(COLUMN_PROCESS_INSTANCE_DESCRIPTION, filters.get(2).getColumnId());
        assertEquals(COLUMN_IDENTITY, filters.get(3).getColumnId());
    }
    
    @Test
    public void testDataSetQueryHelperColumnMapping() {
        final Long TEST_PROC_INST_ID = Long.valueOf(55);
        final String TEST_PROC_ID = "TEST_PROC_ID";
        final String TEST_EXT_ID = "TEST_EXT_ID";
        final String TEST_PROC_NAME = "TEST_PROC_NAME";
        final String TEST_PROC_VER = "TEST_PROC_VER";
        final int TEST_STATE = 7;
        final Date TEST_START_DATE = new Date(new Date().getTime() - (2*60*60*1000));
        final Date TEST_END_DATE = new Date(new Date().getTime() + (2*60*60*1000));
        final String TEST_IDENTITY = "TEST_IDENTITY";
        final String TEST_INST_DESC = "TEST_INST_DESC";
        final String TEST_CORREL_KEY = "TEST_CORREL_KEY";
        final Long TEST_PARENT_PROC_INST_ID = Long.valueOf(66);
        final Date TEST_LAST_MODIF_DATE = new Date();
        final int TEST_ERROR_COUNT = 66;

        when(dataSetQueryHelper.getColumnLongValue(dataSetProcessVar, COLUMN_PROCESS_INSTANCE_ID, 0)).thenReturn(TEST_PROC_INST_ID);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_PROCESS_ID, 0)).thenReturn(TEST_PROC_ID);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_EXTERNAL_ID, 0)).thenReturn(TEST_EXT_ID);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_PROCESS_NAME, 0)).thenReturn(TEST_PROC_NAME);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_PROCESS_VERSION, 0)).thenReturn(TEST_PROC_VER);
        when(dataSetQueryHelper.getColumnIntValue(dataSetProcessVar, COLUMN_STATUS, 0)).thenReturn(TEST_STATE);
        when(dataSetQueryHelper.getColumnDateValue(dataSetProcessVar, COLUMN_START, 0)).thenReturn(TEST_START_DATE);
        when(dataSetQueryHelper.getColumnDateValue(dataSetProcessVar, COLUMN_END, 0)).thenReturn(TEST_END_DATE);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_IDENTITY, 0)).thenReturn(TEST_IDENTITY);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_PROCESS_INSTANCE_DESCRIPTION, 0)).thenReturn(TEST_INST_DESC);
        when(dataSetQueryHelper.getColumnStringValue(dataSetProcessVar, COLUMN_CORRELATION_KEY, 0)).thenReturn(TEST_CORREL_KEY);
        when(dataSetQueryHelper.getColumnLongValue(dataSetProcessVar, COLUMN_PARENT_PROCESS_INSTANCE_ID, 0)).thenReturn(TEST_PARENT_PROC_INST_ID);
        when(dataSetQueryHelper.getColumnDateValue(dataSetProcessVar, COLUMN_LAST_MODIFICATION_DATE, 0)).thenReturn(TEST_LAST_MODIF_DATE);
        when(dataSetQueryHelper.getColumnIntValue(dataSetProcessVar, COLUMN_ERROR_COUNT, 0)).thenReturn(TEST_ERROR_COUNT);

        ProcessInstanceSummary pis = presenter.createProcessInstanceSummaryFromDataSet(dataSetProcessVar, 0);

        assertEquals(TEST_PROC_INST_ID, pis.getProcessInstanceId());
        assertEquals(TEST_PROC_ID, pis.getProcessId());
        assertEquals(TEST_EXT_ID, pis.getDeploymentId());
        assertEquals(TEST_PROC_NAME, pis.getProcessName());
        assertEquals(TEST_PROC_VER, pis.getProcessVersion());
        assertEquals(TEST_STATE, pis.getState());
        assertEquals(TEST_START_DATE, pis.getStartTime());
        assertEquals(TEST_END_DATE, pis.getEndTime());
        assertEquals(TEST_IDENTITY, pis.getInitiator());
        assertEquals(TEST_INST_DESC, pis.getProcessInstanceDescription());
        assertEquals(TEST_CORREL_KEY, pis.getCorrelationKey());
        assertEquals(TEST_PARENT_PROC_INST_ID, pis.getParentId());
        assertEquals(TEST_LAST_MODIF_DATE, pis.getLastModificationDate());
        assertEquals(TEST_ERROR_COUNT, pis.getErrorCount());
    }

    @Test
    public void testAdvancedSearchDefaultActiveFilter(){
        presenter.setupAdvancedSearchView();

        verify(viewMock).addActiveFilter(eq(Constants.INSTANCE.State()),
                                         eq(Constants.INSTANCE.Active()),
                                         eq(String.valueOf(ProcessInstance.STATE_ACTIVE)),
                                         any(ParameterizedCommand.class));
    }

}