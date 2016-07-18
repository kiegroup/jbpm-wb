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

import java.util.Arrays;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.TaskAssignmentSummary;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAssignmentsPresenterTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";

    @Mock
    private TaskAssignmentsPresenter.TaskAssignmentsView viewMock;

    @Mock
    private User userMock;

    @Mock
    private TaskService taskService;

    private Caller<TaskService> remoteTaskServiceCaller;

    //Thing under test
    private TaskAssignmentsPresenter presenter;

    @Before
    public void initMocks() {
        when(userMock.getIdentifier()).thenReturn(CURRENT_USER);

        remoteTaskServiceCaller = new CallerMock<TaskService>(taskService);
        final Event<TaskRefreshedEvent> taskRefreshed = spy(new EventSourceMock<TaskRefreshedEvent>());
        doNothing().when(taskRefreshed).fire(any(TaskRefreshedEvent.class));

        presenter = new TaskAssignmentsPresenter(
                viewMock,
                userMock,
                remoteTaskServiceCaller,
                taskRefreshed
        );
    }

    @Test
    public void delegationButtonDisabled_whenDelegationSuccessful() {
        final long TASK_ID = 1;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_ID);
        task.setStatus("InProgress");
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        task.setDelegationAllowed(true);

        when(taskService.getTaskAssignmentDetails(anyString(), anyString(), eq(TASK_ID))).thenReturn(task);

        presenter.onTaskSelectionEvent(new TaskSelectionEvent(1L));
        presenter.delegateTask(OTHER_USER);

        verify(viewMock).setDelegateButtonActive(false);
        verify(viewMock, times(2)).enableDelegateButton(false);
    }

    @Test
    public void emptyDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask("");

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(taskService, never())
                .delegate(anyString(), anyString(), anyLong(), anyString());
    }

    @Test
    public void nullDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask(null);

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(taskService, never())
                .delegate(anyString(), anyString(), anyLong(), anyString());
    }

    @Test
    public void delegationDisabled_whenCompletedTaskSelected() {
        final long COMPLETED_TASK_ID = 1;
        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(COMPLETED_TASK_ID);
        task.setStatus("Completed");
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        when(taskService.getTaskAssignmentDetails(anyString(), anyString(), eq(COMPLETED_TASK_ID))).thenReturn(task);

        // When task in status Completed is selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(COMPLETED_TASK_ID));

        verify(viewMock, times(2)).enableDelegateButton(false);
        verify(viewMock, times(2)).enableUserOrGroupInput(false);
        verify(viewMock, never()).enableDelegateButton(true);
        verify(viewMock, never()).enableUserOrGroupInput(true);
    }

    @Test
    public void delegationDisabled_whenTaskNotOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_SOMEONE_ELSE_ID = 2;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_OWNED_BY_SOMEONE_ELSE_ID);
        task.setStatus("Ready");
        task.setActualOwner(OTHER_USER);
        task.setPotOwnersString(Arrays.asList(OTHER_USER));
        task.setDelegationAllowed(false);
        when(taskService.getTaskAssignmentDetails(anyString(), anyString(), eq(TASK_OWNED_BY_SOMEONE_ELSE_ID))).thenReturn(task);

        // When task not owned by Current user
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_OWNED_BY_SOMEONE_ELSE_ID));

        verify(viewMock, times(2)).enableDelegateButton(false);
        verify(viewMock, times(2)).enableUserOrGroupInput(false);
        verify(viewMock, never()).enableDelegateButton(true);
        verify(viewMock, never()).enableUserOrGroupInput(true);
    }

    @Test
    public void delegationEnabled_whenTaskOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_CURRENT_USER = 3;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_OWNED_BY_CURRENT_USER);
        task.setStatus("Ready");
        task.setActualOwner(CURRENT_USER);
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        task.setDelegationAllowed(true);
        when(taskService.getTaskAssignmentDetails(anyString(), anyString(), eq(TASK_OWNED_BY_CURRENT_USER))).thenReturn(task);

        // When task not owned by Current user
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_OWNED_BY_CURRENT_USER));

        final InOrder inOrder = inOrder(viewMock);
        inOrder.verify(viewMock).enableDelegateButton(false);
        inOrder.verify(viewMock).enableUserOrGroupInput(false);
        inOrder.verify(viewMock).enableDelegateButton(true);
        inOrder.verify(viewMock).enableUserOrGroupInput(true);
    }

}