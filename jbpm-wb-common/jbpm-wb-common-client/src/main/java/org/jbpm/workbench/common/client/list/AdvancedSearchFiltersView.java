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

import org.uberfire.mvp.ParameterizedCommand;

public interface AdvancedSearchFiltersView {

    void addTextFilter(String label,
                       String placeholder,
                       ParameterizedCommand<String> addCallback,
                       ParameterizedCommand<String> removeCallback);

    void addNumericFilter(String label,
                          String placeholder,
                          ParameterizedCommand<String> addCallback,
                          ParameterizedCommand<String> removeCallback);

    void addSelectFilter(String label,
                         Map<String, String> options,
                         Boolean liveSearch,
                         ParameterizedCommand<String> addCallback,
                         ParameterizedCommand<String> removeCallback);

    void addActiveFilter(String labelKey,
                         String labelValue,
                         String value,
                         ParameterizedCommand<String> removeCallback);
}