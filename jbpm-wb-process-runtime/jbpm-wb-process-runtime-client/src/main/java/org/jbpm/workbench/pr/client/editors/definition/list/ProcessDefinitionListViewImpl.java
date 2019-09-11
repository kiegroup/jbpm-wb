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
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.ConditionalAction;
import org.jbpm.workbench.common.client.util.ConditionalKebabActionCell;
import org.jbpm.workbench.common.preferences.ManagePreferences;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSNAME;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROCESSVERSION;
import static org.jbpm.workbench.pr.model.ProcessDefinitionDataSetConstants.COL_ID_PROJECT;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class ProcessDefinitionListViewImpl extends AbstractMultiGridView<ProcessSummary, ProcessDefinitionListPresenter>
        implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

    private Constants constants = Constants.INSTANCE;

    @Inject
    protected ManagePreferences preferences;

    @Inject
    protected ManagedInstance<ConditionalKebabActionCell> conditionalKebabActionCell;


    @Override
    public void displayBlockingError(final String summary,
                                     final String content) {
        column.classList.add("hidden");
        alert.getElement().classList.remove("hidden");
        alert.setSummary(summary);
        alert.setDescription(content);
    }

    @Override
    public void clearBlockingError() {
        alert.getElement().classList.add("hidden");
        alert.setSummary("");
        alert.setDescription("");
        column.classList.remove("hidden");
    }

    @Override
    public void initColumns(ListTable<ProcessSummary> extendedPagedTable) {
        Column processNameColumn = initProcessNameColumn();
        Column versionColumn = initVersionColumn();
        Column deploymentColumn = initDeploymentColumn();
        ColumnMeta<ProcessSummary> actionsColumnMeta = initActionsColumn();

        List<ColumnMeta<ProcessSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessSummary>>();
        columnMetas.add(new ColumnMeta<>(processNameColumn,
                                         constants.Name()));
        columnMetas.add(new ColumnMeta<>(versionColumn,
                                         constants.Version()));
        columnMetas.add(new ColumnMeta<>(deploymentColumn,
                                         constants.Deployment_Name()));
        columnMetas.add(actionsColumnMeta);

        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());
        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(actionsColumnMeta.getColumn(),
                                          AbstractMultiGridView.ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.getColumnSortList().push(processNameColumn);
    }

    @Override
    public String getEmptyTableCaption() {
        return constants.No_Process_Definitions_Found();
    }

    @Override
    public List<String> getInitColumns() {
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_PROCESSNAME);
        initColumns.add(COL_ID_PROCESSVERSION);
        initColumns.add(COL_ID_PROJECT);
        initColumns.add(COL_ID_ACTIONS);
        return initColumns;
    }

    @Override
    public List<String> getBannedColumns() {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(COL_ID_PROCESSNAME);
        bannedColumns.add(COL_ID_ACTIONS);
        return bannedColumns;
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

    private Column initDeploymentColumn() {
        Column<ProcessSummary, String> deploymentColumn = new Column<ProcessSummary, String>(new TextCell()) {
            @Override
            public String getValue(ProcessSummary object) {
                return object.getDeploymentId();
            }
        };
        deploymentColumn.setSortable(true);
        deploymentColumn.setDataStoreName(COL_ID_PROJECT);
        return deploymentColumn;
    }

    protected List<ConditionalAction<ProcessSummary>> getConditionalActions() {
        return Arrays.asList(

                new ConditionalAction<>(
                        constants.Start(),
                        processSummary ->
                                presenter.openGenericForm(processSummary.getProcessDefId(),
                                                          processSummary.getDeploymentId(),
                                                          processSummary.getProcessDefName(),
                                                          processSummary.isDynamic()),
                        presenter.getStartCondition(),
                        false
                ),

                new ConditionalAction<>(
                        constants.View_Process_Instances(),
                        processSummary ->
                                presenter.viewProcessInstances(processSummary.getProcessDefId()),
                        presenter.getViewProcessInstanceActionCondition(),
                        true
                )
        );
    }

    @Override
    protected boolean hasBulkActions(){
        return false;
    }
}
