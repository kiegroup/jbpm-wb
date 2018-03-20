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

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterAddEvent;
import org.jbpm.workbench.common.client.filters.basic.BasicFilterRemoveEvent;
import org.jbpm.workbench.common.client.filters.saved.SavedFilterSelectedEvent;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

public abstract class AbstractMultiGridPresenter<T extends GenericSummary, V extends MultiGridView> extends AbstractScreenListPresenter<T> {

    protected DataSetQueryHelper dataSetQueryHelper;

    protected V view;

    protected AuthorizationManager authorizationManager;

    protected FilterSettingsManager filterSettingsManager;

    @Inject
    protected ErrorPopupPresenter errorPopup;

    @Inject
    public void setAuthorizationManager(final AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Inject
    public void setDataSetQueryHelper(final DataSetQueryHelper dataSetQueryHelper) {
        this.dataSetQueryHelper = dataSetQueryHelper;
    }

    public void setFilterSettingsManager(final FilterSettingsManager filterSettingsManager) {
        this.filterSettingsManager = filterSettingsManager;
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @WorkbenchPartView
    public UberView<T> getView() {
        return view;
    }

    @Inject
    public void setView(V view) {
        this.view = view;
    }

    public void setupActiveSearchFilters() {
        setupDefaultActiveSearchFilters();
    }

    public abstract void setupDefaultActiveSearchFilters();

    @PostConstruct
    public void init() {
        final BiConsumer<String, Consumer<String>> filterNameCallback = (name, callback) -> saveSearchFilterSettings(name,
                                                                                                                     callback);
        view.setSaveFilterCallback(filterNameCallback);
    }

    @Override
    @OnOpen
    public void onOpen() {
        super.onOpen();
        setFilterSettings(filterSettingsManager.createDefaultFilterSettingsPrototype(),
                          table -> {
                              setupActiveSearchFilters();
                              addDataDisplay(table);
                          });
    }

    public void onBasicFilterAddEvent(@Observes final BasicFilterAddEvent event) {
        addActiveFilter(event.getFilter(),
                        event.getActiveFilterItem());
    }

    public void onBasicFilterRemoveEvent(@Observes final BasicFilterRemoveEvent event) {
        removeActiveFilter(event.getFilter(),
                           event.getActiveFilterItem());
    }

    protected void onSavedFilterSelectedEvent(@Observes final SavedFilterSelectedEvent event) {
        filterSettingsManager.getFilterSettings(event.getSavedFilter().getKey(),
                                                filter -> addActiverFilters(filter));
    }

    protected void setFilterSettings(final FilterSettings filter,
                                     final Consumer<ListTable<T>> readyCallback) {
        dataSetQueryHelper.setCurrentTableSettings(filter);
        view.loadListTable(filter.getKey(),
                           readyCallback);
    }

    protected void addActiverFilters(final FilterSettings filter) {
        view.removeAllActiveFilters();
        setFilterSettings(filter,
                          table -> {
                              if (filter.getDataSetLookup().getFirstFilterOp() != null) {
                                  List<ColumnFilter> filters = filter.getDataSetLookup().getFirstFilterOp().getColumnFilterList();
                                  filters.forEach(column -> {
                                      final ActiveFilterItem activeFilter = new ActiveFilterItem<>(column.getColumnId(),
                                                                                                   column.toString(),
                                                                                                   null,
                                                                                                   null,
                                                                                                   v -> removeActiveFilter(column));
                                      view.addActiveFilter(activeFilter);
                                  });
                              }
                              addDataDisplay(table);
                          });
    }

    @Override
    public void getData(final Range visibleRange) {
        try {
            final FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            currentTableSettings.setServerTemplateId(getSelectedServerTemplate());
            currentTableSettings.setTablePageSize(view.getListGrid().getPageSize());
            ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
            if (columnSortList != null && columnSortList.size() > 0) {
                dataSetQueryHelper.setLastOrderedColumn(columnSortList.size() > 0 ? columnSortList.get(0).getColumn().getDataStoreName() : "");
                dataSetQueryHelper.setLastSortOrder(columnSortList.size() > 0 && columnSortList.get(0).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
            }

            dataSetQueryHelper.setCurrentTableSettings(currentTableSettings);
            dataSetQueryHelper.setDataSetHandler(currentTableSettings);
            dataSetQueryHelper.lookupDataSet(visibleRange.getStart(),
                                             getDataSetReadyCallback(visibleRange.getStart(),
                                                                     currentTableSettings));
        } catch (Exception e) {
            errorPopup.showMessage(Constants.INSTANCE.UnexpectedError(e.getMessage()));
            view.hideBusyIndicator();
        }
    }

    protected abstract DataSetReadyCallback getDataSetReadyCallback(final Integer startRange,
                                                                    final FilterSettings tableSettings);

    protected <T extends Object> void addActiveFilter(final ColumnFilter columnFilter,
                                                      final String labelKey,
                                                      final String labelValue,
                                                      final T value,
                                                      final Consumer<T> removeCallback) {
        addActiveFilter(columnFilter,
                        new ActiveFilterItem<>(labelKey,
                                               labelKey + ": " + labelValue,
                                               null,
                                               value,
                                               removeCallback));
    }

    protected <T extends Object> void addActiveFilter(final ColumnFilter columnFilter,
                                                      final ActiveFilterItem<T> filter) {
        filter.setCallback(v -> removeActiveFilter(columnFilter));
        final FilterSettings settings = dataSetQueryHelper.getCurrentTableSettings();
        settings.addColumnFilter(columnFilter);
        view.addActiveFilter(filter);
        refreshGrid();
    }

    protected void removeActiveFilter(final ColumnFilter columnFilter) {
        final FilterSettings settings = dataSetQueryHelper.getCurrentTableSettings();
        settings.removeColumnFilter(columnFilter);
        refreshGrid();
    }

    protected void removeActiveFilter(final ColumnFilter columnFilter,
                                      final ActiveFilterItem<T> filter) {
        view.removeActiveFilter(filter);
        removeActiveFilter(columnFilter);
    }

    public void saveSearchFilterSettings(final String filterName,
                                         final Consumer<String> callback) {
        final FilterSettings settings = dataSetQueryHelper.getCurrentTableSettings();
        settings.setTableName(filterName);
        settings.setTableDescription(filterName);
        filterSettingsManager.saveFilterIntoPreferences(settings,
                                                        state -> callback.accept(state ? null : Constants.INSTANCE.FilterWithSameNameAlreadyExists()));
    }

    protected Optional<String> getSearchParameter(final String parameterId) {
        return Optional.ofNullable(place.getParameter(parameterId,
                                                      null));
    }

    protected void navigateToPerspective(final String perspectiveId,
                                         final String parameterName,
                                         final String parameterValue) {
        final PlaceRequest request = new DefaultPlaceRequest(perspectiveId);
        request.addParameter(parameterName,
                             parameterValue);
        placeManager.goTo(request);
    }

    public boolean isUserAuthorizedForPerspective(final String perspectiveId) {
        final ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                        ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              identity);
    }

    public void openErrorView(final String parameterId) {
    }

    public Predicate<T> getViewErrorsActionCondition() {
        return null;
    }
}