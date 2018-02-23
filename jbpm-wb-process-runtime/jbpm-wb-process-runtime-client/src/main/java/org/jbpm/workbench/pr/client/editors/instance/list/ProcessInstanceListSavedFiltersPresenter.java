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

package org.jbpm.workbench.pr.client.editors.instance.list;

import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.filters.SavedFiltersPresenter;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_INSTANCE_LIST_SAVED_FILTERS_SCREEN;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = PROCESS_INSTANCE_LIST_SAVED_FILTERS_SCREEN)
public class ProcessInstanceListSavedFiltersPresenter extends SavedFiltersPresenter {

    protected static final String TAB_ACTIVE = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0";
    protected static final String TAB_COMPLETED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1";
    protected static final String TAB_ABORTED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2";

    private Constants constants = Constants.INSTANCE;

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX;
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createFilterSettings(PROCESS_INSTANCE_DATASET,
                                    COLUMN_START,
                                    null);
    }

    @Override
    public void initDefaultFilters() {
        //Filter status Active
        initSavedFilter(PROCESS_INSTANCE_DATASET,
                        COLUMN_START,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           ProcessInstance.STATE_ACTIVE)),
                        TAB_ACTIVE,
                        constants.Active(),
                        constants.FilterActive());

        //Filter status completed
        initSavedFilter(PROCESS_INSTANCE_DATASET,
                        COLUMN_START,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           ProcessInstance.STATE_COMPLETED)),
                        TAB_COMPLETED,
                        constants.Completed(),
                        constants.FilterCompleted());

        //Filter status aborted
        initSavedFilter(PROCESS_INSTANCE_DATASET,
                        COLUMN_START,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           ProcessInstance.STATE_ABORTED)),
                        TAB_ABORTED,
                        constants.Aborted(),
                        constants.FilterAborted());
    }
}
