/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.experimental.grid.base;

import java.util.Collection;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.view.client.ProvidesKey;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;

/**
 * @author salaboy
 */
public class ExtendedPagedTable<T extends GenericSummary> extends PagedTable<T> {

    public ExtendedPagedTable( int pageSize,
                               GridGlobalPreferences gridPreferences ) {
        super( pageSize, new ProvidesKey<T>() {

            @Override
            public Object getKey( T item ) {
                return ( item == null ) ? null : item.getId();
            }
        }, gridPreferences, true );

        dataGrid.addColumnSortHandler( new AsyncHandler( dataGrid ) );
    }

    public void setTooltip( int row,
                            int column,
                            String description ) {
        dataGrid.getRowElement( row ).getCells().getItem( column ).setTitle( description );
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

    public void removeColumn( Column<T, ?> col ) {
        dataGrid.removeColumn( col );
    }

    public void removeColumnMeta( ColumnMeta<T> columnMeta ) {
        columnPicker.removeColumn( columnMeta );
    }

    public Collection<ColumnMeta<T>> getColumnMetaList() {
        return columnPicker.getColumnMetaList();
    }

}
