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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;

import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.*;

@Dependent
public class ProcessDefinitionListFilterSettingsManager extends FilterSettingsManagerImpl {

    protected static final String TAB_ALL = PROCESS_DEFINITION_LIST_PREFIX + "_0";
    private Constants constants = Constants.INSTANCE;

    @Override
    protected String getGridGlobalPreferencesKey() {
        return PROCESS_DEFINITION_LIST_PREFIX;
    }

    @Override
    protected List<FilterSettings> initDefaultFilters() {
        FilterSettings filterSettings = createFilterSettings(PROCESS_DEFINITION_DATASET, COL_ID_PROCESSNAME, null);
        filterSettings.setKey(TAB_ALL);
        filterSettings.setTableName(constants.All());
        filterSettings.setTableDescription(constants.FilterAll());
        return Arrays.asList(filterSettings);
    }

    @Override
    public FilterSettings createFilterSettingsPrototype() {
        FilterSettings filterSettings = createFilterSettings(PROCESS_DEFINITION_DATASET, COL_ID_PROCESSNAME, null);
        filterSettings.setKey(getGridGlobalPreferencesKey());
        return filterSettings;
    }
}
