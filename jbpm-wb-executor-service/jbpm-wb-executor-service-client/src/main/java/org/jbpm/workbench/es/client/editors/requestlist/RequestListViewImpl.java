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

package org.jbpm.workbench.es.client.editors.requestlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.client.util.JobStatusConverter;
import org.jbpm.workbench.es.model.RequestSummary;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class RequestListViewImpl extends AbstractMultiGridView<RequestSummary, RequestListPresenter>
        implements RequestListPresenter.RequestListView {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    private JobStatusConverter jobStatusConverter;

    @Inject
    private ConfirmPopup confirmPopup;

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ID,
                             COLUMN_BUSINESSKEY,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_ID,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public void initColumns(final ListTable extendedPagedTable) {
        final ColumnMeta checkColumnMeta = initChecksColumn(extendedPagedTable);
        ColumnMeta<RequestSummary> actionsColumnMeta = initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());
        extendedPagedTable.addSelectionIgnoreColumn(checkColumnMeta.getColumn());

        final List<ColumnMeta<RequestSummary>> columnMetas = new ArrayList<ColumnMeta<RequestSummary>>();
        columnMetas.add(checkColumnMeta);
        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_ID,
                                                            req -> req.getJobId()),
                                         constants.Id()));

        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_BUSINESSKEY,
                                                          req -> req.getKey()),
                                         constants.BusinessKey()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_COMMANDNAME,
                                                          req -> req.getCommandName()),
                                         constants.Type()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_STATUS,
                                                          req -> jobStatusConverter.toWidgetValue(req.getStatus())
        ),
                                         constants.Status()));
        final Column<RequestSummary, String> timestampColumn = createTextColumn(COLUMN_TIMESTAMP,
                                                                                req -> DateUtils.getDateTimeStr(req.getTime()));
        timestampColumn.setDefaultSortAscending(false);
        columnMetas.add(new ColumnMeta<>(timestampColumn,
                                         constants.Due_On()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_NAME,
                                                          req -> req.getProcessName()),
                                         constants.Process_Name()));
        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_PROCESS_INSTANCE_ID,
                                                            req -> req.getProcessInstanceId()),
                                         constants.Process_Instance_Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                          req -> req.getProcessInstanceDescription()),
                                         constants.Process_Instance_Description()));
        columnMetas.add(actionsColumnMeta);

        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(checkColumnMeta.getColumn(),
                                          CHECK_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.setColumnWidth(actionsColumnMeta.getColumn(),
                                          ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.getColumnSortList().push(timestampColumn);
    }

    @Override
    public String getEmptyTableCaption() {
        return constants.No_Jobs_Found();
    }

    @Override
    public List<AnchorListItem> getBulkActionsItems(ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        List<AnchorListItem> bulkActionsItems = new ArrayList<>();
        bulkActionsItems.add(getBulkCancel(extendedPagedTable));
        bulkActionsItems.add(getBulkRequeue(extendedPagedTable));
        return bulkActionsItems;
    }

    @Override
    protected List<ConditionalAction<RequestSummary>> getConditionalActions() {
        return Arrays.asList(

                new ConditionalAction<>(
                        constants.Cancel(),
                        job -> {
                            confirmPopup.show(constants.Cancel_Job_Confirmation(),
                                              constants.Cancel_Job(),
                                              constants.Cancel_Job_Sure(),
                                              () -> {
                                                  presenter.cancelRequest(job.getDeploymentId(),
                                                                          job.getJobId());
                                              });
                        },
                        presenter.getCancelActionCondition(),
                        false),

                new ConditionalAction<>(
                        constants.Requeue(),
                        job -> {
                            confirmPopup.show(constants.Requeue_Job_Confirmation(),
                                              constants.Requeue_Job(),
                                              constants.Requeue_Job_Sure(),
                                              () -> {
                                                  presenter.requeueRequest(job.getDeploymentId(),
                                                                           job.getJobId());
                                              });
                        },
                        presenter.getRequeueActionCondition(),
                        false),

                new ConditionalAction<>(
                        constants.ViewProcessInstance(),
                        job -> {
                            presenter.openProcessInstanceView(Long.toString(job.getProcessInstanceId()));
                        },
                        presenter.getViewProcessActionCondition(),
                        true));
    }

    protected AnchorListItem getBulkCancel(final ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        final AnchorListItem bulkAbortNavLink = GWT.create(AnchorListItem.class);
        bulkAbortNavLink.setText(constants.Bulk_Cancel_jobs());
        bulkAbortNavLink.setIcon(IconType.TIMES_CIRCLE_O);

        bulkAbortNavLink.setIconFixedWidth(true);
        bulkAbortNavLink.addClickHandler(event -> confirmPopup.show(constants.Cancel_Jobs_Confirmation(),
                                                                    constants.Cancel_Jobs(),
                                                                    constants.Cancel_Jobs_Sure(),
                                                                    getCancelCommand(extendedPagedTable))
        );

        return bulkAbortNavLink;
    }

    protected AnchorListItem getBulkRequeue(final ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        final AnchorListItem bulkAbortNavLink = GWT.create(AnchorListItem.class);
        bulkAbortNavLink.setText(constants.Bulk_Requeue_jobs());
        bulkAbortNavLink.setIcon(IconType.HISTORY);

        bulkAbortNavLink.setIconFixedWidth(true);
        bulkAbortNavLink.addClickHandler(event -> confirmPopup.show(constants.Requeue_Jobs_Confirmation(),
                                                                    constants.Requeue_Jobs(),
                                                                    constants.Requeue_Jobs_Sure(),
                                                                    getRequeueCommand(extendedPagedTable))
        );
        return bulkAbortNavLink;
    }

    protected Command getCancelCommand(final ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        return () -> presenter.bulkCancel(extendedPagedTable.getSelectedItems());
    }

    protected Command getRequeueCommand(final ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        return () -> presenter.bulkRequeue(extendedPagedTable.getSelectedItems());
    }
}