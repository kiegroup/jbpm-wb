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

package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.filters.BasicFiltersPresenter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.util.ExecutionErrorType;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.EXECUTION_ERROR_LIST_BASIC_FILTERS_SCREEN;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = EXECUTION_ERROR_LIST_BASIC_FILTERS_SCREEN)
public class ExecutionErrorListBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    protected String getAdvancedFilterPopupTitle() {
        return constants.New_ErrorList();
    }

    @Override
    public void loadFilters() {
        view.addNumericFilter(constants.Process_Instance_Id(),
                              constants.FilterByProcessInstanceId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INST_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_PROCESS_INST_ID,
                                                                       v))
        );

        view.addNumericFilter(constants.JobId(),
                              constants.FilterByJobId(),
                              v -> addAdvancedSearchFilter(equalsTo(COLUMN_JOB_ID,
                                                                    v)),
                              v -> removeAdvancedSearchFilter(equalsTo(COLUMN_JOB_ID,
                                                                       v))
        );

        view.addTextFilter(constants.Id(),
                           constants.FilterByErrorId(),
                           v -> addAdvancedSearchFilter(likeTo(COLUMN_ERROR_ID,
                                                               v)),
                           v -> removeAdvancedSearchFilter(likeTo(COLUMN_ERROR_ID,
                                                                  v))
        );

        final Map<String, String> states = new HashMap<>();
        states.put(ExecutionErrorType.DB.getType(),
                   constants.DB());
        states.put(ExecutionErrorType.TASK.getType(),
                   constants.Task());
        states.put(ExecutionErrorType.PROCESS.getType(),
                   constants.Process());
        states.put(ExecutionErrorType.JOB.getType(),
                   constants.Job());
        view.addSelectFilter(constants.Type(),
                             states,
                             false,
                             v -> addAdvancedSearchFilter(equalsTo(COLUMN_ERROR_TYPE,
                                                                   v)),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_ERROR_TYPE,
                                                                      v))
        );

        final Map<String, String> acks = new HashMap<>();
        final org.jbpm.workbench.common.client.resources.i18n.Constants constants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;
        acks.put("1",
                 constants.Yes());
        acks.put("0",
                 constants.No());
        view.addSelectFilter(this.constants.Acknowledged(),
                             acks,
                             false,
                             v -> addAdvancedSearchFilter(equalsTo(COLUMN_ERROR_ACK,
                                                                   v)),
                             v -> removeAdvancedSearchFilter(equalsTo(COLUMN_ERROR_ACK,
                                                                      v))
        );

        view.addDateRangeFilter(this.constants.ErrorDate(),
                                this.constants.ErrorDatePlaceholder(),
                                true,
                                v -> addAdvancedSearchFilter(between(COLUMN_ERROR_DATE,
                                                                     v.getStartDate(),
                                                                     v.getEndDate())),
                                v -> removeAdvancedSearchFilter(between(COLUMN_ERROR_DATE,
                                                                        v.getStartDate(),
                                                                        v.getEndDate()))
        );
    }
}
