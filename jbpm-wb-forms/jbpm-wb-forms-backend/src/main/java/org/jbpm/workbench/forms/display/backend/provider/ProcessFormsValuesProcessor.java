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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.forms.service.providing.ProcessRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.kie.workbench.common.forms.service.backend.util.ModelPropertiesGenerator;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class ProcessFormsValuesProcessor extends KieWorkbenchFormsValuesProcessor<ProcessRenderingSettings> {

    private static final Logger logger = LoggerFactory.getLogger(ProcessFormsValuesProcessor.class);

    @Inject
    public ProcessFormsValuesProcessor(FormDefinitionSerializer formSerializer,
                                       BackendFormRenderingContextManager contextManager,
                                       DynamicBPMNFormGenerator dynamicBPMNFormGenerator) {
        super(formSerializer,
              contextManager,
              dynamicBPMNFormGenerator);
    }

    @Override
    protected String getFormName(ProcessRenderingSettings settings) {
        return settings.getProcess().getId();
    }

    @Override
    protected Map<String, Object> getOutputValues(Map<String, Object> values,
                                                  FormDefinition form,
                                                  ProcessRenderingSettings context) {

        if (isValid(form)) {
            BusinessProcessFormModel model = (BusinessProcessFormModel) form.getModel();

            values.entrySet().stream().allMatch(entry -> model.getProperties().stream().filter(variable -> variable.getName().equals(
                    entry.getKey())).findFirst().isPresent());
            return values;
        }
        throw new IllegalArgumentException("Form not valid to start process");
    }

    @Override
    protected void prepareContext(ProcessRenderingSettings settings,
                                  BackendFormRenderingContext context) {
        // Nothing to do for processes
    }

    @Override
    protected boolean isValid(FormDefinition rootForm) {
        return rootForm != null && rootForm.getModel() instanceof BusinessProcessFormModel;
    }

    @Override
    protected Collection<FormDefinition> generateDefaultFormsForContext(ProcessRenderingSettings settings) {

        List<ModelProperty> properties = settings.getProcessData().entrySet().stream().map(entry -> {
            ModelProperty property = ModelPropertiesGenerator.createModelProperty(entry.getKey(),
                                                                                  BPMNVariableUtils.getRealTypeForInput(entry.getValue()),
                                                                                  settings.getMarshallerContext().getClassloader());


            if(property.getTypeInfo().getClassName().equals(Object.class.getName())) {
                property.getMetaData().addEntry(new FieldTypeEntry(TextAreaFieldType.NAME));
            }

            return property;
        }).sorted((property1, property2) -> property1.getName().compareToIgnoreCase(property2.getName())).collect(Collectors.toList());

        BusinessProcessFormModel formModel = new BusinessProcessFormModel(settings.getProcess().getId(),
                                                                          settings.getProcess().getName(),
                                                                          properties);

        return dynamicBPMNFormGenerator.generateProcessForms(formModel,
                                                             settings.getMarshallerContext().getClassloader());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
