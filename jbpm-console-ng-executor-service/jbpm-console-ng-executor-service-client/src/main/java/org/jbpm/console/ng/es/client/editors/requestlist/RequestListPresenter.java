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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.es.client.editors.quicknewjob.QuickNewJobPopup;
import org.jbpm.console.ng.es.client.editors.servicesettings.JobServiceSettingsPopup;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.paging.PageResponse;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.likeTo;

@Dependent
@WorkbenchScreen(identifier = "Requests List")
public class RequestListPresenter extends AbstractScreenListPresenter<RequestSummary> {
    public static String FILTER_STATUSES_PARAM_NAME = "states";

    public interface RequestListView extends ListView<RequestSummary, RequestListPresenter> {
        public int getRefreshValue();
        public void restoreTabs();
        public void saveRefreshValue(int newValue);
        public void applyFilterOnPresenter(String key);
    }
    private Constants constants = GWT.create( Constants.class );

    @Inject
    private RequestListView view;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    @Inject
    private Event<RequestChangedEvent> requestChangedEvent;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    private List<String> currentActiveStates;

    @Inject
    private JobServiceSettingsPopup jobServiceSettingsPopup;

    @Inject
    private QuickNewJobPopup quickNewJobPopup;

    public Button menuActionsButton;
    private PopupPanel popup = new PopupPanel(true);

    public Button menuRefreshButton = new Button();
    public Button menuResetTabsButton = new Button();


    @Inject
    private ErrorPopupPresenter errorPopup;


    public RequestListPresenter() {
        super();
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return constants.RequestsListTitle();
    }

    @WorkbenchPartView
    public UberView<RequestListPresenter> getView() {
        return view;
    }

    public void filterGrid(FilterSettings tableSettings) {
        dataSetQueryHelper.setCurrentTableSettings( tableSettings);
        refreshGrid();
    }

