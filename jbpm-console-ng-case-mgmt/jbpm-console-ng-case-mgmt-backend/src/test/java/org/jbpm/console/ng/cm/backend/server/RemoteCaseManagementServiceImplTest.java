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

package org.jbpm.console.ng.cm.backend.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;
import org.jbpm.console.ng.cm.util.CaseInstanceSortBy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.cases.CaseComment;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.client.CaseServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.jbpm.console.ng.cm.backend.server.CaseDefinitionMapperTest.assertCaseDefinition;
import static org.jbpm.console.ng.cm.backend.server.CaseInstanceMapperTest.assertCaseInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCaseManagementServiceImplTest {

    final String serverTemplateId = "serverTemplateId";
    final String containerId = "containerId";
    final String caseDefinitionId = "caseDefinitionId";
    final String caseId = "CASE-1";
    final String caseName = "case name";
    final String caseDescription = "case description";
    final String author = "author";
    final String text = "text";
    final String commentId = "commentId";

    @Mock
    CaseServicesClient clientMock;

    @InjectMocks
    RemoteCaseManagementServiceImpl testedService;

    @Test
    public void testGetCaseDefinitions() {
        final CaseDefinition definition = createTestDefinition();
        when(clientMock.getCaseDefinitions(anyInt(), anyInt()))
                .thenReturn(singletonList(definition));

        List<CaseDefinitionSummary> definitions = testedService.getCaseDefinitions();
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        assertCaseDefinition(definition, definitions.get(0));
    }

    @Test
    public void getCaseDefinition_whenClientReturnsCaseDefinition() {
        final CaseDefinition definition = createTestDefinition();
        when(clientMock.getCaseDefinition(anyString(), anyString()))
                .thenReturn(definition);

        CaseDefinitionSummary actualDef = testedService.getCaseDefinition(serverTemplateId, containerId, caseDefinitionId);
        assertCaseDefinition(definition, actualDef);
    }

    @Test
    public void getCaseDefinition_whenClientReturnsNull() {
        when(clientMock.getCaseDefinition(anyString(), anyString()))
                .thenReturn(null);

        CaseDefinitionSummary shouldBeNull = testedService.getCaseDefinition(serverTemplateId, containerId, caseDefinitionId);
        assertNull(shouldBeNull);
    }

    @Test
    public void getCaseInstances() {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        final CaseInstance instance = createTestInstance(caseId);
        when(clientMock.getCaseInstances(eq(singletonList(request.getStatus())), anyInt(), anyInt())).thenReturn(singletonList(instance));

        final List<CaseInstanceSummary> instances = testedService.getCaseInstances(request);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        final CaseInstanceSummary caseInstanceSummary = instances.get(0);
        assertCaseInstance(instance, caseInstanceSummary);
    }

    @Test
    public void getCaseInstances_sorting() {
        CaseInstance c1 = createTestInstance("id1");
        c1.setStartedAt(new Date(10000));

        CaseInstance c2 = createTestInstance("id2");
        c2.setStartedAt(new Date(10));

        when(clientMock.getCaseInstances(anyList(), anyInt(), anyInt())).thenReturn(Arrays.asList(c1, c2));

        CaseInstanceSearchRequest defaultSortRequest = new CaseInstanceSearchRequest(); //Default sort is by CASE_ID
        List<CaseInstanceSummary> sortedInstances = testedService.getCaseInstances(defaultSortRequest);
        assertEquals("id1", sortedInstances.get(0).getCaseId());
        assertEquals("id2", sortedInstances.get(1).getCaseId());

        CaseInstanceSearchRequest sortByIdRequest = new CaseInstanceSearchRequest();
        sortByIdRequest.setSortBy(CaseInstanceSortBy.CASE_ID);
        sortByIdRequest.setSortByAsc(true);
        sortedInstances = testedService.getCaseInstances(sortByIdRequest);
        assertEquals("id1", sortedInstances.get(0).getCaseId());
        assertEquals("id2", sortedInstances.get(1).getCaseId());
        sortByIdRequest.setSortByAsc(false);
        sortedInstances = testedService.getCaseInstances(sortByIdRequest);
        assertEquals("id2", sortedInstances.get(0).getCaseId());
        assertEquals("id1", sortedInstances.get(1).getCaseId());

        CaseInstanceSearchRequest sortByStarted = new CaseInstanceSearchRequest();
        sortByStarted.setSortBy(CaseInstanceSortBy.START_TIME);
        sortByStarted.setSortByAsc(true);
        sortedInstances = testedService.getCaseInstances(sortByStarted);
        assertEquals("id2", sortedInstances.get(0).getCaseId());
        assertEquals("id1", sortedInstances.get(1).getCaseId());
        sortByStarted.setSortByAsc(false);
        sortedInstances = testedService.getCaseInstances(sortByStarted);
        assertEquals("id1", sortedInstances.get(0).getCaseId());
        assertEquals("id2", sortedInstances.get(1).getCaseId());
    }

    @Test
    public void testStartCaseInstance() {
        testedService.startCaseInstance(serverTemplateId, containerId, caseDefinitionId);

        verify(clientMock).startCase(containerId, caseDefinitionId);
    }

    @Test
    public void testCancelCaseInstance() {
        testedService.cancelCaseInstance(serverTemplateId, containerId, caseId);

        verify(clientMock).cancelCaseInstance(containerId, caseId);
    }

    @Test
    public void testDestroyCaseInstance() {
        testedService.destroyCaseInstance(serverTemplateId, containerId, caseId);

        verify(clientMock).destroyCaseInstance(containerId, caseId);
    }

    @Test
    public void testGetCaseInstanceNull() {
        final CaseInstanceSummary cis = testedService.getCaseInstance(serverTemplateId, containerId, caseId);

        assertNull(cis);
    }

    @Test
    public void getCaseInstance_whenClientReturnsInstance() {
        final CaseInstance ci = createTestInstance(caseId);
        when(clientMock.getCaseInstance(ci.getContainerId(), ci.getCaseId(), true, true, true, true))
                .thenReturn(ci);

        final CaseInstanceSummary cis = testedService.getCaseInstance(serverTemplateId, ci.getContainerId(), ci.getCaseId());
        assertCaseInstance(ci, cis);
    }

    @Test
    public void getCaseInstance_whenClientReturnsNull() {
        when(clientMock.getCaseInstance(containerId, caseId, true, true, true, true))
                .thenReturn(null);

        final CaseInstanceSummary cis = testedService.getCaseInstance(serverTemplateId, containerId, caseId);
        assertNull(cis);
    }

    @Test
    public void testGetCaseComments() {
        final CaseComment caseComment = new CaseComment();
        caseComment.setId(commentId);
        caseComment.setAuthor(author);
        caseComment.setText(text);
        caseComment.setAddedAt(new Date());

        when(clientMock.getComments(containerId, caseId, 0, 0)).thenReturn(singletonList(caseComment));

        final List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId, containerId, caseId, 0, 0);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        final CaseCommentSummary caseCommentSummary = comments.get(0);
        assertEquals(caseComment.getId(), caseCommentSummary.getId());
        assertEquals(caseComment.getAuthor(), caseCommentSummary.getAuthor());
        assertEquals(caseComment.getText(), caseCommentSummary.getText());
        assertEquals(caseComment.getAddedAt(), caseCommentSummary.getAddedAt());
    }

    @Test
    public void testAddComment() {
        testedService.addComment(serverTemplateId, containerId, caseId, author, text);

        verify(clientMock).addComment(containerId, caseId, author, text);
    }

    @Test
    public void testUpdateComment() {
        testedService.updateComment(serverTemplateId, containerId, caseId, commentId, author, text);

        verify(clientMock).updateComment(containerId, caseId, commentId, author, text);
    }

    @Test
    public void testRemoveComment() {
        testedService.removeComment(serverTemplateId, containerId, caseId, commentId);

        verify(clientMock).removeComment(containerId, caseId, commentId);
    }

    private CaseDefinition createTestDefinition() {
        CaseDefinition definition = new CaseDefinition();
        definition.setIdentifier(caseDefinitionId);
        definition.setName(caseName);
        definition.setContainerId(containerId);
        definition.setRoles(Collections.emptyMap());
        return definition;
    }

    private CaseInstance createTestInstance(String caseId) {
        CaseInstance instance = new CaseInstance();
        instance.setCaseDescription(caseDescription);
        instance.setCaseId(caseId);
        instance.setCaseStatus(1);
        instance.setContainerId(containerId);
        return instance;
    }
}