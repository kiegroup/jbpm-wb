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

package org.jbpm.workbench.forms.modeler.client.editors.displayers;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.forms.display.view.FormContentResizeListener;
import org.jbpm.workbench.forms.client.display.util.JSNIHelper;
import org.jbpm.workbench.forms.modeler.client.editors.displayers.test.TestFormModellerTaskDisplayerImpl;
import org.jbpm.workbench.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.workbench.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith ( GwtMockitoTestRunner.class )
public class FormModellerTaskDisplayerTest {

    @Mock
    protected TaskService taskService;

    @Mock
    protected EventSourceMock<TaskRefreshedEvent> taskRefreshed;

    @Mock
    protected User identity;

    @GwtMock
    private FormRendererWidget rendererWidget;

    @Mock
    private FormModelerProcessStarterEntryPoint service;

    @Mock
    private FormContentResizeListener resizeListener;

    @GwtMock
    protected JSNIHelper jsniHelper;

    protected CallerMock<TaskService> taskServiceCaller;

    private CallerMock<FormModelerProcessStarterEntryPoint> serviceCaller;

    private TestFormModellerTaskDisplayerImpl displayer;

    private FormModelerFormRenderingSettings settings = new FormModelerFormRenderingSettings( "ctxtID" );

    @Before
    public void init() {
        serviceCaller = new CallerMock<>( service );
        taskServiceCaller = new CallerMock<>( taskService );

        displayer = new TestFormModellerTaskDisplayerImpl( rendererWidget, serviceCaller );

        displayer.setIdentity( identity );
        displayer.setJSNIHelper( jsniHelper );
        displayer.setTaskRefreshedEvent( taskRefreshed );

        displayer.setResizeListener( resizeListener );

        displayer.setTaskService( taskServiceCaller );
    }

    @Test
    public void testDisplay() {
        displayer.setRenderingSettings( settings );

        displayer.initDisplayer();

        verify( rendererWidget ).loadContext( settings.getContextId() );
        verify( rendererWidget ).setVisible( true );
        verify( rendererWidget ).asWidget();
    }

    @Test
    public void testResizeEventWithSettings() {
        displayer.setRenderingSettings( settings );

        ResizeFormcontainerEvent resizeEvent = new ResizeFormcontainerEvent();
        resizeEvent.setContext( new FormRenderContextTO( settings.getContextId() ) );
        resizeEvent.setHeight( 100 );
        resizeEvent.setWidth( 100 );
        displayer.onFormResized( resizeEvent );

        verify( rendererWidget ).resize( 100, 100 );
        verify( resizeListener ).resize( 100, 100 );
    }

    @Test
    public void testResizeEventWithoutSettings() {
        ResizeFormcontainerEvent resizeEvent = new ResizeFormcontainerEvent();
        resizeEvent.setContext( new FormRenderContextTO( settings.getContextId() ) );
        resizeEvent.setHeight( 100 );
        resizeEvent.setWidth( 100 );
        displayer.onFormResized( resizeEvent );

        verify( rendererWidget, never() ).resize( 100, 100 );
        verify( resizeListener, never() ).resize( 100, 100 );
    }

    @Test
    public void testClaimTask() {
        displayer.setRenderingSettings( settings );

        displayer.claimFromDisplayer();

        verify( service ).clearContext( settings.getContextId() );
        verify( taskService ).claimTask( anyString(), anyString(), anyLong() );
        verify( taskRefreshed ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testStartTask() {
        displayer.setRenderingSettings( settings );

        displayer.startFromDisplayer();

        verify( service ).clearContext( settings.getContextId() );
        verify( taskService ).startTask( anyString(), anyString(), anyLong() );
        verify( taskRefreshed ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testReleaseTask() {
        displayer.setRenderingSettings( settings );

        displayer.releaseFromDisplayer();

        verify( service ).clearContext( settings.getContextId() );
        verify( taskService ).releaseTask( anyString(), anyString(), anyLong() );
        verify( taskRefreshed ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testSaveTaskWithSettings() {
        displayer.setRenderingSettings( settings );

        displayer.saveStateFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );

        displayer.onFormSubmitted( formSubmittedEvent );
        verify( service ).saveTaskStateFromRenderContext( anyString(), anyString(), anyString(), anyLong() );
        verify( taskRefreshed ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testSaveTaskWithoutSettings() {
        displayer.saveStateFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );

        displayer.onFormSubmitted( formSubmittedEvent );

        verify( service, never() ).saveTaskStateFromRenderContext( anyString(), anyString(), anyString(), anyLong() );
        verify( taskRefreshed, never() ).fire( any() );
        verify( jsniHelper, never() ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testCompleteTaskWithSettings() {
        displayer.setRenderingSettings( settings );

        displayer.completeFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );

        displayer.onFormSubmitted( formSubmittedEvent );
        verify( service ).completeTaskFromContext( anyString(), anyString(), anyString(), anyLong() );
        verify( taskRefreshed, never() ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
    }

    @Test
    public void testCompleteTaskWithoutSettings() {
        displayer.completeFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );

        displayer.onFormSubmitted( formSubmittedEvent );

        verify( service, never() ).saveTaskStateFromRenderContext( anyString(), anyString(), anyString(), anyLong() );
        verify( taskRefreshed, never() ).fire( any() );
        verify( jsniHelper, never() ).notifySuccessMessage( anyString(), anyString() );
    }
}
