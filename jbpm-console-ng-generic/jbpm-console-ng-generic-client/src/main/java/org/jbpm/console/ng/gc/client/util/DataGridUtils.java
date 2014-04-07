/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.gc.client.util;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.constants.ResponsiveStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.Range;

public class DataGridUtils {

    // it is rgb because datagrid returns this kind of info
    private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";

    private static final String BG_ROW_COMPLETED = "#EFBDBD";

    public static Long currentIdSelected = null;

    public static Long idTaskCalendar = null;

    public static int pageSize = 10;
    
    public static int clientSidePages = 3;

    public static int CHAR_SIZE_IN_PIXELS = 10;

    public static enum StatusTaskDataGrid {
        COMPLETED("Completed"), INPROGRESS("InProgress"), RESERVED("Reserved"), READY("Ready");

        private String description;

        StatusTaskDataGrid(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public static enum ActionsCRUD {
        CREATE("Create"), READ("Read"), UPDATE("Update"), DELETE("Delete");

        private String description;

        ActionsCRUD(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public static enum ActionsDataGrid {
        CLAIM("Claim"), RELEASE("Release"), START("Start"), COMPLETE("Complete"), DETAILS("Details");

        private String description;

        ActionsDataGrid(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum ColumnsTask {
        ID(true, 0), TASK(false, 1), PRIORITY(true, 2), STATUS(true, 3), CREATED_ON(true, 4), DUE_ON(true, 5), ACTIONS(false, 6);

        ColumnsTask(boolean responsive, int column) {
            this.responsive = responsive;
            this.column = column;
        }

        private boolean responsive;
        private int column;

        public boolean isResponsive() {
            return responsive;
        }

        public int getColumn() {
            return column;
        }

    }

    public static void paintRowSelected(DataGrid<?> myListGrid, String id) {
        for (int i = 0; i < getCurrentRowCount(myListGrid); i++) {
            for (int j = 0; j < myListGrid.getColumnCount(); j++) {
                if (!myListGrid.getRowElement(i).getCells().getItem(0).getInnerText().equals(id)) {
                    myListGrid.getRowElement(i).getCells().getItem(j).getStyle().clearBackgroundColor();
                } else {
                    paint(myListGrid, i, j, BG_ROW_SELECTED);
                }
            }
        }
    }

    public static String getIdRowSelected(DataGrid<?> myListGrid) {
        String idSelected = null;
        for (int i = 0; i < getCurrentRowCount(myListGrid); i++) {
            if (myListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                idSelected = myListGrid.getRowElement(i).getCells().getItem(0).getInnerText();
                break;
            }
        }
        return idSelected;
    }

    public static void paintRowsCompleted(DataGrid<?> myListGrid) {
        for (int i = 0; i < getCurrentRowCount(myListGrid); i++) {
            if (myListGrid.getRowElement(i).getCells().getItem(ColumnsTask.STATUS.getColumn()).getInnerText()
                    .equals(StatusTaskDataGrid.COMPLETED.getDescription())
                    && !myListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor()
                            .equals(BG_ROW_SELECTED)) {
                for (int j = 0; j < myListGrid.getColumnCount(); j++) {
                    paint(myListGrid, i, j, BG_ROW_COMPLETED);
                }
            }
            if (DataGridUtils.currentIdSelected != null) {
                paintRowById(myListGrid, DataGridUtils.currentIdSelected);
            }
        }

    }

    public static void paint(DataGrid<?> myListGrid, int row, int column, String color) {
        myListGrid.getRowElement(row).getCells().getItem(column).getStyle().setBackgroundColor(color);
    }

    public static void paintRowById(DataGrid<?> myListGrid, Long id) {
        for (int i = 0; i < getCurrentRowCount(myListGrid); i++) {
            for (int j = 0; j < myListGrid.getColumnCount(); j++) {
                if (Long.valueOf(myListGrid.getRowElement(i).getCells().getItem(0).getInnerText()).equals(id)) {
                    paint(myListGrid, i, j, BG_ROW_SELECTED);
                }
            }
        }
    }

    public static void paintCalendarFromGrid(DataGrid<?> myListGrid) {
        if (idTaskCalendar == null) {
            idTaskCalendar = Long.valueOf(DataGridUtils.getIdRowSelected(myListGrid));
        }
    }

    public static void PaintGridFromCalendar(DataGrid<?> myListGrid) {
        if (idTaskCalendar != null) {
            currentIdSelected = DataGridUtils.idTaskCalendar;
        }
    }

    public static void setHideOnAllColumns(DataGrid<?> myListGrid) {
        for (ColumnsTask col : ColumnsTask.values()) {
            if (col.isResponsive()) {
                myListGrid.getColumn(col.getColumn()).setCellStyleNames(ResponsiveStyle.HIDDEN_PHONE.get());
                myListGrid.getHeader(col.getColumn()).setHeaderStyleNames(ResponsiveStyle.HIDDEN_PHONE.get());
                myListGrid.addColumnStyleName(col.getColumn(), ResponsiveStyle.HIDDEN_PHONE.get());
            }
        }
    }

    private static int getCurrentRowCount(DataGrid<?> myListGrid) {
        int rowCount = 0;
        for (int i = 0; i < DataGridUtils.pageSize; i++) {
            try {
                rowCount = i + 1;
                myListGrid.getRowElement(i);
            } catch (Exception e) {
                rowCount = i;
                break;
            }
        }
        return rowCount;
    }

    public static void setTooltip(DataGrid<?> myListGrid, long idCurrentRow, int column, String description) {
        for (int i = 0; i < getCurrentRowCount(myListGrid); i++) {
            if (myListGrid.getRowElement(i).getCells().getItem(1).getInnerText().equals(String.valueOf(idCurrentRow))) {
                myListGrid.getRowElement(i).getCells().getItem(column).setTitle(description);
                break;
            }
        }
    }

    public static String trimToColumnWidth(AbstractCellTable table, Column column, String value) {
        if (value != null && value.length() > 0) {
            int columnWidth = getColumnWith(table, column);
            if (columnWidth < 0) {
                columnWidth = getDistributedColumnWidth(table, column);
            }
            if (columnWidth < 0)
                return "";
            int textWidth = CHAR_SIZE_IN_PIXELS * value.length();
            if (columnWidth < textWidth) {
                int visibleChars = columnWidth / CHAR_SIZE_IN_PIXELS;
                visibleChars = visibleChars > value.length() ? value.length() : visibleChars;
                value = value.substring(0, visibleChars) + "...";
            }
        }
        return value;
    }

    public static int getColumnWith(AbstractCellTable table, int col) {
        return getColumnWith(table, table.getColumn(col));
    }

    public static int getColumnWith(AbstractCellTable table, Column col) {
        String columnWidth = table.getColumnWidth(col);
        return columnWidth != null && !"null".equals(columnWidth) ? Integer.parseInt(columnWidth.substring(0,
                columnWidth.length() - 2)) : -1;
    }

    public static int getDistributedColumnWidth(AbstractCellTable table, Column col) {
        int width = getColumnWith(table, col);
        if (width <= 0) {
            width = table.getOffsetWidth();
            int columnWidth = 0;
            int columns = table.getColumnCount();
            for (int i = 0; i < table.getColumnCount(); i++) {
                columnWidth = getColumnWith(table, i);
                if (columnWidth > 0) {
                    columns--;
                    width = width - columnWidth;
                }
            }
            width = width >= 0 ? width / (columns != 0 ? columns : 1) : -1;
        }
        return width;
    }

    public static SafeHtml createDivStart(String title) {
        return createDivStart(title, "");
    }

    public static SafeHtml createDivStart(String title, String defaultValue) {
        if (title == null || "".equals(title))
            title = defaultValue;
        return SafeHtmlUtils.fromTrustedString("<div title=\"" + title.trim() + "\">");
    }

    public static SafeHtml createDivEnd() {
        return SafeHtmlUtils.fromTrustedString("</div>");
    }

    public static void redrawVisibleRange(AbstractCellTable table) {
        if (table != null) {
            Range range = table.getVisibleRange();
            if (range != null && range.getLength() > 0) {
                int offset = range.getStart();
                int count = 0;

                for (; (count < table.getVisibleItemCount()) && (offset < (range.getStart() + range.getLength()));) {
                    table.redrawRow(offset);
                    count++;
                    offset++;
                }
            }
        }
    }

    public static void setTooltip(DataGrid<?> kmoduleListGrid, String idCurrentRow, int column, String kModuleId) {
        for (int i = 0; i < getCurrentRowCount(kmoduleListGrid); i++) {
            String kModuleFinalId = "";
            if (kModuleId.length() > 25) {
                kModuleFinalId = idCurrentRow.substring(0, 25) + "...";
            } else {
                kModuleFinalId = idCurrentRow;
            }
            if (kmoduleListGrid.getRowElement(i).getCells().getItem(0).getInnerText().equals(kModuleFinalId)) {
                kmoduleListGrid.getRowElement(i).getCells().getItem(column).setTitle(kModuleId);
                break;
            }
        }
    }

}