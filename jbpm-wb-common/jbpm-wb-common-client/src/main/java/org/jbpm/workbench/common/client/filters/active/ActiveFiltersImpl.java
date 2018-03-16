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

package org.jbpm.workbench.common.client.filters.active;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;

@Dependent
public class ActiveFiltersImpl implements ActiveFilters {

    @Inject
    ActiveFiltersView view;

    private BiConsumer<String, Consumer<String>> filterNameCallback;

    @PostConstruct
    public void init() {
        view.setSaveFilterCallback(name -> {
            if (filterNameCallback != null) {
                filterNameCallback.accept(name,
                                          error -> {
                                              if (error == null) {
                                                  view.closeSaveFilter();
                                              } else {
                                                  view.setSaveFilterErrorMessage(error);
                                              }
                                          });
            }
        });
    }

    @Override
    public void setSaveFilterCallback(final BiConsumer<String, Consumer<String>> filterNameCallback) {
        this.filterNameCallback = filterNameCallback;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public <T extends Object> void addActiveFilter(final ActiveFilterItem<T> filter) {
        view.addActiveFilter(filter);
    }

    @Override
    public void removeAllActiveFilters() {
        view.removeAllActiveFilters();
    }
}
