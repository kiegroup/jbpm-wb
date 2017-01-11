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

import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.model.CaseMilestoneSummary;
import org.jbpm.console.ng.cm.util.CaseActionSearchRequest;
import org.jbpm.console.ng.cm.util.CaseActionsFilterBy;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;
import org.jbpm.console.ng.cm.util.CaseInstanceSortBy;
import org.jbpm.console.ng.cm.util.CaseMilestoneSearchRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.cases.CaseAdHocFragment;
import org.kie.server.api.model.cases.CaseComment;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.api.model.cases.CaseMilestone;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.CaseServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jbpm.console.ng.cm.backend.server.CaseCommentMapperTest.assertCaseComment;
import static org.jbpm.console.ng.cm.backend.server.CaseDefinitionMapperTest.assertCaseDefinition;
import static org.jbpm.console.ng.cm.backend.server.CaseInstanceMapperTest.assertCaseInstance;
import static org.jbpm.console.ng.cm.backend.server.RemoteCaseManagementServiceImpl.PAGE_SIZE_UNLIMITED;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
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
    public void testGetCaseDefinitions_singleCaseDefinition() {
        final CaseDefinition definition = createTestDefinition();
        when(clientMock.getCaseDefinitions(anyInt(), anyInt()))
                .thenReturn(singletonList(definition));

        List<CaseDefinitionSummary> definitions = testedService.getCaseDefinitions();
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        assertCaseDefinition(definition, definitions.get(0));
    }

    @Test
    public void testGetCaseDefinitions_emptyList() {
        when(clientMock.getCaseDefinitions(anyInt(), anyInt()))
                .thenReturn(emptyList());

        List<CaseDefinitionSummary> definitions = testedService.getCaseDefinitions();
        assertNotNull(definitions);
        assertTrue(definitions.isEmpty());
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
    public void getCaseInstances_singleCaseInstance() {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        final CaseInstance instance = createTestInstance(caseId);
        when(clientMock.getCaseInstances(eq(singletonList(request.getStatus())), anyInt(), anyInt())).thenReturn(singletonList(instance));

        final List<CaseInstanceSummary> instances = testedService.getCaseInstances(request);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertCaseInstance(instance, instances.get(0));
    }

    @Test
    public void getCaseInstances_emptyList() {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        when(clientMock.getCaseInstances(eq(singletonList(request.getStatus())), anyInt(), anyInt())).thenReturn(emptyList());

        final List<CaseInstanceSummary> instances = testedService.getCaseInstances(request);
        assertNotNull(instances);
        assertTrue(instances.isEmpty());
    }

    @Test
    public void getCaseInstances_sortCaseInstanceList() {
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
    public void testGetComments_singleComment() {
        final CaseComment caseComment = createTestComment();
        when(clientMock.getComments(containerId, caseId, 0, PAGE_SIZE_UNLIMITED)).thenReturn(singletonList(caseComment));

        final List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId, containerId, caseId);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertCaseComment(caseComment, comments.get(0));
    }

    @Test
    public void testGetComments_emptyList() {
        when(clientMock.getComments(containerId, caseId, 0, 0)).thenReturn(emptyList());

        final List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId, containerId, caseId);
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
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

    @Test
    public void getCaseMilestones_sorting() {
        CaseMilestone c1 = createTestMilestone("id1", "milestone1", "Available");
        CaseMilestone c2 = createTestMilestone("id2", "milestone2", "Available");
        CaseMilestone c3 = createTestMilestone("id3", "milestone3", "Completed");

        when(clientMock.getMilestones(anyString(), anyString(), anyBoolean(), anyInt(), anyInt())).thenReturn(Arrays.asList(c1, c2, c3));

        CaseMilestoneSearchRequest defaultSortRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        List<CaseMilestoneSummary> sortedMilestones = testedService.getCaseMilestones("containerId", "caseId", defaultSortRequest);
        assertEquals("id1", sortedMilestones.get(0).getIdentifier());
        assertEquals("id2", sortedMilestones.get(1).getIdentifier());
        assertEquals("id3", sortedMilestones.get(2).getIdentifier());

        CaseMilestoneSearchRequest sortByNameAscRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        sortByNameAscRequest.setSortByAsc(true);
        sortedMilestones = testedService.getCaseMilestones("containerId", "caseId", sortByNameAscRequest);
        assertEquals("id1", sortedMilestones.get(0).getIdentifier());
        assertEquals("id2", sortedMilestones.get(1).getIdentifier());
        assertEquals("id3", sortedMilestones.get(2).getIdentifier());

        CaseMilestoneSearchRequest sortByNameDescRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        sortByNameDescRequest.setSortByAsc(false);
        sortedMilestones = testedService.getCaseMilestones("containerId", "caseId", sortByNameDescRequest);
        assertEquals("id2", sortedMilestones.get(0).getIdentifier());
        assertEquals("id1", sortedMilestones.get(1).getIdentifier());
        assertEquals("id3", sortedMilestones.get(2).getIdentifier());
    }

    private CaseDefinition createTestDefinition() {
        CaseDefinition definition = CaseDefinition.builder()
                .id(caseDefinitionId)
                .name(caseName)
                .containerId(containerId)
                .roles(Collections.emptyMap())
                .build();

        return definition;
    }

    private CaseInstance createTestInstance(String caseId) {
        CaseInstance instance = CaseInstance.builder()
                .caseDescription(caseDescription)
                .caseId(caseId)
                .caseStatus(1)
                .containerId(containerId)
                .build();

        return instance;
    }

    private CaseComment createTestComment() {
        CaseComment comment = CaseComment.builder()
                .id(commentId)
                .author(author)
                .text(text)
                .addedAt(new Date())
                .build();

        return comment;
    }

    private CaseMilestone createTestMilestone(String caseMilestoneId, String caseMilestoneName, String status) {
        CaseMilestone milestone = CaseMilestone.builder()
                .name(caseMilestoneName)
                .status(status)
                .id(caseMilestoneId)
                .achieved(false)
                .build();

        return milestone;
    }

    private TaskSummary createTaskSummary(Long taskId, String name, String status) {
        TaskSummary taskSummary = TaskSummary.builder()
                .id(taskId)
                .name(name)
                .status(status)
                .build();

        return taskSummary;
    }

    private CaseAdHocFragment createCaseAdhocFragment(String name, String type) {
        CaseAdHocFragment caseAdHocFragment = CaseAdHocFragment.builder()
                .name(name)
                .type(type)
                .build();

        return caseAdHocFragment;
    }

    @Test
    public void getCaseActionsByStatusTest() {
        String sortStr="CreatedOn";
        boolean sortAsc=true;
        String userId = "userId";

        Long ts1_id = Long.valueOf(1);
        String ts1_name = "task1";
        String ts1_status = "Completed";
        Long ts2_id = Long.valueOf(2);
        String ts2_name = "task2";
        String ts2_status = "Suspended";


        TaskSummary ts1 = createTaskSummary(ts1_id, ts1_name, ts1_status);
        TaskSummary ts2 = createTaskSummary(ts2_id, ts2_name, ts2_status);

        when(clientMock.findCaseTasksAssignedAsBusinessAdministrator(eq(caseId), anyString(),
                anyList(),anyInt(), anyInt(),anyString(),anyBoolean())).thenReturn(Arrays.asList(ts1, ts2));


        CaseActionSearchRequest defaultSearchRequest = new CaseActionSearchRequest();
        defaultSearchRequest.setSort(sortStr);
        defaultSearchRequest.setSortOrder(sortAsc);

        List<CaseActionSummary> actionSummaries = testedService.getCaseActionsByStatus(caseId,defaultSearchRequest,userId,"Completed","Suspended","Failed",
                "Error","Exited","Obsolete");

        assertEquals(2,actionSummaries.size());

        assertEquals(ts1_id,actionSummaries.get(0).getId());
        assertEquals(ts1_name,actionSummaries.get(0).getName());
        assertEquals(ts1_status,actionSummaries.get(0).getStatus());
        assertEquals(ts2_id,actionSummaries.get(1).getId());
        assertEquals(ts2_name,actionSummaries.get(1).getName());
        assertEquals(ts2_status,actionSummaries.get(1).getStatus());


        final ArgumentCaptor<List> captor2 = ArgumentCaptor.forClass(List.class);

        verify(clientMock).findCaseTasksAssignedAsBusinessAdministrator(eq(caseId),eq(userId),captor2.capture(),anyInt(),anyInt(),eq(sortStr),eq(sortAsc));
        assertEquals("Completed", captor2.getValue().get(0));
        assertEquals("Suspended", captor2.getValue().get(1));
        assertEquals("Failed", captor2.getValue().get(2));
        assertEquals("Error", captor2.getValue().get(3));
        assertEquals("Exited", captor2.getValue().get(4));
        assertEquals("Obsolete", captor2.getValue().get(5));

    }

    @Test
    public void getCaseActionsListTest() {
        String userId = "userId";

        testedService.getCaseActionsLists(containerId,caseId,userId);

        verify(clientMock).getAdHocFragments(containerId,caseId);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(clientMock,times(3)).findCaseTasksAssignedAsBusinessAdministrator(eq(caseId),eq(userId),captor.capture() ,anyInt(),anyInt(),anyString(),anyBoolean());
        List capturedStatusLists = captor.getAllValues();

        List<String> statuses1 = (List<String>) capturedStatusLists.get(0);
        assertEquals("Ready",statuses1.get(0));

        List<String> statuses2 = (List<String>) capturedStatusLists.get(1);
        assertEquals("InProgress",statuses2.get(0));
        assertEquals("Reserved",statuses2.get(1));

        List<String> statuses3 = (List<String>) capturedStatusLists.get(2);
        assertEquals("Completed", statuses3.get(0));
        assertEquals("Suspended", statuses3.get(1));
        assertEquals("Failed", statuses3.get(2));
        assertEquals("Error", statuses3.get(3));
        assertEquals("Exited", statuses3.get(4));
        assertEquals("Obsolete", statuses3.get(5));
    }

    @Test
    public void getCaseAvailableActionsTest() {
        String sortStr="CreatedOn";
        boolean sortAsc=true;
        String userId = "userId";
        String ahf_1_name = "adhoc-1-name";
        String ahf_1_type = "adhoc-1-type";

        String ahf_2_name = "adhoc-2-name";
        String ahf_2_type = "adhoc-2-type";

        CaseAdHocFragment adHocFragment1 = createCaseAdhocFragment(ahf_1_name,ahf_1_type);
        CaseAdHocFragment adHocFragment2 = createCaseAdhocFragment(ahf_2_name,ahf_2_type);

        Long ts1_id = Long.valueOf(1);
        String ts1_name = "task1";
        String ts1_status = "Ready";
        Long ts2_id = Long.valueOf(2);
        String ts2_name = "task2";
        String ts2_status = "Ready";


        TaskSummary ts1 = createTaskSummary(ts1_id, ts1_name, ts1_status);
        TaskSummary ts2 = createTaskSummary(ts2_id, ts2_name, ts2_status);

        when(clientMock.findCaseTasksAssignedAsBusinessAdministrator(eq(caseId), anyString(),
                anyList(),anyInt(), anyInt(),anyString(),anyBoolean())).thenReturn(Arrays.asList(ts1, ts2));
        when(clientMock.getAdHocFragments(containerId,caseId)).thenReturn(Arrays.asList(adHocFragment1,adHocFragment2));

        CaseActionSearchRequest defaultSearchRequest = new CaseActionSearchRequest();
        defaultSearchRequest.setSort(sortStr);
        defaultSearchRequest.setSortOrder(sortAsc);
        defaultSearchRequest.setFilterBy(CaseActionsFilterBy.AVAILABLE);

        List<CaseActionSummary> availableActions= testedService.getCaseActions(containerId,caseId,defaultSearchRequest,userId);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(clientMock).getAdHocFragments(containerId,caseId);
        verify(clientMock).findCaseTasksAssignedAsBusinessAdministrator(eq(caseId),eq(userId),captor.capture(),anyInt(),anyInt(),eq(sortStr),eq(sortAsc));
        assertEquals("Ready", captor.getValue().get(0));

        assertEquals(4,availableActions.size());
        assertEquals(ts1_name,availableActions.get(0).getName());
        assertEquals(ts2_name,availableActions.get(1).getName());
        assertEquals(ahf_1_name,availableActions.get(2).getName());
        assertEquals(ahf_2_name,availableActions.get(3).getName());
    }

    @Test
    public void getCaseInProgressActionsTest() {
        String sortStr="CreatedOn";
        boolean sortAsc=true;
        String userId = "userId";

        CaseActionSearchRequest defaultSearchRequest = new CaseActionSearchRequest();
        defaultSearchRequest.setSort(sortStr);
        defaultSearchRequest.setSortOrder(sortAsc);
        defaultSearchRequest.setFilterBy(CaseActionsFilterBy.IN_PROGRESS);

        testedService.getCaseActions(containerId,caseId,defaultSearchRequest,userId);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(clientMock,never()).getAdHocFragments(containerId,caseId);
        verify(clientMock).findCaseTasksAssignedAsBusinessAdministrator(eq(caseId),eq(userId),captor.capture(),anyInt(),anyInt(),eq(sortStr),eq(sortAsc));
        assertEquals("InProgress",captor.getValue().get(0));
        assertEquals("Reserved",captor.getValue().get(1));
    }

    @Test
    public void getCaseCompletedActionsTest() {
        String sortStr="CreatedOn";
        boolean sortAsc=true;
        String userId = "userId";

        CaseActionSearchRequest defaultSearchRequest = new CaseActionSearchRequest();
        defaultSearchRequest.setSort(sortStr);
        defaultSearchRequest.setSortOrder(sortAsc);
        defaultSearchRequest.setFilterBy(CaseActionsFilterBy.COMPLETED);

        testedService.getCaseActions(containerId,caseId,defaultSearchRequest,userId);

        final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(clientMock,never()).getAdHocFragments(containerId,caseId);
        verify(clientMock).findCaseTasksAssignedAsBusinessAdministrator(eq(caseId),eq(userId),captor.capture(),anyInt(),anyInt(),eq(sortStr),eq(sortAsc));
        assertEquals("Completed", captor.getValue().get(0));
        assertEquals("Suspended", captor.getValue().get(1));
        assertEquals("Failed", captor.getValue().get(2));
        assertEquals("Error", captor.getValue().get(3));
        assertEquals("Exited", captor.getValue().get(4));
        assertEquals("Obsolete", captor.getValue().get(5));
    }
}