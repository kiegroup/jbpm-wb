/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.df.client.list.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServiceError;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;

import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.dataset.DataSetHandler;
import org.jbpm.console.ng.df.client.filter.dataset.DataSetHandlerImpl;

import javax.enterprise.context.Dependent;


@Dependent
public class DataSetQueryHelper<T> {

    protected FilterSettings currentTableSetting;

    protected int numberOfRows = 0;

    protected String lastOrderedColumn = null;

    protected SortOrder lastSortOrder = null;

    protected DataSet dataSet;

    protected DataSetHandler dataSetHandler;


    protected Timer refreshTimer = null;
    protected boolean autoRefreshEnabled = true;
    protected int autoRefreshSeconds = 10; // This should be loaded from the grid settings (probably the filters)


    public DataSetQueryHelper() {
    }

    public FilterSettings getCurrentTableSettings(){
      return currentTableSetting;
    }

    public void setCurrentTableSettings(FilterSettings tableSettings){
        this.currentTableSetting=tableSettings;
    }

    public void lookupDataSet(Integer offset, final DataSetReadyCallback callback) {
        try {
           // Get the sort settings
            if (lastOrderedColumn == null) {
                String defaultSortColumn = currentTableSetting.getTableDefaultSortColumnId();
                if (!StringUtils.isBlank( defaultSortColumn )) {
                    lastOrderedColumn = defaultSortColumn;
                    lastSortOrder = currentTableSetting.getTableDefaultSortOrder();
                }
            }
            // Apply the sort order specified (if any)
            if (lastOrderedColumn != null) {
                dataSetHandler.sort( lastOrderedColumn, lastSortOrder );
            }
            // Lookup only the target rows
            dataSetHandler.limitDataSetRows(offset, currentTableSetting.getTablePageSize());

            // Do the lookup
            dataSetHandler.lookupDataSet(
                    new DataSetReadyCallback() {

                        public void callback( DataSet dataSet ) {
                            DataSetQueryHelper.this.dataSet = dataSet;
                            numberOfRows = dataSet.getRowCountNonTrimmed();
                            callback.callback( dataSet );
                        }
                        public void notFound() {
                            callback.notFound();
                        }

                        @Override
                        public boolean onError(DataSetClientServiceError error) {
                            callback.onError(error);
                            return false;
                        }
                    }
            );
        } catch ( Exception e ) {
            GWT.log("DataSetQueryHelper: lookuDataserError"+e.getMessage());

        }
    }

    public void setLastOrderedColumn(String lastOrderedColumn){
        this.lastOrderedColumn = lastOrderedColumn;
    }

    public void setLastSortOrder(SortOrder lastSortOrder){
        this.lastSortOrder = lastSortOrder;
    }

    public FilterSettings getCurrentTableSetting() {
        return currentTableSetting;
    }

    public void setCurrentTableSetting( FilterSettings currentTableSetting ) {
        this.currentTableSetting = currentTableSetting;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows( int numberOfRows ) {
        this.numberOfRows = numberOfRows;
    }

    public String getLastOrderedColumn() {
        return lastOrderedColumn;
    }

    public SortOrder getLastSortOrder() {
        return lastSortOrder;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet ) {
        this.dataSet = dataSet;
    }

    public DataSetHandler getDataSetHandler() {
        return dataSetHandler;
    }

    public void setDataSetHandler(FilterSettings tableSettings) {
        this.dataSetHandler = new DataSetHandlerImpl( tableSettings.getDataSetLookup() );
    }

    public Timer getRefreshTimer() {
        return refreshTimer;
    }

    public void setRefreshTimer( Timer refreshTimer ) {
        this.refreshTimer = refreshTimer;
    }

    public boolean isAutoRefreshEnabled() {
        return autoRefreshEnabled;
    }

    public void setAutoRefreshEnabled( boolean autoRefreshEnabled ) {
        this.autoRefreshEnabled = autoRefreshEnabled;
    }

    public int getAutoRefreshSeconds() {
        return autoRefreshSeconds;
    }

    public void setAutoRefreshSeconds( int autoRefreshSeconds ) {
        this.autoRefreshSeconds = autoRefreshSeconds;
    }
}
