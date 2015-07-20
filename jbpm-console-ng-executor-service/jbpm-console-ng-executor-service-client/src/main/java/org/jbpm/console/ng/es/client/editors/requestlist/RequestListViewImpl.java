/*
 * Copyright 2012 JBoss Inc
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

import java.util.*;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import org.jbpm.console.ng.es.client.editors.jobdetails.JobDetailsPopup;
import org.jbpm.console.ng.es.client.editors.quicknewjob.QuickNewJobPopup;
import org.jbpm.console.ng.es.client.editors.servicesettings.JobServiceSettingsPopup;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;

import org.jbpm.console.ng.gc.client.list.base.AbstractMultiGridView;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
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

@Dependent
public class RequestListViewImpl extends AbstractMultiGridView<RequestSummary,RequestListPresenter>
        implements RequestListPresenter.RequestListView {
    private Constants constants = GWT.create( Constants.class );

    @Inject
    private Event<NotificationEvent> notification;

    private List<RequestSummary> selectedRequestSummary = new ArrayList<RequestSummary>();

    @Inject
    private JobDetailsPopup jobDetailsPopup;

    //@Inject
    //private QuickNewJobPopup quickNewJobPopup;

    @Inject
    private NewTabFilterPopup newTabFilterPopup;



    //@Inject
    //private JobServiceSettingsPopup jobServiceSettingsPopup;

    @Override
    public void init(final RequestListPresenter presenter ) {
        final List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.Id());
        bannedColumns.add(constants.Type());
        final List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Id());
        initColumns.add(constants.Type());
        initColumns.add(constants.Actions());
        final Button button = new Button();
        button.setText( "+" );
        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                Command addNewGrid = new Command() {
                    @Override
                    public void execute() {
                        HashMap<String,Object> newTabFormValues = newTabFilterPopup.getFormValues();

                        final String key = getValidKeyForAdditionalListGrid("RequestListGrid_");

                        filterPagedTable.saveNewTabSettings( key, newTabFormValues );
                        final ExtendedPagedTable<RequestSummary> extendedPagedTable = createGridInstance(  new GridGlobalPreferences( key, initColumns, bannedColumns ), key );

                        presenter.addDataDisplay( extendedPagedTable );
                        extendedPagedTable.setDataProvider(presenter.getDataProvider() );

                        filterPagedTable.createNewTab( extendedPagedTable, key, button,new Command() {
                            @Override
                            public void execute() {
                                currentListGrid = extendedPagedTable;
                                applyFilterOnPresenter( key );
                            }
                        } ) ;
                        applyFilterOnPresenter( newTabFormValues );


                    }
                };
                createFilterForm();
                newTabFilterPopup.show( addNewGrid, getMultiGridPreferencesStore() );

            }
        } );

        super.init(presenter, new GridGlobalPreferences("RequestListGrid", initColumns, bannedColumns),button);
    }

    public void requestCreated( @Observes RequestChangedEvent event ) {
        presenter.refreshRequests(null);
    }


    @Override
    public void initColumns(ExtendedPagedTable extendedPagedTable  ) {

        initJobIdColumn(extendedPagedTable);
        initJobTypeColumn(extendedPagedTable);
        initStatusColumn(extendedPagedTable);
        initDueDateColumn(extendedPagedTable);
        actionsColumn = initActionsColumn();
        extendedPagedTable.addColumn( actionsColumn, constants.Actions() );
    }

    private void createFilterForm(){
        HashMap<String,String> statesListBoxInfo = new HashMap<String, String>(  );

        statesListBoxInfo.put( String.valueOf( "QUEUED" ), Constants.INSTANCE.Queued() );
        statesListBoxInfo.put( String.valueOf( "RUNNING" ), Constants.INSTANCE.Running() );
        statesListBoxInfo.put( String.valueOf( "RETRYING" ), Constants.INSTANCE.Retrying() );
        statesListBoxInfo.put( String.valueOf( "ERROR" ), Constants.INSTANCE.Error() );
        statesListBoxInfo.put( String.valueOf( "DONE" ), Constants.INSTANCE.Completed() );
        statesListBoxInfo.put( String.valueOf( "CANCELLED" ), Constants.INSTANCE.Cancelled() );

        newTabFilterPopup.init();
        newTabFilterPopup.addListBoxToFilter( Constants.INSTANCE.Status(), "states",true,statesListBoxInfo );

    }

    public void initSelectionModel(){
        final ExtendedPagedTable<RequestSummary> extendedPagedTable = getListGrid();
        extendedPagedTable.setEmptyTableCaption( constants.No_Jobs_Found() );

        initLeftToolbarActions( extendedPagedTable );

        selectionModel = new NoSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                boolean close = false;
                if (selectedRow == -1) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();

                } else if (extendedPagedTable.getKeyboardSelectedRow() != selectedRow) {
                    extendedPagedTable.setRowStyles( selectedStyles );
                    selectedRow = extendedPagedTable.getKeyboardSelectedRow();
                    extendedPagedTable.redraw();
                } else {
                    close = true;
                }
            }
        });
        initNoActionColumnManager(extendedPagedTable);

        extendedPagedTable.setSelectionModel( selectionModel, noActionColumnManager );
        extendedPagedTable.setRowStyles( selectedStyles );
    }

    private void initNoActionColumnManager( final ExtendedPagedTable extendedPagedTable){
        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<RequestSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<RequestSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<RequestSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct column.
                            if (extendedPagedTable.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                            //Extension for checkboxes
                            Element target = nativeEvent.getEventTarget().cast();
                            if ("input".equals(target.getTagName().toLowerCase())) {
                                final InputElement input = target.cast();
                                if ("checkbox".equals(input.getType().toLowerCase())) {
                                    // Synchronize the checkbox with the current selection state.
                                    if (!selectedRequestSummary.contains(event.getValue())) {
                                        selectedRequestSummary.add(event.getValue());
                                        input.setChecked(true);
                                    } else {
                                        selectedRequestSummary.remove(event.getValue());
                                        input.setChecked(false);
                                    }
                                    return DefaultSelectionEventManager.SelectAction.IGNORE;
                                }
                            }
                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                });

    }

    private void initLeftToolbarActions(ExtendedPagedTable extendedPagedTable) {
 /*       SplitDropdownButton actions = new SplitDropdownButton();
        actions.setText( constants.Actions() );
        NavLink newJobNavLink = new NavLink(constants.New_Job());
        newJobNavLink.setIcon(IconType.PLUS_SIGN);
        newJobNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                quickNewJobPopup.show();
            }
        } );

        NavLink settingsNavLink = new NavLink(constants.Settings());
        settingsNavLink.setIcon( IconType.COG );
        settingsNavLink.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                jobServiceSettingsPopup.show();
            }
        } );

        actions.add( newJobNavLink );
        actions.add( settingsNavLink );
        extendedPagedTable.getRightActionsToolbar().clear();
        extendedPagedTable.getRightActionsToolbar().add( actions );
  */
    }



    private void initJobIdColumn(ExtendedPagedTable extendedPagedTable){
        // Id
        Column<RequestSummary, Number> taskIdColumn = new Column<RequestSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( RequestSummary object ) {
                return object.getJobId();
            }
        };
        taskIdColumn.setSortable( true );
        extendedPagedTable.addColumn(taskIdColumn, constants.Id());
        taskIdColumn.setDataStoreName( "r.id" );
    }

    private void initJobTypeColumn(ExtendedPagedTable extendedPagedTable){
        // Name
        Column<RequestSummary, String> jobTypeColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getCommandName();
            }
        };
        jobTypeColumn.setSortable( true );
        extendedPagedTable.addColumn(jobTypeColumn, constants.Type());
        jobTypeColumn.setDataStoreName( "r.commandName" );
    }

    private void initStatusColumn(ExtendedPagedTable extendedPagedTable){
        // Status
        Column<RequestSummary, String> statusColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable( true );
        extendedPagedTable.addColumn(statusColumn, constants.Status());
        statusColumn.setDataStoreName( "r.status" );
    }

    private void initDueDateColumn(ExtendedPagedTable extendedPagedTable){
        // Time
        Column<RequestSummary, String> taskNameColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getTime().toString();
            }
        };
        taskNameColumn.setSortable( true );
        extendedPagedTable.addColumn(taskNameColumn, constants.Due_On());
        taskNameColumn.setDataStoreName( "r.time" );
    }

    private Column<RequestSummary, RequestSummary> initActionsColumn(){
        List<HasCell<RequestSummary, ?>> cells = new LinkedList<HasCell<RequestSummary, ?>>();
        List<String> allStatuses = new ArrayList<String>();
        allStatuses.add("QUEUED");
        allStatuses.add("DONE");
        allStatuses.add("CANCELLED");
        allStatuses.add("ERROR");
        allStatuses.add("RETRYING");
        allStatuses.add("RUNNING");
        cells.add( new ActionHasCell(Constants.INSTANCE.Details(), allStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                jobDetailsPopup.show(String.valueOf( job.getJobId() ));
            }
        } ) );

        List<String> activeStatuses = new ArrayList<String>();
        activeStatuses.add("QUEUED");
        activeStatuses.add("RETRYING");
        activeStatuses.add("RUNNING");
        cells.add( new ActionHasCell( Constants.INSTANCE.Cancel(), activeStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to cancel this Job?" ) ) {
                    presenter.cancelRequest( job.getJobId() );
                }
            }
        } ) );

        List<String> requeueStatuses = new ArrayList<String>();
        requeueStatuses.add("ERROR");
        requeueStatuses.add("RUNNING");
        cells.add( new ActionHasCell( Constants.INSTANCE.Requeue(), requeueStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to requeue this Job?" ) ) {
                    presenter.requeueRequest(job.getJobId());
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
        return actionsColumn;
    }

    private class ActionHasCell implements HasCell<RequestSummary, RequestSummary> {

        private final List<String> availableStatuses;
        private ActionCell<RequestSummary> cell;

        public ActionHasCell( final String text, List<String> availableStatusesList,
                              Delegate<RequestSummary> delegate ) {
            this.availableStatuses = availableStatusesList;
            cell = new ActionCell<RequestSummary>( text, delegate ){
                @Override
                public void render(Context context, RequestSummary value, SafeHtmlBuilder sb) {
                    if ( availableStatuses.contains(value.getStatus())){
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<a href='javascript:;' class='btn btn-mini' style='margin-right:5px;' title='"+text+"'>"+text+"</a>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<RequestSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<RequestSummary, RequestSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public RequestSummary getValue( RequestSummary object ) {
            return object;
        }
    }




    public void initDefaultFilters(GridGlobalPreferences preferences ,Button createTabButton){

        List<String> statuses;

        statuses = null;

        initTabFilter( preferences, "RequestListGrid_0", Constants.INSTANCE.All(), "Filter " + Constants.INSTANCE.All(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "QUEUED" );

        initTabFilter( preferences, "RequestListGrid_1", Constants.INSTANCE.Queued(), "Filter " + Constants.INSTANCE.Queued(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "RUNNING" );

        initTabFilter( preferences, "RequestListGrid_2", Constants.INSTANCE.Running(), "Filter " + Constants.INSTANCE.Running(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "RETRYING" );

        initTabFilter( preferences, "RequestListGrid_3", Constants.INSTANCE.Retrying(), "Filter " + Constants.INSTANCE.Retrying(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "ERROR" );

        initTabFilter( preferences, "RequestListGrid_4", Constants.INSTANCE.Error(), "Filter " + Constants.INSTANCE.Error(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "DONE" );

        initTabFilter( preferences, "RequestListGrid_5", Constants.INSTANCE.Completed(), "Filter " + Constants.INSTANCE.Completed(), statuses );

        statuses=new ArrayList<String>(  );
        statuses.add( "CANCELLED" );

        initTabFilter( preferences, "RequestListGrid_6", Constants.INSTANCE.Cancelled(), "Filter " + Constants.INSTANCE.Cancelled(), statuses );

        filterPagedTable.addAddTableButton( createTabButton );
        applyFilterOnPresenter( "RequestListGrid_6" );

    }

    private void initTabFilter(GridGlobalPreferences preferences, final String key, String tabName,
                               String tabDesc, List<String> statuses ){

        HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>(  );

        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM,tabName);
        tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, tabDesc);
        tabSettingsValues.put( RequestListPresenter.FILTER_STATUSES_PARAM_NAME, statuses );

        filterPagedTable.saveNewTabSettings( key, tabSettingsValues );
        final ExtendedPagedTable<RequestSummary> extendedPagedTable = createGridInstance(  new GridGlobalPreferences( key, preferences.getInitialColumns(), preferences.getBannedColumns()), key );
        currentListGrid = extendedPagedTable;
        presenter.addDataDisplay( extendedPagedTable );
        extendedPagedTable.setDataProvider(presenter.getDataProvider() );
        filterPagedTable.addTab( extendedPagedTable, key, new Command() {
            @Override
            public void execute() {
                currentListGrid = extendedPagedTable;
                applyFilterOnPresenter( key  );
            }
        } ) ;

    }

    public void applyFilterOnPresenter( HashMap<String, Object> params){
        List<String> statuses = ( List ) params.get(RequestListPresenter.FILTER_STATUSES_PARAM_NAME );
        presenter.refreshRequests( statuses );

    }
    public void applyFilterOnPresenter(String key) {
        initSelectionModel();
        applyFilterOnPresenter( filterPagedTable.getMultiGridPreferencesStore().getGridSettings( key ) );
    }
    public int getRefreshValue(){
        return getMultiGridPreferencesStore().getRefreshInterval();
    }

    public void saveRefreshValue(int newValue){
        filterPagedTable.saveNewRefreshInterval( newValue );
    }

    public void restoreTabs() {
        ArrayList<String> existingGrids = getMultiGridPreferencesStore().getGridsId();
        ArrayList<String> allTabs= new ArrayList<String>( existingGrids.size() );


        if ( existingGrids != null && existingGrids.size() > 0 ) {

            for ( int i = 0; i < existingGrids.size(); i++ ) {
                allTabs.add( existingGrids.get( i ) );
            }

            for ( int i = 0; i < allTabs.size(); i++ ) {
                filterPagedTable.removeTab( allTabs.get(i) );
            }

        }
        filterPagedTable.tabPanel.remove( 0 );
        initDefaultFilters( currentGlobalPreferences, createTabButton );
    }



}
