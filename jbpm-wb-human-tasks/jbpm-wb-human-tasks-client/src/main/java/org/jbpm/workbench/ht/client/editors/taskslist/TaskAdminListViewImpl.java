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

import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_ADMIN_DATASET;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class TaskAdminListViewImpl extends AbstractTaskListView<TaskAdminListPresenter> {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskAdminGrid";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_0";

    @Override
    public void initDefaultFilters() {
        super.initDefaultFilters();

        initTabFilter(presenter.createAdminTabSettings(),
                      TAB_ADMIN,
                      constants.Task_Admin(),
                      constants.FilterTaskAdmin(),
                      HUMAN_TASKS_WITH_ADMIN_DATASET);

    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

}