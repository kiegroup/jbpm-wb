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

package org.jbpm.console.ng.bd.client.util;



import com.github.gwtbootstrap.client.ui.DataGrid;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;


public class DataGridUtils {

   
    public static int pageSize = 10;

 
    private static int getCurrentRowCount(DataGrid<KModuleDeploymentUnitSummary> myTaskListGrid) {
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

    public static void setTooltip(DataGrid<KModuleDeploymentUnitSummary> kmoduleListGrid, String idCurrentRow, int column,
            String kModuleId) {
        for (int i = 0; i < getCurrentRowCount(kmoduleListGrid); i++) {
            String kModuleFinalId = "";
            if(kModuleId.length() > 25){
                kModuleFinalId = idCurrentRow.substring(0, 25) + "...";
            }else{
                kModuleFinalId = idCurrentRow;
            }
            if (kmoduleListGrid.getRowElement(i).getCells().getItem(0).getInnerText().equals(kModuleFinalId)) {
                kmoduleListGrid.getRowElement(i).getCells().getItem(column).setTitle(kModuleId);
                break;
            }
        }
    }

}
