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

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;

import com.google.gwt.view.client.ProvidesKey;
import java.util.Map;
import org.jbpm.console.ng.ga.model.GenericSummary;
import org.kie.uberfire.client.tables.PagedTable;

/**
 *
 * @author salaboy
 */
public class ExtendedPagedTable<T extends GenericSummary> extends PagedTable<T> {

  // it is rgb because datagrid returns this kind of info
  private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";


  public ExtendedPagedTable(int pageSize, Map<String, String> params) {
    super(pageSize, new ProvidesKey<T>() {

      @Override
      public Object getKey(T item) {
        return (item == null) ? null : item.getId();
      }
    }, params);

    dataGrid.addColumnSortHandler(new AsyncHandler(dataGrid));

  }

 
  

  public void paintRow(int row) {
    for (int i = 0; i < dataGrid.getRowElement(row).getCells().getLength(); i++) {
      dataGrid.getRowElement(row).getCells().getItem(i).getStyle().setBackgroundColor(BG_ROW_SELECTED);
    }
  }

  public void clearRow(int row) {
    for (int i = 0; i < dataGrid.getRowElement(row).getCells().getLength(); i++) {
      dataGrid.getRowElement(row).getCells().getItem(i).getStyle().clearBackgroundColor();
    }
  }

  public void setTooltip(int row, int column, String description) {
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

}
