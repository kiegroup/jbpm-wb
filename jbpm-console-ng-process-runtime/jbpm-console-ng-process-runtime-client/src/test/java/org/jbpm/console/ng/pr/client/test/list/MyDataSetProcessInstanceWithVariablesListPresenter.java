/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.pr.client.test.list;

import java.util.ArrayList;
import java.util.List;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash.DataSetProcessInstanceWithVariablesListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash.DataSetProcessInstanceWithVariablesListViewImpl;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.uberfire.client.annotations.WorkbenchScreen;

/**
 *
 * @author salaboy
 */
@WorkbenchScreen(identifier = "mock")
public class MyDataSetProcessInstanceWithVariablesListPresenter extends DataSetProcessInstanceWithVariablesListPresenter {

    public MyDataSetProcessInstanceWithVariablesListPresenter(DataSetProcessInstanceWithVariablesListPresenter.DataSetProcessInstanceWithVariablesListView view, DataSetQueryHelper dataSetQueryHelper, DataSetQueryHelper dataSetQueryHelperDomainSpecific) {
        super(view, dataSetQueryHelper, dataSetQueryHelperDomainSpecific);
    }

    private DataSetQueryHelper dataSetQueryHelperMock;

    private DataSetQueryHelper dataSetQueryHelperDomainSpecificMock;

    private FilterSettings filterSettingsMock;

    public DataSetQueryHelper getDataSetQueryHelperMock() {
        return dataSetQueryHelperMock;
    }

    public void setDataSetQueryHelperMock(DataSetQueryHelper dataSetQueryHelperMock) {
        this.dataSetQueryHelperMock = dataSetQueryHelperMock;
    }

    public DataSetQueryHelper getDataSetQueryHelperDomainSpecificMock() {
        return dataSetQueryHelperDomainSpecificMock;
    }

    public void setDataSetQueryHelperDomainSpecificMock(DataSetQueryHelper dataSetQueryHelperDomainSpecificMock) {
        this.dataSetQueryHelperDomainSpecificMock = dataSetQueryHelperDomainSpecificMock;
    }

    public FilterSettings getFilterSettingsMock() {
        return filterSettingsMock;
    }

    public void setFilterSettingsMock(FilterSettings filterSettingsMock) {
        this.filterSettingsMock = filterSettingsMock;
    }
    
    

    @Override
    protected DataSetReadyCallback createDataSetProcessInstanceCallback(int startRange, FilterSettings fs) {
        return new DataSetReadyCallback() {

            @Override
            public void callback(DataSet dataSet) {
                verify(dataSetQueryHelperMock, times(1)).setLastSortOrder(SortOrder.ASCENDING);
                verify(dataSetQueryHelperMock, times(1)).setLastOrderedColumn(DataSetProcessInstanceWithVariablesListViewImpl.COLUMN_START);
                getDomainSpecifDataForProcessInstances(0, dataSet, "mock", new ArrayList<ProcessInstanceSummary>());
            }

            @Override
            public void notFound() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean onError(ClientRuntimeError error) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

    }

    @Override
    protected DataSetReadyCallback createDataSetDomainSpecificCallback(int startRange, int totalRowSize, List<ProcessInstanceSummary> instances, FilterSettings tableSettings) {
        return new DataSetReadyCallback() {

            @Override
            public void callback(DataSet dataSet) {
                verify(dataSetQueryHelperDomainSpecificMock, times(1)).setLastSortOrder(SortOrder.ASCENDING);
                verify(dataSetQueryHelperDomainSpecificMock, times(1)).setLastOrderedColumn("pid");
                verify(filterSettingsMock).setTablePageSize(-1);
            }

            @Override
            public void notFound() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean onError(ClientRuntimeError error) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
}