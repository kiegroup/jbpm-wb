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
package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
public class TaskAdminListViewImpl extends AbstractTaskListView<TaskAdminListPresenter> {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskAdminGrid";
    private static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_0";
    
    @Override
    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_ADMIN_DATASET);
        builder.group(COLUMN_TASK_ID);

        addCommonColumnSettings(builder);

        return builder.buildSettings();
    }

    @Override
    public void initDefaultFilters( GridGlobalPreferences preferences ) {
        super.initDefaultFilters(preferences);
        //Filter status Admin
        List<String> states = TaskUtils.getStatusByType( TaskUtils.TaskType.ADMIN );
        initAdminTabFilter( preferences, TAB_ADMIN, constants.Task_Admin(), constants.FilterTaskAdmin(), states);
    }

    private void initAdminTabFilter( GridGlobalPreferences preferences,
                                     final String key,
                                     String tabName,
                                     String tabDesc,
                                     List<String> states ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_ADMIN_DATASET);
        List<Comparable> names = new ArrayList<>(states);
        builder.filter( COLUMN_STATUS, equalsTo( COLUMN_STATUS, names ) );

        builder.group(COLUMN_TASK_ID);

        addCommonColumnSettings(builder);

        initFilterTab(builder, key, tabName, tabDesc, preferences );
    }
    
    @Override
    public void resetDefaultFilterTitleAndDescription() {
        super.resetDefaultFilterTitleAndDescription();
        saveTabSettings(TAB_ADMIN,
                constants.Task_Admin(),
                constants.FilterTaskAdmin());
    }

    @Override
    public String getDataSetTaskListPrefix() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

}