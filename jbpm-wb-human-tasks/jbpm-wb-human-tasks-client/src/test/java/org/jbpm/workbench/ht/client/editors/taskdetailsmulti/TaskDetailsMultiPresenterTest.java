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
package org.jbpm.workbench.ht.client.editors.taskdetailsmulti;

import java.util.Date;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayProvider;
import org.jbpm.workbench.forms.client.display.views.FormDisplayerView;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenter;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenterTest;
import org.jbpm.workbench.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.workbench.ht.client.editors.taskform.TaskFormPresenter;
import org.jbpm.workbench.ht.model.TaskSummary;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsMultiPresenterTest extends AbstractTaskPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String TASK_NAME = "taskName";

    @Mock
    TaskService taskServiceMock;

    Caller<TaskService> taskService;

    @Mock
    TaskFormPresenter.TaskFormView taskFormViewMock;

    @Mock
    FormDisplayerView formDisplayerViewMock;

    @Spy
    Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent = new EventSourceMock<ChangeTitleWidgetEvent>();

    @Spy
    Event<TaskSelectionEvent> taskSelectionEvent = new EventSourceMock<TaskSelectionEvent>();

    @Mock
    private TaskFormPresenter taskFormPresenter;

    @Mock
    private TaskDetailsMultiViewImpl view;

    @Mock
    @SuppressWarnings("unused")
    private HumanTaskFormDisplayProvider taskFormDisplayProvider;

    @Mock
    @SuppressWarnings("unused")
    private TaskDetailsPresenter taskDetailsPresenter;

    @InjectMocks
    private TaskDetailsMultiPresenter presenter;

    @Override
    public AbstractTaskPresenter getPresenter() {
        return presenter;
    }

    @Before
    public void setupMocks() {
        taskService = new CallerMock<>(taskServiceMock);
        presenter.setTaskDataService(taskService);
        when(taskServiceMock.getTask(anyString(), anyString(), anyLong()))
                .thenReturn(mock(TaskSummary.class));
        when(taskFormPresenter.getTaskFormView()).thenReturn(taskFormViewMock);
        when(taskFormViewMock.getDisplayerView()).thenReturn(formDisplayerViewMock);
        doNothing().when(changeTitleWidgetEvent).fire(any(ChangeTitleWidgetEvent.class));
        doNothing().when(taskSelectionEvent).fire(any(TaskSelectionEvent.class));
    }

    @Test
    public void isForLogRemainsEnabledAfterRefresh() {
        //When task selected with logOnly
        presenter.onTaskSelectionEvent(new TaskSelectionEvent("", "", TASK_ID, TASK_NAME, false, true));

        //Then only tab log is displayed
        verify(view).displayOnlyLogTab();
        verify(view).setAdminTabVisible(false);
        verify(view).resetTabs(true);
        verify(taskFormPresenter,
               never()).getTaskFormView();
        assertFalse(presenter.isForAdmin());
        assertTrue(presenter.isForLog());

        presenter.onRefresh();
        assertFalse(presenter.isForAdmin());
        assertTrue(presenter.isForLog());
    }

    @Test
    public void isForLogRemainsDisabledAfterRefresh() {
        //When task selected without logOnly
        boolean logOnly = false;
        presenter.onTaskSelectionEvent(new TaskSelectionEvent("", "", TASK_ID, TASK_NAME, false, logOnly));

        //Then alltabs are displayed
        verify(view).displayAllTabs();
        verify(view).setAdminTabVisible(false);
        verify(view).resetTabs(logOnly);
        assertFalse(presenter.isForAdmin());
        assertFalse(presenter.isForLog());
        verify(taskFormPresenter, times(2)).getTaskFormView();

        presenter.onRefresh();
        assertFalse(presenter.isForAdmin());
        assertFalse(presenter.isForLog());
    }

    @Test
    public void refreshTest() {
        Long taskId = 1L;
        String containerId = "container1.2";
        String serverTemplateId = "serverTemplateId";
        int slaCompliance = ProcessInstance.SLA_PENDING;
        TaskSummary taskSummary =
                TaskSummary.builder()
                        .deploymentId(containerId)
                        .id(taskId)
                        .name("name")
                        .description("description")
                        .expirationTime(new Date())
                        .status("Completed")
                        .actualOwner("Rob")
                        .priority(5)
                        .processInstanceId(2L)
                        .processId("Evaluation")
                        .slaCompliance(slaCompliance)
                        .build();

        when(taskServiceMock.getTask(serverTemplateId, containerId, taskId)).thenReturn(taskSummary);
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(serverTemplateId,
                                                              taskSummary.getDeploymentId(),
                                                              taskSummary.getId(),
                                                              taskSummary.getName(),
                                                              false,
                                                              false,
                                                              taskSummary.getDescription(),
                                                              taskSummary.getExpirationTime(),
                                                              taskSummary.getStatus(),
                                                              taskSummary.getActualOwner(),
                                                              taskSummary.getPriority(),
                                                              taskSummary.getProcessInstanceId(),
                                                              taskSummary.getProcessId(),
                                                              taskSummary.getSlaCompliance()));
        verify(view).displayAllTabs();
        verify(view).resetTabs(false);
        verify(view).setAdminTabVisible(false);

        presenter.onRefresh();

        verify(taskServiceMock).getTask(eq(serverTemplateId), eq(containerId), eq(taskId));

        final ArgumentCaptor<TaskSelectionEvent> taskSelectionEventArgumentCaptor = ArgumentCaptor.forClass(TaskSelectionEvent.class);
        verify(taskSelectionEvent).fire(taskSelectionEventArgumentCaptor.capture());
        assertEquals(serverTemplateId, taskSelectionEventArgumentCaptor.getValue().getServerTemplateId());
        assertEquals(taskSummary.getDeploymentId(), taskSelectionEventArgumentCaptor.getValue().getContainerId());
        assertEquals(taskSummary.getId(), taskSelectionEventArgumentCaptor.getValue().getTaskId());
        assertEquals(taskSummary.getName(), taskSelectionEventArgumentCaptor.getValue().getTaskName());
        assertEquals(taskSummary.getDescription(), taskSelectionEventArgumentCaptor.getValue().getDescription());
        assertEquals(taskSummary.getExpirationTime(), taskSelectionEventArgumentCaptor.getValue().getExpirationTime());
        assertEquals(taskSummary.getStatus(), taskSelectionEventArgumentCaptor.getValue().getStatus());
        assertEquals(taskSummary.getActualOwner(), taskSelectionEventArgumentCaptor.getValue().getActualOwner());
        assertEquals(taskSummary.getPriority(), taskSelectionEventArgumentCaptor.getValue().getPriority());
        assertEquals(taskSummary.getProcessInstanceId(), taskSelectionEventArgumentCaptor.getValue().getProcessInstanceId());
        assertEquals(taskSummary.getProcessId(), taskSelectionEventArgumentCaptor.getValue().getProcessId());
        assertEquals(taskSummary.getSlaCompliance(), taskSelectionEventArgumentCaptor.getValue().getSlaCompliance());

        presenter.onTaskSelectionEvent(taskSelectionEventArgumentCaptor.getValue());
        verify(view, times(2)).displayAllTabs();
        verify(view).resetTabs(false);
        verify(view, times(2)).setAdminTabVisible(false);
    }

    @Test
    public void refreshWithUnableTaskDetailsTest() {
        Long taskId = 1L;
        String containerId = "container1.2";
        String serverTemplateId = "serverTemplateId";
        when(taskServiceMock.getTask(serverTemplateId, containerId, taskId)).thenReturn(null);

        presenter.onTaskSelectionEvent(new TaskSelectionEvent(serverTemplateId, containerId, taskId, "task", false, false));
        verify(view).displayAllTabs();
        verify(view).resetTabs(false);

        presenter.onRefresh();

        verify(taskServiceMock).getTask(eq(serverTemplateId), eq(containerId), eq(taskId));

        verify(view).displayNotification(anyString());
        verifyNoMoreInteractions(taskSelectionEvent);
    }
}
