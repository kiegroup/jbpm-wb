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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchScreen;

import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_LIST_BASIC_FILTERS_SCREEN;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.HUMAN_TASKS_WITH_USER_DATASET;

@Dependent
@WorkbenchScreen(identifier = TASK_LIST_BASIC_FILTERS_SCREEN)
public class TaskListBasicFiltersPresenter extends AbstractTaskListBasicFiltersPresenter {

    @Override
    public String getDataSetId() {
        return HUMAN_TASKS_WITH_USER_DATASET;
    }

    @Inject
    public void setFilterSettingsManager(final TaskListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }
}
