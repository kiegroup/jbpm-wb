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

import com.google.gwt.user.client.ui.HTMLPanel;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.util.JSNIFormValuesReader;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author salaboy
 */
@Dependent
public class FTLTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {
  @Inject
  private JSNIFormValuesReader jsniFormValuesReader;

  @Override
  protected void initDisplayer() {
    publish(this);
    jsniFormValuesReader.publishGetFormValues();
    formContainer.clear();
    formContainer.add(new HTMLPanel(formContent));
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
  /*
   * This method is used by JSNI to get the values from the form
   */

  public void complete(String values) {
    final Map<String, Object> params = jsniFormValuesReader.getUrlParameters(values);
    complete(params);
    close();
  }

  public void saveState(String values) {
    final Map<String, Object> params = jsniFormValuesReader.getUrlParameters(values);
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
