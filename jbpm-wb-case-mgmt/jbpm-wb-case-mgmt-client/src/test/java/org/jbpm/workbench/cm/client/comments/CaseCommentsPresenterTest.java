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

package org.jbpm.workbench.cm.client.comments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenterTest;
import org.jbpm.workbench.cm.model.CaseCommentSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseCommentsPresenterTest extends AbstractCaseInstancePresenterTest {

    private final String commentId = "commentId";
    private final String author = "author";
    private final String text = "text";
    private final Date addedAt = new Date();
    private final String serverTemplateId = "serverTemplateId";

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

    @Test
    public void testLoadCaseInstance() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        when(caseManagementService.getComments(serverTemplateId, 
                                               cis.getContainerId(), 
                                               cis.getCaseId(), 
                                               0, 
                                               presenter.getPageSize())).thenReturn(
                Collections.singletonList(caseComment));
        when(identity.getIdentifier()).thenReturn(author);

        setupCaseInstance(cis,
                          serverTemplateId);

        verify(caseCommentsView).setCaseCommentList(Collections.singletonList(caseComment));
        verifyClearCaseInstance(1);
    }

    @Test
    public void testAddCaseComment() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        when(identity.getIdentifier()).thenReturn(author);

        setupCaseInstance(cis,
                          serverTemplateId);
        presenter.addCaseComment(text);

        verify(caseManagementService).addComment(eq(serverTemplateId),
                                                 eq(cis.getContainerId()),
                                                 eq(cis.getCaseId()),
                                                 eq(author),
                                                 eq(text));
        verifyClearCaseInstance(1);
    }

    @Test
    public void testUpdateCaseComment() {
        String newCommentText = "newCommentText";
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        when(identity.getIdentifier()).thenReturn(author);
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        when(caseManagementService.getComments(serverTemplateId, 
                                               cis.getContainerId(), 
                                               cis.getCaseId(), 
                                               0, 
                                               presenter.getPageSize())).thenReturn(
                Collections.singletonList(caseComment));

        setupCaseInstance(cis,
                          serverTemplateId);
        presenter.updateCaseComment(caseComment,
                                    newCommentText);

        verify(caseManagementService).updateComment(eq(serverTemplateId),
                                                    eq(cis.getContainerId()),
                                                    eq(cis.getCaseId()),
                                                    eq(commentId),
                                                    eq(author),
                                                    eq(newCommentText));
        verify(caseCommentsView,
               times(2)).setCaseCommentList(Collections.singletonList(caseComment));
        verifyClearCaseInstance(2);
    }

    @Test
    public void testDeleteComment() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();
        final CaseCommentSummary caseComment = CaseCommentSummary.builder().id(commentId).author(author).text(text).addedAt(addedAt).build();

        when(caseManagementService.getComments(serverTemplateId, 
                                               cis.getContainerId(), 
                                               cis.getCaseId(), 
                                               0, 
                                               presenter.getPageSize())).thenReturn(
                Collections.singletonList(caseComment));

        setupCaseInstance(cis,
                          serverTemplateId);

        presenter.deleteCaseComment(caseComment);
        verify(caseManagementService).removeComment(eq(serverTemplateId),
                                                    eq(cis.getContainerId()),
                                                    eq(cis.getCaseId()),
                                                    eq(commentId));
        verify(caseCommentsView,
               times(2)).setCaseCommentList(Collections.singletonList(caseComment));
        verifyClearCaseInstance(2);
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
                                                                 verifyGetCaseInstanceCalled(invalidParams,
                                                                                             0)
        );

        // Only all non empty values constitute valid place request params
        String[] validPlaceRequestParams = {"serverTemplateId", "containerId", "caseId"};
        verifyGetCaseInstanceCalled(validPlaceRequestParams,
                                    1);
    }

    private void verifyClearCaseInstance(int times) {
        verify(caseCommentsView,
               times(times)).clearCommentInputForm();
    }

    private void verifyGetCaseInstanceCalled(String[] placeRequestParams,
                                             int timesCalled) {
        HashMap<String, String> params = new HashMap<>();
        params.put(AbstractCaseInstancePresenter.PARAMETER_SERVER_TEMPLATE_ID,
                   placeRequestParams[0]);
        params.put(AbstractCaseInstancePresenter.PARAMETER_CONTAINER_ID,
                   placeRequestParams[1]);
        params.put(AbstractCaseInstancePresenter.PARAMETER_CASE_ID,
                   placeRequestParams[2]);
        PlaceRequest placeRequest = new DefaultPlaceRequest(CaseCommentsPresenter.SCREEN_ID,
                                                            params,
                                                            false);

        presenter.onStartup(placeRequest);

        verify(caseManagementService,
               times(timesCalled)).getCaseInstance(anyString(),
                                                   anyString(),
                                                   anyString());
    }

    @Test
    public void testSortComments() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();

        String comment1_id = "comment1";
        String comment2_id = "comment2";
        Date first = new Date(1000);
        Date second = new Date(2000);
        final CaseCommentSummary caseComment1 = CaseCommentSummary.builder().id(comment1_id).author(author).text(text).addedAt(first).build();
        final CaseCommentSummary caseComment2 = CaseCommentSummary.builder().id(comment2_id).author(author).text(text).addedAt(second).build();

        when(caseManagementService.getComments(serverTemplateId, 
                                               cis.getContainerId(), 
                                               cis.getCaseId(), 
                                               0, 
                                               presenter.getPageSize())).thenReturn(
                Arrays.asList(caseComment1,
                              caseComment2));

        setupCaseInstance(cis,
                          serverTemplateId);
        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(caseCommentsView).setCaseCommentList(captor.capture());
        assertEquals(comment2_id,
                     ((CaseCommentSummary) captor.getValue().get(0)).getId());
        assertEquals(comment1_id,
                     ((CaseCommentSummary) captor.getValue().get(1)).getId());

        presenter.sortComments(true);
        verify(caseCommentsView,
               times(2)).setCaseCommentList(captor.capture());
        assertEquals(comment1_id,
                     ((CaseCommentSummary) captor.getValue().get(0)).getId());
        assertEquals(comment2_id,
                     ((CaseCommentSummary) captor.getValue().get(1)).getId());
    }
    
    @Test
    public void testLoadMoreComments() {
        final CaseInstanceSummary cis = newCaseInstanceSummary();

        List<CaseCommentSummary> caseCommentSummary = new ArrayList<>();
        
        for (int i = 0 ; i < 55 ; i++) {
            final CaseCommentSummary comment = CaseCommentSummary.builder().id("comment" + i).author(author).text(text).addedAt(addedAt).build();
            caseCommentSummary.add(comment);
        }
        
        when(caseManagementService.getComments(serverTemplateId, cis.getContainerId(), cis.getCaseId(), 0, presenter.getPageSize())).thenReturn(
                caseCommentSummary.subList(0, 20));

        setupCaseInstance(cis, serverTemplateId);
        
        presenter.loadMoreCaseComments();
        
        assertEquals(1, presenter.getCurrentPage());
        verify(caseManagementService).getComments(serverTemplateId, cis.getContainerId(), cis.getCaseId(), presenter.getCurrentPage(), presenter.getPageSize());
    }
}
