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

package org.jbpm.workbench.forms.modeler.backend.server.provider;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.workbench.forms.service.providing.FormProvider;
import org.jbpm.workbench.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.workbench.forms.service.providing.TaskRenderingSettings;
import org.jbpm.workbench.forms.service.providing.model.TaskDefinition;
import org.jbpm.workbench.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class FormModelerFormProvider implements FormProvider<FormModelerFormRenderingSettings> {

    protected Logger log = LoggerFactory.getLogger( FormModelerFormProvider.class );

    private FormSerializationManager formSerializationManager;

    private FormRenderContextManager formRenderContextManager;

    private FormRenderContentMarshallerManager formRenderContentMarshaller;

    @Inject
    public FormModelerFormProvider( FormSerializationManager formSerializationManager,
                                    FormRenderContextManager formRenderContextManager,
                                    FormRenderContentMarshallerManager formRenderContentMarshaller ) {
        this.formSerializationManager = formSerializationManager;
        this.formRenderContextManager = formRenderContextManager;
        this.formRenderContentMarshaller = formRenderContentMarshaller;
    }

    @Override
    public FormModelerFormRenderingSettings render( ProcessRenderingSettings settings ) {
        if ( !StringUtils.isEmpty( settings.getFormContent() ) ) {
            try {
                Form form = formSerializationManager.loadFormFromXML( settings.getFormContent() );

                Map ctx = new HashMap();

                ctx.put( "process", settings.getProcess() );

                // Adding forms to context while forms are'nt available on marshaller classloader
                FormRenderContext context = formRenderContextManager.newContext( form,
                                                                                 settings.getProcess().getDeploymentId(),
                                                                                 ctx,
                                                                                 new HashMap<String, Object>() );
                formRenderContentMarshaller.addContentMarshaller( context.getUID(),
                                                                  settings.getMarshallerContext() );

                return new FormModelerFormRenderingSettings( context.getUID() );
            } catch ( Exception e ) {
                log.warn( "Error rendering form: ", e );
            }
        }
        return null;
    }

    @Override
    public FormModelerFormRenderingSettings render( TaskRenderingSettings settings ) {
        if ( !StringUtils.isEmpty( settings.getFormContent() ) ) {
            try {

                TaskDefinition task = settings.getTask();
                Form form = formSerializationManager.loadFormFromXML( settings.getFormContent() );

                Map inputs = new HashMap();

                Map outputs;
                if ( !task.isOutputIncluded() ) {
                    outputs = new HashMap();
                } else {
                    outputs = settings.getOutputs();
                }

                Map m = settings.getInputs();
                if ( m != null ) inputs.putAll( m );

                inputs.put( "task", task );

                // Adding forms to context while forms are'nt available on marshaller classloader
                FormRenderContext context = formRenderContextManager.newContext( form,
                                                                                 task.getDeploymentId(),
                                                                                 inputs,
                                                                                 outputs );
                formRenderContentMarshaller.addContentMarshaller( context.getUID(),
                                                                  settings.getMarshallerContext() );

                String status = task.getStatus();
                boolean readonly = !"InProgress".equals( status );
                context.setReadonly( readonly );
                return new FormModelerFormRenderingSettings( context.getUID() );

            } catch ( Exception e ) {
                log.warn( "Error rendering form: ", e );
            }
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
