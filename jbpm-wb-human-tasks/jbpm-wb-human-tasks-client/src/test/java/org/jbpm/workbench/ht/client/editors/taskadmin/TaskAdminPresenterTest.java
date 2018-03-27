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
package org.jbpm.workbench.ht.client.editors.taskadmin;

import java.util.Arrays;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAdminPresenterTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";
    private static final String OTHER_USER2 = "OTHER_USER2";

    @Mock
    TaskAdminPresenter.TaskAdminView viewMock;

    @Mock
    TaskService taskService;

    Caller<TaskService> remoteTaskServiceCaller;

    @Spy
    Event<TaskRefreshedEvent> taskRefreshed = new EventSourceMock<>();

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<>();

    @InjectMocks
    TaskAdminPresenter presenter;

    @Before
    public void initMocks() {
        remoteTaskServiceCaller = new CallerMock<>(taskService);
        doNothing().when(taskRefreshed).fire(any());
        doNothing().when(notification).fire(any());

        presenter.setTaskService(remoteTaskServiceCaller);
    }

    @Test
    public void taskSelectionEventIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;

        TaskAssignmentSummary ts = new TaskAssignmentSummary();
        ts.setPotOwnersString(Arrays.asList("owner1",
                                            "owner2"));
        when(taskService.getTaskAssignmentDetails(eq(serverTemplateId),
                                                  eq(containerId),
                                                  eq(taskId))).thenReturn(ts);

        boolean isForLog = true;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          true,
                                                          isForLog);

        presenter.onTaskSelectionEvent(event);

        verifyNoMoreInteractions(taskService);
    }

    @Test
    public void taskSelectionEventNotIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;

        TaskAssignmentSummary ts = new TaskAssignmentSummary();
        ts.setPotOwnersString(Arrays.asList(OTHER_USER,
                                            OTHER_USER2));
        ts.setActualOwner(CURRENT_USER);
        ts.setForwardAllowed(true);
        when(taskService.getTaskAssignmentDetails(eq(serverTemplateId),
                                                  eq(containerId),
                                                  eq(taskId))).thenReturn(ts);
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
        verify(viewMock).enableForwardButton(true);
        verify(viewMock).enableUserOrGroupText(true);
        verify(viewMock).enableReminderButton(true);
        verify(viewMock).setActualOwnerText(CURRENT_USER);
        verify(viewMock).setUsersGroupsControlsPanelText(Arrays.asList(OTHER_USER,
                                                                       OTHER_USER2));
    }

    @Test
    public void testForwardTask() {
        final String serverTemplateId = "serverTemplateId";
        final String containerId = "containerId";
        final Long taskId = 1L;
        final String entity = "user";
        final TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                                containerId,
                                                                taskId,
                                                                "task",
                                                                true,
                                                                false);

        presenter.onTaskSelectionEvent(event);
        presenter.forwardTask(entity);

        verify(taskService).forward(serverTemplateId,
                                    containerId,
                                    taskId,
                                    entity);
        verify(notification).fire(any());
        verify(taskRefreshed).fire(any());
    }

    @Test
    public void testRefreshTaskPotentialOwners() {
        final String serverTemplateId = "serverTemplateId";
        final String containerId = "containerId";
        final Long taskId = 1L;
        final TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                                containerId,
                                                                taskId,
                                                                "task",
                                                                true,
                                                                false);

        final TaskAssignmentSummary summary = new TaskAssignmentSummary();
        summary.setForwardAllowed(false);
        when(taskService.getTaskAssignmentDetails(serverTemplateId,
                                                  containerId,
                                                  taskId)).thenReturn(summary);

        presenter.onTaskSelectionEvent(event);

        InOrder inOrder = inOrder(viewMock);

        inOrder.verify(viewMock).enableReminderButton(false);
        inOrder.verify(viewMock).enableForwardButton(false);
        inOrder.verify(viewMock).enableUserOrGroupText(false);
        inOrder.verify(viewMock).setUsersGroupsControlsPanelText(emptyList());
        inOrder.verify(viewMock).clearUserOrGroupText();
        inOrder.verify(viewMock).setActualOwnerText("");

        inOrder.verify(viewMock).setUsersGroupsControlsPanelText(singletonList(Constants.INSTANCE.No_Potential_Owners()));
        inOrder.verify(viewMock).enableForwardButton(false);
        inOrder.verify(viewMock).enableUserOrGroupText(false);
        inOrder.verify(viewMock).enableReminderButton(false);
        inOrder.verify(viewMock).setActualOwnerText(Constants.INSTANCE.No_Actual_Owner());
    }
}