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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Instance;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.commons.io.IOUtils;
import org.jbpm.console.ng.ga.forms.service.providing.RenderingSettings;
import org.jbpm.console.ng.workbench.forms.display.api.KieWorkbenchFormRenderingSettings;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.BackendFormRenderingContextManagerImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.FormValuesProcessorImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.MultipleSubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.SubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.serialization.impl.FormModelSerializer;
import org.mockito.Mock;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public abstract class AbstractFormProvidingEngineTest<R extends RenderingSettings, T extends KieWorkbenchFormsValuesProcessor<R>> {

    @Mock
    protected ContentMarshallerContext marshallerContext;

    protected FormDefinitionSerializer formSerializer;

    protected BackendFormRenderingContextManager contextManager;

    protected FormValuesProcessor formValuesProcessor;

    protected T processor;

    protected R settings;

    protected KieWorkbenchFormsProvider workbenchFormsProvider;

    @Before
    public void initTest() {

        when( marshallerContext.getClassloader() ).thenReturn( AbstractFormProvidingEngineTest.class.getClassLoader() );

        formSerializer = new FormDefinitionSerializerImpl( new FieldSerializer(), new FormModelSerializer() );

        contextManager = new BackendFormRenderingContextManagerImpl();

        List<FieldValueProcessor> processors = Arrays.asList( new SubFormFieldValueProcessor(),
                                                              new MultipleSubFormFieldValueProcessor() );

        Instance fieldValueProcessors = mock( Instance.class );
        when( fieldValueProcessors.iterator() ).then( proc -> processors.iterator() );

        formValuesProcessor = new FormValuesProcessorImpl( fieldValueProcessors );

        settings = generateSettigns();

        processor = getProcessorInstance( formSerializer, contextManager, formValuesProcessor );

        initFormsProvider();
    }

    protected abstract void initFormsProvider();

    protected abstract R generateSettigns();

    protected abstract T getProcessorInstance( FormDefinitionSerializer formSerializer,
                                               BackendFormRenderingContextManager contextManager,
                                               FormValuesProcessor formValuesProcessor );

    protected abstract Map<String, Object> getFormValues();

    protected abstract void checkRuntimeValues( Map<String, Object> resultValues );

    @Test
    public void testGenerateRenderingContext() {
        generateRenderingSettings();
    }

    protected KieWorkbenchFormRenderingSettings generateRenderingSettings() {
        KieWorkbenchFormRenderingSettings settings = processor.generateRenderingContext( this.settings );

        checkRenderingSettings( settings );

        return settings;
    }

    protected void checkRenderingSettings( KieWorkbenchFormRenderingSettings settings ) {
        assertNotNull( "Settings cannot be null", settings );

        assertNotNull( "There should be a backend context", contextManager.getContext( settings.getTimestamp() ) );

        assertFalse( "There should exist some forms...", settings.getRenderingContext().getAvailableForms().isEmpty() );

        assertNotNull( "A root form should exist", settings.getRenderingContext().getRootForm() );
    }

    @Test
    public void testGenerateRuntimeValuesMap() {
        KieWorkbenchFormRenderingSettings settings = generateRenderingSettings();

        Map<String, Object> result = processor.generateRuntimeValuesMap( settings.getTimestamp(), getFormValues() );

        assertNotNull( "Result values cannot be null", result );

        assertFalse( "Result cannot be empty", result.isEmpty() );

        assertNull( "Backend context should be removed", contextManager.getContext( settings.getTimestamp() ) );

        checkRuntimeValues( result );

    }

    protected String getFormContent() {

        try {
            JsonArray formsArray = new JsonArray();

            String content = IOUtils.toString( this.getClass().getResourceAsStream( "/forms/Client.frm" ) );

            formsArray.add( content );

            content = IOUtils.toString( this.getClass().getResourceAsStream( "/forms/InvoiceLine.frm" ) );

            formsArray.add( content );

            content = IOUtils.toString( this.getClass().getResourceAsStream( "/forms/Invoice.frm" ) );

            formsArray.add( content );

            content = IOUtils.toString( this.getClass().getResourceAsStream( "/forms/invoices-taskform.frm" ) );

            formsArray.add( content );

            content = IOUtils.toString( this.getClass().getResourceAsStream( "/forms/modify-taskform.frm" ) );

            formsArray.add( content );

            Gson gson = new Gson();

            return gson.toJson( formsArray );

        } catch ( IOException e ) {
        }

        return null;
    }

}
