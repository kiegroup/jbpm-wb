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
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

public class ExtendedPagedTable<T extends GenericSummary> extends PagedTable<T> {

    private List<Column<T, ?>> ignoreSelectionColumns = new ArrayList<Column<T, ?>>();

    private List<T> selectedItems = new ArrayList<T>();

    private Consumer<T> selectionCallback;

    public ExtendedPagedTable(final GridGlobalPreferences gridPreferences) {
        super(DEFAULT_PAGE_SIZE,
              (T item) -> (item == null) ? null : item.getId(),
              gridPreferences,
              true,
              false,
              false,
              false);

        dataGrid.addColumnSortHandler(new AsyncHandler(dataGrid));
        setSelectionModel(createSelectionModel(),
                          createNoActionColumnManager());
    }

    public void setTooltip(int row,
                           int column,
                           String description) {
        dataGrid.getRowElement(row).getCells().getItem(column).setTitle(description);
    }

    public int getKeyboardSelectedColumn() {
        return dataGrid.getKeyboardSelectedColumn();
    }

    public int getKeyboardSelectedRow() {
        return dataGrid.getKeyboardSelectedRow();
    }

    public int getColumnCount() {
        return dataGrid.getColumnCount();
    }

    public void removeColumn(Column<T, ?> col) {
        dataGrid.removeColumn(col);
    }

    public void removeColumnMeta(ColumnMeta<T> columnMeta) {
        columnPicker.removeColumn(columnMeta);
    }

    public Collection<ColumnMeta<T>> getColumnMetaList() {
        return columnPicker.getColumnMetaList();
    }

    public void addSelectionIgnoreColumn(Column<T, ?> column) {
        if (!ignoreSelectionColumns.contains(column)) {
            ignoreSelectionColumns.add(column);
        }
    }

    public boolean removeSelectionIgnoreColumn(Column<T, ?> column) {
        return ignoreSelectionColumns.remove(column);
    }

    public boolean isSelectionIgnoreColumn(int colIx) {
        return (colIx >= 0 && ignoreSelectionColumns.stream().anyMatch(col -> (getColumnIndex(col) == colIx)));
    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

    protected void setSelectedItems(List<T> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public boolean isItemSelected(T item) {
        return selectedItems.contains(item);
    }

    public boolean hasSelectedItems() {
        return StreamSupport.stream(getVisibleItems().spliterator(),
                                    false).anyMatch(item -> isItemSelected(item));
    }

    public void deselectAllItems() {
        for (int i = 0; i < getVisibleItemCount(); i++) {
            final T item = getVisibleItem(i);
            if (selectedItems.contains(item)) {
                updateSelectedColumnRow(i,
                                        item,
                                        false);
            }
        }
        selectedItems.clear();
    }

    public void selectAllItems() {
        for (int i = 0; i < getVisibleItemCount(); i++) {
            final T item = getVisibleItem(i);
            if (selectedItems.contains(item) == false) {
                updateSelectedColumnRow(i,
                                        item,
                                        true);
            }
        }
    }

    public void updateSelectedColumnRow(final Integer row,
                                        final T object,
                                        final Boolean value) {
        final Column<T, Boolean> column = (Column<T, Boolean>) this.dataGrid.getColumn(0);
        column.getFieldUpdater().update(row,
                                        object,
                                        value);
        dataGrid.redrawRow(row);
    }

    public boolean isAllItemsSelected() {
        if (getVisibleItemCount() == 0) {
            return false;
        } else {
            return StreamSupport.stream(getVisibleItems().spliterator(),
                                        false).allMatch(item -> isItemSelected(item));
        }
    }

    protected NoSelectionModel<T> createSelectionModel() {
        final NoSelectionModel<T> selectionModel = new NoSelectionModel<T>();
        selectionModel.addSelectionChangeHandler(event -> {
            if (selectionCallback != null) {
                selectionCallback.accept(selectionModel.getLastSelectedObject());
            }
        });
        return selectionModel;
    }

    protected DefaultSelectionEventManager<T> createNoActionColumnManager() {
        final ExtendedPagedTable<T> extendedPagedTable = this;
        return DefaultSelectionEventManager.createCustomManager(new DefaultSelectionEventManager.EventTranslator<T>() {

            @Override
            public boolean clearCurrentSelection(CellPreviewEvent<T> event) {
                return false;
            }

            @Override
            public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<T> event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                if (BrowserEvents.CLICK.equals(nativeEvent.getType()) && extendedPagedTable.isSelectionIgnoreColumn(event.getColumn())) {
                    // Ignore if the event didn't occur in the correct column.
                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                } else {
                    return DefaultSelectionEventManager.SelectAction.DEFAULT;
                }
            }
        });
    }

    public void setSelectionCallback(final Consumer<T> selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    public void setItemSelection(final T item,
                                 final Boolean newValue) {
        if (item == null) {
            return;
        }

        if (newValue == isItemSelected(item)) {
            return;
        }

        if (newValue) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }

        dataGrid.redrawHeaders();
    }
}
