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

import java.util.function.Consumer;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListFilterSettingsManager extends FilterSettingsManagerImpl {

    protected Constants constants = Constants.INSTANCE;

    protected Consumer<FilterSettingsBuilderHelper> commonColumnSettings() {
        return builder -> {
            builder.setColumn(COLUMN_ACTIVATION_TIME,
                              constants.ActivationTime(),
                              DateUtils.getDateTimeFormatMask());
            builder.setColumn(COLUMN_ACTUAL_OWNER,
                              constants.Actual_Owner());
            builder.setColumn(COLUMN_CREATED_BY,
                              constants.CreatedBy());
            builder.setColumn(COLUMN_CREATED_ON,
                              constants.Created_On(),
                              DateUtils.getDateTimeFormatMask());
            builder.setColumn(COLUMN_DEPLOYMENT_ID,
                              constants.DeploymentId());
            builder.setColumn(COLUMN_DESCRIPTION,
                              constants.Description());
            builder.setColumn(COLUMN_DUE_DATE,
                              constants.DueDate(),
                              DateUtils.getDateTimeFormatMask());
            builder.setColumn(COLUMN_NAME,
                              constants.Task());
            builder.setColumn(COLUMN_PARENT_ID,
                              constants.ParentId());
            builder.setColumn(COLUMN_PRIORITY,
                              constants.Priority());
            builder.setColumn(COLUMN_PROCESS_ID,
                              constants.Process_Definition_Id());
            builder.setColumn(COLUMN_PROCESS_INSTANCE_ID,
                              constants.Process_Instance_Id());
            builder.setColumn(COLUMN_PROCESS_SESSION_ID,
                              constants.ProcessSessionId());
            builder.setColumn(COLUMN_STATUS,
                              constants.Status());
            builder.setColumn(COLUMN_TASK_ID,
                              constants.Id());
            builder.setColumn(COLUMN_WORK_ITEM_ID,
                              constants.WorkItemId());
            builder.setColumn(COLUMN_LAST_MODIFICATION_DATE,
                              constants.Last_Modification_Date());
            builder.setColumn(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                              constants.Process_Instance_Correlation_Key());
            builder.setColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                              constants.Process_Instance_Description());
        };
    }

    @Override
    public FilterSettings getVariablesFilterSettings(final String taskName) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_VARIABLES_DATASET);
        builder.filter(equalsTo(COLUMN_TASK_VARIABLE_TASK_NAME,
                                taskName));

        builder.filterOn(true,
                         true,
                         true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(COLUMN_TASK_ID,
                                  SortOrder.ASCENDING);

        FilterSettings varTableSettings = builder.buildSettings();
        varTableSettings.setTablePageSize(-1);

        return varTableSettings;
    }
}
