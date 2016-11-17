/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.workbench.forms.display.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayer;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.service.KieWorkbenchFormsEntryPoint;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;

@Dependent
public class KieWorkbenchFormsStartProcessDisplayer extends AbstractStartProcessFormDisplayer<KieWorkbenchFormRenderingSettings> {

    private DynamicFormRenderer formRenderer;

    private Caller<KieWorkbenchFormsEntryPoint> service;

    @Inject
    public KieWorkbenchFormsStartProcessDisplayer( DynamicFormRenderer formRenderer,
                                                   Caller<KieWorkbenchFormsEntryPoint> service ) {
        this.formRenderer = formRenderer;
        this.service = service;
    }

    @Override
    public Class<KieWorkbenchFormRenderingSettings> getSupportedRenderingSettings() {
        return KieWorkbenchFormRenderingSettings.class;
    }

    @Override
    protected void initDisplayer() {
        formRenderer.render( renderingSettings.getRenderingContext() );
    }

    @Override
    public IsWidget getFormWidget() {
        return formRenderer;
    }

    @Override
    public void startProcessFromDisplayer() {
        if ( formRenderer.isValid() ) {
            service.call( getStartProcessRemoteCallback(), getUnexpectedErrorCallback() ).startProcessFromRenderContext(
                    renderingSettings.getTimestamp(),
                    renderingSettings.getRenderingContext().getModel(),
                    serverTemplateId,
                    deploymentId,
                    processDefId,
                    getCorrelationKey() );
        }
    }
}
