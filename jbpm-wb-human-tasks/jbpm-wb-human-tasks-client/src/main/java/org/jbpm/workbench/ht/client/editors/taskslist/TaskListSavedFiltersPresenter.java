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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.filters.SavedFiltersPresenter;
import org.jbpm.workbench.common.client.util.TaskUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_LIST_SAVED_FILTERS_SCREEN;
import static org.jbpm.workbench.common.client.util.TaskUtils.getStatusByType;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = TASK_LIST_SAVED_FILTERS_SCREEN)
public class TaskListSavedFiltersPresenter extends SavedFiltersPresenter {

    private static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskListGrid";
    protected static final String TAB_ALL = DATA_SET_TASK_LIST_PREFIX + "_3";
    protected static final String TAB_GROUP = DATA_SET_TASK_LIST_PREFIX + "_2";
    protected static final String TAB_PERSONAL = DATA_SET_TASK_LIST_PREFIX + "_1";
    protected static final String TAB_ACTIVE = DATA_SET_TASK_LIST_PREFIX + "_0";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_4";

    private Constants constants = Constants.INSTANCE;

    private User identity;

    @Inject
    public void setIdentity(final User identity) {
        this.identity = identity;
    }

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

    @Override
    protected void loadSavedFiltersFromPreferences() {
        //Remove old Admin tab in case still in the user preferences
        final ArrayList<String> existingGrids = new ArrayList<>(multiGridPreferencesStore.getGridsId());

        if (existingGrids.contains(TAB_ADMIN)) {
            removeSavedFilterFromPreferences(TAB_ADMIN);
            existingGrids.remove(TAB_ADMIN);
        }

        super.loadSavedFiltersFromPreferences();
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createFilterSettings(HUMAN_TASKS_WITH_USER_DATASET,
                                    COLUMN_CREATED_ON,
                                    builder -> builder.group(COLUMN_TASK_ID));
    }

    @Override
    public void initDefaultFilters() {
        //Filter status Active
        initSavedFilter(HUMAN_TASKS_WITH_USER_DATASET,
                        COLUMN_CREATED_ON,
                        builder -> {
                            final List<Comparable> status = new ArrayList<>(getStatusByType(TaskUtils.TaskType.ACTIVE));
                            builder.filter(COLUMN_STATUS,
                                           equalsTo(COLUMN_STATUS,
                                                    status));
                            builder.group(COLUMN_TASK_ID);
                        },
                        TAB_ACTIVE,
                        constants.Active(),
                        constants.FilterActive());

        //Filter status Personal
        initSavedFilter(HUMAN_TASKS_DATASET,
                        COLUMN_CREATED_ON,
                        builder -> {
                            final List<Comparable> names = new ArrayList<>(getStatusByType(TaskUtils.TaskType.PERSONAL));
                            builder.filter(equalsTo(COLUMN_STATUS,
                                                    names));
                            builder.filter(equalsTo(COLUMN_ACTUAL_OWNER,
                                                    identity.getIdentifier()));
                        },
                        TAB_PERSONAL,
                        constants.Personal(),
                        constants.FilterPersonal());

        //Filter status Group
        initSavedFilter(HUMAN_TASKS_WITH_USER_DATASET,
                        COLUMN_CREATED_ON,
                        builder -> {
                            final List<Comparable> names = new ArrayList<>(getStatusByType(TaskUtils.TaskType.GROUP));
                            builder.filter(COLUMN_STATUS,
                                           equalsTo(COLUMN_STATUS,
                                                    names));

                            builder.filter(COLUMN_ACTUAL_OWNER,
                                           OR(equalsTo(""),
                                              isNull()));

                            builder.group(COLUMN_TASK_ID);
                        },
                        TAB_GROUP,
                        constants.Group(),
                        constants.FilterGroup());

        //Filter status All
        initSavedFilter(HUMAN_TASKS_WITH_USER_DATASET,
                        COLUMN_CREATED_ON,
                        builder -> {
                            final List<Comparable> status = new ArrayList<>(getStatusByType(TaskUtils.TaskType.ALL));
                            builder.filter(COLUMN_STATUS,
                                           equalsTo(COLUMN_STATUS,
                                                    status));
                            builder.group(COLUMN_TASK_ID);
                        },
                        TAB_ALL,
                        constants.All(),
                        constants.FilterAll());
    }
}
