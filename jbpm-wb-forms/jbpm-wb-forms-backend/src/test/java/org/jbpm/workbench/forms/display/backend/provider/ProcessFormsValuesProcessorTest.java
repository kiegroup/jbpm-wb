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

package org.jbpm.workbench.forms.display.backend.provider;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workbench.forms.display.backend.provider.model.Invoice;
import org.jbpm.workbench.forms.display.backend.provider.util.FormContentReader;
import org.jbpm.workbench.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.workbench.forms.service.providing.model.ProcessDefinition;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.serialization.impl.FormModelSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessFormsValuesProcessorTest extends AbstractFormsValuesProcessorTest<ProcessFormsValuesProcessor, ProcessRenderingSettings> {

    @Mock
    ProcessDefinition process;

    @Override
    public void init() {
        super.init();

        when(process.getId()).thenReturn("invoices");
    }

    @Override
    ProcessFormsValuesProcessor getProcessorInstance(FormDefinitionSerializer serializer,
                                                     BackendFormRenderingContextManager backendFormRenderingContextManager,
                                                     DynamicBPMNFormGenerator dynamicBPMNFormGenerator) {
        return new ProcessFormsValuesProcessor(new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                                new FormModelSerializer()),
                                               backendFormRenderingContextManager,
                                               dynamicBPMNFormGenerator);
    }

    @Override
    ProcessRenderingSettings getFullRenderingSettings() {
        return getRenderSettings(FormContentReader.getStartProcessForms());
    }

    @Override
    ProcessRenderingSettings getRenderingSettingsWithoutForms() {
        return getRenderSettings(null);
    }

    private ProcessRenderingSettings getRenderSettings(String formContent) {
        Map<String, String> formData = new HashMap<>();

        formData.put("invoice",
                     Invoice.class.getName());

        return new ProcessRenderingSettings(process,
                                            formData,
                                            SERVER_TEMPLATE_ID,
                                            formContent,
                                            marshallerContext);
    }
}
