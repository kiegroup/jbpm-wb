/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.cm.client.comments;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.console.ng.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseCommentsPresenterTest extends AbstractCaseInstancePresenterTest {

    @Mock
    CaseCommentsPresenter.CaseCommentsView caseCommentsView;

    @Mock
    User identity;

    @InjectMocks
    CaseCommentsPresenter presenter;

    @Override
    public CaseCommentsPresenter getPresenter() {
        return presenter;
    }

    private final String commentId = "commentId";
    private final String author = "author";
    private final String text = "text";
    private final Date addedAt = new Date();

    private final String serverTemplateId = "serverTemplateId";

    @Test
    public void testClearCaseInstance() {
        presenter.clearCaseInstance();
        verifyClearCaseInstance(1);
    }

    private void verifyClearCaseInstance(int times) {
        verify(caseCommentsView, times(times)).removeAllComments();
    }

    @Test
    public void testLoadCaseInstance() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        when(caseManagementService.getComments(serverTemplateId, cis.getContainerId(), cis.getCaseId())).thenReturn(
                Collections.singletonList(caseComment));
        when(identity.getIdentifier()).thenReturn(author);

        setupCaseInstance(cis, serverTemplateId);

        final ArgumentCaptor<CaseCommentsPresenter.CaseCommentAction> captorEdit = ArgumentCaptor.forClass(CaseCommentsPresenter.CaseCommentAction.class);
        verify(caseCommentsView).addComment(eq(false), eq("Edit"), eq(commentId), eq(author), eq(text), eq(addedAt), captorEdit.capture());
        assertEquals("Delete", captorEdit.getValue().label());

        verifyClearCaseInstance(2);
    }

    @Test
    public void testAddCommentEmpty() {
        presenter.addCaseComment("");
        verify(caseCommentsView, never()).addComment(anyBoolean(), anyString(), anyString(), anyString(), anyString(), any(Date.class));
    }

    @Test
    public void testAddCaseComment() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        setupCaseInstance(cis, serverTemplateId);
        presenter.addCaseComment(caseComment);

        verify(caseManagementService).addComment(eq(serverTemplateId), eq(cis.getContainerId()), eq(cis.getCaseId()), eq(author), eq(text));
        verifyClearCaseInstance(3);
    }

    @Test
    public void testUpdateCommentEmpty() {
        presenter.updateCaseComment("", commentId);
        verify(caseCommentsView, never()).addComment(anyBoolean(), anyString(), anyString(), anyString(), anyString(), any(Date.class));
    }

    @Test
    public void testUpdateCaseComment() {
        when(identity.getIdentifier()).thenReturn(author);

        final CaseInstanceSummary cis = newCaseInstanceSummary();
        setupCaseInstance(cis, serverTemplateId);
        presenter.updateCaseComment(text, commentId);

        verify(caseManagementService).updateComment(eq(serverTemplateId), eq(cis.getContainerId()), eq(cis.getCaseId()), eq(commentId), eq(author), eq(text));
        verifyClearCaseInstance(3);
    }

    @Test
    public void testDeleteComment() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        when(caseManagementService.getComments(serverTemplateId, cis.getContainerId(), cis.getCaseId())).thenReturn(
                Collections.singletonList(caseComment));
        when(identity.getIdentifier()).thenReturn(author);

        setupCaseInstance(cis, serverTemplateId);

        final ArgumentCaptor<CaseCommentsPresenter.CaseCommentAction> captorEdit = ArgumentCaptor.forClass(CaseCommentsPresenter.CaseCommentAction.class);
        verify(caseCommentsView).addComment(anyBoolean(), anyString(), eq(commentId), eq(author), eq(text), eq(addedAt), captorEdit.capture());
        assertEquals("Delete", captorEdit.getValue().label());

        captorEdit.getValue().execute();
        verify(caseManagementService).removeComment(eq(serverTemplateId), eq(cis.getContainerId()), eq(cis.getCaseId()), eq(commentId));

        verifyClearCaseInstance(3);
    }

    @Test
    public void onStartupShouldNotCallCaseService_whenAnyParameterNull() {
        String[][] invalidPlaceRequestParams = new String[][]{
                {null, null, null},
                {null, null, "caseId"},
                {null, "containerId", null},
                //TODO uncomment once empty check for serverTempalteId added to AbstractCaseInstancePresenter#isCaseInstanceValid
                // {null, "containerId", "caseId"},
                {"serverTemplateId", null, null},
                {"serverTemplateId", null, "caseId"},
                {"serverTemplateId", "containerId", null},
        };

        Arrays.stream(invalidPlaceRequestParams).forEach(invalidParams ->
                verifyGetCaseInstanceCalled(invalidParams, 0)
        );

        // Only all non empty values constitute valid place request params
        String[] validPlaceRequestParams = {"serverTemplateId", "containerId", "caseId"};
        verifyGetCaseInstanceCalled(validPlaceRequestParams, 1);
    }

    private void verifyGetCaseInstanceCalled(String[] placeRequestParams, int timesCalled) {
        HashMap<String, String> params = new HashMap<>();
        params.put(AbstractCaseInstancePresenter.PARAMETER_SERVER_TEMPLATE_ID, placeRequestParams[0]);
        params.put(AbstractCaseInstancePresenter.PARAMETER_CONTAINER_ID, placeRequestParams[1]);
        params.put(AbstractCaseInstancePresenter.PARAMETER_CASE_ID, placeRequestParams[2]);
        PlaceRequest placeRequest = new DefaultPlaceRequest(CaseCommentsPresenter.SCREEN_ID, params, false);

        presenter.onStartup(placeRequest);

        verify(caseManagementService, times(timesCalled)).getCaseInstance(anyString(), anyString(), anyString());
    }
}
