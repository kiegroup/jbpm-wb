/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.forms.client.display.displayers.process;

import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class FTLStartProcessDisplayerImplTest extends AbstractStartProcessFormDisplayerTest {

    @InjectMocks
    private FTLStartProcessDisplayerImpl ftlStartProcessDisplayer;

    @Before
    public void setupMocks() {
        super.setupMocks();
    }

    @Test
    public void testStartProcessWithJavaScriptObject() {
        ftlStartProcessDisplayer.startProcess( mock( JavaScriptObject.class ) );
        verifyEventOnStartProcess();
        testClose();
    }

    @Test
    public void testStartProcessWithJavaScriptObjectWithException() {
        when( kieSessionEntryPoint.startProcess( any( String.class ), any( String.class ), any( String.class ), any( Map.class ) ) ).thenThrow( mock( RuntimeException.class ) );
        ftlStartProcessDisplayer.startProcess( mock( JavaScriptObject.class ) );
        verifyEventOnStartProcessWithException();
        testClose();

    }

    @Override
    public AbstractStartProcessFormDisplayer getStartProcessFormDisplayer() {
        return ftlStartProcessDisplayer;
    }
}
