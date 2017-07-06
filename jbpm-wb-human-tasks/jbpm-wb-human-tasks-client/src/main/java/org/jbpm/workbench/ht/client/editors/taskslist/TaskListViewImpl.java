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
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

@Dependent
public class TaskListViewImpl extends AbstractTaskListView<TaskListPresenter> {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskListGrid";
    private static final String TAB_ALL = DATA_SET_TASK_LIST_PREFIX + "_3";
    private static final String TAB_GROUP = DATA_SET_TASK_LIST_PREFIX + "_2";
    private static final String TAB_PERSONAL = DATA_SET_TASK_LIST_PREFIX + "_1";
    private static final String TAB_ACTIVE = DATA_SET_TASK_LIST_PREFIX + "_0";

    @Override
    public void initDefaultFilters(final GridGlobalPreferences preferences,
                                   final Button createTabButton) {
        super.initDefaultFilters(preferences,
                                 createTabButton);

        //Filter status Active
        initFilterTab(presenter.createActiveTabSettings(),
                      TAB_ACTIVE,
                      Constants.INSTANCE.Active(),
                      Constants.INSTANCE.FilterActive(),
                      preferences);

        //Filter status Personal
        initFilterTab(presenter.createPersonalTabSettings(),
                      TAB_PERSONAL,
                      Constants.INSTANCE.Personal(),
                      Constants.INSTANCE.FilterPersonal(),
                      preferences);

        //Filter status Group
        initFilterTab(presenter.createGroupTabSettings(),
                      TAB_GROUP,
                      Constants.INSTANCE.Group(),
                      Constants.INSTANCE.FilterGroup(),
                      preferences);

        //Filter status All
        initFilterTab(presenter.createAllTabSettings(),
                      TAB_ALL,
                      Constants.INSTANCE.All(),
                      Constants.INSTANCE.FilterAll(),
                      preferences);

        filterPagedTable.addAddTableButton(createTabButton);
    }

    @Override
    public void resetDefaultFilterTitleAndDescription() {
        super.resetDefaultFilterTitleAndDescription();
        saveTabSettings(TAB_ACTIVE,
                        constants.Active(),
                        constants.FilterActive());
        saveTabSettings(TAB_PERSONAL,
                        constants.Personal(),
                        constants.FilterPersonal());
        saveTabSettings(TAB_GROUP,
                        constants.Group(),
                        constants.FilterGroup());
        saveTabSettings(TAB_ALL,
                        constants.All(),
                        constants.FilterAll());
    }

    @Override
    public String getDataSetTaskListPrefix() {
        return DATA_SET_TASK_LIST_PREFIX;
    }
}