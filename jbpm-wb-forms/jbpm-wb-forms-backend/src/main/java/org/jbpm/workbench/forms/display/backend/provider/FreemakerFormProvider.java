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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.jbpm.workbench.forms.display.impl.StaticHTMLFormRenderingSettings;
import org.jbpm.workbench.forms.service.providing.FormProvider;
import org.jbpm.workbench.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.workbench.forms.service.providing.TaskRenderingSettings;


public abstract class FreemakerFormProvider implements FormProvider<StaticHTMLFormRenderingSettings> {

    protected abstract InputStream getProcessTemplateInputStream( ProcessRenderingSettings settings );

    protected abstract InputStream getTaskTemplateInputStream( TaskRenderingSettings settings );

    @Override
    public StaticHTMLFormRenderingSettings render( ProcessRenderingSettings settings ) {
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put( "process", settings.getProcess() );
        renderContext.put( "outputs", settings.getProcessData() );
        renderContext.put( "marshallerContext", settings.getMarshallerContext() );

        return renderForm( settings.getProcess().getName(), getProcessTemplateInputStream( settings ), renderContext );
    }

    @Override
    public StaticHTMLFormRenderingSettings render( TaskRenderingSettings settings ) {
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put( "task", settings.getTask() );
        renderContext.put( "marshallerContext", settings.getMarshallerContext() );

        Map<String, Object> inputs = settings.getInputs();
        if ( inputs != null && !inputs.isEmpty() ) {
            renderContext.put( "inputs", inputs );
            renderContext.putAll( inputs );
        }

        Map<String, Object> outputs = settings.getOutputs();
        if ( outputs != null && !outputs.isEmpty() ) {
            renderContext.put( "outputs", outputs );
            renderContext.putAll( outputs );
            settings.getTask().setOutputIncluded( true );
        }

        return renderForm( settings.getTask().getName(), getTaskTemplateInputStream( settings ), renderContext );
    }

    protected StaticHTMLFormRenderingSettings renderForm( String name,
                                                          InputStream src,
                                                          Map<String, Object> renderContext ) {
        if ( src == null ) {
            return null;
        }
        String htmlTemplate = "";
        StringWriter writer = null;
        InputStreamReader source = null;
        try {
            Configuration cfg = new Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper( true );
            cfg.setObjectWrapper( defaultInstance );
            cfg.setTemplateUpdateDelay( 0 );
            source = new InputStreamReader( src );
            Template temp = new Template( name, source, cfg );
            writer = new StringWriter();
            temp.process( renderContext, writer );
            writer.flush();
            htmlTemplate = writer.getBuffer().toString();
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to process form template", e );
        } finally {
            IOUtils.closeQuietly( writer );
            IOUtils.closeQuietly( source );
        }
        return new StaticHTMLFormRenderingSettings( htmlTemplate );
    }
}
