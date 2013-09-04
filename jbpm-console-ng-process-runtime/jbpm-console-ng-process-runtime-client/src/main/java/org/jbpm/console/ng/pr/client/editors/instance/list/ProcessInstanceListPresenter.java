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
package org.jbpm.console.ng.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import java.util.Set;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesSearchEvent;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceCreated;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Instance List")
public class ProcessInstanceListPresenter {

    public interface ProcessInstanceListView extends UberView<ProcessInstanceListPresenter> {

        void displayNotification(String text);

        DataGrid<ProcessInstanceSummary> getDataGrid();

        String getCurrentFilter();

        void setCurrentFilter(String filter);

        NavLink getShowAllLink();

        NavLink getShowCompletedLink();

        NavLink getShowAbortedLink();

        NavLink getShowRelatedToMeLink();

        DataGrid<ProcessInstanceSummary> getProcessInstanceListGrid();

        Set<ProcessInstanceSummary> getSelectedProcessInstances();
    }
    @Inject
    private PlaceManager placeManager;
    private String currentProcessDefinition;
    private PlaceRequest place;
    private Menus menus;
    @Inject
    private Identity identity;
    @Inject
    private ProcessInstanceListView view;
    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    @Inject
    private Caller<KieSessionEntryPoint> kieSessionServices;
    private ListDataProvider<ProcessInstanceSummary> dataProvider = new ListDataProvider<ProcessInstanceSummary>();
    private Constants constants = GWT.create(Constants.class);
    private List<ProcessInstanceSummary> currentProcessInstances;
    @Inject
    private Event<ClearSearchEvent> clearSearchEvent;

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceListPresenter> getView() {
        return view;
    }

    public ProcessInstanceListPresenter() {
        makeMenuBar();
    }

    @PostConstruct
    public void init() {
    }

