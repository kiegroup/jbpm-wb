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
package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details Multi", preferredWidth = 500)
public class ProcessInstanceDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

  public interface ProcessInstanceDetailsMultiView
          extends TabbedDetailsView<ProcessInstanceDetailsMultiPresenter> {
  }

  @Inject
  public ProcessInstanceDetailsMultiView view;

  private Constants constants = GWT.create(Constants.class);

  @Inject
  private Caller<KieSessionEntryPoint> kieSessionServices;

  @Inject
  private Caller<DataServiceEntryPoint> dataServices;

  private String selectedDeploymentId = "";

  private int selectedProcessInstanceStatus = 0;
  
  private String selectedProcessDefName = "";

  public ProcessInstanceDetailsMultiPresenter() {

  }

  @WorkbenchPartView
  public UberView<ProcessInstanceDetailsMultiPresenter> getView() {
    return view;
  }

  @DefaultPosition
  public Position getPosition() {
    return Position.EAST;
  }

  @Override
  public void selectDefaultTab() {
    goToProcessInstanceDetailsTab();
  }

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Details();
  }

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    super.onStartup(place);
    selectDefaultTab();
  }

  public void onProcessSelectionEvent(@Observes ProcessInstanceSelectionEvent event) {
    selectedItemId = String.valueOf(event.getProcessInstanceId());
    selectedItemName = event.getProcessDefId();
    selectedDeploymentId = event.getDeploymentId();
    selectedProcessInstanceStatus = event.getProcessInstanceStatus();
    selectedProcessDefName = event.getProcessDefName();
    view.getHeaderPanel().clear();
    
    view.getHeaderPanel().add(new HTMLPanel(SafeHtmlUtils.htmlEscape(String.valueOf(selectedItemId) + " - " 
                    + selectedProcessDefName + " (" + getProcessStatusString(selectedProcessInstanceStatus) + ")")));
    view.getTabPanel().selectTab(0);
    selectDefaultTab();

  }
  
  private String getProcessStatusString(int processStatus){
    String processStatusString = "";
    if(selectedProcessInstanceStatus == ProcessInstance.STATE_ACTIVE){
      processStatusString = "ACTIVE";
    }else if(selectedProcessInstanceStatus == ProcessInstance.STATE_ABORTED){
      processStatusString = "ABORTED";
    }else if(selectedProcessInstanceStatus == ProcessInstance.STATE_COMPLETED){
      processStatusString = "COMPLETED";
    } else if(selectedProcessInstanceStatus == ProcessInstance.STATE_PENDING){
      processStatusString = "PENDING";
    }else if(selectedProcessInstanceStatus == ProcessInstance.STATE_SUSPENDED){
      processStatusString = "SUSPENDED";
    }
    return processStatusString;
  }

  public void goToProcessInstanceDetailsTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Process Instance Details";
      ((HTMLPanel) view.getTabPanel().getWidget(0)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("processInstanceId", selectedItemId);
      defaultPlaceRequest.addParameter("processDefId", selectedItemName);
      defaultPlaceRequest.addParameter("deploymentId", selectedDeploymentId);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);

      ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);

      ((HTMLPanel) view.getTabPanel().getWidget(0)).add(widget);
      activity.onOpen();
    }
  }

  public void goToProcessInstanceModelTab() {
    if (place != null && !selectedItemId.equals("")) {

      dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
        @Override
        public void callback(List<NodeInstanceSummary> activeNodes) {
          final StringBuffer nodeParam = new StringBuffer();
          for (NodeInstanceSummary activeNode : activeNodes) {
            nodeParam.append(activeNode.getNodeUniqueName() + ",");
          }
          if (nodeParam.length() > 0) {
            nodeParam.deleteCharAt(nodeParam.length() - 1);
          }

          dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> completedNodes) {
              StringBuffer completedNodeParam = new StringBuffer();
              for (NodeInstanceSummary completedNode : completedNodes) {
                if (completedNode.isCompleted()) {
                  // insert outgoing sequence flow and node as this is for on entry event
                  completedNodeParam.append(completedNode.getNodeUniqueName() + ",");
                  completedNodeParam.append(completedNode.getConnection() + ",");
                } else if (completedNode.getConnection() != null) {
                  // insert only incoming sequence flow as node id was already inserted
                  completedNodeParam.append(completedNode.getConnection() + ",");
                }

              }
              completedNodeParam.deleteCharAt(completedNodeParam.length() - 1);
              DummyProcessPath dummyProcessPath = new DummyProcessPath(selectedItemName);
              PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest(dummyProcessPath);

              defaultPlaceRequest.addParameter("activeNodes", nodeParam.toString());
              defaultPlaceRequest.addParameter("completedNodes", completedNodeParam.toString());
              defaultPlaceRequest.addParameter("readOnly", "true");
              defaultPlaceRequest.addParameter("processId", selectedItemName);
              defaultPlaceRequest.addParameter("deploymentId", selectedDeploymentId);

              ((HTMLPanel) view.getTabPanel().getWidget(1)).clear();

              AbstractWorkbenchActivity activity = null;

              if (activitiesMap.get(selectedItemName) == null) {
                Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                activity = (AbstractWorkbenchActivity) activities.iterator().next();
              } else {
                activity = activitiesMap.get(selectedItemName);
              }
              IsWidget widget = activity.getWidget();
              activity.launch(place, null);
              if (activity instanceof AbstractWorkbenchScreenActivity) {
                ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
              } else if (activity instanceof AbstractWorkbenchEditorActivity) {
                ((AbstractWorkbenchEditorActivity) activity).onStartup(new ObservablePathImpl().wrap(dummyProcessPath), defaultPlaceRequest);
              }
              ((HTMLPanel) view.getTabPanel().getWidget(1)).add(widget);
              activity.onOpen();

            }
          }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
              ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
              return true;
            }
          }).getProcessInstanceCompletedNodes(Long.parseLong(selectedItemId));

        }
      }, new ErrorCallback<Message>() {
        @Override
        public boolean error(Message message, Throwable throwable) {
          ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
          return true;
        }
      }).getProcessInstanceActiveNodes(Long.parseLong(selectedItemId));
    }
  }

  public void goToProcessInstanceVariables() {

    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Process Variables List";
      ((HTMLPanel) view.getTabPanel().getWidget(2)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("processInstanceId", selectedItemId);
      defaultPlaceRequest.addParameter("processDefId", selectedItemName);
      defaultPlaceRequest.addParameter("deploymentId", selectedDeploymentId);
      defaultPlaceRequest.addParameter("processInstanceStatus", String.valueOf(selectedProcessInstanceStatus));

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);

      ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);

      ((HTMLPanel) view.getTabPanel().getWidget(2)).add(widget);
      activity.onOpen();
    }

  }

  public void signalProcessInstance() {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
    placeRequestImpl.addParameter("processInstanceId", selectedItemId);
    placeManager.goTo(placeRequestImpl);

  }

  public void abortProcessInstance() {
    if (Window.confirm("Are you sure that you want to abort the process instance?")) {
      final long processInstanceId = Long.parseLong(selectedItemId);
      kieSessionServices.call(new RemoteCallback<Void>() {
        @Override
        public void callback(Void v) {
//                            refreshProcessInstanceData(view.getProcessDeploymentText().getText(),
//                                    view.getProcessInstanceIdText().getText(),
//                                    view.getProcessDefinitionIdText().getText());
//                            processInstancesUpdatedEvent.fire(new ProcessInstancesUpdateEvent(processInstanceId));
//                            view.displayNotification(constants.Aborting_Process_Instance() + "(id=" + processInstanceId + ")");

        }
      }, new ErrorCallback<Message>() {
        @Override
        public boolean error(Message message, Throwable throwable) {
          ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
          return true;
        }
      }).abortProcessInstance(processInstanceId);
    }
  }

  @OnClose
  public void onClose() {
    super.onClose();
  }
}
