/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_MISSING_POM;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_UNABLE_TO_PARSE_POM;
import static org.jbpm.workbench.wi.client.workitem.ServiceTaskUploadFormPresenter.UPLOAD_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTaskUploadFormPresenterTest {

    private ServiceTaskUploadFormPresenter presenter;
    
    @Mock
    private ServiceTaskUploadFormView view;
    
    @Mock
    private ServiceTaskService serviceTaskService;
    
    @Mock    
    private EventSourceMock<NotificationEvent> notificationEvent;
    
    @Mock
    private SyncBeanManager iocManager;

    @Before
    public void before() {
        when(serviceTaskService.addServiceTasks(any())).thenReturn(new ArrayList<>());
        presenter = spy(new ServiceTaskUploadFormPresenter(view,
                                                           new CallerMock<ServiceTaskService>(serviceTaskService),
                                                           notificationEvent,
                                                           iocManager));
        presenter.init();
    }
    
    @Test
    public void testOnCloseCommand() {
        
        assertNotNull(presenter.onCloseCommand());
    }
    
    @Test
    public void testUploadMissingPom() {
        
        AbstractForm.SubmitCompleteEvent event = mock(AbstractForm.SubmitCompleteEvent.class);
        when(event.getResults()).thenReturn(UPLOAD_MISSING_POM);
        
        presenter.handleSubmitComplete(event);
        
        verify(view, times(1)).showInvalidJarNoPomWarning();
        verify(view, times(1)).hide();
        verify(serviceTaskService, never()).addServiceTasks(any());
    }
    
    @Test
    public void testUploadInvalidPom() {
        
        AbstractForm.SubmitCompleteEvent event = mock(AbstractForm.SubmitCompleteEvent.class);
        when(event.getResults()).thenReturn(UPLOAD_UNABLE_TO_PARSE_POM);
        
        presenter.handleSubmitComplete(event);
        
        verify(view, times(1)).showInvalidPomWarning();
        verify(view, times(1)).hide();
        verify(serviceTaskService, never()).addServiceTasks(any());
    }
    
    @Test
    public void testUploadFailed() {
        
        AbstractForm.SubmitCompleteEvent event = mock(AbstractForm.SubmitCompleteEvent.class);
        when(event.getResults()).thenReturn(UPLOAD_FAILED);
        
        presenter.handleSubmitComplete(event);
        
        verify(view, times(1)).showUploadFailedError();
        verify(view, times(1)).hide();
        verify(serviceTaskService, never()).addServiceTasks(any());
    }
    
    @Test
    public void testUploadSuccessful() {
        String gav = "org.test:artifact:1.0";
        AbstractForm.SubmitCompleteEvent event = mock(AbstractForm.SubmitCompleteEvent.class);
        when(event.getResults()).thenReturn(gav);
        presenter.showView(() -> {});
        presenter.handleSubmitComplete(event);
                
        verify(view, times(1)).hide();
        verify(serviceTaskService, times(1)).addServiceTasks(eq(gav));
        verify(notificationEvent, times(1)).fire(any());
    }
}
