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

import java.util.function.Predicate;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.util.TaskUtils.*;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.TASK_ADMIN_LIST_SCREEN)
public class TaskAdminListPresenter extends AbstractTaskListPresenter<TaskAdminListViewImpl> {

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.Manage_Tasks());
    }

    @Override
    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.Manage_Tasks(),
                              detailLabel,
                              PerspectiveIds.TASK_DETAILS_SCREEN);
    }

    @Inject
    public void setFilterSettingsManager(final TaskAdminListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
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