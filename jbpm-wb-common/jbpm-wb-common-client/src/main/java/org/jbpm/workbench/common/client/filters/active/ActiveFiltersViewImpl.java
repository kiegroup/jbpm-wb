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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;
import org.uberfire.mvp.ParameterizedCommand;

@Templated
@Dependent
public class ActiveFiltersViewImpl implements ActiveFiltersView {

    @Inject
    @DataField("remove-all-filters")
    HTMLAnchorElement removeAll;

    @Inject
    @DataField("save-filter")
    Anchor saveFilter;

    @Inject
    @DataField("content")
    HTMLDivElement content;

    @Inject
    @DataField("actions")
    HTMLDivElement actions;

    @Inject
    @DataField("active-filters")
    @ListContainer("ul")
    @Bound
    private ListComponent<ActiveFilterItem, ActiveFilterItemView> activeFilters;

    @Inject
    @AutoBound
    private DataBinder<List<ActiveFilterItem>> activeFiltersList;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Inject
    private SaveFilterPopoverView saveFilterPopoverView;

    private Popover saveFilterPopover;

    private ParameterizedCommand<String> saveFilterCallback;

    @PostConstruct
    public void init() {
        activeFiltersList.setModel(new ArrayList<>());
        activeFilters.addComponentCreationHandler(v -> actions.classList.remove("hidden"));
        activeFilters.addComponentDestructionHandler(v -> {
            if (activeFiltersList.getModel().isEmpty()) {
                actions.classList.add("hidden");
            }
            final Consumer callback = v.getValue().getCallback();
            if (callback != null) {
                callback.accept(v.getValue().getValue());
            }
        });

        saveFilterPopover = jQueryPopover.wrap(this.saveFilter);
        saveFilterPopoverView.setCancelCallback(() -> closeSaveFilter());
        saveFilterPopoverView.setSaveCallback(name -> saveFilter(name));
        final PopoverOptions popoverOptions = new PopoverOptions();
        popoverOptions.setContent(e -> saveFilterPopoverView.getElement());
        saveFilterPopover.popover(popoverOptions);
        saveFilterPopover.addShowListener(() -> saveFilterPopoverView.onOpen());
        saveFilterPopover.addShownListener(() -> saveFilterPopoverView.onShow());
    }

    @PreDestroy
    public void destroy() {
        saveFilterPopover.destroy();
    }

    @Override
    public void setSaveFilterCallback(final ParameterizedCommand<String> callback) {
        saveFilterCallback = callback;
    }

    @Override
    public void closeSaveFilter() {
        saveFilterPopover.hide();
    }

    @Override
    public void setSaveFilterErrorMessage(final String message) {
        saveFilterPopoverView.setError(message);
    }

    protected void saveFilter(final String filterName) {
        if (saveFilterCallback != null) {
            saveFilterCallback.execute(filterName);
        }
    }

    @Override
    public <T extends Object> void addActiveFilter(final ActiveFilterItem filter) {
        activeFiltersList.getModel().removeIf(f -> f.getKey().equals(filter.getKey()));
        activeFiltersList.getModel().add(filter);
    }

    public void onRemoveActiveFilter(@Observes final ActiveFilterItemRemovedEvent event) {
        activeFiltersList.getModel().remove(event.getActiveFilterItem());
    }

    @EventHandler("remove-all-filters")
    public void onRemoveAll(@ForEvent("click") Event e) {
        activeFiltersList.getModel().clear();
    }

    @EventHandler("save-filter")
    public void onSaveFilter(@ForEvent("click") Event e) {
        saveFilterPopover.toggle();
    }

    @Override
    public void removeAllActiveFilters() {
        activeFiltersList.getModel().forEach(f -> f.setCallback(null));
        activeFiltersList.getModel().clear();
    }

    @Override
    public HTMLElement getElement() {
        return content;
    }
}
