package org.jbpm.console.ng.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.ext.widgets.common.client.tables.DataGridFilter;
import org.uberfire.ext.widgets.common.client.tables.popup.NewFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public abstract class BaseProcessInstanceListViewImpl extends AbstractListView<ProcessInstanceSummary, BaseProcessInstanceListPresenter>
        implements BaseProcessInstanceListPresenter.BaseProcessInstanceListView {

    protected Constants constants = GWT.create( Constants.class );

    protected ProcessRuntimeImages images = GWT.create( ProcessRuntimeImages.class );

    protected List<ProcessInstanceSummary> selectedProcessInstances = new ArrayList<ProcessInstanceSummary>();

    @Inject
    private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

    @Inject
    protected NewFilterPopup newFilterPopup;

    @Inject
    private ContextualView contextualView;

    protected NavLink bulkAbortNavLink;

    protected Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn;

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
        super.init( presenter, new GridGlobalPreferences( "ProcessInstancesGrid", initColumns, bannedColumns ) );
        initSelectionModel( listGrid );
    }

    public void initSelectionModel( final ExtendedPagedTable additionalGrid ) {
        initBulkActionsDropDown( additionalGrid );
        initBaseSelectionModel( additionalGrid );
    }

    private void initBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable ) {
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
                String placeIdentifier = "Basic Process Instance Details Multi";
                if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
                    placeIdentifier = "Advanced Process Instance Details Multi";
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
    public void initColumns() {
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
        listGrid.addColumns( columnMetas );
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

    @Override
    public void initFilters() {
        listGrid.setShowFilterSelector( true );

        listGrid.addFilter( new DataGridFilter<ProcessInstanceSummary>( "active", Constants.INSTANCE.Active(),
                new Command() {

                    @Override
                    public void execute() {
                        presenter.refreshActiveProcessList();
                    }
                } ) );

        listGrid.addFilter( new DataGridFilter<ProcessInstanceSummary>( "completed", Constants.INSTANCE.Completed(),
                new Command() {

                    @Override
                    public void execute() {
                        presenter.refreshCompletedProcessList();
                    }
                } ) );

        listGrid.addFilter( new DataGridFilter<ProcessInstanceSummary>( "aborted", Constants.INSTANCE.Aborted(),
                new Command() {

                    @Override
                    public void execute() {
                        presenter.refreshAbortedProcessList();
                    }
                } ) );

        listGrid.addFilter( new DataGridFilter<ProcessInstanceSummary>( "relatedToMe", constants.Related_To_Me(),
                new Command() {

                    @Override
                    public void execute() {
                        presenter.refreshRelatedToMeProcessList( identity.getIdentifier() );

                    }
                } ) );
        final HashMap storedCustomFilters = listGrid.getStoredCustomFilters();
        List<DataGridFilter> customFilters = new ArrayList<DataGridFilter>();
        if ( storedCustomFilters != null ) {
            Set customFilterKeys = storedCustomFilters.keySet();
            Iterator it = customFilterKeys.iterator();
            String customFilterName;
            DataGridFilter dataGridFilter;
            while ( it.hasNext() ) {
                customFilterName = (String) it.next();

                final HashMap filterValues = (HashMap) storedCustomFilters.get( customFilterName );
                dataGridFilter = new DataGridFilter<ProcessInstanceSummary>( customFilterName, customFilterName,
                        new Command() {

                            @Override
                            public void execute() {
                                List<String> states = (List) filterValues.get( BaseProcessInstanceListPresenter.FILTER_STATE_PARAM_NAME );
                                ArrayList<Integer> statesInteger = new ArrayList<Integer>();
                                for ( String state : states ) {
                                    statesInteger.add( Integer.parseInt( state ) );
                                }
                                presenter.filterGrid( statesInteger, (String) filterValues.get( BaseProcessInstanceListPresenter.FILTER_PROCESS_DEFINITION_PARAM_NAME ),
                                        (String) filterValues.get( BaseProcessInstanceListPresenter.FILTER_INITIATOR_PARAM_NAME ) );
                            }
                        } );
                listGrid.addFilter( dataGridFilter );
                customFilters.add( dataGridFilter );

            }
        }
        final Command refreshFilterDropDownCommand = new Command() {

            @Override
            public void execute() {
                listGrid.clearFilters();
                initFilters();
            }
        };
        listGrid.addFilter( new DataGridFilter<ProcessInstanceSummary>( "addFilter", "-- " + Constants.INSTANCE.FilterManagement() + " --",
                new Command() {

                    @Override
                    public void execute() {
                        Command addFilter = new Command() {

                            @Override
                            public void execute() {
                                final String newFilterName = (String) newFilterPopup.getFormValues().get( NewFilterPopup.FILTER_NAME_PARAM );
                                listGrid.storeNewCustomFilter( newFilterName, newFilterPopup.getFormValues() );
                                listGrid.clearFilters();
                                initFilters();
                            }
                        };
                        createFilterForm();
                        newFilterPopup.show( addFilter, refreshFilterDropDownCommand, listGrid.getGridPreferencesStore() );
                    }
                } ) );
        listGrid.refreshFilterDropdown();

    }

    private void createFilterForm() {
        HashMap<String, String> stateListBoxInfo = new HashMap<String, String>();

        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_ACTIVE ), Constants.INSTANCE.Active() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_COMPLETED ), Constants.INSTANCE.Completed() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_ABORTED ), Constants.INSTANCE.Aborted() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_PENDING ), Constants.INSTANCE.Pending() );
        stateListBoxInfo.put( String.valueOf( ProcessInstance.STATE_SUSPENDED ), Constants.INSTANCE.Suspended() );

        newFilterPopup.init();
        newFilterPopup.addListBoxToFilter( Constants.INSTANCE.State(), BaseProcessInstanceListPresenter.FILTER_STATE_PARAM_NAME, true, stateListBoxInfo );
        newFilterPopup.addTextBoxToFilter( Constants.INSTANCE.Process_Definitions(), BaseProcessInstanceListPresenter.FILTER_PROCESS_DEFINITION_PARAM_NAME );
        newFilterPopup.addTextBoxToFilter( Constants.INSTANCE.Initiator(), BaseProcessInstanceListPresenter.FILTER_INITIATOR_PARAM_NAME );
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
        String placeIdentifier = "Basic Process Instance Details Multi";
        if ( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ).equals( ContextualView.ADVANCED_MODE ) ) {
            placeIdentifier = "Advanced Process Instance Details Multi";
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
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.abortGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.Abort() + "' style='margin-right:5px;'>" );
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
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
            presenter.refreshActiveProcessList();
        }
    }
}
