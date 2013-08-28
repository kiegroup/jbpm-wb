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

package org.jbpm.console.ng.pr.client.editors.definition.list;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessDefinitionsSearchEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceCreated;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Process Definition List")
public class ProcessDefinitionListPresenter {

    public interface ProcessDefinitionListView extends UberView<ProcessDefinitionListPresenter> {

        void displayNotification( String text );

        String getCurrentFilter();

        void setCurrentFilter( String filter );

        DataGrid<ProcessSummary> getDataGrid();

        void showBusyIndicator( String message );

        void hideBusyIndicator();
    }

    private Menus menus;

    @Inject
    private ProcessDefinitionListView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    @Inject
    private Event<ProcessInstanceCreated> processInstanceCreatedEvents;

    @Inject
    private Event<ClearSearchEvent> clearSearchEvent;

    private ListDataProvider<ProcessSummary> dataProvider = new ListDataProvider<ProcessSummary>();

    private Constants constants = GWT.create( Constants.class );

    private List<ProcessSummary> currentProcesses;

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Definitions();
    }

    @WorkbenchPartView
    public UberView<ProcessDefinitionListPresenter> getView() {
        return view;
    }

    public ProcessDefinitionListPresenter() {
        makeMenuBar();
    }

    public void refreshProcessList() {
        dataServices.call( new RemoteCallback<List<ProcessSummary>>() {
            @Override
            public void callback( List<ProcessSummary> processes ) {
                currentProcesses = processes;
                filterProcessList( view.getCurrentFilter() );
                clearSearchEvent.fire( new ClearSearchEvent() );
            }
        } ).getProcesses();
    }

    public void filterProcessList( String filter ) {
        if ( filter.equals( "" ) ) {
            if ( currentProcesses != null ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( new ArrayList<ProcessSummary>( currentProcesses ) );
                dataProvider.refresh();

            }
        } else {
            if ( currentProcesses != null ) {
                List<ProcessSummary> processes = new ArrayList<ProcessSummary>( currentProcesses );
                List<ProcessSummary> filteredProcesses = new ArrayList<ProcessSummary>();
                for ( ProcessSummary ps : processes ) {
                    if ( ps.getName().toLowerCase().contains( filter.toLowerCase() ) ) {
                        filteredProcesses.add( ps );
                    }
                }
                dataProvider.getList().clear();
                dataProvider.getList().addAll( filteredProcesses );
                dataProvider.refresh();
            }
        }

    }

    public void reloadRepository() {

        view.showBusyIndicator( constants.Please_Wait() );
        deploymentManager.call( new RemoteCallback<Void>() {
                                    @Override
                                    public void callback( Void organizations ) {
                                        refreshProcessList();
                                        view.hideBusyIndicator();
                                        view.displayNotification( constants.Processes_Refreshed_From_The_Repo() );
                                    }
                                }, new ErrorCallback<Message>() {

                                    @Override
                                    public boolean error( Message message,
                                                          Throwable throwable ) {
                                        view.hideBusyIndicator();
                                        view.displayNotification( "Error: Process refreshed from repository failed" );
                                        return true;
                                    }
                                }
                              ).redeploy();

    }

    public void addDataDisplay( HasData<ProcessSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<ProcessSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnOpen
    public void onOpen() {
        refreshProcessList();
    }

    public void onSearch( @Observes final ProcessDefinitionsSearchEvent searchFilter ) {
        view.setCurrentFilter( searchFilter.getFilter() );
        dataServices.call( new RemoteCallback<List<ProcessSummary>>() {
            @Override
            public void callback( List<ProcessSummary> processes ) {
                currentProcesses = processes;
                filterProcessList( view.getCurrentFilter() );
            }
        } ).getProcesses();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
//                .newTopLevelMenu( constants.Options())
//                .withItems( null )
//                .endMenu()
                .newTopLevelMenu( constants.Refresh() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshProcessList();
                        view.setCurrentFilter( "" );
                        view.displayNotification( constants.Process_Definitions_Refreshed() );
                    }
                } )
                .endMenu().
                        build();

    }

//    private List<MenuItem> getOptions(){
//        
//        
//        MenuFactory.newSimpleItem( description ).respondsWith( new Command() {
//                    @Override
//                    public void execute() {
//                        newResourcePresenter.show( activeHandler );
//                    }
//                } 
//    
//    }

}
