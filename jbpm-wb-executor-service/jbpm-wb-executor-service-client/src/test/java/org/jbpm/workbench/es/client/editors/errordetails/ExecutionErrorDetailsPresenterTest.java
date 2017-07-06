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
package org.jbpm.workbench.es.client.editors.errordetails;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.client.editors.events.ExecutionErrorSelectedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ExecutionErrorDetailsPresenterTest {

    private final String serverTemplateId = "serverTemplateTest";
    private final String deploymentId = "deploymentIdTest";
    private final String errorId = "1";
    @Mock
    protected ExecutorService executorServiceMock;
    protected Caller<ExecutorService> executorService;
    @Mock
    private ExecutionErrorDetailsViewImpl viewMock;
    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleWidgetEventMock;
    @InjectMocks
    private ExecutionErrorDetailsPresenter presenter;
    private ExecutionErrorSummary testError = createTestError(errorId,
                                                              deploymentId);

    private static ExecutionErrorSummary createTestError(String errorId,
                                                         String deploymentId) {
        return ExecutionErrorSummary.builder()
                .errorId(errorId)
                .deploymentId(deploymentId)
                .build();
    }

    @Before
    public void setupMocks() {
        executorService = new CallerMock<>(executorServiceMock);
        presenter.setExecutorService(executorService);
        presenter.setChangeTitleWidgetEvent(changeTitleWidgetEventMock);
        when(executorServiceMock.getError(serverTemplateId,
                                          deploymentId,
                                          errorId)).thenReturn(testError);
    }

    @Test
    public void testRefreshErrorDetails() {
        presenter.refreshExecutionErrorDataRemote(serverTemplateId,
                                                  deploymentId,
                                                  errorId);
        assertRefresErrorDetails();
    }

    @Test
    public void testErrorSelection() {
        presenter.onExecutionErrorSelectedEvent(new ExecutionErrorSelectedEvent(serverTemplateId,
                                                                        deploymentId,
                                                                        errorId));
        assertRefresErrorDetails();
    }

    private void assertRefresErrorDetails(){
        verify(executorServiceMock).getError(eq(serverTemplateId),
                                             eq(deploymentId),
                                             eq(errorId));
        verify(viewMock).setValue(eq(testError));

        final ArgumentCaptor<ChangeTitleWidgetEvent> captor = org.mockito.ArgumentCaptor.forClass(ChangeTitleWidgetEvent.class);
        verify(changeTitleWidgetEventMock).fire(captor.capture());
        assertEquals(testError.getProcessId() +
                             " - " + testError.getProcessInstanceId() +
                             " (" + testError.getDeploymentId() + ")",
                     captor.getValue().getTitle());
    }
}