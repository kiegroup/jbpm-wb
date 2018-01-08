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
package org.jbpm.workbench.ht.client.editors.taskcomments;

import java.util.Arrays;
import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.ht.client.editors.taskcomments.TaskCommentsPresenter.TaskCommentsView;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskCommentsPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    private CallerMock<TaskService> callerMock;

    @Mock
    private TaskService commentsServiceMock;

    @Mock
    private TaskCommentsView viewMock;

    //Thing under test
    private TaskCommentsPresenter presenter;

    @Before
    public void setupMocks() {
        //Mock that actually calls the callbacks
        callerMock = new CallerMock<TaskService>(commentsServiceMock);

        presenter = new TaskCommentsPresenter(viewMock,
                                              callerMock);
    }

    @Test
    public void commentsUpdatedWhenTaskSelectedOrRefreshed() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          TASK_ID,
                                                          "task",
                                                          true,
                                                          isForLog,
                                                          "description",
                                                          new Date(),
                                                          "Ready",
                                                          "actualOwner",
                                                          2,
                                                          1L,
                                                          "processId");
        //When task selected
        presenter.onTaskSelectionEvent(event);

        //Then comments for given task loaded & comment grid refreshed
        verify(commentsServiceMock).getTaskComments(serverTemplateId,
                                                    containerId,
                                                    TASK_ID);
        verify(viewMock).redrawDataGrid();

        //When task Refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent(TASK_ID));

        //Then comments for given task loaded & comment grid refreshed
        verify(commentsServiceMock,
               times(2)).getTaskComments(serverTemplateId,
                                         containerId,
                                         TASK_ID);
        verify(viewMock,
               times(2)).redrawDataGrid();
    }

    @Test
    public void emptyCommentNotAccepted() {
        //when comment input area is empty and add button is clicked
        presenter.addTaskComment("");

        //No comment is added toTaskCommentService
        verify(commentsServiceMock,
               never())
                .addTaskComment(anyString(),
                                anyString(),
                                anyLong(),
                                anyString(),
                                any(Date.class));
        //User notified
        verify(viewMock).displayNotification("CommentCannotBeEmpty");
    }

    @Test
    public void commentInputClearedAfterCommetAdded() {
        String comment = "Working on it, man.";
        presenter.addTaskComment(comment);

        // Comment added
        verify(commentsServiceMock)
                .addTaskComment(nullable(String.class),
                                nullable(String.class),
                                anyLong(),
                                eq(comment),
                                any(Date.class));
        // Input cleared
        verify(viewMock).clearCommentInput();
    }

    @Test
    public void removeCommentAdded() {
        presenter.removeTaskComment(COMMENT_ID);

        // Comment removed
        verify(commentsServiceMock)
                .deleteTaskComment(nullable(String.class),
                                   nullable(String.class),
                                   anyLong(),
                                   eq(COMMENT_ID));
        // Input cleared
        verify(viewMock).clearCommentInput();
        verify(commentsServiceMock).getTaskComments(nullable(String.class),
                                                    nullable(String.class),
                                                    anyLong());
        verify(viewMock).redrawDataGrid();
    }

    @Test
    public void taskSelectionEventIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = true;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          true,
                                                          isForLog);
        CommentSummary comment1 = new CommentSummary(1,
                                                     "commentText",
                                                     "ByTest",
                                                     new Date());
        when(commentsServiceMock.getTaskComments(eq(serverTemplateId),
                                                 eq(containerId),
                                                 eq(taskId))).thenReturn(Arrays.asList(comment1));

        presenter.onTaskSelectionEvent(event);

        verifyZeroInteractions(commentsServiceMock);
    }

    @Test
    public void taskSelectionEventNotIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          true,
                                                          isForLog);
        CommentSummary comment1 = new CommentSummary(1,
                                                     "commentText",
                                                     "ByTest",
                                                     new Date());
        when(commentsServiceMock.getTaskComments(eq(serverTemplateId),
                                                 eq(containerId),
                                                 eq(taskId))).thenReturn(Arrays.asList(comment1));

        presenter.onTaskSelectionEvent(event);

        verify(commentsServiceMock).getTaskComments(serverTemplateId,
                                                    containerId,
                                                    taskId);
    }
}
