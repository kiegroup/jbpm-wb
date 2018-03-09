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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ActiveFilters;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class AbstractMultiGridView<T extends GenericSummary, V extends AbstractMultiGridPresenter>
        extends Composite implements MultiGridView<T, V> {

    public static final String COL_ID_SELECT = "Select";
    public static final String COL_ID_ACTIONS = "Actions";

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected PlaceManager placeManager;

    protected Caller<UserPreferencesService> userPreferencesService;

    protected ListTable<T> listTable;

    protected V presenter;

    @Inject
    @DataField("column")
    protected HTMLDivElement column;

    @Inject
    @DataField("active-filters")
    protected ActiveFilters filters;

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public void init(final V presenter) {
        this.presenter = presenter;
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

    @Override
    public void loadListTable(final String key,
                              final Consumer<ListTable<T>> readyCallback) {
        final GridGlobalPreferences pref = new GridGlobalPreferences(key,
                                                                     getInitColumns(),
                                                                     getBannedColumns());
        final ListTable<T> newListGrid = new ListTable<T>(pref);
        newListGrid.setShowLastPagerButton(false);
        newListGrid.setShowFastFordwardPagerButton(false);
        newListGrid.dataGrid.addRedrawHandler(() -> controlBulkOperations(newListGrid));
        newListGrid.setPreferencesService(userPreferencesService);
        userPreferencesService.call((GridPreferencesStore preferencesStore) -> {
            if (preferencesStore == null) {
                newListGrid.setGridPreferencesStore(new GridPreferencesStore(pref));
            } else {
                newListGrid.setGridPreferencesStore(preferencesStore);
            }
            initColumns(newListGrid);
            initSelectionModel(newListGrid);
            newListGrid.loadPageSizePreferences();
            newListGrid.createPageSizesListBox(5,
                                               20,
                                               5);
            if (column.childNodes.length == 1) {
                column.removeChild(column.firstChild);
            }
            new Elemental2DomUtil().appendWidgetToElement(column,
                                                          newListGrid);
            listTable = newListGrid;
            readyCallback.accept(listTable);
        }).loadUserPreferences(key,
                               UserPreferencesType.GRIDPREFERENCES);
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

    public abstract void initSelectionModel(ListTable<T> extendedPagedTable);

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
        selectPageHeader.setHeaderStyleNames("kie-datatable-select");

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName(COL_ID_SELECT);
        checkColumn.setCellStyleNames("kie-datatable-select");
        ColumnMeta<T> checkColMeta = new ColumnMeta<T>(checkColumn,
                                                       "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
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
    public void removeAllActiveFilters() {
        filters.removeAllActiveFilters();
    }
}