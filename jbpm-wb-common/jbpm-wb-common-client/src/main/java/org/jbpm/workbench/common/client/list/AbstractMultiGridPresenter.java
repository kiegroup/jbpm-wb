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

import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.workbench.common.client.events.SearchEvent;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.base.DataSetQueryHelper;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;
import org.uberfire.lifecycle.OnOpen;

public abstract class AbstractMultiGridPresenter<T extends GenericSummary, V extends MultiGridView> extends AbstractScreenListPresenter<T> implements RefreshSelectorMenuBuilder.SupportsRefreshInterval {

    protected DataSetQueryHelper dataSetQueryHelper;

    protected RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder(this);

    protected V view;

    @Inject
    public void setDataSetQueryHelper(final DataSetQueryHelper dataSetQueryHelper) {
        this.dataSetQueryHelper = dataSetQueryHelper;
    }

    @Inject
    public void setView(V view) {
        this.view = view;
    }

    public void filterGrid(FilterSettings tableSettings) {
        dataSetQueryHelper.setCurrentTableSettings(tableSettings);
        refreshGrid();
    }

    @Override
    public void onSearchEvent(@Observes SearchEvent searchEvent) {
        textSearchStr = searchEvent.getFilter();
        view.applyFilterOnPresenter(dataSetQueryHelper.getCurrentTableSettings().getKey());
    }

    @Override
    public void onUpdateRefreshInterval(boolean enableAutoRefresh, int newInterval) {
        super.onUpdateRefreshInterval(enableAutoRefresh, newInterval);
        view.saveRefreshValue(newInterval);
    }

    @Override
    public void onGridPreferencesStoreLoaded() {
        refreshSelectorMenuBuilder.loadOptions( view.getRefreshValue() );
        clearActiveSearchFilters();
        setupActiveSearchFilters();
        view.selectFirstTabAndEnableQueries();
    }

    public void onRestoreTabs(){
        view.restoreTabs();
        clearActiveSearchFilters();
        setupDefaultActiveSearchFilters();
        view.selectFirstTabAndEnableQueries();
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @WorkbenchPartView
    public UberView<T> getView() {
        return view;
    }

    public abstract FilterSettings createTableSettingsPrototype();

    public abstract FilterSettings createSearchTabSettings();

    public abstract void setupAdvancedSearchView();

    public void setupActiveSearchFilters(){
        setupDefaultActiveSearchFilters();
    }

    public abstract void setupDefaultActiveSearchFilters();

    @Override
    @OnOpen
    public void onOpen() {
        super.onOpen();
        setupAdvancedSearchView();
    }

    protected void clearActiveSearchFilters() {
        final FilterSettings settings = view.getAdvancedSearchFilterSettings();
        settings.removeAllColumnFilters();
        view.saveAdvancedSearchFilterSettings(settings);
        view.removeAllActiveFilters();
    }


    protected void addAdvancedSearchFilter(final ColumnFilter columnFilter) {
        final FilterSettings settings = view.getAdvancedSearchFilterSettings();
        settings.addColumnFilter(columnFilter);
        view.saveAdvancedSearchFilterSettings(settings);
        filterGrid(settings);
    }

    protected void removeAdvancedSearchFilter(final ColumnFilter columnFilter) {
        final FilterSettings settings = view.getAdvancedSearchFilterSettings();
        if (settings.removeColumnFilter(columnFilter)) {
            view.saveAdvancedSearchFilterSettings(settings);
            filterGrid(settings);
        }
    }

    protected Optional<String> getSearchParameter(final String parameterId) {
        return Optional.ofNullable(place.getParameter(parameterId,
                                                      null));
    }

}