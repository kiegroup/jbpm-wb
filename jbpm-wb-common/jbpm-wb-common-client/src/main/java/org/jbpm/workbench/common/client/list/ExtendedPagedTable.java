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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import org.jbpm.workbench.common.client.resources.CommonResources;
import org.jbpm.workbench.common.client.resources.css.CommonCSS;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

public class ExtendedPagedTable<T extends GenericSummary> extends PagedTable<T> {

    private final CommonCSS commonCSS = CommonResources.INSTANCE.css();

    private List<Column<T, ?>> ignoreSelectionColumns = new ArrayList<Column<T, ?>>();

    private int selectedRow = -1;

    private BiConsumer<T, Boolean> selectionCallback = null;

    public ExtendedPagedTable(final GridGlobalPreferences gridPreferences) {
        super(DEFAULT_PAGE_SIZE,
              (T item) -> (item == null) ? null : item.getId(),
              gridPreferences,
              true);

        dataGrid.addColumnSortHandler(new AsyncHandler(dataGrid));
        setSelectionModel(createSelectionModel(),
                          createNoActionColumnManager());
        setRowStyles((T row,
                      int rowIndex) -> {
            if (rowIndex == selectedRow) {
                return commonCSS.selected();
            }
            return null;
        });
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
        return StreamSupport.stream(this.getVisibleItems().spliterator(),
                                    false).filter(domain -> domain.isSelected()).collect(Collectors.toList());
    }

    public boolean hasSelectedItems() {
        return StreamSupport.stream(this.getVisibleItems().spliterator(),
                                    false).anyMatch(domain -> domain.isSelected());
    }

    public void deselectAllItems() {
        this.getVisibleItems().forEach(pis -> pis.setSelected(false));
    }

    public boolean isAllItemsSelected() {
        if (this.getVisibleItemCount() == 0) {
            return false;
        } else {
            return StreamSupport.stream(this.getVisibleItems().spliterator(),
                                        false).allMatch(pis -> pis.isSelected());
        }
    }

    protected NoSelectionModel<T> createSelectionModel() {
        final NoSelectionModel<T> selectionModel = new NoSelectionModel<T>();
        final ExtendedPagedTable<T> extendedPagedTable = this;
        selectionModel.addSelectionChangeHandler(event -> {
            boolean close = false;
            if (selectedRow == -1 || extendedPagedTable.getKeyboardSelectedRow() != selectedRow) {
                selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                extendedPagedTable.redraw();
            } else {
                close = true;
            }

            if (selectionCallback != null) {
                selectionCallback.accept(selectionModel.getLastSelectedObject(),
                                         close);
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
                DefaultSelectionEventManager.SelectAction ret = DefaultSelectionEventManager.SelectAction.DEFAULT;
                NativeEvent nativeEvent = event.getNativeEvent();
                if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                    // Ignore if the event didn't occur in the correct column.
                    if (extendedPagedTable.isSelectionIgnoreColumn(event.getColumn())) {
                        ret = DefaultSelectionEventManager.SelectAction.IGNORE;
                    }
                    //Extension for checkboxes
                    Element target = nativeEvent.getEventTarget().cast();
                    if ("input".equals(target.getTagName().toLowerCase())) {
                        final InputElement input = target.cast();
                        if ("checkbox".equals(input.getType().toLowerCase())) {
                            // Synchronize the checkbox with the current selection state.
                            final T domain = event.getValue();
                            if (domain.isSelected()) {
                                input.setChecked(false);
                                domain.setSelected(false);
                            } else {
                                input.setChecked(true);
                                domain.setSelected(true);
                            }
                            extendedPagedTable.redraw();
                            ret = DefaultSelectionEventManager.SelectAction.IGNORE;
                        }
                    }
                }
                return ret;
            }
        });
    }

    public void setSelectionCallback(final BiConsumer<T, Boolean> selectionCallback) {
        this.selectionCallback = selectionCallback;
    }

    public int getSelectedRow() {
        return selectedRow;
    }
}
