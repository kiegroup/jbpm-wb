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



import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.core.client.GWT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import javax.enterprise.event.Observes;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceCreated;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Instance List")
public class ProcessInstanceListPresenter {

  public interface ProcessInstanceListView
          extends
          UberView<ProcessInstanceListPresenter> {

    void displayNotification(String text);

    String getFilterProcessText();

    void setFilterProcessText(String processText);
    
    DataGrid<ProcessInstanceSummary> getDataGrid();

    void setAvailableProcesses(Collection<ProcessInstanceSummary> processes);
  }
  private String currentProcessDefinition;
  private PlaceRequest place;
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

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Process_Instances();
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
  
  public void refreshActiveProcessList() {
      List<Integer> states = new ArrayList<Integer>();
      states.add(ProcessInstance.STATE_ACTIVE);
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processInstances) {
          dataProvider.getList().clear();
          dataProvider.getList().addAll(processInstances);
          dataProvider.refresh();
        }
      }).getProcessInstances(states, view.getFilterProcessText(), null);
  }

  public void refreshRelatedToMeProcessList() {
      List<Integer> states = new ArrayList<Integer>();
      states.add(ProcessInstance.STATE_ACTIVE);
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processInstances) {
          dataProvider.getList().clear();
          dataProvider.getList().addAll(processInstances);
          dataProvider.refresh();
        }
      }).getProcessInstances(states, view.getFilterProcessText(), identity.getName());
  }
  
  public void refreshAbortedProcessList() {
      List<Integer> states = new ArrayList<Integer>();
      states.add(ProcessInstance.STATE_ABORTED);
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processInstances) {
          dataProvider.getList().clear();
          dataProvider.getList().addAll(processInstances);
          dataProvider.refresh();
        }
      }).getProcessInstances(states, view.getFilterProcessText(), null);
  }
  
  public void refreshCompletedProcessList() {
      List<Integer> states = new ArrayList<Integer>();
      states.add(ProcessInstance.STATE_COMPLETED);
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processInstances) {
          dataProvider.getList().clear();
          dataProvider.getList().addAll(processInstances);
          dataProvider.refresh();
        }
      }).getProcessInstances(states, view.getFilterProcessText(), null);
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

  @OnStart
  public void onStart(final PlaceRequest place) {
    this.place = place;
  }

  @OnReveal
  public void onReveal() {

    this.currentProcessDefinition = place.getParameter("processDefId", "");
    listProcessInstances();
    refreshActiveProcessList();
  }

  public void abortProcessInstance(String processDefId, long processInstanceId) {
    kieSessionServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void v) {
        refreshActiveProcessList();

      }
    }).abortProcessInstance(processInstanceId);
  }
  
  public void suspendProcessInstance(String processDefId, long processInstanceId) {
    kieSessionServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void v) {
        refreshActiveProcessList();

      }
    }).suspendProcessInstance(processInstanceId);
  }

  public void listProcessInstances() {
    
    if (!this.currentProcessDefinition.equals("")) {
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processes) {
          view.setAvailableProcesses(processes);

        }
      }).getProcessInstancesByProcessDefinition(this.currentProcessDefinition);
    } else {
      dataServices.call(new RemoteCallback<List<ProcessInstanceSummary>>() {
        @Override
        public void callback(List<ProcessInstanceSummary> processes) {
          view.setAvailableProcesses(processes);

        }
      }).getProcessInstances();

    }

  }
}
