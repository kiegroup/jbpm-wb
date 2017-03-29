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
package org.jbpm.workbench.pr.client.editors.instance.list.variables.dash;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jbpm.workbench.common.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.base.AbstractMultiGridView;
import org.jbpm.workbench.common.client.util.DateUtils;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsBuilderHelper;
import org.jbpm.workbench.df.client.list.base.DataSetEditorManager;
import org.jbpm.workbench.pr.client.editors.instance.list.ProcessInstanceSummaryActionCell;
import org.jbpm.workbench.pr.client.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
public class DataSetProcessInstanceWithVariablesListViewImpl extends AbstractMultiGridView<ProcessInstanceSummary, DataSetProcessInstanceWithVariablesListPresenter>
        implements DataSetProcessInstanceWithVariablesListPresenter.DataSetProcessInstanceWithVariablesListView {

    interface Binder
            extends
            UiBinder<Widget, DataSetProcessInstanceWithVariablesListViewImpl> {

    }

    private List<ProcessInstanceSummary> selectedProcessInstances = new ArrayList<ProcessInstanceSummary>();

    private final Constants constants = Constants.INSTANCE;

    @Inject
    protected DataSetEditorManager dataSetEditorManager;

    private Column actionsColumn;

    private AnchorListItem bulkAbortNavLink;
    private AnchorListItem bulkSignalNavLink;

    private void controlBulkOperations() {
        if ( selectedProcessInstances != null && selectedProcessInstances.size() > 0 ) {
            bulkAbortNavLink.setEnabled( true );
            bulkSignalNavLink.setEnabled( true );
        } else {
            bulkAbortNavLink.setEnabled( false );
            bulkSignalNavLink.setEnabled( false );
        }
    }


    @Override
    public void init( final DataSetProcessInstanceWithVariablesListPresenter presenter ) {
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
        initColumns.add(COL_ID_ACTIONS);

        final Button button = new Button();
        button.setIcon( IconType.PLUS );
        button.setSize( ButtonSize.SMALL );

        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                final String key = getValidKeyForAdditionalListGrid( PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_" );

                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {

                        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

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
                dataSetEditorManager.showTableSettingsEditor( filterPagedTable, constants.New_Process_InstanceList(), tableSettings, addNewGrid );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX, initColumns, bannedColumns ), button );
    }

    @Override
    public void initSelectionModel() {
        final ExtendedPagedTable extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption(constants.No_Process_Instances_Found());
        extendedPagedTable.getRightActionsToolbar().clear();
        initExtraButtons(extendedPagedTable);
        initBulkActions(extendedPagedTable);
        selectionModel = new NoSelectionModel<ProcessInstanceSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                boolean close = false;
                if (selectedRow == -1) {
                    extendedPagedTable.setRowStyles(selectedStyles);
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();

                } else if (extendedPagedTable.getKeyboardSelectedRow() != selectedRow) {
                    extendedPagedTable.setRowStyles(selectedStyles);
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                presenter.selectProcessInstance(selectedItem, close);
            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager( new DefaultSelectionEventManager.EventTranslator<ProcessInstanceSummary>() {

                    @Override
                    public boolean clearCurrentSelection( CellPreviewEvent<ProcessInstanceSummary> event ) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent( CellPreviewEvent<ProcessInstanceSummary> event ) {
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
                                    getListGrid().redraw();
                                    controlBulkOperations();
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

        extendedPagedTable.setSelectionModel(selectionModel, noActionColumnManager);
        extendedPagedTable.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns( ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {

        ColumnMeta checkColumMeta = initChecksColumn();
        Column processInstanceIdColumn = initProcessInstanceIdColumn();
        Column processNameColumn = initProcessNameColumn();
        Column processInitiatorColumn = initInitiatorColumn();
        Column processVersionColumn = initProcessVersionColumn();
        Column processStateColumn = initProcessStateColumn();
        Column startTimeColumn = initStartDateColumn();
        Column lastModificationColumn = initLastModificationDateColumn();
        Column descriptionColumn = initDescriptionColumn();
        Column correlationKeyColumn = initCorrelationKeyColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();
        columnMetas.add( checkColumMeta );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processInstanceIdColumn, constants.Id() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processNameColumn, constants.Name() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( descriptionColumn, constants.Process_Instance_Description() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processInitiatorColumn, constants.Initiator() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processVersionColumn, constants.Version() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processStateColumn, constants.State() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( startTimeColumn, constants.Start_Date() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( lastModificationColumn, constants.Last_Modification_Date() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( correlationKeyColumn, constants.Correlation_Key() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( actionsColumn, constants.Actions() ) );

        List<GridColumnPreference> columPreferenceList = extendedPagedTable.getGridPreferencesStore().getColumnPreferences();

        for ( GridColumnPreference colPref : columPreferenceList ) {
            if ( !isColumnAdded( columnMetas, colPref.getName() ) ) {
                Column genericColumn = initGenericColumn( colPref.getName() );
                genericColumn.setSortable( false );
                columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( genericColumn, colPref.getName(), true, true ) );
            }
        }
        extendedPagedTable.addColumns( columnMetas );
        extendedPagedTable.setColumnWidth(checkColumMeta.getColumn(), 37, Style.Unit.PX );
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

    public void initExtraButtons( final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
    }

    private void initBulkActions( final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        bulkAbortNavLink = new AnchorListItem( constants.Bulk_Abort() );
        bulkSignalNavLink = new AnchorListItem( constants.Bulk_Signal() );

        final ButtonGroup bulkActions = new ButtonGroup() {{
            add( new Button( constants.Bulk_Actions() ) {{
                setDataToggle( Toggle.DROPDOWN );
                getElement().getStyle().setMarginRight( 5, Style.Unit.PX );
            }} );
            add( new DropDownMenu() {{
                addStyleName( Styles.DROPDOWN_MENU + "-right" );
                getElement().getStyle().setMarginRight( 5, Style.Unit.PX );
                add( bulkAbortNavLink );
                add( bulkSignalNavLink );
            }} );
        }};
        bulkAbortNavLink.setIcon( IconType.BAN );
        bulkAbortNavLink.setIconFixedWidth( true );
        bulkAbortNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if( Window.confirm( constants.Abort_Process_Instances() ) ) {
                    presenter.bulkAbort(selectedProcessInstances);
                    selectedProcessInstances.clear();
                    extendedPagedTable.redraw();
                }
            }
        } );

        bulkSignalNavLink.setIcon( IconType.BELL );
        bulkSignalNavLink.setIconFixedWidth( true );
        bulkSignalNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.bulkSignal( selectedProcessInstances );
                selectedProcessInstances.clear();
                extendedPagedTable.redraw();
            }
        } );

        extendedPagedTable.getRightActionsToolbar().add(bulkActions);

        controlBulkOperations();
    }

    private Column initProcessInstanceIdColumn() {
        // Process Instance Id.
        Column<ProcessInstanceSummary, String> processInstanceIdColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return String.valueOf( object.getProcessInstanceId() );
            }
        };
        processInstanceIdColumn.setSortable( true );
        processInstanceIdColumn.setDataStoreName(COLUMN_PROCESS_INSTANCE_ID);

        return processInstanceIdColumn;
    }

    private Column initProcessNameColumn() {
        // Process Name.
        Column<ProcessInstanceSummary, String> processNameColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessName();
            }
        };
        processNameColumn.setSortable( true );
        processNameColumn.setDataStoreName(COLUMN_PROCESS_NAME);

        return processNameColumn;
    }

    private Column initInitiatorColumn() {
        // Initiator
        Column<ProcessInstanceSummary, String> processInitiatorColumn = new Column<ProcessInstanceSummary, String>(
                new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getInitiator();
            }
        };
        processInitiatorColumn.setSortable( true );
        processInitiatorColumn.setDataStoreName( COLUMN_IDENTITY );

        return processInitiatorColumn;
    }

    private Column initProcessVersionColumn() {
        // Process Version.
        Column<ProcessInstanceSummary, String> processVersionColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessVersion();
            }
        };
        processVersionColumn.setSortable( true );
        processVersionColumn.setDataStoreName(COLUMN_PROCESS_VERSION);

        return processVersionColumn;
    }

    private Column initProcessStateColumn() {
        // Process State
        Column<ProcessInstanceSummary, String> processStateColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                String statusStr = constants.Unknown();
                switch ( object.getState() ) {
                    case ProcessInstance.STATE_ACTIVE:
                        statusStr = constants.Active();
                        break;
                    case ProcessInstance.STATE_ABORTED:
                        statusStr = constants.Aborted();
                        break;
                    case ProcessInstance.STATE_COMPLETED:
                        statusStr = constants.Completed();
                        break;
                    case ProcessInstance.STATE_PENDING:
                        statusStr = constants.Pending();
                        break;
                    case ProcessInstance.STATE_SUSPENDED:
                        statusStr = constants.Suspended();
                        break;

                    default:
                        break;
                }

                return statusStr;
            }
        };
        processStateColumn.setSortable( true );
        processStateColumn.setDataStoreName( COLUMN_STATUS );

        return processStateColumn;
    }

    private Column initStartDateColumn() {
        // Start time
        Column<ProcessInstanceSummary, String> startTimeColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return DateUtils.getDateTimeStr(object.getStartTime());
            }
        };
        startTimeColumn.setSortable( true );
        startTimeColumn.setDataStoreName( COLUMN_START );

        return startTimeColumn;
    }
    
    private Column initLastModificationDateColumn() {
        // Modification time
        Column<ProcessInstanceSummary, String> col = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                Date colDate = object.getLastModificationDate();
                if(colDate == null){
                    colDate = object.getEndTime();
                }
                return DateUtils.getDateTimeStr(colDate);
            }
        };
        col.setSortable( true );
        col.setDataStoreName( COLUMN_LAST_MODIFICATION_DATE );

        return col;
    }

    private Column initActionsColumn() {
        List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();

        cells.add(new ProcessInstanceSummaryActionCell(constants.Signal(), new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute( final ProcessInstanceSummary processInstance ) {
                presenter.signalProcessInstance(processInstance);
            }
        } ) );

        cells.add(new ProcessInstanceSummaryActionCell(constants.Abort(), new Delegate<ProcessInstanceSummary>() {
            @Override
            public void execute( ProcessInstanceSummary processInstance ) {
                if ( Window.confirm( constants.Abort_Process_Instance() ) ) {
                    presenter.abortProcessInstance( processInstance.getDeploymentId(), processInstance.getProcessInstanceId() );
                }
            }
        } ) );

        CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>( cells );
        Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
                cell ) {
            @Override
            public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
                return object;
            }
        };
        actionsColumn.setDataStoreName( COL_ID_ACTIONS );
        return actionsColumn;

    }

    private ColumnMeta<ProcessInstanceSummary> initChecksColumn() {
        CheckboxCell checkboxCell = new CheckboxCell(true,false);
        Column<ProcessInstanceSummary, Boolean> checkColumn = new Column<ProcessInstanceSummary, Boolean>(checkboxCell ) {
            @Override
            public Boolean getValue( ProcessInstanceSummary object ) {
                // Get the value from the selection model.
                return selectedProcessInstances.contains( object );
            }
        };

        Header<Boolean> selectPageHeader = new Header<Boolean>(checkboxCell) {
            @Override
            public Boolean getValue() {
                List<ProcessInstanceSummary> displayedInstances = presenter.getDisplayedProcessInstances();
                return displayedInstances.size() > 0
                        && selectedProcessInstances.size() == presenter.getDisplayedProcessInstances().size();
            }
        };
        selectPageHeader.setUpdater(new ValueUpdater<Boolean>() {
            @Override
            public void update(Boolean value) {
                selectedProcessInstances.clear();
                if (value) {
                    selectedProcessInstances.addAll(presenter.getDisplayedProcessInstances());
                }
                getListGrid().redraw();
                controlBulkOperations();
            }
        });

        checkColumn.setSortable(false);
        checkColumn.setDataStoreName( COL_ID_SELECT );
        ColumnMeta<ProcessInstanceSummary> checkColMeta = new ColumnMeta<ProcessInstanceSummary>( checkColumn, "");
        checkColMeta.setHeader(selectPageHeader);
        return checkColMeta;
    }

    private Column initDescriptionColumn() {
        Column<ProcessInstanceSummary, String> descriptionColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessInstanceDescription();
            }
        };
        descriptionColumn.setSortable( true );
        descriptionColumn.setDataStoreName(COLUMN_PROCESS_INSTANCE_DESCRIPTION);
        return descriptionColumn;
    }

    private Column initCorrelationKeyColumn() {
        Column<ProcessInstanceSummary, String> col = new Column<ProcessInstanceSummary, String>( new TextCell() ) {
            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getCorrelationKey();
            }
        };
        col.setSortable( true );
        col.setDataStoreName(COLUMN_CORRELATION_KEY);
        return col;
    }

    public void initDefaultFilters( GridGlobalPreferences preferences,
                                    Button createTabButton ) {


        List<Integer> states = new ArrayList<Integer>();
        presenter.setAddingDefaultFilters( true );
        //Filter status Active
        states.add( Integer.valueOf( ProcessInstance.STATE_ACTIVE ) );
        initGenericTabFilter( preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0", constants.Active(), constants.FilterActive(), states );

        //Filter status completed
        states = new ArrayList<Integer>();
        states.add( Integer.valueOf( ProcessInstance.STATE_COMPLETED ) );
        initGenericTabFilter( preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1", constants.Completed(), constants.FilterCompleted(), states );

        //Filter status completed
        states = new ArrayList<Integer>();
        states.add( Integer.valueOf( ProcessInstance.STATE_ABORTED ) );
        initGenericTabFilter( preferences, PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2", constants.Aborted(), constants.FilterAborted(), states );

        filterPagedTable.addAddTableButton( createTabButton );
        selectFirstTabAndEnableQueries( PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0" );

    }

    private void initGenericTabFilter( GridGlobalPreferences preferences,
                                       final String key,
                                       String tabName,
                                       String tabDesc,
                                       List<Integer> states ) {

        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( PROCESS_INSTANCE_DATASET );
        List<Comparable> names = new ArrayList<Comparable>();

        for ( Integer s : states ) {
            names.add( s );
        }
        builder.filter( equalsTo( COLUMN_STATUS, names ) );

//        builder.setColumn(COLUMN_PROCESS_INSTANCE_ID, constants.Process_Instance_ID() );
//        builder.setColumn(COLUMN_PROCESS_ID, constants.Process_Definition_Id() );
//        builder.setColumn(COLUMN_START, constants.Start(), DateUtils.getDateTimeFormatMask());
//        builder.setColumn(COLUMN_END, constants.End(), DateUtils.getDateTimeFormatMask());
//        builder.setColumn(COLUMN_STATUS, constants.Status());
//        builder.setColumn(COLUMN_PARENT_PROCESS_INSTANCE_ID, constants.ParentProcessInstanceId() );
//        builder.setColumn(COLUMN_OUTCOME, constants.Outcome());
//        builder.setColumn(COLUMN_DURATION, constants.Duration());
//        builder.setColumn(COLUMN_IDENTITY, constants.Identity());
//        builder.setColumn(COLUMN_PROCESS_VERSION, constants.Version() );
//        builder.setColumn(COLUMN_PROCESS_NAME, constants.Name() );
//        builder.setColumn(COLUMN_CORRELATION_KEY, constants.Correlation_Key() );
//        builder.setColumn(COLUMN_EXTERNAL_ID, constants.ExternalId() );
//        builder.setColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION, constants.Process_Instance_Description() );

        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_START, DESCENDING );

        FilterSettings tableSettings = builder.buildSettings();
        tableSettings.setKey( key );
        tableSettings.setTableName( tabName );
        tableSettings.setTableDescription( tabDesc );

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( FILTER_TABLE_SETTINGS, dataSetEditorManager.getTableSettingsToStr( tableSettings ) );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tableSettings.getTableName() );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tableSettings.getTableDescription() );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns() ), key );
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
        presenter.filterGrid(tableSettings);

    }

    @Override
    public FilterSettings getVariablesTableSettings( String processName ) {
        String tableSettingsJSON = "{\n"
                + "    \"type\": \"TABLE\",\n"
                + "    \"filter\": {\n"
                + "        \"enabled\": \"true\",\n"
                + "        \"selfapply\": \"true\",\n"
                + "        \"notification\": \"true\",\n"
                + "        \"listening\": \"true\"\n"
                + "    },\n"
                + "    \"table\": {\n"
                + "        \"sort\": {\n"
                + "            \"enabled\": \"true\",\n"
                + "            \"columnId\": \"" + PROCESS_INSTANCE_ID + "\",\n"
                + "            \"order\": \"ASCENDING\"\n"
                + "        }\n"
                + "    },\n"
                + "    \"dataSetLookup\": {\n"
                + "        \"dataSetUuid\": \"jbpmProcessInstancesWithVariables\",\n"
                + "        \"rowCount\": \"-1\",\n"
                + "        \"rowOffset\": \"0\",\n";
        if ( processName != null ) {
            tableSettingsJSON += "        \"filterOps\":[{\"columnId\":\"" + PROCESS_NAME + "\", \"functionType\":\"EQUALS_TO\", \"terms\":[\"" + processName + "\"]}],";
        }
        tableSettingsJSON += "        \"groupOps\": [\n"
                + "            {\n"
                + "                \"groupFunctions\": [\n"
                + "                    {\n"
                + "                        \"sourceId\": \"" + PROCESS_INSTANCE_ID + "\",\n"
                + "                        \"columnId\": \"" + PROCESS_INSTANCE_ID + "\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"" + PROCESS_NAME + "\",\n"
                + "                        \"columnId\": \"" + PROCESS_NAME + "\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"" + VARIABLE_ID + "\",\n"
                + "                        \"columnId\": \"" + VARIABLE_ID + "\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"" + VARIABLE_NAME + "\",\n"
                + "                        \"columnId\": \"" + VARIABLE_NAME + "\"\n"
                + "                    },\n"
                + "                    {\n"
                + "                        \"sourceId\": \"" + VARIABLE_VALUE + "\",\n"
                + "                        \"columnId\": \"" + VARIABLE_VALUE +  "\"\n"
                + "                    }\n"
                + "                ],\n"
                + "                \"join\": \"false\"\n"
                + "            }\n"
                + "        ]\n"
                + "    },\n"
                + "    \"columns\": [\n"
                + "        {\n"
                + "            \"id\": \"" + PROCESS_INSTANCE_ID + "\",\n"
                + "            \"name\": \"processInstanceId\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \""  + PROCESS_NAME + "\",\n"
                + "            \"name\": \"processName\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"" + VARIABLE_ID + "\",\n"
                + "            \"name\": \"variableID\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"" + VARIABLE_NAME + "\",\n"
                + "            \"name\": \"variableName\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"" + VARIABLE_VALUE + "\",\n"
                + "            \"name\": \"variableValue\"\n"
                + "        }\n"
                + "    ],\n"
                + "    \"tableName\": \"Filtered\",\n"
                + "    \"tableDescription\": \"Filtered Desc\",\n"
                + "    \"tableEditEnabled\": \"false\"\n"
                + "}";

        return dataSetEditorManager.getStrToTableSettings( tableSettingsJSON );
    }

    public void applyFilterOnPresenter( String key ) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }

    /*-------------------------------------------------*/
    /*---              DashBuilder                   --*/
    /*-------------------------------------------------*/
    public FilterSettings createTableSettingsPrototype() {
        FilterSettingsBuilderHelper builder = FilterSettingsBuilderHelper.init();
        builder.initBuilder();

        builder.dataset( PROCESS_INSTANCE_DATASET );
        builder.filterOn( true, true, true );
        builder.tableOrderEnabled( true );
        builder.tableOrderDefault( COLUMN_START, DESCENDING );
        builder.tableWidth( 1000 );

        return builder.buildSettings();

    }

    public int getRefreshValue() {
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue( int newValue ) {
        filterPagedTable.saveNewRefreshInterval( newValue );
    }

    public void resetDefaultFilterTitleAndDescription(){

        HashMap<String, Object> tabSettingsValues =null;

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0");
        if(tabSettingsValues!=null){
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Active() );
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterActive() );
            filterPagedTable.saveTabSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_0",tabSettingsValues);
        }

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1");
        if(tabSettingsValues!=null){
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Completed() );
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterCompleted() );
            filterPagedTable.saveTabSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_1",tabSettingsValues);
        }

        tabSettingsValues = filterPagedTable.getMultiGridPreferencesStore().getGridSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2");
        if(tabSettingsValues!=null){
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, Constants.INSTANCE.Aborted() );
            tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, Constants.INSTANCE.FilterAborted() );
            filterPagedTable.saveTabSettings(PROCESS_INSTANCES_WITH_VARIABLES_INCLUDED_LIST_PREFIX + "_2",tabSettingsValues);
        }

    }

}
