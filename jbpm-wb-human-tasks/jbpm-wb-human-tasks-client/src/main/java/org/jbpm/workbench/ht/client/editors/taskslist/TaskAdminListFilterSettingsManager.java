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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.ht.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
public class TaskAdminListFilterSettingsManager extends AbstractTaskListFilterSettingsManager {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskAdminGrid";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_0";

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

    @Override
    protected Consumer<FilterSettingsBuilderHelper> commonColumnSettings() {
        return builder -> {
            super.commonColumnSettings().accept(builder);
            builder.setColumn(COLUMN_ERROR_COUNT,
                              constants.Error_Count());
        };
    }

    @Override
    public FilterSettings createFilterSettingsPrototype() {
        return createFilterSettings(HUMAN_TASKS_WITH_ADMIN_DATASET,
                                    COLUMN_CREATED_ON,
                                    builder -> {
                                        builder.group(COLUMN_TASK_ID);
                                        commonColumnSettings().accept(builder);
                                    });
    }

    @Override
    public List<FilterSettings> initDefaultFilters() {
        return Arrays.asList(
                //Filter open tasks
                createFilterSettings(HUMAN_TASKS_WITH_ADMIN_DATASET,
                                     COLUMN_CREATED_ON,
                                     builder -> {
                                         final List<Comparable> status = new ArrayList<>(getStatusByType(TaskType.ADMIN));
                                         builder.filter(COLUMN_STATUS,
                                                        equalsTo(COLUMN_STATUS,
                                                                 status));
                                         builder.group(COLUMN_TASK_ID);
                                         commonColumnSettings().accept(builder);
                                     },
                                     TAB_ADMIN,
                                     constants.Task_Admin(),
                                     constants.FilterTaskAdmin())
        );
    }
}
