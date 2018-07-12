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
package org.jbpm.workbench.ht.client.editors.taskdetails;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsPresenterTest {

    private CallerMock<TaskService> callerMock;

    @Mock
    private TaskService taskService;

    @Mock
    private TranslationService translationService;

    @Mock
    private TaskDetailsPresenter.TaskDetailsView viewMock;

    @Mock
    private EventSourceMock<TaskRefreshedEvent> taskRefreshedEvent;

    private TaskDetailsPresenter presenter;

    @Before
    public void setup() {
        callerMock = new CallerMock<TaskService>(taskService);
        doNothing().when(taskRefreshedEvent).fire(any(TaskRefreshedEvent.class));

        presenter = new TaskDetailsPresenter(viewMock,
                                             callerMock,
                                             taskRefreshedEvent);
        presenter.setTranslationService(translationService);
    }

    @Test
    public void disableTaskDetailEditionTest() {
        presenter.setReadOnlyTaskDetail();
        verifyReadOnlyMode(1);
    }

    @Test
    public void testSetTaskDetails_isForLog() {
        when(translationService.format(any())).thenReturn("Completed");
        boolean isForLog = true;
        String status = "Completed";
        String description = "description";
        String actualOwner = "Owner";
        Date expirationTime = new Date();
        int priority = 2;

        TaskSelectionEvent event = createTestTaskSelectionEvent(isForLog,
                                                                status,
                                                                description,
                                                                actualOwner,
                                                                expirationTime,
                                                                priority);
        presenter.onTaskSelectionEvent(event);

        verify(viewMock).setSelectedDate(expirationTime);

        verifySetTaskDetails(actualOwner,
                             status,
                             String.valueOf(priority));
        verifyReadOnlyMode(1);
    }

    @Test
    public void testSetTaskDetails_statusReady() {
        when(translationService.format(any())).thenReturn("Completed");
        boolean isForLog = false;
        String status = "Completed";
        String description = "description";
        String actualOwner = "Owner";
        Date expirationTime = new Date();
        int priority = 2;

        TaskSelectionEvent event = createTestTaskSelectionEvent(isForLog,
                                                                status,
                                                                description,
                                                                actualOwner,
                                                                expirationTime,
                                                                priority);
        presenter.onTaskSelectionEvent(event);

        verify(viewMock).setSelectedDate(expirationTime);

        verifySetTaskDetails(actualOwner,
                             status,
                             String.valueOf(priority));
        verifyReadOnlyMode(0);
    }

    @Test
    public void testUpdateDetails() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = false;

        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
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

        String description = "description";
        Date dueDate = new Date();
        int priority = 3;
        presenter.updateTask(description,
                             dueDate,
                             priority);

        verify(taskService).updateTask(serverTemplateId,
                                       containerId,
                                       taskId,
                                       priority,
                                       description,
                                       dueDate);
        final ArgumentCaptor<TaskRefreshedEvent> argument = ArgumentCaptor.forClass(TaskRefreshedEvent.class);
        verify(taskRefreshedEvent).fire(argument.capture());
        assertEquals(taskId,
                     argument.getValue().getTaskId());
    }

    private void verifySetTaskDetails(String actualOwner,
                                      String status,
                                      String priority) {
        verify(viewMock).setUser(actualOwner);
        verify(viewMock).setTaskStatus(status);
        verify(viewMock).setTaskPriority(priority);
    }

    private void verifyReadOnlyMode(int i) {
        verify(viewMock,
               times(i)).setTaskDescriptionEnabled(false);
        verify(viewMock,
               times(i)).setDueDateEnabled(false);
        verify(viewMock,
               times(i)).setTaskPriorityEnabled(false);
        verify(viewMock,
               times(i)).setUpdateTaskVisible(false);
    }

    private TaskSelectionEvent createTestTaskSelectionEvent(boolean isForLog,
                                                            String status,
                                                            String description,
                                                            String actualOwner,
                                                            Date expirationTime,
                                                            int priority) {
        return new TaskSelectionEvent("serverTemplateId",
                                      "containerId",
                                      1L,
                                      "task",
                                      true,
                                      isForLog,
                                      description,
                                      expirationTime,
                                      status,
                                      actualOwner,
                                      priority,
                                      1L,
                                      "processId");
    }
}