/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class FTLStartProcessDisplayerImplTest extends AbstractStartProcessFormDisplayerTest {

    @InjectMocks
    private FTLStartProcessDisplayerImpl ftlStartProcessDisplayer;

    @Override
    public AbstractStartProcessFormDisplayer getStartProcessFormDisplayer() {
        return ftlStartProcessDisplayer;
    }

    @Test
    public void testNotificationOnStartProcessWithJavaScriptObject() {
        ftlStartProcessDisplayer.startProcess( mock( JavaScriptObject.class ) );

        verify( newProcessInstanceEvent ).fire( any( NewProcessInstanceEvent.class ) );
        ArgumentCaptor<NotificationEvent> argument = ArgumentCaptor.forClass( NotificationEvent.class );
        verify( notificationEvent ).fire( argument.capture() );
        assertEquals( NotificationEvent.NotificationType.SUCCESS, argument.getValue().getType() );
    }
}
