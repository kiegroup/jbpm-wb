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
import org.jbpm.workbench.forms.display.view.FormContentResizeListener;
import org.jbpm.workbench.forms.client.display.util.JSNIHelper;
import org.jbpm.workbench.forms.modeler.client.editors.displayers.test.TestFormModellerStartProcessDisplayerImpl;
import org.jbpm.workbench.forms.modeler.display.impl.FormModelerFormRenderingSettings;
import org.jbpm.workbench.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.workbench.pr.events.NewProcessInstanceEvent;
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
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith ( GwtMockitoTestRunner.class )
public class FormModellerStartProcessDisplayerTest {

    @GwtMock
    private FormRendererWidget rendererWidget;

    @Mock
    private FormModelerProcessStarterEntryPoint service;

    @Mock
    private FormContentResizeListener resizeListener;

    @Mock
    private EventSourceMock<NewProcessInstanceEvent> newProcessInstanceEvent;

    @GwtMock
    protected JSNIHelper jsniHelper;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    private CallerMock<FormModelerProcessStarterEntryPoint> serviceCaller;

    private TestFormModellerStartProcessDisplayerImpl displayer;

    private FormModelerFormRenderingSettings settings = new FormModelerFormRenderingSettings( "ctxtID" );

    @Before
    public void init() {
        serviceCaller = new CallerMock<>( service );

        displayer = new TestFormModellerStartProcessDisplayerImpl( rendererWidget, serviceCaller );

        displayer.setNewProcessInstanceEvent( newProcessInstanceEvent );
        displayer.setJSNIHelper( jsniHelper );
        displayer.setNotificationEvent( notificationEvent );

        displayer.setResizeListener( resizeListener );

        assertEquals( rendererWidget, displayer.getFormWidget() );
    }

    @Test
    public void testDisplay() {
        displayer.setRenderingSettings( settings );

        displayer.initDisplayer();

        verify( rendererWidget ).loadContext( settings.getContextId() );
        verify( rendererWidget ).setVisible( true );
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
    public void testStartProcessWithSettings() {
        displayer.setRenderingSettings( settings );

        displayer.startProcessFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );
        displayer.onFormSubmitted( formSubmittedEvent );
        verify( service ).startProcessFromRenderContext( anyString(), anyString(), anyString(), anyString(), anyString(), anyLong() );
        verify( newProcessInstanceEvent ).fire( any() );
        verify( jsniHelper ).notifySuccessMessage( anyString(), anyString() );
        verify( notificationEvent ).fire( any() );
        verify( service ).clearContext( settings.getContextId() );
    }

    @Test
    public void testStartProcessWithoutSettings() {
        displayer.startProcessFromDisplayer();

        verify( rendererWidget ).submitFormAndPersist();

        FormSubmittedEvent formSubmittedEvent = new FormSubmittedEvent( new FormRenderContextTO( settings.getContextId(), true, 0 ) );
        displayer.onFormSubmitted( formSubmittedEvent );
        verify( service, never() ).startProcessFromRenderContext( anyString(), anyString(), anyString(), anyString(), anyString(), anyLong() );
        verify( newProcessInstanceEvent, never() ).fire( any() );
        verify( jsniHelper, never() ).notifySuccessMessage( anyString(), anyString() );
        verify( notificationEvent, never() ).fire( any() );
        verify( service, never() ).clearContext( settings.getContextId() );
    }
}
