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

package org.jbpm.workbench.forms.client.display.process;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.forms.display.impl.StaticHTMLFormRenderingSettings;

@Dependent
public class FTLStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer<StaticHTMLFormRenderingSettings> {

    @Override
    public Class<StaticHTMLFormRenderingSettings> getSupportedRenderingSettings() {
        return StaticHTMLFormRenderingSettings.class;
    }

    @Override
    protected void initDisplayer() {
        publish(this);

        jsniHelper.publishGetFormValues();

        jsniHelper.injectFormValidationsScripts(renderingSettings.getFormContent());
    }

    @Override
    public IsWidget getFormWidget() {
        return new HTMLPanel(renderingSettings.getFormContent());
    }

    @Override
    public native void startProcessFromDisplayer() /*-{
        try {
            if ($wnd.eval("taskFormValidator()")) $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
        } catch (err) {
            alert("Unexpected error: " + err);
        }
    }-*/;

    public void startProcess(JavaScriptObject values) {
        final Map<String, Object> params = jsniHelper.getParameters(values);
        processService.call(getStartProcessRemoteCallback(),
                            getUnexpectedErrorCallback())
                .startProcess(serverTemplateId,
                              deploymentId,
                              processDefId,
                              getCorrelationKey(),
                              params);
    }

    protected native void publish(FTLStartProcessDisplayerImpl ftl)/*-{
        $wnd.startProcess = function (form) {
            ftl.@org.jbpm.workbench.forms.client.display.process.FTLStartProcessDisplayerImpl::startProcess(Lcom/google/gwt/core/client/JavaScriptObject;)(form);
        }
    }-*/;
}
