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
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ConditionalButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
public class ProcessInstanceListViewImpl extends AbstractMultiGridView<ProcessInstanceSummary, ProcessInstanceListPresenter>
        implements ProcessInstanceListPresenter.ProcessInstanceListView {

    public static final String TAB_ACTIVE = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0";
    public static final String TAB_COMPLETED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1";
    public static final String TAB_ABORTED = PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2";

    private final Constants constants = Constants.INSTANCE;

    @Inject
    private ManagedInstance<ProcessInstanceSummaryErrorPopoverCell> popoverCellInstance;

    @Override
    public void init( final ProcessInstanceListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(COL_ID_SELECT);
        bannedColumns.add(COLUMN_PROCESS_INSTANCE_ID);
        bannedColumns.add(COLUMN_PROCESS_NAME);
        bannedColumns.add(COLUMN_PROCESS_INSTANCE_DESCRIPTION);
        bannedColumns.add(COL_ID_ACTIONS);
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_SELECT);
        initColumns.add(COLUMN_PROCESS_INSTANCE_ID);
        initColumns.add(COLUMN_PROCESS_NAME);
        initColumns.add(COLUMN_PROCESS_INSTANCE_DESCRIPTION);
        initColumns.add(COLUMN_PROCESS_VERSION);
        initColumns.add(COLUMN_LAST_MODIFICATION_DATE);
        initColumns.add(COLUMN_ERROR_COUNT);
        initColumns.add(COL_ID_ACTIONS);

        createTabButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid( PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_" );

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, createTabButton, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } );
                        applyFilterOnPresenter( key );

                    }
                };
                FilterSettings tableSettings = presenter.createTableSettingsPrototype();
                tableSettings.setKey( key );
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, constants.New_Process_InstanceList(), tableSettings, addNewGrid );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX, initColumns, bannedColumns ) );
    }

    @Override
    public void initSelectionModel(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        extendedPagedTable.setEmptyTableCaption(constants.No_Process_Instances_Found());
        extendedPagedTable.setSelectionCallback((pis, close) -> presenter.selectProcessInstance(pis,
                                                                                                close));
        initBulkActions(extendedPagedTable);
    }

    @Override
    public void initColumns( ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        final ColumnMeta checkColumnMeta = initChecksColumn(extendedPagedTable);

        Column<ProcessInstanceSummary, ?> actionsColumn = initActionsColumn();
        Column<ProcessInstanceSummary, ?> errorCountColumn = initErrorCountColumn();
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumn);
        extendedPagedTable.addSelectionIgnoreColumn(errorCountColumn);

        final List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();

        columnMetas.add(checkColumnMeta);
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_ID,
                                                          process -> String.valueOf(process.getProcessInstanceId())),
                                         constants.Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_NAME,
                                                          process -> process.getProcessName()),
                                         constants.Name()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                          process -> process.getProcessInstanceDescription()),
                                         constants.Process_Instance_Description()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_IDENTITY,
                                                          process -> process.getInitiator()),
                                         constants.Initiator()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_VERSION,
                                                          process -> process.getProcessVersion()),
                                         constants.Version()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_STATUS,
                                                          process -> {
                                                              switch (process.getState()) {
                                                                  case ProcessInstance.STATE_ACTIVE:
                                                                      return constants.Active();
                                                                  case ProcessInstance.STATE_ABORTED:
                                                                      return constants.Aborted();
                                                                  case ProcessInstance.STATE_COMPLETED:
                                                                      return constants.Completed();
                                                                  case ProcessInstance.STATE_PENDING:
                                                                      return constants.Pending();
                                                                  case ProcessInstance.STATE_SUSPENDED:
                                                                      return constants.Suspended();
                                                                  default:
                                                                      return constants.Unknown();
                                                              }
                                                          }),
                                         constants.State()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_START,
                                                          process -> DateUtils.getDateTimeStr(process.getStartTime())),
                                         constants.Start_Date()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_LAST_MODIFICATION_DATE,
                                                          process -> DateUtils.getDateTimeStr(process.getLastModificationDate())),
                                         constants.Last_Modification_Date()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_CORRELATION_KEY,
                                                          process -> process.getCorrelationKey()),
                                         constants.Correlation_Key()));
        columnMetas.add(new ColumnMeta<>(errorCountColumn, constants.Errors()));
        columnMetas.add(new ColumnMeta<>(actionsColumn, constants.Actions()));

        List<GridColumnPreference> columPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();

        for (GridColumnPreference colPref : columPreferenceList) {
            if (!isColumnAdded(columnMetas,
                               colPref.getName())) {
                Column genericColumn = initGenericColumn(colPref.getName());
                genericColumn.setSortable(false);
                columnMetas.add(new ColumnMeta<ProcessInstanceSummary>(genericColumn,
                                                                       colPref.getName(),
                                                                       true,
                                                                       true));
            }
        }
        extendedPagedTable.addColumns(columnMetas);
        extendedPagedTable.setColumnWidth(checkColumnMeta.getColumn(), 38, Style.Unit.PX);
        extendedPagedTable.setColumnWidth(errorCountColumn, 65, Style.Unit.PX);
        
        extendedPagedTable.storeColumnToPreferences();
    }
    
    private boolean isColumnAdded( List<ColumnMeta<ProcessInstanceSummary>> columnMetas,
                                   String caption ) {
        if ( caption != null ) {
            for ( ColumnMeta<ProcessInstanceSummary> colMet : columnMetas ) {
                if ( caption.equals( colMet.getColumn().getDataStoreName() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addDomainSpecifColumns( ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable,
                                        Set<String> columns ) {

        extendedPagedTable.storeColumnToPreferences();

        HashMap modifiedCaptions = new HashMap<String, String>();
        ArrayList<ColumnMeta> existingExtraColumns = new ArrayList<ColumnMeta>();
        for ( ColumnMeta<ProcessInstanceSummary> cm : extendedPagedTable.getColumnMetaList() ) {
            if ( cm.isExtraColumn() ) {
                existingExtraColumns.add( cm );
            } else if ( columns.contains( cm.getCaption() ) ) {      //exist a column with the same caption
                for ( String c : columns ) {
                    if ( c.equals( cm.getCaption() ) ) {
                        modifiedCaptions.put( c, "Var_" + c );
                    }
                }
            }
        }
        for ( ColumnMeta colMet : existingExtraColumns ) {
            if ( !columns.contains( colMet.getCaption() ) ) {
                extendedPagedTable.removeColumnMeta( colMet );
            } else {
                columns.remove( colMet.getCaption() );
            }
        }

        List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();
        String caption = "";
        for ( String c : columns ) {
            caption = c;
            if ( modifiedCaptions.get( c ) != null ) {
                caption = (String) modifiedCaptions.get( c );
            }
            Column genericColumn = initGenericColumn( c );
            genericColumn.setSortable( false );

            columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( genericColumn, caption, true, true ) );
        }

        extendedPagedTable.addColumns( columnMetas );

    }

    private Column initGenericColumn( final String key ) {

        Column<ProcessInstanceSummary, String> genericColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getDomainDataValue( key );
            }
        };
        genericColumn.setSortable( true );
        genericColumn.setDataStoreName(key);

        return genericColumn;
    }

    private void initBulkActions( final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        extendedPagedTable.getRightActionsToolbar().clear();
        final AnchorListItem bulkAbortNavLink = GWT.create(AnchorListItem.class);
        bulkAbortNavLink.setText(constants.Bulk_Abort());
        final AnchorListItem bulkSignalNavLink = GWT.create(AnchorListItem.class);
        bulkSignalNavLink.setText(constants.Bulk_Signal());

        final ButtonGroup bulkActions = GWT.create(ButtonGroup.class);

        final Button bulkButton = GWT.create(Button.class);
        bulkButton.setText(constants.Bulk_Actions());
        bulkButton.setDataToggle(Toggle.DROPDOWN);
        bulkButton.getElement().getStyle().setMarginRight(5,
                                                          Style.Unit.PX);
        bulkActions.add(bulkButton);

        final DropDownMenu bulkDropDown = GWT.create(DropDownMenu.class);
        bulkDropDown.addStyleName(Styles.DROPDOWN_MENU + "-right");
        bulkDropDown.getElement().getStyle().setMarginRight(5,
                                                            Style.Unit.PX);
        bulkDropDown.add(bulkAbortNavLink);
        bulkDropDown.add(bulkSignalNavLink);
        bulkActions.add(bulkDropDown);

        bulkAbortNavLink.setIcon( IconType.BAN );
        bulkAbortNavLink.setIconFixedWidth( true );
        bulkAbortNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if( Window.confirm( constants.Abort_Process_Instances() ) ) {
                    presenter.bulkAbort(extendedPagedTable.getSelectedItems());
                    extendedPagedTable.deselectAllItems();
                    extendedPagedTable.redraw();
                }
            }
        } );

        bulkSignalNavLink.setIcon( IconType.BELL );
        bulkSignalNavLink.setIconFixedWidth( true );
        bulkSignalNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.bulkSignal(extendedPagedTable.getSelectedItems());
                extendedPagedTable.deselectAllItems();
                extendedPagedTable.redraw();
            }
        } );

        extendedPagedTable.getRightActionsToolbar().add(bulkActions);
    }

    private Column<ProcessInstanceSummary, ProcessInstanceSummary> initErrorCountColumn() {
        
        Column<ProcessInstanceSummary, ProcessInstanceSummary> column = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
                popoverCellInstance.get().init(presenter)){

            @Override
            public ProcessInstanceSummary getValue(ProcessInstanceSummary process) {
                return process;
            }

        };

        column.setSortable(true);
        column.setDataStoreName(COLUMN_ERROR_COUNT);
        return column;
    }
    
    private Column<ProcessInstanceSummary, ProcessInstanceSummary> initActionsColumn() {
        List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();

        cells.add(new ConditionalButtonActionCell<ProcessInstanceSummary>(
                constants.Signal(),
                processInstance -> presenter.signalProcessInstance(processInstance),
                presenter.getSignalActionCondition()
        ));

        cells.add(new ConditionalButtonActionCell<ProcessInstanceSummary>(
                constants.Abort(),
                processInstance -> {
                    if (Window.confirm(constants.Abort_Process_Instance())) {
                        presenter.abortProcessInstance(processInstance.getDeploymentId(),
                                                       processInstance.getProcessInstanceId());
                    }
                },
                presenter.getAbortActionCondition()
        ));

        cells.add(new ConditionalButtonActionCell<ProcessInstanceSummary>(
                constants.ViewJobs(),
                processInstance -> presenter.openJobsView(Long.toString(processInstance.getProcessInstanceId())),
                presenter.getViewJobsActionCondition()
        ));

        cells.add(new ConditionalButtonActionCell<ProcessInstanceSummary>(
                constants.ViewTasks(),
                processInstance -> presenter.openTaskView(Long.toString(processInstance.getProcessInstanceId())),
                presenter.getViewTasksActionCondition()
        ));
        
        cells.add(new ConditionalButtonActionCell<ProcessInstanceSummary>(
                constants.ViewErrors(),
                processInstance -> presenter.openErrorView(Long.toString(processInstance.getProcessInstanceId())),
                presenter.getViewErrorsActionCondition()
        ));

        CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>(cells);
        Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(cell) {
            @Override
            public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
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

        //Filter status Active
        initGenericTabFilter(presenter.createActiveTabSettings(),
                             TAB_ACTIVE,
                             constants.Active(),
                             constants.FilterActive(),
                             preferences);

        //Filter status completed
        initGenericTabFilter(presenter.createCompletedTabSettings(),
                             TAB_COMPLETED,
                             constants.Completed(),
                             constants.FilterCompleted(),
                             preferences);

        //Filter status aborted
        initGenericTabFilter(presenter.createAbortedTabSettings(),
                             TAB_ABORTED,
                             constants.Aborted(),
                             constants.FilterAborted(),
                             preferences);

        filterPagedTable.addAddTableButton( createTabButton );
    }

    private void initGenericTabFilter( FilterSettings tableSettings,
                                       final String key,
                                       String tabName,
                                       String tabDesc,
                                       GridGlobalPreferences preferences ) {

        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );
        tableSettings.setUUID( PROCESS_INSTANCE_DATASET );

        addNewTab(preferences,
                  tableSettings);
    }

    @Override
    public void resetDefaultFilterTitleAndDescription(){
        super.resetDefaultFilterTitleAndDescription();
        saveTabSettings(TAB_ACTIVE,
                        constants.Active(),
                        constants.FilterActive());
        saveTabSettings(TAB_COMPLETED,
                        constants.Completed(),
                        constants.FilterCompleted());
        saveTabSettings(TAB_ABORTED,
                        constants.Aborted(),
                        constants.FilterAborted());
    }

}
