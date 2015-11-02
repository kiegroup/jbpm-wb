/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.client.editors.taskassignments;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskLifeCycleService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAssignmentsPresenterTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";

    @Mock
    private TaskAssignmentsPresenter.TaskAssignmentsView viewMock;
    @Mock
    private User userMock;
    @Mock
    private TaskLifeCycleService lifecycleServiceMock;
    private Caller<TaskLifeCycleService> lifecycleServiceCallerMock;
    @Mock
    private TaskOperationsService operationsServiceMock;
    private Caller<TaskOperationsService> operationsServiceCallerMock;

    //Thing under test
    private TaskAssignmentsPresenter presenter;

    @Before
    public void initMocks() {
        when(userMock.getIdentifier())
                .thenReturn(CURRENT_USER);

        lifecycleServiceCallerMock = new CallerMock<TaskLifeCycleService>(lifecycleServiceMock);
        operationsServiceCallerMock = new CallerMock<TaskOperationsService>(operationsServiceMock);

        presenter = new TaskAssignmentsPresenter(
                viewMock, userMock,
                lifecycleServiceCallerMock,
                operationsServiceCallerMock,
                new MockEventTaskRefreshed()
        );
    }

    @Test
    public void delegationButtonDisabled_whenDelegationSuccessfull() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(1L));
        presenter.delegateTask(OTHER_USER);

        verify(viewMock).setDelegateButtonActive(false);
        verify(viewMock, times(2)).enableDelegateButton(false);
    }

    @Test
    public void emptyDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask("");

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(lifecycleServiceMock, never())
                .delegate(anyLong(), anyString(), anyString());
    }

    @Test
    public void nullDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask(null);

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(lifecycleServiceMock, never())
                .delegate(anyLong(), anyString(), anyString());
    }

    @Test
    public void delegationDisabled_whenCompletedTaskSelected() {
        final long COMPLETED_TASK_ID = 1;
        when(operationsServiceMock.getTaskDetails(COMPLETED_TASK_ID))
                .thenReturn(new TaskSummary(COMPLETED_TASK_ID, null, null,
                                "Completed"/*status*/, 0, CURRENT_USER/*actual owner*/,
                                null, null, null, null, null, 0, 0, null, 0));

        // When task in status Completed is selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(COMPLETED_TASK_ID));

        verify(viewMock).enableDelegateButton(false);
        verify(viewMock).enableUserOrGroupInput(false);
    }

    @Test
    public void delegationDisabled_whenTaskNotOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_SOMEONE_ELSE_ID = 2;
        when(operationsServiceMock.getTaskDetails(TASK_OWNED_BY_SOMEONE_ELSE_ID))
                .thenReturn(new TaskSummary(TASK_OWNED_BY_SOMEONE_ELSE_ID, null,
                                null, "Ready"/*status*/, 0, OTHER_USER/*actual owner*/,
                                null, null, null, null, null, 0, 0, null, 0));

        // When task not owned by Current user
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_OWNED_BY_SOMEONE_ELSE_ID));

        verify(viewMock).enableDelegateButton(false);
        verify(viewMock).enableUserOrGroupInput(false);
    }

    @Test
    public void delegationEnabled_whenTaskOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_CURRENT_USER = 3;
        when(operationsServiceMock.getTaskDetails(TASK_OWNED_BY_CURRENT_USER))
                .thenReturn(new TaskSummary(TASK_OWNED_BY_CURRENT_USER, null,
                                null, "Ready"/*status*/, 0, CURRENT_USER/*actual owner*/,
                                null, null, null, null, null, 0, 0, null, 0));

        // When task not owned by Current user
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_OWNED_BY_CURRENT_USER));

        verify(viewMock).enableDelegateButton(true);
        verify(viewMock).enableUserOrGroupInput(true);
    }

    private static class MockEventTaskRefreshed extends EventSourceMock<TaskRefreshedEvent> {

        @Override
        public void fire(TaskRefreshedEvent t) {
        }
    }
}
