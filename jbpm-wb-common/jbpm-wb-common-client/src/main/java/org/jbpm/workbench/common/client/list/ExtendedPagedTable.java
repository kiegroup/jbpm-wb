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

import java.util.Collection;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

public class ExtendedPagedTable<T extends GenericSummary> extends PagedTable<T> {

    public ExtendedPagedTable(GridGlobalPreferences gridPreferences) {
        super(DEFAULT_PAGE_SIZE,
              (T item) -> (item == null) ? null : item.getId(),
              gridPreferences,
              true);

        dataGrid.addColumnSortHandler(new AsyncHandler(dataGrid));

        setShowLastPagerButton(false);
        setShowFastFordwardPagerButton(false);
        createPageSizesListBox(5,
                               20,
                               5);
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
}
