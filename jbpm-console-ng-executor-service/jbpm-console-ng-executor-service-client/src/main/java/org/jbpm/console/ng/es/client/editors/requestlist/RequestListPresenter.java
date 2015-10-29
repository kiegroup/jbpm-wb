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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.editors.quicknewjob.QuickNewJobPopup;
import org.jbpm.console.ng.es.client.editors.servicesettings.JobServiceSettingsPopup;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.paging.PageResponse;


import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "Requests List")
public class RequestListPresenter extends AbstractScreenListPresenter<RequestSummary> {
    public static String FILTER_STATUSES_PARAM_NAME = "states";

    public interface RequestListView extends ListView<RequestSummary, RequestListPresenter> {
        int getRefreshValue();
        void restoreTabs();
        void saveRefreshValue(int newValue);
    }
    private Constants constants = GWT.create( Constants.class );

    @Inject
    private RequestListView view;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    @Inject
    private Event<RequestChangedEvent> requestChangedEvent;


    private List<String> currentActiveStates;

    @Inject
    private JobServiceSettingsPopup jobServiceSettingsPopup;

    @Inject
    private QuickNewJobPopup quickNewJobPopup;

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

    public void refreshRequests(List<String> statuses) {
        currentActiveStates = statuses;
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
    public void getData(Range visibleRange) {
        if(!isAddingDefaultFilters()) {
            ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
            if ( currentFilter == null ) {
                currentFilter = new PortableQueryFilter( visibleRange.getStart(),
                        visibleRange.getLength(),
                        false, "",
                        ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                                .getColumn().getDataStoreName() : "",
                        ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                                .isAscending() : true );
            }
            // If we are refreshing after a search action, we need to go back to offset 0
            if ( currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                    || currentFilter.getParams().get( "textSearch" ) == null || currentFilter.getParams().get( "textSearch" ).equals( "" ) ) {
                currentFilter.setOffset( view.getListGrid().getPageStart() );
                currentFilter.setCount( view.getListGrid().getPageSize() );
            } else {
                currentFilter.setOffset( 0 );
                currentFilter.setCount( view.getListGrid().getPageSize() );
            }
            //Applying screen specific filters
            if ( currentFilter.getParams() == null ) {
                currentFilter.setParams( new HashMap<String, Object>() );
            }
            currentFilter.getParams().put( "states", currentActiveStates );

            currentFilter.setOrderBy( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                    .getColumn().getDataStoreName() : "" );
            currentFilter.setIsAscending( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                    .isAscending() : true );

            executorServices.call( new RemoteCallback<PageResponse<RequestSummary>>() {
                @Override
                public void callback( PageResponse<RequestSummary> response ) {
                    updateDataOnCallback( response );
                }
            }, new ErrorCallback<Message>() {
                @Override
                public boolean error( Message message, Throwable throwable ) {
                    view.hideBusyIndicator();
                    view.displayNotification( "Error: Getting Jbos Requests: " + message );
                    GWT.log( throwable.toString() );
                    return true;
                }
            } ).getData( currentFilter );
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
        view.setupButtons();

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
                                return "org.jbpm.console.ng.es.client.editors.requestlist.RequestListPresenter#menuResetTabsButton";
                            }

                        };
                    }
                } ).endMenu()
                .build();


    }

    protected void saveRefreshValue(int newValue){
        view.saveRefreshValue( newValue );
    }

    protected int getRefreshValue(){
        return view.getRefreshValue();
    }


}