    public void init() {
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Executor Service Started ...");
            }
        }).init();
    }

    public void createRequest() {
        Map<String, String> ctx = new HashMap<String, String>();
        ctx.put("businessKey", "1234");
        executorServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long requestId) {
                view.displayNotification("Request Schedulled: " + requestId);

            }
        }).scheduleRequest("PrintOutCmd", ctx);
    }

    @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(final Range visibleRange) {
        try {
            FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
            if(currentTableSettings!=null) {
                currentTableSettings.setTablePageSize( view.getListGrid().getPageSize() );
                ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                //GWT.log( "processInstances getData "+columnSortList.size() +"currentTableSettings table name "+ currentTableSettings.getTableName() );
                if(columnSortList!=null &&  columnSortList.size()>0) {
                    dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
                    dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
                }else {
                    dataSetQueryHelper.setLastOrderedColumn( RequestListViewImpl.COLUMN_TIMESTAMP );
                    dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
                }
                dataSetQueryHelper.setDataSetHandler(   currentTableSettings );
                if(textSearchStr!=null && textSearchStr.trim().length()>0){

                    DataSetFilter filter = new DataSetFilter();
                    List<ColumnFilter> filters =new ArrayList<ColumnFilter>(  );
                    filters.add(likeTo( RequestListViewImpl.COLUMN_COMMANDNAME, "%" + textSearchStr + "%" ) );
                    filters.add(likeTo( RequestListViewImpl.COLUMN_MESSAGE, "%" + textSearchStr + "%" ) );
                    filters.add(likeTo( RequestListViewImpl.COLUMN_BUSINESSKEY, "%" + textSearchStr + "%" ) );
                    filter.addFilterColumn( OR(filters));

                    if(currentTableSettings.getDataSetLookup().getFirstFilterOp()!=null) {
                        currentTableSettings.getDataSetLookup().getFirstFilterOp().addFilterColumn( OR( filters ) );
                    }else {
                        currentTableSettings.getDataSetLookup().addOperation( filter );
                    }
                    textSearchStr="";
                }
                dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
                    @Override
                    public void callback( DataSet dataSet ) {
                        if ( dataSet != null) {
                            List<RequestSummary> myRequestSumaryFromDataSet = new ArrayList<RequestSummary>();

                            for ( int i = 0; i < dataSet.getRowCount(); i++ ) {

                                myRequestSumaryFromDataSet.add( new RequestSummary(
                                        dataSetQueryHelper.getColumnLongValue( dataSet, RequestListViewImpl.COLUMN_ID, i ),
                                        dataSetQueryHelper.getColumnDateValue( dataSet, RequestListViewImpl.COLUMN_TIMESTAMP, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, RequestListViewImpl.COLUMN_STATUS, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, RequestListViewImpl.COLUMN_COMMANDNAME, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, RequestListViewImpl.COLUMN_MESSAGE, i ),
                                        dataSetQueryHelper.getColumnStringValue( dataSet, RequestListViewImpl.COLUMN_BUSINESSKEY, i ) ) );

                            }
                            PageResponse<RequestSummary> requestSummaryPageResponse = new PageResponse<RequestSummary>();
                            requestSummaryPageResponse.setPageRowList( myRequestSumaryFromDataSet );
                            requestSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                            requestSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                            requestSummaryPageResponse.setTotalRowSizeExact( true );
                            if ( visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                                requestSummaryPageResponse.setLastPage( true );
                            } else {
                                requestSummaryPageResponse.setLastPage( false );
                            }
                            updateDataOnCallback( requestSummaryPageResponse );
                        }
                        view.hideBusyIndicator();
                    }

                    @Override
                    public void notFound() {
                        view.hideBusyIndicator();
                        errorPopup.showMessage( "Not found DataSet with UUID [  " + RequestListViewImpl.REQUEST_LIST_DATASET_ID + " ] " );
                        GWT.log( "DataSet with UUID [  "+RequestListViewImpl.REQUEST_LIST_DATASET_ID+ " ] not found." );
                    }

                    @Override
                    public boolean onError( final ClientRuntimeError error ) {
                        view.hideBusyIndicator();
                        errorPopup.showMessage( "DataSet with UUID [  "+RequestListViewImpl.REQUEST_LIST_DATASET_ID+ " ] error: " + error.getThrowable() );
                        GWT.log( "DataSet with UUID [  "+RequestListViewImpl.REQUEST_LIST_DATASET_ID+ " ] error: ", error.getThrowable() );
                        return false;
                    }
                } );
            }else {
                view.hideBusyIndicator();
            }
        } catch (Exception e) {
            GWT.log("Error looking up dataset with UUID [ " + RequestListViewImpl.REQUEST_LIST_DATASET_ID + " ]");
        }






    }

    public void addDataDisplay(HasData<RequestSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public AsyncDataProvider<RequestSummary> getDataProvider() {
        return dataProvider;
    }

    public void cancelRequest(final Long requestId) {
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Request " + requestId + " cancelled");
                requestChangedEvent.fire(new RequestChangedEvent(requestId));
            }
        }).cancelRequest(requestId);
    }

    public void requeueRequest(final Long requestId) {
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.displayNotification("Request " + requestId + " cancelled");
                requestChangedEvent.fire(new RequestChangedEvent(requestId));
            }
        }).requeueRequest(requestId);
    }


    @WorkbenchMenu
    public Menus getMenus() {
        setupButtons();

        return MenuFactory

                .newTopLevelMenu( Constants.INSTANCE.New_Job() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        executorServices.call(new RemoteCallback<Boolean>() {
                            @Override
                            public void callback(Boolean isDisabled) {
                                if (isDisabled) {
                                    view.displayNotification("Executor service is disabled");
                                } else {
                                    quickNewJobPopup.show();
                                }
                            }
                        }).isExecutorDisabled();

                    }
                } )
                .endMenu()

                .newTopLevelMenu( Constants.INSTANCE.Job_Service_Settings() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        executorServices.call(new RemoteCallback<Boolean>() {
                            @Override
                            public void callback(Boolean isDisabled) {
                                if (isDisabled) {
                                    view.displayNotification("Executor service is disabled");
                                } else {
                                    jobServiceSettingsPopup.show();
                                }
                            }
                        }).isExecutorDisabled();

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
                                menuRefreshButton.addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        refreshGrid();
                                    }
                                } );
                                return menuRefreshButton;
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
                                return "org.jbpm.console.ng.es.client.editors.requestlist.RequestListPresenter#menuRefreshButton";
                            }

                        };
                    }
                } ).endMenu()


                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return menuActionsButton;
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
                                return "org.jbpm.console.ng.es.client.editors.requestlist.RequestListPresenter#menuActionsButton";
                            }

                        };
                    }
                } ).endMenu()

                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                menuResetTabsButton.addClickHandler( new ClickHandler() {
                                    @Override
                                    public void onClick( ClickEvent clickEvent ) {
                                        view.restoreTabs();
                                    }
                                } );
                                return menuResetTabsButton;
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
                                return "org.jbpm.console.ng.es.client.editors.requestlist.RequestListPresenter#menuResetTabsButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();


    }
    public void setupButtons( ) {
        menuActionsButton = new Button();
        createRefreshToggleButton(menuActionsButton);

        menuRefreshButton.setIcon( IconType.REFRESH );
        menuRefreshButton.setSize( ButtonSize.MINI );
        menuRefreshButton.setTitle(Constants.INSTANCE.Refresh() );

        menuResetTabsButton.setIcon( IconType.TH_LIST );
        menuResetTabsButton.setSize( ButtonSize.MINI );
        menuResetTabsButton.setTitle(Constants.INSTANCE.RestoreDefaultFilters() );
    }

    public void createRefreshToggleButton(final Button refreshIntervalSelector) {

        refreshIntervalSelector.setToggle(true);
        refreshIntervalSelector.setIcon( IconType.COG);
        refreshIntervalSelector.setTitle( Constants.INSTANCE.AutoRefresh() );
        refreshIntervalSelector.setSize( ButtonSize.MINI );

        popup.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
        popup.addAutoHidePartner(refreshIntervalSelector.getElement());
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (popupPanelCloseEvent.isAutoClosed()) {
                    refreshIntervalSelector.setActive(false);
                }
            }
        });

        refreshIntervalSelector.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!refreshIntervalSelector.isActive() ) {
                    showSelectRefreshIntervalPopup( refreshIntervalSelector.getAbsoluteLeft() + refreshIntervalSelector.getOffsetWidth(),
                            refreshIntervalSelector.getAbsoluteTop() + refreshIntervalSelector.getOffsetHeight(),refreshIntervalSelector);
                } else {
                    popup.hide(false);
                }
            }
        });

    }

    private void showSelectRefreshIntervalPopup(final int left,
                                                final int top,
                                                final Button refreshIntervalSelector) {
        VerticalPanel popupContent = new VerticalPanel();

        final Button resetButton = new Button( Constants.INSTANCE.Disable_autorefresh() );

        //int configuredSeconds = presenter.getAutoRefreshSeconds();
        int configuredSeconds = view.getRefreshValue();
        if(configuredSeconds>10) {
            updateRefreshInterval( true,configuredSeconds );
            resetButton.setActive( false );
            resetButton.setEnabled( true );
        } else {
            updateRefreshInterval( false, 0 );
            resetButton.setActive(true );
            resetButton.setEnabled(false);
            resetButton.setText( Constants.INSTANCE.Autorefresh_Disabled() );
        }


        RadioButton oneMinuteRadioButton = createTimeSelectorRadioButton(60, "1 "+Constants.INSTANCE.Minute(), configuredSeconds, refreshIntervalSelector, popupContent,resetButton);
        RadioButton fiveMinuteRadioButton = createTimeSelectorRadioButton(300, "5 "+Constants.INSTANCE.Minutes(), configuredSeconds, refreshIntervalSelector, popupContent,resetButton);
        RadioButton tenMinuteRadioButton = createTimeSelectorRadioButton(600, "10 "+Constants.INSTANCE.Minutes(), configuredSeconds, refreshIntervalSelector, popupContent,resetButton);

        popupContent.add(oneMinuteRadioButton);
        popupContent.add(fiveMinuteRadioButton);
        popupContent.add(tenMinuteRadioButton);

        resetButton.setSize( ButtonSize.MINI );
        resetButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                updateRefreshInterval( false,0 );
                view.saveRefreshValue( 0 );
                refreshIntervalSelector.setActive( false );
                resetButton.setActive( false );
                resetButton.setEnabled( false );
                resetButton.setText( Constants.INSTANCE.Autorefresh_Disabled() );
                popup.hide();
            }
        } );

        popupContent.add( resetButton );


        popup.setWidget(popupContent);
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition(finalLeft, top);

    }

    private RadioButton createTimeSelectorRadioButton(int time, String name, int configuredSeconds,
                                                      final Button refreshIntervalSelector,
                                                      VerticalPanel popupContent,final Button refreshDisableButton) {
        RadioButton oneMinuteRadioButton = new RadioButton("refreshInterval",name);
        oneMinuteRadioButton.setText( name  );
        final int selectedRefreshTime = time;
        if(configuredSeconds == selectedRefreshTime ) {
            oneMinuteRadioButton.setValue( true );
        }

        oneMinuteRadioButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                updateRefreshInterval(true, selectedRefreshTime );
                view.saveRefreshValue( selectedRefreshTime );
                refreshIntervalSelector.setActive( false );
                refreshDisableButton.setActive( false );
                refreshDisableButton.setEnabled( true );
                refreshDisableButton.setText( Constants.INSTANCE.Disable_autorefresh() );
                popup.hide();

            }
        } );
        return oneMinuteRadioButton;
    }

    @Override
    protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
        String filterString = searchEvent.getFilter();
        if(filterString!=null && filterString.trim().length()>0){
            textSearchStr=filterString.toLowerCase();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put( "textSearch", textSearchStr );
            dataSetQueryHelper.getCurrentTableSettings().getKey();

            view.applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
        }
    }
}
