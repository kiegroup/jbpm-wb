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
package org.jbpm.workbench.ht.client.editors.tasklogs;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskLogsPresenterTest {

    private static final Long TASK_ID = 1L;

    @Mock
    private TaskService taskService;

    private Caller<TaskService> remoteTaskServiceCaller;

    @Mock
    private TaskLogsPresenter.TaskLogsView taskLogsView;

    private TaskLogsPresenter presenter;

    @Before
    public void setupMocks() {
        remoteTaskServiceCaller = new CallerMock<TaskService>(taskService);
        presenter = new TaskLogsPresenter(taskLogsView,
                                          remoteTaskServiceCaller);
        when(taskService.getTaskComments("",
                                         "",
                                         1l)).thenReturn(mock(List.class));
    }

    @Test
    public void logsUpdatedWhenTaskSelected() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        //Logs retrieved and text area refreshed
        verify(taskService).getTaskEvents(nullable(String.class),
                                          nullable(String.class),
                                          anyLong());
        verify(taskLogsView,
               times(2)).setLogTextAreaText(emptyList());
    }

    @Test
    public void logsUpdatedWhenTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        //When task refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent(TASK_ID));

        //Logs retrieved and text area refreshed
        verify(taskService,
               times(2)).getTaskEvents(nullable(String.class),
                                       nullable(String.class),
                                       anyLong());
        verify(taskLogsView,
               times(4)).setLogTextAreaText(emptyList());
    }

    @Test
    public void logsNotUpdatedWhenDifferentTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        //When task refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent(TASK_ID + 1));

        //Logs retrieved and text area refreshed
        verify(taskService).getTaskEvents(nullable(String.class),
                                          nullable(String.class),
                                          anyLong());
        verify(taskLogsView,
               times(2)).setLogTextAreaText(emptyList());
    }

    @Test
    public void logEventsAreFormattedProperly() {
        List<TaskEventSummary> eventSummaries = createEventSummariesForTaks(TASK_ID);
        when(taskService.getTaskEvents(nullable(String.class),
                                       nullable(String.class),
                                       eq(TASK_ID)))
                .thenReturn(eventSummaries);

        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(taskLogsView,
               times(2)).setLogTextAreaText(captor.capture());
        assertTrue(captor.getAllValues().get(0).isEmpty());
        final List logs = captor.getAllValues().get(1);
        assertNotNull(logs);
        assertEquals(3,
                     logs.size());
        assertEquals("15/12/2017 00:00: Task ADDED (Jan)",
                     logs.get(0));
        assertEquals("01/01/2018 00:00: Task UPDATED (Maria updated this task)",
                     logs.get(1));
        assertEquals("20/01/2018 00:00: Task CLAIMED (John)",
                     logs.get(2));
    }

    private List<TaskEventSummary> createEventSummariesForTaks(Long taskId) {
        TaskEventSummary added = new TaskEventSummary(
                1L,
                taskId,
                "ADDED",
                "Jan",
                3L,
                createDate(2017,
                           Month.DECEMBER,
                           15),
                "Jan created this task"
        );
        TaskEventSummary updated = new TaskEventSummary(
                2L,
                taskId,
                "UPDATED",
                "Maria",
                3L,
                createDate(2018,
                           Month.JANUARY,
                           1),
                "Maria updated this task"
        );
        TaskEventSummary claimed = new TaskEventSummary(
                3L,
                taskId,
                "CLAIMED",
                "John",
                3L,
                createDate(2018,
                           Month.JANUARY,
                           20),
                "John claimed this task"
        );
        List<TaskEventSummary> summaryList = Arrays.asList(added,
                                                           updated,
                                                           claimed);

        return summaryList;
    }

    private Date createDate(int year,
                            Month month,
                            int day) {
        LocalDate localDate = LocalDate.of(year,
                                           month,
                                           day);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
