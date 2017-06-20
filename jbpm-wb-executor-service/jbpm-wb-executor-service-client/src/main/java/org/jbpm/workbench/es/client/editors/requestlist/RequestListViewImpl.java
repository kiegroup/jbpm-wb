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
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.gwtbootstrap3.client.ui.Button;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ConditionalButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;

@Dependent
public class RequestListViewImpl extends AbstractMultiGridView<RequestSummary, RequestListPresenter>
        implements RequestListPresenter.RequestListView {

    private final Constants constants = Constants.INSTANCE;

    public static String REQUEST_LIST_PREFIX = "DS_RequestListGrid";
    public static final String COL_ID_ACTIONS = "Actions";
    private static final String TAB_CANCELLED = REQUEST_LIST_PREFIX + "_6";
    private static final String TAB_COMPLETED = REQUEST_LIST_PREFIX + "_5";
    private static final String TAB_ERROR = REQUEST_LIST_PREFIX + "_4";
    private static final String TAB_RETRYING = REQUEST_LIST_PREFIX + "_3";
    private static final String TAB_RUNNING = REQUEST_LIST_PREFIX + "_2";
    private static final String TAB_QUEUED = REQUEST_LIST_PREFIX + "_1";
    private static final String TAB_ALL = REQUEST_LIST_PREFIX + "_0";

    @Override
    public void init(final RequestListPresenter presenter) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(COLUMN_ID);
        bannedColumns.add(COLUMN_COMMANDNAME);
        bannedColumns.add(COL_ID_ACTIONS);
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(COLUMN_ID);
        initColumns.add(COLUMN_BUSINESSKEY);
        initColumns.add(COLUMN_COMMANDNAME);
        initColumns.add(COL_ID_ACTIONS);

        createTabButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                final String key = getValidKeyForAdditionalListGrid(REQUEST_LIST_PREFIX + "_");

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<RequestSummary> extendedPagedTable = createGridInstance(new GridGlobalPreferences(key, initColumns, bannedColumns), key);

                        extendedPagedTable.setDataProvider(presenter.getDataProvider());

                        filterPagedTable.createNewTab(extendedPagedTable, key, createTabButton, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter(key);
                            }
                        } );
                        applyFilterOnPresenter(key);

                    }
                };
                FilterSettings tableSettings = presenter.createTableSettingsPrototype();
                tableSettings.setKey(key);
                dataSetEditorManager.showTableSettingsEditor(filterPagedTable, constants.New_JobList(), tableSettings, addNewGrid);

            }
        } );

        super.init(presenter, new GridGlobalPreferences(REQUEST_LIST_PREFIX, initColumns, bannedColumns));
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
                                                          req -> req.getStatus()),
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
    public void initDefaultFilters(final GridGlobalPreferences preferences,
                                   final Button createTabButton) {
        super.initDefaultFilters(preferences,
                                 createTabButton);

        initTabFilter(presenter.createAllTabSettings(),
                      TAB_ALL,
                      constants.All(),
                      constants.FilterAll(),
                      preferences);
        initTabFilter(presenter.createQueuedTabSettings(),
                      TAB_QUEUED,
                      constants.Queued(),
                      constants.FilterQueued(),
                      preferences);
        initTabFilter(presenter.createRunningTabSettings(),
                      TAB_RUNNING,
                      constants.Running(),
                      constants.FilterRunning(),
                      preferences);
        initTabFilter(presenter.createRetryingTabSettings(),
                      TAB_RETRYING,
                      constants.Retrying(),
                      constants.FilterRetrying(),
                      preferences);
        initTabFilter(presenter.createErrorTabSettings(),
                      TAB_ERROR,
                      constants.Error(),
                      constants.FilterError(),
                      preferences);
        initTabFilter(presenter.createCompletedTabSettings(),
                      TAB_COMPLETED,
                      constants.Completed(),
                      constants.FilterCompleted(),
                      preferences);
        initTabFilter(presenter.createCancelledTabSettings(),
                      TAB_CANCELLED,
                      constants.Cancelled(),
                      constants.FilterCancelled(),
                      preferences);

        filterPagedTable.addAddTableButton(createTabButton);
    }

    private void initTabFilter(FilterSettings tableSettings,
                               final String key,
                               String tabName,
                               String tabDesc,
                               GridGlobalPreferences preferences) {
        tableSettings.setUUID(REQUEST_LIST_DATASET);
        tableSettings.setKey(key);
        tableSettings.setTableName(tabName);
        tableSettings.setTableDescription(tabDesc);

        addNewTab(preferences, tableSettings);
    }

    @Override
    public void resetDefaultFilterTitleAndDescription() {
        super.resetDefaultFilterTitleAndDescription();
        saveTabSettings(TAB_ALL,
                        constants.All(),
                        constants.FilterAll());
        saveTabSettings(TAB_QUEUED,
                        constants.Queued(),
                        constants.FilterQueued());
        saveTabSettings(TAB_RUNNING,
                        constants.Running(),
                        constants.FilterRunning());
        saveTabSettings(TAB_RETRYING,
                        constants.Retrying(),
                        constants.FilterRetrying());
        saveTabSettings(TAB_ERROR,
                        constants.Error(),
                        constants.FilterError());
        saveTabSettings(TAB_COMPLETED,
                        constants.Completed(),
                        constants.FilterCompleted());
        saveTabSettings(TAB_CANCELLED,
                        constants.Cancelled(),
                        constants.FilterCancelled());
    }

}