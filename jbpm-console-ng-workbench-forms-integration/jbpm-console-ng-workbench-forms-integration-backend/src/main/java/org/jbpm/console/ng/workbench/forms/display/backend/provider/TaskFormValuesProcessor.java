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

package org.jbpm.console.ng.workbench.forms.display.backend.provider;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.console.ng.ga.forms.service.providing.TaskRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class TaskFormValuesProcessor extends KieWorkbenchFormsValuesProcessor<TaskRenderingSettings> {

    private static final Logger logger = LoggerFactory.getLogger( TaskFormValuesProcessor.class );

    @Inject
    public TaskFormValuesProcessor( FormDefinitionSerializer formSerializer,
                                    BackendFormRenderingContextManager contextManager,
                                    FormValuesProcessor formValuesProcessor ) {
        super( formSerializer, contextManager, formValuesProcessor );
    }

    @Override
    protected String getFormName( TaskRenderingSettings settings ) {
        return settings.getTask().getFormName();
    }

    @Override
    protected Map<String, Object> getOutputValues( Map<String, Object> values, FormDefinition form ) {
        Map<String, Object> result = new HashMap<>();

        if ( isValid( form ) ) {

            TaskFormModel model = (TaskFormModel) form.getModel();

            model.getVariables().forEach( var -> {
                if ( !StringUtils.isEmpty( var.getOuputMapping() ) ) {
                    result.put( var.getOuputMapping(), values.get( var.getName() ) );
                }
            } );
        }
        return result;
    }

    @Override
    protected void prepareContext( TaskRenderingSettings settings, MapModelRenderingContext context ) {

        context.setRenderMode( !"InProgress".equals( settings.getTask().getStatus() ) ? RenderMode.READ_ONLY_MODE : RenderMode.EDIT_MODE );

    }

    @Override
    protected Map<String, Object> generateRawFormData( TaskRenderingSettings settings,
                                                       MapModelRenderingContext renderingContext ) {
        final Map<String, Object> formData = new HashMap<>();

        FormDefinition form = renderingContext.getRootForm();

        if ( isValid( form ) ) {

            boolean includeOutputs = settings.getTask().isOutputIncluded();

            Map<String, Object> inputs = settings.getInputs();
            Map<String, Object> outputs = settings.getOutputs();

            TaskFormModel formModel = (TaskFormModel) form.getModel();

            formModel.getVariables().forEach( taskVariable -> {
                Object value = null;
                if ( includeOutputs && !StringUtils.isEmpty( taskVariable.getOuputMapping() ) ) {
                    value = outputs.get( taskVariable.getOuputMapping() );
                } else {
                    value = inputs.get( taskVariable.getInputMapping() );
                }
                formData.put( taskVariable.getName(), value );
            } );

        }
        return formData;
    }

    @Override
    protected Map<String, Object> generateFormData( Map<String, Object> rawData,
                                                    BackendFormRenderingContext context ) {
        return formValuesProcessor.readFormValues( context.getRenderingContext().getRootForm(), rawData, context );
    }

    @Override
    protected boolean isValid( FormDefinition rootForm ) {
        return rootForm != null && rootForm.getModel() instanceof TaskFormModel;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
