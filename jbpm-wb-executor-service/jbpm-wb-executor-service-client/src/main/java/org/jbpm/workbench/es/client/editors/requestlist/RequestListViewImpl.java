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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ButtonActionCell;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
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

    private List<RequestSummary> selectedRequestSummary = new ArrayList<RequestSummary>();

    @Inject
    private DataSetEditorManager dataSetEditorManager;

    @Override
    public void init( final RequestListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( COLUMN_ID );
        bannedColumns.add( COLUMN_COMMANDNAME );
        bannedColumns.add( COL_ID_ACTIONS );
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add( COLUMN_ID );
        initColumns.add( COLUMN_BUSINESSKEY );
        initColumns.add( COLUMN_COMMANDNAME );
        initColumns.add( COL_ID_ACTIONS );
        final Button button = GWT.create(Button.class);
        button.setIcon( IconType.PLUS );
        button.setSize( ButtonSize.SMALL );
        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid( REQUEST_LIST_PREFIX + "_" );

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<RequestSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, button, new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } );
                        applyFilterOnPresenter( key );

                    }
                };
                FilterSettings tableSettings = createTableSettingsPrototype();
                tableSettings.setKey( key );
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, constants.New_JobList(), tableSettings, addNewGrid );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( REQUEST_LIST_PREFIX, initColumns, bannedColumns ), button );
    }

    @Override
    public void initColumns( ExtendedPagedTable extendedPagedTable ) {
        actionsColumn = initActionsColumn();
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

    public void initSelectionModel() {
        final ExtendedPagedTable<RequestSummary> extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption( constants.No_Jobs_Found() );

        selectionModel = new NoSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {

                if ( selectedRow == -1 ) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else if ( extendedPagedTable.getKeyboardSelectedRow() != selectedRow ) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                }
            }
        } );
        initNoActionColumnManager( extendedPagedTable );

        extendedPagedTable.setSelectionModel( selectionModel, noActionColumnManager );
        extendedPagedTable.setRowStyles( selectedStyles );
    }

    private void initNoActionColumnManager( final ExtendedPagedTable extendedPagedTable ) {
        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<RequestSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<RequestSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<RequestSummary> event ) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if ( BrowserEvents.CLICK.equals( nativeEvent.getType() ) ) {
                            // Ignore if the event didn't occur in the correct column.
                            if ( extendedPagedTable.getColumnIndex( actionsColumn ) == event.getColumn() ) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                            //Extension for checkboxes
                            Element target = nativeEvent.getEventTarget().cast();
                            if ( "input".equals( target.getTagName().toLowerCase() ) ) {
                                final InputElement input = target.cast();
                                if ( "checkbox".equals( input.getType().toLowerCase() ) ) {
                                    // Synchronize the checkbox with the current selection state.
                                    if ( !selectedRequestSummary.contains( event.getValue() ) ) {
                                        selectedRequestSummary.add( event.getValue() );
                                        input.setChecked( true );
                                    } else {
                                        selectedRequestSummary.remove( event.getValue() );
                                        input.setChecked( false );
                                    }
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

    }

    private Column<RequestSummary, RequestSummary> initActionsColumn() {
        List<HasCell<RequestSummary, ?>> cells = new LinkedList<HasCell<RequestSummary, ?>>();
        List<String> allStatuses = new ArrayList<String>();
        allStatuses.add( "QUEUED" );
        allStatuses.add( "DONE" );
        allStatuses.add( "CANCELLED" );
        allStatuses.add( "ERROR" );
        allStatuses.add( "RETRYING" );
        allStatuses.add( "RUNNING" );
        cells.add( new ActionHasCell( constants.Details(), allStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                presenter.showJobDetails( job );
            }
        } ) );

        List<String> activeStatuses = new ArrayList<String>();
        activeStatuses.add( "QUEUED" );
        activeStatuses.add( "RETRYING" );
        activeStatuses.add( "RUNNING" );
        cells.add( new ActionHasCell( constants.Cancel(), activeStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( constants.CancelJob() ) ) {
                    presenter.cancelRequest( job.getJobId() );
                }
            }
        } ) );

        List<String> requeueStatuses = new ArrayList<String>();
        requeueStatuses.add( "ERROR" );
        requeueStatuses.add( "RUNNING" );
        cells.add( new ActionHasCell( constants.Requeue(), requeueStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( constants.RequeueJob() ) ) {
                    presenter.requeueRequest( job.getJobId() );
                }
            }
        } ) );

        CompositeCell<RequestSummary> cell = new CompositeCell<RequestSummary>( cells );
        Column<RequestSummary, RequestSummary> actionsColumn = new Column<RequestSummary, RequestSummary>( cell ) {
            @Override
            public RequestSummary getValue( RequestSummary object ) {
                return object;
            }
        };
        actionsColumn.setDataStoreName( COL_ID_ACTIONS );
        return actionsColumn;
    }

    private class ActionHasCell extends ButtonActionCell<RequestSummary> {

        private final List<String> availableStatuses;

        public ActionHasCell( final String text,
                              List<String> availableStatusesList,
                              Delegate<RequestSummary> delegate ) {
            super( text, delegate );
            this.availableStatuses = availableStatusesList;
        }

        @Override
        public void render(Cell.Context context, RequestSummary value, SafeHtmlBuilder sb) {
            if ( availableStatuses.contains( value.getStatus() ) ) {
                super.render(context, value, sb);
            }
        }

    }

    public void initDefaultFilters( GridGlobalPreferences preferences,
                                    Button createTabButton ) {

        presenter.setAddingDefaultFilters( true );

        initTabFilter(preferences,
                      TAB_ALL,
                      constants.All(),
                      constants.FilterAll(),
                      null);
        initTabFilter(preferences,
                      TAB_QUEUED,
                      constants.Queued(),
                      constants.FilterQueued(),
                      "QUEUED");
        initTabFilter(preferences,
                      TAB_RUNNING,
                      constants.Running(),
                      constants.FilterRunning(),
                      "RUNNING");
        initTabFilter(preferences,
                      TAB_RETRYING,
                      constants.Retrying(),
                      constants.FilterRetrying(),
                      "RETRYING");
        initTabFilter(preferences,
                      TAB_ERROR,
                      constants.Error(),
                      constants.FilterError(),
                      "ERROR");
        initTabFilter(preferences,
                      TAB_COMPLETED,
                      constants.Completed(),
                      constants.FilterCompleted(),
                      "DONE");
        initTabFilter(preferences,
                      TAB_CANCELLED,
                      constants.Cancelled(),
                      constants.FilterCancelled(),
                      "CANCELLED");

        filterPagedTable.addAddTableButton( createTabButton );

        selectFirstTabAndEnableQueries(TAB_ALL);
    }

    private void initTabFilter( GridGlobalPreferences preferences,
                                final String key,
                                String tabName,
                                String tabDesc,
                                String status ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();
        builder.dataset( REQUEST_LIST_DATASET );
        if ( status != null ) {
            builder.filter( equalsTo( COLUMN_STATUS, status ) );
        }
        builder.setColumn( COLUMN_ID, constants.Id() );
        builder.setColumn( COLUMN_TIMESTAMP, constants.Time(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_COMMANDNAME, constants.CommandName() );
        builder.setColumn( COLUMN_MESSAGE, constants.Message() );
        builder.setColumn( COLUMN_BUSINESSKEY, constants.Key() );
        builder.setColumn( COLUMN_RETRIES, constants.Retries() );
        builder.setColumn( COLUMN_EXECUTIONS, constants.Executions() );
        builder.setColumn( COLUMN_PROCESS_NAME, constants.Process_Name() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Description() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setUUID( REQUEST_LIST_DATASET );
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<RequestSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
        currentListGrid = extendedPagedTable;
        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key );
            }
        } );

    }

    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( REQUEST_LIST_DATASET );
        builder.setColumn( COLUMN_ID, constants.Id() );
        builder.setColumn( COLUMN_TIMESTAMP, constants.Time(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_STATUS, constants.Status() );
        builder.setColumn( COLUMN_COMMANDNAME, constants.CommandName(), DateUtils.getDateTimeFormatMask() );
        builder.setColumn( COLUMN_MESSAGE, constants.Message() );
        builder.setColumn( COLUMN_BUSINESSKEY, constants.Key() );
        builder.setColumn( COLUMN_RETRIES, constants.Retries() );
        builder.setColumn( COLUMN_EXECUTIONS, constants.Executions() );
        builder.setColumn( COLUMN_PROCESS_NAME, constants.Process_Name() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_Id() );
        builder.setColumn( COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Description() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, DESCENDING );
        builder.tableWidth( 1000 );

        return builder.buildSettings();

    }

    @Override
    public void resetDefaultFilterTitleAndDescription() {
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