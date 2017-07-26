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
package org.jbpm.workbench.ht.client.editors.taskdetailsmulti;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.forms.client.display.views.FormDisplayerView;
import org.jbpm.workbench.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.workbench.ht.client.editors.taskform.TaskFormPresenter;
import org.jbpm.workbench.forms.client.display.api.HumanTaskFormDisplayProvider;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsMultiPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String TASK_NAME = "taskName";

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

    @Before
    public void setupMocks() {
        when(taskFormPresenter.getTaskFormView()).thenReturn(taskFormViewMock);
        when(taskFormViewMock.getDisplayerView()).thenReturn(formDisplayerViewMock);
        doNothing().when(changeTitleWidgetEvent).fire(any(ChangeTitleWidgetEvent.class));
        doNothing().when(taskSelectionEvent).fire(any(TaskSelectionEvent.class));
    }

    @Test
    public void isForLogRemainsEnabledAfterRefresh() {
        //When task selected with logOnly
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(null,
                                                              null,
                                                              TASK_ID,
                                                              TASK_NAME,
                                                              false,
                                                              true));

        //Then only tab log is displayed
        verify(view).displayOnlyLogTab();
        verify(view).setAdminTabVisible(false);
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
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(null,
                                                              null,
                                                              TASK_ID,
                                                              TASK_NAME,
                                                              false,
                                                              false));

        //Then alltabs are displayed
        verify(view).displayAllTabs();
        verify(view).setAdminTabVisible(false);
        assertFalse(presenter.isForAdmin());
        assertFalse(presenter.isForLog());
        verify(taskFormPresenter,
               times(2)).getTaskFormView();

        presenter.onRefresh();
        assertFalse(presenter.isForAdmin());
        assertFalse(presenter.isForLog());
    }
}
