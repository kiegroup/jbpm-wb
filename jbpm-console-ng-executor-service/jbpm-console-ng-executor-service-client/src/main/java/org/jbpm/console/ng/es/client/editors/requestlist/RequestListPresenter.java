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

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.DataGrid;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.uberfire.client.annotations.WorkbenchMenu;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Requests List")
public class RequestListPresenter {

    public interface RequestListView extends UberView<RequestListPresenter> {

        void displayNotification(String text);

        NavLink getShowAllLink();

        NavLink getShowQueuedLink();

        NavLink getShowRunningLink();

        NavLink getShowRetryingLink();

        NavLink getShowErrorLink();

        NavLink getShowCompletedLink();

        NavLink getShowCancelledLink();

        DataGrid<RequestSummary> getDataGrid();

        ColumnSortEvent.ListHandler<RequestSummary> getSortHandler();
    }
    private Constants constants = GWT.create( Constants.class );
    
    @Inject
    private PlaceManager placeManager;

    @Inject
    private RequestListView view;
    
    private Menus menus;
    
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    @Inject
    private Event<RequestChangedEvent> requestChangedEvent;

    private ListDataProvider<RequestSummary> dataProvider = new ListDataProvider<RequestSummary>();

    public RequestListPresenter() {
        makeMenuBar();
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
        if (statuses == null || statuses.isEmpty()) {
            executorServices.call(new RemoteCallback<List<RequestSummary>>() {
                @Override
                public void callback(List<RequestSummary> requests) {
                    dataProvider.setList(requests);
                    dataProvider.refresh();
                    view.getSortHandler().getList().addAll(dataProvider.getList());

                }
            }).getAllRequests();
        } else {
            executorServices.call(new RemoteCallback<List<RequestSummary>>() {
                @Override
                public void callback(List<RequestSummary> requests) {
                    dataProvider.setList(requests);
                    dataProvider.refresh();
                    view.getSortHandler().getList().addAll(dataProvider.getList());
                }
            }).getRequestsByStatus(statuses);
        }
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

    public void addDataDisplay(HasData<RequestSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<RequestSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
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
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(constants.Settings())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "Job Service Settings" ) );
                        
                    }
                })
                .endMenu()
                .newTopLevelMenu(constants.New_Job())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( new DefaultPlaceRequest( "Quick New Job" ) );
                        
                    }
                })
                .endMenu()
                .newTopLevelMenu(constants.Refresh())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        view.getShowAllLink().setStyleName("active");
                        view.getShowCompletedLink().setStyleName("");
                        view.getShowCancelledLink().setStyleName("");
                        view.getShowErrorLink().setStyleName("");
                        view.getShowQueuedLink().setStyleName("");
                        view.getShowRetryingLink().setStyleName("");
                        view.getShowRunningLink().setStyleName("");
                        refreshRequests(null);
//                        clearSearchEvent.fire(new ClearSearchEvent());
//                        view.setCurrentFilter("");
//                        view.displayNotification(constants.Process_Instances_Refreshed());
                    }
                })
                .endMenu().build();

    }
    
    
}
