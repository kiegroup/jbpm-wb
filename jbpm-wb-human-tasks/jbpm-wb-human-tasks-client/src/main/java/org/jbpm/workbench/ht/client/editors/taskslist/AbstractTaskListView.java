/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.view.client.CellPreviewEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.common.client.util.SLAComplianceCell;
import org.jbpm.workbench.ht.client.resources.HumanTaskResources;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.jbpm.workbench.ht.util.TaskStatus.TASK_STATUS_COMPLETED;

public abstract class AbstractTaskListView<P extends AbstractTaskListPresenter> extends AbstractMultiGridView<TaskSummary, P>
        implements AbstractTaskListPresenter.TaskListView<P> {

    private TranslationService translationService;

    protected final Constants constants = Constants.INSTANCE;

    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COLUMN_NAME, COL_ID_ACTIONS);
    }

    @Override
    public String getEmptyTableCaption() {
        return constants.No_Tasks_Found();
    }

    @Override
    public void initSelectionModel(final ListTable<TaskSummary> extendedPagedTable) {
        super.initSelectionModel(extendedPagedTable);
        final RowStyles<TaskSummary> selectedStyles = (TaskSummary row,
                                                       int rowIndex) -> {
            if (TASK_STATUS_COMPLETED.equals(row.getTaskStatus())) {
                return HumanTaskResources.INSTANCE.css().taskCompleted();
            }
            return null;
        };
        extendedPagedTable.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns(ListTable<TaskSummary> extendedPagedTable) {
        initCellPreview(extendedPagedTable);

        final Column<TaskSummary, String> createdOnColumn = createTextColumn(COLUMN_CREATED_ON,
                                                                             task -> DateUtils.getDateTimeStr(task.getCreatedOn()));
        ColumnMeta<TaskSummary> actionsColumnMeta = initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());

        List<ColumnMeta<TaskSummary>> columnMetas = getGeneralColumnMetas(extendedPagedTable, createdOnColumn, actionsColumnMeta);

        columnMetas.addAll(renameVariables(extendedPagedTable, columnMetas));
        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(actionsColumnMeta.getColumn(),
                                          ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.getColumnSortList().push(createdOnColumn);
    }

    protected List<ColumnMeta<TaskSummary>> getGeneralColumnMetas(ListTable<TaskSummary> extendedPagedTable,
                                                                  Column<TaskSummary, String> createdOnColumn,
                                                                  ColumnMeta<TaskSummary> actionsColumnMeta) {
        List<ColumnMeta<TaskSummary>> columnMetas = new ArrayList<ColumnMeta<TaskSummary>>();
        Column<TaskSummary, ?> slaComplianceColumn = initSlaComplianceColumn();
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
                                 task -> translationService.format(task.getStatus())),
                constants.Status()
        ));

        createdOnColumn.setDefaultSortAscending(false);
        columnMetas.add(new ColumnMeta<>(
                createdOnColumn,
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
        columnMetas.add(new ColumnMeta<>(slaComplianceColumn,
                                         constants.SlaCompliance()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_SLA_DUE_DATE,
                                                          task -> DateUtils.getDateTimeStr(task.getSlaDueDate())),
                                         constants.SlaDueDate()));
        addNewColumn(extendedPagedTable,
                     columnMetas);
        columnMetas.add(actionsColumnMeta);
        return columnMetas;
    }

    protected void addNewColumn(ListTable<TaskSummary> extendedPagedTable, List<ColumnMeta<TaskSummary>> columnMetas) {
    }

    protected Column<TaskSummary, Integer> initSlaComplianceColumn() {

        Column<TaskSummary, Integer> column = new Column<TaskSummary, Integer>(
                new SLAComplianceCell()) {

            @Override
            public Integer getValue(TaskSummary taskSummary) {
                return taskSummary.getSlaCompliance();
            }
        };

        column.setSortable(true);
        column.setDataStoreName(COLUMN_SLA_COMPLIANCE);
        return column;
    }

    protected void initCellPreview(final ListTable<TaskSummary> extendedPagedTable) {
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

    protected void onMouseOverGrid(final ListTable<TaskSummary> extendedPagedTable,
                                   final CellPreviewEvent<TaskSummary> event) {
        TaskSummary task = event.getValue();

        if (task.getDescription() != null) {
            extendedPagedTable.setTooltip(extendedPagedTable.getKeyboardSelectedRow(),
                                          event.getColumn(),
                                          task.getDescription());
        }
    }

    @Override
    protected List<ConditionalAction<TaskSummary>> getConditionalActions() {
        return Arrays.asList(

                new ConditionalAction<TaskSummary>(constants.Claim(),
                                                   task -> presenter.claimTask(task),
                                                   presenter.getClaimActionCondition(),
                                                   false),
                new ConditionalAction<TaskSummary>(constants.ClaimAndWork(),
                                                   task -> presenter.claimAndWorkTask(task),
                                                   presenter.getClaimActionCondition(),
                                                   false),

                new ConditionalAction<TaskSummary>(constants.Release(),
                                                   task -> presenter.releaseTask(task),
                                                   presenter.getReleaseActionCondition(),
                                                   false),

                new ConditionalAction<TaskSummary>(constants.Suspend(),
                                                   task -> presenter.suspendTask(task),
                                                   presenter.getSuspendActionCondition(),
                                                   false),

                new ConditionalAction<TaskSummary>(constants.Resume(),
                                                   task -> presenter.resumeTask(task),
                                                   presenter.getResumeActionCondition(),
                                                   false),

                new ConditionalAction<TaskSummary>(constants.ViewProcess(),
                                                   task -> presenter.openProcessInstanceView(task.getProcessInstanceId().toString()),
                                                   presenter.getProcessInstanceCondition(),
                                                   true)
        );
    }

    protected Column<TaskSummary, ?> initGenericColumn(final String key) {
        return createTextColumn(key, task -> task.getDomainDataValue(key));
    }

    @Inject
    public void setTranslationService(TranslationService translationService) {
        this.translationService = translationService;
    }
}
