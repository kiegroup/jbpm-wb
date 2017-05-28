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

import java.util.Map;
import java.util.function.Consumer;

import org.dashbuilder.dataset.DataSetLookup;
import org.jbpm.workbench.common.client.util.DateRange;

public interface AdvancedSearchFiltersView {

    void addTextFilter(String label,
                       String placeholder,
                       Consumer<String> addCallback,
                       Consumer<String> removeCallback);

    void addNumericFilter(String label,
                          String placeholder,
                          Consumer<String> addCallback,
                          Consumer<String> removeCallback);

    void addDateRangeFilter(String label,
                            Consumer<DateRange> addCallback,
                            Consumer<DateRange> removeCallback);

    void addSelectFilter(String label,
                         Map<String, String> options,
                         Boolean liveSearch,
                         Consumer<String> addCallback,
                         Consumer<String> removeCallback);

    void addDataSetSelectFilter(String label,
                                String tableKey,
                                DataSetLookup lookup,
                                String textColumnId,
                                String valueColumnId,
                                Consumer<String> addCallback,
                                Consumer<String> removeCallback);

    <T extends Object> void addActiveFilter(String labelKey,
                                            String labelValue,
                                            T value,
                                            Consumer<T> removeCallback);
}