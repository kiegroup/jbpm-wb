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
package org.jbpm.console.ng.client.editors.process.instance.list;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.TextBox;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import javax.enterprise.event.Observes;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.events.ProcessInstanceCreated;
import org.jbpm.console.ng.shared.model.ProcessInstanceSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Process Instance List")
public class ProcessInstanceListPresenter {

    public interface InboxView
            extends
            UberView<ProcessInstanceListPresenter> {

        void displayNotification(String text);

        TextBox getSessionIdText();

        DataGrid<ProcessInstanceSummary> getDataGrid();
        
    }
    @Inject
    private InboxView view;
    @Inject
    private Caller<KnowledgeDomainServiceEntryPoint> knowledgeServices;
    private ListDataProvider<ProcessInstanceSummary> dataProvider = new ListDataProvider<ProcessInstanceSummary>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Instance List";
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceListPresenter> getView() {
        return view;
    }

    public ProcessInstanceListPresenter() {
    }

    @PostConstruct
    public void init() {
    }

    public void refreshProcessList(final String sessionId) {

        if(sessionId != null && !sessionId.equals("")){
            knowledgeServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
                @Override
                public void callback(List<ProcessInstanceSummary> processInstances) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(processInstances);
                    dataProvider.refresh();
                }
            }).getProcessInstancesBySessionId(sessionId);
        }else{
            knowledgeServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
                @Override
                public void callback(List<ProcessInstanceSummary> processInstances) {
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(processInstances);
                    dataProvider.refresh();
                }
            }).getProcessInstances();
        }
        



    }
    
    public void newInstanceCreated(@Observes ProcessInstanceCreated pi){
        refreshProcessList("");
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
    
    @OnReveal
    public void onReveal() {
       refreshProcessList("");
    }
    
}
