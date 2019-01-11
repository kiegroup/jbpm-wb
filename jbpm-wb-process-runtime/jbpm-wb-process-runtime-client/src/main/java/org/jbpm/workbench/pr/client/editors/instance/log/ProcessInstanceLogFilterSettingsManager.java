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

package org.jbpm.workbench.pr.client.editors.instance.log;

import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManagerImpl;

import static java.util.Collections.emptyList;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;

import static org.jbpm.workbench.pr.model.ProcessInstanceLogDataSetConstants.*;

@Dependent
public class ProcessInstanceLogFilterSettingsManager extends FilterSettingsManagerImpl {

    protected static final String LOGS_LIST_PREFIX = "DS_ProcessInstanceLogListGrid";

    @Override
    public String getGridGlobalPreferencesKey() {
        return LOGS_LIST_PREFIX;
    }

    @Override
    public FilterSettings createFilterSettingsPrototype() {
        return createFilterSettings(PROCESS_INSTANCE_LOGS_DATASET,
                                    getSortByMap(),
                                    null);
    }

    public FilterSettings createDefaultFilterSettingsPrototype(Long processInstanceId) {
        final FilterSettings filterSettings = createFilterSettings(PROCESS_INSTANCE_LOGS_DATASET,
                                                                   getSortByMap(),
                                                                   builder -> {
                                                                       builder.filter(equalsTo(COLUMN_LOG_PROCESS_INSTANCE_ID,
                                                                                               processInstanceId));
                                                                   }
        );
        filterSettings.setTableDefaultSortColumnId("");
        filterSettings.setKey(getDefaultFilterSettingsKey());
        return filterSettings;
    }

    private HashMap<String, SortOrder> getSortByMap() {
        HashMap<String, SortOrder> multipleSortBy = new HashMap<>();
        multipleSortBy.put(COLUMN_LOG_DATE,
                           SortOrder.DESCENDING);
        multipleSortBy.put(COLUMN_LOG_ID,
                           SortOrder.DESCENDING);
        multipleSortBy.put(COLUMN_LOG_TYPE,
                           SortOrder.DESCENDING);
        return multipleSortBy;
    }

    @Override
    public List<FilterSettings> initDefaultFilters() {
        return emptyList();
    }
}
