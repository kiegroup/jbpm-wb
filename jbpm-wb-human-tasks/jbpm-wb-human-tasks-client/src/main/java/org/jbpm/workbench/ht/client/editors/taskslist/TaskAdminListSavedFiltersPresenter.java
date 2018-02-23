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
import java.util.List;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.filters.SavedFiltersPresenter;
import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_ADMIN_LIST_SAVED_FILTERS_SCREEN;
import static org.jbpm.workbench.common.client.util.TaskUtils.getStatusByType;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = TASK_ADMIN_LIST_SAVED_FILTERS_SCREEN)
public class TaskAdminListSavedFiltersPresenter extends SavedFiltersPresenter {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskAdminGrid";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_0";

    private Constants constants = Constants.INSTANCE;

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createFilterSettings(HUMAN_TASKS_WITH_ADMIN_DATASET,
                                    COLUMN_CREATED_ON,
                                    builder -> builder.group(COLUMN_TASK_ID));
    }

    @Override
    public void initDefaultFilters() {
        //Filter open tasks
        initSavedFilter(HUMAN_TASKS_WITH_ADMIN_DATASET,
                        COLUMN_CREATED_ON,
                        builder -> {
                            final List<Comparable> status = new ArrayList<>(getStatusByType(TaskUtils.TaskType.ADMIN));
                            builder.filter(COLUMN_STATUS,
                                           equalsTo(COLUMN_STATUS,
                                                    status));
                            builder.group(COLUMN_TASK_ID);
                        },
                        TAB_ADMIN,
                        constants.Task_Admin(),
                        constants.FilterTaskAdmin());
    }
}
