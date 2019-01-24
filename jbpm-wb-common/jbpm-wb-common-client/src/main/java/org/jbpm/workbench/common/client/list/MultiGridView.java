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

package org.jbpm.workbench.common.client.list;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gwt.user.cellview.client.ColumnSortList;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.model.GenericSummary;

public interface MultiGridView<T extends GenericSummary, V> extends ListView<T, V> {

    <T extends Object> void addActiveFilter(ActiveFilterItem<T> filter);

    <T extends Object> void removeActiveFilter(ActiveFilterItem<T> filter);

    void removeAllActiveFilters();

    void loadListTable(String key,
                       Consumer<ListTable<T>> callback);

    void setSaveFilterCallback(BiConsumer<String, Consumer<String>> filterNameCallback);

    ColumnSortList reloadColumnSortList();
}
