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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;

/**
 *
 * @author salaboy
 */
@Dependent
public class FTLTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

  @Override
  protected void initDisplayer() {
    publish(this);
    publishGetFormValues();
  }

  @Override
  public boolean supportsContent(String content) {
    return true;
  }

  // Set up the JS-callable signature as a global JS function.
  protected native void publish(FTLTaskDisplayerImpl td)/*-{
   $wnd.complete = function (from) {
   td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.FTLTaskDisplayerImpl::complete(Ljava/lang/String;)(from);
   }

   $wnd.saveState = function (from) {
   td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.FTLTaskDisplayerImpl::saveState(Ljava/lang/String;)(from);
   }

   
   }-*/;

  protected native void publishGetFormValues() /*-{
   $wnd.getFormValues = function (form) {
   var params = '';

   for (i = 0; i < form.elements.length; i++) {
   var fieldName = form.elements[i].name;
   var fieldValue = form.elements[i].value;
   if (fieldName != '') {
   params += fieldName + '=' + fieldValue + '&';
   }
   }
   return params;
   };
   }-*/;

  public static Map<String, Object> getUrlParameters(String values) {
    Map<String, Object> params = new HashMap<String, Object>();
    for (String param : values.split("&")) {
      String pair[] = param.split("=");
      String key = pair[0];
      String value = "";
      if (pair.length > 1) {
        value = pair[1];
      }
      if (!key.startsWith("btn_")) {
        params.put(key, value);
      }
    }

    return params;
  }
  /*
   * This method is used by JSNI to get the values from the form
   */

  public void complete(String values) {
    final Map<String, Object> params = getUrlParameters(values);
    complete(params);

  }

  public void saveState(String values) {
    final Map<String, Object> params = getUrlParameters(values);
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
    return 1000;
  }
  
  

}
