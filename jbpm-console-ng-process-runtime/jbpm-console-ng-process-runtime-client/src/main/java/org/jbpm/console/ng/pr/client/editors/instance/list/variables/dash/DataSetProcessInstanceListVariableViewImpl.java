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
package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
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
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.jbpm.console.ng.pr.model.ProcessInstanceVariableSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.dashbuilder.dataset.sort.SortOrder.*;

@Dependent
public class DataSetProcessInstanceListVariableViewImpl extends AbstractMultiGridView<ProcessInstanceVariableSummary, DataSetProcessInstanceVariableListPresenter>
        implements DataSetProcessInstanceVariableListPresenter.DataSetProcessInstanceVariableListView {

    public static final String PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX = "DS_ProcessInstancesVariableGrid";

    public static final String PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    public static final String PROCESS_INSTANCE_ID = "pid";
    public static final String PROCESS_NAME = "pname";
    public static final String VARIABLE_ID = "varid";
    public static final String VARIABLE_NAME = "varname";
    public static final String VARIABLE_VALUE = "varvalue";

    private Constants constants = GWT.create( Constants.class );

    private List<ProcessInstanceVariableSummary> selectedProcessInstances = new ArrayList<ProcessInstanceVariableSummary>();

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private DataSetEditorManager dataSetEditorManager;

    private Column actionsColumn;

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    @Override
    public void init( final DataSetProcessInstanceVariableListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add(PROCESS_INSTANCE_ID);
        bannedColumns.add(PROCESS_NAME);

        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(PROCESS_INSTANCE_ID);
        initColumns.add(PROCESS_NAME);
        initColumns.add(VARIABLE_ID);
        initColumns.add(VARIABLE_NAME);
        initColumns.add(VARIABLE_VALUE);

        final Button button = new Button();
        button.setIcon( IconType.PLUS );
        button.setSize( ButtonSize.SMALL );

        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid( PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX + "_" );

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<ProcessInstanceVariableSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

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
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, Constants.INSTANCE.New_Process_InstanceList(), tableSettings, addNewGrid );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX, initColumns, bannedColumns ), button );

    }

    @Override
    public void initSelectionModel() {

        final ExtendedPagedTable extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption( constants.No_Process_Instances_Found() );
        extendedPagedTable.getRightActionsToolbar().clear();
        initExtraButtons( extendedPagedTable );
        initBulkActions( extendedPagedTable );
        selectionModel = new NoSelectionModel<ProcessInstanceVariableSummary>();
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

                selectedItem = selectionModel.getLastSelectedObject();

                PlaceStatus status = placeManager.getStatus( new DefaultPlaceRequest( "Process Instance Details Multi" ) );

            }
        } );

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<ProcessInstanceVariableSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<ProcessInstanceVariableSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<ProcessInstanceVariableSummary> event ) {
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
                                    if ( !selectedProcessInstances.contains( event.getValue() ) ) {
                                        selectedProcessInstances.add( event.getValue() );
                                        input.setChecked( true );
                                    } else {
                                        selectedProcessInstances.remove( event.getValue() );
                                        input.setChecked( false );
                                    }
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

        extendedPagedTable.setSelectionModel( selectionModel, noActionColumnManager );
        extendedPagedTable.setRowStyles( selectedStyles );
    }

    @Override
    public void initColumns( ExtendedPagedTable<ProcessInstanceVariableSummary> extendedPagedTable ) {

        Column processInstanceIdColumn = initProcessInstanceIdColumn();

        Column processNameColumn = initProcessNameColumn();
        Column variableIdColumn = initVariableIdColumn();
        Column variableNameColumn = initVariableNameColumn();
        Column variableValueColumn = initVariableValueColumn();

        List<ColumnMeta<ProcessInstanceVariableSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceVariableSummary>>();

        columnMetas.add( new ColumnMeta<ProcessInstanceVariableSummary>( processInstanceIdColumn, constants.Process_Instance_ID() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceVariableSummary>( processNameColumn, constants.Process_Instance_Name() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceVariableSummary>( variableIdColumn, constants.Id() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceVariableSummary>( variableNameColumn, constants.Variables_Name() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceVariableSummary>( variableValueColumn, constants.Variable_Value() ) );

        extendedPagedTable.addColumns( columnMetas );
    }

    public void initExtraButtons( final ExtendedPagedTable<ProcessInstanceVariableSummary> extendedPagedTable ) {

    }

    private void initBulkActions( final ExtendedPagedTable<ProcessInstanceVariableSummary> extendedPagedTable ) {

    }

    private Column initProcessInstanceIdColumn() {
        // Process Instance Id.
        Column<ProcessInstanceVariableSummary, String> processInstanceIdColumn = new Column<ProcessInstanceVariableSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceVariableSummary object ) {
                return String.valueOf( object.getProcessInstanceId() );
            }
        };
        processInstanceIdColumn.setSortable( true );
        processInstanceIdColumn.setDataStoreName( PROCESS_INSTANCE_ID );

        return processInstanceIdColumn;
    }

    private Column initVariableIdColumn() {
        // Process Instance Id.
        Column<ProcessInstanceVariableSummary, String> vairableIdColumn = new Column<ProcessInstanceVariableSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceVariableSummary object ) {
                return String.valueOf( object.getVariableId() );
            }
        };
        vairableIdColumn.setSortable( true );
        vairableIdColumn.setDataStoreName( VARIABLE_ID );

        return vairableIdColumn;
    }

    private Column initProcessNameColumn() {
        // Process Instance Id.
        Column<ProcessInstanceVariableSummary, String> processNameColumn = new Column<ProcessInstanceVariableSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceVariableSummary object ) {
                return String.valueOf( object.getProcessName() );
            }
        };
        processNameColumn.setSortable( true );
        processNameColumn.setDataStoreName( PROCESS_NAME );

        return processNameColumn;
    }

    private Column initVariableNameColumn() {
        // Process Name.
        Column<ProcessInstanceVariableSummary, String> variableNameColumn = new Column<ProcessInstanceVariableSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceVariableSummary object ) {
                return object.getVariableName();
            }
        };
        variableNameColumn.setSortable( true );
        variableNameColumn.setDataStoreName( VARIABLE_NAME );

        return variableNameColumn;
    }

    private Column initVariableValueColumn() {
        // Process Name.
        Column<ProcessInstanceVariableSummary, String> variableNameColumn = new Column<ProcessInstanceVariableSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceVariableSummary object ) {
                return object.getVariableValue();
            }
        };
        variableNameColumn.setSortable( true );
        variableNameColumn.setDataStoreName( VARIABLE_VALUE );

        return variableNameColumn;
    }

    public void initDefaultFilters( GridGlobalPreferences preferences,
                                    Button createTabButton ) {

        List<String> states = new ArrayList<String>();
        presenter.setAddingDefaultFilters( true );
        //Filter status Active
        states.add( String.valueOf( ProcessInstance.STATE_ACTIVE ) );
        initGenericTabFilter( preferences, PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX + "_0", Constants.INSTANCE.Active(), "Filter " + Constants.INSTANCE.Active(), states, "", "" );

        filterPagedTable.addAddTableButton( createTabButton );
        presenter.setAddingDefaultFilters( false );
        getMultiGridPreferencesStore().setSelectedGrid( PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX + "_0" );
        filterPagedTable.setSelectedTab();
        applyFilterOnPresenter( PROCESS_INSTANCES_WITH_VARIABLES_LIST_PREFIX + "_0" );

    }

    private void initGenericTabFilter( GridGlobalPreferences preferences,
                                       final String key,
                                       String tabName,
                                       String tabDesc,
                                       List<String> states,
                                       String processDefinition,
                                       String initiator ) {

        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( PROCESS_INSTANCE_WITH_VARIABLES_DATASET );

        builder.setColumn( PROCESS_INSTANCE_ID, "processInstanceId" );
        builder.setColumn( PROCESS_NAME, "processName" );
        builder.setColumn( VARIABLE_ID, "variableID" );
        builder.setColumn( VARIABLE_NAME, "variableName" );
        builder.setColumn( VARIABLE_VALUE, "variableValue" );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( VARIABLE_NAME, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<ProcessInstanceVariableSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
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

        builder.dataset( PROCESS_INSTANCE_WITH_VARIABLES_DATASET );

        builder.setColumn( PROCESS_INSTANCE_ID, "processInstanceId" );
        builder.setColumn( PROCESS_NAME, "processName" );
        builder.setColumn( VARIABLE_ID, "variableId" );
        builder.setColumn( VARIABLE_NAME, "variableName" );
        builder.setColumn( VARIABLE_VALUE, "variableValue" );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( VARIABLE_NAME, DESCENDING );
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