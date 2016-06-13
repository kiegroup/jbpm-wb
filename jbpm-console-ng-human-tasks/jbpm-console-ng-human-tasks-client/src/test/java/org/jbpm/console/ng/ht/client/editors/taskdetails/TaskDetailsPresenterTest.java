/*
 * Copyright 2016 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskKey;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.model.events.TaskCalendarEvent;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.jbpm.console.ng.ht.service.TaskQueryService;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsPresenterTest {

    @Mock
    TaskDetailsViewImpl view;
    @Mock
    TaskQueryService taskQueryServiceMock;
    @Mock
    PlaceManager placeManager;
    @Mock
    Caller<TaskOperationsService> taskOperationsService;
    @Mock
    Caller<DataServiceEntryPoint> dataServices;

    Event<TaskRefreshedEvent> taskRefreshed = new EventSourceMock<TaskRefreshedEvent>();
    Event<TaskCalendarEvent> taskCalendarEvent = new EventSourceMock<TaskCalendarEvent>();
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();
    Event<ProcessInstancesWithDetailsRequestEvent> processInstanceSelected =
            new EventSourceMock<ProcessInstancesWithDetailsRequestEvent>();

    CallerMock<TaskQueryService> taskQueryServiceCallerMock;

    private TaskDetailsPresenter presenter;

    @Before
    public void setupMocks() {
        taskQueryServiceCallerMock = new CallerMock<TaskQueryService>(taskQueryServiceMock);
        presenter = new TaskDetailsPresenter(placeManager, view, processInstanceSelected, taskQueryServiceCallerMock,
                taskOperationsService, dataServices, taskRefreshed, taskCalendarEvent, notification);
    }

    @Test
    public void disableTaskDetailEditionTest() {
        presenter.setReadOnlyTaskDetail();

        verify(view).setTaskDescriptionEnabled(false);
        verify(view).setDueDateEnabled(false);
        verify(view).setDueDateTimeEnabled(false);
        verify(view).setTaskPriorityEnabled(false);
        verify(view).setUpdateTaskVisible(false);
    }

    @Test
    public void taskDetailsInputsDisabled_WhenCompletedTaskSelected() {
        final Long completedTaskId = 1L;
        when(taskQueryServiceMock.getItem(new TaskKey(completedTaskId)))
                .thenReturn(new TaskSummary(completedTaskId, null, null,
                        "Completed", 0, "Some user", null, null, null, null, null, 0, 0, null, 0));

        //When completed task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(completedTaskId));

        verify(view).setTaskDescriptionEnabled(false);
    }

    @Test
    public void taskDetailsFilledIn_WhenTaskInReadyStateSelected() {
        final String user = "Some user", status = "Ready";
        final Long readyTaskId = 2L;
        when(taskQueryServiceMock.getItem(new TaskKey(readyTaskId)))
                .thenReturn(new TaskSummary(readyTaskId, null, null,
                        status, 0, user, null, null, null, null, null, 0, 0, null, 0));

        //When completed task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(readyTaskId));

        verify(view).setUser(user);
        verify(view).setTaskStatus(status);
        verify(view).setTaskPriority("0");
    }
}
