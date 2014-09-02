/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.JSNIFormValuesReader;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {
  
  @Inject
  private ActivityManager activityManager;

  @Inject
  private JSNIFormValuesReader jsniFormValuesReader;
  
  private AbstractWorkbenchScreenActivity currentActivity;

  public PlaceManagerTaskDisplayerImpl() {
  }

  @Override
  protected void initDisplayer() {
    publish(this);
    jsniFormValuesReader.publishGetFormValues();
  }

  @Override
  public boolean supportsContent(String content) {
    return content.contains("handledByPlaceManagerFormProvider");
  }

  public void complete(String values) {
    Map<String, Object> params = jsniFormValuesReader.getUrlParameters(values);
    complete(params);
    close();
  }

  public void saveState(String values) {
    Map<String, Object> params = jsniFormValuesReader.getUrlParameters(values);
    saveState(params);
  }

  @Override
  protected native void completeFromDisplayer()/*-{
    $wnd.complete($wnd.getFormValues($doc.getElementById("form-data")));
  }-*/;

  @Override
  protected native void saveStateFromDisplayer()/*-{
    $wnd.saveState($wnd.getFormValues($doc.getElementById("form-data")));
  }-*/;

  @Override
  protected void startFromDisplayer() {
    start();
  }

  @Override
  protected void claimFromDisplayer() {
    claim();
  }

  @Override
  protected void releaseFromDisplayer() {
    release();
  }

  @Override
  public int getPriority() {
    return 2;
  }

  public void onFormRender(@Observes RenderFormEvent event) {
    String taskName = event.getParams().get("TaskName");
    if (taskName == null || taskName.equals("")) {
      return;
    }
    DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(taskName + " Form", event.getParams());
    Set<Activity> activities = activityManager.getActivities(defaultPlaceRequest);
    if (activities.isEmpty()) {
      return;
    }
    currentActivity = ((AbstractWorkbenchScreenActivity) activities.iterator().next());
    IsWidget widget = currentActivity.getWidget();
    currentActivity.launch(defaultPlaceRequest, null);
    currentActivity.onStartup(defaultPlaceRequest);
    formContainer.clear();
    formContainer.add(widget);
    currentActivity.onOpen();
  }

  @Override
  public void close() {
    super.close(); 
    if(currentActivity != null){
      currentActivity.onClose();
    }
  }

  // Set up the JS-callable signature as a global JS function.
  protected native void publish(PlaceManagerTaskDisplayerImpl td)/*-{
    $wnd.complete = function (from) {
      td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.PlaceManagerTaskDisplayerImpl::complete(Ljava/lang/String;)(from);
    }

    $wnd.saveState = function (from) {
      td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.PlaceManagerTaskDisplayerImpl::saveState(Ljava/lang/String;)(from);
    }
  }-*/;
}
