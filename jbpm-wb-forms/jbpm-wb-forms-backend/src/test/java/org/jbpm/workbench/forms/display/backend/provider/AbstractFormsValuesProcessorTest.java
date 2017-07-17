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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Instance;

import org.jbpm.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.workbench.forms.display.backend.provider.model.Invoice;
import org.jbpm.workbench.forms.display.backend.provider.model.InvoiceLine;
import org.jbpm.workbench.forms.service.providing.RenderingSettings;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.workbench.common.forms.commons.shared.layout.impl.DynamicFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.BackendFormRenderingContextManagerImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.FormValuesProcessorImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.MultipleSubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.SubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.impl.ContextModelConstraintsExtractorImpl;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime.BPMNRuntimeFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.DynamicBPMNFormGeneratorImpl;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.serialization.impl.FormModelSerializer;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractFormsValuesProcessorTest<PROCESSOR extends KieWorkbenchFormsValuesProcessor<SETTINGS>, SETTINGS extends RenderingSettings> {

    static final String ID = "id";
    static final String NAME = "name";
    static final String ADDRESS = "address";
    static final String PRODUCT = "product";
    static final String QUANTITY = "quantity";
    static final String PRICE = "price";
    static final String TOTAL = "total";
    static final String CLIENT = "client";
    static final String LINES = "lines";
    static final String COMMENTS = "comments";
    static final String DATE = "date";
    static final String INVOICE = "invoice";

    static final int EXPECTED_MODEL_VALIDATIONS = 3;
    static final int EXPECTED_FORMS = 4;

    @Mock
    ContentMarshallerContext marshallerContext;

    FormValuesProcessor formValuesProcessor;

    DynamicBPMNFormGenerator dynamicBPMNFormGenerator;

    BackendFormRenderingContextManagerImpl backendFormRenderingContextManager;

    BPMNRuntimeFormGeneratorService runtimeFormGeneratorService;

    KieWorkbenchFormRenderingSettings kieWorkbenchFormRenderingSettings;

    SETTINGS renderingSettings;

    PROCESSOR processor;

    @Before
    public void init() {
        List<FieldValueProcessor> processors = Arrays.asList(new SubFormFieldValueProcessor(),
                                                             new MultipleSubFormFieldValueProcessor());

        Instance<FieldValueProcessor<? extends FieldDefinition, ?, ?>> fieldValueProcessors = mock(Instance.class);
        when(fieldValueProcessors.iterator()).then(proc -> processors.iterator());

        formValuesProcessor = new FormValuesProcessorImpl(fieldValueProcessors);

        backendFormRenderingContextManager = new BackendFormRenderingContextManagerImpl(formValuesProcessor,
                                                                                        new ContextModelConstraintsExtractorImpl());

        runtimeFormGeneratorService = new BPMNRuntimeFormGeneratorService(new TestFieldManager(),
                                                                          new DynamicFormLayoutTemplateGenerator());

        dynamicBPMNFormGenerator = new DynamicBPMNFormGeneratorImpl(runtimeFormGeneratorService);

        processor = getProcessorInstance(new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                          new FormModelSerializer()),
                                         backendFormRenderingContextManager,
                                         dynamicBPMNFormGenerator);

        when(marshallerContext.getClassloader()).thenReturn(this.getClass().getClassLoader());
    }

    @Test
    public void testGenerateRenderingContextWithExistingForms() {
        Map<String, String> formData = new HashMap<>();

        formData.put("invoice",
                     Invoice.class.getName());

        renderingSettings = getFullRenderingSettings();

        kieWorkbenchFormRenderingSettings = processor.generateRenderingContext(renderingSettings);

        checkGeneratedContext();
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

        assertFalse(formRenderingContext.getModelConstraints().isEmpty());
        assertEquals(EXPECTED_MODEL_VALIDATIONS,
                     formRenderingContext.getModelConstraints().size());

        assertFalse(formRenderingContext.getAvailableForms().isEmpty());
        assertEquals(EXPECTED_FORMS,
                     formRenderingContext.getAvailableForms().size());
    }

    @Test
    public void testProcessFormValues() {
        testGenerateRenderingContextWithExistingForms();

        Map<String, Object> formValues = getFormValues();

        Map<String, Object> outputValues = processor.generateRuntimeValuesMap(kieWorkbenchFormRenderingSettings.getTimestamp(),
                                                                              formValues);

        assertNotNull(outputValues);
        assertFalse(outputValues.isEmpty());

        assertNotNull(outputValues.get(INVOICE));
        assertTrue(outputValues.get(INVOICE) instanceof Invoice);

        Invoice invoice = (Invoice) outputValues.get(INVOICE);

        Map<String, Object> invoiceMap = (Map<String, Object>) formValues.get(INVOICE);

        Map<String, Object> clientMap = (Map<String, Object>) invoiceMap.get(CLIENT);

        assertNotNull(invoice.getClient());
        assertEquals(clientMap.get("id"),
                     invoice.getClient().getId());
        assertEquals(clientMap.get("name"),
                     invoice.getClient().getName());
        assertEquals(clientMap.get("address"),
                     invoice.getClient().getAddress());

        List<Map<String, Object>> linesMap = (List<Map<String, Object>>) invoiceMap.get(LINES);

        assertNotNull(invoice.getLines());
        assertEquals(linesMap.size(),
                     invoice.getLines().size());

        Map<String, Object> lineMap = linesMap.get(0);
        InvoiceLine line = invoice.getLines().get(0);

        assertEquals(lineMap.get("product"),
                     line.getProduct());
        assertEquals(lineMap.get("quantity"),
                     line.getQuantity());
        assertEquals(lineMap.get("price"),
                     line.getPrice());
        assertEquals(lineMap.get("total"),
                     line.getTotal());

        assertEquals(invoiceMap.get("comments"),
                     invoice.getComments());
        assertEquals(invoiceMap.get("total"),
                     invoice.getTotal());
        assertEquals(invoiceMap.get("date"),
                     invoice.getDate());
    }

    protected Map<String, Object> getFormValues() {
        Map<String, Object> formValues = new HashMap<>();

        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put(ID,
                      new Long(1234));
        clientMap.put(NAME,
                      "John Snow");
        clientMap.put(ADDRESS,
                      "Winterfell");

        List<Map<String, Object>> linesMap = new ArrayList<>();

        Map<String, Object> lineMap = new HashMap<>();

        lineMap.put(PRODUCT,
                    "Really Dangerous Sword");
        lineMap.put(QUANTITY,
                    1);
        lineMap.put(PRICE,
                    100.5);
        lineMap.put(TOTAL,
                    100.5);

        linesMap.add(lineMap);

        Map<String, Object> invoiceMap = new HashMap<>();
        invoiceMap.put(CLIENT,
                       clientMap);
        invoiceMap.put(LINES,
                       linesMap);
        invoiceMap.put(TOTAL,
                       100.5);
        invoiceMap.put(COMMENTS,
                       "Everything was perfect");
        invoiceMap.put(DATE,
                       new Date());

        formValues.put(INVOICE,
                       invoiceMap);

        return formValues;
    }

    abstract SETTINGS getFullRenderingSettings();

    abstract SETTINGS getRenderingSettingsWithoutForms();

    abstract PROCESSOR getProcessorInstance(FormDefinitionSerializer serializer,
                                            BackendFormRenderingContextManager backendFormRenderingContextManager,
                                            DynamicBPMNFormGenerator dynamicBPMNFormGenerator);
}
