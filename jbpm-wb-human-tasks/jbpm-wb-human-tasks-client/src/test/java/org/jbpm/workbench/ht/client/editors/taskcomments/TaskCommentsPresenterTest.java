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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.ht.client.editors.AbstractTaskPresenterTest;
import org.jbpm.workbench.ht.model.CommentSummary;
import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskRefreshedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.jbpm.workbench.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TaskCommentsPresenterTest extends AbstractTaskPresenterTest {

    @Mock
    private TaskCommentsPresenter.TaskCommentsView viewMock;

    @InjectMocks
    private TaskCommentsPresenter presenter;

    @Override
    public TaskCommentsPresenter getPresenter() {
        return presenter;
    }

    private static final Long TASK_ID = 1L;
    private static final Long COMMENT_ID = 1L;

    private static final String COMMENT_1_TEXT = "Comment1";
    private static final String COMMENT_2_TEXT = "Comment2";
    private static final String COMMENT_3_TEXT = "Comment3";

    protected CallerMock<TaskService> callerMock;

    @Mock
    protected TaskService commentsService;

    @Mock
    private User identity;

    @Spy
    Event<NotificationEvent> notificationEventMock = new EventSourceMock<>();

    @Before
    public void setupMocks() {
        callerMock = new CallerMock<>(commentsService);

        getPresenter().setNotification(notificationEventMock);
        getPresenter().setTaskService(callerMock);
        getPresenter().setIdentity(identity);

        long currentTime = new Date().getTime();
        CommentSummary commentSummary1 = new CommentSummary(1L, COMMENT_1_TEXT, "user1", new Date(currentTime + 200000));
        CommentSummary commentSummary2 = new CommentSummary(2L, COMMENT_2_TEXT, "user1", new Date(currentTime + 400000));
        CommentSummary commentSummary3 = new CommentSummary(3L, COMMENT_3_TEXT, "user1", new Date(currentTime + 600000));

        when(commentsService.getTaskComments(anyString(), anyString(), anyLong()))
                .thenReturn(Arrays.asList(commentSummary1, commentSummary2, commentSummary3));
        doNothing().when(notificationEventMock).fire(any());
    }

    @Test
    public void loadCommentsOnTaskSelectedOrRefreshed() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId, containerId, TASK_ID, "task", true, isForLog,
                                                          "description", new Date(), "Ready", "actualOwner", 2, 1L,
                                                          "processId");
        getPresenter().onTaskSelectionEvent(event);

        verify(commentsService).getTaskComments(serverTemplateId, containerId, TASK_ID);

        getPresenter().onTaskRefreshedEvent(new TaskRefreshedEvent(serverTemplateId, containerId, TASK_ID));

        verify(commentsService, times(2)).getTaskComments(serverTemplateId, containerId, TASK_ID);
    }

    @Test
    public void loadCommentsSortingASC() {
        getPresenter().sortComments(true);

        ArgumentCaptor<List> comentsListASC = ArgumentCaptor.forClass(List.class);
        verify(getPresenter().getTaskCommentView()).setCommentList(comentsListASC.capture());

        assertEquals(3, comentsListASC.getValue().size());
        assertEquals(COMMENT_1_TEXT, ((CommentSummary) comentsListASC.getValue().get(0)).getText());
        assertEquals(COMMENT_2_TEXT, ((CommentSummary) comentsListASC.getValue().get(1)).getText());
        assertEquals(COMMENT_3_TEXT, ((CommentSummary) comentsListASC.getValue().get(2)).getText());
    }

    @Test
    public void loadCommentsSortingDESC() {
        getPresenter().sortComments(false);

        ArgumentCaptor<List> comentsListASC = ArgumentCaptor.forClass(List.class);
        verify(getPresenter().getTaskCommentView()).setCommentList(comentsListASC.capture());

        assertEquals(3, comentsListASC.getValue().size());
        assertEquals(COMMENT_3_TEXT, ((CommentSummary) comentsListASC.getValue().get(0)).getText());
        assertEquals(COMMENT_2_TEXT, ((CommentSummary) comentsListASC.getValue().get(1)).getText());
        assertEquals(COMMENT_1_TEXT, ((CommentSummary) comentsListASC.getValue().get(2)).getText());
    }

    @Test
    public void refreshCommentsResetsCurrentPageTest() {
        getPresenter().setCurrentPage(3);

        getPresenter().refreshCommentsView();

        verify(getPresenter().getTaskCommentView()).clearCommentInputForm();
        verify(commentsService).getTaskComments(anyString(), anyString(), anyLong());
        assertEquals(1, getPresenter().getCurrentPage());
    }

    @Test
    public void loadMoreCommentsIncreaseCurrentPageTest() {
        int currentPage = 2;
        getPresenter().setCurrentPage(currentPage);

        getPresenter().loadMoreTaskComments();

        verify(commentsService).getTaskComments(anyString(), anyString(), anyLong());
        assertEquals(currentPage + 1, getPresenter().getCurrentPage());
    }

    @Test
    public void commentInputClearedAfterCommentAddedTest() {
        String comment = "Working on it, man.";
        getPresenter().addTaskComment(comment);

        verify(commentsService).addTaskComment(anyString(), anyString(), anyLong(), eq(comment), any(Date.class));
        verify(getPresenter().getTaskCommentView()).clearCommentInputForm();
    }

    @Test
    public void removeCommentAddedTest() {
        getPresenter().removeTaskComment(COMMENT_ID);

        verify(commentsService).deleteTaskComment(anyString(), anyString(), anyLong(), eq(COMMENT_ID));
        verify(commentsService).getTaskComments(anyString(), anyString(), anyLong());
        verify(getPresenter().getTaskCommentView()).clearCommentInputForm();
        assertEquals(1, getPresenter().getCurrentPage());
    }

    @Test
    public void taskSelectionEventIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = true;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId, containerId, taskId, "task", true, isForLog);
        CommentSummary comment1 = new CommentSummary(1l, "commentText", "ByTest", new Date());
        when(commentsService.getTaskComments(eq(serverTemplateId), eq(containerId), eq(taskId))).thenReturn(Arrays.asList(comment1));

        getPresenter().onTaskSelectionEvent(event);

        verify(commentsService).getTaskComments(serverTemplateId, containerId, taskId);
        verify(getPresenter().getTaskCommentView()).disableNewComments();
    }

    @Test
    public void taskSelectionEventNotIsForLogTask() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        Long taskId = 1L;
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId, containerId, taskId, "task", true, isForLog);
        CommentSummary comment1 = new CommentSummary(1l,
                                                     "commentText", "ByTest", new Date());
        when(commentsService.getTaskComments(eq(serverTemplateId), eq(containerId), eq(taskId))).thenReturn(Arrays.asList(comment1));

        getPresenter().onTaskSelectionEvent(event);

        verify(commentsService).getTaskComments(serverTemplateId, containerId, taskId);
        verify(getPresenter().getTaskCommentView(), never()).disableNewComments();
    }

    @Test
    public void testDeleteCommentConditionForLog() {
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId", "containerId", 1L, "task", true, true);

        getPresenter().onTaskSelectionEvent(event);

        final String addedBy = "user1";
        CommentSummary comment1 = new CommentSummary(1l, "commentText", addedBy, new Date());

        when(identity.getIdentifier()).thenReturn(addedBy);

        assertFalse(getPresenter().getDeleteCondition().test(comment1));
    }

    @Test
    public void testDeleteCommentConditionForAdmin() {
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId", "containerId", 1L, "task", true, false);

        getPresenter().onTaskSelectionEvent(event);

        final String addedBy = "user1";
        CommentSummary comment1 = new CommentSummary(1l, "commentText", addedBy, new Date());

        when(identity.getIdentifier()).thenReturn("user2");

        assertTrue(getPresenter().getDeleteCondition().test(comment1));
    }

    @Test
    public void testDeleteCommentConditionForUser() {
        TaskSelectionEvent event = new TaskSelectionEvent("serverTemplateId", "containerId", 1L, "task", false, false);

        getPresenter().onTaskSelectionEvent(event);

        final String addedBy = "user1";
        CommentSummary comment1 = new CommentSummary(1l, "commentText", addedBy, new Date());

        when(identity.getIdentifier()).thenReturn(addedBy, "user2");

        assertTrue(getPresenter().getDeleteCondition().test(comment1));
        assertFalse(getPresenter().getDeleteCondition().test(comment1));
    }

    @Test
    public void completeTaskHideAddCommentTest() {
        String serverTemplateId = "serverTemplateId";
        String containerId = "containerId";
        boolean isForLog = false;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId, containerId, TASK_ID, "task", true, isForLog,
                                                          "description", new Date(), "Ready", "actualOwner", 2, 1L,
                                                          "processId");
        getPresenter().onTaskSelectionEvent(event);

        verify(commentsService).getTaskComments(serverTemplateId, containerId, TASK_ID);

        getPresenter().onTaskCompletedEvent(new TaskCompletedEvent(serverTemplateId, containerId, TASK_ID));

        verify(getPresenter().getTaskCommentView()).disableNewComments();
    }

    protected void createTaskCommentsResults(int numberOfComments) {
        List<CommentSummary> comments = new ArrayList<>();
        long id = 1L;
        long currentTime = new Date().getTime();
        for (int i = 0; i < numberOfComments; i++) {
            comments.add(new CommentSummary(id + i, "comment" + i, "user", new Date(currentTime + i * 200000)));
        }
        when(commentsService.getTaskComments(anyString(), anyString(), anyLong()))
                .thenReturn(comments);
    }

    @Test
    public void showLoadMoreButtonTest() {
        createTaskCommentsResults(getPresenter().getPageSize() + 5);

        getPresenter().refreshCommentsView();
        ArgumentCaptor<List> comentsListASC = ArgumentCaptor.forClass(List.class);
        verify(getPresenter().getTaskCommentView()).setCommentList(comentsListASC.capture());

        assertEquals(getPresenter().getPageSize(), comentsListASC.getValue().size());
        verify(getPresenter().getTaskCommentView()).showLoadButton();
    }

    @Test
    public void hideLoadMoreButtonTest() {
        createTaskCommentsResults(getPresenter().getPageSize());

        getPresenter().refreshCommentsView();
        ArgumentCaptor<List> comentsListASC = ArgumentCaptor.forClass(List.class);
        verify(getPresenter().getTaskCommentView()).setCommentList(comentsListASC.capture());

        assertEquals(getPresenter().getPageSize(), comentsListASC.getValue().size());
        verify(getPresenter().getTaskCommentView()).hideLoadButton();
    }

    @Test
    public void hideLoadMoreButtonLessItemsThanPageSizeTest() {
        createTaskCommentsResults(getPresenter().getPageSize() - 2);

        getPresenter().refreshCommentsView();
        ArgumentCaptor<List> comentsListASC = ArgumentCaptor.forClass(List.class);
        verify(getPresenter().getTaskCommentView()).setCommentList(comentsListASC.capture());

        assertEquals(getPresenter().getPageSize() - 2, comentsListASC.getValue().size());
        verify(getPresenter().getTaskCommentView()).hideLoadButton();
    }


}
