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

import org.gwtbootstrap3.client.ui.Button;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

@Dependent
public class TaskAdminListViewImpl extends AbstractTaskListView<TaskAdminListPresenter> {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskAdminGrid";
    private static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_0";

    @Override
    public void initDefaultFilters(final GridGlobalPreferences preferences,
                                   final Button createTabButton) {
        super.initDefaultFilters(preferences,
                                 createTabButton);

        initFilterTab(presenter.createAdminTabSettings(),
                      TAB_ADMIN,
                      constants.Task_Admin(),
                      constants.FilterTaskAdmin(),
                      preferences);

        filterPagedTable.addAddTableButton(createTabButton);
        selectFirstTabAndEnableQueries();
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