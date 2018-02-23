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
import java.util.HashMap;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jbpm.workbench.common.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.DataSetEditorManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Commands;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static java.util.Optional.ofNullable;

public abstract class SavedFiltersPresenter implements RestoreDefaultFiltersMenuBuilder.SupportsRestoreDefaultFilters {

    public static final String FILTER_TABLE_SETTINGS = "tableSettings";
    public static final String TAB_SEARCH = "base";

    private final Constants constants = Constants.INSTANCE;

    @Inject
    protected DataSetEditorManager dataSetEditorManager;

    protected MultiGridPreferencesStore multiGridPreferencesStore;

    @Inject
    private SavedFiltersViewImpl view;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.SavedFilters();
    }

    @PostConstruct
    public void init() {
        loadSavedFilters();
    }

    protected void loadSavedFilters() {
        preferencesService.call((MultiGridPreferencesStore multiGridPreferencesStore) -> {
            this.multiGridPreferencesStore =
                    ofNullable(multiGridPreferencesStore)
                            .orElse(new MultiGridPreferencesStore(getGridGlobalPreferencesKey()));
            loadSavedFiltersFromPreferences();
        }).loadUserPreferences(getGridGlobalPreferencesKey(),
                               UserPreferencesType.MULTIGRIDPREFERENCES);
    }

    public Menus getMenus() {
        return MenuFactory
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder(this)).endMenu()
                .build();
    }

    public abstract FilterSettings createTableSettingsPrototype();

    public abstract String getGridGlobalPreferencesKey();

    public abstract void initDefaultFilters();

    protected void initSavedFilter(final String dataSetId,
                                   final String columnSortId,
                                   final Consumer<FilterSettingsBuilderHelper> consumer,
                                   final String key,
                                   final String tabName,
                                   final String tabDesc) {
        final FilterSettings tableSettings = createFilterSettings(dataSetId,
                                                                  columnSortId,
                                                                  consumer);
        tableSettings.setKey(key);
        tableSettings.setTableName(tabName);
        tableSettings.setTableDescription(tabDesc);

        final HashMap<String, Object> tabSettingsValues = new HashMap<>();
        tabSettingsValues.put(FILTER_TABLE_SETTINGS,
                              dataSetEditorManager.getTableSettingsToStr(tableSettings));
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                              tableSettings.getTableName());
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                              tableSettings.getTableDescription());

        saveFilterIntoPreferences(tableSettings.getKey(),
                                  tabSettingsValues);

        addSavedFilter(tableSettings);
    }

    protected void addSavedFilter(final FilterSettings tableSettings) {
        view.addSavedFilter(new SavedFilter(tableSettings.getKey(),
                                            tableSettings.getTableName()));
    }

    public void saveFilterIntoPreferences(final String key,
                                          final HashMap<String, Object> params) {
        multiGridPreferencesStore.addNewTab(key,
                                            params);
        preferencesService.call().saveUserPreferences(multiGridPreferencesStore);
    }

    protected void loadSavedFiltersFromPreferences() {
        final ArrayList<String> existingGrids = new ArrayList<>(multiGridPreferencesStore.getGridsId());

        if (existingGrids.contains(TAB_SEARCH)) {
            removeSavedFilterFromPreferences(TAB_SEARCH);
            existingGrids.remove(TAB_SEARCH);
        }

        if (existingGrids.isEmpty()) {
            initDefaultFilters();
        } else {
            existingGrids.stream().map(key -> new SavedFilter(key,
                                                              getSavedFilterNameFromKey(key))).forEach(f -> view.addSavedFilter(f));
        }
    }

    protected String getSavedFilterNameFromKey(final String key) {
        return (String) ofNullable(multiGridPreferencesStore.getGridSettings(key))
                .orElseThrow(() -> new RuntimeException("Grid settings not found for key: " + key))
                .get(NewTabFilterPopup.FILTER_TAB_NAME_PARAM);
    }

    protected void removeSavedFilterFromPreferences(final String key) {
        multiGridPreferencesStore.removeTab(key);
        preferencesService.call().saveUserPreferences(multiGridPreferencesStore);
    }

    protected void removeSavedFilter(final SavedFilter savedFilter) {
        removeSavedFilterFromPreferences(savedFilter.getKey());
        view.removeSavedFilter(savedFilter);
    }

    protected void onRestoreFilters() {
        view.getSavedFilters().forEach(f -> removeSavedFilter(f));
        initDefaultFilters();
    }

    public void onRemoveSavedFilter(@Observes final RemoveSavedFilterEvent event) {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(constants.RemoveSavedFilterTitle(),
                                                                                 constants.RemoveSavedFilterMessage(event.getSavedFilter().getName()),
                                                                                 () -> removeSavedFilter(event.getSavedFilter()),
                                                                                 null,
                                                                                 Commands.DO_NOTHING);
        yesNoCancelPopup.show();
    }

    @Override
    public void onRestoreDefaultFilters() {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(constants.RestoreDefaultFilters(),
                                                                                 constants.AreYouSureRestoreDefaultFilters(),
                                                                                 () -> onRestoreFilters(),
                                                                                 null,
                                                                                 Commands.DO_NOTHING);
        yesNoCancelPopup.show();
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
