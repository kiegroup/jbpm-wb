/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ActiveFilters;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.common.client.util.BlockingError;
import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.common.client.util.ConditionalKebabActionCell;
import org.jbpm.workbench.common.model.GenericSummary;
import org.jbpm.workbench.common.preferences.ManagePreferences;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.GridSortedColumnPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractMultiGridPresenter> extends Composite implements MultiGridView<T, V>,
                                                                                                                                         RequiresResize {

    public static final String COL_ID_SELECT = "Select";
    public static final String COL_ID_ACTIONS = "Actions";
    public static final int ACTIONS_COLUMN_WIDTH = 175;
    public static final int CHECK_COLUMN_WIDTH = 38;
    public static final int ERROR_COLUMN_WIDTH = 65;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected ManagedInstance<ConditionalKebabActionCell> conditionalKebabActionCell;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected ManagePreferences preferences;

    protected Caller<UserPreferencesService> userPreferencesService;

    protected ListTable<T> listTable;

    protected V presenter;

    @Inject
    @DataField("column")
    protected HTMLDivElement column;

    @Inject
    @DataField("active-filters")
    protected ActiveFilters filters;

    @Inject
    @DataField("alert")
    protected BlockingError alert;

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public void displayBlockingError(String summary,
                                     String content) {
        column.classList.add("hidden");
        filters.getElement().classList.add("hidden");
        alert.getElement().classList.remove("hidden");
        alert.setSummary(summary);
        alert.setDescription(content);
    }

    @Override
    public void clearBlockingError() {
        filters.getElement().classList.remove("hidden");
        alert.getElement().classList.add("hidden");
        alert.setSummary("");
        alert.setDescription("");
        column.classList.remove("hidden");
    }

    public void init(final V presenter) {
        this.presenter = presenter;
    }

    protected void controlBulkOperations(final ListTable<T> extendedPagedTable) {
        enableWidgets(StreamSupport.stream(extendedPagedTable.getRightActionsToolbar().spliterator(),
                                           false).findFirst().orElse(null),
                      extendedPagedTable.hasSelectedItems());
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

    @Override
    public void loadListTable(final String key,
                              final Consumer<ListTable<T>> readyCallback) {
        preferences.load(preferences -> {
            final GridGlobalPreferences pref = new GridGlobalPreferences(key,
                                                                         getInitColumns(),
                                                                         getBannedColumns());
            pref.setPageSize(preferences.getItemsPerPage());
            final ListTable<T> newListGrid = new ListTable<>(pref);
            newListGrid.setShowLastPagerButton(false);
            newListGrid.setShowFastFordwardPagerButton(false);
            newListGrid.enableDataGridMinWidth(true);
            initSelectionModel(newListGrid);
            addColumnSortHandler(newListGrid);

            userPreferencesService.call((GridPreferencesStore preferencesStore) -> {
                if (preferencesStore == null) {
                    newListGrid.setGridPreferencesStore(new GridPreferencesStore(pref));
                } else {
                    newListGrid.setGridPreferencesStore(preferencesStore);
                }

                initColumns(newListGrid);

                newListGrid.loadPageSizePreferences();
                newListGrid.setPreferencesService(userPreferencesService);

                addNewTableToColumn(newListGrid);

                listTable = newListGrid;
                readyCallback.accept(listTable);
            }).loadUserPreferences(key,
                                   UserPreferencesType.GRIDPREFERENCES);
        }, error -> new DefaultWorkbenchErrorCallback().error(error));
    }

    protected void addColumnSortHandler(ExtendedPagedTable listTable) {
        listTable.addColumnSortHandler(event -> {
            GridPreferencesStore gridPreferencesStore = listTable.getGridPreferencesStore();
            if (gridPreferencesStore != null && event.getColumnSortList().size() > 0) {
                ColumnSortList.ColumnSortInfo columnSortInfo = event.getColumnSortList().get(0);
                if (columnSortInfo != null) {
                    GridSortedColumnPreference gridSortedColumnPreference = new GridSortedColumnPreference(columnSortInfo.getColumn().getDataStoreName(),
                                                                                                           columnSortInfo.isAscending());
                    gridPreferencesStore.setGridSortedColumnPreference(gridSortedColumnPreference);
                }
            }
            listTable.saveGridPreferences();
        });
    }

    protected void addNewTableToColumn(final ListTable<T> newListGrid) {
        if (column.childNodes.length == 1) {
            column.removeChild(column.firstChild);
        }
        new Elemental2DomUtil().appendWidgetToElement(column,
                                                      newListGrid);
    }

    protected Column initGenericColumn(final String key){
        return null;
    }

    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    public ListTable<T> getListGrid() {
        return listTable;
    }

    /*
     * For each specific implementation define the
     *  DataGrid columns and how they must be initialized
     */
    public abstract void initColumns(ListTable<T> extendedPagedTable);

    public void initSelectionModel(final ListTable<T> extendedPagedTable) {
        extendedPagedTable.setEmptyTableCaption(getEmptyTableCaption());
        extendedPagedTable.setSelectionCallback((s) -> presenter.selectSummaryItem(s));
        initBulkActions(extendedPagedTable);
    }

    public abstract String getEmptyTableCaption();

    public abstract List<String> getInitColumns();

    public abstract List<String> getBannedColumns();

    @Inject
    public void setUserPreferencesService(final Caller<UserPreferencesService> userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
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

    protected void initBulkActions(final ExtendedPagedTable<T> extendedPagedTable) {
        extendedPagedTable.getRightActionsToolbar().clear();

        final ButtonGroup bulkActions = GWT.create(ButtonGroup.class);

        final Button bulkButton = GWT.create(Button.class);
        bulkButton.setText(Constants.INSTANCE.Bulk_Actions());
        bulkButton.setDataToggle(Toggle.DROPDOWN);
        bulkButton.setEnabled(false);
        bulkButton.getElement().getStyle().setMarginRight(5,
                                                          Style.Unit.PX);
        bulkActions.add(bulkButton);

        final DropDownMenu bulkDropDown = GWT.create(DropDownMenu.class);
        bulkDropDown.addStyleName(Styles.DROPDOWN_MENU + "-right");
        bulkDropDown.getElement().getStyle().setMarginRight(5,
                                                            Style.Unit.PX);
        for (AnchorListItem item : getBulkActionsItems(extendedPagedTable)) {
            bulkDropDown.add(item);
        }

        bulkActions.add(bulkDropDown);

        extendedPagedTable.getRightActionsToolbar().add(bulkActions);
    }

    protected List<AnchorListItem> getBulkActionsItems(ExtendedPagedTable<T> extendedPagedTable) {
        return Collections.emptyList();
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

    public ColumnMeta<T> initChecksColumn(final ListTable<T> extendedPagedTable) {
        CheckboxCell checkboxCell = new CheckboxCell(true,
                                                     false);
        Column<T, Boolean> checkColumn = new Column<T, Boolean>(checkboxCell) {
            @Override
            public Boolean getValue(T item) {
                // Get the value from the selection model.
                return extendedPagedTable.isItemSelected(item);
            }
        };

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName(COL_ID_SELECT);
        checkColumn.setCellStyleNames("kie-datatable-select");

        Header<Boolean> selectPageHeader = new Header<Boolean>(checkboxCell) {
            @Override
            public Boolean getValue() {
                return extendedPagedTable.isAllItemsSelected();
            }
        };

        selectPageHeader.setUpdater(value -> {
            if (value) {
                extendedPagedTable.selectAllItems();
            } else {
                extendedPagedTable.deselectAllItems();
            }
            controlBulkOperations(extendedPagedTable);
        });
        selectPageHeader.setHeaderStyleNames("kie-datatable-select");

        checkColumn.setFieldUpdater((int index,
                                     T model,
                                     Boolean value) -> {
            extendedPagedTable.setItemSelection(model,
                                                value);

            controlBulkOperations(extendedPagedTable);
        });

        ColumnMeta<T> checkColMeta = new ColumnMeta<T>(checkColumn,
                                                       "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
    }

    protected boolean isColumnAdded(List<ColumnMeta<T>> columnMetas,
                                    String caption) {
        if (caption != null) {
            for (ColumnMeta<T> colMet : columnMetas) {
                if (caption.equals(colMet.getColumn().getDataStoreName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean existsColumnWithSameName(GridColumnPreference gridColumnPreference, List<ColumnMeta<T>> columns) {
        return columns.stream().filter(meta -> meta.getCaption().equals(gridColumnPreference.getName())).findFirst().isPresent();
    }

    protected abstract List<ConditionalAction<T>> getConditionalActions();

    public ColumnMeta<T> initActionsColumn() {
        final ConditionalKebabActionCell<T> cell = conditionalKebabActionCell.get();

        cell.setActions(getConditionalActions());

        Column<T, T> actionsColumn = new Column<T, T>(cell) {
            @Override
            public T getValue(T object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        actionsColumn.setCellStyleNames("kie-table-view-pf-actions text-center");

        Header header = new TextHeader(Constants.INSTANCE.Actions());
        header.setHeaderStyleNames("text-center");

        final ColumnMeta<T> actionsColMeta = new ColumnMeta<T>(actionsColumn,
                                                               "");
        actionsColMeta.setHeader(header);
        actionsColMeta.setVisibleIndex(false);
        return actionsColMeta;
    }

    public List<ColumnMeta<T>> renameVariables(ListTable<T> extendedPagedTable, List<ColumnMeta<T>> columnMetas) {
        List<GridColumnPreference> columnPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();

        return (List<ColumnMeta<T>>) columnPreferenceList.stream().filter(colPref -> !isColumnAdded(columnMetas, colPref.getName())).map(colPref -> {
            return newColumnMeta(colPref.getName(),
                                 existsColumnWithSameName(colPref,
                                                          columnMetas),
                                 false);
        }).collect(Collectors.toList());
    }

    private ColumnMeta<T> newColumnMeta(String columnName, boolean existsColumnWithSameName, boolean isVisible) {

        Column genericColumn = initGenericColumn(columnName);
        genericColumn.setSortable(false);

        String caption = !existsColumnWithSameName ? columnName : "Var_" + columnName;
        return new ColumnMeta<T>(genericColumn,
                                 caption,
                                 isVisible,
                                 true);
    }

    public void addDomainSpecifColumns(Set<String> columns) {
        getListGrid().storeColumnToPreferences();

        List<GridColumnPreference> gridColumnPreferenceList = removeRedundantColumns(getListGrid(), columns);

        List<ColumnMeta<T>> columnMetaList = renameDomainSpecifColumns(getListGrid(), columns);

        addDomainColumns(columnMetaList, columns);

        getListGrid().getGridPreferencesStore().getColumnPreferences().addAll(gridColumnPreferenceList);
        columnMetaList.forEach(columnMeta -> {
            List<GridColumnPreference> list = gridColumnPreferenceList.stream().filter(gridColumnPreference -> gridColumnPreference.getName().equals(columnMeta.getColumn().getDataStoreName())).collect(Collectors.toList());
            getListGrid().getGridPreferencesStore().getColumnPreferences().addAll(list);
            getListGrid().addColumns(Arrays.asList(columnMeta));
        });
    }

    private void addDomainColumns(List<ColumnMeta<T>> columnMetaList, Set<String> columns) {
        columns.stream().filter(newColumn -> newColumnIsNotInDataStoreNames(newColumn, columnMetaList))
                .forEach(column -> {
                    ColumnMeta<T> columnMeta = newColumnMeta(column, false, false);
                    columnMetaList.add(columnMeta);
                });
    }

    private boolean newColumnIsNotInDataStoreNames(String newColumn, List<ColumnMeta<T>> columnMetaList) {
        return columnMetaList.stream()
                .map(colMeta -> colMeta.getColumn().getDataStoreName())
                .noneMatch(newColumn::contains);
    }

    private List<ColumnMeta<T>> renameDomainSpecifColumns(ExtendedPagedTable<T> extendedPagedTable,
                                                          Set<String> columns) {
        return (List<ColumnMeta<T>>) extendedPagedTable.getColumnMetaList().stream()
                .filter(cm -> !cm.isExtraColumn() && columns.contains(cm.getCaption()))
                .map(colMet -> newColumnMeta(colMet.getCaption(), true, false)
                ).collect(Collectors.toList());
    }

    private List<GridColumnPreference> removeRedundantColumns(ExtendedPagedTable<T> extendedPagedTable,
                                                              Set<String> columns) {

        List<ColumnMeta<T>> columnMetas = extendedPagedTable.getColumnMetaList().stream()
                .filter(cm -> cm.isExtraColumn())
                .collect(Collectors.toList());

        List<GridColumnPreference> gridColumnPreferenceList = new ArrayList<GridColumnPreference>();
        columnMetas.forEach(colMet -> {
            if (!columns.contains(colMet.getCaption())) {
                List<GridColumnPreference> columnPreferences = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();
                columnPreferences.stream().filter(columnPreference -> columnPreference.getName().equals(colMet.getColumn().getDataStoreName())).forEach(columnPreference -> gridColumnPreferenceList.add(columnPreference));
                extendedPagedTable.removeColumnMeta(colMet);
            } else {
                columns.remove(colMet.getCaption());
            }
        });

        return gridColumnPreferenceList;
    }

    public void removeDomainSpecifColumns() {
        final ListTable<T> listGrid = getListGrid();
        userPreferencesService.call((GridPreferencesStore preferencesStore) -> {
            List<ColumnMeta<T>> metas = listGrid.getColumnMetaList().stream().filter(c -> c.isExtraColumn() && c.isVisible()).collect(Collectors.toList());
            metas.forEach(meta -> {
                listGrid.removeColumnMeta(meta);
                Optional<GridColumnPreference> preference = preferencesStore.getColumnPreferences().stream().filter(pref -> pref.getName().equals(meta.getColumn().getDataStoreName())
                ).findFirst();
                if (preference.isPresent()) {
                    preferencesStore.getColumnPreferences().remove(preference.get());
                }
            });
            listGrid.saveGridPreferences();
        }).loadUserPreferences(listGrid.getGridPreferencesStore().getPreferenceKey(), UserPreferencesType.GRIDPREFERENCES);
    }

    @Override
    public void setSaveFilterCallback(final BiConsumer<String, Consumer<String>> filterNameCallback) {
        filters.setSaveFilterCallback(filterNameCallback);
    }

    @Override
    public <T extends Object> void addActiveFilter(final ActiveFilterItem<T> filter) {
        filters.addActiveFilter(filter);
    }

    @Override
    public <T extends Object> void removeActiveFilter(final ActiveFilterItem<T> filter) {
        filters.removeActiveFilter(filter);
    }

    @Override
    public void removeAllActiveFilters() {
        filters.removeAllActiveFilters();
    }

    @Override
    public void onResize() {
        if (listTable != null) {
            listTable.onResize();
        }
    }

    @Override
    public ColumnSortList reloadColumnSortList() {
        ColumnSortList columnSortList = getListGrid().getColumnSortList();
        GridPreferencesStore gridPreferencesStore = getListGrid().getGridPreferencesStore();
        if (gridPreferencesStore != null) {
            GridSortedColumnPreference gridSortedColumnPreference = gridPreferencesStore.getGridSortedColumnPreference();
            //Avoid duplicating the call push method of ColumnSortList when catch ColumnSortedEvent
            if (gridSortedColumnPreference != null && columnSortList.size() <= 1) {
                Optional<ColumnMeta<T>> optional = getListGrid().getColumnMetaList().stream().filter(
                        tColumnMeta -> tColumnMeta.getColumn().getDataStoreName().equals(gridSortedColumnPreference.getDataStoreName()))
                        .findFirst();
                if (optional.isPresent()) {
                    if (columnSortList.get(0).getColumn().getDataStoreName().equals(gridSortedColumnPreference.getDataStoreName())) {
                        columnSortList.push(getSecondSortColumn());
                    }
                    Column col = optional.get().getColumn();
                    col.setDataStoreName(gridSortedColumnPreference.getDataStoreName());
                    columnSortList.push(new ColumnSortList.ColumnSortInfo(col, gridSortedColumnPreference.isAscending()));
                }
            }
        }
        return columnSortList;
    }

    public abstract Column<T, String> getSecondSortColumn();
}
