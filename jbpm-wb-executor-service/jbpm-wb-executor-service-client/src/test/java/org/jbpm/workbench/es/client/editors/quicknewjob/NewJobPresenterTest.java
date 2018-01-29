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
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewJobPresenterTest {

    private String serverTemplateId = "serverTemplateId";
    private String JOB_NAME = "JOB_NAME_1";
    private String JOB_TYPE = "org.jbpm.executor.commands.PrintOutCommand";
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
    public void testScheduleJobSuccess_basicParametersOnly() {
        final Date dueDate = new Date();
        final Long firstJobId = 1L;
        final Long secondJobId = 2L;
        when(executorServicesMock.scheduleRequest(eq(serverTemplateId),
                                                  eq(JOB_TYPE),
                                                  eq(dueDate),
                                                  any())).thenReturn(firstJobId,
                                                                     secondJobId);
        presenter.createJob("firstJob",
                            dueDate,
                            JOB_TYPE,
                            JOB_RETRIES,
                            emptyList());
        presenter.createJob("secondJob",
                            dueDate,
                            JOB_TYPE,
                            JOB_RETRIES,
                            null);

        verify(view).show();
        verify(view,
               times(2)).cleanErrorMessages();

        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent,
               times(2)).fire(captor.capture());
        assertEquals(2,
                     captor.getAllValues().size());
        assertEquals(NotificationEvent.NotificationType.DEFAULT,
                     captor.getAllValues().get(0).getType());
        assertEquals(NotificationEvent.NotificationType.DEFAULT,
                     captor.getAllValues().get(1).getType());
        assertEquals(Constants.INSTANCE.RequestScheduled(firstJobId),
                     captor.getAllValues().get(0).getNotification());
        assertEquals(Constants.INSTANCE.RequestScheduled(secondJobId),
                     captor.getAllValues().get(1).getNotification());

        verify(requestChangedEvent,
               times(2)).fire(any(RequestChangedEvent.class));
        verify(view,
               times(2)).hide();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testScheduleJobSuccess_withAdvancedParameters() {
        final String editMessage = "ClickToEdit";
        final String paramKey = "paramKey";
        final String paramValue = "paramValue";
        final Date dueOnDate = new Date();
        final SoftAssertions softly = new SoftAssertions();

        doAnswer(invocation -> {
            softly.assertThat(invocation.getArguments()[0]).isEqualTo(serverTemplateId);
            softly.assertThat(invocation.getArguments()[1]).isEqualTo(JOB_TYPE);
            softly.assertThat(invocation.getArguments()[2]).isEqualTo(dueOnDate);

            final HashMap<String, String> ctxValues = (HashMap) invocation.getArguments()[3];
            softly.assertThat(ctxValues).hasSize(3);
            softly.assertThat(ctxValues.get(RequestDataSetConstants.COLUMN_BUSINESSKEY).equals(JOB_NAME));
            softly.assertThat(ctxValues.get(RequestDataSetConstants.COLUMN_RETRIES).equals(JOB_RETRIES));
            softly.assertThat(ctxValues.get(paramKey).equals(paramValue));
            return null;
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));
        presenter.createJob(JOB_NAME,
                            dueOnDate,
                            JOB_TYPE,
                            JOB_RETRIES,
                            Arrays.asList(new RequestParameterSummary(paramKey,
                                                                      paramValue),
                                          new RequestParameterSummary("paramKeyOnly",
                                                                      editMessage),
                                          new RequestParameterSummary(editMessage,
                                                                      "paramValueOnly"),
                                          new RequestParameterSummary(editMessage,
                                                                      editMessage)));
        softly.assertAll();
    }

    @Test
    public void testScheduleJobFailure_unknownError() {
        final String genericErrorMessage = "Unknown error";

        doAnswer(invocation -> {
            throw new Exception(genericErrorMessage);
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));
        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            emptyList());

        verify(view).show();
        verify(view).cleanErrorMessages();
        verify(view).showBasicPane();
        verify(view).showInlineNotification(eq(genericErrorMessage));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testScheduleJobFailure_invalidClassError() {
        doAnswer(invocation -> {
            throw new Exception("Invalid command type");
        }).when(executorServicesMock).scheduleRequest(anyString(),
                                                      anyString(),
                                                      any(Date.class),
                                                      any(Map.class));
        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            emptyList());

        verify(view).show();
        verify(view).cleanErrorMessages();
        verify(view).showBasicPane();
        verify(view).showInvalidTypeErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testScheduleJobFailure_emptyNameError() {
        presenter.createJob("",
                            new Date(),
                            JOB_TYPE,
                            JOB_RETRIES,
                            emptyList());

        verify(view).show();
        verify(view).cleanErrorMessages();
        verify(view).showEmptyNameErrorMessage();

        verifyZeroInteractions(executorServicesMock);

        verify(view).showBasicPane();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testScheduleJobFailure_emptyTypeError() {
        presenter.createJob(JOB_NAME,
                            new Date(),
                            "",
                            JOB_RETRIES,
                            emptyList());

        verify(view).show();
        verify(view).cleanErrorMessages();
        verify(view).showEmptyTypeErrorMessage();

        verifyZeroInteractions(executorServicesMock);

        verify(view).showBasicPane();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testScheduleJobFailure_emptyRetriesError() {
        presenter.createJob(JOB_NAME,
                            new Date(),
                            JOB_TYPE,
                            "",
                            emptyList());

        verify(view).show();
        verify(view).cleanErrorMessages();
        verify(view).showEmptyRetriesErrorMessage();

        verifyZeroInteractions(executorServicesMock);

        verify(view).showBasicPane();
        verifyNoMoreInteractions(view);
    }
}