    public void filterProcessList(String filter) {
        if (filter.equals("")) {
            if (currentProcessInstances != null) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll(new ArrayList<ProcessInstanceSummary>(currentProcessInstances));
                dataProvider.refresh();

            }
        } else {
            if (currentProcessInstances != null) {
                List<ProcessInstanceSummary> processes = new ArrayList<ProcessInstanceSummary>(currentProcessInstances);
                List<ProcessInstanceSummary> filteredProcesses = new ArrayList<ProcessInstanceSummary>();
                for (ProcessInstanceSummary ps : processes) {
                    if (ps.getProcessName().toLowerCase().contains(filter.toLowerCase())
                            || ps.getInitiator().toLowerCase().contains(filter.toLowerCase())) {
                        filteredProcesses.add(ps);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.getList().addAll(filteredProcesses);
                dataProvider.refresh();
            }
        }

    }

    public void refreshActiveProcessList() {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
            @Override
            public void callback(List<ProcessInstanceSummary> processInstances) {
                currentProcessInstances = processInstances;
                filterProcessList(view.getCurrentFilter());
            }
        }).getProcessInstances(states, "", null);
    }

    public void refreshRelatedToMeProcessList() {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
            @Override
            public void callback(List<ProcessInstanceSummary> processInstances) {
                currentProcessInstances = processInstances;
                filterProcessList(view.getCurrentFilter());
            }
        }).getProcessInstances(states, "", identity.getName());
    }

    public void refreshAbortedProcessList() {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ABORTED);
        dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
            @Override
            public void callback(List<ProcessInstanceSummary> processInstances) {
                currentProcessInstances = processInstances;
                filterProcessList(view.getCurrentFilter());
            }
        }).getProcessInstances(states, "", null);
    }

    public void refreshCompletedProcessList() {
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_COMPLETED);
        dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
            @Override
            public void callback(List<ProcessInstanceSummary> processInstances) {
                currentProcessInstances = processInstances;
                filterProcessList(view.getCurrentFilter());
            }
        }).getProcessInstances(states, "", null);
    }

    public void newInstanceCreated(@Observes ProcessInstanceCreated pi) {
        refreshActiveProcessList();
    }

    public void addDataDisplay(HasData<ProcessInstanceSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<ProcessInstanceSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @OnOpen
    public void onOpen() {

        this.currentProcessDefinition = place.getParameter("processName", "");
        view.setCurrentFilter(currentProcessDefinition);
        refreshActiveProcessList();
    }

    public void abortProcessInstance(long processInstanceId) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshActiveProcessList();

            }
        }).abortProcessInstance(processInstanceId);
    }
    
    public void abortProcessInstance(List<Long> processInstanceIds) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshActiveProcessList();

            }
        }).abortProcessInstances(processInstanceIds);
    }

    
    public void suspendProcessInstance(String processDefId,
            long processInstanceId) {
        kieSessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
                refreshActiveProcessList();

            }
        }).suspendProcessInstance(processInstanceId);
    }

    public void onSearch(@Observes final ProcessInstancesSearchEvent searchFilter) {
        view.setCurrentFilter(searchFilter.getFilter());
        List<Integer> states = new ArrayList<Integer>();
        states.add(ProcessInstance.STATE_ACTIVE);
        dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
            @Override
            public void callback(List<ProcessInstanceSummary> processInstances) {
                currentProcessInstances = processInstances;
                filterProcessList(view.getCurrentFilter());
            }
        }).getProcessInstances(states, "", null);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(constants.Bulk_Actions())
                .withItems(getBulkActions())
                .endMenu()
                .newTopLevelMenu(constants.Refresh())
                .respondsWith(new Command() {
            @Override
            public void execute() {
                view.getShowAllLink().setStyleName("active");
                view.getShowCompletedLink().setStyleName("");
                view.getShowAbortedLink().setStyleName("");
                view.getShowRelatedToMeLink().setStyleName("");
                refreshActiveProcessList();
                clearSearchEvent.fire(new ClearSearchEvent());
                view.setCurrentFilter("");
                view.displayNotification(constants.Process_Instances_Refreshed());
            }
        })
                .endMenu().build();

    }

    private List<? extends MenuItem> getBulkActions() {
        List<MenuItem> bulkActions = new ArrayList<MenuItem>(2);
        bulkActions.add(MenuFactory.newSimpleItem(constants.Bulk_Signal()).respondsWith(new Command() {
            @Override
            public void execute() {
                StringBuffer processIdsParam = new StringBuffer();
                if (view.getSelectedProcessInstances() != null) {

                    for (ProcessInstanceSummary selected : view.getSelectedProcessInstances()) {
                        if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                            view.displayNotification(constants.Signaling_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                                    + ")");
                            continue;
                        }
                        processIdsParam.append(selected.getId() + ",");
                        view.getProcessInstanceListGrid().getSelectionModel().setSelected(selected, false);
                    }
                    // remove last ,
                    if (processIdsParam.length() > 0) {
                        processIdsParam.deleteCharAt(processIdsParam.length() - 1);
                    }
                } else {
                    processIdsParam.append("-1");
                }
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
                placeRequestImpl.addParameter("processInstanceId", processIdsParam.toString());

                placeManager.goTo(placeRequestImpl);
                view.displayNotification(constants.Signaling_Process_Instance());
            }
        }).endMenu().build().getItems().get(0));

        bulkActions.add(MenuFactory.newSimpleItem(constants.Bulk_Abort()).respondsWith(new Command() {
            @Override
            public void execute() {
                if (view.getSelectedProcessInstances() != null) {
                    List<Long> ids = new ArrayList<Long>();
                    for (ProcessInstanceSummary selected : view.getSelectedProcessInstances()) {
                        if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                            view.displayNotification(constants.Aborting_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                                    + ")");
                            continue;
                        }
                        ids.add(selected.getId());
                        
                        view.getProcessInstanceListGrid().getSelectionModel().setSelected(selected, false);
                        view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")");
                    }
                    abortProcessInstance(ids);
                    
                }
            }
        }).endMenu().build().getItems().get(0));


        return bulkActions;
    }
}
