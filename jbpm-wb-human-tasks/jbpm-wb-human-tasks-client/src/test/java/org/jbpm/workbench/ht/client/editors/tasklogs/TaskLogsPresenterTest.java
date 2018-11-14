/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenterTest;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskLogsPresenterTest extends AbstractTaskPresenterTest {

    private static final Long TASK_ID = 1L;
    private static String serverTemplateId = "serverTemplateId";
    private static String containerId = "containerId";

    @Mock
    private TaskService taskService;

    private Caller<TaskService> remoteTaskServiceCaller;

    @Mock
    private TaskLogsPresenter.TaskLogsView taskLogsView;

    @InjectMocks
    private TaskLogsPresenter presenter;

    @Override
    public AbstractTaskPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void setupMocks() {
        remoteTaskServiceCaller = new CallerMock<TaskService>(taskService);
        presenter = new TaskLogsPresenter(taskLogsView,
                                          remoteTaskServiceCaller);
        when(taskService.getTaskEvents(serverTemplateId,
                                       containerId,
                                       TASK_ID,
                                       0,
                                       10)).thenReturn(createEventSummariesForTask(TASK_ID));
    }

    @Test
    public void logsUpdatedWhenTaskSelected() {
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(serverTemplateId,
                                                              containerId,
                                                              TASK_ID));

        verify(taskService).getTaskEvents(serverTemplateId,
                                          containerId,
                                          TASK_ID,
                                          0,
                                          10);
        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(taskLogsView).setLogs(argumentDESC.capture());
        assertEquals(3,
                     argumentDESC.getValue().size());

    }

    @Test
    public void logsUpdatedWhenTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent("",
                                                              "",
                                                              TASK_ID));

        //When task refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent("",
                                                              "",
                                                              TASK_ID));

        //Logs retrieved and text area refreshed
        verify(taskService,
               times(2)).getTaskEvents(anyString(),
                                       anyString(),
                                       anyLong(),
                                       anyInt(),
                                       anyInt());
        verify(taskLogsView,
               times(2)).setLogs(emptyList());
    }

    @Test
    public void logsNotUpdatedWhenDifferentTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent("",
                                                              "",
                                                              TASK_ID));

        //When task refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent("",
                                                              "",
                                                              TASK_ID + 1));

        //Logs retrieved and text area refreshed
        verify(taskService).getTaskEvents(anyString(),
                                          anyString(),
                                          anyLong(),
                                          anyInt(),
                                          anyInt());
        verify(taskLogsView).setLogs(emptyList());
    }

    @Test
    public void loadMoreLogs() {
        List<TaskEventSummary> allLogs = new ArrayList<>();
        int testAllLogsSize = 12;
        for (int i = 0; i < testAllLogsSize; i++) {
            allLogs.add(mock(TaskEventSummary.class));
        }
        when(taskService.getTaskEvents(serverTemplateId,
                                       containerId,
                                       TASK_ID,
                                       0,
                                       10)).thenReturn(allLogs.subList(0,
                                                                       10));
        when(taskService.getTaskEvents(serverTemplateId,
                                       containerId,
                                       TASK_ID,
                                       1,
                                       10)).thenReturn(allLogs.subList(10,
                                                                       12));

        presenter.onTaskSelectionEvent(new TaskSelectionEvent(serverTemplateId,
                                                              containerId,
                                                              TASK_ID));

        ArgumentCaptor<List> argumentDESC = ArgumentCaptor.forClass(List.class);
        verify(taskLogsView).setLogs(argumentDESC.capture());
        assertEquals(presenter.getPageSize(),
                     argumentDESC.getValue().size());
        verify(taskService).getTaskEvents(serverTemplateId,
                                          containerId,
                                          TASK_ID,
                                          0,
                                          10);

        presenter.loadMoreProcessInstanceLogs();
        verify(taskService).getTaskEvents(serverTemplateId,
                                          containerId,
                                          TASK_ID,
                                          1,
                                          10);
        verify(taskLogsView,
               times(2)).setLogs(argumentDESC.capture());
        assertEquals(testAllLogsSize,
                     argumentDESC.getValue().size());

    }

    private List<TaskEventSummary> createEventSummariesForTask(Long taskId) {
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
