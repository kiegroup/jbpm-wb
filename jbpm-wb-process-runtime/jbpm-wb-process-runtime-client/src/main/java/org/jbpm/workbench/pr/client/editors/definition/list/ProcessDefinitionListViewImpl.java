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
package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractListView;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.ButtonActionCell;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractListView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class ProcessDefinitionListViewImpl extends AbstractListView<ProcessSummary, ProcessDefinitionListPresenter>
        implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

    public static final String COL_ID_PROCESSNAME = "ProcessName";
    public static final String COL_ID_PROCESSVERSION = "ProcessVersion";
    public static final String COL_ID_PROJECT = "Project";
    public static final String COL_ID_ACTIONS = "Actions";

    private Constants constants = Constants.INSTANCE;

    @Override
    protected ExtendedPagedTable<ProcessSummary> createListGrid(final GridGlobalPreferences preferences) {
        return new ListTable<>(preferences);
    }

    @Override
    public void init(final ProcessDefinitionListPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(COL_ID_PROCESSNAME);
        bannedColumns.add(COL_ID_ACTIONS);
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_PROCESSNAME);
        initColumns.add(COL_ID_PROCESSVERSION);
        initColumns.add(COL_ID_PROJECT);
        initColumns.add(COL_ID_ACTIONS);
        super.init(presenter,
                   new GridGlobalPreferences("ProcessDefinitionsGrid",
                                             initColumns,
                                             bannedColumns));

        selectionModel = new NoSelectionModel<ProcessSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedItem = selectionModel.getLastSelectedObject();

                presenter.selectProcessDefinition(selectedItem);
            }
        });
        final ExtendedPagedTable<ProcessSummary> extendedPagedTable = getListGrid();
        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<ProcessSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<ProcessSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<ProcessSummary> event) {
                        DefaultSelectionEventManager.SelectAction ret = DefaultSelectionEventManager.SelectAction.DEFAULT;
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType()) &&
                                // Ignore if the event didn't occur in the correct column.
                                extendedPagedTable.isSelectionIgnoreColumn(event.getColumn())) {
                            ret = DefaultSelectionEventManager.SelectAction.IGNORE;
                        }
                        return ret;
                    }
                });

        listGrid.setSelectionModel(selectionModel,
                                   noActionColumnManager);
        listGrid.setEmptyTableCaption(constants.No_Process_Definitions_Found());
    }

    @Override
    public void initColumns(ExtendedPagedTable extendedPagedTable) {
        Column processNameColumn = initProcessNameColumn();
        Column versionColumn = initVersionColumn();
        Column projectColumn = initProjectColumn();
        Column actionsColumn = initActionsColumn();

        extendedPagedTable.addSelectionIgnoreColumn(actionsColumn);

        List<ColumnMeta<ProcessSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessSummary>>();
        columnMetas.add(new ColumnMeta<>(processNameColumn,
                                         constants.Name()));
        columnMetas.add(new ColumnMeta<>(versionColumn,
                                         constants.Version()));
        columnMetas.add(new ColumnMeta<>(projectColumn,
                                         constants.Project()));
        final ColumnMeta columnMeta = new ColumnMeta<>(actionsColumn,
                                                       "");
        final TextHeader header = new TextHeader(constants.Actions());
        header.setHeaderStyleNames("text-center");
        columnMeta.setHeader(header);
        columnMetas.add(columnMeta);

        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(actionsColumn,
                                          AbstractMultiGridView.ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.getColumnSortList().push(processNameColumn);
    }

    private Column initProcessNameColumn() {
        // Process Name String.
        Column<ProcessSummary, String> processNameColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getProcessDefName();
            }
        };
        processNameColumn.setSortable(true);
        processNameColumn.setDefaultSortAscending(false);
        processNameColumn.setDataStoreName(COL_ID_PROCESSNAME);
        return processNameColumn;
    }

    private Column initVersionColumn() {
        Column<ProcessSummary, String> versionColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getVersion();
            }
        };
        versionColumn.setSortable(true);
        versionColumn.setDataStoreName(COL_ID_PROCESSVERSION);
        return versionColumn;
    }

    private Column initProjectColumn() {
        Column<ProcessSummary, String> projectColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getDeploymentId();
            }
        };
        projectColumn.setSortable(true);
        projectColumn.setDataStoreName(COL_ID_PROJECT);
        return projectColumn;
    }

    private Column initActionsColumn() {
        final StartButtonActionCell actionCell = new StartButtonActionCell(constants.Start(),
                                                                           processSummary ->
                                                                                   presenter.openGenericForm(processSummary.getProcessDefId(),
                                                                                                             processSummary.getDeploymentId(),
                                                                                                             processSummary.getProcessDefName()));

        Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>(actionCell.getCell()) {
            @Override
            public ProcessSummary getValue(ProcessSummary object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        actionsColumn.setCellStyleNames("text-center");
        return actionsColumn;
    }

    protected class StartButtonActionCell extends ButtonActionCell<ProcessSummary> {

        public StartButtonActionCell(final String text,
                                     final ActionCell.Delegate<ProcessSummary> delegate) {
            super(text,
                  delegate,
                  ButtonSize.DEFAULT);
        }

        @Override
        public void render(final Cell.Context context,
                           final ProcessSummary summary,
                           final SafeHtmlBuilder sb) {
            if (summary.isDynamic() == false) {
                super.render(context,
                             summary,
                             sb);
            }
        }
    }
}
