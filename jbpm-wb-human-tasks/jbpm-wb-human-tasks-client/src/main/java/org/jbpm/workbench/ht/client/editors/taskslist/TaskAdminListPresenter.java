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
import java.util.Collections;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.util.TaskUtils.TASK_STATUS_READY;
import static org.jbpm.workbench.common.client.util.TaskUtils.getStatusByType;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = TaskAdminListPresenter.SCREEN_ID)
public class TaskAdminListPresenter extends AbstractTaskListPresenter<TaskAdminListViewImpl> {

    public static final String SCREEN_ID = "Task Admin List";
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_Admin();
    }

    @WorkbenchMenu
    public Menus getMenus(){ //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public void setupAdvancedSearchView() {
        super.setupAdvancedSearchView();
        addProcessNameFilter(HUMAN_TASKS_WITH_ADMIN_DATASET);
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createStatusSettings(HUMAN_TASKS_WITH_ADMIN_DATASET, null);
    }

    public FilterSettings createAdminTabSettings(){
        //Filter status Admin
        return createStatusSettings(HUMAN_TASKS_WITH_ADMIN_DATASET, new ArrayList<>(getStatusByType(TaskUtils.TaskType.ADMIN)));
    }

    @Override
    public FilterSettings createSearchTabSettings() {
        return createStatusSettings(HUMAN_TASKS_WITH_ADMIN_DATASET, Collections.singletonList(TASK_STATUS_READY));
    }

}