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
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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
public class PlaceManagerStartProcessDisplayerImpl extends FTLStartProcessDisplayerImpl{
  @Inject
  private ActivityManager activityManager;
  
  private AbstractWorkbenchScreenActivity currentActivity;

  public PlaceManagerStartProcessDisplayerImpl() {
    
  }
  
  @Override
  protected void initDisplayer() {
    publish(this);
    publishGetFormValues();
  }


  @Override
  public boolean supportsContent(String content) {
    return content.contains("handledByPlaceManagerFormProvider");
  }

  @Override
  public int getPriority() {
    return 2;
  }

  public void onFormRender(@Observes RenderFormEvent event) {
    String processId = event.getParams().get("processId");
    if (processId == null || processId.equals("")) {
      return;
    }
    DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(processId+" Form", event.getParams());
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
}
