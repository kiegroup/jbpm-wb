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
package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import org.jbpm.workbench.ht.model.TaskSummary;
import org.mockito.Spy;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;

import static org.jbpm.workbench.ht.util.TaskStatus.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TaskListPresenterTest extends AbstractTaskListPresenterTest {

    private static final String PERSPECTIVE_ID = PerspectiveIds.TASKS;

    private org.jbpm.workbench.common.client.resources.i18n.Constants commonConstants;

    @Spy
    TaskListFilterSettingsManager manager;

    @InjectMocks
    protected TaskListPresenter presenter;

    @Override
    public TaskListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AbstractTaskListFilterSettingsManager getFilterSettingsManager() {
        return manager;
    }

    @Before
    public void setupMocks() {
        super.setupMocks();
        when(perspectiveActivity.getIdentifier()).thenReturn(PERSPECTIVE_ID);
        commonConstants = org.jbpm.workbench.common.client.resources.i18n.Constants.INSTANCE;
    }

    @Test
    public void testSuspendActionCondition() {
        testTaskStatusCondition(getPresenter().getSuspendActionCondition(),
                                TASK_STATUS_RESERVED.getIdentifier(),
                                TASK_STATUS_IN_PROGRESS.getIdentifier());
    }

    @Test
    public void testResumeActionCondition() {
        testTaskStatusCondition(getPresenter().getResumeActionCondition(),
                                TASK_STATUS_SUSPENDED.getIdentifier());
    }

    @Test
    public void userShouldNotBeAbleToReleaseTasksOwnedByOthers() {
        assertFalse(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner("userx").status(TASK_STATUS_RESERVED.getIdentifier()).build()));
        assertFalse(getPresenter().getReleaseActionCondition().test(TaskSummary.builder().actualOwner("userx").status(TASK_STATUS_IN_PROGRESS.getIdentifier()).build()));
    }

    @Test
    public void testListBreadcrumbCreation() {
        presenter.createListBreadcrumb();
        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(breadcrumbs).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Home()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManager).goTo(PerspectiveIds.HOME);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Task_Inbox()),
                                          eq(Commands.DO_NOTHING));

        verifyNoMoreInteractions(breadcrumbs);
    }

    @Test
    public void testSetupDetailBreadcrumb() {
        String detailLabel = "detailLabel";
        String detailScreenId = "screenId";

        PlaceManager placeManagerMock = mock(PlaceManager.class);
        presenter.setPlaceManager(placeManagerMock);
        presenter.setupDetailBreadcrumb(placeManagerMock,
                                        commonConstants.Task_Inbox(),
                                        detailLabel,
                                        detailScreenId);

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);

        verify(breadcrumbs).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Home()),
                                          captureCommand.capture());
        captureCommand.getValue().execute();
        verify(placeManagerMock).goTo(PerspectiveIds.HOME);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(commonConstants.Task_Inbox()),
                                          captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).closePlace(detailScreenId);

        verify(breadcrumbs).addBreadCrumb(eq(PERSPECTIVE_ID),
                                          eq(detailLabel),
                                          eq(Commands.DO_NOTHING));
    }

    @Test
    public void bulkReleaseOnlyOnReservedTest() {
        List<TaskSummary> taskSummaries = new ArrayList<>();
        taskSummaries.add(createTestTaskSummary(TASK_ID, TASK_STATUS_RESERVED, identity.getIdentifier()));
        taskSummaries.add(createTestTaskSummary(TASK_ID, TASK_STATUS_RESERVED, "otherUser"));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 1, TASK_STATUS_READY, ""));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 2, TASK_STATUS_IN_PROGRESS, ""));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 3, TASK_STATUS_IN_PROGRESS, identity.getIdentifier()));

        getPresenter().bulkRelease(taskSummaries);

        verify(taskService, times(2)).releaseTask(anyString(), eq(TASK_DEPLOYMENT_ID), anyLong());
        verify(taskService).releaseTask(anyString(), eq(TASK_DEPLOYMENT_ID), eq(TASK_ID));
        verify(taskService).releaseTask(anyString(), eq(TASK_DEPLOYMENT_ID), eq(TASK_ID + 3));
    }

    @Test
    public void bulkSuspendOnlyOnReservedInProgressTest() {
        List<TaskSummary> taskSummaries = new ArrayList<>();
        taskSummaries.add(createTestTaskSummary(TASK_ID, TASK_STATUS_RESERVED, identity.getIdentifier()));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 1, TASK_STATUS_IN_PROGRESS, ""));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 2, TASK_STATUS_READY, ""));
        taskSummaries.add(createTestTaskSummary(TASK_ID + 2, TASK_STATUS_COMPLETED, ""));

        getPresenter().bulkSuspend(taskSummaries);

        verify(taskService, times(2)).suspendTask(anyString(), eq(TASK_DEPLOYMENT_ID), anyLong());
        verify(taskService).suspendTask(anyString(), eq(TASK_DEPLOYMENT_ID), eq(TASK_ID));
        verify(taskService).suspendTask(anyString(), eq(TASK_DEPLOYMENT_ID), eq(TASK_ID + 1));
    }
}