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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFiltersPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.client.util.LogUtils;

import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.*;

@Dependent
public class ProcessInstanceLogBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    public String getDataSetId() {
        return PROCESS_INSTANCE_LOGS_DATASET;
    }

    @Override
    public void loadFilters() {

        final Map<String, String> logStateOptions = new HashMap<>();
        logStateOptions.put(LogUtils.NODE_ENTERED.toString(),
                            constants.NodeEntered());
        logStateOptions.put(LogUtils.NODE_COMPLETED.toString(),
                            constants.NodeCompleted());

        view.addMultiSelectFilter(constants.EventType(),
                                  logStateOptions,
                                  f -> {
                                      final List<String> values = f.getValue();
                                      final ColumnFilter columnFilter = in(COLUMN_LOG_TYPE,
                                                                           values);
                                      addSearchFilterList(f,
                                                          columnFilter);
                                  }

        );

        final Map<String, String> logTypeOptions = new HashMap<>();
        logTypeOptions.put(LogUtils.ALL_NODES,
                           constants.All());
        logTypeOptions.put(LogUtils.NODE_TYPE_HUMAN_TASK,
                           constants.Human_Tasks());
        logTypeOptions.put(LogUtils.NODE_TYPE_START,
                           constants.StartNodes());
        logTypeOptions.put(LogUtils.NODE_TYPE_END,
                           constants.EndNodes());
        logTypeOptions.put(LogUtils.NODE_TYPE_ACTION,
                           constants.ActionNodes());
        logTypeOptions.put(LogUtils.NODE_TYPE_MILESTONE,
                           constants.Milestones());
        logTypeOptions.put(LogUtils.NODE_TYPE_SUBPROCESS,
                           constants.SubProcesses());
        logTypeOptions.put(LogUtils.NODE_TYPE_RULE_SET,
                           constants.RuleSets());
        logTypeOptions.put(LogUtils.NODE_TYPE_WORK_ITEM,
                           constants.WorkItems());

        view.addMultiSelectFilter(constants.EventNodeType(),
                                  logTypeOptions,
                                  f -> {
                                      final List<String> values = f.getValue();
                                      final ColumnFilter columnFilter = in(COLUMN_LOG_NODE_TYPE,
                                                                           values);
                                      if (values != null && values.contains(LogUtils.ALL_NODES)) {
                                          removeSearchFilter(f,
                                                             columnFilter);
                                          checkSelectFilterValues(constants.EventNodeType(),
                                                                  Arrays.asList(LogUtils.NODE_TYPE_HUMAN_TASK,
                                                                                LogUtils.NODE_TYPE_START,
                                                                                LogUtils.NODE_TYPE_END,
                                                                                LogUtils.NODE_TYPE_ACTION,
                                                                                LogUtils.NODE_TYPE_MILESTONE,
                                                                                LogUtils.NODE_TYPE_SUBPROCESS,
                                                                                LogUtils.NODE_TYPE_RULE_SET,
                                                                                LogUtils.NODE_TYPE_WORK_ITEM));
                                      } else {
                                          addSearchFilterList(f,
                                                              columnFilter);
                                      }
                                  });
        view.hideFilterBySection();
    }

    @Override
    protected void onActiveFilterAdded(ActiveFilterItem activeFilterItem) {
        if (activeFilterItem.getKey().equals(constants.EventNodeType())) {
            checkSelectFilterValues(constants.EventNodeType(),
                                    (List<String>) activeFilterItem.getValue());
        }
        if (activeFilterItem.getKey().equals(constants.EventType())) {
            checkSelectFilterValues(constants.EventType(),
                                    (List<String>) activeFilterItem.getValue());
        }
    }

    public void checkSelectFilterValues(String label,
                                        List<String> values) {
        values.forEach(v -> view.checkSelectFilter(label,
                                                   v));
    }
}