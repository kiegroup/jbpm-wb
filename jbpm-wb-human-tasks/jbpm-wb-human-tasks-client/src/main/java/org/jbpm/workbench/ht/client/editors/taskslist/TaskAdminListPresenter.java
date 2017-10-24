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
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.TASK_ADMIN_LIST_SCREEN)
public class TaskAdminListPresenter extends AbstractTaskListPresenter<TaskAdminListViewImpl> {

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_Admin();
    }

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public void setupAdvancedSearchView() {
        super.setupAdvancedSearchView();
        addProcessNameFilter(HUMAN_TASKS_WITH_ADMIN_DATASET);
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createStatusSettings(HUMAN_TASKS_WITH_ADMIN_DATASET,
                                    null);
    }

    public FilterSettings createAdminTabSettings() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_ADMIN_DATASET);
        List<Comparable> status = new ArrayList<>(getStatusByType(TaskUtils.TaskType.ADMIN));
        if (status != null) {
            builder.filter(COLUMN_STATUS,
                           equalsTo(COLUMN_STATUS,
                                    status));
        }
        builder.group(COLUMN_TASK_ID);

        addCommonColumnSettings(builder);
        return builder.buildSettings();
    }
    
    @Override
    protected void addCommonColumnSettings(FilterSettingsBuilderHelper builder) {
        builder.setColumn(COLUMN_ERROR_COUNT,
                          constants.Error_Count());
        super.addCommonColumnSettings(builder);
    }

    @Override
    protected Predicate<TaskSummary> getSuspendActionCondition() {
        return task -> {
            String taskStatus = task.getStatus();
            return (taskStatus.equals(TASK_STATUS_RESERVED) ||
                    taskStatus.equals(TASK_STATUS_IN_PROGRESS) ||
                    taskStatus.equals(TASK_STATUS_READY));
        };
    }

    @Override
    protected Predicate<TaskSummary> getResumeActionCondition() {
        return task -> TASK_STATUS_SUSPENDED.equals(task.getStatus());
    }
}