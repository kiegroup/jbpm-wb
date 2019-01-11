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
package org.jbpm.workbench.ht.backend.server;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteTaskServiceImplTest {

    private static final String CURRENT_USER = "Jan";

    @Mock
    IdentityProvider identityProvider;

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    KieServicesClient kieServicesClient;

    @Mock
    UserTaskServicesClient userTaskServicesClient;

    @Mock
    Event<TaskCompletedEvent> taskCompletedEvent;

    @InjectMocks
    RemoteTaskServiceImpl remoteTaskService;

    @Before
    public void initMocks() {
        when(identityProvider.getName()).thenReturn(CURRENT_USER);
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(UserTaskServicesClient.class)).thenReturn(userTaskServicesClient);
    }

    @Test
    public void testBuildTaskEventSummary() {
        final TaskEventInstance event =
                TaskEventInstance.builder().
                        id(1l).
                        taskId(2l).
                        type("UPDATED").
                        user("admin").
                        date(new Date()).
                        workItemId(3l).
                        message("message").build();

        final TaskEventSummary summary = remoteTaskService.build(event);

        assertNotNull(summary);
        assertEquals(event.getId(),
                     summary.getEventId());
        assertEquals(event.getTaskId(),
                     summary.getTaskId());
        assertEquals(event.getType(),
                     summary.getType());
        assertEquals(event.getUserId(),
                     summary.getUserId());
        assertEquals(event.getLogTime(),
                     summary.getLogTime());
        assertEquals(event.getWorkItemId(),
                     summary.getWorkItemId());
        assertEquals(event.getMessage(),
                     summary.getMessage());
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = TaskService.class.getMethods();
        for (Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            final Object[] args = new Object[method.getParameterCount()];
            Object result = method.invoke(remoteTaskService,
                                          args);

            assertMethodResult(method,
                               returnType,
                               result);

            args[0] = "";
            result = method.invoke(remoteTaskService,
                                   args);
            assertMethodResult(method,
                               returnType,
                               result);
        }
    }

    private void assertMethodResult(final Method method,
                                    final Class<?> returnType,
                                    final Object result) {
        if (Collection.class.isAssignableFrom(returnType)) {
            assertNotNull(format("Returned collection for method %s should not be null",
                                 method.getName()),
                          result);
            assertTrue(format("Returned collection for method %s should be empty",
                              method.getName()),
                       ((Collection) result).isEmpty());
        } else {
            assertNull(format("Returned object for method %s should be null",
                              method.getName()),
                       result);
        }
    }

    @Test
    public void testForward() {
        final String containerId = "containerId";
        final long taskId = 1l;
        final String userId = "user";
        final String serverTemplateId = "serverTemplateId";

        remoteTaskService.forward(serverTemplateId,
                                  containerId,
                                  taskId,
                                  userId);

        verify(userTaskServicesClient).forwardTask(containerId,
                                                   taskId,
                                                   CURRENT_USER,
                                                   userId);
        verify(kieServerIntegration).getServerClient(serverTemplateId);
    }

    @Test
    public void getTask_ReturnsSingleTaskTest() {
        final String containerId = "containerId";
        final long taskId = 1l;
        final String taskName = "taskName";
        final String serverTemplateId = "serverTemplateId";
        when(userTaskServicesClient.findTaskById(any())).thenReturn(TaskInstance.builder().id(taskId).name(taskName).build());
        TaskSummary taskSummary = remoteTaskService.getTask(serverTemplateId,
                                                            containerId,
                                                            taskId);
        verify(userTaskServicesClient).findTaskById(taskId);
        assertNotNull(taskSummary);
        assertTrue(taskId == taskSummary.getId());
        assertEquals(taskName,
                     taskSummary.getName());
    }

    @Test
    public void getTask_ReturnsNoneTasksTest() {
        final long taskId = 1l;
        final String serverTemplateId = "serverTemplateId";
        when(userTaskServicesClient.findTaskById(any())).thenReturn(null);
        TaskSummary taskSummary = remoteTaskService.getTask(serverTemplateId,
                                                            "containerId",
                                                            taskId);
        verify(userTaskServicesClient).findTaskById(taskId);
        assertNull(taskSummary);
    }

    @Test
    public void testTaskComplete() {
        final String containerId = "containerId";
        final Long taskId = 1l;
        final String serverTemplateId = "serverTemplateId";
        final Map<String, Object> output = Collections.emptyMap();

        remoteTaskService.completeTask(serverTemplateId,
                                       containerId,
                                       taskId,
                                       output);

        verify(userTaskServicesClient).completeTask(containerId,
                                                    taskId,
                                                    CURRENT_USER,
                                                    output);

        ArgumentCaptor<TaskCompletedEvent> captor = ArgumentCaptor.forClass(TaskCompletedEvent.class);
        verify(taskCompletedEvent).fire(captor.capture());

        final TaskCompletedEvent event = captor.getValue();
        assertNotNull(event);
        assertEquals(taskId,
                     event.getTaskId());
        assertEquals(containerId,
                     event.getContainerId());
        assertEquals(serverTemplateId,
                     event.getServerTemplateId());
    }

    @Test
    public void getTaskByWorkItem_ReturnsSingleTaskTest() {
        final String containerId = "containerId";
        final long workItemId = 1l;
        final String taskName = "taskName";
        final String serverTemplateId = "serverTemplateId";
        when(userTaskServicesClient.findTaskByWorkItemId(any())).thenReturn(TaskInstance.builder().id(workItemId).name(taskName).build());
        TaskSummary taskSummary = remoteTaskService.getTaskByWorkItemId(serverTemplateId,
                                                                        containerId,
                                                                        workItemId);
        verify(userTaskServicesClient).findTaskByWorkItemId(workItemId);
        assertNotNull(taskSummary);
        assertTrue(workItemId == taskSummary.getId());
        assertEquals(taskName,
                     taskSummary.getName());
    }

    @Test
    public void getTaskByWorkItemId_ReturnsNoneTasksTest() {
        final long workItemId = 1l;
        final String serverTemplateId = "serverTemplateId";
        when(userTaskServicesClient.findTaskByWorkItemId(any())).thenReturn(null);
        TaskSummary taskSummary = remoteTaskService.getTaskByWorkItemId(serverTemplateId,
                                                                        "containerId",
                                                                        workItemId);
        verify(userTaskServicesClient).findTaskByWorkItemId(workItemId);
        assertNull(taskSummary);
    }

    @Test
    public void getTaskEventsTest() {
        final long taskId = 1l;
        final String containerId = "containerId";
        final String serverTemplateId = "serverTemplateId";
        TaskEventInstance eventInstance =
                TaskEventInstance.builder()
                        .taskId(taskId)
                        .type("STARTED")
                        .user("wbadmin")
                        .workItemId(2L)
                        .date(new Date())
                        .message("")
                        .build();

        when(userTaskServicesClient.findTaskEvents(containerId,
                                                   taskId,
                                                   0,
                                                   5,
                                                   "id",
                                                   false)).thenReturn(Arrays.asList(eventInstance));
        List<TaskEventSummary> taskEventSummaries = remoteTaskService.getTaskEvents(serverTemplateId,
                                                                                    containerId,
                                                                                    taskId,
                                                                                    0,
                                                                                    5);
        verify(userTaskServicesClient).findTaskEvents(containerId,
                                                      taskId,
                                                      0,
                                                      5,
                                                      "id",
                                                      false);

        assertEquals(1,
                     taskEventSummaries.size());
        assertEquals(eventInstance.getId(),
                     taskEventSummaries.get(0).getEventId());
        assertEquals(eventInstance.getTaskId(),
                     taskEventSummaries.get(0).getTaskId());
        assertEquals(eventInstance.getType(),
                     taskEventSummaries.get(0).getType());
        assertEquals(eventInstance.getUserId(),
                     taskEventSummaries.get(0).getUserId());
        assertEquals(eventInstance.getWorkItemId(),
                     taskEventSummaries.get(0).getWorkItemId());
        assertEquals(eventInstance.getLogTime(),
                     taskEventSummaries.get(0).getLogTime());
        assertEquals(eventInstance.getMessage(),
                     taskEventSummaries.get(0).getMessage());
    }
}