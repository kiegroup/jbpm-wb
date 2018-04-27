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

package org.jbpm.workbench.forms.display.backend.provider;

import org.jbpm.workbench.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.workbench.forms.service.providing.model.ProcessDefinition;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessFormsValuesProcessorWithWrongValuesTest extends AbstractFormsValuesProcessorWithWrongTypesTest<ProcessFormsValuesProcessor, ProcessRenderingSettings> {

    @Mock
    ProcessDefinition process;

    @Override
    public void init() {
        super.init();

        when(process.getId()).thenReturn("wrongTypes");
    }

    @Override
    ProcessFormsValuesProcessor getProcessorInstance(FormDefinitionSerializer serializer,
                                                     BackendFormRenderingContextManager backendFormRenderingContextManager,
                                                     DynamicBPMNFormGenerator dynamicBPMNFormGenerator) {
        return new ProcessFormsValuesProcessor(new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                                new FormModelSerializer(),
                                                                                new TestMetaDataEntryManager()),
                                               backendFormRenderingContextManager,
                                               dynamicBPMNFormGenerator);
    }

    @Override
    ProcessRenderingSettings getRenderingSettingsWithoutForms() {

        return new ProcessRenderingSettings(process,
                                            variables,
                                            SERVER_TEMPLATE_ID,
                                            null,
                                            marshallerContext);
    }
}
