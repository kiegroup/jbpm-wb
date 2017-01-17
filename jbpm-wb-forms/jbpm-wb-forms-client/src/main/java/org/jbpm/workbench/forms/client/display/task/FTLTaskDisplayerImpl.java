/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.forms.client.display.task;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.jbpm.workbench.forms.display.impl.StaticHTMLFormRenderingSettings;

@Dependent
public class FTLTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer<StaticHTMLFormRenderingSettings> {

    @Override
    protected void initDisplayer() {
        publish(this);
        jsniHelper.publishGetFormValues();

        jsniHelper.injectFormValidationsScripts( renderingSettings.getFormContent() );

        formContainer.clear();
        formContainer.add( new HTMLPanel( renderingSettings.getFormContent() ) );
        if (resizeListener != null) resizeListener.resize(formContainer.getOffsetWidth(), formContainer.getOffsetHeight());
    }

    @Override
    public Class<StaticHTMLFormRenderingSettings> getSupportedRenderingSettings() {
        return StaticHTMLFormRenderingSettings.class;
    }

    // Set up the JS-callable signature as a global JS function.
    protected native void publish(FTLTaskDisplayerImpl td)/*-{
        $wnd.complete = function (from) {
            td.@org.jbpm.workbench.forms.client.display.task.FTLTaskDisplayerImpl::complete(Lcom/google/gwt/core/client/JavaScriptObject;)(from);
        }

        $wnd.saveState = function (from) {
            td.@org.jbpm.workbench.forms.client.display.task.FTLTaskDisplayerImpl::saveState(Lcom/google/gwt/core/client/JavaScriptObject;)(from);
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
        try {
            if($wnd.eval("taskFormValidator()")) $wnd.complete($wnd.getFormValues($doc.getElementById("form-data")));
        } catch (err) {
            alert("Unexpected error: " + err);
        }
    }-*/;

    @Override
    protected native void saveStateFromDisplayer()/*-{
        try {
            if($wnd.eval("taskFormValidator()")) $wnd.saveState($wnd.getFormValues($doc.getElementById("form-data")));
        } catch (err) {
            alert("Unexpected error: " + err);
        }
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
}
