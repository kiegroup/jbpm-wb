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

package org.jbpm.console.ng.ga.forms.display.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ks.integration.AbstractKieServerService;
import org.jbpm.console.ng.ga.forms.display.FormRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.providing.DefaultFormProvider;
import org.jbpm.console.ng.ga.forms.service.providing.FormProvider;
import org.jbpm.console.ng.ga.forms.service.providing.ProcessRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.providing.TaskRenderingSettings;
import org.jbpm.console.ng.ga.forms.service.providing.model.TaskDefinition;
import org.jbpm.console.ng.ga.forms.service.shared.FormServiceEntryPoint;
import org.jbpm.document.Document;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.TaskInputsDefinition;
import org.kie.server.api.model.definition.TaskOutputsDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class FormServiceEntryPointImpl extends AbstractKieServerService implements FormServiceEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger( FormServiceEntryPointImpl.class );

    private final FormProvider<? extends FormRenderingSettings> defaultFormProvider;

    private Set<FormProvider<? extends FormRenderingSettings>> providers = new TreeSet<>( ( o1, o2 ) -> o1.getPriority() - o2.getPriority() );

    @Inject
    public FormServiceEntryPointImpl( Instance<FormProvider<? extends FormRenderingSettings>> providersInjected,
                                      @DefaultFormProvider FormProvider<? extends FormRenderingSettings> defaultFormProvider ) {
        for ( FormProvider provider : providersInjected ) {
            providers.add( provider );
        }

        this.defaultFormProvider = defaultFormProvider;
    }

    @Override
    public FormRenderingSettings getFormDisplayTask( String serverTemplateId, String domainId, long taskId ) {
        String registrationKey = serverTemplateId + "@" + domainId + "@" + System.currentTimeMillis();

        DocumentServicesClient documentClient = getClient( serverTemplateId, domainId, DocumentServicesClient.class );

        // get form content
        UIServicesClient uiServicesClient = getClient( serverTemplateId, domainId, UIServicesClient.class );

        // get task with inputs and outputs
        UserTaskServicesClient taskClient = getClient( serverTemplateId, domainId, UserTaskServicesClient.class );
        TaskInstance task = taskClient.getTaskInstance( domainId, taskId, true, true, false );
        if ( task == null ) {
            throw new RuntimeException( "No task found for id " + taskId );
        }

        ProcessServicesClient processService = getClient( serverTemplateId, domainId, ProcessServicesClient.class );

        TaskDefinition taskInstance = new TaskDefinition();
        taskInstance.setId( task.getId() );
        taskInstance.setName( task.getName() );
        taskInstance.setDescription( task.getDescription() );
        taskInstance.setFormName( task.getFormName() );
        taskInstance.setDeploymentId( registrationKey );
        taskInstance.setProcessId( task.getProcessId() );

        taskInstance.setStatus( task.getStatus() );

        TaskInputsDefinition inputDefinitions = processService.getUserTaskInputDefinitions( domainId, task.getProcessId(), task.getName() );

        taskInstance.setTaskInputDefinitions( inputDefinitions.getTaskInputs() );

        TaskOutputsDefinition outputDefinitions = processService.getUserTaskOutputDefinitions( domainId, task.getProcessId(), task.getName() );

        taskInstance.setTaskOutputDefinitions( outputDefinitions.getTaskOutputs() );

        // prepare render context
        Map<String, Object> inputs = processData( documentClient, task.getInputData() );

        Map<String, Object> outputs = processData( documentClient, task.getOutputData() );

        if ( outputs != null && !outputs.isEmpty() ) {
            taskInstance.setOutputIncluded( true );
        }

        KieServicesClient kieServicesClient = getKieServicesClient( serverTemplateId, domainId );

        try {
            String formContent = uiServicesClient.getTaskRawForm( domainId, taskId );
            TaskRenderingSettings settings = new TaskRenderingSettings( taskInstance,
                                                                        inputs,
                                                                        outputs,
                                                                        formContent,
                                                                        new ContentMarshallerContext( null,
                                                                                                      kieServicesClient.getClassLoader() ) );
            for ( FormProvider provider : providers ) {
                FormRenderingSettings template = provider.render( settings );
                if ( template != null ) {
                    return template;
                }
            }

        } catch ( KieServicesException e ) {
            logger.debug( "Unable to find task form in remote server due to {}", e.getMessage() );
        } catch ( Exception e ) {
            logger.debug( "Unable to render task form due to {}", e.getMessage() );
        }

        return renderDefaultTaskForm( taskInstance, inputs, outputs, kieServicesClient );
    }

    private FormRenderingSettings renderDefaultTaskForm( TaskDefinition taskInstance,
                                                         Map<String, Object> inputs,
                                                         Map<String, Object> outputs,
                                                         KieServicesClient kieServicesClient ) {
        try {
            return defaultFormProvider.render( new TaskRenderingSettings( taskInstance,
                                                                          inputs,
                                                                          outputs,
                                                                          "",
                                                                          new ContentMarshallerContext( null,
                                                                                                        kieServicesClient.getClassLoader() ) ) );
        } catch ( Exception ex ) {
            logger.warn( "Unable to generate default form for task '" + taskInstance.getName() + "': {}",
                         ex.getMessage() );
        }
        return null;
    }

    @Override
    public FormRenderingSettings getFormDisplayProcess( String serverTemplateId, String domainId, String processId ) {

        ProcessServicesClient processClient = getClient( serverTemplateId, domainId, ProcessServicesClient.class );

        ProcessDefinition processDefinition = processClient.getProcessDefinition( domainId, processId );

        org.jbpm.console.ng.ga.forms.service.providing.model.ProcessDefinition processDesc = new org.jbpm.console.ng.ga.forms.service.providing.model.ProcessDefinition();
        processDesc.setId( processDefinition.getId() );
        processDesc.setName( processDefinition.getName() );
        processDesc.setPackageName( processDefinition.getPackageName() );
        processDesc.setDeploymentId( serverTemplateId + "@" + processDefinition.getContainerId() + "@" + System.currentTimeMillis() );

        Map<String, String> processData = processDefinition.getProcessVariables();

        if ( processData == null ) {
            processData = new HashMap<String, String>();
        }

        UIServicesClient uiServicesClient = getClient( serverTemplateId, domainId, UIServicesClient.class );

        KieServicesClient kieServicesClient = getKieServicesClient( serverTemplateId, domainId );

        try {
            String formContent = uiServicesClient.getProcessRawForm( domainId, processId );
            ProcessRenderingSettings settings = new ProcessRenderingSettings( processDesc,
                                                                              processData,
                                                                              formContent,
                                                                              new ContentMarshallerContext( null,
                                                                                                            kieServicesClient.getClassLoader() ) );

            for ( FormProvider provider : providers ) {
                FormRenderingSettings renderingSettings = provider.render( settings );

                if ( renderingSettings != null ) {
                    return renderingSettings;
                }
            }

        } catch ( KieServicesException e ) {
            logger.debug( "Unable to find process form in remote server due to {}", e.getMessage() );
        } catch ( Exception e ) {
            logger.debug( "Unable to render process form due to {}", e.getMessage() );
        }

        return renderDefaultProcessForm( processDesc, processData, kieServicesClient );
    }

    private FormRenderingSettings renderDefaultProcessForm( org.jbpm.console.ng.ga.forms.service.providing.model.ProcessDefinition processDesc,
                                                            Map<String, String> processData,
                                                            KieServicesClient kieServicesClient ) {
        try {
            return defaultFormProvider.render( new ProcessRenderingSettings( processDesc,
                                                                             processData,
                                                                             "",
                                                                             new ContentMarshallerContext( null,
                                                                                                           kieServicesClient.getClassLoader() ) ) );
        } catch ( Exception ex ) {
            logger.warn( "Unable to generate default form for process '" + processDesc.getName() + "': {}",
                         ex.getMessage() );
        }
        return null;
    }

    protected Map<String, Object> processData( DocumentServicesClient documentClient, Map<String, Object> data ) {

        if ( data == null || data.isEmpty() ) {
            return data;
        }

        for ( Map.Entry<String, Object> entry : data.entrySet() ) {
            if ( entry.getValue() instanceof Document ) {
                Document document = ( (Document) entry.getValue() );
                document.setLink( documentClient.getDocumentLink( document.getIdentifier() ) );
            }
        }

        return data;
    }

}
