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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.console.ng.ga.forms.service.providing.RenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.slf4j.Logger;

public abstract class KieWorkbenchFormsValuesProcessor<T extends RenderingSettings> {

    protected FormDefinitionSerializer formSerializer;

    protected BackendFormRenderingContextManager contextManager;

    protected FormValuesProcessor formValuesProcessor;

    public KieWorkbenchFormsValuesProcessor( FormDefinitionSerializer formSerializer,
                                             BackendFormRenderingContextManager contextManager,
                                             FormValuesProcessor formValuesProcessor ) {
        this.formSerializer = formSerializer;
        this.contextManager = contextManager;
        this.formValuesProcessor = formValuesProcessor;
    }

    public KieWorkbenchFormRenderingSettings generateRenderingContext( T settings ) {
        if ( !StringUtils.isEmpty( settings.getFormContent() ) ) {

            try {
                MapModelRenderingContext renderingContext = new MapModelRenderingContext();

                initializeContextForms( settings, renderingContext );

                if ( !isValid( renderingContext.getRootForm() ) ) {
                    return null;
                }

                Map<String, Object> rawData = generateRawFormData( settings, renderingContext );

                BackendFormRenderingContext context = contextManager.registerContext( renderingContext,
                                                                                      rawData,
                                                                                      settings.getMarshallerContext().getClassloader() );

                Map<String, Object> formData = generateFormData( rawData, context );

                renderingContext.setModel( formData );

                prepareContext( settings, renderingContext );

                return new KieWorkbenchFormRenderingSettings( context.getTimestamp(), renderingContext );

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
                contextManager.removeContext( timestamp );
                return getOutputValues( formValuesProcessor.writeFormValues( form,
                                                            formValues,
                                                            context.getFormData(),
                                                            context ), form );

            }
        }
        throw new IllegalArgumentException( "Unable read form values for context '" + timestamp + "'. Form Values: " + formValues );
    }

    protected abstract Map<String, Object> getOutputValues( Map<String, Object> values, FormDefinition form );

    protected abstract void prepareContext( T settings, MapModelRenderingContext context );

    protected void initializeContextForms( T settings, MapModelRenderingContext renderingContext ) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        JsonElement element = parser.parse( settings.getFormContent() );

        JsonArray forms = element.getAsJsonArray();
        forms.forEach( jsonForm -> {
            String content = gson.toJson( jsonForm );

            if ( !StringUtils.isEmpty( content ) ) {
                FormDefinition formDefinition = formSerializer.deserialize( content );
                if ( formDefinition != null ) {

                    if ( formDefinition.getName().startsWith( getFormName( settings ) + "-taskform" ) ) {
                        renderingContext.setRootForm( formDefinition );
                    } else {
                        renderingContext.getAvailableForms().put( formDefinition.getId(), formDefinition );
                    }
                }
            }
        } );
    }

    protected abstract boolean isValid( FormDefinition rootForm );

    protected abstract String getFormName( T settings );

    protected Map<String, Object> generateRawFormData( T settings, MapModelRenderingContext renderingContext ) {
        return new HashMap<>();
    };

    protected Map<String, Object> generateFormData( Map<String, Object> rawData,
                                                             BackendFormRenderingContext context ) {
        return new HashMap<>();
    }

    protected abstract Logger getLogger();

}
