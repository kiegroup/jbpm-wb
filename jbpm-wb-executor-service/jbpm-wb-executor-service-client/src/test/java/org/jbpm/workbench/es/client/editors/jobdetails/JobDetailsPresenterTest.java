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
package org.jbpm.workbench.es.client.editors.jobdetails;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;

import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.RequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class JobDetailsPresenterTest {

    @Mock
    public JobDetailsPresenter.JobDetailsView view;

    @Mock
    PlaceManager placeManager;
    private CallerMock<ExecutorService> callerMockExecutorService;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleWidgetEventMock;

    @InjectMocks
    private JobDetailsPresenter presenter;

    @Before
    public void setupMocks() {
        callerMockExecutorService = new CallerMock<>(executorServiceMock);
        presenter.setExecutorService(callerMockExecutorService);
    }

    @Test
    public void testOnSelectionEvent() {
        String selectedServerTemplate = "serverTemplate";
        Long jobId = 1L;
        String deploymentId = "evaluation.1.0.1";
        String key = "key";
        RequestSummary job = new RequestSummary(jobId,
                                                new Date(),
                                                RequestStatus.QUEUED,
                                                "commandName",
                                                "Message",
                                                key,
                                                1,
                                                0,
                                                "processName",
                                                1L,
                                                "processInstanceDescription",
                                                deploymentId);

        RequestDetails requestDetails = new RequestDetails(job,
                                                           null,
                                                           null);
        JobSelectedEvent event = new JobSelectedEvent(selectedServerTemplate,
                                                      job.getDeploymentId(),
                                                      job.getJobId());

        when(placeManager.getStatus(any(DefaultPlaceRequest.class))).thenReturn(PlaceStatus.CLOSE);
        when(executorServiceMock.getRequestDetails(selectedServerTemplate,
                                                   deploymentId,
                                                   jobId)).thenReturn(requestDetails);
        presenter.onJobSelectedEvent(event);

        verify(executorServiceMock).getRequestDetails(selectedServerTemplate,
                                                      deploymentId,
                                                      jobId);
        verify(view).setValue(requestDetails);

        ArgumentCaptor<ChangeTitleWidgetEvent> captor = ArgumentCaptor.forClass(ChangeTitleWidgetEvent.class);
        verify(changeTitleWidgetEventMock).fire(captor.capture());
        assertEquals(job.getId() + " - " + job.getKey(),
                     captor.getValue().getTitle());
        assertEquals(jobId + " - " + key,
                     captor.getValue().getTitle());
    }

    @Test
    public void getJobDetailTitleTest() {
        Long jobId = 1L;
        String key = "key";
        RequestSummary job = new RequestSummary();
        job.setId(jobId);

        job.setKey(key);
        assertEquals(job.getId() + " - " + job.getKey() ,
                     presenter.getJobDetailTitle(job));
        assertEquals(jobId + " - " + key ,
                     presenter.getJobDetailTitle(job));
        assertEquals("1 - key",
                     presenter.getJobDetailTitle(job));

        job.setKey(null);
        assertEquals(job.getId().toString(),
                     presenter.getJobDetailTitle(job));
        assertEquals(jobId.toString(),
                     presenter.getJobDetailTitle(job));
        assertEquals("1",
                     presenter.getJobDetailTitle(job));
    }
}
