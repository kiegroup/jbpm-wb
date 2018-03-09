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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.workbench.forms.display.backend.provider.model.Client;
import org.jbpm.workbench.forms.display.backend.provider.model.Invoice;
import org.jbpm.workbench.forms.display.backend.provider.model.InvoiceLine;
import org.jbpm.workbench.forms.display.backend.provider.util.FormContentReader;
import org.jbpm.workbench.forms.service.providing.TaskRenderingSettings;
import org.jbpm.workbench.forms.service.providing.model.TaskDefinition;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskFormValuesProcessorTest extends AbstractFormsValuesProcessorTest<TaskFormValuesProcessor, TaskRenderingSettings> {

    @Mock
    protected TaskDefinition task;

    @Override
    public void init() {
        super.init();
        when(task.getFormName()).thenReturn("modify");

        Map<String, String> variables = new HashMap<>();
        variables.put(INVOICE,
                      Invoice.class.getName());

        when(task.getTaskInputDefinitions()).thenReturn(variables);
        when(task.getTaskOutputDefinitions()).thenReturn(variables);
    }

    @Override
    TaskRenderingSettings getFullRenderingSettings() {
        return getRenderingSettings(FormContentReader.getTaskForms());
    }

    @Override
    TaskRenderingSettings getRenderingSettingsWithoutForms() {
        return getRenderingSettings(null);
    }

    private TaskRenderingSettings getRenderingSettings(String formContent) {
        Invoice invoice = new Invoice();
        invoice.setClient(new Client(new Long(1234),
                                     "Dad Smurf",
                                     "Mushroom #1"));
        invoice.setDate(new Date());
        invoice.setComments("It could be better...");
        invoice.setTotal(300.50);

        List<InvoiceLine> lines = new ArrayList<>();
        lines.add(new InvoiceLine("Magical book",
                                  1,
                                  100.5,
                                  200.0));
        lines.add(new InvoiceLine("Nice red hat",
                                  1,
                                  50.0,
                                  150.0));

        invoice.setLines(lines);

        Map<String, Object> inputs = new HashMap<>();

        inputs.put(INVOICE,
                   invoice);

        return new TaskRenderingSettings(task,
                                         inputs,
                                         new HashMap<>(),
                                         SERVER_TEMPLATE_ID,
                                         formContent,
                                         marshallerContext);
    }

    @Override
    TaskFormValuesProcessor getProcessorInstance(FormDefinitionSerializer serializer,
                                                 BackendFormRenderingContextManager backendFormRenderingContextManager,
                                                 DynamicBPMNFormGenerator dynamicBPMNFormGenerator) {
        return new TaskFormValuesProcessor(serializer,
                                           backendFormRenderingContextManager,
                                           dynamicBPMNFormGenerator);
    }
}
