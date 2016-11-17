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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.ga.forms.service.providing.ProcessRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class ProcessFormsValuesProcessor extends KieWorkbenchFormsValuesProcessor<ProcessRenderingSettings> {

    private static final Logger logger = LoggerFactory.getLogger( ProcessFormsValuesProcessor.class );

    @Inject
    public ProcessFormsValuesProcessor( FormDefinitionSerializer formSerializer,
                                        BackendFormRenderingContextManager contextManager,
                                        DynamicBPMNFormGenerator dynamicBPMNFormGenerator ) {
        super( formSerializer, contextManager, dynamicBPMNFormGenerator );
    }

    @Override
    protected String getFormName( ProcessRenderingSettings settings ) {
        return settings.getProcess().getId();
    }

    @Override
    protected Map<String, Object> getOutputValues( Map<String, Object> values,
                                                   FormDefinition form,
                                                   ProcessRenderingSettings context ) {

        if ( isValid( form ) ) {
            BusinessProcessFormModel model = (BusinessProcessFormModel) form.getModel();

            values.entrySet().stream().allMatch( entry -> model.getVariables().stream().filter( variable -> variable.getName().equals(
                    entry.getKey() ) ).findFirst().isPresent() );
            return values;
        }
        throw new IllegalArgumentException( "Form not valid to start process" );
    }

    @Override
    protected void prepareContext( ProcessRenderingSettings settings, BackendFormRenderingContext context ) {
        // Nothing to do for processes
    }

    @Override
    protected boolean isValid( FormDefinition rootForm ) {
        return rootForm != null && rootForm.getModel() instanceof BusinessProcessFormModel;
    }

    @Override
    protected Collection<FormDefinition> generateDefaultFormsForContext( ProcessRenderingSettings settings ) {
        List<JBPMVariable> variables = new ArrayList<>();
        settings.getProcessData().forEach( ( name, type ) -> {
            variables.add( new JBPMVariable( name, type ) );
        } );
        BusinessProcessFormModel formModel = new BusinessProcessFormModel( settings.getProcess().getId(),
                                                                           settings.getProcess().getName(),
                                                                           variables );

        return dynamicBPMNFormGenerator.generateProcessForms( formModel,
                                                              settings.getMarshallerContext().getClassloader() );
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
