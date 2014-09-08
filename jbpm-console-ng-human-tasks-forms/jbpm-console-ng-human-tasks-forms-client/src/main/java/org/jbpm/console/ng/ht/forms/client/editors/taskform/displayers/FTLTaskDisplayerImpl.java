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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.enterprise.context.Dependent;
import java.util.Map;

/**
 *
 * @author salaboy
 */
@Dependent
public class FTLTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer {

    @Override
    protected void initDisplayer() {
        publish(this);
        jsniHelper.publishGetFormValues();
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
            td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.FTLTaskDisplayerImpl::complete(Lcom/google/gwt/core/client/JavaScriptObject;)(from);
        }

        $wnd.saveState = function (from) {
            td.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.FTLTaskDisplayerImpl::saveState(Lcom/google/gwt/core/client/JavaScriptObject;)(from);
        }
    }-*/;
  /*
   * This method is used by JSNI to get the values from the form
   */

    public void complete(JavaScriptObject values) {
        final Map<String, Object> params = jsniHelper.getParameters(values);
        complete(params);
        close();
    }

    public void saveState(JavaScriptObject values) {
        final Map<String, Object> params = jsniHelper.getParameters(values);
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
