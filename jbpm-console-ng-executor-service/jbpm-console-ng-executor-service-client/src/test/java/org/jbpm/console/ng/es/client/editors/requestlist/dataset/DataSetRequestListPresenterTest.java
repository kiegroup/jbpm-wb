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
package org.jbpm.console.ng.es.client.editors.requestlist.dataset;


import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetRequestListPresenterTest {

    private CallerMock<ExecutorServiceEntryPoint> callerMockExecutorService;

    @Mock
    private ExecutorServiceEntryPoint executorServiceMock;


    @Mock
    private DataSetRequestListViewImpl viewMock;

    @Mock
    private DataSetQueryHelper dataSetQueryHelper;

    @Mock
    private ExtendedPagedTable<RequestSummary> extendedPagedTable;

    @Mock
    private ErrorPopupPresenter errorPopup;

    private FilterSettings filterSettings;

    private DataSetRequestListPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMockExecutorService = new CallerMock<ExecutorServiceEntryPoint>(executorServiceMock);

        filterSettings= createTableSettingsPrototype();

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getPageSize()).thenReturn(10);
        when(extendedPagedTable.getColumnSortList()).thenReturn(null);
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettings);

        presenter = spy(new DataSetRequestListPresenter(viewMock,
                callerMockExecutorService,dataSetQueryHelper));
        
        doNothing().when(presenter).setupRefreshButton();
    }

    @Test
    public void getDataTest() {
        presenter.setAddingDefaultFilters(false);
        presenter.getData(new Range(0, 5));


        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
        verify(viewMock).hideBusyIndicator();
    }
    
    @Test
    public void getMenusSetsUpRefreshButton() {
        presenter.getMenus();

        verify(presenter).setupRefreshButton();
    }
    

    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( DataSetRequestListViewImpl.REQUEST_LIST_DATASET_ID );
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_ID, "id" );
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_TIMESTAMP, "time" , "MMM dd E, yyyy");
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_STATUS,"status" );
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_COMMANDNAME , "commandName", "MMM dd E, yyyy" );
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_MESSAGE, "status" );
        builder.setColumn( DataSetRequestListViewImpl.COLUMN_BUSINESSKEY, "key" );

        builder.filterOn( true, true, true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault( DataSetRequestListViewImpl.COLUMN_TIMESTAMP, DESCENDING );
        builder.tableWidth(1000);

        return  builder.buildSettings();
    }

}