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

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;

import org.jbpm.workbench.es.model.ErrorSummary;
import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.service.ExecutorService;
import org.jbpm.workbench.es.util.RequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static java.util.Collections.singletonList;
import static org.jbpm.workbench.es.client.editors.util.JobUtils.*;

@RunWith(GwtMockitoTestRunner.class)
public class JobDetailsPresenterTest {

    @Mock
    private JobDetailsPresenter.JobDetailsView viewMock;

    @Mock
    private ExecutorService executorServiceMock;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleWidgetEventMock;

    @InjectMocks
    private JobDetailsPresenter presenter;

    @Before
    public void setupMocks() {
        CallerMock<ExecutorService> callerMockExecutorService = new CallerMock<>(executorServiceMock);
        presenter.setExecutorService(callerMockExecutorService);
    }

    @Test
    public void testStaleJobSelected() {

        final String serverTemplate = "serverTemplate";
        RequestSummary job = createRequestSummary(RequestStatus.DONE);

        JobSelectedEvent event = new JobSelectedEvent(serverTemplate,
                                                      job.getDeploymentId(),
                                                      job.getJobId());

        when(executorServiceMock.getRequestDetails(serverTemplate,
                                                   job.getDeploymentId(),
                                                   job.getJobId())).thenReturn(null);
        presenter.onJobSelectedEvent(event);

        verifyZeroInteractions(viewMock);
        verifyZeroInteractions(changeTitleWidgetEventMock);
    }

    @Test
    public void testJobSelected() {

        String selectedServerTemplate = "serverTemplate";
        RequestSummary job = createRequestSummary();
        List<ErrorSummary> jobErrors = singletonList(createErrorSummary());
        List<RequestParameterSummary> jobParameters = singletonList(createRequestParameterSummary());

        RequestDetails requestDetails = new RequestDetails(job,
                                                           jobErrors,
                                                           jobParameters);

        JobSelectedEvent event = new JobSelectedEvent(selectedServerTemplate,
                                                      job.getDeploymentId(),
                                                      job.getJobId());

        when(executorServiceMock.getRequestDetails(selectedServerTemplate,
                                                   job.getDeploymentId(),
                                                   job.getJobId())).thenReturn(requestDetails);
        presenter.onJobSelectedEvent(event);

        ArgumentCaptor<RequestSummary> requestSummaryCaptor = ArgumentCaptor.forClass(RequestSummary.class);
        verify(viewMock).setBasicDetails(requestSummaryCaptor.capture());
        assertEquals(job,
                     requestSummaryCaptor.getValue());

        ArgumentCaptor<List> parameterListCaptor = ArgumentCaptor.forClass(List.class);
        verify(viewMock).setParameters(parameterListCaptor.capture());
        assertEquals(jobParameters,
                     parameterListCaptor.getValue());

        ArgumentCaptor<List> errorsListCaptor = ArgumentCaptor.forClass(List.class);
        verify(viewMock).setErrors(errorsListCaptor.capture());
        assertEquals(jobErrors,
                     errorsListCaptor.getValue());

        ArgumentCaptor<ChangeTitleWidgetEvent> captor = ArgumentCaptor.forClass(ChangeTitleWidgetEvent.class);
        verify(changeTitleWidgetEventMock).fire(captor.capture());
        assertEquals(job.getId() + " - " + job.getKey(),
                     captor.getValue().getTitle());
    }

    @Test
    public void getJobDetailTitleTest() {
        Long jobId = 1L;
        String key = "key";
        RequestSummary job = createRequestSummary(jobId,
                                                  key,
                                                  RequestStatus.QUEUED);

        assertEquals(job.getId() + " - " + job.getKey(),
                     presenter.getJobDetailTitle(job));
        assertEquals(jobId + " - " + key,
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
