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

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ga.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ht.forms.client.display.displayers.task.AbstractHumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.service.KieWorkbenchFormsEntryPoint;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.uberfire.mvp.Command;

@Dependent
public class KieWorkbenchFormsHumanTaskDisplayer extends AbstractHumanTaskFormDisplayer<KieWorkbenchFormRenderingSettings> {

    private DynamicFormRenderer formRenderer;

    private Caller<KieWorkbenchFormsEntryPoint> service;

    @Inject
    public KieWorkbenchFormsHumanTaskDisplayer( DynamicFormRenderer formRenderer,
                                                Caller<KieWorkbenchFormsEntryPoint> service ) {
        this.formRenderer = formRenderer;
        this.service = service;
    }

    @Override
    public void init( FormDisplayerConfig<TaskKey, KieWorkbenchFormRenderingSettings> config,
                      Command onCloseCommand,
                      Command onRefreshCommand,
                      FormContentResizeListener resizeListener ) {
        super.init( config, onCloseCommand, onRefreshCommand, resizeListener );
    }

    @Override
    protected void initDisplayer() {
        formRenderer.render( renderingSettings.getRenderingContext() );
        formContainer.add( formRenderer );
    }

    @Override
    protected void completeFromDisplayer() {
        if ( formRenderer.isValid() ) {
            service.call( getCompleteTaskRemoteCallback(), getUnexpectedErrorCallback() ).completeTaskFromContext(
                    renderingSettings.getTimestamp(),
                    renderingSettings.getRenderingContext().getModel(),
                    serverTemplateId,
                    deploymentId,
                    taskId );
        }
    }

    @Override
    protected void saveStateFromDisplayer() {
        if ( formRenderer.isValid() ) {
            service.call( getSaveTaskStateCallback(), getUnexpectedErrorCallback() ).saveTaskStateFromRenderContext(
                    renderingSettings.getTimestamp(),
                    renderingSettings.getRenderingContext().getModel(),
                    serverTemplateId,
                    deploymentId,
                    taskId );
        }
    }

    @Override
    protected void startFromDisplayer() {
        service.call( response -> start() ).clearContext( renderingSettings.getTimestamp() );
    }

    @Override
    protected void claimFromDisplayer() {
        service.call( response -> claim() ).clearContext( renderingSettings.getTimestamp() );
    }

    @Override
    protected void releaseFromDisplayer() {
        service.call( response -> release() ).clearContext( renderingSettings.getTimestamp() );
    }

    @Override
    protected void clearRenderingSettings() {
        service.call().clearContext( renderingSettings.getTimestamp() );
        super.clearRenderingSettings();
    }

    @Override
    public Class<KieWorkbenchFormRenderingSettings> getSupportedRenderingSettings() {
        return KieWorkbenchFormRenderingSettings.class;
    }
}
