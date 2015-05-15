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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;

import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.paging.PageResponse;

@Dependent
@WorkbenchScreen(identifier = "Process Instance List")
public class ProcessInstanceListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {
  public static String FILTER_STATE_PARAM_NAME = "states";
  public static String FILTER_PROCESS_DEFINITION_PARAM_NAME = "currentProcessDefinition";
  public static String FILTER_INITIATOR_PARAM_NAME = "initiator";



  public interface ProcessInstanceListView extends ListView<ProcessInstanceSummary, ProcessInstanceListPresenter> {

  }

  @Inject
  private ProcessInstanceListView view;

  @Inject
  private Caller<ProcessInstanceService> processInstanceService;
  @Inject
  private Caller<KieSessionEntryPoint> kieSessionServices;


  private String currentProcessDefinition;

  private List<Integer> currentActiveStates;

  private String initiator;

  private Constants constants = GWT.create(Constants.class);

  public ProcessInstanceListPresenter() {
   super();
  }


  public void filterGrid(  ArrayList<Integer> states, String currentProcessDefinition,String initiator ) {
    this.currentActiveStates= states;
    this.currentProcessDefinition = currentProcessDefinition;
    this.initiator= initiator ;
    refreshGrid(  );
  }

  @Override
  protected ListView getListView() {
    return view;
  }

  @Override
  public void getData(Range visibleRange) {
    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
    if (currentFilter == null) {
      currentFilter = new PortableQueryFilter(visibleRange.getStart(),
              visibleRange.getLength(),
              false, "",
              (columnSortList.size() > 0) ? columnSortList.get(0)
                      .getColumn().getDataStoreName() : "",
              (columnSortList.size() > 0) ? columnSortList.get(0)
                      .isAscending() : true);
    }
    // If we are refreshing after a search action, we need to go back to offset 0
    if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
            || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
      currentFilter.setOffset(visibleRange.getStart());
      currentFilter.setCount(visibleRange.getLength());
    } else {
      currentFilter.setOffset(0);
      currentFilter.setCount(view.getListGrid().getPageSize());
    }
    //Applying screen specific filters
    if (currentFilter.getParams() == null) {
      currentFilter.setParams(new HashMap<String, Object>());
    }
    if ( initiator != null && initiator.trim().length() > 0 ) {
      currentFilter.getParams().put( FILTER_INITIATOR_PARAM_NAME, initiator );
    } else {
      currentFilter.getParams().remove( FILTER_INITIATOR_PARAM_NAME );
    }
    currentFilter.getParams().put(FILTER_STATE_PARAM_NAME, currentActiveStates);

    currentFilter.getParams().put(FILTER_PROCESS_DEFINITION_PARAM_NAME, currentProcessDefinition);

    currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
            .getColumn().getDataStoreName() : "");
    currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
            .isAscending() : true);

    processInstanceService.call(new RemoteCallback<PageResponse<ProcessInstanceSummary>>() {
      @Override
      public void callback(PageResponse<ProcessInstanceSummary> response) {
        updateDataOnCallback(response);
      }
    }, new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        view.hideBusyIndicator();
        view.displayNotification("Error: Getting Process Definitions: " + message);
        GWT.log(throwable.toString());
        return true;
      }
    }).getData(currentFilter);
  }

  public void newInstanceCreated(@Observes NewProcessInstanceEvent pi) {
    refreshGrid();
  }

  public void newInstanceCreated(@Observes ProcessInstancesUpdateEvent pis) {
    refreshGrid();
  }

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    this.place = place;
  }

  @OnFocus
  public void onFocus() {
    refreshGrid();
  }

  @OnOpen
  public void onOpen() {
    this.currentProcessDefinition = place.getParameter("processName", "");
    refreshGrid();
  }

  public void abortProcessInstance(long processInstanceId) {
    kieSessionServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void v) {
        refreshGrid(  );
      }
    }, new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
        return true;
      }
    }).abortProcessInstance(processInstanceId);
  }

  public void abortProcessInstance(List<Long> processInstanceIds) {
    kieSessionServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void v) {
        refreshGrid();
      }
    }, new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
        return true;
      }
    }).abortProcessInstances(processInstanceIds);
  }

  public void suspendProcessInstance(String processDefId,
          long processInstanceId) {
    kieSessionServices.call(new RemoteCallback<Void>() {
      @Override
      public void callback(Void v) {
        refreshGrid(  );

      }
    }, new ErrorCallback<Message>() {
      @Override
      public boolean error(Message message, Throwable throwable) {
        ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
        return true;
      }
    }).suspendProcessInstance(processInstanceId);
  }

  public void bulkSignal(List<ProcessInstanceSummary> processInstances) {
    StringBuilder processIdsParam = new StringBuilder();
    if (processInstances != null) {

      for (ProcessInstanceSummary selected : processInstances) {
        if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
          view.displayNotification(constants.Signaling_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                  + ")");
          continue;
        }
        processIdsParam.append(selected.getId() + ",");
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

  public void bulkAbort(List<ProcessInstanceSummary> processInstances) {
    if (processInstances != null) {
      if (Window.confirm("Are you sure that you want to abort the selected process instances?")) {
        List<Long> ids = new ArrayList<Long>();
        for (ProcessInstanceSummary selected : processInstances) {
          if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
            view.displayNotification(constants.Aborting_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                    + ")");
            continue;
          }
          ids.add(selected.getProcessInstanceId());

          view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")");
        }
        abortProcessInstance(ids);

      }
    }
  }


  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Process_Instances();
  }

  @WorkbenchPartView
  public UberView<ProcessInstanceListPresenter> getView() {
    return view;
  }
}
