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

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.workbench.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayer;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

@Dependent
public class FormModellerStartProcessDisplayerImpl extends AbstractStartProcessFormDisplayer<FormModelerFormRenderingSettings> {

    private static final String ACTION_START_PROCESS = "startProcess";

    private FormRendererWidget formRenderer;

    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    protected String action;

    @Inject
    public FormModellerStartProcessDisplayerImpl( FormRendererWidget formRenderer, Caller<FormModelerProcessStarterEntryPoint> renderContextServices ) {
        this.formRenderer = formRenderer;
        this.renderContextServices = renderContextServices;
    }

    @Override
    public Class<FormModelerFormRenderingSettings> getSupportedRenderingSettings() {
        return FormModelerFormRenderingSettings.class;
    }

    protected void initDisplayer() {
        formRenderer.loadContext( renderingSettings.getContextId() );

        formRenderer.setVisible( true );
    }

    @Override
    public IsWidget getFormWidget() {
        return formRenderer;
    }

    public void startProcessFromDisplayer() {
        submitForm( ACTION_START_PROCESS );
    }

    protected void submitForm( String action ) {
        this.action = action;
        formRenderer.submitFormAndPersist();
    }

    @Override
    public void close() {
        renderContextServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {
                renderingSettings = null;
                FormModellerStartProcessDisplayerImpl.super.close();
            }
        } ).clearContext( renderingSettings.getContextId() );
    }

    public void onFormSubmitted( @Observes FormSubmittedEvent event ) {
        if ( renderingSettings == null ) {
            return;
        }
        if ( event.isMine( renderingSettings.getContextId() ) &&
                event.getContext().getErrors() == 0 &&
                ACTION_START_PROCESS.equals( action ) ) {
            renderContextServices.call( getStartProcessRemoteCallback(), getUnexpectedErrorCallback() )
                    .startProcessFromRenderContext( renderingSettings.getContextId(), serverTemplateId, deploymentId, processDefId, getCorrelationKey(), parentProcessInstanceId );
        }
    }

    public void onFormResized( @Observes ResizeFormcontainerEvent event ) {
        if ( renderingSettings == null ) {
            return;
        }
        if ( event.isMine( renderingSettings.getContextId() ) ) {
            formRenderer.resize( event.getWidth(), event.getHeight() );
            if ( resizeListener != null ) {
                resizeListener.resize( event.getWidth(), event.getHeight() );
            }
        }
    }

}
