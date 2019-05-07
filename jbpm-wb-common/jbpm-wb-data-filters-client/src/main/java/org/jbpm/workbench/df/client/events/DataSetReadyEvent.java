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

package org.jbpm.workbench.df.client.events;

import org.jbpm.workbench.df.client.filter.FilterSettings;

public class DataSetReadyEvent {

    private FilterSettings filterSettings;
    
    private String dataSetUUID;

    public DataSetReadyEvent(FilterSettings filterSettings, String dataSetUUID) {
        this.filterSettings = filterSettings;
        this.dataSetUUID = dataSetUUID;
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public String getDataSetUUID() {
        return dataSetUUID;
    }

    @Override
    public String toString() {
        return "DataSetReadyEvent{" +
                "filterSettings=" + filterSettings +
                ", dataSetUUID='" + dataSetUUID + '\'' +
                '}';
    }
}
