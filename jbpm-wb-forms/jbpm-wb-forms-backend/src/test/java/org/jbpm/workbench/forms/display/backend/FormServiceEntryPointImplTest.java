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

package org.jbpm.workbench.forms.display.backend;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.apache.commons.io.IOUtils;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.jbpm.workbench.forms.display.backend.provider.DefaultKieWorkbenchFormsProvider;
import org.jbpm.workbench.forms.display.backend.provider.KieWorkbenchFormsProvider;
import org.jbpm.workbench.forms.display.backend.provider.ProcessFormsValuesProcessor;
import org.jbpm.workbench.forms.display.backend.provider.TaskFormValuesProcessor;
import org.jbpm.workbench.forms.display.backend.provider.util.FormContentReader;
import org.jbpm.workbench.forms.service.providing.FormProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.TaskInputsDefinition;
import org.kie.server.api.model.definition.TaskOutputsDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.BackendFormRenderingContextManagerImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.FormValuesProcessorImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.MultipleSubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.SubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation.impl.ContextModelConstraintsExtractorImpl;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.runtime.BPMNRuntimeFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.DynamicBPMNFormGeneratorImpl;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormServiceEntryPointImplTest {

    private KieWorkbenchFormsProvider kieWorkbenchFormsProvider;

    private DefaultKieWorkbenchFormsProvider defaultProvider;

    @Mock
    private DocumentServicesClient documentServicesClient;

    @Mock
    private UIServicesClient uiServicesClient;

    @Mock
    private UserTaskServicesClient userTaskServicesClient;

    @Mock
    private ProcessServicesClient processServicesClient;

    @Mock
    private KieServicesClient kieServicesClient;

    private FormValuesProcessor formValuesProcessor;

    private DynamicBPMNFormGenerator dynamicBPMNFormGenerator;

    private BackendFormRenderingContextManagerImpl backendFormRenderingContextManager;

    private BPMNRuntimeFormGeneratorService runtimeFormGeneratorService;

    private FormServiceEntryPointImpl serviceEntryPoint;

    private String formContent;

    @Before
    public void init() {
        List<FieldValueProcessor> processors = Arrays.asList(new SubFormFieldValueProcessor(),
                                                             new MultipleSubFormFieldValueProcessor());

        Instance<FieldValueProcessor<? extends FieldDefinition, ?, ?>> fieldValueProcessors = mock(Instance.class);
        when(fieldValueProcessors.iterator()).then(proc -> processors.iterator());

        formValuesProcessor = new FormValuesProcessorImpl(fieldValueProcessors);

        Instance<FormProvider<? extends FormRenderingSettings>> instance = mock(Instance.class);

        backendFormRenderingContextManager = new BackendFormRenderingContextManagerImpl(formValuesProcessor,
                                                                                        new ContextModelConstraintsExtractorImpl());

        runtimeFormGeneratorService = new BPMNRuntimeFormGeneratorService(new TestFieldManager(),
                                                                          new RawMVELEvaluator());

        dynamicBPMNFormGenerator = new DynamicBPMNFormGeneratorImpl(runtimeFormGeneratorService);

        FormDefinitionSerializer serializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                               new FormModelSerializer(),
                                                                               new TestMetaDataEntryManager());

        ProcessFormsValuesProcessor processValuesProcessor = new ProcessFormsValuesProcessor(serializer,
                                                                                             backendFormRenderingContextManager,
                                                                                             dynamicBPMNFormGenerator);
        TaskFormValuesProcessor taskValuesProcessor = new TaskFormValuesProcessor(serializer,
                                                                                  backendFormRenderingContextManager,
                                                                                  dynamicBPMNFormGenerator);

        kieWorkbenchFormsProvider = new KieWorkbenchFormsProvider(processValuesProcessor,
                                                                  taskValuesProcessor);

        defaultProvider = new DefaultKieWorkbenchFormsProvider(processValuesProcessor,
                                                               taskValuesProcessor);

        when(kieServicesClient.getClassLoader()).thenReturn(this.getClass().getClassLoader());

        when(instance.iterator()).then(result -> Arrays.asList(kieWorkbenchFormsProvider).iterator());

        serviceEntryPoint = new FormServiceEntryPointImpl(instance,
                                                          defaultProvider) {

            @Override
            protected <T> T getClient(String serverTemplateId,
                                      String containerId,
                                      Class<T> clientType) {
                if (clientType.equals(DocumentServicesClient.class)) {
                    return (T) documentServicesClient;
                }
                if (clientType.equals(UIServicesClient.class)) {
                    return (T) uiServicesClient;
                }
                if (clientType.equals(UserTaskServicesClient.class)) {
                    return (T) userTaskServicesClient;
                }
                if (clientType.equals(ProcessServicesClient.class)) {
                    return (T) processServicesClient;
                }

                return null;
            }

            @Override
            protected KieServicesClient getKieServicesClient(String serverTemplateId,
                                                             String containerId) {
                return kieServicesClient;
            }
        };

        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setId("invoices");
        processDefinition.setName("invoices");
        processDefinition.setContainerId("localhost");
        processDefinition.setPackageName("org.jbpm.test");

        when(processServicesClient.getProcessDefinition(anyString(),
                                                        anyString())).thenReturn(processDefinition);

        when(processServicesClient.getUserTaskInputDefinitions(anyString(),
                                                               anyString(),
                                                               anyString())).thenReturn(new TaskInputsDefinition());
        when(processServicesClient.getUserTaskOutputDefinitions(anyString(),
                                                                anyString(),
                                                                anyString())).thenReturn(new TaskOutputsDefinition());

        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setId(new Long(12));
        taskInstance.setName("modify");
        taskInstance.setFormName("modify");
        taskInstance.setDescription("modify");
        taskInstance.setProcessId("invoices");
        taskInstance.setInputData(new HashMap<>());
        taskInstance.setOutputData(new HashMap<>());

        when(userTaskServicesClient.getTaskInstance(anyString(),
                                                    anyLong(),
                                                    anyBoolean(),
                                                    anyBoolean(),
                                                    anyBoolean())).thenReturn(taskInstance);
    }

    @Test
    public void testRenderProcessForm() {

        formContent = FormContentReader.getStartProcessForms();

        when(uiServicesClient.getProcessRawForm(anyString(),
                                                anyString())).thenReturn(formContent);

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess("template",
                                                                                 "domain",
                                                                                 "invoices");

        verify(processServicesClient).getProcessDefinition(anyString(),
                                                           anyString());
        verify(kieServicesClient).getClassLoader();
        verify(uiServicesClient).getProcessRawForm(anyString(),
                                                   anyString());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderProcessDefaultForm() {

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess("template",
                                                                                 "domain",
                                                                                 "invoices");

        verify(processServicesClient).getProcessDefinition(anyString(),
                                                           anyString());
        verify(kieServicesClient,
               times(2)).getClassLoader();
        verify(uiServicesClient).getProcessRawForm(anyString(),
                                                   anyString());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderProcessFormFromWrongFormContent() {

        formContent = "this form content is wrong and a default form should be generated";

        when(uiServicesClient.getProcessRawForm(anyString(),
                                                anyString())).thenReturn(formContent);

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess("template",
                                                                                 "domain",
                                                                                 "invoices");

        verify(processServicesClient).getProcessDefinition(anyString(),
                                                           anyString());
        verify(kieServicesClient,
               times(2)).getClassLoader();
        verify(uiServicesClient).getProcessRawForm(anyString(),
                                                   anyString());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderProcessDefaultFormWithException() {

        when(uiServicesClient.getProcessRawForm(anyString(),
                                                anyString())).thenThrow(new KieServicesException("Unable to find form"));

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess("template",
                                                                                 "domain",
                                                                                 "invoices");

        verify(processServicesClient).getProcessDefinition(anyString(),
                                                           anyString());
        verify(kieServicesClient).getClassLoader();
        verify(uiServicesClient).getProcessRawForm(anyString(),
                                                   anyString());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderTaskForm() {

        formContent = FormContentReader.getTaskForms();

        when(uiServicesClient.getTaskRawForm(anyString(),
                                             anyLong())).thenReturn(formContent);

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask("template",
                                                                              "domain",
                                                                              12);

        verify(userTaskServicesClient).getTaskInstance(anyString(),
                                                       anyLong(),
                                                       anyBoolean(),
                                                       anyBoolean(),
                                                       anyBoolean());
        verify(kieServicesClient).getClassLoader();
        verify(uiServicesClient).getTaskRawForm(anyString(),
                                                anyLong());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderTaskDefaultForm() {
        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask("template",
                                                                              "domain",
                                                                              12);

        verify(userTaskServicesClient).getTaskInstance(anyString(),
                                                       anyLong(),
                                                       anyBoolean(),
                                                       anyBoolean(),
                                                       anyBoolean());
        verify(kieServicesClient,
               times(2)).getClassLoader();
        verify(uiServicesClient).getTaskRawForm(anyString(),
                                                anyLong());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderTaskDefaultFormWithException() {
        when(uiServicesClient.getTaskRawForm(anyString(),
                                             anyLong())).thenThrow(new KieServicesException("Unable to find form"));

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask("template",
                                                                              "domain",
                                                                              12);

        verify(userTaskServicesClient).getTaskInstance(anyString(),
                                                       anyLong(),
                                                       anyBoolean(),
                                                       anyBoolean(),
                                                       anyBoolean());
        verify(kieServicesClient).getClassLoader();
        verify(uiServicesClient).getTaskRawForm(anyString(),
                                                anyLong());

        checkRenderingSettings(settings);
    }

    @Test
    public void testRenderTaskFormFromWrongFormContent() {

        formContent = "this form content is wrong and a default form should be generated";

        when(uiServicesClient.getTaskRawForm(anyString(),
                                             anyLong())).thenReturn(formContent);

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask("template",
                                                                              "domain",
                                                                              12);

        verify(userTaskServicesClient).getTaskInstance(anyString(),
                                                       anyLong(),
                                                       anyBoolean(),
                                                       anyBoolean(),
                                                       anyBoolean());
        verify(kieServicesClient,
               times(2)).getClassLoader();
        verify(uiServicesClient).getTaskRawForm(anyString(),
                                                anyLong());

        checkRenderingSettings(settings);
    }

    protected void checkRenderingSettings(FormRenderingSettings settings) {
        assertNotNull("Settings cannot be null",
                      settings);
        assertTrue("Settings must be WB Forms",
                   settings instanceof KieWorkbenchFormRenderingSettings);

        KieWorkbenchFormRenderingSettings wbSettings = (KieWorkbenchFormRenderingSettings) settings;

        assertNotNull("Rendering context shouldn't be empty",
                      wbSettings.getRenderingContext());

        assertNotNull("There should be a default FormDefinition",
                      wbSettings.getRenderingContext().getRootForm());
    }

    protected String getFormContent() {
        try {
            return IOUtils.toString(this.getClass().getResourceAsStream(
                    "/forms/invoices-taskform.frm"));
        } catch (IOException ex) {
            fail("Exception thrown getting form content");
        }
        return "";
    }
}
