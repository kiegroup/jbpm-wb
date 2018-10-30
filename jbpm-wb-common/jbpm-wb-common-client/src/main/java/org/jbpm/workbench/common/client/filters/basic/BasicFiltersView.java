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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.dashbuilder.dataset.DataSetLookup;
import org.jboss.errai.common.client.api.IsElement;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.util.DateRange;

public interface BasicFiltersView extends IsElement {

    void addTextFilter(String label,
                       String placeholder,
                       Consumer<ActiveFilterItem<String>> callback);

    void addNumericFilter(String label,
                          String placeholder,
                          Consumer<ActiveFilterItem<Integer>> callback);

    void addDateRangeFilter(String label,
                            String placeholder,
                            Boolean useMaxDate,
                            Consumer<ActiveFilterItem<DateRange>> callback);

    void clearSelectFilter(String label);

    void checkSelectFilter(String label,
                           String value);

    void clearAllSelectFilter();

    void addMultiSelectFilter(String label,
                              Map<String, String> options,
                              Consumer<ActiveFilterItem<List<String>>> callback);

    void addSelectFilter(String label,
                         Map<String, String> options,
                         Consumer<ActiveFilterItem<String>> callback);

    void addDataSetSelectFilter(String label,
                                DataSetLookup lookup,
                                String textColumnId,
                                String valueColumnId,
                                Consumer<ActiveFilterItem<String>> callback);

    void hideFilterBySection();
}
