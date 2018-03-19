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

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.GenericErrorSummaryCountCell;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class TaskAdminListViewImpl extends AbstractTaskListView<TaskAdminListPresenter> {

    @Inject
    private ManagedInstance<GenericErrorSummaryCountCell> popoverCellInstance;

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COLUMN_NAME,
                             COLUMN_PROCESS_ID,
                             COLUMN_STATUS,
                             COLUMN_ACTUAL_OWNER,
                             COLUMN_CREATED_ON,
                             COLUMN_ERROR_COUNT,
                             COL_ID_ACTIONS);
    }

    @Override
    protected void addNewColumn(ListTable<TaskSummary> extendedPagedTable,
                                List<ColumnMeta<TaskSummary>> columnMetas) {
        Column<TaskSummary, ?> errorCountColumn = initErrorCountColumn();
        extendedPagedTable.addSelectionIgnoreColumn(errorCountColumn);
        columnMetas.add(new ColumnMeta<>(errorCountColumn,
                                         constants.Errors()));
        extendedPagedTable.setColumnWidth(errorCountColumn,
                                          ERROR_COLUMN_WIDTH,
                                          Style.Unit.PX);
    }

    private Column<TaskSummary, TaskSummary> initErrorCountColumn() {
        Column<TaskSummary, TaskSummary> column = new Column<TaskSummary, TaskSummary>(
                popoverCellInstance.get().init(presenter)) {
            @Override
            public TaskSummary getValue(TaskSummary task) {
                return task;
            }
        };

        column.setSortable(true);
        column.setDataStoreName(COLUMN_ERROR_COUNT);
        return column;
    }
}