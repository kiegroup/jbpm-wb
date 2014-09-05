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
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

/**
 *
 * @author salaboy
 */
@Dependent
public class FTLStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    @Inject
    private Caller<KieSessionEntryPoint> sessionServices;

    @Override
    public boolean supportsContent(String content) {
        return true;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    protected void initDisplayer() {
        publish(this);
        jsniHelper.publishGetFormValues();
        formContainer.clear();
        formContainer.add(new HTMLPanel(formContent));
    }

    @Override
    protected native void startProcessFromDisplayer() /*-{
        $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
    }-*/;

    public void startProcess(String values) {
        final Map<String, Object> params = jsniHelper.getUrlParameters(values);
        sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                .startProcess(deploymentId, processDefId, params);
    }

    protected native void publish(FTLStartProcessDisplayerImpl ftl)/*-{
        $wnd.startProcess = function (from) {
            ftl.@org.jbpm.console.ng.ht.forms.client.editors.taskform.displayers.FTLStartProcessDisplayerImpl::startProcess(Ljava/lang/String;)(from);
        }
    }-*/;
}
