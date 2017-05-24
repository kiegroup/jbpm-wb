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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.mvp.ParameterizedCommand;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class AdvancedSearchFiltersViewImpl extends Composite implements AdvancedSearchFiltersView {

    @Inject
    @DataField("dropdown-filter-text")
    Span filterText;

    @Inject
    @DataField("filters-input")
    Div filtersInput;

    @Inject
    @DataField("remove-all-filters")
    Anchor removeAll;

    @Inject
    @DataField("filters")
    UnorderedList filters;

    @Inject
    @DataField("active-filters")
    @ListContainer("ul")
    @Bound
    private ListComponent<ActiveFilterItem, ActiveFilterItemView> activeFilters;

    @Inject
    @AutoBound
    private DataBinder<List<ActiveFilterItem>> activeFiltersList;

    @Inject
    @DataField("select-filters")
    private Div selectFilters;

    @Inject
    private ManagedInstance<Select> selectProvider;

    @PostConstruct
    public void init() {
        activeFiltersList.setModel(new ArrayList<>());
        activeFilters.addComponentCreationHandler(v -> removeCSSClass(removeAll,
                                                                      "hidden"));
        activeFilters.addComponentDestructionHandler(v -> {
            if (activeFiltersList.getModel().isEmpty()) {
                addCSSClass(removeAll,
                            "hidden");
            }
            v.getValue().getCallback().execute(v.getValue().getValue());
        });
    }

    @Override
    public void addTextFilter(final String label,
                              final String placeholder,
                              final ParameterizedCommand<String> addCallback,
                              final ParameterizedCommand<String> removeCallback) {
        createInput("text",
                    label,
                    placeholder,
                    addCallback,
                    removeCallback);
        createFilterOption(label);
    }

    @Override
    public void addNumericFilter(final String label,
                                 final String placeholder,
                                 final ParameterizedCommand<String> addCallback,
                                 final ParameterizedCommand<String> removeCallback) {
        createInput("number",
                    label,
                    placeholder,
                    addCallback,
                    removeCallback);
        createFilterOption(label);
    }

    @Override
    public void addSelectFilter(final String label,
                                final Map<String, String> options,
                                final Boolean liveSearch,
                                final ParameterizedCommand<String> addCallback,
                                final ParameterizedCommand<String> removeCallback) {
        final Select select = selectProvider.get();
        select.setTitle(label);
        select.setLiveSearch(liveSearch);
        select.setWidth("auto");
        select.getElement().getClassList().add("selectpicker");
        select.getElement().getClassList().add("form-control");
        options.forEach((k, v) -> select.addOption(v,
                                                   k));
        selectFilters.appendChild(select.getElement());
        select.refresh();
        select.getElement().addEventListener("change",
                                             event -> {
                                                 if (select.getValue().isEmpty() == false) {
                                                     addActiveFilter(label,
                                                                     options.get(select.getValue()),
                                                                     select.getValue(),
                                                                     removeCallback);
                                                     addCallback.execute(select.getValue());
                                                     select.setValue("");
                                                 }
                                             },
                                             false);
    }

    private void createInput(final String type,
                             final String label,
                             final String placeholder,
                             final ParameterizedCommand<String> addCallback,
                             final ParameterizedCommand<String> removeCallback) {
        final Input input = (Input) getDocument().createElement("input");
        input.setType(type);
        input.setAttribute("placeholder",
                           placeholder);
        input.setAttribute("data-filter",
                           label);
        input.getClassList().add("form-control");
        input.getClassList().add("filter-control");
        input.getClassList().add("hidden");
        input.addEventListener("keypress",
                               (KeyboardEvent e) -> {
                                   if (e.getKeyCode() == 13 && input.getValue().isEmpty() == false) {
                                       addActiveFilter(label,
                                                       input.getValue(),
                                                       input.getValue(),
                                                       removeCallback);
                                       addCallback.execute(input.getValue());
                                       input.setValue("");
                                   }
                               },
                               false);
        filtersInput.appendChild(input);
        if (filterText.getTextContent().isEmpty()) {
            setCurrentFilter(label);
        }
    }

    private void createFilterOption(final String label) {
        final Anchor a = (Anchor) getDocument().createElement("a");
        a.setTextContent(label);
        a.addEventListener("click",
                           e -> setCurrentFilter(label),
                           false);
        final ListItem li = (ListItem) getDocument().createElement("li");
        li.setAttribute("data-filter",
                        label);
        li.appendChild(a);
        filters.appendChild(li);
    }

    public void setCurrentFilter(final String label) {
        filterText.setTextContent(label);
        for (Element child : elementIterable(filters.getChildNodes())) {
            if (label.equals(child.getAttribute("data-filter"))) {
                addCSSClass((HTMLElement) child,
                            "hidden");
            } else {
                removeCSSClass((HTMLElement) child,
                               "hidden");
            }
        }
        for (Element child : elementIterable(filtersInput.getChildNodes())) {
            if (label.equals(child.getAttribute("data-filter"))) {
                removeCSSClass((HTMLElement) child,
                               "hidden");
            } else {
                addCSSClass((HTMLElement) child,
                            "hidden");
            }
        }
    }

    @Override
    public void addActiveFilter(final String labelKey,
                                final String labelValue,
                                final String value,
                                final ParameterizedCommand<String> removeCallback) {
        activeFiltersList.getModel().add(new ActiveFilterItem(labelKey,
                                                              labelValue,
                                                              value,
                                                              removeCallback));
    }

    public void onRemoveActiveFilter(@Observes final ActiveFilterItemRemoved event) {
        activeFiltersList.getModel().remove(event.getActiveFilterItem());
    }

    @EventHandler("remove-all-filters")
    public void onRemoveAll(@ForEvent("click") Event e) {
        activeFiltersList.getModel().clear();
    }
}