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

package org.jbpm.workbench.common.client.filters;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class SavedFiltersViewImpl implements IsElement {

    @Inject
    @DataField("filters")
    HTMLDivElement filters;

    @Inject
    @DataField("saved-filters")
    @ListContainer("ul")
    @Bound
    @SuppressWarnings("unused")
    private ListComponent<SavedFilter, SavedFilterView> savedFilters;

    @Inject
    @AutoBound
    private DataBinder<List<SavedFilter>> savedFilterDataBinder;

    @Override
    public HTMLElement getElement() {
        return filters;
    }

    @PostConstruct
    public void init() {
        savedFilterDataBinder.setModel(new ArrayList<>());
    }

    public void addSavedFilter(final SavedFilter savedFilter) {
        savedFilterDataBinder.getModel().add(savedFilter);
    }

    public void removeSavedFilter(final SavedFilter savedFilter) {
        savedFilterDataBinder.getModel().remove(savedFilter);
    }

    public List<SavedFilter> getSavedFilters() {
        return new ArrayList<>(savedFilterDataBinder.getModel());
    }
}
