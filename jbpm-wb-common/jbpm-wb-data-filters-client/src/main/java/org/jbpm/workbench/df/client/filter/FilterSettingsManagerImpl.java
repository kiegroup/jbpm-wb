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

package org.jbpm.workbench.df.client.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.df.client.events.SavedFilterAddedEvent;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;

import static java.util.Optional.ofNullable;

public abstract class FilterSettingsManagerImpl implements FilterSettingsManager {

    public static final String FILTER_TABLE_SETTINGS = "tableSettings";
    public static final String DEFAULT_FILTER_SETTINGS_KEY = "base";

    @Inject
    protected FilterSettingsJSONMarshaller marshaller;

    private Caller<UserPreferencesService> preferencesService;

    @Inject
    private Event<SavedFilterAddedEvent> filterSavedEvent;

    @Inject
    public void setPreferencesService(final Caller<UserPreferencesService> preferencesService) {
        this.preferencesService = preferencesService;
    }

    @Override
    public void loadSavedFilters(final Consumer<List<SavedFilter>> savedFiltersConsumer) {
        loadMultiGridPreferencesStore(store -> loadSavedFiltersFromPreferences(store,
                                                                               savedFiltersConsumer));
    }

    protected void loadMultiGridPreferencesStore(final Consumer<MultiGridPreferencesStore> multiGridPreferencesStoreConsumer) {
        preferencesService.call((MultiGridPreferencesStore multiGridPreferencesStore) -> {
            multiGridPreferencesStoreConsumer.accept(ofNullable(multiGridPreferencesStore)
                                                             .orElse(new MultiGridPreferencesStore(getGridGlobalPreferencesKey())));
        }).loadUserPreferences(getGridGlobalPreferencesKey(),
                               UserPreferencesType.MULTIGRIDPREFERENCES);
    }

    protected abstract String getGridGlobalPreferencesKey();

    protected abstract List<FilterSettings> initDefaultFilters();

    @Override
    public FilterSettings createDefaultFilterSettingsPrototype() {
        final FilterSettings filterSettings = createFilterSettingsPrototype();
        filterSettings.setKey(getDefaultFilterSettingsKey());
        return filterSettings;
    }

    protected String getNewFilterSettingsKey() {
        return getGridGlobalPreferencesKey() + "_" + UUID.uuid();
    }

    protected String getDefaultFilterSettingsKey() {
        return getGridGlobalPreferencesKey() + "_" + DEFAULT_FILTER_SETTINGS_KEY;
    }

    @Override
    public void saveFilterIntoPreferences(final FilterSettings filterSettings,
                                          final Consumer<Boolean> callback) {
        filterSettings.setKey(getNewFilterSettingsKey());
        loadMultiGridPreferencesStore(store -> {
            loadSavedFiltersFromPreferences(store,
                                            savedFilters -> {
                                                final Optional<SavedFilter> savedFilter = savedFilters.stream().filter(f -> f.getName().equalsIgnoreCase(filterSettings.getTableName())).findFirst();
                                                if (savedFilter.isPresent()) {
                                                    callback.accept(false);
                                                    return;
                                                }

                                                addFilterToPreferencesStore(filterSettings,
                                                                            store);
                                                saveMultiGridPreferencesStore(store,
                                                                              () -> {
                                                                                  filterSavedEvent.fire(new SavedFilterAddedEvent(new SavedFilter(filterSettings.getKey(),
                                                                                                                                                  filterSettings.getTableName())));
                                                                                  callback.accept(true);
                                                                              });
                                            });
        });
    }

