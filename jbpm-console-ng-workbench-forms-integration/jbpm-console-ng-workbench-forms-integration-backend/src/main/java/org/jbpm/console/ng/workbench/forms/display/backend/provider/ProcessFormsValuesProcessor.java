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

import org.jbpm.console.ng.ga.forms.service.providing.ProcessRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
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
                                        FormValuesProcessor formValuesProcessor ) {
        super( formSerializer, contextManager, formValuesProcessor );
    }

    @Override
    protected String getFormName( ProcessRenderingSettings settings ) {
        return settings.getProcess().getId();
    }

    @Override
    protected Map<String, Object> getOutputValues( Map<String, Object> values, FormDefinition form ) {
        Map<String, Object> result = new HashMap<>();

        if ( isValid( form ) ) {
            BusinessProcessFormModel model = (BusinessProcessFormModel) form.getModel();

            model.getVariables().forEach( var -> result.put( var.getName(), values.get( var.getName() ) ) );
        }
        return result;
    }

    @Override
    protected void prepareContext( ProcessRenderingSettings settings, MapModelRenderingContext context ) {
        // Nothing to do for processes
    }

    @Override
    protected boolean isValid( FormDefinition rootForm ) {
        return rootForm != null && rootForm.getModel() instanceof BusinessProcessFormModel;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
