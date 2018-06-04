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
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jboss.errai.common.client.api.IsElement;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItemAddedEvent;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItemRemovedEvent;
import org.jbpm.workbench.common.client.filters.active.ClearAllActiveFiltersEvent;
import org.jbpm.workbench.common.client.filters.saved.SavedFilterSelectedEvent;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.filter.FilterEditorPopup;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.filter.SavedFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnOpen;

import static org.dashbuilder.dataset.filter.FilterFactory.in;

public abstract class BasicFiltersPresenter {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    protected BasicFiltersView view;

    protected FilterSettingsManager filterSettingsManager;

    @Inject
    protected Event<BasicFilterAddEvent> basicFilterAddEvent;

    @Inject
    protected Event<BasicFilterRemoveEvent> basicFilterRemoveEvent;

    @Inject
    private FilterEditorPopup filterEditorPopup;

    @Inject
    private Event<SavedFilterSelectedEvent> savedFilterSelectedEvent;

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Filters();
    }

    @PostConstruct
    public void init() {
        view.setAdvancedFiltersCallback(() -> showAdvancedFilters());
        loadFilters();
    }

    @OnOpen
    public void onOpen(){
        view.clearAllSelectFilter();
    }

    public void onClearAllActiveFiltersEvent(@Observes ClearAllActiveFiltersEvent event){
        view.clearAllSelectFilter();
    }

    public abstract void loadFilters();

    public void setFilterSettingsManager(final FilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    protected void addSearchFilter(final ActiveFilterItem filter,
                                   final ColumnFilter columnFilter) {
        basicFilterAddEvent.fire(new BasicFilterAddEvent(filter,
                                                         columnFilter));
    }

    protected void addSearchFilterList(final String columnId,
                                       final ActiveFilterItem<List<String>> filter) {
        final ColumnFilter columnFilter = in(columnId,
                                             filter.getValue());
        addSearchFilterList(filter,
                            columnFilter);
    }

    protected void addSearchFilterList(final ActiveFilterItem<List<String>> filter,
                                       final ColumnFilter columnFilter) {
        if (filter.getValue().isEmpty()) {
            removeSearchFilter(filter,
                               columnFilter);
        } else {
            addSearchFilter(filter,
                            columnFilter);
        }
    }

    protected void removeSearchFilter(final ActiveFilterItem filter,
                                      final ColumnFilter columnFilter) {
        basicFilterRemoveEvent.fire(new BasicFilterRemoveEvent(filter,
                                                               columnFilter));
    }

    protected void showAdvancedFilters() {
        filterEditorPopup.setTitle(getAdvancedFilterPopupTitle());
        filterEditorPopup.show(filterSettingsManager.createFilterSettingsPrototype(),
                               filterSettings ->
                                       filterSettingsManager.saveFilterIntoPreferences(filterSettings,
                                                                                       state -> {
                                                                                           if (state) {
                                                                                               filterEditorPopup.hide();
                                                                                               savedFilterSelectedEvent.fire(new SavedFilterSelectedEvent(new SavedFilter(filterSettings.getKey(),
                                                                                                                                                                          filterSettings.getTableName())));
                                                                                           } else {
                                                                                               filterEditorPopup.setTableNameError(Constants.INSTANCE.FilterWithSameNameAlreadyExists());
                                                                                           }
                                                                                       })
        );
    }

    protected abstract String getAdvancedFilterPopupTitle();

    protected abstract void onActiveFilterAdded(final ActiveFilterItem activeFilterItem);

    public void onActiveFilterAddedEvent(@Observes final ActiveFilterItemAddedEvent event) {
        onActiveFilterAdded(event.getActiveFilterItem());
    }

    public void onRemoveActiveFilter(@Observes final ActiveFilterItemRemovedEvent event) {
        view.clearSelectFilter(event.getActiveFilterItem().getKey());
    }

    protected void onSavedFilterSelectedEvent(@Observes final SavedFilterSelectedEvent event) {
        view.clearAllSelectFilter();
    }
}