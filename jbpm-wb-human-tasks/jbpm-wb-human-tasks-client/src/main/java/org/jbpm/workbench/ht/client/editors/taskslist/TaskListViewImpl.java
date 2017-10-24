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

import javax.enterprise.context.Dependent;

import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

import java.util.Arrays;
import java.util.List;

@Dependent
public class TaskListViewImpl extends AbstractTaskListView<TaskListPresenter> {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskListGrid";
    protected static final String TAB_ALL = DATA_SET_TASK_LIST_PREFIX + "_3";
    protected static final String TAB_GROUP = DATA_SET_TASK_LIST_PREFIX + "_2";
    protected static final String TAB_PERSONAL = DATA_SET_TASK_LIST_PREFIX + "_1";
    protected static final String TAB_ACTIVE = DATA_SET_TASK_LIST_PREFIX + "_0";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_4";

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COLUMN_NAME,
                             COLUMN_PROCESS_ID,
                             COLUMN_STATUS,
                             COLUMN_CREATED_ON,
                             COL_ID_ACTIONS);
    }

    @Override
    protected void loadTabsFromPreferences(final MultiGridPreferencesStore multiGridPreferencesStore,
                                           final TaskListPresenter presenter) {
        //Remove old Admin tab in case still in the user preferences
        multiGridPreferencesStore.getGridsId().remove(TAB_ADMIN);

        super.loadTabsFromPreferences(multiGridPreferencesStore,
                                      presenter);
    }

    @Override
    public void initDefaultFilters() {
        super.initDefaultFilters();

        //Filter status Active
        initTabFilter(presenter.createActiveTabSettings(),
                      TAB_ACTIVE,
                      Constants.INSTANCE.Active(),
                      Constants.INSTANCE.FilterActive(),
                      HUMAN_TASKS_WITH_USER_DATASET);

        //Filter status Personal
        initTabFilter(presenter.createPersonalTabSettings(),
                      TAB_PERSONAL,
                      Constants.INSTANCE.Personal(),
                      Constants.INSTANCE.FilterPersonal(),
                      HUMAN_TASKS_WITH_USER_DATASET);

        //Filter status Group
        initTabFilter(presenter.createGroupTabSettings(),
                      TAB_GROUP,
                      Constants.INSTANCE.Group(),
                      Constants.INSTANCE.FilterGroup(),
                      HUMAN_TASKS_WITH_USER_DATASET);

        //Filter status All
        initTabFilter(presenter.createAllTabSettings(),
                      TAB_ALL,
                      Constants.INSTANCE.All(),
                      Constants.INSTANCE.FilterAll(),
                      HUMAN_TASKS_WITH_USER_DATASET);

    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }
}