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
package org.jbpm.workbench.es.client.editors.requestlist;

import java.util.Date;
import java.util.List;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.jbpm.workbench.common.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.base.events.SearchEvent;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RequestListPresenterTest {

    private static final Long REQUESTID_ID = 1L;

    private CallerMock<ExecutorService> callerMockExecutorService;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private RequestListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private ExtendedPagedTable<RequestSummary> extendedPagedTable;

    @Mock
    private EventSourceMock<RequestChangedEvent> requestChangedEvent;

    private FilterSettings filterSettings;

    private RequestListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockExecutorService = new CallerMock<ExecutorService>(executorServiceMock);

        filterSettings = createTableSettingsPrototype();

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);


        presenter = new RequestListPresenter(viewMock,
                callerMockExecutorService, dataSetQueryHelper, requestChangedEvent);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(ASCENDING);
        verify(viewMock).hideBusyIndicator();
    }

    @Test
    public void cancelRequestTest() {
        presenter.cancelRequest(REQUESTID_ID);

        verify(requestChangedEvent, times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).cancelRequest(anyString(), eq(REQUESTID_ID));
    }

    @Test
    public void requeueRequestTest() {
        presenter.requeueRequest(REQUESTID_ID);

        verify(requestChangedEvent, times(1)).fire(any(RequestChangedEvent.class));
        verify(executorServiceMock).requeueRequest(anyString(), eq(REQUESTID_ID));
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
    public void testSearchFilterNull() {
        final List<ColumnFilter> filters = presenter.getColumnFilters(null);

        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterEmpty() {
        List<ColumnFilter> filters;

        filters = presenter.getColumnFilters("");
        assertTrue(filters.isEmpty());

        filters = presenter.getColumnFilters("     ");
        assertTrue(filters.isEmpty());
    }

    @Test
    public void testSearchFilterString() {
        final List<ColumnFilter> filters = presenter.getColumnFilters("jobReference");

        assertEquals(3, filters.size());
        assertEquals(COLUMN_COMMANDNAME, filters.get(0).getColumnId());
        assertEquals(COLUMN_MESSAGE, filters.get(1).getColumnId());
        assertEquals(COLUMN_BUSINESSKEY, filters.get(2).getColumnId());
    }

    @Test
    public void testGetRequestSummary() {
        final Long id = 1l;
        final String message = "message";
        final String status = "status";
        final String commandName = "commandName";
        final String businessKey = "businessKey";
        final Integer retries = 2;
        final Integer executions = 1;
        final Date time = new Date();

        when(dataSetQueryHelper.getColumnLongValue(any(DataSet.class), eq(COLUMN_ID), eq(0))).thenReturn(id);
        when(dataSetQueryHelper.getColumnDateValue(any(DataSet.class), eq(COLUMN_TIMESTAMP), eq(0))).thenReturn(time);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class), eq(COLUMN_STATUS), eq(0))).thenReturn(status);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class), eq(COLUMN_COMMANDNAME), eq(0))).thenReturn(commandName);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class), eq(COLUMN_MESSAGE), eq(0))).thenReturn(message);
        when(dataSetQueryHelper.getColumnStringValue(any(DataSet.class), eq(COLUMN_BUSINESSKEY), eq(0))).thenReturn(businessKey);
        when(dataSetQueryHelper.getColumnIntValue(any(DataSet.class), eq(COLUMN_RETRIES), eq(0))).thenReturn(retries);
        when(dataSetQueryHelper.getColumnIntValue(any(DataSet.class), eq(COLUMN_EXECUTIONS), eq(0))).thenReturn(executions);

        final RequestSummary rs = presenter.getRequestSummary(mock(DataSet.class), 0);

        assertEquals(id, rs.getId());
        assertEquals(time, rs.getTime());
        assertEquals(status, rs.getStatus());
        assertEquals(commandName, rs.getCommandName());
        assertEquals(message, rs.getMessage());
        assertEquals(businessKey, rs.getKey());
        assertEquals(retries, rs.getRetries());
        assertEquals(executions, rs.getExecutions());
    }

    private FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(REQUEST_LIST_DATASET);
        builder.setColumn(COLUMN_ID, "id");
        builder.setColumn(COLUMN_TIMESTAMP, "time", DateUtils.getDateTimeFormatMask());
        builder.setColumn(COLUMN_STATUS, "status");
        builder.setColumn(COLUMN_COMMANDNAME, "commandName");
        builder.setColumn(COLUMN_MESSAGE, "status");
        builder.setColumn(COLUMN_BUSINESSKEY, "key");
        builder.setColumn(COLUMN_RETRIES, "retries");
        builder.setColumn(COLUMN_EXECUTIONS, "executions");

        builder.filterOn(true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_TIMESTAMP, DESCENDING);
        builder.tableWidth(1000);

        return builder.buildSettings();
    }

}