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
package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.DummyProcessPath;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
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
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = "Process Details Multi", preferredWidth = 500)
public class ProcessDefDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

  public interface ProcessDefDetailsMultiView
          extends TabbedDetailsView<ProcessDefDetailsMultiPresenter> {
  }

  @Inject
  public ProcessDefDetailsMultiView view;

  private Constants constants = GWT.create(Constants.class);

  @Inject
  private Caller<ProcessDefinitionService> processDefService;

  @Inject
  private Caller<VFSService> fileServices;

  public ProcessDefDetailsMultiPresenter() {

  }

  @WorkbenchPartView
  public UberView<ProcessDefDetailsMultiPresenter> getView() {
    return view;
  }

  @DefaultPosition
  public Position getPosition() {
    return Position.EAST;
  }

  @Override
  public void selectDefaultTab() {
    goToProcessDefDetailsTab();
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

  public void onProcessSelectionEvent(@Observes ProcessDefSelectionEvent event) {
    selectedItemId = event.getDeploymentId();
    selectedItemName = event.getProcessId();
    view.getHeaderPanel().clear();
    view.getHeaderPanel().add(new HTMLPanel(SafeHtmlUtils.htmlEscape(String.valueOf(selectedItemId) + " - " + selectedItemName)));
    view.getTabPanel().selectTab(0);
    selectDefaultTab();

  }

  public void goToProcessDefDetailsTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Process Definition Details";
      ((HTMLPanel) view.getTabPanel().getWidget(0)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("deploymentId", String.valueOf(selectedItemId));
      defaultPlaceRequest.addParameter("processDefId", selectedItemName);

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

  public void createNewProcessInstance() {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display Popup");
    placeRequestImpl.addParameter("processId", selectedItemName);
    placeRequestImpl.addParameter("domainId", selectedItemId);
    placeRequestImpl.addParameter("processName", selectedItemName);
    placeManager.goTo(placeRequestImpl);
  }

  public void goToProcessDefModelTab() {
    if (place != null && !selectedItemId.equals("")) {

      processDefService.call(new RemoteCallback<ProcessSummary>() {
        @Override
        public void callback(ProcessSummary process) {
          if (process != null) {

            if (process.getOriginalPath() != null) {
              fileServices.call(new RemoteCallback<Path>() {
                @Override
                public void callback(Path processPath) {
                  ((HTMLPanel) view.getTabPanel().getWidget(1)).clear();
                  PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest(processPath);
                  //Set Parameters here: 
                  defaultPlaceRequest.addParameter("readOnly", "true");

                  defaultPlaceRequest.addParameter("processId", selectedItemName);
                  defaultPlaceRequest.addParameter("deploymentId", selectedItemId);

                  AbstractWorkbenchActivity activity = null;

                  if (activitiesMap.get(processPath) == null) {
                    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                    activity = (AbstractWorkbenchActivity) activities.iterator().next();

                  } else {
                    activity = activitiesMap.get(processPath);
                  }
                  IsWidget widget = activity.getWidget();
                  activity.launch(place, null);
                  if (activity instanceof AbstractWorkbenchScreenActivity) {
                    ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
                  } else if (activity instanceof AbstractWorkbenchEditorActivity) {
                    ((AbstractWorkbenchEditorActivity) activity).onStartup(new ObservablePathImpl().wrap(processPath));
                  }
                  ((HTMLPanel) view.getTabPanel().getWidget(1)).add(widget);
                  activity.onOpen();

                }
              }).get(process.getOriginalPath());
            } else {

              ((HTMLPanel) view.getTabPanel().getWidget(1)).clear();
              DummyProcessPath dummyProcessPath = new DummyProcessPath(process.getProcessDefId());
              PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest(dummyProcessPath);
              //Set Parameters here: 
              defaultPlaceRequest.addParameter("readOnly", "true");

              defaultPlaceRequest.addParameter("processId", selectedItemName);
              defaultPlaceRequest.addParameter("deploymentId", selectedItemId);

              AbstractWorkbenchActivity activity = null;

              if (activitiesMap.get(process.getProcessDefId()) == null) {
                Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
                activity = (AbstractWorkbenchActivity) activities.iterator().next();
              } else {
                activity = activitiesMap.get(process.getProcessDefId());
              }
              IsWidget widget = activity.getWidget();
              activity.launch(place, null);
              if (activity instanceof AbstractWorkbenchScreenActivity) {
                ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
              } else if (activity instanceof AbstractWorkbenchEditorActivity) {
                ((AbstractWorkbenchEditorActivity) activity).onStartup(new ObservablePathImpl().wrap(dummyProcessPath),defaultPlaceRequest);
              }
              ((HTMLPanel) view.getTabPanel().getWidget(1)).add(widget);
              activity.onOpen();
            }

          } else {
            // set to null to ensure it's clear state

          }
        }
      }, new ErrorCallback<Message>() {
        @Override
        public boolean error(Message message, Throwable throwable) {
          ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
          return true;
        }
      }).getItem(new ProcessDefinitionKey(selectedItemId, selectedItemName));

    }
  }


  public void viewProcessInstances() {
    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Instances");
    placeRequestImpl.addParameter("processName", selectedItemName);
    placeManager.goTo(placeRequestImpl);
  }
}
