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
package org.jbpm.console.ng.gc.client.experimental.generic.popup;


import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jbpm.console.ng.ga.model.process.DummyProcessPath;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.security.Identity;


@Dependent
@WorkbenchPopup(identifier = "Generic Popup")
public class GenericPopupPresenter {

  private Constants constants = GWT.create(Constants.class);

  public interface PopupView extends UberView<GenericPopupPresenter> {

    FlowPanel getContainer();
  }

  @Inject
  private PopupView view;

  @Inject
  private Identity identity;

  @Inject
  private PlaceManager placeManager;

  private PlaceRequest place;

  @Inject
  protected ActivityManager activityManager;
  
  @Inject
  private Event<ChangeTitleWidgetEvent> changeTitleNotification;
  
  @Inject
  private Event<BeforeClosePlaceEvent> closePlaceEvent;
  
  private String placeToGo;
  
  
  @PostConstruct
  public void init() {
    
  }

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    this.place = place;
  }

  @WorkbenchPartTitle
  public String getTitle() {
    return constants.New_Item();
  }

  @WorkbenchPartView
  public UberView<GenericPopupPresenter> getView() {
    return view;
  }

  @OnOpen
  public void onOpen() {
    placeToGo = place.getParameter("placeToGo", "");
    final String type = place.getParameter("type", ""); // editor or screen
    String name = place.getParameter("name", ""); 
    String key = place.getParameter("key", ""); 
    String params = place.getParameter("params", ""); 
    changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, name , null ) );
    Modal modal = ((com.github.gwtbootstrap.client.ui.Modal)view.getContainer().getParent().getParent().getParent().getParent());
    modal.getElement().getStyle().setMarginTop(-270, Style.Unit.PX);
    modal.setTitle(name);
    renderContent(placeToGo, type, key, params);

  }
  
  

  private void renderContent(final String placeToGo, final String type, 
                             final String key, final String params) {

    view.getContainer().clear();
    PlaceRequest placeRequest = null;
    DummyProcessPath dummyProcessPath = null;
    if(type.equals("editor")){
      dummyProcessPath = new DummyProcessPath(key);
      placeRequest = new PathPlaceRequest(dummyProcessPath);
    }else if(type.equals("screen")){
      placeRequest = new DefaultPlaceRequest(placeToGo);
    }
    
    //Set Parameters here:
    String[] paramArray = params.split(",");
    for(int i = 0; i < paramArray.length; i = i + 2){
      placeRequest.addParameter(paramArray[i], paramArray[i+1]);
    }
    placeRequest.addParameter("onClose", "Generic Popup");


    Set<Activity> activities = activityManager.getActivities(placeRequest);
    if(activities.size() > 0){
      AbstractWorkbenchActivity activity = (AbstractWorkbenchActivity) activities.iterator().next();
      if(activity != null){
        IsWidget widget = activity.getWidget();
        activity.launch(placeRequest, null);
        if (type.equals("editor")) {
          ((AbstractWorkbenchEditorActivity) activity).onStartup(new ObservablePathImpl().wrap(dummyProcessPath), placeRequest);
        }else if(type.equals("screen")){
          ((AbstractWorkbenchScreenActivity) activity).onStartup(placeRequest);
        }

        view.getContainer().add(widget);
        activity.onOpen();
        
      }else{
        GWT.log("The activity is null we cannot proceed");
      }
    }else{
      GWT.log("There is no activity for the requested place");
    }
  }
  
  public void onChildClosePlaceEvent(@Observes BeforeClosePlaceEvent closeEvent){
    if(closeEvent.getPlace().getIdentifier().equals(placeToGo)){
      close();
    }
  }
  
  public void close() {
    closePlaceEvent.fire(new BeforeClosePlaceEvent(GenericPopupPresenter.this.place));
  }
}
