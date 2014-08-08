/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.editors.diagram;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.ga.model.process.DummyProcessPath;
import org.kie.uberfire.client.common.MultiPageEditorView;
import org.kie.uberfire.client.common.Page;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.security.Identity;
import com.github.gwtbootstrap.client.ui.Modal;

@Dependent
@WorkbenchPopup(identifier = "Process Diagram Popup")
public class ProcessDiagramPopupPresenter {

  private Constants constants = GWT.create(Constants.class);

  public interface PopupView extends UberView<ProcessDiagramPopupPresenter> {

    FlowPanel getContainer();
  }

  @Inject
  private PopupView view;

  @Inject
  private Identity identity;

  @Inject
  private PlaceManager placeManager;

  @Inject
  private Event<BeforeClosePlaceEvent> closePlaceEvent;

  private PlaceRequest place;

  @Inject
  protected ActivityManager activityManager;

  @PostConstruct
  public void init() {
    
  }

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    this.place = place;
  }

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Process_Model();
  }

  @WorkbenchPartView
  public UberView<ProcessDiagramPopupPresenter> getView() {
    return view;
  }

  @OnOpen
  public void onOpen() {
    final String deploymentId = place.getParameter("deploymentId", "");
    final String processId = place.getParameter("processId", "");
    final String activeNodes = place.getParameter("activeNodes", "");
    final String completedNodes = place.getParameter("completedNodes", "");
    Modal designerView = ((com.github.gwtbootstrap.client.ui.Modal)view.getContainer().getParent().getParent().getParent().getParent());
    designerView.setWidth("1000px");
    designerView.setHeight("600px");
    designerView.getElement().getStyle().setMarginLeft(-500, Style.Unit.PX);
    designerView.getElement().getStyle().setMarginTop(-300, Style.Unit.PX);
    renderDesigner(deploymentId, processId, activeNodes, completedNodes);

  }

  private void renderDesigner(final String deploymentId, final String processId,
                              final String activeNodes,  final String completedNodes) {

    view.getContainer().clear();
    DummyProcessPath dummyProcessPath = new DummyProcessPath(processId);
    PathPlaceRequest defaultPlaceRequest = new PathPlaceRequest(dummyProcessPath);
    //Set Parameters here: 
    defaultPlaceRequest.addParameter("readOnly", "true");
    if(!activeNodes.equals("")){
      defaultPlaceRequest.addParameter("activeNodes", activeNodes);
    }
    if(!completedNodes.equals("")){
      defaultPlaceRequest.addParameter("completedNodes", completedNodes);
    }
    defaultPlaceRequest.addParameter("processId", processId);
    defaultPlaceRequest.addParameter("deploymentId", deploymentId);

    AbstractWorkbenchActivity activity = null;

    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
    activity = (AbstractWorkbenchActivity) activities.iterator().next();

    IsWidget widget = activity.getWidget();
    activity.launch(place, null);
    if (activity instanceof AbstractWorkbenchEditorActivity) {
      ((AbstractWorkbenchEditorActivity) activity).onStartup(new ObservablePathImpl().wrap(dummyProcessPath), defaultPlaceRequest);
    }
    
    
    Widget asWidget = ((MultiPageEditorView)widget.asWidget()).getTabContent().asWidget();
    
    Widget widget1 = ((com.github.gwtbootstrap.client.ui.TabPane)
            ((com.google.gwt.user.client.ui.ComplexPanel)asWidget).getWidget(0)).getWidget(0);
    
    Widget widget2 = ((com.google.gwt.user.client.ui.ComplexPanel)widget1).getWidget(0);
    Widget widget3 = ((Page.PageView)widget2).getSp().getWidget();
    widget3.setSize("1000px", "600px");
    view.getContainer().add(widget3);
    activity.onOpen();
  }
  
  
  public void close() {
    closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
  }

}
