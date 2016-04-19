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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.Range;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetOp;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.dataset.AbstractDataSetReadyCallback;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.gc.client.menu.RestoreDefaultFiltersMenuBuilder;
import org.jbpm.console.ng.pr.client.editors.instance.signal.ProcessInstanceSignalPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.perspectives.DataSetProcessInstancesWithVariablesPerspective;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.pr.model.ProcessInstanceDataSetConstants.*;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

@Dependent
@WorkbenchScreen( identifier = DataSetProcessInstanceWithVariablesListPresenter.SCREEN_ID)
public class DataSetProcessInstanceWithVariablesListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {

    public static final String SCREEN_ID = "DataSet Process Instance List With Variables";

    public interface DataSetProcessInstanceWithVariablesListView extends ListView<ProcessInstanceSummary, DataSetProcessInstanceWithVariablesListPresenter> {

        int getRefreshValue();

        void saveRefreshValue( int newValue );

        FilterSettings getVariablesTableSettings( String processName );

        void addDomainSpecifColumns( ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable,
                                     Set<String> columns );

        void applyFilterOnPresenter( String key );

    }

    @Inject
    private DataSetProcessInstanceWithVariablesListView view;

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    private DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private DataSetQueryHelper dataSetQueryHelperDomainSpecific;

    @Inject
    private ErrorPopupPresenter errorPopup;

    private RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder( this );

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    public DataSetProcessInstanceWithVariablesListPresenter() {
        super();
    }

    public DataSetProcessInstanceWithVariablesListPresenter( DataSetProcessInstanceWithVariablesListView view, DataSetQueryHelper dataSetQueryHelper,
                                                             DataSetQueryHelper dataSetQueryHelperDomainSpecific ) {
        this();
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.dataSetQueryHelperDomainSpecific = dataSetQueryHelperDomainSpecific;
        this.view = view;

    }

    public DataSetProcessInstanceWithVariablesListPresenter(
            DataSetProcessInstanceWithVariablesListView view,
            Caller<ProcessInstanceService> processInstanceService,
            Caller<KieSessionEntryPoint> kieSessionServices,
            DataSetQueryHelper dataSetQueryHelper,
            DataSetQueryHelper dataSetQueryHelperDomainSpecific,
            PlaceManager placeManager) {
        this();
        this.dataSetQueryHelper = dataSetQueryHelper;
        this.processInstanceService = processInstanceService;
        this.kieSessionServices = kieSessionServices;
        this.dataSetQueryHelperDomainSpecific = dataSetQueryHelperDomainSpecific;
        this.view = view;
        this.placeManager = placeManager;
    }

