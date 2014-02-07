/*
* Copyright 2012 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.jbpm.console.ng.bd.client.editors.deployment.list;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.gc.client.list.base.BasePresenter;
import org.jbpm.console.ng.ht.model.events.SearchEvent;
import org.kie.workbench.common.widgets.client.search.ClearSearchEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchScreen(identifier = "Deployments List")
public class DeploymentUnitsListPresenter extends BasePresenter<KModuleDeploymentUnitSummary, DeploymentUnitsListViewImpl> {
    
    public interface DeploymentUnitsListView extends UberView<DeploymentUnitsListPresenter> {

        void showBusyIndicator(String message);

        void hideBusyIndicator();

    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public UberView<DeploymentUnitsListPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Deployment_Units();
    }

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    @Inject
    private Event<ClearSearchEvent> clearSearchEvent;

    private Constants constants = GWT.create(Constants.class);
    
    @PostConstruct
    public void init() {
        super.NEW_ITEM_MENU = constants.New_Deployment_Unit();
        super.makeMenuBar();
    }

    public void undeployUnit(final String id, final String group, final String artifact, final String version,
            final String kbaseName, final String kieSessionName) {
        view.showBusyIndicator(constants.Please_Wait());
        deploymentManager.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.hideBusyIndicator();
                view.displayNotification(" Kjar Undeployed " + group + ":" + artifact + ":" + version);
                refreshItems();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.hideBusyIndicator();
                view.displayNotification("Error: Undeploy failed, check Problems panel");
                return true;
            }
        }).undeploy(new KModuleDeploymentUnitSummary(id, group, artifact, version, kbaseName, kieSessionName, null));
    }

    @Override
    public void refreshItems() {
        view.setCurrentFilter("");
        deploymentManager.call(new RemoteCallback<List<KModuleDeploymentUnitSummary>>() {
            @Override
            public void callback(List<KModuleDeploymentUnitSummary> units) {
                allItemsSummaries = units;
                filterItems(view.getCurrentFilter(), view.getListGrid());
                clearSearchEvent.fire(new ClearSearchEvent());
                view.setCurrentFilter( "" );
                view.displayNotification( constants.Deployed_Units_Refreshed() );
            }
        }).getDeploymentUnits();
    }

    @Override
    protected void onSearchEvent(SearchEvent searchEvent) {
        view.setCurrentFilter(searchEvent.getFilter());
        deploymentManager.call(new RemoteCallback<List<KModuleDeploymentUnitSummary>>() {
            @Override
            public void callback(List<KModuleDeploymentUnitSummary> units) {
                allItemsSummaries = units;
                filterItems(view.getCurrentFilter(), view.getListGrid());
            }
        }).getDeploymentUnits();
    }

    @Override
    protected void createItem() {
        GWT.log("create item in presenter ");
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("New Deployment");
        placeManager.goTo(placeRequestImpl);
    }

    @Override
    protected void readItem(Long id) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void updateItem(Long id) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void deleteItem(Long id) {
        // TODO Auto-generated method stub
    }

}