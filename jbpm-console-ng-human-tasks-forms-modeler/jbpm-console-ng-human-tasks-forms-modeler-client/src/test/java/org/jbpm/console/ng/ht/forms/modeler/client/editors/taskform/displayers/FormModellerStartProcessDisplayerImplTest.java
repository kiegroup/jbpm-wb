/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.forms.modeler.client.editors.taskform.displayers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ht.forms.modeler.service.FormModelerProcessStarterEntryPoint;
import org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayer;
import org.jbpm.console.ng.pr.forms.client.display.displayers.process.AbstractStartProcessFormDisplayerTest;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class FormModellerStartProcessDisplayerImplTest extends AbstractStartProcessFormDisplayerTest {
    
    private Caller<FormModelerProcessStarterEntryPoint> renderContextServices;

    @Mock
    private FormSubmittedEvent event;
    
    @Mock
    private FormRenderContextTO context;
            
    @Mock
    private FormModelerProcessStarterEntryPoint formModelerProcessStarterEntryPointMock;

    @Mock
    private FormRendererWidget formRenderer;

    @InjectMocks
    private FormModellerStartProcessDisplayerImpl formModellerStartProcessDisplayer;

    @Before
    public void setupMocks() {
        super.setupMocks();
        renderContextServices = new CallerMock<FormModelerProcessStarterEntryPoint>( formModelerProcessStarterEntryPointMock );
        formModellerStartProcessDisplayer.setRenderContextServices( renderContextServices );
        formModellerStartProcessDisplayer.action = "startProcess";
        
        when(event.isMine( any(String.class) )).thenReturn( true );
        when(event.getContext()).thenReturn( context );
        when(context.getErrors()).thenReturn( new Integer(0) );
    }

    @Override
    public AbstractStartProcessFormDisplayer getStartProcessFormDisplayer() {
        return formModellerStartProcessDisplayer;
    }

    @Test
    public void testOnFormSubmitted() {
        formModellerStartProcessDisplayer.onFormSubmitted( event );
        verifyEventOnStartProcess();
        testFormModellerClose();
    }

    @Test
    public void testOnFormSubmittedWithException() {
        when( formModelerProcessStarterEntryPointMock.startProcessFromRenderContext( any( String.class ), any( String.class ),
                any( String.class ), any( String.class ), any( Long.class ) ) ).thenThrow( mock( RuntimeException.class ) );
        formModellerStartProcessDisplayer.onFormSubmitted( event );
        verifyEventOnStartProcessWithException();
        testFormModellerClose();
    }

    private void testFormModellerClose() {
        verify( formModelerProcessStarterEntryPointMock ).clearContext( any( String.class ) );
        super.testClose();
    }
}
