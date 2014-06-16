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
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.SelectionModel;
import org.uberfire.client.tables.PagedTable;

/**
 *
 * @author salaboy
 */
public class ExtendedPagedTable<T> extends PagedTable<T> {

  // it is rgb because datagrid returns this kind of info
  private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";
  public static int CHAR_SIZE_IN_PIXELS = 10;
  
  private AsyncHandler columnSortHandler;

  public ExtendedPagedTable(int pageSize) {
    super(pageSize);
    this.columnSortHandler = new AsyncHandler(dataGrid);
    dataGrid.addColumnSortHandler(columnSortHandler);
  }
  
  public ColumnSortList getColumnSortList(){
    return dataGrid.getColumnSortList();
  }

  @Override
  public void addColumn(Column<T, ?> column, String caption, boolean visible) {
    super.addColumn(column, caption, visible); 
    column.setDataStoreName(caption);
  }

  @Override
  public void addColumn(Column<T, ?> column, String caption) {
    super.addColumn(column, caption); 
    column.setDataStoreName(caption);
  }

  public void setSelectionModel(SelectionModel<? super T> selectionModel) {
    dataGrid.setSelectionModel(selectionModel);
  }

  public void setSelectionModel(SelectionModel<? super T> selectionModel, CellPreviewEvent.Handler<T> selectionEventManager) {
    dataGrid.setSelectionModel(selectionModel, selectionEventManager);
  }

  public void paintRowSelected(String id) {
    for (int i = 0; i < getCurrentRowCount(); i++) {
      for (int j = 0; j < dataGrid.getColumnCount(); j++) {
        if (!dataGrid.getRowElement(i).getCells().getItem(0).getInnerText().equals(id)) {
          dataGrid.getRowElement(i).getCells().getItem(j).getStyle().clearBackgroundColor();
        } else {
          paint(i, j, BG_ROW_SELECTED);
        }
      }
    }
  }

  public void paint(int row, int column, String color) {
    dataGrid.getRowElement(row).getCells().getItem(column).getStyle().setBackgroundColor(color);
  }

  private int getCurrentRowCount() {
    int rowCount = 0;
    for (int i = 0; i < getPageSize(); i++) {
      try {
        rowCount = i + 1;
        dataGrid.getRowElement(i);
      } catch (Exception e) {
        rowCount = i;
        break;
      }
    }
    return rowCount;
  }
}
