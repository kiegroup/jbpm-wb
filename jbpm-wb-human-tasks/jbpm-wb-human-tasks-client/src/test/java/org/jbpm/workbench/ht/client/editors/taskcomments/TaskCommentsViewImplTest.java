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

import com.google.gwtmockito.GwtMockitoTestRunner;

import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.FormGroup;
import org.uberfire.client.views.pfly.widgets.ValidationState;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskCommentsViewImplTest {

    @InjectMocks
    private TaskCommentsViewImpl view;

    @Mock
    private TaskCommentsPresenter presenter;

    @Mock
    private TextArea newCommentTextArea;

    @Mock(name = "newCommentTextAreaHelp")
    private Span newCommentTextAreaHelp;

    @Mock
    private FormGroup newCommentTextAreaGroup;

    @Before
    public void setupMocks() {
    }

    @Test
    public void fieldValidationErrorTest() {

        when(newCommentTextArea.getValue()).thenReturn("");
        view.submitCommentAddition();

        final InOrder inOrder = inOrder(newCommentTextAreaHelp);
        inOrder.verify(newCommentTextAreaHelp).setTextContent("");
        inOrder.verify(newCommentTextAreaHelp).setTextContent(Constants.INSTANCE.CommentCannotBeEmpty());

        final InOrder inOrderValidationState = inOrder(newCommentTextAreaGroup);
        inOrderValidationState.verify(newCommentTextAreaGroup).clearValidationState();
        inOrderValidationState.verify(newCommentTextAreaGroup).setValidationState(ValidationState.ERROR);

        verify(newCommentTextArea).focus();
        verify(presenter, never()).addTaskComment(anyString());
    }

    @Test
    public void fieldValidationSuccessTest() {
        String commentContent = "New comment";
        when(newCommentTextArea.getValue()).thenReturn(commentContent);
        view.submitCommentAddition();

        verify(newCommentTextArea, never()).focus();
        verify(newCommentTextAreaHelp).setTextContent("");
        verify(newCommentTextAreaGroup).clearValidationState();
        verify(presenter).addTaskComment(commentContent);
    }
}
