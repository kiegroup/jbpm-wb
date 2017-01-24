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
package org.jbpm.workbench.df.client.filter;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.sort.SortOrder;

/**
 * Table settings builder
 */
public final class FilterSettingsBuilderHelper {

    FilterSettingsBuilder builder = FilterSettingsBuilder.init();

    public static FilterSettingsBuilderHelper init() {
        return new FilterSettingsBuilderHelper();
    }

    public void initBuilder() {
        builder = FilterSettingsBuilder.init();
    }

    public void dataset(String dataSetId) {
        builder.dataset(dataSetId);
    }

    public void filter(ColumnFilter... filter) {
        builder.filter(filter);
    }

    public void filter(String column_name, ColumnFilter... filter) {
        builder.filter(column_name, filter);
    }

    public void setColumn(String columnId, String columnHeader, String formatStr) {
        builder.column(columnId).format(columnHeader, formatStr);
    }

    public void setColumn(String columnId, String columnHeader) {
        builder.column(columnId).format(columnHeader);
    }

    public void filterOn(boolean applySelf, boolean notifyOthers, boolean receiveFromOthers) {
        builder.filterOn(applySelf, notifyOthers, receiveFromOthers);
    }

    public void tableOrderEnabled(boolean orderEnabled) {
        builder.tableOrderEnabled(orderEnabled);
    }

    public void tableOrderDefault(String defaultColumnId, SortOrder sortOrder) {
        builder.tableOrderDefault(defaultColumnId, sortOrder);
    }

    public void tableWidth(int width) {
        builder.tableWidth(width);
    }

    public void group(String columnId) {
        builder.group(columnId);
    }

    public FilterSettings buildSettings() {
        return (FilterSettings) builder.buildSettings();
    }

}
