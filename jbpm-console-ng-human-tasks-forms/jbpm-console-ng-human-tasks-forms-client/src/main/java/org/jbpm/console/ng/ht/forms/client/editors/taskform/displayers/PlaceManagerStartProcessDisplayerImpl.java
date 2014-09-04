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
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.PlaceManagerFormActivitySearcher;
import org.jbpm.console.ng.ht.model.events.RenderFormEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Map;

/**
 *
 * @author salaboy
 */
@Dependent
public class PlaceManagerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

  @Inject
  private Caller<KieSessionEntryPoint> sessionServices;

  @Inject
  private PlaceManagerFormActivitySearcher placeManagerFormActivitySearcher;

  public PlaceManagerStartProcessDisplayerImpl() {
    
  }
    @Override
  protected native void startProcessFromDisplayer() /*-{
    $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
  }-*/;

  @Override
  protected void initDisplayer() {
    publish(this);
    jsniHelper.publishGetFormValues();
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

    IsWidget widget = placeManagerFormActivitySearcher.findFormActivityWidget(processId, event.getParams());

    formContainer.clear();
    if (widget != null) {
      formContainer.add(widget);
    }
  }
  
  @Override
  public void close() {
    super.close();
    placeManagerFormActivitySearcher.closeFormActivity();
  }

  public void startProcess(String values) {
    final Map<String, Object> params = jsniHelper.getUrlParameters(values);
    sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
            .startProcess(deploymentId, processDefId, params);
  }

  protected native void publish(PlaceManagerStartProcessDisplayerImpl ftl)/*-{
    $wnd.startProcess = function (from) {
      ftl.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.PlaceManagerStartProcessDisplayerImpl::startProcess(Ljava/lang/String;)(from);
    }
  }-*/;
}
