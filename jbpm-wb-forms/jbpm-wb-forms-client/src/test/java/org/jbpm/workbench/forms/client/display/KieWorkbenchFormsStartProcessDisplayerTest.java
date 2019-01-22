/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.forms.client.display.displayer.KieWorkbenchFormDisplayer;
import org.jbpm.workbench.forms.client.display.displayers.pr.AbstractStartProcessFormDisplayerTest;
import org.jbpm.workbench.forms.client.display.process.AbstractStartProcessFormDisplayer;
import org.jbpm.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.forms.display.service.KieWorkbenchFormsEntryPoint;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieWorkbenchFormsStartProcessDisplayerTest extends AbstractStartProcessFormDisplayerTest {
    @Mock
    protected KieWorkbenchFormDisplayer displayer;

    //Specific setting for Issue: JBPM-5333
    @Mock
    protected Caller<KieWorkbenchFormsEntryPoint> service;

    @Mock
    protected KieWorkbenchFormsEntryPoint kieWorkbenchFormsEntryPoint;

    @Mock
    protected KieWorkbenchFormRenderingSettings formRenderingSettings;

    @InjectMocks
    protected KieWorkbenchFormsStartProcessDisplayer kieWorkbenchFormsStartProcessDisplayer;

    @Test
    public void testStartProcessFromDisplayer() {
        when(displayer.isValid()).thenReturn(true);
        when(formRenderingSettings.getTimestamp()).thenReturn(100000l);

        MapModelRenderingContext mapModelRenderingContext = mock(MapModelRenderingContext.class);
        when(formRenderingSettings.getRenderingContext()).thenReturn(mapModelRenderingContext);
        when(service.call(any())).thenReturn(kieWorkbenchFormsEntryPoint);

        ProcessDefinitionKey processDefinitionKey = new ProcessDefinitionKey( "test-serverTemplateId",
                                                                              "test-deploymentId",
                                                                              "test-processId",
                                                                              "test-processDefName");
        ProcessDisplayerConfig processDisplayerConfig = new ProcessDisplayerConfig(processDefinitionKey,
                                                                                   "test",
                                                                                   false);
        processDisplayerConfig.setRenderingSettings(formRenderingSettings);
        kieWorkbenchFormsStartProcessDisplayer.initConfigs(processDisplayerConfig, null, null);
        kieWorkbenchFormsStartProcessDisplayer.startProcessFromDisplayer();

        //Verify that the call is DefaultWorkbenchErrorCallback, not a custom one.
        verify(service).call(any());
        verify(kieWorkbenchFormsEntryPoint).startProcessFromRenderContext(100000l,
                                                                          null,
                                                                          "test-serverTemplateId",
                                                                          "test-deploymentId",
                                                                          "test-processId",
                                                                          "");
    }

    @Override
    public AbstractStartProcessFormDisplayer getStartProcessFormDisplayer() {
        return kieWorkbenchFormsStartProcessDisplayer;
    }
}
