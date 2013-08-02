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

package org.jbpm.console.ng.bd.client.editors.deployment.list;

import com.google.gwt.core.client.GWT;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.model.events.DeploymentsSearchEvent;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Deployments List")
public class DeploymentUnitsListPresenter {

    public interface DeploymentUnitsListView extends UberView<DeploymentUnitsListPresenter> {

        void displayNotification(String text);

        void showBusyIndicator(String message);

        void hideBusyIndicator();

        String getCurrentFilter();

        void setCurrentFilter(String filter);
    }
    
    @Inject
    private PlaceManager placeManager;
    
    private Menus menus;
    
    @Inject
    private DeploymentUnitsListView view;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    private Constants constants = GWT.create(Constants.class);

    private ListDataProvider<KModuleDeploymentUnitSummary> dataProvider = new ListDataProvider<KModuleDeploymentUnitSummary>();

    private List<KModuleDeploymentUnitSummary> currentDeployedUnits;
    
    @Inject 
    private Event<ClearSearchEvent> clearSearchEvent;
    
    public DeploymentUnitsListPresenter() {
        makeMenuBar();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Deployment_Units();
    }

    @WorkbenchPartView
    public UberView<DeploymentUnitsListPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    
    public void undeployUnit(final String id, final String group, final String artifact, final String version, 
                            final String kbaseName, final String kieSessionName) {
        view.showBusyIndicator(constants.Please_Wait());
        deploymentManager.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.hideBusyIndicator();
                view.displayNotification(" Kjar Undeployed " + group + ":" + artifact + ":" + version);
                refreshDeployedUnits();
            }
        }, new ErrorCallback() {
           @Override
           public boolean error(Message message, Throwable throwable) {
               view.hideBusyIndicator();
               view.displayNotification("Error: Undeploy failed " + throwable.getMessage());
               return true;
           }
       }).undeploy(new KModuleDeploymentUnitSummary(id, group, artifact, version, kbaseName, kieSessionName, null));
    }
    
    public void filterDeployedUnits(String filter){
        if(filter.equals("")){
                if(currentDeployedUnits != null){
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(new ArrayList<KModuleDeploymentUnitSummary>(currentDeployedUnits));
                    dataProvider.refresh();
                    
                }
        }else{
            if(currentDeployedUnits != null){    
                List<KModuleDeploymentUnitSummary> deployedUnits = new ArrayList<KModuleDeploymentUnitSummary>(currentDeployedUnits);
                List<KModuleDeploymentUnitSummary> filteredDeployedUnits = new ArrayList<KModuleDeploymentUnitSummary>();
                for(KModuleDeploymentUnitSummary ps : deployedUnits){
                    if(ps.getArtifactId().toLowerCase().contains(filter.toLowerCase())
                            || ps.getGroupId().toLowerCase().contains(filter.toLowerCase())){
                        filteredDeployedUnits.add(ps);
                    }
                }
                dataProvider.getList().clear();
                dataProvider.getList().addAll(filteredDeployedUnits);
                dataProvider.refresh();
            }
        }
    
    
    }

    public void refreshDeployedUnits() {
        
        deploymentManager.call(new RemoteCallback<List<KModuleDeploymentUnitSummary>>() {
            @Override
            public void callback(List<KModuleDeploymentUnitSummary> units) {
                currentDeployedUnits = units;
                filterDeployedUnits(view.getCurrentFilter());
                clearSearchEvent.fire(new ClearSearchEvent());
            }
        }).getDeploymentUnits();

    }

    public void addDataDisplay(HasData<KModuleDeploymentUnitSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<KModuleDeploymentUnitSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnReveal
    public void onReveal() {
        refreshDeployedUnits();
    }
    
    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
    
    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( constants.New_Deployment_Unit())
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "New Deployment" );
                        placeManager.goTo( placeRequestImpl );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( constants.Refresh() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshDeployedUnits();
                        view.setCurrentFilter("");
                        view.displayNotification("Deployment Units Refreshed");
                    }
                } )
                .endMenu().build();

    }
    
    public void onSearchEvent(@Observes final DeploymentsSearchEvent searchEvent){
        view.setCurrentFilter(searchEvent.getFilter());
        deploymentManager.call(new RemoteCallback<List<KModuleDeploymentUnitSummary>>() {
            @Override
            public void callback(List<KModuleDeploymentUnitSummary> units) {
                currentDeployedUnits = units;
                filterDeployedUnits(view.getCurrentFilter());
            }
        }).getDeploymentUnits();
    }

}