    protected void addFilterToPreferencesStore(final FilterSettings filterSettings,
                                               final MultiGridPreferencesStore store) {
        final HashMap<String, String> tabSettingsValues = new HashMap<>();
        tabSettingsValues.put(FILTER_TABLE_SETTINGS,
                              marshaller.toJsonString(filterSettings));
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                              filterSettings.getTableName());
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                              filterSettings.getTableDescription());

        store.addNewTab(filterSettings.getKey(),
                        tabSettingsValues);
    }

    protected void saveMultiGridPreferencesStore(final MultiGridPreferencesStore store,
                                                 final Command callback) {
        preferencesService.call(r -> callback.execute()).saveUserPreferences(store);
    }

    public void loadSavedFiltersFromPreferences(final MultiGridPreferencesStore store,
                                                final Consumer<List<SavedFilter>> savedFiltersConsumer) {
        if (store.getGridsId().isEmpty()) {
            final List<FilterSettings> defaultFilters = initDefaultFilters();
            defaultFilters.forEach(f -> addFilterToPreferencesStore(f,
                                                                    store));
            saveMultiGridPreferencesStore(store,
                                          () -> {
                                              final List<SavedFilter> filters = defaultFilters.stream().map(s -> new SavedFilter(s.getKey(),
                                                                                                                                 s.getTableName())).collect(Collectors.toList());
                                              if (savedFiltersConsumer != null) {
                                                  savedFiltersConsumer.accept(filters);
                                              }
                                          });
        } else {
            final List<SavedFilter> filters = store.getGridsId().stream().map(key -> new SavedFilter(key,
                                                                                                     getSavedFilterNameFromKey(key,
                                                                                                                               store))).collect(Collectors.toList());
            if (savedFiltersConsumer != null) {
                savedFiltersConsumer.accept(filters);
            }
        }
    }

    protected String getSavedFilterNameFromKey(final String key,
                                               final MultiGridPreferencesStore store) {
        return (String) ofNullable(store.getGridSettings(key))
                .orElseThrow(() -> new RuntimeException("Grid settings not found for key: " + key))
                .get(NewTabFilterPopup.FILTER_TAB_NAME_PARAM);
    }

    @Override
    public void getFilterSettings(final String key,
                                  final Consumer<FilterSettings> filterSettingsConsumer) {
        loadMultiGridPreferencesStore(store -> {
            final HashMap<String, Object> params = store.getGridSettings(key);
            final String json = (String) params.get(FILTER_TABLE_SETTINGS);
            final FilterSettings settings = marshaller.fromJsonString(json);
            filterSettingsConsumer.accept(settings);
        });
    }

    @Override
    public void resetDefaultSavedFilters(final Consumer<List<SavedFilter>> savedFiltersConsumer) {
        loadMultiGridPreferencesStore(store -> {
            final ArrayList<String> existingGrids = new ArrayList<>(store.getGridsId());
            existingGrids.forEach(id -> store.removeTab(id));
            preferencesService.call(r -> loadSavedFiltersFromPreferences(store,
                                                                         savedFiltersConsumer)).saveUserPreferences(store);
        });
    }

    @Override
    public void removeSavedFilterFromPreferences(final String key) {
        loadMultiGridPreferencesStore(store -> {
            removeSavedFilterFromPreferences(key,
                                             store,
                                             null);
        });
    }

    protected void removeSavedFilterFromPreferences(final String key,
                                                    final MultiGridPreferencesStore store,
                                                    final Command callback) {
        store.removeTab(key);
        preferencesService.call(r -> {
            if (callback != null) {
                callback.execute();
            }
        }).saveUserPreferences(store);
    }

    protected FilterSettings createFilterSettings(final String dataSetId,
                                                  final String columnSortId,
                                                  final Consumer<FilterSettingsBuilderHelper> consumer,
                                                  final String key,
                                                  final String tabName,
                                                  final String tabDesc) {
        final FilterSettings filterSettings = createFilterSettings(dataSetId,
                                                                   columnSortId,
                                                                   consumer);
        filterSettings.setKey(key);
        filterSettings.setTableName(tabName);
        filterSettings.setTableDescription(tabDesc);

        return filterSettings;
    }

    protected FilterSettings createFilterSettings(final String dataSetId,
                                                  final String columnSortId,
                                                  final Consumer<FilterSettingsBuilderHelper> consumer) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(dataSetId);
        builder.uuid(dataSetId);

        builder.filterOn(true,
                         true,
                         true);
        builder.tableOrderEnabled(true);
        builder.tableOrderDefault(columnSortId,
                                  SortOrder.DESCENDING);

        if (consumer != null) {
            consumer.accept(builder);
        }

        return builder.buildSettings();
    }
}
