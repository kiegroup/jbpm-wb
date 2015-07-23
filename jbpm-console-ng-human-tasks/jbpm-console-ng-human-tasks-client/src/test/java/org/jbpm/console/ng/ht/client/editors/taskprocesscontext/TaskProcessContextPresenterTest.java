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
package org.jbpm.console.ng.ht.client.editors.taskprocesscontext;

import javax.enterprise.event.Event;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.client.editors.taskprocesscontext.TaskProcessContextPresenter.TaskProcessContextView;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;

@RunWith(MockitoJUnitRunner.class)
public class TaskProcessContextPresenterTest {

    @Mock
    private TaskProcessContextView viewMock;
    @Mock
    private PlaceManager placeManager;
    @Mock
    Event<ProcessInstancesWithDetailsRequestEvent> procNavigationMock;

    private TaskProcessContextPresenter presenter;
    private static final Long TASK_ID_NO_PROCESS = 1L;
    private static final Long TASK_ID_WITH_PROC = 2L;
    private static final Long TASK_ID_NULL_DETAILS = 3L;

    TaskSummary taskNoProcess = new TaskSummary(TASK_ID_NO_PROCESS, "task without process", null, null, 0, null, null, null, null, null, null/*ProcessID*/, -1, -1 /*Proc instId*/, null, -1);
    TaskSummary taskWithProcess = new TaskSummary(TASK_ID_WITH_PROC, "task with process", null, null, 0, null, null, null, null, null, "TEST_PROCESS_ID"/*ProcessID*/, -1, 123L /*Proc inst Id*/, null, -1);

    @Before
    public void before() {
        //Task query service mock
        TaskQueryService tqs = mock(TaskQueryService.class);
        when(tqs.getItem(new TaskKey(TASK_ID_NO_PROCESS))).thenReturn(taskNoProcess);
        when(tqs.getItem(new TaskKey(TASK_ID_WITH_PROC))).thenReturn(taskWithProcess);
        when(tqs.getItem(new TaskKey(TASK_ID_NULL_DETAILS))).thenReturn(null);
        CallerMock<TaskQueryService> taskQueryServiceMock
                = new CallerMock<TaskQueryService>(tqs);

        // DataService caller mock
        CallerMock<DataServiceEntryPoint> dataServiceCallerMock
                = new CallerMock<DataServiceEntryPoint>(mock(DataServiceEntryPoint.class));

        presenter = new TaskProcessContextPresenter(
                viewMock,
                placeManager,
                taskQueryServiceMock,
                dataServiceCallerMock,
                procNavigationMock);
    }

    @Test
    public void processContextEmpty_whenTaskDetailsNull() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID_NULL_DETAILS));

        verify(viewMock).setProcessId("None");
        verify(viewMock).setProcessInstanceId("None");
        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void processContextEmtpy_whenTaskNotAssociatedWithProcess() {
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
        verify(viewMock).enablePIDetailsButton(true);
    }
}
