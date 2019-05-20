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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.ht.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
public class TaskListFilterSettingsManager extends AbstractTaskListFilterSettingsManager {

    protected static final String DATA_SET_TASK_LIST_PREFIX = "DataSetTaskListGrid";
    protected static final String TAB_ALL = DATA_SET_TASK_LIST_PREFIX + "_3";
    protected static final String TAB_GROUP = DATA_SET_TASK_LIST_PREFIX + "_2";
    protected static final String TAB_PERSONAL = DATA_SET_TASK_LIST_PREFIX + "_1";
    protected static final String TAB_ACTIVE = DATA_SET_TASK_LIST_PREFIX + "_0";
    protected static final String TAB_ADMIN = DATA_SET_TASK_LIST_PREFIX + "_4";

    private User identity;

    @Inject
    public void setIdentity(final User identity) {
        this.identity = identity;
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return DATA_SET_TASK_LIST_PREFIX;
    }

    @Override
    public void loadSavedFiltersFromPreferences(final MultiGridPreferencesStore store,
                                                final Consumer<List<SavedFilter>> savedFiltersConsumer) {
        final ArrayList<String> existingGrids = new ArrayList<>(store.getGridsId());

        //Remove old Admin tab in case still in the user preferences
        if (existingGrids.contains(TAB_ADMIN)) {
            removeSavedFilterFromPreferences(TAB_ADMIN,
                                             store,
                                             () -> loadSavedFiltersFromPreferences(store,
                                                                                   savedFiltersConsumer));
        } else if (existingGrids.contains(TAB_ALL)) {
            removeSavedFilterFromPreferences(TAB_ALL,
                                             store,
                                             () -> loadSavedFiltersFromPreferences(store,
                                                                                   savedFiltersConsumer));
        } else {
            super.loadSavedFiltersFromPreferences(store,
                                                  savedFiltersConsumer);
        }
    }

    @Override
    public FilterSettings createFilterSettingsPrototype() {
        return createFilterSettings(HUMAN_TASKS_WITH_USER_DATASET,
                                    COLUMN_CREATED_ON,
                                    builder -> {
                                        builder.group(COLUMN_TASK_ID);
                                        commonColumnSettings().accept(builder);
                                    });
    }

    @Override
    public List<FilterSettings> initDefaultFilters() {
        return Arrays.asList(

                //Filter status Active
                createFilterSettings(HUMAN_TASKS_WITH_USER_DATASET,
                                     COLUMN_CREATED_ON,
                                     builder -> {

                                         final List<Comparable> status = new ArrayList<>(getStatusByType(TaskType.ACTIVE));

                                         builder.filter(COLUMN_STATUS,
                                                        equalsTo(COLUMN_STATUS,
                                                                 status));
                                         builder.group(COLUMN_TASK_ID);
                                         commonColumnSettings().accept(builder);
                                     },
                                     getDefaultFilterSettingsKey(), // initial default filter,
                                     constants.Active(),
                                     constants.FilterActive()),

                //Filter status Personal
                createFilterSettings(HUMAN_TASKS_DATASET,
                                     COLUMN_CREATED_ON,
                                     builder -> {
                                         final List<Comparable> names = new ArrayList<>(getStatusByType(TaskType.PERSONAL));
                                         builder.filter(equalsTo(COLUMN_STATUS,
                                                                 names));
                                         builder.filter(equalsTo(COLUMN_ACTUAL_OWNER,
                                                                 identity.getIdentifier()));
                                         commonColumnSettings().accept(builder);
                                     },
                                     TAB_PERSONAL,
                                     constants.Personal(),
                                     constants.FilterPersonal()),

                //Filter status Group
                createFilterSettings(HUMAN_TASKS_WITH_USER_DATASET,
                                     COLUMN_CREATED_ON,
                                     builder -> {
                                         final List<Comparable> names = new ArrayList<>(getStatusByType(TaskType.GROUP));
                                         builder.filter(COLUMN_STATUS,
                                                        equalsTo(COLUMN_STATUS,
                                                                 names));

                                         builder.filter(COLUMN_ACTUAL_OWNER,
                                                        OR(equalsTo(""),
                                                           isNull()));

                                         builder.group(COLUMN_TASK_ID);
                                         commonColumnSettings().accept(builder);
                                     },
                                     TAB_GROUP,
                                     constants.Group(),
                                     constants.FilterGroup())

        );
    }
}
