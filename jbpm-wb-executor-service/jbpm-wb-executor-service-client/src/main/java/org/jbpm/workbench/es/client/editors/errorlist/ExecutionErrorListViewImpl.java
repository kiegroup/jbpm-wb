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

package org.jbpm.workbench.es.client.editors.errorlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.BooleanConverter;
import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.common.client.util.DateTimeConverter;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.client.util.ExecutionErrorTypeConverter;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class ExecutionErrorListViewImpl extends AbstractMultiGridView<ExecutionErrorSummary, ExecutionErrorListPresenter>
        implements ExecutionErrorListPresenter.ExecutionErrorListView {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    ConfirmPopup confirmPopup;

    @Inject
    ExecutionErrorTypeConverter executionErrorTypeConverter;

    @Inject
    DateTimeConverter dateTimeConverter;

    @Inject
    BooleanConverter booleanConverter;

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ERROR_TYPE,
                             COLUMN_PROCESS_INST_ID,
                             COLUMN_ERROR_DATE,
                             COLUMN_DEPLOYMENT_ID,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ERROR_TYPE,
                             COLUMN_PROCESS_INST_ID,
                             COLUMN_ERROR_DATE,
                             COL_ID_ACTIONS);
    }

    @Override
    public String getEmptyTableCaption() {
        return constants.No_Execution_Errors_Found();
    }

    @Override
    public List<AnchorListItem> getBulkActionsItems(ExtendedPagedTable<ExecutionErrorSummary> extendedPagedTable) {
        return Collections.singletonList(getBulkAck(extendedPagedTable));
    }

    protected AnchorListItem getBulkAck(final ExtendedPagedTable<ExecutionErrorSummary> extendedPagedTable) {
        final AnchorListItem bulkAckNavLink = GWT.create(AnchorListItem.class);
        bulkAckNavLink.setText(constants.Bulk_Ack());
        bulkAckNavLink.setIcon(IconType.BAN);
        bulkAckNavLink.setIconFixedWidth(true);
        bulkAckNavLink.addClickHandler((ClickEvent event) -> {
            confirmPopup.show(constants.Bulk_Ack(),
                              constants.Acknowledge(),
                              constants.Bulk_Ack_confirm(),
                              () -> {
                                  presenter.bulkAcknowledge(extendedPagedTable.getSelectedItems());
                                  extendedPagedTable.deselectAllItems();
                              });
        });
        return bulkAckNavLink;
    }

    @Override
    public void initColumns(final ListTable<ExecutionErrorSummary> extendedPagedTable) {
        final ColumnMeta<ExecutionErrorSummary> checkColumnMeta = initChecksColumn(extendedPagedTable);
        extendedPagedTable.addSelectionIgnoreColumn(checkColumnMeta.getColumn());

        final List<ColumnMeta<ExecutionErrorSummary>> columnMetas = new ArrayList<ColumnMeta<ExecutionErrorSummary>>();

        columnMetas.add(checkColumnMeta);
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_ID,
                                                          errorSummary -> errorSummary.getErrorId()),
                                         constants.Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_TYPE,
                                                          errorSummary -> executionErrorTypeConverter.toWidgetValue(errorSummary.getType())),
                                         constants.Type()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_ACK,
                                                          errorSummary -> booleanConverter.toWidgetValue(errorSummary.isAcknowledged())),
                                         constants.Ack()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_ACK_AT,
                                                          errorSummary ->
                                                                  dateTimeConverter.toWidgetValue(errorSummary.getAcknowledgedAt())),
                                         constants.AckAt()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_ACK_BY,
                                                          errorSummary -> errorSummary.getAcknowledgedBy()),
                                         constants.AckBy()));
        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_JOB_ID,
                                                            errorSummary -> errorSummary.getJobId()),
                                         constants.JobId()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_ID,
                                                          errorSummary -> errorSummary.getProcessId()),
                                         constants.ProcessId()));
        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_PROCESS_INST_ID,
                                                            errorSummary -> errorSummary.getProcessInstanceId()),
                                         constants.Process_Instance_Id()));

        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_ACTIVITY_ID,
                                                            errorSummary -> errorSummary.getActivityId()),
                                         constants.ActivityId()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ACTIVITY_NAME,
                                                          errorSummary -> errorSummary.getActivityName()),
                                         constants.ActivityName()));
        final Column<ExecutionErrorSummary, String> errorDateColumn = createTextColumn(COLUMN_ERROR_DATE,
                                                                                       errorSummary ->
                                                                                               dateTimeConverter.toWidgetValue(errorSummary.getErrorDate()));
        errorDateColumn.setDefaultSortAscending(false);
        columnMetas.add(new ColumnMeta<>(errorDateColumn,
                                         constants.ErrorDate()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_DEPLOYMENT_ID,
                                                          errorSummary -> errorSummary.getDeploymentId()),
                                         constants.DeploymentId()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_ERROR_MSG,
                                                          errorSummary -> errorSummary.getErrorMessage()),
                                         constants.Message()));

        ColumnMeta<ExecutionErrorSummary> actionsColumnMeta = initActionsColumn();

        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());

        columnMetas.add(actionsColumnMeta);

        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(checkColumnMeta.getColumn(),
                                          CHECK_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.setColumnWidth(actionsColumnMeta.getColumn(),
                                          ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.getColumnSortList().push(errorDateColumn);
    }

    @Override
    protected List<ConditionalAction<ExecutionErrorSummary>> getConditionalActions() {
        return Arrays.asList(

                new ConditionalAction<>(
                        constants.Acknowledge(),
                        errorSummary -> presenter.acknowledgeExecutionError(errorSummary.getErrorId(),
                                                                            errorSummary.getDeploymentId()),
                        presenter.getAcknowledgeActionCondition(),
                        false),

                new ConditionalAction<>(
                        constants.ViewProcessInstance(),
                        errorSummary -> presenter.goToProcessInstance(errorSummary),
                        presenter.getViewProcessInstanceActionCondition(),
                        true),

                new ConditionalAction<>(
                        constants.ViewJob(),
                        errorSummary -> presenter.goToJob(errorSummary),
                        presenter.getViewJobActionCondition(),
                        true),

                new ConditionalAction<>(
                        constants.ViewTask(),
                        errorSummary -> presenter.goToTask(errorSummary),
                        presenter.getViewTaskActionCondition(),
                        true)
        );
    }
}