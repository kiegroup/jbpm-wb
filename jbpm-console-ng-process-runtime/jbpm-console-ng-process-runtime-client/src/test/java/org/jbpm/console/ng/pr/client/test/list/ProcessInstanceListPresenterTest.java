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
package org.jbpm.console.ng.pr.client.test.list;

import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jbpm.console.ng.df.client.filter.FilterSettings;

import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash.DataSetProcessInstanceWithVariablesListPresenter.DataSetProcessInstanceWithVariablesListView;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListPresenterTest {
    
    @Mock
    private DataSetQueryHelper dataSetQueryHelperMock;
    
    @Mock
    private DataSetQueryHelper dataSetQueryHelperDomainSpecificMock;
    
    @Mock
    private FilterSettings filterSettingsMock;
    
    @Mock
    private DataSetProcessInstanceWithVariablesListView viewMock;
    
    @Mock
    private ExtendedPagedTable<ProcessInstanceSummary> tableMock;
    
    @Mock
    private DataSet dataSetMock;
    


    private MyDataSetProcessInstanceWithVariablesListPresenter presenter;

    
    @Before
    public void setupMocks() {

        when(dataSetQueryHelperMock.getCurrentTableSettings()).thenReturn(filterSettingsMock);
        when(viewMock.getListGrid()).thenReturn(tableMock);
        when(tableMock.getPageSize()).thenReturn(10);
        when(dataSetMock.getRowCountNonTrimmed()).thenReturn(8);
        when(viewMock.getVariablesTableSettings("mock")).thenReturn(filterSettingsMock);
        
        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback)invocation.getArguments()[1]).callback(dataSetMock);
                return null;
            }
        }).when(dataSetQueryHelperMock).lookupDataSet(anyInt(), (DataSetReadyCallback) anyObject());
        
        //Mock that actually calls the callbacks
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback)invocation.getArguments()[1]).callback(dataSetMock);
                return null;
            }
        }).when(dataSetQueryHelperDomainSpecificMock).lookupDataSet(anyInt(), (DataSetReadyCallback) anyObject());
        
    }

    @Test
    public void domainSpecificColumnsWithPagination() {
        presenter = new MyDataSetProcessInstanceWithVariablesListPresenter(viewMock, dataSetQueryHelperMock, dataSetQueryHelperDomainSpecificMock); 
        presenter.setDataSetQueryHelperMock(dataSetQueryHelperMock);
        presenter.setFilterSettingsMock(filterSettingsMock);
        presenter.setDataSetQueryHelperDomainSpecificMock(dataSetQueryHelperDomainSpecificMock);
        //Get the first page of process instances selected
        Range range = new Range(0, 10);
        
        presenter.getData(range);
     
    }
    
   
    

}

