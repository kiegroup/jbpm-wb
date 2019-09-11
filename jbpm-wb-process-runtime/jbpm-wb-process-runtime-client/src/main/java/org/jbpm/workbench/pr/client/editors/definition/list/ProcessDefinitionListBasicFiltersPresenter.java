/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.pr.client.editors.definition.list;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFiltersPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchScreen;

import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.*;

@ApplicationScoped
@WorkbenchScreen(identifier = PROCESS_DEFINITION_LIST_BASIC_FILTERS_SCREEN)
public class ProcessDefinitionListBasicFiltersPresenter extends BasicFiltersPresenter {

    private Constants constants = Constants.INSTANCE;

    @Override
    public void loadFilters() {
        view.addTextFilter(constants.Name(),
                           constants.Default_Process_Definition_Name(),
                           true,
                           f -> addSearchFilter(f, likeTo(COL_ID_PROCESSNAME, validateValue(f.getValue()), false))
        );
    }

    @Override
    public String getDataSetId() {
        return PROCESS_DEFINITION_DATASET;
    }

    @Override
    protected void onActiveFilterAdded(ActiveFilterItem activeFilterItem) {

    }

    private String validateValue(String value) {
        return value.replaceAll("\\*", ".*");
    }
}
