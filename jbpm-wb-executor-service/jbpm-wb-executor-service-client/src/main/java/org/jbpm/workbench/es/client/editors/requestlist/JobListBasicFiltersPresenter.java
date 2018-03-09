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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.filters.basic.BasicFiltersPresenter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.util.RequestStatus;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.workbench.common.client.PerspectiveIds.JOB_LIST_BASIC_FILTERS_SCREEN;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
@WorkbenchScreen(identifier = JOB_LIST_BASIC_FILTERS_SCREEN)
public class JobListBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    protected String getAdvancedFilterPopupTitle() {
        return constants.New_JobList();
    }

    @Inject
    public void setFilterSettingsManager(final JobListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }

    @Override
    public void loadFilters() {
        final Map<String, String> status = new HashMap<>();
        status.put(RequestStatus.CANCELLED.name(),
                   constants.Canceled());
        status.put(RequestStatus.DONE.name(),
                   constants.Completed());
        status.put(RequestStatus.ERROR.name(),
                   constants.Error());
        status.put(RequestStatus.QUEUED.name(),
                   constants.Queued());
        status.put(RequestStatus.RETRYING.name(),
                   constants.Retrying());
        status.put(RequestStatus.RUNNING.name(),
                   constants.Running());

        view.addSelectFilter(constants.Status(),
                             status,
                             false,
                             f -> addSearchFilter(f,
                                                  equalsTo(COLUMN_STATUS,
                                                           f.getValue()))
        );

        final DataSetLookup dataSetLookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(REQUEST_LIST_DATASET)
                .group(COLUMN_PROCESS_NAME)
                .column(COLUMN_PROCESS_NAME)
                .sort(COLUMN_PROCESS_NAME,
                      SortOrder.ASCENDING)
                .buildLookup();
        view.addDataSetSelectFilter(constants.Process_Name(),
                                    dataSetLookup,
                                    COLUMN_PROCESS_NAME,
                                    COLUMN_PROCESS_NAME,
                                    f -> addSearchFilter(f,
                                                         equalsTo(COLUMN_PROCESS_NAME,
                                                                  f.getValue())));

        view.addNumericFilter(constants.Process_Instance_Id(),
                              constants.FilterByProcessInstanceId(),
                              f -> addSearchFilter(f,
                                                   equalsTo(COLUMN_PROCESS_INSTANCE_ID,
                                                            f.getValue()))
        );

        view.addTextFilter(constants.BusinessKey(),
                           constants.FilterByBusinessKey(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_BUSINESSKEY,
                                                       f.getValue(),
                                                       false))
        );

        view.addTextFilter(constants.Type(),
                           constants.FilterByType(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_COMMANDNAME,
                                                       f.getValue(),
                                                       false))
        );

        view.addTextFilter(constants.Process_Instance_Description(),
                           constants.FilterByProcessDescription(),
                           f -> addSearchFilter(f,
                                                likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                       f.getValue(),
                                                       false))
        );

        view.addDateRangeFilter(constants.Due_On(),
                                constants.Due_On_Placeholder(),
                                false,
                                f -> addSearchFilter(f,
                                                     between(COLUMN_TIMESTAMP,
                                                             f.getValue().getStartDate(),
                                                             f.getValue().getEndDate()))
        );
    }
}
