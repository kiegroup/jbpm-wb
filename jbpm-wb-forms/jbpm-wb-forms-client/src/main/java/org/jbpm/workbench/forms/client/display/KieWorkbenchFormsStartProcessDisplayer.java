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

package org.jbpm.workbench.forms.client.display;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jbpm.workbench.forms.client.display.displayer.KieWorkbenchFormDisplayer;
import org.jbpm.workbench.forms.client.display.process.AbstractStartProcessFormDisplayer;
import org.jbpm.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.workbench.forms.display.service.KieWorkbenchFormsEntryPoint;

@Dependent
public class KieWorkbenchFormsStartProcessDisplayer extends AbstractStartProcessFormDisplayer<KieWorkbenchFormRenderingSettings> {

    private KieWorkbenchFormDisplayer formDisplayer;

    private Caller<KieWorkbenchFormsEntryPoint> service;

    @Inject
    public KieWorkbenchFormsStartProcessDisplayer(KieWorkbenchFormDisplayer formDisplayer,
                                                  Caller<KieWorkbenchFormsEntryPoint> service) {
        this.formDisplayer = formDisplayer;
        this.service = service;
    }

    @Override
    public Class<KieWorkbenchFormRenderingSettings> getSupportedRenderingSettings() {
        return KieWorkbenchFormRenderingSettings.class;
    }

    @Override
    protected void initDisplayer() {
        formDisplayer.show(renderingSettings.getRenderingContext(), renderingSettings.isDefaultForms());
    }

    @Override
    public IsWidget getFormWidget() {
        return ElementWrapperWidget.getWidget(formDisplayer.getElement());
    }

    @Override
    public void startProcessFromDisplayer() {
        if (formDisplayer.isValid()) {
            service.call(getStartProcessRemoteCallback(),
                         getUnexpectedErrorCallback()).startProcessFromRenderContext(
                    renderingSettings.getTimestamp(),
                    renderingSettings.getRenderingContext().getModel(),
                    serverTemplateId,
                    deploymentId,
                    processDefId,
                    getCorrelationKey());
        }
    }
}
