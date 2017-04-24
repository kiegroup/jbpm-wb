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

package org.jbpm.workbench.forms.display.backend;

import java.io.IOException;
import java.util.Arrays;
import javax.enterprise.inject.Instance;

import org.apache.commons.io.IOUtils;
import org.jbpm.workbench.forms.display.FormRenderingSettings;
import org.jbpm.workbench.forms.display.backend.provider.ClasspathFormProvider;
import org.jbpm.workbench.forms.display.backend.provider.InMemoryFormProvider;
import org.jbpm.workbench.forms.display.impl.StaticHTMLFormRenderingSettings;
import org.jbpm.workbench.forms.service.providing.FormProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.TaskInputsDefinition;
import org.kie.server.api.model.definition.TaskOutputsDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.DocumentServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class FormServiceEntryPointImplTest {

    protected InMemoryFormProvider inMemoryFormProvider = new InMemoryFormProvider();

    protected ClasspathFormProvider classpathFormProvider = new ClasspathFormProvider();

    @Mock
    protected DocumentServicesClient documentServicesClient;

    @Mock
    protected UIServicesClient uiServicesClient;

    @Mock
    protected UserTaskServicesClient userTaskServicesClient;

    @Mock
    protected ProcessServicesClient processServicesClient;

    @Mock
    protected KieServicesClient kieServicesClient;


    protected FormServiceEntryPointImpl serviceEntryPoint;

    protected String formContent;

    @Before
    public void init() {
        Instance<FormProvider<? extends FormRenderingSettings>> instance = mock( Instance.class );

        when( instance.iterator() ).then( result -> Arrays.asList( inMemoryFormProvider ).iterator() );

        serviceEntryPoint = new FormServiceEntryPointImpl( instance, classpathFormProvider ) {

            @Override
            protected <T> T getClient( String serverTemplateId, String containerId, Class<T> clientType ) {
                if ( clientType.equals( DocumentServicesClient.class ) ) {
                    return (T) documentServicesClient;
                }
                if ( clientType.equals( UIServicesClient.class ) ) {
                    return (T) uiServicesClient;
                }
                if ( clientType.equals( UserTaskServicesClient.class ) ) {
                    return (T) userTaskServicesClient;
                }
                if ( clientType.equals( ProcessServicesClient.class ) ) {
                    return (T) processServicesClient;
                }

                return null;
            }

            @Override
            protected KieServicesClient getKieServicesClient( String serverTemplateId, String containerId ) {
                return kieServicesClient;
            }
        };

        formContent = getFormContent();

        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setId( "testProcess" );
        processDefinition.setName( "testProcess" );
        processDefinition.setContainerId( "localhost" );
        processDefinition.setPackageName( "org.jbpm.test" );

        when( processServicesClient.getProcessDefinition( anyString(), anyString() ) ).thenReturn( processDefinition );

        when( processServicesClient.getUserTaskInputDefinitions( anyString(), anyString(), anyString() ) ).thenReturn(  new TaskInputsDefinition() );
        when( processServicesClient.getUserTaskOutputDefinitions( anyString(), anyString(), anyString() ) ).thenReturn(  new TaskOutputsDefinition() );

        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setId( new Long(12) );
        taskInstance.setName( "TaskName" );
        taskInstance.setFormName( "TaskFormName" );
        taskInstance.setDescription( "TaskDescription" );
        taskInstance.setProcessId( "testProcess" );

        when( userTaskServicesClient.getTaskInstance( anyString(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean() ) ).thenReturn( taskInstance );
    }

    @Test
    public void testRenderProcessForm() {

        when( uiServicesClient.getProcessRawForm( anyString(),
                                                  anyString() ) ).thenReturn( formContent );

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess( "template",
                                                                                  "domain",
                                                                                  "testProcess" );

        verify( processServicesClient ).getProcessDefinition( anyString(), anyString() );
        verify( kieServicesClient ).getClassLoader();
        verify( uiServicesClient ).getProcessRawForm( anyString(), anyString() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    @Test
    public void testRenderProcessDefaultForm() {

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess( "template",
                                                                                  "domain",
                                                                                  "testProcess" );

        verify( processServicesClient ).getProcessDefinition( anyString(), anyString() );
        verify( kieServicesClient, times( 2 ) ).getClassLoader();
        verify( uiServicesClient ).getProcessRawForm( anyString(), anyString() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertNotEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    @Test
    public void testRenderProcessDefaultFormWithException() {

        when( uiServicesClient.getProcessRawForm( anyString(), anyString() ) ).thenThrow( new KieServicesException( "Unable to find form" ) );

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayProcess( "template",
                                                                                  "domain",
                                                                                  "testProcess" );

        verify( processServicesClient ).getProcessDefinition( anyString(), anyString() );
        verify( kieServicesClient ).getClassLoader();
        verify( uiServicesClient ).getProcessRawForm( anyString(), anyString() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertNotEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    @Test
    public void testRenderTaskForm() {

        when( uiServicesClient.getTaskRawForm( anyString(),
                                                  anyLong() ) ).thenReturn( formContent );


        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask( "template",
                                                                                  "domain",
                                                                                  12 );

        verify( userTaskServicesClient ).getTaskInstance( anyString(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean() );
        verify( kieServicesClient ).getClassLoader();
        verify( uiServicesClient ).getTaskRawForm( anyString(), anyLong() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    @Test
    public void testRenderTaskDefaultForm() {
        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask( "template",
                                                                               "domain",
                                                                               12 );

        verify( userTaskServicesClient ).getTaskInstance( anyString(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean() );
        verify( kieServicesClient, times( 2 ) ).getClassLoader();
        verify( uiServicesClient ).getTaskRawForm( anyString(), anyLong() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertNotEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    @Test
    public void testRenderTaskDefaultFormWithException() {
        when( uiServicesClient.getTaskRawForm( anyString(), anyLong() ) ).thenThrow( new KieServicesException( "Unable to find form" ) );

        FormRenderingSettings settings = serviceEntryPoint.getFormDisplayTask( "template",
                                                                               "domain",
                                                                               12 );

        verify( userTaskServicesClient ).getTaskInstance( anyString(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean() );
        verify( kieServicesClient ).getClassLoader();
        verify( uiServicesClient ).getTaskRawForm( anyString(), anyLong() );

        assertNotNull( "Settings cannot be null", settings );
        assertTrue( "Settings must be Static HTML", settings instanceof StaticHTMLFormRenderingSettings );

        StaticHTMLFormRenderingSettings htmlSettings = (StaticHTMLFormRenderingSettings) settings;

        assertNotEquals( "FormContent must be equal", formContent, htmlSettings.getFormContent() );
    }

    protected String getFormContent() {
        try {
            return IOUtils.toString( this.getClass().getResourceAsStream(
                    "/forms/form.ftl" ) );
        } catch ( IOException ex ) {
            fail( "Exception thrown getting form content" );
        }
        return "";
    }
}
