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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.gc.forms.client.display.displayers.util.JSNIHelper;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public abstract class AbstractStartProcessFormDisplayerTest {

    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent = new EventSourceMock<NotificationEvent>();

    @Mock
    protected EventSourceMock<NewProcessInstanceEvent> newProcessInstanceEvent = new EventSourceMock<NewProcessInstanceEvent>();

    @Mock
    protected Caller<KieSessionEntryPoint> sessionServices;

    @Mock
    protected KieSessionEntryPoint kieSessionEntryPoint;

    @Mock
    protected JSNIHelper jsniHelper;

    public abstract AbstractStartProcessFormDisplayer getStartProcessFormDisplayer();

    @Mock
    protected ErrorPopupPresenter errorPopup;
    
    @Mock
    protected FormPanel container ;
    
    @Mock
    protected FlowPanel formContainer ;
    
    @Mock
    protected FlowPanel footerButtons ;
    
    @Mock
    protected FormContentResizeListener resizeListener;
    
    @Mock
    Command onClose;
    
    @Mock
    Command onHide;

    public abstract AbstractStartProcessFormDisplayer getStartProcessFormDisplayer();

    public void setupMocks() {
        sessionServices = new CallerMock<KieSessionEntryPoint>( kieSessionEntryPoint );
        getStartProcessFormDisplayer().setSessionServices( sessionServices );
    }

    @Test
    public void testStartProcess() {
        getStartProcessFormDisplayer().setParentProcessInstanceId( 0L );
        getStartProcessFormDisplayer().startProcess( new HashMap<String, Object>() );
       
        verifyEventOnStartProcess();
        testClose();
    }

    protected void verifyEventOnStartProcess() {
        verify( newProcessInstanceEvent ).fire( any( NewProcessInstanceEvent.class ) );
        ArgumentCaptor<NotificationEvent> argument = ArgumentCaptor.forClass( NotificationEvent.class );
        verify( notificationEvent ).fire( argument.capture() );
        assertEquals( NotificationEvent.NotificationType.SUCCESS, argument.getValue().getType() );
    }
    
    @Test
    public void testStartProcessWithException() {
        when( kieSessionEntryPoint.startProcess( any( String.class ), any( String.class ), any( String.class ), any( Map.class ) ) ).thenThrow( mock( RuntimeException.class) );
        getStartProcessFormDisplayer().setParentProcessInstanceId( 0L );
        getStartProcessFormDisplayer().startProcess( new HashMap<String, Object>() );
       
        verifyEventOnStartProcessWithException();
        testClose();
    }

    protected void verifyEventOnStartProcessWithException() {
        verify( errorPopup ).showMessage( any( String.class ) );
        verify( jsniHelper ).notifyErrorMessage( any( String.class ), any( String.class ) );
    }
    
    @Test
    public void testOnSubmitButton(){
        getStartProcessFormDisplayer().onSubmit();
        verify(onHide).execute();
    }
    
    protected void testClose(){
        verify(onClose).execute();
        verify(container).clear();
        verify(formContainer).clear();
        verify(footerButtons).clear();
    }
}
