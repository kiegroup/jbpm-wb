/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFiltersPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_LIST_BASIC_FILTERS_SCREEN;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@ApplicationScoped
@WorkbenchScreen(identifier = PROCESS_INSTANCE_LIST_BASIC_FILTERS_SCREEN)
public class ProcessInstanceListBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    public String getDataSetId() {
        return PROCESS_INSTANCE_DATASET;
    }

    @Override
    public void loadFilters() {
        view.addNumericFilter(constants.Id(),
                              constants.FilterByProcessInstanceId(),
                              f -> addSearchFilter(f,
                                                   equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                            f.getValue()))
        );

        view.addTextFilter(constants.Initiator(),
                           constants.FilterByInitiator(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_IDENTITY,
                                                       f.getValue(),
                                                       false))
        );

        view.addTextFilter(constants.Correlation_Key(),
                           constants.FilterByCorrelationKey(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_CORRELATION_KEY,
                                                       f.getValue(),
                                                       false))
        );

        view.addTextFilter(constants.Process_Instance_Description(),
                           constants.FilterByDescription(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                       f.getValue(),
                                                       false))
        );

        final Map<String, String> errorOptions = new HashMap<>();
        errorOptions.put(String.valueOf(true),
                         constants.WithErrors());
        errorOptions.put(String.valueOf(false),
                         constants.WithoutErrors());
        final Function<String, ColumnFilter> errorFilterGenerator = (String hasErrors) ->
                (Boolean.valueOf(hasErrors) ? greaterThan(COLUMN_ERROR_COUNT,
                                                          0) : lowerOrEqualsTo(COLUMN_ERROR_COUNT,
                                                                               0));
        view.addMultiSelectFilter(constants.Errors(),
                                  errorOptions,
                                  f -> {
                                      if (f.getValue().isEmpty() || f.getValue().size() == 2) {
                                          removeSearchFilter(f,
                                                             notNull(COLUMN_ERROR_COUNT));
                                      } else {
                                          addSearchFilter(f,
                                                          errorFilterGenerator.apply(f.getValue().get(0)));
                                      }
                                  }
        );

        final Map<String, String> states = new HashMap<>();
        states.put(String.valueOf(ProcessInstance.STATE_ACTIVE),
                   constants.Active());
        states.put(String.valueOf(ProcessInstance.STATE_ABORTED),
                   constants.Aborted());
        states.put(String.valueOf(ProcessInstance.STATE_COMPLETED),
                   constants.Completed());
        states.put(String.valueOf(ProcessInstance.STATE_PENDING),
                   constants.Pending());
        states.put(String.valueOf(ProcessInstance.STATE_SUSPENDED),
                   constants.Suspended());
        view.addMultiSelectFilter(constants.State(),
                                  states,
                                  f -> {
                                      final List<Integer> values = f.getValue().stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
                                      final ColumnFilter columnFilter = in(COLUMN_STATUS,
                                                                           values);
                                      addSearchFilterList(f,
                                                          columnFilter);
        });

        addProcessNameFilter();

        addProcessIdFilter();

        addDeploymentIdFilter();

        addSLAComplianceFilter();

        view.addDateRangeFilter(constants.Start_Date(),
                                constants.Start_Date_Placeholder(),
                                true,
                                f -> addSearchFilter(f,
                                                     between(COLUMN_START,
                                                             f.getValue().getStartDate(),
                                                             f.getValue().getEndDate()))
        );

        view.addDateRangeFilter(constants.Last_Modification_Date(),
                                constants.Last_Modification_Date_Placeholder(),
                                true,
                                f -> addSearchFilter(f,
                                                     between(COLUMN_LAST_MODIFICATION_DATE,
                                                             f.getValue().getStartDate(),
                                                             f.getValue().getEndDate()))
        );
    }

    protected void addProcessNameFilter() {
        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(PROCESS_INSTANCE_DATASET)
                .group(COLUMN_PROCESS_NAME)
                .column(COLUMN_PROCESS_NAME)
                .sort(COLUMN_PROCESS_NAME, SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Name(),
                                    dataSetLookup,
                                    COLUMN_PROCESS_NAME,
                                    COLUMN_PROCESS_NAME,
                                    f -> addSearchFilter(f, equalsTo(COLUMN_PROCESS_NAME, f.getValue())));
    }

    protected void addProcessIdFilter() {
        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(PROCESS_INSTANCE_DATASET)
                .group(COLUMN_PROCESS_ID)
                .column(COLUMN_PROCESS_ID)
                .sort(COLUMN_PROCESS_ID, SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Process_Definition_Id(),
                                    dataSetLookup,
                                    COLUMN_PROCESS_ID,
                                    COLUMN_PROCESS_ID,
                                    f -> addSearchFilter(f, equalsTo(COLUMN_PROCESS_ID, f.getValue())));
    }

    protected void addDeploymentIdFilter() {
        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(PROCESS_INSTANCE_DATASET)
                .group(COLUMN_EXTERNAL_ID)
                .column(COLUMN_EXTERNAL_ID)
                .sort(COLUMN_EXTERNAL_ID, SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.DeploymentId(),
                                    dataSetLookup,
                                    COLUMN_EXTERNAL_ID,
                                    COLUMN_EXTERNAL_ID,
                                    f -> addSearchFilter(f, equalsTo(COLUMN_EXTERNAL_ID, f.getValue())));
    }

    protected void addSLAComplianceFilter() {
        Map<String, String> aliasMap = new HashMap<String, String>();
        aliasMap.put(String.valueOf(ProcessInstance.SLA_ABORTED), constants.SlaAborted());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_MET), constants.SlaMet());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_NA), constants.SlaNA());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_PENDING), constants.SlaPending());
        aliasMap.put(String.valueOf(ProcessInstance.SLA_VIOLATED), constants.SlaViolated());
        view.addSelectFilter(constants.SlaCompliance(),
                             aliasMap,
                             f -> addSearchFilter(f, equalsTo(COLUMN_SLA_COMPLIANCE, f.getValue())));
    }

    @Override
    protected void onActiveFilterAdded(ActiveFilterItem activeFilterItem) {
        if(activeFilterItem.getKey().equals(constants.State())){
            view.checkSelectFilter(constants.State(), activeFilterItem.getValue().toString());
        }
    }

}
