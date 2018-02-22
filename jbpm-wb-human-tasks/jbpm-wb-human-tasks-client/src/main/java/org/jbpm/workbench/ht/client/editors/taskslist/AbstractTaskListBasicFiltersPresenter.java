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

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.filters.BasicFiltersPresenter;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.util.TaskUtils.getStatusByType;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    protected String getAdvancedFilterPopupTitle() {
        return constants.New_FilteredList();
    }

    @Override
    public void loadFilters() {
        view.addNumericFilter(constants.Id(),
                              constants.FilterByTaskId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_TASK_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_TASK_ID,
                                                                       v))
        );

        view.addTextFilter(constants.Task(),
                           constants.FilterByTaskName(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_NAME,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_NAME,
                                                                  v,
                                                                  false))
        );

        final Map<String, String> status = getStatusByType(TaskUtils.TaskType.ALL).stream().sorted().collect(Collectors.toMap(Function.identity(),
                                                                                                                              Function.identity()));
        view.addSelectFilter(constants.Status(),
                             status,
                             false,
                             v -> addAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                   v)),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_STATUS,
                                                                      v))
        );

        view.addTextFilter(constants.Process_Instance_Correlation_Key(),
                           constants.FilterByCorrelationKey(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Actual_Owner(),
                           constants.FilterByActualOwner(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_ACTUAL_OWNER,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_ACTUAL_OWNER,
                                                                  v,
                                                                  false))
        );

        view.addTextFilter(constants.Process_Instance_Description(),
                           constants.FilterByProcessInstanceDescription(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                               v,
                                                               false)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                                  v,
                                                                  false))
        );

        addProcessNameFilter(getDataSetId());

        view.addDateRangeFilter(constants.Created_On(),
                                constants.Created_On_Placeholder(),
                                true,
                                v -> addAdvancedSearchFilter(between(COLUMN_CREATED_ON,
                                                                     v.getStartDate(),
                                                                     v.getEndDate())),
                                v -> removeAdvancedSearchFilter(between(COLUMN_CREATED_ON,
                                                                        v.getStartDate(),
                                                                        v.getEndDate()))
        );
    }

    protected void addProcessNameFilter(final String dataSetId) {
        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(dataSetId)
                .group(COLUMN_PROCESS_ID)
                .column(COLUMN_PROCESS_ID)
                .sort(COLUMN_PROCESS_ID,
                      SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Process_Definition_Id(),
                                    AbstractMultiGridView.TAB_SEARCH,
                                    dataSetLookup,
                                    COLUMN_PROCESS_ID,
                                    COLUMN_PROCESS_ID,
                                    v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_ID,
                                                                          v)),
                                    v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_ID,
                                                                             v)));
    }

    public abstract String getDataSetId();
}
