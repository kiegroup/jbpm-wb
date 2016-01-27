/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.es.client.editors.requestlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
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
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.es.client.editors.jobdetails.JobDetailsPopup;
import org.jbpm.console.ng.es.client.editors.quicknewjob.QuickNewJobPopup;
import org.jbpm.console.ng.es.client.editors.servicesettings.JobServiceSettingsPopup;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.gc.client.util.ButtonActionCell;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;

@Dependent
public class RequestListViewImpl extends AbstractMultiGridView<RequestSummary, RequestListPresenter>
        implements RequestListPresenter.RequestListView {

    private Constants constants = GWT.create( Constants.class );

    public static String REQUEST_LIST_PREFIX = "DS_RequestListGrid";
    public static final String REQUEST_LIST_DATASET_ID = "jbpmRequestList";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_COMMANDNAME = "commandName";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_BUSINESSKEY = "businessKey";

    public static final String COL_ID_ACTIONS = "Actions";

    @Inject
    private Event<NotificationEvent> notification;

    private List<RequestSummary> selectedRequestSummary = new ArrayList<RequestSummary>();

    @Inject
    private JobDetailsPopup jobDetailsPopup;

    @Inject
    private QuickNewJobPopup quickNewJobPopup;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private DataSetEditorManager dataSetEditorManager;

    @Inject
    private JobServiceSettingsPopup jobServiceSettingsPopup;

    @Override
    public void init( final RequestListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( COLUMN_ID );
        bannedColumns.add( COLUMN_COMMANDNAME );
        bannedColumns.add( COL_ID_ACTIONS );
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add( COLUMN_ID );
        initColumns.add( COLUMN_COMMANDNAME );
        initColumns.add( COL_ID_ACTIONS );
        final Button button = new Button();
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
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, Constants.INSTANCE.New_JobList(), tableSettings, addNewGrid );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( REQUEST_LIST_PREFIX, initColumns, bannedColumns ), button );
    }

    public void requestCreated( @Observes RequestChangedEvent event ) {
        presenter.refreshGrid();
    }

    @Override
    public void initColumns( ExtendedPagedTable extendedPagedTable ) {
        Column jobIdColumn = initJobIdColumn();
        Column jobTypeColumn = initJobTypeColumn();
        Column statusColumn = initStatusColumn();
        Column dueDateColumn = initDueDateColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<RequestSummary>> columnMetas = new ArrayList<ColumnMeta<RequestSummary>>();
        columnMetas.add( new ColumnMeta<RequestSummary>( jobIdColumn, constants.Id() ) );
        columnMetas.add( new ColumnMeta<RequestSummary>( jobTypeColumn, constants.Type() ) );
        columnMetas.add( new ColumnMeta<RequestSummary>( statusColumn, constants.Status() ) );
        columnMetas.add( new ColumnMeta<RequestSummary>( dueDateColumn, constants.Due_On() ) );
        columnMetas.add( new ColumnMeta<RequestSummary>( actionsColumn, constants.Actions() ) );
        extendedPagedTable.addColumns( columnMetas );

    }

    public void initSelectionModel() {
        final ExtendedPagedTable<RequestSummary> extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption( constants.No_Jobs_Found() );

        initLeftToolbarActions( extendedPagedTable );

        selectionModel = new NoSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange( SelectionChangeEvent event ) {

                boolean close = false;
                if ( selectedRow == -1 ) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();

                } else if ( extendedPagedTable.getKeyboardSelectedRow() != selectedRow ) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
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

    private void initLeftToolbarActions( ExtendedPagedTable extendedPagedTable ) {
    }

    private Column initJobIdColumn() {
        // Id
        Column<RequestSummary, Number> jobIdColumn = new Column<RequestSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( RequestSummary object ) {
                return object.getJobId();
            }
        };
        jobIdColumn.setSortable( true );
        jobIdColumn.setDataStoreName( COLUMN_ID );

        return jobIdColumn;

    }

    private Column initJobTypeColumn() {
        // Name
        Column<RequestSummary, String> jobTypeColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getCommandName();
            }
        };
        jobTypeColumn.setSortable( true );
        jobTypeColumn.setDataStoreName( COLUMN_COMMANDNAME );
        return jobTypeColumn;

    }

    private Column initStatusColumn() {
        // Status
        Column<RequestSummary, String> statusColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        statusColumn.setDataStoreName( COLUMN_STATUS );

        return statusColumn;

    }

    private Column initDueDateColumn() {
        // Time
        Column<RequestSummary, String> dueDateColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getTime().toString();
            }
        };
        dueDateColumn.setSortable( true );
        dueDateColumn.setDataStoreName( COLUMN_TIMESTAMP );
        return dueDateColumn;

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
        cells.add( new ActionHasCell( Constants.INSTANCE.Details(), allStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                jobDetailsPopup.show( String.valueOf( job.getJobId() ) );
            }
        } ) );

        List<String> activeStatuses = new ArrayList<String>();
        activeStatuses.add( "QUEUED" );
        activeStatuses.add( "RETRYING" );
        activeStatuses.add( "RUNNING" );
        cells.add( new ActionHasCell( Constants.INSTANCE.Cancel(), activeStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to cancel this Job?" ) ) {
                    presenter.cancelRequest( job.getJobId() );
                }
            }
        } ) );

        List<String> requeueStatuses = new ArrayList<String>();
        requeueStatuses.add( "ERROR" );
        requeueStatuses.add( "RUNNING" );
        cells.add( new ActionHasCell( Constants.INSTANCE.Requeue(), requeueStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to requeue this Job?" ) ) {
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

        List<String> statuses;
        presenter.setAddingDefaultFilters( true );
        statuses = new ArrayList<String>();

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_0", Constants.INSTANCE.All(), "Filter " + Constants.INSTANCE.All(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "QUEUED" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_1", Constants.INSTANCE.Queued(), "Filter " + Constants.INSTANCE.Queued(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "RUNNING" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_2", Constants.INSTANCE.Running(), "Filter " + Constants.INSTANCE.Running(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "RETRYING" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_3", Constants.INSTANCE.Retrying(), "Filter " + Constants.INSTANCE.Retrying(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "ERROR" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_4", Constants.INSTANCE.Error(), "Filter " + Constants.INSTANCE.Error(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "DONE" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_5", Constants.INSTANCE.Completed(), "Filter " + Constants.INSTANCE.Completed(), statuses );

        statuses = new ArrayList<String>();
        statuses.add( "CANCELLED" );

        initTabFilter( preferences, REQUEST_LIST_PREFIX + "_6", Constants.INSTANCE.Cancelled(), "Filter " + Constants.INSTANCE.Cancelled(), statuses );

        filterPagedTable.addAddTableButton( createTabButton );
        presenter.setAddingDefaultFilters( false );
        getMultiGridPreferencesStore().setSelectedGrid( REQUEST_LIST_PREFIX + "_0" );
        filterPagedTable.setSelectedTab();
        applyFilterOnPresenter( REQUEST_LIST_PREFIX + "_0" );

    }

    private void initTabFilter( GridGlobalPreferences preferences,
                                final String key,
                                String tabName,
                                String tabDesc,
                                List<String> statuses ) {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();
        if ( statuses != null && statuses.size() > 0 ) {
            builder.dataset( REQUEST_LIST_DATASET_ID );
            List<Comparable> names = new ArrayList<Comparable>();

            for ( String s : statuses ) {
                names.add( s );
            }
            builder.filter( equalsTo( COLUMN_STATUS, names ) );
        }
        builder.dataset( REQUEST_LIST_DATASET_ID );
        builder.setColumn( COLUMN_ID, "id" );
        builder.setColumn( COLUMN_TIMESTAMP, "time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_STATUS, "status" );
        builder.setColumn( COLUMN_COMMANDNAME, "commandName", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_MESSAGE, "status" );
        builder.setColumn( COLUMN_BUSINESSKEY, "key" );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
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

    public void applyFilterOnPresenter( HashMap<String, Object> params ) {

        String tableSettingsJSON = (String) params.get( FILTER_TABLE_SETTINGS );
        FilterSettings tableSettings = dataSetEditorManager.getStrToTableSettings( tableSettingsJSON );
        presenter.filterGrid( tableSettings );

    }

    public void applyFilterOnPresenter( String key ) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }

    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( REQUEST_LIST_DATASET_ID );
        builder.setColumn( COLUMN_ID, "id" );
        builder.setColumn( COLUMN_TIMESTAMP, "time", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_STATUS, "status" );
        builder.setColumn( COLUMN_COMMANDNAME, "commandName", "MMM dd E, yyyy" );
        builder.setColumn( COLUMN_MESSAGE, "status" );
        builder.setColumn( COLUMN_BUSINESSKEY, "key" );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_TIMESTAMP, DESCENDING );
        builder.tableWidth( 1000 );

        return builder.buildSettings();

    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue( int newValue ) {
        filterPagedTable.saveNewRefreshInterval( newValue );
    }


}
