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
package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.RefreshSelectorMenuBuilder;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.jbpm.console.ng.pr.model.ProcessInstanceVariableSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash.DataSetProcessInstanceListVariableViewImpl.*;

@Dependent
@WorkbenchScreen(identifier = "DataSet Process Instance Variable List")
public class DataSetProcessInstanceVariableListPresenter extends AbstractScreenListPresenter<ProcessInstanceVariableSummary> {

    public interface DataSetProcessInstanceVariableListView extends ListView<org.jbpm.console.ng.pr.model.ProcessInstanceVariableSummary, DataSetProcessInstanceVariableListPresenter> {

        int getRefreshValue();

        void restoreTabs();

        void saveRefreshValue( int newValue );

        void applyFilterOnPresenter( String key );
    }

    @Inject
    private DataSetProcessInstanceVariableListView view;

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private ErrorPopupPresenter errorPopup;

    private RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder( this );

    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;

    private Constants constants = GWT.create( Constants.class );

    public DataSetProcessInstanceVariableListPresenter() {
        super();
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
            if(!isAddingDefaultFilters()) {
                FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
                if ( currentTableSettings != null ) {
                    currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                    //GWT.log( "-----PIVarList getData table name " + currentTableSettings.getTableName() );
                    if ( columnSortList != null && columnSortList.size() > 0 ) {
                        dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                        dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                    } else {
                        dataSetQueryHelper.setLastOrderedColumn( DataSetProcessInstanceListVariableViewImpl.VARIABLE_NAME );
                        dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                    }

                    if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {

                        DataSetFilter filter = new DataSetFilter();
                        List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
                        filters.add( likeTo( DataSetProcessInstanceListVariableViewImpl.PROCESS_NAME, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filters.add( likeTo( DataSetProcessInstanceListVariableViewImpl.PROCESS_INSTANCE_ID, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filters.add( likeTo( DataSetProcessInstanceListVariableViewImpl.VARIABLE_VALUE, "%" + textSearchStr.toLowerCase() + "%", false ) );
                        filter.addFilterColumn( OR( filters ) );

                        if ( currentTableSettings.getDataSetLookup().getFirstFilterOp() != null ) {
                            currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn( OR( filters ) );
                        } else {
                            currentTableSettings.getDataSetLookup().addOperation( filter );
                        }
                        textSearchStr = "";
                    }
                    dataSetQueryHelper.setDataSetHandler( currentTableSettings );
                    dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
                        @Override
                        public void callback( DataSet dataSet ) {
                            if ( dataSet != null ) {
                                List<ProcessInstanceVariableSummary> myProcessInstancesFromDataSet = new ArrayList<ProcessInstanceVariableSummary>();

                                for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                                    myProcessInstancesFromDataSet.add( new ProcessInstanceVariableSummary(
                                            dataSetQueryHelper.getColumnLongValue( dataSet, DataSetProcessInstanceListVariableViewImpl.PROCESS_INSTANCE_ID, i ),
                                            dataSetQueryHelper.getColumnStringValue( dataSet, DataSetProcessInstanceListVariableViewImpl.PROCESS_NAME, i ),
                                            dataSetQueryHelper.getColumnLongValue( dataSet, DataSetProcessInstanceListVariableViewImpl.VARIABLE_ID, i ),
                                            dataSetQueryHelper.getColumnStringValue( dataSet, DataSetProcessInstanceListVariableViewImpl.VARIABLE_NAME, i ),
                                            dataSetQueryHelper.getColumnStringValue( dataSet, DataSetProcessInstanceListVariableViewImpl.VARIABLE_VALUE, i )
                                    ) );

                                }
                                PageResponse<ProcessInstanceVariableSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceVariableSummary>();
                                processInstanceSummaryPageResponse.setPageRowList( myProcessInstancesFromDataSet );
                                processInstanceSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                                processInstanceSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                                processInstanceSummaryPageResponse.setTotalRowSizeExact( true );
                                if ( visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                                    processInstanceSummaryPageResponse.setLastPage( true );
                                } else {
                                    processInstanceSummaryPageResponse.setLastPage( false );
                                }
                                DataSetProcessInstanceVariableListPresenter.this.updateDataOnCallback( processInstanceSummaryPageResponse );
                            }
                            view.hideBusyIndicator();
                        }

                        @Override
                        public void notFound() {
                            view.hideBusyIndicator();
                            errorPopup.showMessage( "Not found DataSet with UUID [  " + PROCESS_INSTANCE_WITH_VARIABLES_DATASET + " ] " );
                            GWT.log( "DataSet with UUID [  " + PROCESS_INSTANCE_WITH_VARIABLES_DATASET + " ] not found." );
                        }

                        @Override
                        public boolean onError( final ClientRuntimeError error ) {
                            view.hideBusyIndicator();
                            errorPopup.showMessage( "DataSet with UUID [  " + PROCESS_INSTANCE_WITH_VARIABLES_DATASET + " ] error: " + error.getThrowable() );
                            GWT.log( "DataSet with UUID [  " + PROCESS_INSTANCE_WITH_VARIABLES_DATASET + " ] error: ", error.getThrowable() );
                            return false;
                        }
                    } );
                } else {
                    view.hideBusyIndicator();
                }
            }
        } catch ( Exception e ) {
            GWT.log( "Error looking up dataset with UUID [ " + PROCESS_INSTANCE_WITH_VARIABLES_DATASET + " ]" );
        }

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
        refreshGrid();
    }

