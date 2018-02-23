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

package org.jbpm.workbench.es.client.editors.requestlist;

import javax.enterprise.context.Dependent;

import org.jbpm.workbench.common.client.filters.SavedFiltersPresenter;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.util.RequestStatus;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.jbpm.workbench.common.client.PerspectiveIds.JOB_LIST_SAVED_FILTERS_SCREEN;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = JOB_LIST_SAVED_FILTERS_SCREEN)
public class JobListSavedFiltersPresenter extends SavedFiltersPresenter {

    protected static final String REQUEST_LIST_PREFIX = "DS_RequestListGrid";
    protected static final String TAB_CANCELED = REQUEST_LIST_PREFIX + "_6";
    protected static final String TAB_COMPLETED = REQUEST_LIST_PREFIX + "_5";
    protected static final String TAB_ERROR = REQUEST_LIST_PREFIX + "_4";
    protected static final String TAB_RETRYING = REQUEST_LIST_PREFIX + "_3";
    protected static final String TAB_RUNNING = REQUEST_LIST_PREFIX + "_2";
    protected static final String TAB_QUEUED = REQUEST_LIST_PREFIX + "_1";
    protected static final String TAB_ALL = REQUEST_LIST_PREFIX + "_0";

    private Constants constants = Constants.INSTANCE;

    @WorkbenchMenu
    public Menus getMenus() { //It's necessary to annotate with @WorkbenchMenu in subclass
        return super.getMenus();
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return REQUEST_LIST_PREFIX;
    }

    @Override
    public FilterSettings createTableSettingsPrototype() {
        return createFilterSettings(REQUEST_LIST_DATASET,
                                    COLUMN_TIMESTAMP,
                                    null);
    }

    @Override
    public void initDefaultFilters() {
        //Filter status All
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        null,
                        TAB_ALL,
                        constants.All(),
                        constants.FilterAll());

        //Filter status Queued
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.QUEUED.name())),
                        TAB_QUEUED,
                        constants.Queued(),
                        constants.FilterQueued());

        //Filter status Running
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.RUNNING.name())),
                        TAB_RUNNING,
                        constants.Running(),
                        constants.FilterRunning());

        //Filter status Retrying
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.RETRYING.name())),
                        TAB_RETRYING,
                        constants.Retrying(),
                        constants.FilterRetrying());

        //Filter status Error
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.ERROR.name())),
                        TAB_ERROR,
                        constants.Error(),
                        constants.FilterError());

        //Filter status Completed
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.DONE.name())),
                        TAB_COMPLETED,
                        constants.Completed(),
                        constants.FilterCompleted());

        //Filter status Canceled
        initSavedFilter(REQUEST_LIST_DATASET,
                        COLUMN_TIMESTAMP,
                        builder -> builder.filter(equalsTo(COLUMN_STATUS,
                                                           RequestStatus.CANCELLED.name())),
                        TAB_CANCELED,
                        constants.Canceled(),
                        constants.FilterCanceled());
    }
}
