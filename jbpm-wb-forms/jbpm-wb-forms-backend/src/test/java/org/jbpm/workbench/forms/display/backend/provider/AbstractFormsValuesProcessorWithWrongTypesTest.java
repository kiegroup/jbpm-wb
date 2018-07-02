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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.jbpm.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.workbench.forms.service.providing.RenderingSettings;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.BackendFormRenderingContextManagerImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistry;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshallerRegistryImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.TextAreaFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.models.MultipleSubFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.models.SubFormFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.DateMultipleInputFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.DateMultipleSelectorFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.LocalDateFieldValueMarshaller;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.impl.ContextModelConstraintsExtractorImpl;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime.BPMNRuntimeFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.DynamicBPMNFormGeneratorImpl;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractFormsValuesProcessorWithWrongTypesTest<PROCESSOR extends KieWorkbenchFormsValuesProcessor<SETTINGS>, SETTINGS extends RenderingSettings> {

    private static final String HTML_PARAM = "HTML_CODE";
    private static final String HEADER = "<div class=\"alert alert-warning\" role=\"alert\"><span class=\"pficon pficon-warning-triangle-o\">";

    protected static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String ERROR = "error";
    protected static final String LIST = "list";
    protected static final String WRONG_TYPE = "wrong";

    protected Map<String, String> variables = new HashMap<>();

    @Mock
    ContentMarshallerContext marshallerContext;

    private FieldValueMarshallerRegistry registry;

    DynamicBPMNFormGenerator dynamicBPMNFormGenerator;

    BackendFormRenderingContextManagerImpl backendFormRenderingContextManager;

    BPMNRuntimeFormGeneratorService runtimeFormGeneratorService;

    KieWorkbenchFormRenderingSettings kieWorkbenchFormRenderingSettings;

    SETTINGS renderingSettings;

    PROCESSOR processor;

    @Before
    public void init() {

        variables.put(ID, Long.class.getName());
        variables.put(NAME, String.class.getName());
        variables.put(LIST, List.class.getName());
        variables.put(ERROR, WorkItemHandlerRuntimeException.class.getName());
        variables.put(WRONG_TYPE, "an unexpected and obviously wrong java type");

        SubFormFieldValueMarshaller subFormFieldValueMarshaller = new SubFormFieldValueMarshaller();
        MultipleSubFormFieldValueMarshaller multipleSubFormFieldValueMarshaller = new MultipleSubFormFieldValueMarshaller();

        List<FieldValueMarshaller> marshallers = Arrays.asList(subFormFieldValueMarshaller,
                                                               multipleSubFormFieldValueMarshaller,
                                                               new DateMultipleInputFieldValueMarshaller(),
                                                               new DateMultipleSelectorFieldValueMarshaller(),
                                                               new LocalDateFieldValueMarshaller(),
                                                               new TextAreaFormFieldValueMarshaller());

        Instance<FieldValueMarshaller<?, ?, ?>> marshallersInstance = mock(Instance.class);

        when(marshallersInstance.iterator()).then(proc -> marshallers.iterator());

        registry = new FieldValueMarshallerRegistryImpl(marshallersInstance);

        subFormFieldValueMarshaller.setRegistry(registry);

        multipleSubFormFieldValueMarshaller.setRegistry(registry);

        backendFormRenderingContextManager = new BackendFormRenderingContextManagerImpl(registry, new ContextModelConstraintsExtractorImpl());

        runtimeFormGeneratorService = new BPMNRuntimeFormGeneratorService(new TestFieldManager(), new RawMVELEvaluator());

        dynamicBPMNFormGenerator = new DynamicBPMNFormGeneratorImpl(runtimeFormGeneratorService);

        processor = getProcessorInstance(new FormDefinitionSerializerImpl(new FieldSerializer(), new FormModelSerializer(), new TestMetaDataEntryManager()),
                                         backendFormRenderingContextManager,
                                         dynamicBPMNFormGenerator);

        when(marshallerContext.getClassloader()).thenReturn(this.getClass().getClassLoader());
    }

    @Test
    public void testGenerateRenderingContextWithoutForms() {

        renderingSettings = getRenderingSettingsWithoutForms();

        kieWorkbenchFormRenderingSettings = processor.generateRenderingContext(renderingSettings,
                                                                               true);

        checkGeneratedContext();
    }

    protected void checkGeneratedContext() {
        assertNotNull(kieWorkbenchFormRenderingSettings);

        MapModelRenderingContext formRenderingContext = kieWorkbenchFormRenderingSettings.getRenderingContext();

        assertNotNull(formRenderingContext);

        FormDefinition formDefinition = formRenderingContext.getRootForm();

        assertNotNull(formDefinition);

        assertEquals(2, formDefinition.getFields().size());

        FieldDefinition idFieldDefinition = formDefinition.getFieldByBinding(ID);

        Assertions.assertThat(idFieldDefinition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", ID)
                .hasFieldOrPropertyWithValue("binding", ID)
                .hasFieldOrPropertyWithValue("standaloneClassName", Long.class.getName());

        FieldDefinition nameFieldDefinition = formDefinition.getFieldByBinding(NAME);

        Assertions.assertThat(nameFieldDefinition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", NAME)
                .hasFieldOrPropertyWithValue("binding", NAME)
                .hasFieldOrPropertyWithValue("standaloneClassName", String.class.getName());

        Assertions.assertThat(formDefinition.getFieldByBinding(ERROR))
                .isNull();

        Assertions.assertThat(formDefinition.getFieldByBinding(LIST))
                .isNull();

        Assertions.assertThat(formDefinition.getFieldByBinding(WRONG_TYPE))
                .isNull();

        LayoutComponent formHeader = formDefinition.getLayoutTemplate().getRows().get(0).getLayoutColumns().get(0).getLayoutComponents().get(0);

        Assertions.assertThat(formHeader)
                .isNotNull()
                .hasFieldOrPropertyWithValue("dragTypeName", "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent");

        String headerContent = formHeader.getProperties().get(HTML_PARAM);

        Assertions.assertThat(headerContent)
                .isNotNull()
                .contains(HEADER);
    }

    abstract SETTINGS getRenderingSettingsWithoutForms();

    abstract PROCESSOR getProcessorInstance(FormDefinitionSerializer serializer,
                                            BackendFormRenderingContextManager backendFormRenderingContextManager,
                                            DynamicBPMNFormGenerator dynamicBPMNFormGenerator);
}
