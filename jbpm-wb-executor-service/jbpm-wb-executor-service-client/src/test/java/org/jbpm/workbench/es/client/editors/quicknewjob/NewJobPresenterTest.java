/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.es.client.editors.quicknewjob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.assertj.core.api.SoftAssertions;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestDataSetConstants;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewJobPresenterTest {

    private String serverTemplateId = "serverTemplateId";
    private String JOB_NAME = "JOB_NAME_1";
    private String JOB_TYPE = "JOB_TYPE_1";
    private String JOB_RETRIES = "5";

    @Mock
    private CallerMock<ExecutorService> executorServices;

    @Mock
    EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    EventSourceMock<RequestChangedEvent> requestChangedEvent;

    @Mock
    private ExecutorService executorServicesMock;

    @Mock
    private NewJobPresenter.NewJobView view;

    @InjectMocks
    private NewJobPresenter presenter;

    @Before
    public void setupMocks() {
        executorServices = new CallerMock<ExecutorService>(executorServicesMock);
        presenter.setExecutorService(executorServices);
        presenter.setNotification(notificationEvent);
        presenter.setRequestChangedEvent(requestChangedEvent);
        presenter.openNewJobDialog(serverTemplateId);
    }

    @Test
    public void testScheduleSuccessfulJob() {
        Date dueDate = new Date();
        Long jobId = Long.valueOf(1);
        when(executorServicesMock.scheduleRequest(eq(serverTemplateId),
                                                  eq(JOB_TYPE),
                                                  eq(dueDate),
                                                  any())).thenReturn(jobId);
        presenter.createJob(JOB_NAME,
                            dueDate,
                            JOB_TYPE,
                            JOB_RETRIES,
                            new ArrayList<RequestParameterSummary>());

        verify(requestChangedEvent).fire(any(RequestChangedEvent.class));
        verify(view).hide();

        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent).fire(captor.capture());
        assertEquals(1,
                     captor.getAllValues().size());
        assertEquals(NotificationEvent.NotificationType.DEFAULT,
                     captor.getValue().getType());
        assertEquals(Constants.INSTANCE.RequestScheduled(jobId),
                     captor.getValue().getNotification());
    }

    @Test
    public void testScheduleRequestParameters() {
        String param_key = "param_key";
        String param_value = "param_value";
        Date dueOnDate = new Date();
        final SoftAssertions softly = new SoftAssertions();
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                softly.assertThat(invocationOnMock.getArguments()[0]).isEqualTo(serverTemplateId);
                softly.assertThat(invocationOnMock.getArguments()[1]).isEqualTo(JOB_TYPE);
                softly.assertThat(invocationOnMock.getArguments()[2]).isEqualTo(dueOnDate);

                final HashMap<String, String> ctxValues = (HashMap) invocationOnMock.getArguments()[3];
                softly.assertThat(ctxValues.get(RequestDataSetConstants.COLUMN_BUSINESSKEY).equals(JOB_NAME));
                softly.assertThat(ctxValues.get(RequestDataSetConstants.COLUMN_RETRIES).equals(String.valueOf(JOB_RETRIES)));
                softly.assertThat(ctxValues.get(param_key).equals(String.valueOf(param_value)));
                return null;
            }
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));

        presenter.createJob(JOB_NAME,
                            dueOnDate,
                            JOB_TYPE,
                            JOB_RETRIES,
                            Arrays.asList(new RequestParameterSummary(param_key,
                                                                      param_value)));
        softly.assertAll();
        verify(executorServicesMock).scheduleRequest(anyString(),
                                                     anyString(),
                                                     any(Date.class),
                                                     any(HashMap.class));
    }

    @Test
    public void testUnsuccessfulJobScheduling() {
        String genericErrorMessage = "Unexpected error";
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw (new Exception(genericErrorMessage));
            }
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));

        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            new ArrayList());

        verify(view).cleanErrorMessages();
        verify(view).showInlineNotification(eq(genericErrorMessage));
    }

    @Test
    public void testInvalidClassError() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw (new Exception("Invalid command type"));
            }
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));

        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            new ArrayList());

        verify(view).cleanErrorMessages();
        verify(view).showInvalidTypeErrorMessage();
    }

    @Test
    public void testEmptyNameError() {
        presenter.createJob("",
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            new ArrayList());

        verify(view).cleanErrorMessages();
        verify(view).showEmptyNameErrorMessage();
        verifyNoMoreInteractions(executorServicesMock);
    }

    @Test
    public void testEmptyTypeError() {
        presenter.createJob(JOB_NAME,
                            new Date(),
                            "",
                            JOB_RETRIES,
                            new ArrayList());

        verify(view).cleanErrorMessages();
        verify(view).showEmptyTypeErrorMessage();
        verifyNoMoreInteractions(executorServicesMock);
    }

    @Test
    public void testEmptyRetriesError() {
        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            "",
                            new ArrayList());

        verify(view).cleanErrorMessages();
        verify(view).showEmptyRetriesErrorMessage();
        verifyNoMoreInteractions(executorServicesMock);
    }
}
