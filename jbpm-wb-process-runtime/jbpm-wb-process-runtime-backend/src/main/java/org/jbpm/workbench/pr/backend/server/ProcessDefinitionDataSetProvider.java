/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.BeanDataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.ks.integration.ConsoleDataSetLookup;
import org.jbpm.workbench.pr.model.ProcessDefinitionDataSetDef;
import org.jbpm.workbench.pr.model.ProcessDefinitionDataSetProviderType;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;

import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSNAME;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.PROCESS_DEFINITION_DATASET;

@ApplicationScoped
public class ProcessDefinitionDataSetProvider extends BeanDataSetProvider {

    @Inject
    protected ProcessRuntimeDataService processRuntimeDataService;

    @Override
    public DataSetProviderType getType() {
        return new ProcessDefinitionDataSetProviderType();
    }

    @Override
    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {

        List<String> columnNames = new ArrayList<>();
        List<ColumnType> columnTypes = new ArrayList<>();

        List<DataColumnDef> columns = def.getColumns();
        for (DataColumnDef column : columns) {
            columnNames.add(column.getId());
            columnTypes.add(column.getColumnType());
        }

        return new DataSetMetadataImpl(def, def.getUUID(), -1, def.getColumns().size(), columnNames, columnTypes, -1);
    }

    @Override
    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {

        ProcessDefinitionDataSetDef processDefinitionDataSetDef = (ProcessDefinitionDataSetDef) def;
        final DataSetBuilder dsBuilder = DataSetFactory.newDataSetBuilder();
        for (final DataColumnDef columnDef : processDefinitionDataSetDef.getColumns()) {
            dsBuilder.column(columnDef.getId(), columnDef.getColumnType());
        }

        ConsoleDataSetLookup consoleDataSetLookup = (ConsoleDataSetLookup) lookup;
        processDefinitionDataSetDef.setServerTemplateId(consoleDataSetLookup.getServerTemplateId());

        String defaultColumnSort = COL_ID_PROCESSNAME;
        boolean defaultSortOrder = false;

        DataSetSort dataSetSort = lookup.getFirstSortOp();
        if (dataSetSort != null && dataSetSort.getColumnSortList().size() > 0) {
            defaultColumnSort = dataSetSort.getColumnSortList().get(0).getColumnId();
            defaultSortOrder = dataSetSort.getColumnSortList().get(0).getOrder().equals(SortOrder.ASCENDING);
        }

        String searchText = "";
        for (DataSetFilter filter : lookup.getOperationList(DataSetFilter.class)) {
            Optional coreFunctionFilter = filter.getColumnFilterList().stream()
                    .filter(columnFilter -> columnFilter instanceof CoreFunctionFilter)
                    .filter(columnFilter -> ((CoreFunctionFilter) columnFilter).getParameters().size() > 0).findFirst();

            if (coreFunctionFilter.isPresent()) {
                searchText = ((CoreFunctionFilter) coreFunctionFilter.get()).getParameters().get(0).toString();
            }
        }
        List<ProcessSummary> processSummaryList = processRuntimeDataService.getProcessesByFilter(consoleDataSetLookup.getServerTemplateId(),
                                                                                                 searchText,
                                                                                                 lookup.getRowOffset() / lookup.getNumberOfRows(),
                                                                                                 lookup.getNumberOfRows(),
                                                                                                 defaultColumnSort,
                                                                                                 defaultSortOrder);

        processSummaryList.forEach(processSummary -> dsBuilder.row(processSummary.getProcessDefName(),
                                                                   processSummary.getVersion(),
                                                                   processSummary.getDeploymentId(),
                                                                   processSummary.getProcessDefId(),
                                                                   processSummary.isDynamic())

        );

        DataSet dataSet = dsBuilder.buildDataSet();
        dataSet.setUUID(PROCESS_DEFINITION_DATASET);
        return dataSet;
    }

    @Override
    public boolean isDataSetOutdated(DataSetDef def) {
        return false;
    }
}