    public void abortProcessInstance( long processInstanceId ) {
        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).abortProcessInstance( processInstanceId );
    }

    public void abortProcessInstance( List<Long> processInstanceIds ) {
        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                refreshGrid();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).abortProcessInstances( processInstanceIds );
    }

    public void suspendProcessInstance( String processDefId,
                                        long processInstanceId ) {
        kieSessionServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                refreshGrid();

            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).suspendProcessInstance( processInstanceId );
    }

    public void bulkSignal( List<ProcessInstanceVariableSummary> processInstances ) {
        StringBuilder processIdsParam = new StringBuilder();
        if ( processInstances != null ) {

            for ( ProcessInstanceVariableSummary selected : processInstances ) {

            }
            // remove last ,
            if ( processIdsParam.length() > 0 ) {
                processIdsParam.deleteCharAt( processIdsParam.length() - 1 );
            }
        } else {
            processIdsParam.append( "-1" );
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Signal Process Popup" );
        placeRequestImpl.addParameter( "processInstanceId", processIdsParam.toString() );

        placeManager.goTo( placeRequestImpl );
        view.displayNotification( constants.Signaling_Process_Instance() );

    }

    public void bulkAbort( List<ProcessInstanceVariableSummary> processInstances ) {
        if ( processInstances != null ) {
            if ( Window.confirm( "Are you sure that you want to abort the selected process instances?" ) ) {
                List<Long> ids = new ArrayList<Long>();
                for ( ProcessInstanceVariableSummary selected : processInstances ) {

                    ids.add( selected.getProcessInstanceId() );

                    view.displayNotification( constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")" );
                }
                abortProcessInstance( ids );

            }
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Instances And Variables";
    }

    @WorkbenchPartView
    public UberView<DataSetProcessInstanceVariableListPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory

                .newTopLevelMenu( Constants.INSTANCE.New_Process_Instance() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        newProcessInstancePopup.show();
                    }
                } )
                .endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                view.getMenuRefreshButton().addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        refreshGrid();
                                    }
                                } );
                                return view.getMenuRefreshButton();
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceListPresenter#menuRefreshButton";
                            }

                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( refreshSelectorMenuBuilder ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                view.getMenuResetTabsButton().addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        view.restoreTabs();
                                    }
                                } );
                                return view.getMenuResetTabsButton();
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceList#menuResetTabsButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();

    }

    @Override
    public void onGridPreferencesStoreLoaded() {
        refreshSelectorMenuBuilder.loadOptions( view.getRefreshValue() );
    }

    @Override
    protected void updateRefreshInterval( boolean enableAutoRefresh, int newInterval ) {
        super.updateRefreshInterval( enableAutoRefresh, newInterval );
        view.saveRefreshValue( newInterval );
    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        textSearchStr = searchEvent.getFilter();
        if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put( "textSearch", textSearchStr );
            dataSetQueryHelper.getCurrentTableSettings().getKey();

            view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
        }
    }
}
