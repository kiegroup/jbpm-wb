/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.ht.client.util;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.constants.ResponsiveStyle;
import com.google.gwt.i18n.client.NumberFormat;
import org.jbpm.console.ng.ht.model.TaskSummary;

public class DataGridUtils {

    // it is rgb because datagrid returns this kind of info
    private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";

    private static final String BG_ROW_COMPLETED = "#EFBDBD";

    public static Long currentIdSelected = null;

    public static Long idTaskCalendar = null;

    public static int pageSize = 10;

    public static enum StatusTaskDataGrid {
        COMPLETED("Completed"), 
        INPROGRESS("InProgress"), 
        RESERVED("Reserved"), 
        READY("Ready");

        private String description;

        StatusTaskDataGrid(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public static enum ActionsDataGrid {
        CLAIM("Claim"), 
        RELEASE("Release"), 
        START("Start"), 
        COMPLETE("Complete"), 
        DETAILS("Details");

        private String description;

        ActionsDataGrid(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public enum ColumnsTask {
        ID(true, 0), 
        TASK(false, 1), 
        PRIORITY(true, 2), 
        STATUS(true, 3), 
        CREATED_ON(true, 4), 
        DUE_ON(true, 5), 
        ACTIONS(false, 6);

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

    public static void paintRowSelected(DataGrid<TaskSummary> myTaskListGrid, Long idTask) {
        for (int i = 0; i < getCurrentRowCount(myTaskListGrid); i++) {
            for (int j = 0; j < myTaskListGrid.getColumnCount(); j++) {
                Long value = new Double(NumberFormat.getDecimalFormat().parse(myTaskListGrid.getRowElement(i).getCells().getItem(0).getInnerText())).longValue();
                if (!idTask.equals(value)) {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().clearBackgroundColor();
                } else {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().setBackgroundColor(BG_ROW_SELECTED);
                }
            }
        }
    }

    public static Long getIdRowSelected(DataGrid<TaskSummary> myTaskListGrid) {
        Long idTaskSelected = null;
        for (int i = 0; i < getCurrentRowCount(myTaskListGrid); i++) {
            if (myTaskListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                idTaskSelected = new Double(NumberFormat.getDecimalFormat().parse(myTaskListGrid.getRowElement(i).getCells().getItem(0).getInnerText())).longValue();
                
                break;
            }
        }
        return idTaskSelected;
    }

    public static void paintRowsCompleted(DataGrid<TaskSummary> myTaskListGrid) {
        for (int i = 0; i < getCurrentRowCount(myTaskListGrid); i++) {
            if (myTaskListGrid.getRowElement(i).getCells().getItem(3).getInnerText()
                    .equals(StatusTaskDataGrid.COMPLETED.getDescription())
                    && !myTaskListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor()
                            .equals(BG_ROW_SELECTED)) {
                for (int j = 0; j < myTaskListGrid.getColumnCount(); j++) {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().setBackgroundColor(BG_ROW_COMPLETED);
                }
            }
        }
    }

    public static void paintCalendarFromGrid(DataGrid<TaskSummary> myTaskListGrid) {
        if (idTaskCalendar == null) {
            idTaskCalendar = DataGridUtils.getIdRowSelected(myTaskListGrid);
        }
    }

    public static void PaintGridFromCalendar(DataGrid<TaskSummary> myTaskListGrid) {
        if (idTaskCalendar != null) {
            currentIdSelected = DataGridUtils.idTaskCalendar;
        }
    }

    public static void setHideOnAllColumns(DataGrid<TaskSummary> myTaskListGrid) {
        for (ColumnsTask col : ColumnsTask.values()) {
            if (col.isResponsive()) {
                myTaskListGrid.getColumn(col.getColumn()).setCellStyleNames(ResponsiveStyle.HIDDEN_PHONE.get());
                myTaskListGrid.getHeader(col.getColumn()).setHeaderStyleNames(ResponsiveStyle.HIDDEN_PHONE.get());
                myTaskListGrid.addColumnStyleName(col.getColumn(), ResponsiveStyle.HIDDEN_PHONE.get());
            }
        }
    }

    private static int getCurrentRowCount(DataGrid<TaskSummary> myTaskListGrid) {
        int rowCount = 0;
        for (int i = 0; i < DataGridUtils.pageSize; i++) {
            try {
                rowCount = i + 1;
                myTaskListGrid.getRowElement(i);
            } catch (Exception e) {
                rowCount = i;
                break;
            }
        }
        return rowCount;
    }

    public static void setTooltip(DataGrid<TaskSummary> myTaskListGrid, long idCurrentRow, int column,
            String description) {
        for (int i = 0; i < getCurrentRowCount(myTaskListGrid); i++) {
            if (myTaskListGrid.getRowElement(i).getCells().getItem(1).getInnerText().equals(String.valueOf(idCurrentRow))) {
                myTaskListGrid.getRowElement(i).getCells().getItem(column).setTitle(description);
                break;
            }
        }
    }

}
