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
package org.jbpm.console.ng.pr.client.util;

import com.github.gwtbootstrap.client.ui.DataGrid;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;

public class DataGridUtils {

    // it is rgb because datagrid returns this info
    private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";

    public static String newProcessDefName = null;
    public static String newProcessDefVersion = null;
    
    public static Long newProcessInstanceId = null;
    public static String newProcessInstanceDefName = null;
    public static String newProcessInstanceDefVersion = null;
    public static String newProcessInstanceStartDate = null;
    
    public static void paintRowSelected(DataGrid<ProcessSummary> myProcessDefListGrid, String nameProcessDef, String versionProcessDef) {
        for (int i = 0; i < myProcessDefListGrid.getRowCount(); i++) {
            boolean nameMatch = false;
            boolean versionMatch = false;
            for (int j = 0; j < myProcessDefListGrid.getColumnCount(); j++) {
                if (myProcessDefListGrid.getRowElement(i).getCells().getItem(j).getInnerText().equals(nameProcessDef)) {
                    nameMatch = true;
                }
                if (myProcessDefListGrid.getRowElement(i).getCells().getItem(j).getInnerText().equals(versionProcessDef)) {
                    versionMatch = true;
                }

            }
            for (int k = 0; k < myProcessDefListGrid.getColumnCount(); k++) {
                if (nameMatch && versionMatch) {
                    myProcessDefListGrid.getRowElement(i).getCells().getItem(k).getStyle().setBackgroundColor(BG_ROW_SELECTED);
                } else {
                    myProcessDefListGrid.getRowElement(i).getCells().getItem(k).getStyle().clearBackgroundColor();
                }
            }
        }

    }
    
    public static void paintInstanceRowSelected(DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid, String nameProcessDef, 
            String versionProcessDef, String startDateInstance) {
        for (int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++) {
            boolean nameMatch = false;
            boolean versionMatch = false;
            boolean startDateMatch = false;
            for (int j = 0; j < myProcessInstanceListGrid.getColumnCount(); j++) {
                if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(j).getInnerText().equals(nameProcessDef)) {
                    nameMatch = true;
                }
                if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(j).getInnerText().equals(versionProcessDef)) {
                    versionMatch = true;
                }
                if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(j).getInnerText().equals(startDateInstance)) {
                    startDateMatch = true;
                }

            }
            for (int k = 0; k < myProcessInstanceListGrid.getColumnCount(); k++) {
                if (nameMatch && versionMatch & startDateMatch) {
                    myProcessInstanceListGrid.getRowElement(i).getCells().getItem(k).getStyle().setBackgroundColor(BG_ROW_SELECTED);
                } else {
                    myProcessInstanceListGrid.getRowElement(i).getCells().getItem(k).getStyle().clearBackgroundColor();
                }
            }
        }

    }

    public static String getProcessNameRowSelected(DataGrid<ProcessSummary> myProcessDefListGrid) {
        String processDefName = null;
        for (int i = 0; i < myProcessDefListGrid.getRowCount(); i++) {
            if (myProcessDefListGrid.getRowElement(i).getCells().getItem(0).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                processDefName = myProcessDefListGrid.getRowElement(i).getCells().getItem(0).getInnerText();
                break;
            }
        }
        return processDefName;
    }
    
    public static String getProcessVersionRowSelected(DataGrid<ProcessSummary> myProcessDefListGrid) {
        String processDefVersion = null;
        for (int i = 0; i < myProcessDefListGrid.getRowCount(); i++) {
            if (myProcessDefListGrid.getRowElement(i).getCells().getItem(1).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                processDefVersion = myProcessDefListGrid.getRowElement(i).getCells().getItem(1).getInnerText();
                break;
            }
        }
        return processDefVersion;
    }

    public static String getProcessInstanceNameRowSelected(DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid) {
        String processDefName = null;
        for (int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++) {
            if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(1).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                processDefName = myProcessInstanceListGrid.getRowElement(i).getCells().getItem(1).getInnerText();
                break;
            }
        }
        return processDefName;
    }
    
    public static String getProcessInstanceVersionRowSelected(DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid) {
        String processDefVersion = null;
        for (int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++) {
            if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(3).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                processDefVersion = myProcessInstanceListGrid.getRowElement(i).getCells().getItem(3).getInnerText();
                break;
            }
        }
        return processDefVersion;
    }
    
    public static String getProcessInstanceStartDateRowSelected(DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid) {
        String processStartDate = null;
        for (int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++) {
            if (myProcessInstanceListGrid.getRowElement(i).getCells().getItem(5).getStyle().getBackgroundColor().equals(BG_ROW_SELECTED)) {
                processStartDate = myProcessInstanceListGrid.getRowElement(i).getCells().getItem(5).getInnerText();
                break;
            }
        }
        return processStartDate;
    }

}
