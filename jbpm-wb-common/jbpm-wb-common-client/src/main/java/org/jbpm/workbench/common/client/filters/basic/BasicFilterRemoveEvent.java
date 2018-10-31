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

package org.jbpm.workbench.common.client.filters.basic;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;

public class BasicFilterRemoveEvent {

    private String dataSetId;

    private ActiveFilterItem activeFilterItem;

    private ColumnFilter filter;

    public BasicFilterRemoveEvent(final String dataSetId,
                                  final ActiveFilterItem activeFilterItem,
                                  final ColumnFilter filter) {
        this.dataSetId = dataSetId;
        this.activeFilterItem = activeFilterItem;
        this.filter = filter;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public ActiveFilterItem getActiveFilterItem() {
        return activeFilterItem;
    }

    public ColumnFilter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return "BasicFilterRemoveEvent{" +
                "activeFilterItem=" + activeFilterItem +
                ", filter=" + filter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicFilterRemoveEvent)) {
            return false;
        }

        BasicFilterRemoveEvent that = (BasicFilterRemoveEvent) o;

        if (!getActiveFilterItem().equals(that.getActiveFilterItem())) {
            return false;
        }
        return getFilter().equals(that.getFilter());
    }

    @Override
    public int hashCode() {
        int result = getActiveFilterItem().hashCode();
        result = 31 * result + getFilter().hashCode();
        return result;
    }
}