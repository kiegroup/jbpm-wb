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
package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.JavaScriptObject;
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

        Accordion accordion = new Accordion();


        AccordionGroup accordionGroupCorrelation = new AccordionGroup();
        accordionGroupCorrelation.addHiddenHandler(new HiddenHandler() {
            @Override
            public void onHidden(HiddenEvent hiddenEvent) {
                hiddenEvent.stopPropagation();
            }
        });
        accordionGroupCorrelation.setHeading(constants.Correlation_Key());
        accordionGroupCorrelation.setDefaultOpen(false);
        accordionGroupCorrelation.add(correlationKey);
        accordion.add(accordionGroupCorrelation);


        AccordionGroup accordionGroupForm = new AccordionGroup();
        accordionGroupForm.addHiddenHandler(new HiddenHandler() {
            @Override
            public void onHidden(HiddenEvent hiddenEvent) {
                hiddenEvent.stopPropagation();
            }
        });
        accordionGroupForm.setHeading(constants.Form());
        accordionGroupForm.setDefaultOpen(true);
        accordionGroupForm.add(new HTMLPanel(formContent));
        accordion.add(accordionGroupForm);

        jsniHelper.injectFormValidationsScripts(formContent);

        formContainer.add(accordion);

    }



    @Override
    public native void startProcessFromDisplayer() /*-{
        try {
            if($wnd.eval("taskFormValidator()")) $wnd.startProcess($wnd.getFormValues($doc.getElementById("form-data")));
        } catch (err) {
            alert("Unexpected error: " + err);
        }
    }-*/;

    public void startProcess(JavaScriptObject values) {
        final Map<String, Object> params = jsniHelper.getParameters(values);
        sessionServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                .startProcess(deploymentId, processDefId, getCorrelationKey(), params);
    }

    protected native void publish(FTLStartProcessDisplayerImpl ftl)/*-{
        $wnd.startProcess = function (form) {
            ftl.@org.jbpm.console.ng.pr.forms.client.display.displayers.process.FTLStartProcessDisplayerImpl::startProcess(Lcom/google/gwt/core/client/JavaScriptObject;)(form);
        }
    }-*/;
}
