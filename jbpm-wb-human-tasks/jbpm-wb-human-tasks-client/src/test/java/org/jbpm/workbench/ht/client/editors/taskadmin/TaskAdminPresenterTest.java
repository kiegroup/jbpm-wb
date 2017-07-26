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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAdminPresenterTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";
    private static final String OTHER_USER2 = "OTHER_USER2";

    @Mock
    private TaskAdminPresenter.TaskAdminView viewMock;

    @Mock
    private TaskService taskService;

    private Caller<TaskService> remoteTaskServiceCaller;

    @Mock
    private EventSourceMock<TaskRefreshedEvent> taskRefreshedEvent;

    private TaskAdminPresenter presenter;

    @Before
    public void initMocks() {
        remoteTaskServiceCaller = new CallerMock<TaskService>(taskService);
        doNothing().when(taskRefreshedEvent).fire(any(TaskRefreshedEvent.class));

        presenter = new TaskAdminPresenter(viewMock,
                                           remoteTaskServiceCaller,
                                           taskRefreshedEvent);
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
        verify(viewMock).setUsersGroupsControlsPanelText("[" + OTHER_USER + ", " + OTHER_USER2 + "]");
    }
}