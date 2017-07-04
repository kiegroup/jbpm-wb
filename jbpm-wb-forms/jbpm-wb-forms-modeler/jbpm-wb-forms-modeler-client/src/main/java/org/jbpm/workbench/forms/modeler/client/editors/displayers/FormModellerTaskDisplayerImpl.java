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

package org.jbpm.workbench.forms.modeler.client.editors.displayers;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.forms.client.display.task.AbstractHumanTaskFormDisplayer;
import org.jbpm.workbench.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.workbench.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

@Dependent
public class FormModellerTaskDisplayerImpl extends AbstractHumanTaskFormDisplayer<FormModelerFormRenderingSettings> {

    private static final String ACTION_SAVE_TASK = "saveTask";
    private static final String ACTION_COMPLETE_TASK = "completeTask";

    private FormRendererWidget formRenderer;

    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    private String action;

    @Inject
    public FormModellerTaskDisplayerImpl(FormRendererWidget formRenderer,
                                         Caller<FormModelerProcessStarterEntryPoint> renderContextServices) {
        this.formRenderer = formRenderer;
        this.renderContextServices = renderContextServices;
    }

    @Override
    protected void initDisplayer() {
        formRenderer.loadContext(renderingSettings.getContextId());

        formRenderer.setVisible(true);

        formContainer.add(formRenderer.asWidget());
    }

    @Override
    public Class<FormModelerFormRenderingSettings> getSupportedRenderingSettings() {
        return FormModelerFormRenderingSettings.class;
    }

    @Override
    protected void completeFromDisplayer() {
        submitForm(ACTION_COMPLETE_TASK);
    }

    @Override
    protected void saveStateFromDisplayer() {
        submitForm(ACTION_SAVE_TASK);
    }

    @Override
    protected void startFromDisplayer() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                start();
            }
        }).clearContext(renderingSettings.getContextId());
    }

    @Override
    protected void claimFromDisplayer() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                claim();
            }
        }).clearContext(renderingSettings.getContextId());
    }

    @Override
    protected void releaseFromDisplayer() {
        renderContextServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                release();
            }
        }).clearContext(renderingSettings.getContextId());
    }

    protected void submitForm(String action) {
        this.action = action;
        formRenderer.submitFormAndPersist();
    }

    @Override
    protected void clearRenderingSettings() {
        renderContextServices.call().clearContext(renderingSettings.getContextId());
        super.clearRenderingSettings();
    }

    public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (renderingSettings == null) {
            return;
        }
        if (event.isMine(renderingSettings.getContextId()) && event.getContext().getErrors() == 0) {
            if (ACTION_SAVE_TASK.equals(action)) {
                renderContextServices.call(getSaveTaskStateCallback(),
                                           getUnexpectedErrorCallback()).saveTaskStateFromRenderContext(renderingSettings.getContextId(),
                                                                                                        serverTemplateId,
                                                                                                        deploymentId,
                                                                                                        taskId);
            } else if (ACTION_COMPLETE_TASK.equals(action)) {
                renderContextServices.call(getCompleteTaskRemoteCallback(),
                                           getUnexpectedErrorCallback()).completeTaskFromContext(renderingSettings.getContextId(),
                                                                                                 serverTemplateId,
                                                                                                 deploymentId,
                                                                                                 taskId);
            }
        }
    }

    public void onFormResized(@Observes ResizeFormcontainerEvent event) {
        if (renderingSettings == null) {
            return;
        }
        if (event.isMine(renderingSettings.getContextId())) {
            formRenderer.resize(event.getWidth(),
                                event.getHeight());
            if (resizeListener != null) {
                resizeListener.resize(event.getWidth(),
                                      event.getHeight());
            }
        }
    }
}
