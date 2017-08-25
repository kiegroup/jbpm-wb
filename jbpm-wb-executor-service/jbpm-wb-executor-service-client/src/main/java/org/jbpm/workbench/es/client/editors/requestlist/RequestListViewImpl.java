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
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ConditionalButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
public class RequestListViewImpl extends AbstractMultiGridView<RequestSummary, RequestListPresenter>
        implements RequestListPresenter.RequestListView {

    private static final String REQUEST_LIST_PREFIX = "DS_RequestListGrid";
    protected static final String TAB_CANCELED = REQUEST_LIST_PREFIX + "_6";
    protected static final String TAB_COMPLETED = REQUEST_LIST_PREFIX + "_5";
    protected static final String TAB_ERROR = REQUEST_LIST_PREFIX + "_4";
    protected static final String TAB_RETRYING = REQUEST_LIST_PREFIX + "_3";
    protected static final String TAB_RUNNING = REQUEST_LIST_PREFIX + "_2";
    protected static final String TAB_QUEUED = REQUEST_LIST_PREFIX + "_1";
    protected static final String TAB_ALL = REQUEST_LIST_PREFIX + "_0";
    private final Constants constants = Constants.INSTANCE;

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COLUMN_ID,
                             COLUMN_BUSINESSKEY,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COLUMN_ID,
                             COLUMN_COMMANDNAME,
                             COL_ID_ACTIONS);
    }

    @Override
    public String getGridGlobalPreferencesKey() {
        return REQUEST_LIST_PREFIX;
    }

    @Override
    public String getNewFilterPopupTitle() {
        return constants.New_JobList();
    }

    @Override
    public void initColumns(ExtendedPagedTable extendedPagedTable) {
        Column actionsColumn = initActionsColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumn);

        final List<ColumnMeta<RequestSummary>> columnMetas = new ArrayList<ColumnMeta<RequestSummary>>();
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
                                                          req -> {
                                                              switch (req.getStatus()) {
                                                                  case QUEUED:
                                                                      return constants.Queued();
                                                                  case DONE:
                                                                      return constants.Completed();
                                                                  case CANCELLED:
                                                                      return constants.Canceled();
                                                                  case ERROR:
                                                                      return constants.Error();
                                                                  case RETRYING:
                                                                      return constants.Retrying();
                                                                  case RUNNING:
                                                                      return constants.Running();
                                                                  default:
                                                                      return "";
                                                              }
                                                          }),
                                         constants.Status()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_TIMESTAMP,
                                                          req -> DateUtils.getDateTimeStr(req.getTime())),
                                         constants.Due_On()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_NAME,
                                                          req -> req.getProcessName()),
                                         constants.Process_Name()));
        columnMetas.add(new ColumnMeta<>(createNumberColumn(COLUMN_PROCESS_INSTANCE_ID,
                                                            req -> req.getProcessInstanceId()),
                                         constants.Process_Instance_Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                          req -> req.getProcessInstanceDescription()),
                                         constants.Process_Description()));
        columnMetas.add(new ColumnMeta<>(actionsColumn,
                                         constants.Actions()));

        extendedPagedTable.addColumns(columnMetas);
    }

    @Override
    public void initSelectionModel(ExtendedPagedTable<RequestSummary> extendedPagedTable) {
        extendedPagedTable.setEmptyTableCaption(constants.No_Jobs_Found());
    }

    private Column<RequestSummary, RequestSummary> initActionsColumn() {
        List<HasCell<RequestSummary, ?>> cells = new LinkedList<HasCell<RequestSummary, ?>>();

        cells.add(new ConditionalButtonActionCell<RequestSummary>(
                constants.Details(),
                job -> presenter.showJobDetails(job),
                presenter.getDetailsActionCondition()));

        cells.add(new ConditionalButtonActionCell<RequestSummary>(
                constants.Cancel(),
                job -> {
                    if (Window.confirm(constants.CancelJob())) {
                        presenter.cancelRequest(job.getJobId());
                    }
                },
                presenter.getCancelActionCondition()));

        cells.add(new ConditionalButtonActionCell<RequestSummary>(
                constants.Requeue(),
                job -> {
                    if (Window.confirm(constants.RequeueJob())) {
                        presenter.requeueRequest(job.getJobId());
                    }
                },
                presenter.getRequeueActionCondition()));

        cells.add(new ConditionalButtonActionCell<RequestSummary>(
                constants.ViewProcessInstance(),
                job -> {
                    presenter.openProcessInstanceView(Long.toString(job.getProcessInstanceId()));
                },
                presenter.getViewProcessActionCondition()));

        CompositeCell<RequestSummary> cell = new CompositeCell<RequestSummary>(cells);
        Column<RequestSummary, RequestSummary> actionsColumn = new Column<RequestSummary, RequestSummary>(cell) {
            @Override
            public RequestSummary getValue(RequestSummary object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        return actionsColumn;
    }

    @Override
    public void initDefaultFilters() {
        super.initDefaultFilters();

        initTabFilter(presenter.createAllTabSettings(),
                      TAB_ALL,
                      constants.All(),
                      constants.FilterAll(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createQueuedTabSettings(),
                      TAB_QUEUED,
                      constants.Queued(),
                      constants.FilterQueued(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createRunningTabSettings(),
                      TAB_RUNNING,
                      constants.Running(),
                      constants.FilterRunning(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createRetryingTabSettings(),
                      TAB_RETRYING,
                      constants.Retrying(),
                      constants.FilterRetrying(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createErrorTabSettings(),
                      TAB_ERROR,
                      constants.Error(),
                      constants.FilterError(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createCompletedTabSettings(),
                      TAB_COMPLETED,
                      constants.Completed(),
                      constants.FilterCompleted(),
                      REQUEST_LIST_DATASET);
        initTabFilter(presenter.createCanceledTabSettings(),
                      TAB_CANCELED,
                      constants.Canceled(),
                      constants.FilterCanceled(),
                      REQUEST_LIST_DATASET);
    }
}