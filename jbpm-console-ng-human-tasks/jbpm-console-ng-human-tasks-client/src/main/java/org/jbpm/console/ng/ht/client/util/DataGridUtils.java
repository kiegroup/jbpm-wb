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

import org.jbpm.console.ng.ht.model.TaskSummary;

import com.github.gwtbootstrap.client.ui.DataGrid;

public class DataGridUtils {

    // it is rgb because datagrid returns this info
    private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";
    
    private static final String BG_ROW_COMPLETED = "#EFBDBD";

    public static Long currentIdSelected = null;
    
    public static Long idTaskCalendar = null;
    
    public static enum StatusTaskDataGrid{
        
        COMPLETED("Completed"),
        INPROGRESS("InProgress"),
        RESERVED("Reserved"),
        READY("Ready");
        
        private String description;
        
        StatusTaskDataGrid(String description){
             this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
    }
    
    public static enum ActionsDataGrid{
        
        CLAIM("Claim"),
        RELEASE("Release"),
        START("Start"),
        COMPLETE("Complete"),
        DETAILS("Details");
        
        private String description;
        
        ActionsDataGrid(String description){
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
    }

    public static void paintRowSelected(DataGrid<TaskSummary> myTaskListGrid, Long idTask) {
        for (int i = 0; i < myTaskListGrid.getRowCount(); i++) {
            for (int j = 0; j < myTaskListGrid.getColumnCount(); j++) {
                if (!Long.valueOf(myTaskListGrid.getRowElement(i).getCells().getItem(0).getInnerText()).equals(idTask)) {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().clearBackgroundColor();
                } else {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().setBackgroundColor(BG_ROW_SELECTED);
                }
            }
        }
    }

    public static Long getIdRowSelected(DataGrid<TaskSummary> myTaskListGrid) {
        Long idTaskSelected = null;
        for (int i = 0; i < myTaskListGrid.getRowCount(); i++) {
            if (myTaskListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                idTaskSelected = Long.valueOf(myTaskListGrid.getRowElement(i).getCells().getItem(0).getInnerText());
                break;
            }
        }
        return idTaskSelected;
    }
    
    public static void paintRowsCompleted(DataGrid<TaskSummary> myTaskListGrid) {
        for (int i = 0; i < myTaskListGrid.getRowCount(); i++) {
            if (myTaskListGrid.getRowElement(i).getCells().getItem(3).getInnerText().equals(StatusTaskDataGrid.COMPLETED)
                    && !myTaskListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor()
                            .equals(BG_ROW_SELECTED)) {
                for (int j = 0; j < myTaskListGrid.getColumnCount(); j++) {
                    myTaskListGrid.getRowElement(i).getCells().getItem(j).getStyle().setBackgroundColor(BG_ROW_COMPLETED);
                }
            }
        }
    }
    
    public static void paintCalendarFromGrid(DataGrid<TaskSummary> myTaskListGrid){
        if(idTaskCalendar == null ){
            idTaskCalendar = DataGridUtils.getIdRowSelected(myTaskListGrid);
        }
    }
    
    public static void PaintGridFromCalendar(DataGrid<TaskSummary> myTaskListGrid){
        if(idTaskCalendar != null){
            currentIdSelected = DataGridUtils.idTaskCalendar; 
        }
    }

}
