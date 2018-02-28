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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import org.dashbuilder.dataset.DataSetLookup;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.client.util.DateRange;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.list.DataSetEditorManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractMultiGridPresenter>
        extends Composite implements MultiGridView<T, V> {

    public static final String TAB_SEARCH = "base";
    public static final String FILTER_TABLE_SETTINGS = "tableSettings";
    public static final String USER_DEFINED = "ud_";
    public static final String COL_ID_SELECT = "Select";
    public static final String COL_ID_ACTIONS = "Actions";

    private final Constants constants = Constants.INSTANCE;

    @Inject
    protected User identity;

    protected Button createTabButton = GWT.create(Button.class);

    @Inject
    protected Event<NotificationEvent> notification;

    protected DataSetEditorManager dataSetEditorManager;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected AdvancedSearchFiltersViewImpl advancedSearchFiltersView;

    protected V presenter;

    protected FilterPagedTable<T> filterPagedTable = GWT.create(FilterPagedTable.class);

    protected ListTable<T> currentListGrid;

    @Inject
    @DataField("column")
    protected HTMLDivElement column;

    private Caller<UserPreferencesService> userPreferencesService;

    @PostConstruct
    public void init(){
        new Elemental2DomUtil().appendWidgetToElement(column, filterPagedTable.makeWidget());
        createTabButton.setIcon(IconType.PLUS);
        createTabButton.setSize(ButtonSize.SMALL);
    }

    public void init(final V presenter) {
        this.presenter = presenter;

        createTabButton.addClickHandler(event -> createNewTab(presenter));

        userPreferencesService.call((MultiGridPreferencesStore multiGridPreferencesStore) -> {
            if (multiGridPreferencesStore == null) {
                multiGridPreferencesStore = new MultiGridPreferencesStore(getGridGlobalPreferencesKey());
            }
            loadTabsFromPreferences(multiGridPreferencesStore,
                                    presenter);
        }).loadUserPreferences(getGridGlobalPreferencesKey(),
                               UserPreferencesType.MULTIGRIDPREFERENCES);
    }

    protected void createNewTab(final V presenter) {
        final String key = getValidKeyForAdditionalListGrid(getGridGlobalPreferencesKey() + "_");

        final Command addNewGrid = () -> {

            final ListTable<T> extendedPagedTable = createGridInstance(key);

            extendedPagedTable.setDataProvider(presenter.getDataProvider());

            filterPagedTable.createNewTab(extendedPagedTable,
                                          key,
                                          createTabButton,
                                          (() -> {
                                              currentListGrid = extendedPagedTable;
                                              applyFilterOnPresenter(key);
                                          }));
            applyFilterOnPresenter(key);
        };

        final FilterSettings tableSettings = presenter.createTableSettingsPrototype();
        tableSettings.setKey(key);
        dataSetEditorManager.showTableSettingsEditor(filterPagedTable,
                                                     getNewFilterPopupTitle(),
                                                     tableSettings,
                                                     addNewGrid);
    }

    protected void loadTabsFromPreferences(final MultiGridPreferencesStore multiGridPreferencesStore,
                                           final V presenter) {
        filterPagedTable.setMultiGridPreferencesStore(multiGridPreferencesStore);

        presenter.setAddingDefaultFilters(true);

        final ArrayList<String> existingGrids = new ArrayList<>(multiGridPreferencesStore.getGridsId());

        if (existingGrids.isEmpty()) {
            initDefaultFilters();
        } else {
            //Special handling for the search tab when upgrading from previous versions
            if (existingGrids.contains(TAB_SEARCH) == false) {
                initSearchFilter();
            }
            existingGrids.forEach(key -> {
                final ListTable<T> extendedPagedTable = loadGridInstance(key);
                extendedPagedTable.setDataProvider(presenter.getDataProvider());
                filterPagedTable.addTab(extendedPagedTable,
                                        key,
                                        () -> {
                                            currentListGrid = extendedPagedTable;
                                            applyFilterOnPresenter(key);
                                        },
                                        false);
            });
            //Ensure Search tab is always the first one
            if (multiGridPreferencesStore.getGridsId().indexOf(TAB_SEARCH) != 0) {
                multiGridPreferencesStore.getGridsId().remove(TAB_SEARCH);
                multiGridPreferencesStore.getGridsId().add(0,
                                                           TAB_SEARCH);
            }
        }
        multiGridPreferencesStore.setSelectedGrid(TAB_SEARCH);
        filterPagedTable.addAddTableButton(createTabButton);
        presenter.onGridPreferencesStoreLoaded();
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public void showRestoreDefaultFilterConfirmationPopup() {
        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(constants.RestoreDefaultFilters(),
                                                                                 constants.AreYouSureRestoreDefaultFilters(),
                                                                                 () -> {
                                                                                     showBusyIndicator(constants.Loading());
                                                                                     presenter.onRestoreTabs();
                                                                                 },
                                                                                 null,
                                                                                 () -> {
                                                                                 });
        yesNoCancelPopup.show();
    }

    public void restoreTabs() {
        presenter.setAddingDefaultFilters(true);
        final List<String> grids = new ArrayList<>(getMultiGridPreferencesStore().getGridsId());
        grids.forEach(key -> filterPagedTable.removeTab(key));
        filterPagedTable.removeTab(0);
        initDefaultFilters();
        filterPagedTable.addAddTableButton(createTabButton);
    }

    protected void controlBulkOperations(final ListTable<T> extendedPagedTable) {
        Scheduler.get().scheduleDeferred(() -> enableWidgets(Iterables.getFirst(extendedPagedTable.getRightActionsToolbar(),
                                                                                null),
                                                             extendedPagedTable.hasSelectedItems()));
    }

    protected void enableWidgets(final Widget widget,
                                 boolean enable) {
        if (widget == null) {
            return;
        }
        if (widget instanceof HasEnabled) {
            ((HasEnabled) widget).setEnabled(enable);
        }
        if (widget instanceof HasWidgets) {
            for (Widget w : (HasWidgets) widget) {
                enableWidgets(w,
                              enable);
            }
        }
    }

    public String getValidKeyForAdditionalListGrid(String baseName) {
        return filterPagedTable.getValidKeyForAdditionalListGrid(baseName + USER_DEFINED);
    }

    public ListTable<T> createGridInstance(final String key) {
        final ListTable<T> newListGrid = createExtendedPagedTable(key);
        newListGrid.setShowLastPagerButton(false);
        newListGrid.setShowFastFordwardPagerButton(false);
        newListGrid.setPreferencesService(userPreferencesService);
        initColumns(newListGrid);
        initSelectionModel(newListGrid);
        newListGrid.loadPageSizePreferences();
        newListGrid.createPageSizesListBox(5,
                                           20,
                                           5);

        return newListGrid;
    }

    protected ListTable<T> createExtendedPagedTable(final String key) {
        GridGlobalPreferences pref;
        ListTable<T> table;
        if (TAB_SEARCH.equals(key)) {
            pref = new GridGlobalPreferences(getGridGlobalPreferencesKey() + key,
                                             getInitColumns(),
                                             getBannedColumns());
            table = createAdvancedSearchTable(pref);
        } else {
            pref = new GridGlobalPreferences(key,
                                             getInitColumns(),
                                             getBannedColumns());
            table = new ListTable<T>(pref);
        }
        table.setGridPreferencesStore(new GridPreferencesStore(pref));
        return table;
    }

    protected ListTable<T> createAdvancedSearchTable(final GridGlobalPreferences preferences) {
        final ListTable advancedSearchTable = new ListTable<T>(preferences);
        advancedSearchTable.getElement().getStyle().setPaddingTop(0,
                                                                  Style.Unit.PX);
        advancedSearchTable.getTopToolbar().add(advancedSearchFiltersView);
        return advancedSearchTable;
    }

    public ListTable<T> loadGridInstance(final String key) {
        final ListTable<T> newListGrid = createExtendedPagedTable(key);
        newListGrid.setShowLastPagerButton(false);
        newListGrid.setShowFastFordwardPagerButton(false);
        newListGrid.dataGrid.addRedrawHandler(() -> controlBulkOperations(newListGrid));
        userPreferencesService.call((GridPreferencesStore preferencesStore) -> {
            newListGrid.setPreferencesService(userPreferencesService);
            if(preferencesStore != null) {
                newListGrid.setGridPreferencesStore(preferencesStore);
            }
            initColumns(newListGrid);
            initSelectionModel(newListGrid);
            newListGrid.loadPageSizePreferences();
            newListGrid.createPageSizesListBox(5,
                                               20,
                                               5);
        }).loadUserPreferences(newListGrid.getGridPreferencesStore().getPreferenceKey(),
                               UserPreferencesType.GRIDPREFERENCES);

        return newListGrid;
    }

    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public ListTable<T> getListGrid() {
        return currentListGrid;
    }

    /*
     * For each specific implementation define the
     *  DataGrid columns and how they must be initialized
     */
    public abstract void initColumns(ListTable<T> extendedPagedTable);

    public abstract void initSelectionModel(ListTable<T> extendedPagedTable);

    public abstract List<String> getInitColumns();

    public abstract List<String> getBannedColumns();

    public abstract String getGridGlobalPreferencesKey();

    public abstract String getNewFilterPopupTitle();

    public MultiGridPreferencesStore getMultiGridPreferencesStore() {
        if (filterPagedTable != null) {
            return filterPagedTable.getMultiGridPreferencesStore();
        }
        return null;
    }

    public void initDefaultFilters() {
        initSearchFilter();
    }

    protected void initSearchFilter() {
        //Search Tab
        final FilterSettings settings = presenter.createSearchTabSettings();
        settings.setTableName(constants.Search());
        settings.setTableDescription(constants.SearchResults());
        settings.setKey(TAB_SEARCH);
        addNewTab(settings);
    }

    public void initTabFilter(final FilterSettings tableSettings,
                              final String key,
                              final String tabName,
                              final String tabDesc,
                              final String dataSetUUID) {

        tableSettings.setKey(key);
        tableSettings.setTableName(tabName);
        tableSettings.setTableDescription(tabDesc);
        tableSettings.setUUID(dataSetUUID);

        addNewTab(tableSettings);
    }

    public void addNewTab(final FilterSettings tableSettings) {
        final HashMap<String, Object> tabSettingsValues = new HashMap<>();

        tabSettingsValues.put(FILTER_TABLE_SETTINGS,
                              dataSetEditorManager.getTableSettingsToStr(tableSettings));
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                              tableSettings.getTableName());
        tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                              tableSettings.getTableDescription());

        filterPagedTable.saveNewTabSettings(tableSettings.getKey(),
                                            tabSettingsValues);

        final ListTable<T> extendedPagedTable = createGridInstance(tableSettings.getKey());
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider(presenter.getDataProvider());

        filterPagedTable.addTab(extendedPagedTable,
                                tableSettings.getKey(),
                                () -> {
                                    currentListGrid = extendedPagedTable;
                                    applyFilterOnPresenter(tableSettings.getKey());
                                },
                                false
        );
    }

    public void selectFirstTabAndEnableQueries() {
        Scheduler.get().scheduleDeferred(getSelectFirstTabAndEnableQueriesCommand());
    }

    protected Scheduler.ScheduledCommand getSelectFirstTabAndEnableQueriesCommand() {
        return () -> {
            presenter.setAddingDefaultFilters(false);
            getMultiGridPreferencesStore().setSelectedGrid(TAB_SEARCH);
            filterPagedTable.setSelectedTab();
        };
    }

    public void applyFilterOnPresenter(final String key) {
        applyFilterOnPresenter(getTableFilterSettings(key));
    }

    public void applyFilterOnPresenter(final FilterSettings filterSettings) {
        presenter.filterGrid(filterSettings);
    }

    protected FilterSettings getTableFilterSettings(final String key) {
        final HashMap<String, Object> params = getGridSettings(key);
        final String tableSettingsJSON = (String) params.get(FILTER_TABLE_SETTINGS);
        return dataSetEditorManager.getStrToTableSettings(tableSettingsJSON);
    }

    @Override
    public FilterSettings getAdvancedSearchFilterSettings() {
        return getTableFilterSettings(TAB_SEARCH);
    }

    @Override
    public void saveAdvancedSearchFilterSettings(final FilterSettings settings) {
        final HashMap<String, Object> gridSettings = getGridSettings(TAB_SEARCH);
        gridSettings.put(FILTER_TABLE_SETTINGS,
                         dataSetEditorManager.getTableSettingsToStr(settings));
        filterPagedTable.saveTabSettings(TAB_SEARCH,
                                         gridSettings);
    }

    protected HashMap<String, Object> getGridSettings(final String key) {
        return filterPagedTable.getMultiGridPreferencesStore().getGridSettings(key);
    }

    public void setIdentity(User identity) {
        this.identity = identity;
    }

    @Inject
    public void setUserPreferencesService(final Caller<UserPreferencesService> userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
        this.filterPagedTable.setPreferencesService(userPreferencesService);
    }

    @Inject
    public void setDataSetEditorManager(final DataSetEditorManager dataSetEditorManager) {
        this.dataSetEditorManager = dataSetEditorManager;
    }

    public FilterPagedTable<T> getFilterPagedTable() {
        return filterPagedTable;
    }

    public void setFilterPagedTable(FilterPagedTable<T> filterPagedTable) {
        this.filterPagedTable = filterPagedTable;
    }

    public Column<T, String> createTextColumn(final String columnId,
                                              final Function<T, String> valueFunction) {
        Column<T, String> column = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T domain) {
                return valueFunction.apply(domain);
            }
        };
        column.setSortable(true);
        column.setDataStoreName(columnId);
        return column;
    }

    public Column<T, Number> createNumberColumn(final String columnId,
                                                final Function<T, Number> valueFunction) {
        Column<T, Number> column = new Column<T, Number>(new NumberCell()) {
            @Override
            public Number getValue(T domain) {
                return valueFunction.apply(domain);
            }
        };
        column.setSortable(true);
        column.setDataStoreName(columnId);
        return column;
    }

    protected ColumnMeta<T> initChecksColumn(final ListTable<T> extendedPagedTable) {
        CheckboxCell checkboxCell = new CheckboxCell(true,
                                                     false);
        Column<T, Boolean> checkColumn = new Column<T, Boolean>(checkboxCell) {
            @Override
            public Boolean getValue(T pis) {
                // Get the value from the selection model.
                return pis.isSelected();
            }
        };

        Header<Boolean> selectPageHeader = new Header<Boolean>(checkboxCell) {
            @Override
            public Boolean getValue() {
                return extendedPagedTable.isAllItemsSelected();
            }
        };

        selectPageHeader.setUpdater(value -> {
            getListGrid().getVisibleItems().forEach(pis -> extendedPagedTable.setItemSelection(pis,
                                                                                               value));
            getListGrid().redraw();
        });

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName(COL_ID_SELECT);
        ColumnMeta<T> checkColMeta = new ColumnMeta<T>(checkColumn,
                                                       "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue(int newValue) {
        filterPagedTable.saveNewRefreshInterval(newValue);
    }

    @Override
    public void addTextFilter(String label,
                              String placeholder,
                              Consumer<String> addCallback,
                              Consumer<String> removeCallback) {
        advancedSearchFiltersView.addTextFilter(label,
                                                placeholder,
                                                addCallback,
                                                removeCallback);
    }

    @Override
    public void addNumericFilter(String label,
                                 String placeholder,
                                 Consumer<String> addCallback,
                                 Consumer<String> removeCallback) {
        advancedSearchFiltersView.addNumericFilter(label,
                                                   placeholder,
                                                   addCallback,
                                                   removeCallback);
    }

    @Override
    public void addSelectFilter(String label,
                                Map<String, String> options,
                                Boolean liveSearch,
                                Consumer<String> addCallback,
                                Consumer<String> removeCallback) {
        advancedSearchFiltersView.addSelectFilter(label,
                                                  options,
                                                  liveSearch,
                                                  addCallback,
                                                  removeCallback);
    }

    @Override
    public <T extends Object> void addActiveFilter(String labelKey,
                                                   String labelValue,
                                                   T value,
                                                   Consumer<T> removeCallback) {
        advancedSearchFiltersView.addActiveFilter(labelKey,
                                                  labelValue,
                                                  value,
                                                  removeCallback);
    }

    @Override
    public void addDataSetSelectFilter(String label,
                                       String tableKey,
                                       DataSetLookup lookup,
                                       String textColumnId,
                                       String valueColumnId,
                                       Consumer<String> addCallback,
                                       Consumer<String> removeCallback) {
        advancedSearchFiltersView.addDataSetSelectFilter(label,
                                                         tableKey,
                                                         lookup,
                                                         textColumnId,
                                                         valueColumnId,
                                                         addCallback,
                                                         removeCallback);
    }

    @Override
    public void addDateRangeFilter(String label,
                                   String placeholder,
                                   Boolean useMaxDate,
                                   Consumer<DateRange> addCallback,
                                   Consumer<DateRange> removeCallback) {
        advancedSearchFiltersView.addDateRangeFilter(label,
                                                     placeholder,
                                                     useMaxDate,
                                                     addCallback,
                                                     removeCallback);
    }

    @Override
    public void removeAllActiveFilters() {
        advancedSearchFiltersView.removeAllActiveFilters();
    }

    interface Binder extends UiBinder<Widget, AbstractMultiGridView> {

    }
}