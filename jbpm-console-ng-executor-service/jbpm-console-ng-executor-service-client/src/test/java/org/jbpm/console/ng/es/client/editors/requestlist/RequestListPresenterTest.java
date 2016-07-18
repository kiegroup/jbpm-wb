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
package org.jbpm.console.ng.es.client.editors.requestlist;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.apache.commons.lang3.RandomStringUtils;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorService;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.jbpm.console.ng.es.model.RequestDataSetConstants.*;

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
    private ErrorPopupPresenter errorPopup;

    @Mock
    private EventSourceMock<RequestChangedEvent> requestChangedEvent;

    private FilterSettings filterSettings;

    private RequestListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockExecutorService = new CallerMock<ExecutorService>(executorServiceMock);

        filterSettings= createTableSettingsPrototype();

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);


        presenter = new RequestListPresenter(viewMock,
                callerMockExecutorService,dataSetQueryHelper,requestChangedEvent);
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));

        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
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

    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( REQUEST_LIST_DATASET );
        builder.setColumn( COLUMN_ID, "id" );
        builder.setColumn( COLUMN_TIMESTAMP, "time" , DateUtils.getDateTimeFormatMask());
        builder.setColumn( COLUMN_STATUS,"status" );
        builder.setColumn( COLUMN_COMMANDNAME , "commandName" );
        builder.setColumn( COLUMN_MESSAGE, "status" );
        builder.setColumn( COLUMN_BUSINESSKEY, "key" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( COLUMN_TIMESTAMP, DESCENDING );
        builder.tableWidth(1000);

        return  builder.buildSettings();
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