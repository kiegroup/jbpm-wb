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

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import javax.enterprise.event.Event;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceCreated;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Process Definition List")
public class ProcessDefinitionListPresenter {

    public interface InboxView extends UberView<ProcessDefinitionListPresenter> {

        void displayNotification(String text);

        TextBox getSessionIdText();

        DataGrid<ProcessSummary> getDataGrid();

        void showBusyIndicator(String message);

        void hideBusyIndicator();
    }

    @Inject
    private InboxView view;

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    private Caller<DeploymentManagerEntryPoint> deploymentManager;

    @Inject
    Event<ProcessInstanceCreated> processInstanceCreatedEvents;
    private ListDataProvider<ProcessSummary> dataProvider = new ListDataProvider<ProcessSummary>();

    private Constants constants = GWT.create(Constants.class);

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Definitions();
    }

    @WorkbenchPartView
    public UberView<ProcessDefinitionListPresenter> getView() {
        return view;
    }

    public ProcessDefinitionListPresenter() {
    }

    public void refreshProcessList(final String filter) {

        if (filter != null && !filter.equals("")) {
            dataServices.call(new RemoteCallback<List<ProcessSummary>>() {
                @Override
                public void callback(List<ProcessSummary> processes) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(processes);
                    dataProvider.refresh();
                }
            }).getProcessesByFilter(filter);
        } else {
            dataServices.call(new RemoteCallback<List<ProcessSummary>>() {
                @Override
                public void callback(List<ProcessSummary> processes) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(processes);
                    dataProvider.refresh();
                }
            }).getProcesses();
        }
    }

    public void reloadRepository() {

        view.showBusyIndicator(constants.Please_Wait());
        deploymentManager.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void organizations) {
                refreshProcessList(null);
                view.hideBusyIndicator();
                view.displayNotification(constants.Processes_Refreshed_From_The_Repo());
            }
        }, new ErrorCallback() {

            @Override
            public boolean error(Message message, Throwable throwable) {
                view.hideBusyIndicator();
                view.displayNotification("Error: Process refreshed from repository failed");
                return true;
            }
        }).redeploy();

    }

    public void addDataDisplay(HasData<ProcessSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<ProcessSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnReveal
    public void onReveal() {
        refreshProcessList("");
    }

}
