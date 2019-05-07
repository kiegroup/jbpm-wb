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
package org.jbpm.workbench.df.client.list;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.dashbuilder.displayer.client.DataSetHandlerImpl;
import org.jbpm.workbench.df.client.events.DataSetReadyEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.ks.integration.ConsoleDataSetLookup;

@Dependent
public class DataSetQueryHelper {

    protected FilterSettings currentTableSetting;

    protected String lastOrderedColumn;

    protected SortOrder lastSortOrder;

    protected DataSet dataSet;

    protected DataSetHandler dataSetHandler;

    protected DataSetClientServices dataSetClientServices;

    protected Event<DataSetReadyEvent> event;

    @Inject
    public void setDataSetClientServices(final DataSetClientServices dataSetClientServices) {
        this.dataSetClientServices = dataSetClientServices;
    }

    @Inject
    public void setDataSetReadyEvent(final Event<DataSetReadyEvent> event) {
        this.event = event;
    }

    public void lookupDataSet(final Integer offset,
                              final DataSetReadyCallback callback) {
        try {
            // Get the sort settings
            if (lastOrderedColumn == null) {
                String defaultSortColumn = currentTableSetting.getTableDefaultSortColumnId();
                if (!StringUtils.isBlank(defaultSortColumn)) {
                    lastOrderedColumn = defaultSortColumn;
                    lastSortOrder = currentTableSetting.getTableDefaultSortOrder();
                }
            }
            // Apply the sort order specified (if any)
            if (lastOrderedColumn != null) {
                dataSetHandler.sort(lastOrderedColumn,
                                    lastSortOrder);
            }
            // Lookup only the target rows
            dataSetHandler.limitDataSetRows(offset,
                                            currentTableSetting.getTablePageSize());

            // Do the lookup
            dataSetHandler.lookupDataSet(
                    new DataSetReadyCallback() {

                        public void callback(DataSet dataSet) {
                            DataSetQueryHelper.this.dataSet = dataSet;
                            callback.callback(dataSet);
                            event.fire(new DataSetReadyEvent(currentTableSetting, dataSet.getUUID()));
                        }

                        public void notFound() {
                            callback.notFound();
                        }

                        @Override
                        public boolean onError(final ClientRuntimeError error) {
                            callback.onError(error);
                            return false;
                        }
                    }
            );
        } catch (Exception e) {
            callback.onError(new ClientRuntimeError(e.getMessage()));
        }
    }

    public FilterSettings getCurrentTableSettings() {
        return currentTableSetting;
    }

    public void setCurrentTableSettings(FilterSettings currentTableSetting) {
        this.currentTableSetting = currentTableSetting;
    }

    public String getLastOrderedColumn() {
        return lastOrderedColumn;
    }

    public void setLastOrderedColumn(String lastOrderedColumn) {
        this.lastOrderedColumn = lastOrderedColumn;
    }

    public SortOrder getLastSortOrder() {
        return lastSortOrder;
    }

    public void setLastSortOrder(SortOrder lastSortOrder) {
        this.lastSortOrder = lastSortOrder;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public void setDataSetHandler(FilterSettings tableSettings) {
        this.dataSetHandler = new DataSetHandlerImpl(dataSetClientServices,
                                                     ConsoleDataSetLookup.fromInstance(tableSettings.getDataSetLookup(),
                                                                                       tableSettings.getServerTemplateId()));
    }

    public void setDataSetHandler(DataSetHandler dataSetHandler) {
        this.dataSetHandler = dataSetHandler;
    }
}
