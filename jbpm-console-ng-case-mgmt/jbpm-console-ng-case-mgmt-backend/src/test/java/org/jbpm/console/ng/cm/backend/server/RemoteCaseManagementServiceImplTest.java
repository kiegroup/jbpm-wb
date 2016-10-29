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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCaseManagementServiceImplTest {

    final String caseId = "CASE-1";
    final String containerId = "containerId";
    final String serverTemplateId = "serverTemplateId";
    final String author = "author";
    final String text = "text";
    final String commentId = "commentId";

    @Mock
    CaseServicesClient caseServicesClient;

    @InjectMocks
    RemoteCaseManagementServiceImpl service;

    @Test
    public void testGetCaseDefinitions() throws Exception {
        final CaseDefinition definition = new CaseDefinition();
        definition.setIdentifier("org.jbpm.case");
        definition.setName("New case");
        definition.setContainerId("org.jbpm");
        definition.setRoles(Collections.emptyMap());
        when(caseServicesClient.getCaseDefinitions(anyInt(), anyInt())).thenReturn(singletonList(definition));

        final List<CaseDefinitionSummary> definitions = service.getCaseDefinitions();
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        assertCaseDefinition(definition, definitions.get(0));
    }

    @Test
    public void testGetCaseInstances() throws Exception {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        final CaseInstance instance = new CaseInstance();
        instance.setCaseDescription("New case");
        instance.setCaseId("CASE-1");
        instance.setCaseStatus(1);
        instance.setContainerId("org.jbpm");
        when(caseServicesClient.getCaseInstances(eq(singletonList(request.getStatus())), anyInt(), anyInt())).thenReturn(singletonList(instance));

        final List<CaseInstanceSummary> instances = service.getCaseInstances(request);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        final CaseInstanceSummary caseInstanceSummary = instances.get(0);
        assertCaseInstance(instance, caseInstanceSummary);
    }

    @Test
    public void testStartCaseInstance() throws Exception {
        final String caseDefinitionId = "org.jbpm";
        final String container = "container";

        service.startCaseInstance("server", container, caseDefinitionId);

        verify(caseServicesClient).startCase(container, caseDefinitionId);
    }

    @Test
    public void testCancelCaseInstance() throws Exception {
        final String caseId = "CASE-1";
        final String container = "container";

        service.cancelCaseInstance("server", container, caseId);

        verify(caseServicesClient).cancelCaseInstance(container, caseId);
    }

    @Test
    public void testDestroyCaseInstance() throws Exception {
        final String caseId = "CASE-1";
        final String container = "container";

        service.destroyCaseInstance("server", container, caseId);

        verify(caseServicesClient).destroyCaseInstance(container, caseId);
    }

    @Test
    public void testGetCaseInstanceNull() throws Exception {
        final CaseInstanceSummary cis = service.getCaseInstance("server", "container", "CASE-1");

        assertNull(cis);
    }

    @Test
    public void testGetCaseInstance() throws Exception {
        final CaseInstance ci = new CaseInstance();
        ci.setCaseDescription("New case");
        ci.setCaseId("CASE-1");
        ci.setCaseStatus(1);
        ci.setContainerId("org.jbpm");
        when(caseServicesClient.getCaseInstance(ci.getContainerId(), ci.getCaseId(), true, true, true, true)).thenReturn(ci);

        final CaseInstanceSummary cis = service.getCaseInstance("server", ci.getContainerId(), ci.getCaseId());

        assertCaseInstance(ci, cis);
    }

    @Test
    public void testGetCaseComments() throws Exception {
        final CaseComment caseComment = new CaseComment();
        caseComment.setId(commentId);
        caseComment.setAuthor(author);
        caseComment.setText(text);
        caseComment.setAddedAt(new Date());

        when(caseServicesClient.getComments(containerId, caseId, 0, 0)).thenReturn(singletonList(caseComment));

        final List<CaseCommentSummary> comments = service.getComments(serverTemplateId, containerId, caseId, 0, 0);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        final CaseCommentSummary caseCommentSummary = comments.get(0);
        assertEquals(caseComment.getId(), caseCommentSummary.getId());
        assertEquals(caseComment.getAuthor(), caseCommentSummary.getAuthor());
        assertEquals(caseComment.getText(), caseCommentSummary.getText());
        assertEquals(caseComment.getAddedAt(), caseCommentSummary.getAddedAt());
    }

    @Test
    public void testAddComment() throws Exception {
        service.addComment(serverTemplateId, containerId, caseId, author, text);

        verify(caseServicesClient).addComment(containerId, caseId, author, text);
    }

    @Test
    public void testUpdateComment() throws Exception {
        service.updateComment(serverTemplateId, containerId, caseId, commentId, author, text);

        verify(caseServicesClient).updateComment(containerId, caseId, commentId, author, text);
    }

    @Test
    public void testRemoveComment() throws Exception {
        service.removeComment(serverTemplateId, containerId, caseId, commentId);

        verify(caseServicesClient).removeComment(containerId, caseId, commentId);
    }

}