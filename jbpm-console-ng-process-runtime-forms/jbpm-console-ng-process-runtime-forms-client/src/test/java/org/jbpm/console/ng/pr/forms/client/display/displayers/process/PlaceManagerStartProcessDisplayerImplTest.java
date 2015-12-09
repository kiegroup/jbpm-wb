/*
 * Copyright 2015 JBoss Inc
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
package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class PlaceManagerStartProcessDisplayerImplTest extends AbstractStartProcessFormDisplayerTest {

    @Mock
    private EventSourceMock<RequestFormParamsEvent> requestFormParamsEvent = new EventSourceMock<RequestFormParamsEvent>();

    @Mock
    private GetFormParamsEvent event;
    
    @InjectMocks
    private PlaceManagerStartProcessDisplayerImpl placeManagerStartProcessDisplayerImpl;

    @Before
    public void setupMocks() {
        super.setupMocks();
        placeManagerStartProcessDisplayerImpl.deploymentId = "test";
        placeManagerStartProcessDisplayerImpl.processDefId = "test";
        
        when(event.getAction()).thenReturn( "startProcess" );
    }

    @Override
    public AbstractStartProcessFormDisplayer getStartProcessFormDisplayer() {
        return placeManagerStartProcessDisplayerImpl;
    }

    @Test
    public void testOnstartProcessCallback() {
        placeManagerStartProcessDisplayerImpl.startProcessCallback( event );
        verifyEventOnStartProcess();
        testClose();
    }
    
    @Test
    public void testOnstartProcessCallbackWithException() {
        when( kieSessionEntryPoint.startProcess( any( String.class ), any( String.class ), any( Map.class ) ) ).thenThrow( mock( RuntimeException.class ) );
        placeManagerStartProcessDisplayerImpl.startProcessCallback( event );
        this.verifyEventOnStartProcessWithException();
        testClose();
    }
}
