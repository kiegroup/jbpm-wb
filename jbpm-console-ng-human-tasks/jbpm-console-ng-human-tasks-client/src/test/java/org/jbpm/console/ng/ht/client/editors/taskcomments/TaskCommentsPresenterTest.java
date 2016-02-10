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
package org.jbpm.console.ng.ht.client.editors.taskcomments;

import com.google.gwtmockito.GwtMockitoTestRunner;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter.TaskCommentsView;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskCommentsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.uberfire.mocks.CallerMock;

@RunWith(GwtMockitoTestRunner.class)
public class TaskCommentsPresenterTest {

    private static final Long TASK_ID = 1L;
    private static final String USR_ID = "Jan";

    private CallerMock<TaskCommentsService> callerMock;
    @Mock
    private TaskCommentsService commentsServiceMock;
    @Mock
    private TaskCommentsView viewMock;
    @Mock
    private User userMock;

    //Thing under test
    private TaskCommentsPresenter presenter;

    @Before
    public void setupMocks() {
        when(userMock.getIdentifier())
                .thenReturn(USR_ID);

        //Mock that actually calls the callbacks
        callerMock = new CallerMock<TaskCommentsService>(commentsServiceMock);

        presenter = new TaskCommentsPresenter(viewMock, callerMock, userMock);
    }

    @Test
    public void commentsUpdatedWhenTaskSelectedOrRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        //Then comments for given task loaded & comment grid refreshed
        verify(commentsServiceMock).getAllCommentsByTaskId(TASK_ID);
        verify(viewMock).redrawDataGrid();

        //When task Refreshed
        presenter.onTaskRefreshedEvent(new TaskRefreshedEvent(TASK_ID));

        //Then comments for given task loaded & comment grid refreshed
        verify(commentsServiceMock, times(2)).getAllCommentsByTaskId(TASK_ID);
        verify(viewMock, times(2)).redrawDataGrid();
    }

    @Test
    public void emptyCommentNotAccepted() {
        //when comment input area is empty and add button is clicked
        presenter.addTaskComment("");

        //No comment is added toTaskCommentService
        verify(commentsServiceMock, never())
                .addComment(anyLong(), anyString(), anyString(), any(Date.class));
        //User notified
        verify(viewMock).displayNotification("CommentCannotBeEmpty");
    }

    @Test
    public void commentInputClearedAfterCommetAdded() {
        String comment = "Working on it, man.";
        presenter.addTaskComment(comment);

        // Comment added
        verify(commentsServiceMock)
                .addComment(anyLong(), eq(comment), eq(USR_ID), any(Date.class));
        // Input cleared
        verify(viewMock).clearCommentInput();
    }
}
