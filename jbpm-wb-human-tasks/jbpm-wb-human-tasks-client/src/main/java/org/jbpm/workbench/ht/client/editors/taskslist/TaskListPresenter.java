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

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.util.TaskStatus;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.TASK_LIST_SCREEN)
public class TaskListPresenter extends AbstractTaskListPresenter<TaskListViewImpl> {

    @WorkbenchPartTitle
    public String getTitle() {
        return org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.Task_Inbox();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitleDecorator() {
        return null;
    }

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public void createListBreadcrumb() {
        setupListBreadcrumb(placeManager,
                            org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.Task_Inbox());
    }

    @Override
    public void setupDetailBreadcrumb(String detailLabel) {
        setupDetailBreadcrumb(placeManager,
                              org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE.Task_Inbox(),
                              detailLabel,
                              PerspectiveIds.TASK_DETAILS_SCREEN);
    }

    @Inject
    public void setFilterSettingsManager(final TaskListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    @Override
    protected Predicate<TaskSummary> getSuspendActionCondition() {
        return task -> TaskStatus.TASK_STATUS_RESERVED.equals(task.getTaskStatus()) || TaskStatus.TASK_STATUS_IN_PROGRESS.equals(task.getTaskStatus());
    }

    @Override
    protected Predicate<TaskSummary> getResumeActionCondition() {
        return task -> TaskStatus.TASK_STATUS_SUSPENDED.equals(task.getTaskStatus());
    }

    @Override
    protected Predicate<TaskSummary> getReleaseActionCondition() {
        return task -> task.getActualOwner() != null && task.getActualOwner().equals(identity.getIdentifier())
                && super.getReleaseActionCondition().test(task);
    }
}