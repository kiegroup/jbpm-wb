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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
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
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = "Task Details Multi", preferredWidth = 500)
public class TaskDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

  public interface TaskDetailsMultiView
          extends TabbedDetailsView<TaskDetailsMultiPresenter> {
  }

  @Inject
  public TaskDetailsMultiView view;

  private Constants constants = GWT.create(Constants.class);

  public TaskDetailsMultiPresenter() {

  }

  @WorkbenchPartView
  public UberView<TaskDetailsMultiPresenter> getView() {
    return view;
  }

  @DefaultPosition
  public Position getPosition() {
    return Position.EAST;
  }

  @Override
  public void selectDefaultTab() {
    goToTaskFormTab();
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

  public void onTaskSelectionEvent(@Observes TaskSelectionEvent event) {
    selectedItemId = String.valueOf(event.getTaskId());
    selectedItemName = event.getTaskName();
    view.getHeaderPanel().clear();
    view.getHeaderPanel().add(new HTMLPanel(SafeHtmlUtils.htmlEscape(String.valueOf(selectedItemId) + " - " + selectedItemName)));
    view.getTabPanel().selectTab(0);
    selectDefaultTab();

  }

  public void goToTaskDetailsTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Task Details";
      ((HTMLPanel) view.getTabPanel().getWidget(1)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("taskId", selectedItemId);
      defaultPlaceRequest.addParameter("taskName", selectedItemName);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = (AbstractWorkbenchActivity) activities.iterator().next();

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);
      if (activity instanceof AbstractWorkbenchScreenActivity) {
        ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
      } else if (activity instanceof AbstractWorkbenchScreenActivity) {
        ((AbstractWorkbenchScreenActivity) activity).onStartup(place);
      }
      ((HTMLPanel) view.getTabPanel().getWidget(1)).add(widget);
      activity.onOpen();
    }
  }

  public void goToTaskFormTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Form Display";
      ((HTMLPanel) view.getTabPanel().getWidget(0)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("taskId", selectedItemId);
      defaultPlaceRequest.addParameter("taskName", selectedItemName);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = ((AbstractWorkbenchActivity) activities.iterator().next());

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);
      if (activity instanceof AbstractWorkbenchScreenActivity) {
        ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
      } else if (activity instanceof AbstractWorkbenchScreenActivity) {
        ((AbstractWorkbenchScreenActivity) activity).onStartup(place);
      }
      ((HTMLPanel) view.getTabPanel().getWidget(0)).add(widget);
      activity.onOpen();
    }
  }

  public void goToTaskAssignmentsTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Task Assignments";
      ((HTMLPanel) view.getTabPanel().getWidget(2)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("taskId", selectedItemId);
      defaultPlaceRequest.addParameter("taskName", selectedItemName);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = (AbstractWorkbenchActivity) activities.iterator().next();

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

  public void goToTaskCommentsTab() {
    if (place != null) {
      String placeToGo = "Task Comments";
      ((HTMLPanel) view.getTabPanel().getWidget(3)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("taskId", selectedItemId);
      defaultPlaceRequest.addParameter("taskName", selectedItemName);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = (AbstractWorkbenchActivity) activities.iterator().next();

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);
      ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
      
      ((HTMLPanel) view.getTabPanel().getWidget(3)).add(widget);
      activity.onOpen();
    }
  }
  
  public void goToTaskLogsTab() {
    if (place != null && !selectedItemId.equals("")) {
      String placeToGo = "Task Logs";
      ((HTMLPanel) view.getTabPanel().getWidget(4)).clear();
      DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(placeToGo);
      //Set Parameters here: 
      defaultPlaceRequest.addParameter("taskId", selectedItemId);
      defaultPlaceRequest.addParameter("taskName", selectedItemName);

      AbstractWorkbenchActivity activity = null;
      if (activitiesMap.get(placeToGo) == null) {
        Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
        activity = (AbstractWorkbenchActivity) activities.iterator().next();

      } else {
        activity = activitiesMap.get(placeToGo);
      }
      IsWidget widget = activity.getWidget();
      activity.launch(place, null);
      ((AbstractWorkbenchScreenActivity) activity).onStartup(defaultPlaceRequest);
      
      ((HTMLPanel) view.getTabPanel().getWidget(4)).add(widget);
      activity.onOpen();
    }
  }

}
