/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.ht.client.editors.taskslist.popup;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.model.TaskKey;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TasksReassignmentPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final Long TASK_ID2 = 2L;
    private static final String TASK_NAME = "task_1";
    private static final String TASK_NAME2 = "task_2";
    private static final String SERVER_TEMPLATE_ID = "serverTemplateIdTest";
    private static final String DEPLOYMENT_ID = "deploymentIdTest";

    @Mock
    public TasksReassignmentPresenter.TasksReassignmentView view;

    @Mock
    ConfirmPopup confirmPopup;

    @Spy
    Event<TaskRefreshedEvent> taskRefreshedEvent = new EventSourceMock<>();

    @Spy
    Event<NotificationEvent> notificationEvent = new EventSourceMock<>();

    private CallerMock<TaskService> remoteTaskServiceCaller;

    @Mock
    private TaskService taskService;

    @Mock
    private PlaceRequest place;

    @Mock
    private PlaceManager placeManager;

    @InjectMocks
    private TasksReassignmentPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(taskRefreshedEvent).fire(any(TaskRefreshedEvent.class));
        doNothing().when(notificationEvent).fire(any());
        remoteTaskServiceCaller = new CallerMock<>(taskService);
        presenter.setTaskService(remoteTaskServiceCaller);
    }

    @Test
    public void reassignTasksTest() {
        String userId = "testUser";

        when(place.getParameter("serverTemplateId", "")).thenReturn(SERVER_TEMPLATE_ID);
        when(place.getParameter("deploymentIds", "")).thenReturn(DEPLOYMENT_ID + "," + DEPLOYMENT_ID);
        when(place.getParameter("taskIds", "-1")).thenReturn(TASK_ID + "," + TASK_ID2);

        //Only TASK_ID was reassigned successfully
        when(taskService.delegateTasks(anyString(), anyList(), eq(userId)))
                .thenReturn(Arrays.asList(TaskAssignmentSummary.builder().taskId(Long.valueOf(TASK_ID)).taskName(TASK_NAME).delegationAllowed(true).build(),
                                          TaskAssignmentSummary.builder().taskId(Long.valueOf(TASK_ID2)).taskName(TASK_NAME2).delegationAllowed(false).build()));

        presenter.onStartup(place);
        presenter.onOpen();
        presenter.reassignTasksToUser(userId);

        final ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(taskService).delegateTasks(eq(SERVER_TEMPLATE_ID), listCaptor.capture(), eq(userId));
        assertEquals(2, listCaptor.getValue().size());
        assertEquals(SERVER_TEMPLATE_ID, ((TaskKey) listCaptor.getValue().get(0)).getServerTemplateId());
        assertEquals(TASK_ID, ((TaskKey) listCaptor.getValue().get(0)).getTaskId());
        assertEquals(DEPLOYMENT_ID, ((TaskKey) listCaptor.getValue().get(0)).getDeploymentId());

        assertEquals(SERVER_TEMPLATE_ID, ((TaskKey) listCaptor.getValue().get(1)).getServerTemplateId());
        assertEquals(TASK_ID2, ((TaskKey) listCaptor.getValue().get(1)).getTaskId());
        assertEquals(DEPLOYMENT_ID, ((TaskKey) listCaptor.getValue().get(1)).getDeploymentId());

        final ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notificationEvent, times(2)).fire(captor.capture());

        assertEquals(2, captor.getAllValues().size());
        assertEquals(NotificationEvent.NotificationType.DEFAULT, captor.getAllValues().get(0).getType());
        assertEquals(Constants.INSTANCE.TaskWasDelegated(String.valueOf(TASK_ID), TASK_NAME, userId), captor.getAllValues().get(0).getNotification());

        assertEquals(NotificationEvent.NotificationType.WARNING, captor.getAllValues().get(1).getType());
        assertEquals(Constants.INSTANCE.ReassignmentNotAllowedOn(String.valueOf(TASK_ID2), TASK_NAME2, userId), captor.getAllValues().get(1).getNotification());
    }
}
