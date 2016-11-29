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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.console.ng.ga.forms.service.providing.RenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.jbpm.service.bpmn.DynamicBPMNFormGenerator;
import org.kie.workbench.common.forms.jbpm.service.bpmn.util.BPMNVariableUtils;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.slf4j.Logger;

public abstract class KieWorkbenchFormsValuesProcessor<T extends RenderingSettings> {

    public static String SETTINGS_ATRA_NAME = "_rendering_settings";

    protected FormDefinitionSerializer formSerializer;

    protected BackendFormRenderingContextManager contextManager;

    protected DynamicBPMNFormGenerator dynamicBPMNFormGenerator;

    public KieWorkbenchFormsValuesProcessor( FormDefinitionSerializer formSerializer,
                                             BackendFormRenderingContextManager contextManager,
                                             DynamicBPMNFormGenerator dynamicBPMNFormGenerator ) {
        this.formSerializer = formSerializer;
        this.contextManager = contextManager;
        this.dynamicBPMNFormGenerator = dynamicBPMNFormGenerator;
    }

    public KieWorkbenchFormRenderingSettings generateRenderingContext( T settings ) {
        return generateRenderingContext( settings, false );
    }

    public KieWorkbenchFormRenderingSettings generateRenderingContext( T settings, boolean generateDefaultForms ) {
        if ( generateDefaultForms || !StringUtils.isEmpty( settings.getFormContent() ) ) {

            try {

                ContextForms forms = generateDefaultForms ? generateDefaultForms( settings ) : parseForms( settings );

                if ( forms.getRootForm() == null || !isValid( forms.getRootForm() ) ) {
                    return null;
                }

                Map<String, Object> rawData = generateRawFormData( settings, forms.getRootForm() );

                BackendFormRenderingContext context = contextManager.registerContext( forms.getRootForm(),
                                                                                      rawData,
                                                                                      settings.getMarshallerContext().getClassloader(),
                                                                                      forms.getNestedForms().toArray(
                                                                                              new FormDefinition[forms.getNestedForms().size()] ) );

                prepareContext( settings, context );

                context.getAttributes().put( SETTINGS_ATRA_NAME, settings );

                return new KieWorkbenchFormRenderingSettings( context.getTimestamp(), context.getRenderingContext() );

            } catch ( Exception ex ) {
                getLogger().debug( "Unable to generate render form: ", ex );
            }
        }

        return null;
    }

    public Map<String, Object> generateRuntimeValuesMap( long timestamp, Map<String, Object> formValues ) {

        BackendFormRenderingContext context = contextManager.getContext( timestamp );

        if ( context != null ) {
            FormDefinition form = context.getRenderingContext().getRootForm();

            if ( isValid( form ) ) {
                Map<String, Object> formData = contextManager.updateContextData( timestamp, formValues ).getFormData();
                return getOutputValues( formData, form, (T) context.getAttributes().get( SETTINGS_ATRA_NAME ) );
            }
        }
        return Collections.emptyMap();
    }

    protected ContextForms parseForms( T settings ) {
        ContextForms result = new ContextForms();

        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        JsonElement element = parser.parse( settings.getFormContent() );

        JsonArray forms = element.getAsJsonArray();
        forms.forEach( jsonForm -> {
            String content = gson.toJson( jsonForm );

            if ( !StringUtils.isEmpty( content ) ) {
                FormDefinition formDefinition = formSerializer.deserialize( content );
                if ( formDefinition != null ) {
                    if ( formDefinition.getName().startsWith( getFormName( settings ) + BPMNVariableUtils.TASK_FORM_SUFFIX ) ) {
                        result.setRootForm( formDefinition );
                    } else {
                        result.getNestedForms().add( formDefinition );
                    }
                }
            }
        } );
        return result;
    }

    protected ContextForms generateDefaultForms( T settings ) {
        ContextForms result = new ContextForms();

        Collection<FormDefinition> contextForms = generateDefaultFormsForContext( settings );

        if ( contextForms == null ) {
            throw new IllegalArgumentException( "Unable to create forms for context" );
        }

        contextForms.forEach( form -> {
            if ( form.getName().equals( getFormName( settings ) + BPMNVariableUtils.TASK_FORM_SUFFIX ) ) {
                result.setRootForm( form );
            } else {
                result.getNestedForms().add( form );
            }
        } );

        return result;
    }

    protected abstract Collection<FormDefinition> generateDefaultFormsForContext( T settings );

    protected abstract Map<String, Object> getOutputValues( Map<String, Object> values,
                                                            FormDefinition form,
                                                            T settings );

    protected abstract boolean isValid( FormDefinition rootForm );

    protected abstract String getFormName( T settings );

    protected abstract void prepareContext( T settings, BackendFormRenderingContext context );

    protected Map<String, Object> generateRawFormData( T settings, FormDefinition form ) {
        return new HashMap<>();
    }

    protected abstract Logger getLogger();

    protected class ContextForms {
        private FormDefinition rootForm;
        private List<FormDefinition> nestedForms = new ArrayList<>();

        public FormDefinition getRootForm() {
            return rootForm;
        }

        public void setRootForm( FormDefinition rootForm ) {
            this.rootForm = rootForm;
        }

        public List<FormDefinition> getNestedForms() {
            return nestedForms;
        }
    }
}
