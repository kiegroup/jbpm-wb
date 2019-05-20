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

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
public class ProcessInstanceListFilterSettingsManager extends FilterSettingsManagerImpl {

    protected static final String TAB_ACTIVE = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0";
    protected static final String TAB_COMPLETED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1";
    protected static final String TAB_ABORTED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2";

    private Constants constants = Constants.INSTANCE;

    @Override
    public String getGridGlobalPreferencesKey() {
        return PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX;
    }

    @Override
    public FilterSettings createFilterSettingsPrototype() {
        return createFilterSettings(PROCESS_INSTANCE_DATASET,
                                    COLUMN_START,
                                    null);
    }

    @Override
    public List<FilterSettings> initDefaultFilters() {
        return Arrays.asList(
                //Filter status Active
                createFilterSettings(PROCESS_INSTANCE_DATASET,
                                     COLUMN_START,
                                     builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                                        ProcessInstance.STATE_ACTIVE)),
                                     getDefaultFilterSettingsKey(), //initialDefaultFilter
                                     constants.Active(),
                                     constants.FilterActive()),

                //Filter status completed
                createFilterSettings(PROCESS_INSTANCE_DATASET,
                                     COLUMN_START,
                                     builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                                        ProcessInstance.STATE_COMPLETED)),
                                     TAB_COMPLETED,
                                     constants.Completed(),
                                     constants.FilterCompleted()),

                //Filter status aborted
                createFilterSettings(PROCESS_INSTANCE_DATASET,
                                     COLUMN_START,
                                     builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                                        ProcessInstance.STATE_ABORTED)),
                                     TAB_ABORTED,
                                     constants.Aborted(),
                                     constants.FilterAborted())
        );
    }

    @Override
    public FilterSettings getVariablesFilterSettings(final List<Long> processIds) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(PROCESS_INSTANCE_WITH_VARIABLES_DATASET);
        builder.filter(in(PROCESS_INSTANCE_ID,
                          processIds));

        builder.setColumn(PROCESS_INSTANCE_ID,
                          "processInstanceId");
        builder.setColumn(PROCESS_NAME,
                          "processName");
        builder.setColumn(VARIABLE_ID,
                          "variableID");
        builder.setColumn(VARIABLE_NAME,
                          "variableName");
        builder.setColumn(VARIABLE_VALUE,
                          "variableValue");

        builder.filterOn(true,
                         true,
                         true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(PROCESS_INSTANCE_ID,
                                  SortOrder.ASCENDING);

        FilterSettings varTableSettings = builder.buildSettings();
        varTableSettings.setTablePageSize(-1);
        varTableSettings.setTableName("Filtered");
        varTableSettings.setTableDescription("Filtered Desc");

        return varTableSettings;
    }
}
