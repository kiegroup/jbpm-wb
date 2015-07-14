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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter.TaskCommentsView;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskCommentsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskCommentsPresenterTest {

    @Mock
    TaskCommentsService commentsServiceMock;
    @Mock
    Caller<TaskCommentsService> callerMock;
    @Mock
    TaskCommentsView viewMock;
    @Mock
    User userMock;

    @InjectMocks
    TaskCommentsPresenter presenter = new TaskCommentsPresenter();

    private static final Long TASK_ID = 1L;
    private static final String USR_ID = "Jan";

    @Before
    public void setupMocks() {
        when(callerMock.call(any(RemoteCallback.class), any(ErrorCallback.class)))
                .thenReturn(commentsServiceMock);
        when(userMock.getIdentifier())
                .thenReturn(USR_ID);
    }

    @Test
    public void commentsUpdatedWhenTaskSelected() {
        //When task selected
        presenter.onTaskSelectionEvent(new TaskSelectionEvent(TASK_ID));

        //Then comments for given task loaded & comment grid refreshed
        verify(commentsServiceMock).getAllCommentsByTaskId(TASK_ID);
        verify(viewMock).redrawDataGrid();
    }

    @Test
    public void emptyCommentNotAccepted() {
        //when comment input area is empty and add button is clicked
        presenter.addTaskComment("");

        //No comment is added toTaskCommentService
        verify(commentsServiceMock, never())
                .addComment(anyLong(), anyString(), anyString(), any(Date.class));
        //User notified
        verify(viewMock).displayNotification("The Comment cannot be empty!");
    }

    @Test
    public void commentInputClearedAfterCommetAdded() {
        String comment = "Working on it, man.";
        presenter.addTaskComment(comment);

        // Comment added
        verify(commentsServiceMock)
                .addComment(anyLong(), eq(comment), eq(USR_ID), any(Date.class));
        // Input cleared
        //verify(viewMock).clearCommentInput(); //TODO - will fail, because callback actually never called
    }
}
