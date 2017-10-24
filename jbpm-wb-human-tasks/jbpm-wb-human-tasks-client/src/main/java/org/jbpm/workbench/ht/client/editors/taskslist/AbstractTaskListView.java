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
package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.*;


import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.view.client.CellPreviewEvent;

import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.resources.CommonResources;
import org.jbpm.workbench.common.client.util.ConditionalButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.ht.client.resources.HumanTaskResources;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.common.client.util.TaskUtils.*;
import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

public abstract class AbstractTaskListView<P extends AbstractTaskListPresenter> extends AbstractMultiGridView<TaskSummary, P>
        implements AbstractTaskListPresenter.TaskListView<P> {

    protected final Constants constants = Constants.INSTANCE;
    
    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COLUMN_NAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public String getNewFilterPopupTitle() {
        return Constants.INSTANCE.New_FilteredList();
    }

    @Override
    public void initSelectionModel(final ExtendedPagedTable<TaskSummary> extendedPagedTable) {
        final RowStyles selectedStyles = new RowStyles<TaskSummary>() {

            @Override
            public String getStyleNames(TaskSummary row,
                                        int rowIndex) {
                if (rowIndex == extendedPagedTable.getSelectedRow()) {
                    return CommonResources.INSTANCE.css().selected();
                } else if (row.getStatus().equals(TASK_STATUS_COMPLETED)) {
                    return HumanTaskResources.INSTANCE.css().taskCompleted();
                }
                return null;
            }
        };

        extendedPagedTable.setEmptyTableCaption(constants.No_Tasks_Found());
        extendedPagedTable.setSelectionCallback((task, close) -> presenter.selectTask(task,
                                                                                      close));
        extendedPagedTable.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns(ExtendedPagedTable<TaskSummary> extendedPagedTable) {
        initCellPreview(extendedPagedTable);
        
        Column actionsColumn = initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumn);

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        columnMetas.add(new ColumnMeta<>(
                createNumberColumn(COLUMN_TASK_ID,
                                   task -> task.getId()),
                constants.Id()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_NAME,
                                 task -> task.getName()),
                constants.Task()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_DESCRIPTION,
                                 task -> task.getDescription()),
                constants.Description()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_PROCESS_ID,
                                 task -> task.getProcessId()),
                constants.Process_Definition_Id()
        ));
        columnMetas.add(new ColumnMeta<>(
                createNumberColumn(COLUMN_PROCESS_INSTANCE_ID,
                                   task -> task.getProcessInstanceId()),
                constants.Process_Instance_Id()
        ));
        columnMetas.add(new ColumnMeta<>(
                createNumberColumn(COLUMN_PRIORITY,
                                   task -> task.getPriority()),
                constants.Priority()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_STATUS,
                                 task -> task.getStatus()),
                constants.Status()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_CREATED_ON,
                                 task -> DateUtils.getDateTimeStr(task.getCreatedOn())),
                constants.Created_On()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_DUE_DATE,
                                 task -> DateUtils.getDateTimeStr(task.getExpirationTime())),
                constants.Due_On()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_ACTUAL_OWNER,
                                 task -> task.getActualOwner()),
                constants.Actual_Owner()));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY,
                                 task -> task.getProcessInstanceCorrelationKey()),
                constants.Process_Instance_Correlation_Key()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                 task -> task.getProcessInstanceDescription()),
                constants.Process_Instance_Description()
        ));
        columnMetas.add(new ColumnMeta<>(
                createTextColumn(COLUMN_LAST_MODIFICATION_DATE,
                                 task -> DateUtils.getDateTimeStr(task.getLastModificationDate())),
                constants.Last_Modification_Date()
        ));
        columnMetas.add(new ColumnMeta<>(
                createNumberColumn(COLUMN_PROCESS_SESSION_ID,
                                   task -> task.getProcessSessionId()),
                constants.ProcessSessionId()
        ));
        addNewColumn(extendedPagedTable, columnMetas);
        columnMetas.add(new ColumnMeta<>(actionsColumn,
                                         constants.Actions()));

        List<GridColumnPreference> columPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();
        for (GridColumnPreference colPref : columPreferenceList) {
            if (!isColumnAdded(columnMetas,
                               colPref.getName())) {
                Column<TaskSummary, ?> genericColumn = initGenericColumn(colPref.getName());
                genericColumn.setSortable(false);
                columnMetas.add(new ColumnMeta<TaskSummary>(genericColumn,
                                                            colPref.getName(),
                                                            true,
                                                            true));
               
            }
        }
        extendedPagedTable.addColumns(columnMetas);
    }
    
    protected void addNewColumn(ExtendedPagedTable<TaskSummary> extendedPagedTable, 
                                List<ColumnMeta<TaskSummary>> columnMetas) {}

    protected void initCellPreview(final ExtendedPagedTable<TaskSummary> extendedPagedTable) {
        extendedPagedTable.addCellPreviewHandler(new CellPreviewEvent.Handler<TaskSummary>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<TaskSummary> event) {

                if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onMouseOverGrid(extendedPagedTable,
                                    event);
                }
            }
        });
    }

    protected void onMouseOverGrid(ExtendedPagedTable<TaskSummary> extendedPagedTable,
                                 final CellPreviewEvent<TaskSummary> event) {
        TaskSummary task = event.getValue();

        if (task.getDescription() != null) {
            extendedPagedTable.setTooltip(extendedPagedTable.getKeyboardSelectedRow(),
                                          event.getColumn(),
                                          task.getDescription());
        }
    }

    protected Column<TaskSummary, ?> initActionsColumn() {
        List<HasCell<TaskSummary, ?>> cells = new LinkedList<HasCell<TaskSummary, ?>>();

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.Claim(),
                                                               task -> presenter.claimTask(task),
                                                               presenter.getClaimActionCondition()));

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.Release(),
                                                               task -> presenter.releaseTask(task),
                                                               presenter.getReleaseActionCondition()));

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.Suspend(),
                                                               task -> presenter.suspendTask(task),
                                                               presenter.getSuspendActionCondition()));

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.Resume(),
                                                               task -> presenter.resumeTask(task),
                                                               presenter.getResumeActionCondition()));

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.ViewProcess(),
                                                               task -> presenter.openProcessInstanceView(task.getProcessInstanceId().toString()),
                                                               presenter.getProcessInstanceCondition()));

        cells.add(new ConditionalButtonActionCell<TaskSummary>(constants.Open(),
                                                               task -> presenter.selectTask(task,
                                                                                            false),
                                                               presenter.getCompleteActionCondition()));

        CompositeCell<TaskSummary> cell = new CompositeCell<TaskSummary>(cells);
        Column<TaskSummary, TaskSummary> actionsColumn = new Column<TaskSummary, TaskSummary>(cell) {
            @Override
            public TaskSummary getValue(TaskSummary object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        return actionsColumn;
    }
    
    protected boolean isColumnAdded( List<ColumnMeta<TaskSummary>> columnMetas,
            String caption ) {
        if ( caption != null ) {
            for ( ColumnMeta<TaskSummary> colMet : columnMetas ) {
                if ( caption.equals( colMet.getColumn().getDataStoreName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addDomainSpecifColumns(ExtendedPagedTable<TaskSummary> extendedPagedTable,
                                       Set<String> columns) {

        extendedPagedTable.storeColumnToPreferences();

        HashMap<String, String> modifiedCaptions = new HashMap<String, String>();
        ArrayList<ColumnMeta<TaskSummary>> existingExtraColumns = new ArrayList<ColumnMeta<TaskSummary>>();
        for (ColumnMeta<TaskSummary> cm : extendedPagedTable.getColumnMetaList()) {
            if (cm.isExtraColumn()) {
                existingExtraColumns.add(cm);
            } else if (columns.contains(cm.getCaption())) {      //exist a column with the same caption
                for (String c : columns) {
                    if (c.equals(cm.getCaption())) {
                        modifiedCaptions.put(c,
                                             "Var_" + c);
                    }
                }
            }
        }
        for (ColumnMeta<TaskSummary> colMet : existingExtraColumns) {
            if (!columns.contains(colMet.getCaption())) {
                extendedPagedTable.removeColumnMeta(colMet);
            } else {
                columns.remove(colMet.getCaption());
            }
        }

        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        String caption = "";
        for (String c : columns) {
            caption = c;
            if (modifiedCaptions.get(c) != null) {
                caption = (String) modifiedCaptions.get(c);
            }
            Column<TaskSummary, ?> genericColumn = initGenericColumn(c);
            genericColumn.setSortable(false);

            columnMetas.add(new ColumnMeta<TaskSummary>(genericColumn,
                                                        caption,
                                                        true,
                                                        true));
        }
        extendedPagedTable.addColumns(columnMetas);
    }

    protected Column<TaskSummary, ?> initGenericColumn(final String key) {
        return createTextColumn(key,
                                task -> task.getDomainDataValue(key));
    }
    
    @Override
    public void setSelectedTask(final TaskSummary selectedTask) {
        currentListGrid.getSelectionModel().setSelected(selectedTask,
                                                        true);
    }
}