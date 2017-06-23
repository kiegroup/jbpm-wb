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

import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = TaskListPresenter.SCREEN_ID)
public class TaskListPresenter extends AbstractTaskListPresenter<TaskListViewImpl> {

    public static final String SCREEN_ID = "Tasks List";

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Tasks_List();
    }

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public void setupAdvancedSearchView() {
        super.setupAdvancedSearchView();
        addProcessNameFilter(HUMAN_TASKS_WITH_USER_DATASET);
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createStatusSettings(HUMAN_TASKS_WITH_USER_DATASET, null);
    }

    public FilterSettings createGroupTabSettings() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_WITH_USER_DATASET);
        List<Comparable> names = new ArrayList<>(getStatusByType(TaskUtils.TaskType.GROUP));
        builder.filter(COLUMN_STATUS,
                       equalsTo(COLUMN_STATUS,
                                names));

        builder.filter(COLUMN_ACTUAL_OWNER,
                       OR(equalsTo(""),
                          isNull()));

        builder.group(COLUMN_TASK_ID);

        addCommonColumnSettings(builder);

        return builder.buildSettings();
    }

    public FilterSettings createPersonalTabSettings() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(HUMAN_TASKS_DATASET);
        List<Comparable> names = new ArrayList<>(getStatusByType(TaskUtils.TaskType.PERSONAL));
        builder.filter(equalsTo(COLUMN_STATUS,
                                names));
        builder.filter(equalsTo(COLUMN_ACTUAL_OWNER,
                                identity.getIdentifier()));

        addCommonColumnSettings(builder);

        return builder.buildSettings();
    }

    public FilterSettings createAllTabSettings() {
        return createStatusSettings(HUMAN_TASKS_WITH_USER_DATASET, new ArrayList<>(getStatusByType(TaskUtils.TaskType.ALL)));
    }

    public FilterSettings createActiveTabSettings() {
        return createStatusSettings(HUMAN_TASKS_WITH_USER_DATASET, new ArrayList<>(getStatusByType(TaskUtils.TaskType.ACTIVE)));
    }

    @Override
    protected Predicate<TaskSummary> getSuspendActionCondition() {
        return task -> TASK_STATUS_RESERVED.equals(task.getStatus()) || TASK_STATUS_IN_PROGRESS.equals(task.getStatus());
    }

    @Override
    protected Predicate<TaskSummary> getResumeActionCondition() {
        return task -> TASK_STATUS_SUSPENDED.equals(task.getStatus());
    }

    @Override
    protected Predicate<TaskSummary> getReleaseActionCondition() {
        return task -> task.getActualOwner() != null && task.getActualOwner().equals(identity.getIdentifier())
                && super.getReleaseActionCondition().test(task);
    }

}