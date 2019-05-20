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
import java.util.Map;
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

    protected FilterSettingsJSONMarshaller marshaller;

    private Caller<UserPreferencesService> preferencesService;

    @Inject
    private Event<SavedFilterAddedEvent> filterSavedEvent;

    @Inject
    public void setMarshaller(final FilterSettingsJSONMarshaller marshaller) {
        this.marshaller = marshaller;
    }

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
                                                                                                                                                  filterSettings.getTableName(),
                                                                                                                                                  false)));
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
            store.setDefaultGridId(store.getGridsId().get(0));
            saveMultiGridPreferencesStore(store,
                                          () -> {
                                              final List<SavedFilter> filters = defaultFilters.stream().map(s -> new SavedFilter(s.getKey(),
                                                                                                                                 s.getTableName(), false)).collect(Collectors.toList());
                                              if (savedFiltersConsumer != null) {
                                                  filters.get(0).setDefaultFilter(true);
                                                  savedFiltersConsumer.accept(filters);
                                              }
                                          });
        } else {
            final List<SavedFilter> filters = store.getGridsId().stream().map(key -> new SavedFilter(key,
                                                                                                     getSavedFilterNameFromKey(key, store),
                                                                                                     key.equals(store.getDefaultGridId()))).collect(Collectors.toList());
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
            final FilterSettings settings;
            if (params != null) {
                final String json = (String) params.get(FILTER_TABLE_SETTINGS);
                settings = marshaller.fromJsonString(json);
            } else {
                settings = createFilterSettingsPrototype();
            }
            filterSettingsConsumer.accept(settings);
        });
    }

    protected void removeSavedFilter(final MultiGridPreferencesStore store, String key) {
        store.removeTab(key);
        if (key.equals(store.getDefaultGridId())) {
            if (store.getGridsId().size() > 0) {
                store.setDefaultGridId(store.getGridsId().get(0));
            } else {
                store.setDefaultGridId(null);
            }
        }
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
    public void removeSavedFilterFromPreferences(final String key, Command command) {
        loadMultiGridPreferencesStore(store -> {
            removeSavedFilterFromPreferences(key, store, command);
        });
    }

    protected void removeSavedFilterFromPreferences(final String key,
                                                    final MultiGridPreferencesStore store,
                                                    final Command callback) {
        removeSavedFilter(store, key);
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

        return createFilterSettings(dataSetId,
                                    consumer,
                                    builder -> {
                                        builder.tableOrderEnabled(true);
                                        builder.tableOrderDefault(columnSortId,
                                                                  SortOrder.DESCENDING);
                                    });
    }

    protected FilterSettings createFilterSettings(final String dataSetId,
                                                  final Map<String, SortOrder> sortbyMap,
                                                  final Consumer<FilterSettingsBuilderHelper> consumer) {
        return createFilterSettings(dataSetId,
                                    consumer,
                                    builder -> sortbyMap.forEach((colId, sortOrder) -> builder.sortBy(colId,
                                                                                                      sortOrder)));
    }

    private FilterSettings createFilterSettings(final String dataSetId,
                                                final Consumer<FilterSettingsBuilderHelper> consumer,
                                                final Consumer<FilterSettingsBuilderHelper> consumerSorting) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset(dataSetId);
        builder.uuid(dataSetId);

        builder.filterOn(true,
                         true,
                         true);
        builder.tableOrderEnabled(true);

        if (consumerSorting != null) {
            consumerSorting.accept(builder);
        }

        if (consumer != null) {
            consumer.accept(builder);
        }

        return builder.buildSettings();
    }

    protected void initFilterSettingPreferences(MultiGridPreferencesStore store, Consumer<FilterSettings> initFiltersCallback) {
        final List<FilterSettings> defaultFilters = initDefaultFilters();
        defaultFilters.forEach(f -> addFilterToPreferencesStore(f, store));
        store.setDefaultGridId(getDefaultFilterSettingsKey());

        saveMultiGridPreferencesStore(store, () -> {
            if (initFiltersCallback != null) {
                getFilterSettings(store.getDefaultGridId(), filterSettings -> initFiltersCallback.accept(filterSettings));
            }
        });
    }

    public void defaultActiveFilterInit(final Consumer<FilterSettings> callback) {
        loadMultiGridPreferencesStore(store -> {
            if (store.getDefaultGridId() != null && !store.getDefaultGridId().isEmpty()) {
                getFilterSettings(store.getDefaultGridId(), filterSettings -> callback.accept(filterSettings));
            } else {
                initFilterSettingPreferences(store, callback);
            }
        });
    }

    @Override
    public void saveDefaultActiveFilter(String filterKey, final Command callback) {
        loadMultiGridPreferencesStore(store -> {
            store.setDefaultGridId(filterKey);
            preferencesService.call(r -> {
                if (callback != null) {
                    callback.execute();
                }
            }).saveUserPreferences(store);
        });
    }
}
