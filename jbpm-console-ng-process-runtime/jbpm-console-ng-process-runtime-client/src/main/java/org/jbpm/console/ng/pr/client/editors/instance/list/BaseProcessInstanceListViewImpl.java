package org.jbpm.console.ng.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public abstract class BaseProcessInstanceListViewImpl extends AbstractMultiGridView<ProcessInstanceSummary, BaseProcessInstanceListPresenter>
        implements BaseProcessInstanceListPresenter.BaseProcessInstanceListView {

    protected Constants constants = GWT.create( Constants.class );

    public static final String BASIC_VIEW_MODE = "Basic Process Instance Details Multi";
    public static final String ADVANCED_VIEW_MODE = "Advanced Process Instance Details Multi";
    
    protected List<ProcessInstanceSummary> selectedProcessInstances = new ArrayList<ProcessInstanceSummary>();

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;

    @Inject
    private ContextualView contextualView;

    protected NavLink bulkAbortNavLink;

    protected Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn;

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    @Override
    public void init( final BaseProcessInstanceListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( constants.Select() );
        bannedColumns.add( constants.Id() );
        bannedColumns.add( constants.Name() );
        bannedColumns.add( constants.Process_Instance_Description() );
        bannedColumns.add( constants.Actions() );
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add( constants.Select() );
        initColumns.add( constants.Id() );
        initColumns.add( constants.Name() );
        initColumns.add( constants.Process_Instance_Description() );
        initColumns.add( constants.Version() );
        initColumns.add( constants.Actions() );
        initColumns.add( constants.Version() );
        final Button button = new Button();
        button.setText( "+" );
        button.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Command addNewGrid = new Command() {

                    @Override
                    public void execute() {
                        HashMap<String, Object> newTabFormValues = newTabFilterPopup.getFormValues();
                        final String key = getValidKeyForAdditionalListGrid( "ProcessInstancesGrid_" );
                        filterPagedTable.saveNewTabSettings( key, newTabFormValues );
                        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance( new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        presenter.addDataDisplay( extendedPagedTable );
                        extendedPagedTable.setDataProvider( presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, button, new Command() {

                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } );
                        applyFilterOnPresenter( newTabFormValues );

                    }
                };
                createFilterForm();
                newTabFilterPopup.show( addNewGrid, getMultiGridPreferencesStore() );

            }
        } );

        super.init( presenter, new GridGlobalPreferences( "ProcessInstancesGrid", initColumns, bannedColumns ), button );

    }

    public void initSelectionModel() {
        final ExtendedPagedTable extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption( constants.No_Process_Instances_Found() );
        extendedPagedTable.getLeftToolbar().clear();
        initExtraButtons( extendedPagedTable );
        initBulkActionsDropDown( extendedPagedTable );
        initBaseSelectionModel( extendedPagedTable );
    }

    private void initBulkActionsDropDown( final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        SplitDropdownButton bulkActions = new SplitDropdownButton();
        bulkActions.setText( constants.Bulk_Actions() );
        bulkAbortNavLink = new NavLink( constants.Bulk_Abort() );
        bulkAbortNavLink.setIcon( IconType.REMOVE_SIGN );
        bulkAbortNavLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.bulkAbort( selectedProcessInstances );
                selectedProcessInstances.clear();
                extendedPagedTable.redraw();
            }
        } );

        bulkActions.add( bulkAbortNavLink );
        initSpecificBulkActionsDropDown( extendedPagedTable, bulkActions );
        
        extendedPagedTable.getLeftToolbar().add( bulkActions );

        controlBulkOperations();
    }

    public void initExtraButtons( final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        Button newInstanceButton = new Button();
        newInstanceButton.setTitle( constants.New_Instance() );
        newInstanceButton.setIcon( IconType.PLUS_SIGN );
        newInstanceButton.setTitle( Constants.INSTANCE.New_Instance() );
        newInstanceButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                newProcessInstancePopup.show();
            }
        } );
        extendedPagedTable.getLeftToolbar().add( newInstanceButton );
    }

    protected abstract void controlBulkOperations();

    protected abstract void initSpecificBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable, final SplitDropdownButton bulkActions );

    private void initBaseSelectionModel( final ExtendedPagedTable additionalGrid ) {
        additionalGrid.setEmptyTableCaption( constants.No_Process_Instances_Found() );

        final ExtendedPagedTable extendedPagedTable = additionalGrid;
        selectionModel = new NoSelectionModel<ProcessInstanceSummary>();
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
                String placeIdentifier = BASIC_VIEW_MODE;
                if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
                    placeIdentifier = ADVANCED_VIEW_MODE;
                }
                PlaceStatus status = placeManager.getStatus( new DefaultPlaceRequest( placeIdentifier ) );

                if ( status == PlaceStatus.CLOSE ) {
                    placeManager.goTo( placeIdentifier );
                    processInstanceSelected.fire( new ProcessInstanceSelectionEvent( selectedItem.getDeploymentId(),
                            selectedItem.getProcessInstanceId(), selectedItem.getProcessId(),
                            selectedItem.getProcessName(), selectedItem.getState() ) );
                } else if ( status == PlaceStatus.OPEN && !close ) {
                    processInstanceSelected.fire( new ProcessInstanceSelectionEvent( selectedItem.getDeploymentId(),
                            selectedItem.getProcessInstanceId(), selectedItem.getProcessId(),
                            selectedItem.getProcessName(), selectedItem.getState() ) );
                } else if ( status == PlaceStatus.OPEN && close ) {
                    placeManager.closePlace( placeIdentifier );
                }

            }
        } );

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
                                    controlBulkOperations();
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                } );

        additionalGrid.setSelectionModel( selectionModel, noActionColumnManager );
        additionalGrid.setRowStyles( selectedStyles );
    }

    @Override
    public void initColumns( ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable ) {
        Column checkColumn = initChecksColumn();
        Column processInstanceIdColumn = initProcessInstanceIdColumn();
        Column processNameColumn = initProcessNameColumn();
        Column processInitiatorColumn = initInitiatorColumn();
        Column processVersionColumn = initProcessVersionColumn();
        Column processStateColumn = initProcessStateColumn();
        Column startTimeColumn = initStartDateColumn();
        Column descriptionColumn = initDescriptionColumn();
        actionsColumn = initSpecificActionsColumn();

        List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( checkColumn, constants.Select() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processInstanceIdColumn, constants.Id() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processNameColumn, constants.Name() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( descriptionColumn, constants.Process_Instance_Description() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processInitiatorColumn, constants.Initiator() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processVersionColumn, constants.Version() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( processStateColumn, constants.State() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( startTimeColumn, constants.Start_Date() ) );
        columnMetas.add( new ColumnMeta<ProcessInstanceSummary>( actionsColumn, constants.Actions() ) );
        extendedPagedTable.addColumns( columnMetas );
    }

    private Column initSpecificActionsColumn() {
        List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();
        cells.add( new AbortActionHasCell( constants.Abort(), new Delegate<ProcessInstanceSummary>() {

            @Override
            public void execute( ProcessInstanceSummary processInstance ) {
                if ( Window.confirm( "Are you sure that you want to abort the process instance?" ) ) {
                    presenter.abortProcessInstance( processInstance.getProcessInstanceId() );
                }
            }
        } ) );

        initSpecificCells( cells );

        CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>( cells );
        Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>( cell ) {

            @Override
            public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
                return object;
            }
        };
        return actionsColumn;
    }

    protected abstract void initSpecificCells( final List<HasCell<ProcessInstanceSummary, ?>> cells );

    private void createFilterForm() {
        HashMap<String, String> stateListBoxInfo = new HashMap<String, String>();

        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_ACTIVE ), Constants.INSTANCE.Active() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_COMPLETED ), Constants.INSTANCE.Completed() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_ABORTED ), Constants.INSTANCE.Aborted() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_PENDING ), Constants.INSTANCE.Pending() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_SUSPENDED ), Constants.INSTANCE.Suspended() );

        newTabFilterPopup.init();
        newTabFilterPopup.addListBoxToFilter( Constants.INSTANCE.State(), BaseProcessInstanceListPresenter.FILTER_STATE_PARAM_NAME, true, stateListBoxInfo );
        newTabFilterPopup.addTextBoxToFilter( Constants.INSTANCE.Process_Definitions(), BaseProcessInstanceListPresenter.FILTER_PROCESS_DEFINITION_PARAM_NAME );
        newTabFilterPopup.addTextBoxToFilter( Constants.INSTANCE.Initiator(), BaseProcessInstanceListPresenter.FILTER_INITIATOR_PARAM_NAME );
    }

    protected Column initProcessInstanceIdColumn() {
        // Process Instance Id.
        Column<ProcessInstanceSummary, String> processInstanceIdColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return String.valueOf( object.getProcessInstanceId() );
            }
        };
        processInstanceIdColumn.setSortable( true );
        processInstanceIdColumn.setDataStoreName( "log.processInstanceId" );

        return processInstanceIdColumn;
    }

    protected Column initProcessNameColumn() {
        // Process Name.
        Column<ProcessInstanceSummary, String> processNameColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessName();
            }
        };
        processNameColumn.setSortable( true );
        processNameColumn.setDataStoreName( "log.processName" );

        return processNameColumn;
    }

    protected Column initInitiatorColumn() {
        Column<ProcessInstanceSummary, String> processInitiatorColumn = new Column<ProcessInstanceSummary, String>(
                new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getInitiator();
            }
        };
        processInitiatorColumn.setSortable( true );
        processInitiatorColumn.setDataStoreName( "log.identity" );

        return processInitiatorColumn;
    }

    protected Column initProcessVersionColumn() {
        // Process Version.
        Column<ProcessInstanceSummary, String> processVersionColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessVersion();
            }
        };
        processVersionColumn.setSortable( true );
        processVersionColumn.setDataStoreName( "log.processVersion" );

        return processVersionColumn;
    }

    protected Column initProcessStateColumn() {
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
        processStateColumn.setDataStoreName( "log.status" );

        return processStateColumn;
    }

    protected Column initStartDateColumn() {
        // start time
        Column<ProcessInstanceSummary, String> startTimeColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                Date startTime = object.getStartTime();
                if ( startTime != null ) {
                    DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy HH:mm" );
                    return format.format( startTime );
                }
                return "";
            }
        };
        startTimeColumn.setSortable( true );
        startTimeColumn.setDataStoreName( "log.start" );

        return startTimeColumn;
    }

    protected Column initChecksColumn() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.
        Column<ProcessInstanceSummary, Boolean> checkColumn = new Column<ProcessInstanceSummary, Boolean>( new CheckboxCell(
                true, false ) ) {

            @Override
            public Boolean getValue( ProcessInstanceSummary object ) {
                // Get the value from the selection model.
                return selectedProcessInstances.contains( object );
            }
        };

        return checkColumn;
    }

    protected Column initDescriptionColumn() {
        // start time
        Column<ProcessInstanceSummary, String> descriptionColumn = new Column<ProcessInstanceSummary, String>( new TextCell() ) {

            @Override
            public String getValue( ProcessInstanceSummary object ) {
                return object.getProcessInstanceDescription();
            }
        };
        descriptionColumn.setSortable( true );
        descriptionColumn.setDataStoreName( "log.processInstanceDescription" );
        return descriptionColumn;
    }

    public void onProcessInstanceSelectionEvent( @Observes ProcessInstancesWithDetailsRequestEvent event ) {
        String placeIdentifier = BASIC_VIEW_MODE;
        if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
            placeIdentifier = ADVANCED_VIEW_MODE;
        }
        placeManager.goTo( placeIdentifier );
        processInstanceSelected.fire( new ProcessInstanceSelectionEvent( event.getDeploymentId(),
                event.getProcessInstanceId(), event.getProcessDefId(),
                event.getProcessDefName(), event.getProcessInstanceStatus() ) );
    }

    protected class AbortActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public AbortActionHasCell(String text,
                Delegate<ProcessInstanceSummary> delegate) {
            cell = new ActionCell<ProcessInstanceSummary>( text, delegate ) {

                @Override
                public void render( Cell.Context context,
                        ProcessInstanceSummary value,
                        SafeHtmlBuilder sb ) {
                    if ( value.getState() == ProcessInstance.STATE_ACTIVE ) {
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+constants.Abort()+"'>"+constants.Abort()+"</a>&nbsp;");
                        sb.append( mysb.toSafeHtml() );
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
            return object;
        }
    }

    public void formClosed( @Observes BeforeClosePlaceEvent closed ) {
        if ( "Signal Process Popup".equals( closed.getPlace().getIdentifier() ) ) {
            presenter.refreshGrid();
        }
    }

    public void initDefaultFilters( GridGlobalPreferences preferences, Button createTabButton ) {

        List<String> states = new ArrayList<String>();

        //Filter status Active
        states.add( String.valueOf( ProcessInstance.STATE_ACTIVE ) );
        initTabFilter( preferences, "ProcessInstancesGrid_0", Constants.INSTANCE.Active(), "Filter " + Constants.INSTANCE.Active(), states, "", "" );

        //Filter status completed
        states = new ArrayList<String>();
        states.add( String.valueOf( ProcessInstance.STATE_COMPLETED ) );
        initTabFilter( preferences, "ProcessInstancesGrid_1", Constants.INSTANCE.Completed(), "Filter " + Constants.INSTANCE.Completed(), states, "", "" );

        filterPagedTable.addAddTableButton( createTabButton );
        applyFilterOnPresenter( "ProcessInstancesGrid_1" );

    }

    private void initTabFilter( GridGlobalPreferences preferences, final String key, String tabName, String tabDesc, List<String> states,
            String processDefinition, String initiator ) {
        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, tabName );
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tabDesc );
        tabSettingsValues.put( BaseProcessInstanceListPresenter.FILTER_STATE_PARAM_NAME, states );
        tabSettingsValues.put( BaseProcessInstanceListPresenter.FILTER_PROCESS_DEFINITION_PARAM_NAME, processDefinition );
        tabSettingsValues.put( BaseProcessInstanceListPresenter.FILTER_INITIATOR_PARAM_NAME, initiator );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );

        final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable = createGridInstance(  new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns()), key );
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay( extendedPagedTable );
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
        List<String> states = (List<String>) params.get( BaseProcessInstanceListPresenter.FILTER_STATE_PARAM_NAME );
        ArrayList<Integer> statesInteger = new ArrayList<Integer>();
        for ( String state : states ) {
            statesInteger.add( Integer.parseInt( state ) );
        }
        presenter.filterGrid( statesInteger, (String) params.get( BaseProcessInstanceListPresenter.FILTER_PROCESS_DEFINITION_PARAM_NAME ),
                (String) params.get( BaseProcessInstanceListPresenter.FILTER_INITIATOR_PARAM_NAME ) );
    }

    public void applyFilterOnPresenter( String key ) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }
}
