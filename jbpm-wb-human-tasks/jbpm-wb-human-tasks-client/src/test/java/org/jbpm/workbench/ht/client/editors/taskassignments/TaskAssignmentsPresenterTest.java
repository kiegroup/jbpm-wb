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
package org.jbpm.workbench.ht.client.editors.taskassignments;

import java.util.Arrays;
import java.util.Date;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenterTest;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.jbpm.workbench.ht.util.TaskStatus.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAssignmentsPresenterTest extends AbstractTaskPresenterTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";

    @Mock
    private TaskAssignmentsPresenter.TaskAssignmentsView viewMock;

    @Mock
    private TaskService taskService;

    private Caller<TaskService> remoteTaskServiceCaller;

    //Thing under test
    private TaskAssignmentsPresenter presenter;

    @Override
    public AbstractTaskPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void initMocks() {
        remoteTaskServiceCaller = new CallerMock<TaskService>(taskService);
        final Event<TaskRefreshedEvent> taskRefreshed = spy(new EventSourceMock<TaskRefreshedEvent>());
        doNothing().when(taskRefreshed).fire(any(TaskRefreshedEvent.class));

        presenter = new TaskAssignmentsPresenter(
                viewMock,
                remoteTaskServiceCaller,
                taskRefreshed
        );
    }

    @Test
    public void delegationButtonDisabled_whenDelegationSuccessful() {
        final long TASK_ID = 1;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_ID);
        task.setStatus(TASK_STATUS_IN_PROGRESS.getIdentifier());
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        task.setDelegationAllowed(true);

        when(taskService.getTaskAssignmentDetails(anyString(),
                                                  anyString(),
                                                  eq(TASK_ID))).thenReturn(task);
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId",
                                                          "containerId",
                                                          TASK_ID,
                                                          "task",
                                                          true,
                                                          isForLog,
                                                          "description",
                                                          new Date(),
                                                          "Completed",
                                                          "actualOwner",
                                                          2,
                                                          1L,
                                                          "processId");

        presenter.onTaskSelectionEvent(event);
        presenter.delegateTask(OTHER_USER);

        verify(viewMock).setDelegateButtonActive(false);
        verify(viewMock,
               times(2)).enableDelegateButton(false);
    }

    @Test
    public void emptyDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask("");

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(taskService,
               never())
                .delegate(anyString(),
                          anyString(),
                          anyLong(),
                          anyString());
    }

    @Test
    public void nullDelegationUserOrGroup_notAccepted() {
        presenter.delegateTask(null);

        verify(viewMock).setHelpText(Constants.INSTANCE.DelegationUserInputRequired());
        verify(taskService,
               never())
                .delegate(anyString(),
                          anyString(),
                          anyLong(),
                          anyString());
    }

    @Test
    public void delegationDisabled_whenCompletedTaskSelected() {
        final long COMPLETED_TASK_ID = 1;
        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(COMPLETED_TASK_ID);
        task.setStatus(TASK_STATUS_COMPLETED.getIdentifier());
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        when(taskService.getTaskAssignmentDetails(anyString(),
                                                  anyString(),
                                                  eq(COMPLETED_TASK_ID))).thenReturn(task);
        task.setDelegationAllowed(false);

        // When task in status Completed is selected
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId",
                                                          "containerId",
                                                          COMPLETED_TASK_ID,
                                                          "task",
                                                          true,
                                                          isForLog,
                                                          "description",
                                                          new Date(),
                                                          "Completed",
                                                          "actualOwner",
                                                          2,
                                                          1L,
                                                          "processId");
        presenter.onTaskSelectionEvent(event);

        verify(viewMock,
               times(2)).enableDelegateButton(false);
        verify(viewMock,
               times(2)).enableUserOrGroupInput(false);
        verify(viewMock,
               never()).enableDelegateButton(true);
        verify(viewMock,
               never()).enableUserOrGroupInput(true);
    }

    @Test
    public void delegationDisabled_whenTaskNotOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_SOMEONE_ELSE_ID = 2;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_OWNED_BY_SOMEONE_ELSE_ID);
        task.setStatus(TASK_STATUS_READY.getIdentifier());
        task.setActualOwner(OTHER_USER);
        task.setPotOwnersString(Arrays.asList(OTHER_USER));
        task.setDelegationAllowed(false);
        when(taskService.getTaskAssignmentDetails(anyString(),
                                                  anyString(),
                                                  eq(TASK_OWNED_BY_SOMEONE_ELSE_ID))).thenReturn(task);

        // When task not owned by Current user
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId",
                                                          "containerId",
                                                          TASK_OWNED_BY_SOMEONE_ELSE_ID,
                                                          "task",
                                                          true,
                                                          isForLog,
                                                          "description",
                                                          new Date(),
                                                          "Completed",
                                                          "actualOwner",
                                                          2,
                                                          1L,
                                                          "processId");
        presenter.onTaskSelectionEvent(event);

        verify(viewMock,
               times(2)).enableDelegateButton(false);
        verify(viewMock,
               times(2)).enableUserOrGroupInput(false);
        verify(viewMock,
               never()).enableDelegateButton(true);
        verify(viewMock,
               never()).enableUserOrGroupInput(true);
    }

    @Test
    public void delegationEnabled_whenTaskOwnedByCurrentUserSelected() {
        final long TASK_OWNED_BY_CURRENT_USER = 3;

        final TaskAssignmentSummary task = new TaskAssignmentSummary();
        task.setTaskId(TASK_OWNED_BY_CURRENT_USER);
        task.setStatus(TASK_STATUS_READY.getIdentifier());
        task.setActualOwner(CURRENT_USER);
        task.setPotOwnersString(Arrays.asList(CURRENT_USER));
        task.setDelegationAllowed(true);
        when(taskService.getTaskAssignmentDetails(anyString(),
                                                  anyString(),
                                                  eq(TASK_OWNED_BY_CURRENT_USER))).thenReturn(task);

        // When task not owned by Current user

        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId",
                                                          "containerId",
                                                          TASK_OWNED_BY_CURRENT_USER,
                                                          "task",
                                                          true,
                                                          isForLog,
                                                          "description",
                                                          new Date(),
                                                          "Completed",
                                                          "actualOwner",
                                                          2,
                                                          1L,
                                                          "processId");
        presenter.onTaskSelectionEvent(event);

        final InOrder inOrder = inOrder(viewMock);
        inOrder.verify(viewMock).enableDelegateButton(false);
        inOrder.verify(viewMock).enableUserOrGroupInput(false);
        inOrder.verify(viewMock).enableDelegateButton(true);
        inOrder.verify(viewMock).enableUserOrGroupInput(true);
    }

    @Test
    public void taskSelectionEventIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = true;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          true,
                                                          isForLog);
        TaskAssignmentSummary ts = new TaskAssignmentSummary();
        ts.setPotOwnersString(Arrays.asList("owner1",
                                            "owner2"));
        when(taskService.getTaskAssignmentDetails(eq(serverTemplateId),
                                                  eq(containerId),
                                                  eq(taskId))).thenReturn(ts);

        presenter.onTaskSelectionEvent(event);

        verifyNoMoreInteractions(taskService);
    }

    @Test
    public void taskSelectionEventNotIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          true,
                                                          isForLog);

        presenter.onTaskSelectionEvent(event);

        verify(taskService).getTaskAssignmentDetails(serverTemplateId,
                                                     containerId,
                                                     taskId);
    }
}