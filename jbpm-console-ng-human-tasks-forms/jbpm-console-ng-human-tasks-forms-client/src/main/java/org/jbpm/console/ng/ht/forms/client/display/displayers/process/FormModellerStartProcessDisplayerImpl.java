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
package org.jbpm.console.ng.ht.forms.client.display.displayers.process;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ht.forms.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

/**
 *
 * @author salaboy
 */
@Dependent
public class FormModellerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer {

    private static final String ACTION_START_PROCESS = "startProcess";

    @Inject
    private FormRendererWidget formRenderer;

    @Inject
    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    protected String action;

    protected void initDisplayer() {
        formRenderer.loadContext(formContent);

        formRenderer.setVisible(true);

        formContainer.add(formRenderer.asWidget());
    }

    public void startProcessFromDisplayer() {
        submitForm(ACTION_START_PROCESS);
    }

    protected void submitForm(String action) {
        this.action = action;
        formRenderer.submitFormAndPersist();
    }

    @Override
    public boolean supportsContent(String content) {
        return formRenderer.isValidContextUID(content);
    }

    @Override
    public void close() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                formContent = null;
                FormModellerStartProcessDisplayerImpl.super.close();
            }
        }).clearContext(formContent);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (event.isMine(formContent)) {
            if (event.getContext().getErrors() == 0) {
                if (ACTION_START_PROCESS.equals(action)) {
                    renderContextServices.call(getStartProcessRemoteCallback(), getUnexpectedErrorCallback())
                            .startProcessFromRenderContext(formContent, deploymentId, processDefId);
                }
            }
        }
    }

    public void onFormResized(@Observes ResizeFormcontainerEvent event) {
        if (event.isMine(formContent)) {
            formRenderer.resize(event.getWidth(), event.getHeight());
            if (resizeListener != null) resizeListener.resize(event.getWidth(), event.getHeight());
        }
    }

}
