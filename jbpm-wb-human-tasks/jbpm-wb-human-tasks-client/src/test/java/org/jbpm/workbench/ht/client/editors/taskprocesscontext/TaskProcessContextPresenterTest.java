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
package org.jbpm.workbench.ht.client.editors.taskprocesscontext;

import java.util.Collections;
import javax.enterprise.event.Event;

import com.google.common.collect.Sets;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.jbpm.workbench.pr.events.ProcessInstancesWithDetailsRequestEvent;
import org.jbpm.workbench.pr.model.ProcessInstanceKey;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskProcessContextPresenterTest {

    private static final Long TASK_ID_NO_PROCESS = 1L;
    private static final Long TASK_ID_WITH_PROC = 2L;
    private static final Long TASK_ID_NULL_DETAILS = 3L;

    @Mock
    public User identity;

    TaskSummary taskNoProcess = TaskSummary.builder().id(TASK_ID_NO_PROCESS).name("task without process").build();
    TaskSummary taskWithProcess = TaskSummary.builder().id(TASK_ID_WITH_PROC).name("task with process").processId("TEST_PROCESS_ID").processInstanceId(123L).build();

    @Mock
    ProcessRuntimeDataService dataServiceEntryPoint;

    @Mock
    Event<ProcessInstancesWithDetailsRequestEvent> procNavigationMock;

    @Mock
    private TaskProcessContextPresenter.TaskProcessContextView viewMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private AuthorizationManager authorizationManager;

    private TaskProcessContextPresenter presenter;

    @Before
    public void before() {
        //Task query service mock
        TaskService tqs = mock(TaskService.class);
        when(tqs.getTask(null,
                         null,
                         TASK_ID_NO_PROCESS)).thenReturn(taskNoProcess);
        when(tqs.getTask(null,
                         null,
                         TASK_ID_WITH_PROC)).thenReturn(taskWithProcess);
        when(tqs.getTask(null,
                         null,
                         TASK_ID_NULL_DETAILS)).thenReturn(null);
        CallerMock<TaskService> taskQueryServiceMock
                = new CallerMock<TaskService>(tqs);

        // DataService caller mock
        CallerMock<ProcessRuntimeDataService> dataServiceCallerMock = new CallerMock<ProcessRuntimeDataService>(dataServiceEntryPoint);

        presenter = new TaskProcessContextPresenter(
                viewMock,
                placeManager,
                taskQueryServiceMock,
                dataServiceCallerMock,
                procNavigationMock,
                activityManager,
                authorizationManager,
                identity);
    }

    @Test
    public void processContextEmpty_whenTaskDetailsNull() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID_NULL_DETAILS));

        verify(viewMock).setProcessId("None");
        verify(viewMock).setProcessInstanceId("None");
        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void processContextEmpty_whenTaskNotAssociatedWithProcess() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID_NO_PROCESS));

        verify(viewMock).setProcessId("None");
        verify(viewMock).setProcessInstanceId("None");
        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void processContextShowsProcessInfo_whenTaskDetailsHasProcess() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID_WITH_PROC));

        verify(viewMock).setProcessId("TEST_PROCESS_ID");
        verify(viewMock).setProcessInstanceId("123");
    }

    @Test
    public void testGoToProcessInstanceDetails() {
        final ProcessInstanceSummary summary = new ProcessInstanceSummary();
        summary.setDeploymentId("deploymentId");
        summary.setProcessInstanceId(-1l);
        summary.setProcessId("processId");
        summary.setProcessName("processName");
        summary.setState(1);
        when(dataServiceEntryPoint.getProcessInstance(anyString(),
                                                      any(ProcessInstanceKey.class))).thenReturn(summary);

        presenter.goToProcessInstanceDetails();

        verify(placeManager).goTo(PerspectiveIds.PROCESS_INSTANCES);
        final ArgumentCaptor<ProcessInstancesWithDetailsRequestEvent> eventCaptor = ArgumentCaptor.forClass(ProcessInstancesWithDetailsRequestEvent.class);
        verify(procNavigationMock).fire(eventCaptor.capture());
        final ProcessInstancesWithDetailsRequestEvent event = eventCaptor.getValue();
        assertEquals(summary.getDeploymentId(),
                     event.getDeploymentId());
        assertEquals(summary.getProcessInstanceId(),
                     event.getProcessInstanceId());
        assertEquals(summary.getProcessId(),
                     event.getProcessDefId());
        assertEquals(summary.getProcessName(),
                     event.getProcessDefName());
        assertEquals(summary.getState(),
                     event.getProcessInstanceStatus());
    }

    @Test
    public void testProcessContextEnabled() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(true);
        when(activityManager.getActivities(any(PlaceRequest.class))).thenReturn(Sets.newHashSet(mock(Activity.class)));

        presenter.init();

        verify(viewMock).enablePIDetailsButton(true);
    }

    @Test
    public void testProcessContextDisabled() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(true);
        when(activityManager.getActivities(any(PlaceRequest.class))).thenReturn(Collections.<Activity>emptySet());

        presenter.init();

        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void testProcessContextDisabledWhenUserHasNoPermission() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(false);

        presenter.init();

        verify(viewMock).enablePIDetailsButton(false);
    }
}