    public void filterGrid( FilterSettings tableSettings ) {
        dataSetQueryHelper.setCurrentTableSettings( tableSettings );
        refreshGrid();
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData( final Range visibleRange ) {
        try {
            if ( !isAddingDefaultFilters() ) {
                final FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                if ( currentTableSettings != null ) {
                    currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                    if ( columnSortList != null && columnSortList.size() > 0 ) {
                        dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                        dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                    } else {
                        dataSetQueryHelper.setLastOrderedColumn( COLUMN_START );
                        dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                    }

                    final List<ColumnFilter> filters = getColumnFilters(textSearchStr);
                    if (filters.isEmpty() == false) {
                        if (currentTableSettings.getDataSetLookup().getFirstFilterOp() != null) {
                            currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn(OR(filters));
                        } else {
                            final DataSetFilter filter = new DataSetFilter();
                            filter.addFilterColumn(OR(filters));
                            currentTableSettings.getDataSetLookup().addOperation(filter);
                        }
                    }

                    dataSetQueryHelper.setCurrentTableSettings( currentTableSettings );
                    dataSetQueryHelper.setDataSetHandler( currentTableSettings );
                    dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), createDataSetProcessInstanceCallback( visibleRange.getStart(), currentTableSettings ) );
                } else {
                    view.hideBusyIndicator();
                }
            }
        } catch ( Exception e ) {
            errorPopup.showMessage(Constants.INSTANCE.UnexpectedError(e.getMessage()));
        }
    }

    protected List<ColumnFilter> getColumnFilters(final String searchString) {
        final List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
        if (searchString != null && searchString.trim().length() > 0) {
            try {
                final Long instanceId = Long.valueOf(searchString.trim());
                filters.add(equalsTo(COLUMN_PROCESS_INSTANCE_ID, instanceId));
            } catch (NumberFormatException ex) {
                filters.add(equalsTo(COLUMN_PROCESS_ID, searchString));
                filters.add(likeTo(COLUMN_PROCESS_NAME, "%" + searchString.toLowerCase() + "%", false));
                filters.add(likeTo(COLUMN_PROCESS_INSTANCE_DESCRIPTION, "%" + searchString.toLowerCase() + "%", false));
                filters.add(likeTo(COLUMN_IDENTITY, "%" + searchString.toLowerCase() + "%", false));
            }
        }
        return filters;
    }

    protected DataSetReadyCallback createDataSetDomainSpecificCallback( final int startRange, final int totalRowSize, final List<ProcessInstanceSummary> instances, final FilterSettings tableSettings ) {
        return new AbstractDataSetReadyCallback( errorPopup, view, tableSettings.getDataSet() ) {
            @Override
            public void callback( DataSet dataSet ) {
                Set<String> columns = new HashSet<String>();
                for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                    Long processInstanceId = dataSetQueryHelperDomainSpecific.getColumnLongValue( dataSet, PROCESS_INSTANCE_ID, i );
                    String variableName = dataSetQueryHelperDomainSpecific.getColumnStringValue( dataSet, VARIABLE_NAME, i );
                    String variableValue = dataSetQueryHelperDomainSpecific.getColumnStringValue( dataSet, VARIABLE_VALUE, i );

                    for ( ProcessInstanceSummary pis : instances ) {
                        if ( pis.getProcessInstanceId().equals( processInstanceId ) ) {
                            pis.addDomainData( variableName, variableValue );
                            columns.add( variableName );
                        }
                    }
                }
                view.addDomainSpecifColumns( view.getListGrid(), columns );

                PageResponse<ProcessInstanceSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceSummary>();
                processInstanceSummaryPageResponse.setPageRowList( instances );
                processInstanceSummaryPageResponse.setStartRowIndex( startRange );
                processInstanceSummaryPageResponse.setTotalRowSize( totalRowSize );
                processInstanceSummaryPageResponse.setTotalRowSizeExact( true );
                if ( startRange + instances.size() == totalRowSize ) {
                    processInstanceSummaryPageResponse.setLastPage( true );
                } else {
                    processInstanceSummaryPageResponse.setLastPage( false );
                }

                DataSetProcessInstanceWithVariablesListPresenter.this.updateDataOnCallback( processInstanceSummaryPageResponse );
            }

        };
    }

    protected DataSetReadyCallback createDataSetProcessInstanceCallback( final int startRange, final FilterSettings tableSettings ) {
        return new AbstractDataSetReadyCallback( errorPopup, view, tableSettings.getDataSet() ) {

            @Override
            public void callback( DataSet dataSet ) {

                if ( dataSet != null ) {
                    final List<ProcessInstanceSummary> myProcessInstancesFromDataSet = new ArrayList<ProcessInstanceSummary>();

                    for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                        myProcessInstancesFromDataSet.add( createProcessInstanceSummaryFromDataSet( dataSet, i ) );

                    }
                    List<DataSetOp> ops = tableSettings.getDataSetLookup().getOperationList();
                    String filterValue = isFilteredByProcessId( ops );


                    if ( filterValue != null ) {
                        getDomainSpecifDataForProcessInstances( startRange, dataSet, filterValue, myProcessInstancesFromDataSet );
                    } else {
                        PageResponse<ProcessInstanceSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceSummary>();
                        processInstanceSummaryPageResponse.setPageRowList( myProcessInstancesFromDataSet );
                        processInstanceSummaryPageResponse.setStartRowIndex( startRange );
                        processInstanceSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                        processInstanceSummaryPageResponse.setTotalRowSizeExact( true );
                        if ( startRange + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                            processInstanceSummaryPageResponse.setLastPage( true );
                        } else {
                            processInstanceSummaryPageResponse.setLastPage( false );
                        }

                        DataSetProcessInstanceWithVariablesListPresenter.this.updateDataOnCallback( processInstanceSummaryPageResponse );
                    }

                }

                view.hideBusyIndicator();
            }

        };
    }

    protected String isFilteredByProcessId( List<DataSetOp> ops ) {
        for ( DataSetOp dataSetOp : ops ) {
            if ( dataSetOp.getType().equals( DataSetOpType.FILTER ) ) {
                List<ColumnFilter> filters = ( ( DataSetFilter ) dataSetOp ).getColumnFilterList();

                for ( ColumnFilter filter : filters ) {

                    if ( filter instanceof CoreFunctionFilter ) {
                        CoreFunctionFilter coreFilter = ( ( CoreFunctionFilter ) filter );
                        if ( filter.getColumnId().toUpperCase().equals( COLUMN_PROCESS_ID.toUpperCase() ) &&
                                ((CoreFunctionFilter) filter).getType() == CoreFunctionType.EQUALS_TO ) {

                            List parameters = coreFilter.getParameters();
                            if ( parameters.size() > 0 ) {
                                return parameters.get( 0 ).toString();
                            }
                        }
                    }
                }
            }
        }

        return null;

    }

    public void getDomainSpecifDataForProcessInstances( final int startRange, DataSet dataSet, String filterValue, final List<ProcessInstanceSummary> myProcessInstancesFromDataSet ) {

        final int rowCountNotTrimmed = dataSet.getRowCountNonTrimmed();
        FilterSettings variablesTableSettings = view.getVariablesTableSettings( filterValue );
        variablesTableSettings.setTablePageSize( -1 );

        dataSetQueryHelperDomainSpecific.setDataSetHandler( variablesTableSettings );
        dataSetQueryHelperDomainSpecific.setCurrentTableSettings( variablesTableSettings );
        dataSetQueryHelperDomainSpecific.setLastOrderedColumn( PROCESS_INSTANCE_ID );
        dataSetQueryHelperDomainSpecific.setLastSortOrder( SortOrder.ASCENDING );
        dataSetQueryHelperDomainSpecific.lookupDataSet( 0, createDataSetDomainSpecificCallback( startRange, rowCountNotTrimmed, myProcessInstancesFromDataSet, variablesTableSettings ) );

    }

    private ProcessInstanceSummary createProcessInstanceSummaryFromDataSet( DataSet dataSet, int i ) {
        return new ProcessInstanceSummary(
                dataSetQueryHelper.getColumnLongValue( dataSet, COLUMN_PROCESS_INSTANCE_ID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_PROCESS_ID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_EXTERNAL_ID, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_PROCESS_NAME, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_PROCESS_VERSION, i ),
                dataSetQueryHelper.getColumnIntValue( dataSet, COLUMN_STATUS, i ),
                dataSetQueryHelper.getColumnDateValue( dataSet, COLUMN_START, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_IDENTITY, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_PROCESS_INSTANCE_DESCRIPTION, i ),
                dataSetQueryHelper.getColumnStringValue( dataSet, COLUMN_CORRELATION_KEY, i ),
                dataSetQueryHelper.getColumnLongValue( dataSet, COLUMN_PARENT_PROCESS_INSTANCE_ID, i ) );
    }


    public void newInstanceCreated( @Observes NewProcessInstanceEvent pi ) {
        refreshGrid();
    }

    public void newInstanceCreated( @Observes ProcessInstancesUpdateEvent pis ) {
        refreshGrid();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnFocus
    public void onFocus() {
        refreshGrid();
    }

    @OnOpen
    public void onOpen() {
        this.textSearchStr = place.getParameter(DataSetProcessInstancesWithVariablesPerspective.PROCESS_ID, "");
        refreshGrid();
    }

    public void abortProcessInstance(long processInstanceId) {
        kieSessionServices.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void v) {
                        refreshGrid();
                    }
                },
                new DefaultErrorCallback()
        ).abortProcessInstance(processInstanceId);
    }

    public void abortProcessInstance(List<Long> processInstanceIds) {
        kieSessionServices.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void v) {
                        refreshGrid();
                    }
                },
                new DefaultErrorCallback()
        ).abortProcessInstances(processInstanceIds);
    }

    public void bulkSignal( List<ProcessInstanceSummary> processInstances ) {
        if ( processInstances == null || processInstances.isEmpty()) {
            return;
        }

        final StringBuilder processIdsParam = new StringBuilder();
        for ( ProcessInstanceSummary selected : processInstances ) {
            if ( selected.getState() != ProcessInstance.STATE_ACTIVE ) {
                view.displayNotification(Constants.INSTANCE.Signaling_Process_Instance_Not_Allowed(selected.getId()));
                continue;
            }
            processIdsParam.append( selected.getId() + "," );
        }

        if ( processIdsParam.length() == 0 ) {
            return;
        } else {
            // remove last ,
            processIdsParam.deleteCharAt( processIdsParam.length() - 1 );
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP);
        placeRequestImpl.addParameter( "processInstanceId", processIdsParam.toString() );

        placeManager.goTo( placeRequestImpl );
        view.displayNotification( Constants.INSTANCE.Signaling_Process_Instance() );
    }

    public void bulkAbort( List<ProcessInstanceSummary> processInstances ) {
        if ( processInstances == null || processInstances.isEmpty() ) {
            return;
        }
        final List<Long> ids = new ArrayList<Long>();
        for ( ProcessInstanceSummary selected : processInstances ) {
            if ( selected.getState() != ProcessInstance.STATE_ACTIVE ) {
                view.displayNotification(Constants.INSTANCE.Aborting_Process_Instance_Not_Allowed(selected.getId()));
                continue;
            }
            ids.add( selected.getProcessInstanceId() );

            view.displayNotification(Constants.INSTANCE.Aborting_Process_Instance(selected.getId()));
        }
        if( ids.size() > 0 ) {
            abortProcessInstance(ids);
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<DataSetProcessInstanceWithVariablesListPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu(Constants.INSTANCE.New_Process_Instance())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        newProcessInstancePopup.show();
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(refreshSelectorMenuBuilder).endMenu()
                .newTopLevelCustomMenu(new RestoreDefaultFiltersMenuBuilder( this )).endMenu()
                .build();
    }

    @Override
    public void onGridPreferencesStoreLoaded() {
        refreshSelectorMenuBuilder.loadOptions( view.getRefreshValue() );
    }

    @Override
    public void onUpdateRefreshInterval( boolean enableAutoRefresh, int newInterval ) {
        super.onUpdateRefreshInterval( enableAutoRefresh, newInterval );
        view.saveRefreshValue( newInterval );
    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        textSearchStr = searchEvent.getFilter();
        view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
    }

}