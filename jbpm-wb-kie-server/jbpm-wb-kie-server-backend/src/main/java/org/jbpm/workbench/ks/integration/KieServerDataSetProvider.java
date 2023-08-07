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

package org.jbpm.workbench.ks.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataprovider.DataSetProvider;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.DataColumnImpl;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.definition.QueryParam;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KieServerDataSetProvider extends AbstractKieServerService implements DataSetProvider {

    public static final DataSetProviderType TYPE = new KieServerDataSetProviderType();
    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerDataSetProvider.class);

    @Override
    public DataSetProviderType getType() {
        return TYPE;
    }

    @Override
    public DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception {
        List<String> columnNames = new ArrayList<>();
        List<ColumnType> columnTypes = new ArrayList<>();

        if (def.getColumns() == null && def instanceof RemoteDataSetDef) {
            final QueryServicesClient queryClient = getClient(((RemoteDataSetDef) def).getServerTemplateId(),
                    QueryServicesClient.class);

            QueryDefinition definition = queryClient.getQuery(def.getUUID());
            if (definition != null) {
                addColumnsToDefinition(def, definition.getColumns());
            }
        }
        List<DataColumnDef> columns = def.getColumns();
        for (DataColumnDef column : columns) {
            columnNames.add(column.getId());
            columnTypes.add(column.getColumnType());
        }
        return new DataSetMetadataImpl(def,
                def.getUUID(),
                -1,
                def.getColumns().size(),
                columnNames,
                columnTypes,
                -1);
    }

    @Override
    public DataSet lookupDataSet(DataSetDef def,
                                 DataSetLookup lookup) throws Exception {

        ConsoleDataSetLookup dataSetLookup = adoptLookup(def,
                lookup);

        LOGGER.debug("Data Set lookup using Server Template Id: {}",
                dataSetLookup.getServerTemplateId());
        if (dataSetLookup.getServerTemplateId() == null || dataSetLookup.getServerTemplateId().isEmpty()) {
            return buildDataSet(def,
                    new ArrayList<>(),
                    new ArrayList<>());
        }

        final QueryServicesClient queryClient = getClient(dataSetLookup.getServerTemplateId(),
                QueryServicesClient.class);

        List<QueryParam> filterParams = new ArrayList<>();
        QueryFilterSpec filterSpec = new QueryFilterSpec();
        // apply filtering

        for (DataSetFilter filter : dataSetLookup.getOperationList(DataSetFilter.class)) {
            if (filter != null) {

                for (ColumnFilter cFilter : filter.getColumnFilterList()) {
                    if (cFilter instanceof CoreFunctionFilter) {

                        CoreFunctionFilter coreFunctionFilter = (CoreFunctionFilter) cFilter;

                        filterParams.add(new QueryParam(coreFunctionFilter.getColumnId(),
                                coreFunctionFilter.getType().toString(),
                                coreFunctionFilter.getParameters()));
                    } else if (cFilter instanceof LogicalExprFilter) {
                        LogicalExprFilter logicalExprFilter = (LogicalExprFilter) cFilter;
                        filterParams.add(new QueryParam(logicalExprFilter.getColumnId(),
                                logicalExprFilter.getLogicalOperator().toString(),
                                logicalExprFilter.getLogicalTerms()));
                    }
                }
            }
        }
        List<DataColumn> extraColumns = new ArrayList<DataColumn>();

        List<DataSetGroup> dataSetGroups = lookup.getFirstGroupOpSelections();
        for (DataSetGroup group : dataSetGroups) {
            if (group.getSelectedIntervalList() != null && group.getSelectedIntervalList().size() > 0) {
                appendIntervalSelection(def,
                        group,
                        filterParams);
            }
        }

        DataSetGroup dataSetGroup = dataSetLookup.getLastGroupOp();
        handleDataSetGroup(def,
                dataSetGroup,
                filterParams,
                extraColumns);

        if (!filterParams.isEmpty()) {
            filterSpec.setParameters(filterParams.toArray(new QueryParam[filterParams.size()]));
        }

        // apply sorting
        DataSetSort sort = dataSetLookup.getFirstSortOp();
        if (sort != null) {
            SortOrder sortOrder = SortOrder.UNSPECIFIED;
            StringBuilder orderBy = new StringBuilder();
            for (ColumnSort cSort : sort.getColumnSortList()) {
                orderBy.append(cSort.getColumnId()).append(",");
                sortOrder = cSort.getOrder();
            }
            // remove last ,
            orderBy.deleteCharAt(orderBy.length() - 1);

            filterSpec.setOrderBy(orderBy.toString());
            filterSpec.setAscending(sortOrder.equals(SortOrder.ASCENDING));
        }
        final List<List> instances = performQuery((RemoteDataSetDef) def,
                dataSetLookup,
                queryClient,
                filterSpec);
        LOGGER.debug("Query client returned {} row(s)",
                instances.size());

        return buildDataSet(def,
                instances,
                extraColumns);
    }

    protected ConsoleDataSetLookup adoptLookup(DataSetDef def,
                                               DataSetLookup lookup) {
        ConsoleDataSetLookup dataSetLookup = null;
        if (!(lookup instanceof ConsoleDataSetLookup)) {

            if (def instanceof RemoteDataSetDef) {
                dataSetLookup = (ConsoleDataSetLookup) ConsoleDataSetLookup.fromInstance(lookup,
                        ((RemoteDataSetDef) def).getServerTemplateId());
                DataSetFilter filter = def.getDataSetFilter();
                if (filter != null) {
                    dataSetLookup.addOperation(filter);
                }
            } else {

                throw new IllegalArgumentException("DataSetLookup is of incorrect type " + lookup.getClass().getName());
            }
        } else {
            dataSetLookup = (ConsoleDataSetLookup) lookup;
        }

        return dataSetLookup;
    }

    protected List<List> performQuery(RemoteDataSetDef def,
                                      ConsoleDataSetLookup dataSetLookup,
                                      QueryServicesClient queryClient,
                                      QueryFilterSpec filterSpec) {

        if (dataSetLookup.testMode()) {
            QueryDefinition queryDefinition = QueryDefinition.builder()
                    .name(dataSetLookup.getDataSetUUID())
                    .source(def.getDataSource())
                    .target(def.getQueryTarget())
                    .expression(def.getDbSQL())
                    .build();

            kieServerIntegration.broadcastToKieServers(((RemoteDataSetDef) def).getServerTemplateId(),
                    (KieServicesClient client) -> {
                        QueryServicesClient instanceQueryClient = client.getServicesClient(QueryServicesClient.class);
                        QueryDefinition registered = instanceQueryClient.replaceQuery(queryDefinition);
                        if (registered.getColumns() != null) {

                            for (Entry<String, String> entry : registered.getColumns().entrySet()) {
                                if (def.getColumnById(entry.getKey()) == null) {
                                    def.addColumn(entry.getKey(),
                                            ColumnType.valueOf(entry.getValue()));
                                }
                            }
                        }
                        return registered;
                    });

            try {
                return queryClient.query(
                        dataSetLookup.getDataSetUUID(),
                        QueryServicesClient.QUERY_MAP_RAW,
                        filterSpec,
                        dataSetLookup.getRowOffset() / dataSetLookup.getNumberOfRows(),
                        dataSetLookup.getNumberOfRows(),
                        List.class);
            } catch (Exception e) {
                queryClient.unregisterQuery(dataSetLookup.getDataSetUUID());
                throw new RuntimeException(e);
            }
        } else {
            if (def.getColumns() != null && def.getColumns().isEmpty()) {
                QueryDefinition query = queryClient.getQuery(def.getUUID());
                if (query != null) {
                    addColumnsToDefinition(def, query.getColumns());
                }
            }
            return queryClient.query(
                    dataSetLookup.getDataSetUUID(),
                    QueryServicesClient.QUERY_MAP_RAW,
                    filterSpec,
                    dataSetLookup.getRowOffset() / dataSetLookup.getNumberOfRows(),
                    dataSetLookup.getNumberOfRows(),
                    List.class);
        }
    }

    @Override
    public boolean isDataSetOutdated(DataSetDef def) {
        return false;
    }

    protected DataSet buildDataSet(DataSetDef def,
                                   List<List> instances,
                                   List<DataColumn> extraColumns) throws Exception {
        DataSet dataSet = DataSetFactory.newEmptyDataSet();
        dataSet.setUUID(def.getUUID());
        dataSet.setDefinition(def);

        if (extraColumns != null && !extraColumns.isEmpty()) {

            for (DataColumn extraColumn : extraColumns) {
                dataSet.addColumn(extraColumn);
            }
        } else {

            for (DataColumnDef column : def.getColumns()) {
                DataColumn numRows = new DataColumnImpl(column.getId(),
                        column.getColumnType());
                dataSet.addColumn(numRows);
            }
        }

        for (List<Object> row : instances) {

            int columnIndex = 0;
            for (Object value : row) {
                DataColumn intervalBuilder = dataSet.getColumnByIndex(columnIndex);
                if (intervalBuilder.getColumnType().equals(ColumnType.DATE) && value instanceof Long) {
                    intervalBuilder.getValues().add(new Date((Long) value));

                } else {
                    intervalBuilder.getValues().add(value);
                }

                columnIndex++;
            }
        }
        // set size of the results to allow paging to be more then the actual size
        //        dataSet.setRowCountNonTrimmed(instances.size() == 0 ? 0 : instances.size() + 1);
        dataSet.setRowCountNonTrimmed(instances.size());
        return dataSet;
    }

    protected void appendIntervalSelection(DataSetDef def,
                                           DataSetGroup intervalSel,
                                           List<QueryParam> filterParams) {
        if (intervalSel == null || !intervalSel.isSelect()) {
            return;
        }
        ColumnGroup cg = intervalSel.getColumnGroup();
        List<Interval> intervalList = intervalSel.getSelectedIntervalList();
        DataColumnDef columnDef = def.getColumnById(cg.getSourceId());

        // Get the filter values
        List<Comparable> names = new ArrayList<Comparable>();
        Comparable min = null;
        Comparable max = null;
        for (Interval interval : intervalList) {
            names.add(interval.getName());
            Comparable intervalMin = (Comparable) interval.getMinValue();
            Comparable intervalMax = (Comparable) interval.getMaxValue();

            if (intervalMin != null) {
                if (min == null) {
                    min = intervalMin;
                } else if (min.compareTo(intervalMin) > 0) {
                    min = intervalMin;
                }
            }
            if (intervalMax != null) {
                if (max == null) {
                    max = intervalMax;
                } else if (max.compareTo(intervalMax) > 0) {
                    max = intervalMax;
                }
            }
        }
        // Min can't be greater than max.
        if (min != null && max != null && min.compareTo(max) > 0) {
            min = max;
        }

        ColumnFilter filter;
        if (min != null && max != null) {
            filter = FilterFactory.between(cg.getSourceId(),
                    min,
                    max);
        } else if (min != null) {
            filter = FilterFactory.greaterOrEqualsTo(cg.getSourceId(),
                    min);
        } else if (max != null) {
            filter = FilterFactory.lowerOrEqualsTo(cg.getSourceId(),
                    max);
        } else if (columnDef != null && columnDef.getColumnType() == ColumnType.DATE) {
            filter = DateColumnFilterFactory.createFilter(cg, names);
        } else {
            filter = FilterFactory.equalsTo(cg.getSourceId(),
                    names);
        }
        CoreFunctionFilter coreFunctionFilter = (CoreFunctionFilter) filter;
        filterParams.add(new QueryParam(coreFunctionFilter.getColumnId(),
                coreFunctionFilter.getType().toString(),
                coreFunctionFilter.getParameters()));

    }

    protected void handleDataSetGroup(final DataSetDef def,
                                      final DataSetGroup dataSetGroup,
                                      final List<QueryParam> filterParams,
                                      final List<DataColumn> extraColumns) {
        if (dataSetGroup != null) {
            if (dataSetGroup.getColumnGroup() != null) {
                // handle group
                if (dataSetGroup.getColumnGroup().getIntervalSize() != null) {
                    filterParams.add(new QueryParam(dataSetGroup.getColumnGroup().getSourceId(),
                            "group",
                            Arrays.asList(dataSetGroup.getColumnGroup().getColumnId(),
                                    dataSetGroup.getColumnGroup().getIntervalSize(),
                                    dataSetGroup.getColumnGroup().getMaxIntervals())));
                } else {
                    filterParams.add(new QueryParam(dataSetGroup.getColumnGroup().getSourceId(),
                            "group",
                            Arrays.asList(dataSetGroup.getColumnGroup().getColumnId())));
                }
            }

            // handle additional columns
            for (GroupFunction groupFunction : dataSetGroup.getGroupFunctions()) {
                if (groupFunction.getFunction() != null) {
                    filterParams.add(new QueryParam(groupFunction.getSourceId(),
                            groupFunction.getFunction().toString(),
                            Arrays.asList(groupFunction.getColumnId())));
                    extraColumns.add(new DataColumnImpl(groupFunction.getSourceId(),
                            ColumnType.NUMBER));
                } else {
                    filterParams.add(new QueryParam(groupFunction.getSourceId(),
                            null,
                            Arrays.asList(groupFunction.getColumnId())));
                    extraColumns.add(new DataColumnImpl(groupFunction.getSourceId(),
                            getGroupFunctionColumnType(def,
                                    dataSetGroup.getColumnGroup(),
                                    groupFunction)));
                }
            }
        }
    }

    protected ColumnType getGroupFunctionColumnType(final DataSetDef def,
                                                    final ColumnGroup columnGroup,
                                                    final GroupFunction groupFunction) {

        DataColumnDef column = def.getColumnById(groupFunction.getColumnId());
        if (column == null) {
            column = def.getColumnById(groupFunction.getSourceId());
        }
        ColumnType type = column.getColumnType();
        if (type != ColumnType.DATE || columnGroup == null || groupFunction == null) {
            return type;
        } else {
            return columnGroup.getSourceId().equals(groupFunction.getSourceId()) ? ColumnType.LABEL : type;
        }
    }

    protected void addColumnsToDefinition(DataSetDef def, Map<String, String> columns) {
        if (columns != null) {
            columns.entrySet().stream()
                    .filter(e -> def.getColumnById(e.getKey()) == null)
                    .forEach(e -> def.addColumn(e.getKey(), ColumnType.valueOf(e.getValue())));
        }
    }
}
