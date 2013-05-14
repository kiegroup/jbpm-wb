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

package org.jbpm.console.ng.bd.client.editors.session.list;

import com.google.gwt.core.client.GWT;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import java.util.List;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Deployments")
public class KieSessionsListPresenter {

    public interface KieSessionsListView extends UberView<KieSessionsListPresenter> {

        void displayNotification(String text);

        void showBusyIndicator(String message);

        void hideBusyIndicator();

        void cleanForm();
    }

    @Inject
    private KieSessionsListView view;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    private Constants constants = GWT.create(Constants.class);

    private ListDataProvider<KModuleDeploymentUnitSummary> dataProvider = new ListDataProvider<KModuleDeploymentUnitSummary>();

    public KieSessionsListPresenter() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Deployment_Units();
    }

    @WorkbenchPartView
    public UberView<KieSessionsListPresenter> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
    }

    public void deployUnit(final String id, final String group, final String artifact, final String version, final String kbaseName, final String kieSessionName) {
        view.showBusyIndicator(constants.Please_Wait());
        deploymentManager.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                view.cleanForm();
                view.hideBusyIndicator();
                view.displayNotification(" Kjar Deployed " + group + ":" + artifact + ":" + version);
                refreshDeployedUnits();
            }
        }, new ErrorCallback() {
           @Override
           public boolean error(Message message, Throwable throwable) {
               view.cleanForm();
               view.hideBusyIndicator();
               view.displayNotification("Error: Deploy failed " + throwable.getMessage());
               return true;
           }
       }).deploy(new KModuleDeploymentUnitSummary(id, group, artifact, version, kbaseName, kieSessionName));
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
       }).undeploy(new KModuleDeploymentUnitSummary(id, group, artifact, version, kbaseName, kieSessionName));
    }

    public void refreshDeployedUnits() {
        deploymentManager.call(new RemoteCallback<List<KModuleDeploymentUnitSummary>>() {
            @Override
            public void callback(List<KModuleDeploymentUnitSummary> ids) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll(ids);
                dataProvider.refresh();
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

}
