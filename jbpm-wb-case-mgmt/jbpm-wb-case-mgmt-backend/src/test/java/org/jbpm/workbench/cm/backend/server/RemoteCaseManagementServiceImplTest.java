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
package org.jbpm.workbench.cm.backend.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.jbpm.workbench.cm.model.*;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.jbpm.workbench.cm.util.CaseInstanceSortBy;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.cases.*;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.jbpm.workbench.cm.backend.server.CaseActionMapperTest.*;
import static org.jbpm.workbench.cm.backend.server.CaseCommentMapperTest.assertCaseComment;
import static org.jbpm.workbench.cm.backend.server.CaseDefinitionMapperTest.assertCaseDefinition;
import static org.jbpm.workbench.cm.backend.server.CaseInstanceMapperTest.assertCaseInstance;
import static org.jbpm.workbench.cm.backend.server.CaseInstanceMapperTest.assertCaseStages;
import static org.jbpm.workbench.cm.backend.server.RemoteCaseManagementServiceImpl.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCaseManagementServiceImplTest {

    private final String serverTemplateId = "serverTemplateId";
    private final String containerId = "containerId";
    private final String caseDefinitionId = "caseDefinitionId";
    private final String caseId = "CASE-1";
    private final String caseName = "case name";
    private final String caseDescription = "case description";
    private final String author = "author";
    private final String text = "text";
    private final String commentId = "commentId";
    private final String userId = "userId";

    @Mock
    private CaseServicesClient clientMock;

    @Mock
    private UserTaskServicesClient userTaskServicesClient;

    @Spy
    @InjectMocks
    private RemoteCaseManagementServiceImpl testedService;

    @Test
    public void testGetCaseDefinitions_singleCaseDefinition() {
        final CaseDefinition definition = createTestDefinition();
        when(clientMock.getCaseDefinitions(anyInt(),
                                           anyInt(),
                                           eq(CaseServicesClient.SORT_BY_CASE_DEFINITION_NAME),
                                           eq(true)))
                .thenReturn(singletonList(definition));

        List<CaseDefinitionSummary> definitions = testedService.getCaseDefinitions();
        assertNotNull(definitions);
        assertEquals(1,
                     definitions.size());
        assertCaseDefinition(definition,
                             definitions.get(0));
    }

    @Test
    public void testGetCaseDefinitions_emptyList() {
        when(clientMock.getCaseDefinitions(anyInt(),
                                           anyInt()))
                .thenReturn(emptyList());

        List<CaseDefinitionSummary> definitions = testedService.getCaseDefinitions();
        assertNotNull(definitions);
        assertTrue(definitions.isEmpty());
    }

    @Test
    public void getCaseDefinition_whenClientReturnsCaseDefinition() {
        final CaseDefinition definition = createTestDefinition();
        when(clientMock.getCaseDefinition(anyString(),
                                          anyString()))
                .thenReturn(definition);

        CaseDefinitionSummary actualDef = testedService.getCaseDefinition(serverTemplateId,
                                                                          containerId,
                                                                          caseDefinitionId);
        assertCaseDefinition(definition,
                             actualDef);
    }

    @Test
    public void getCaseDefinition_whenClientReturnsNull() {
        when(clientMock.getCaseDefinition(anyString(),
                                          anyString()))
                .thenReturn(null);

        CaseDefinitionSummary shouldBeNull = testedService.getCaseDefinition(serverTemplateId,
                                                                             containerId,
                                                                             caseDefinitionId);
        assertNull(shouldBeNull);
    }

    @Test
    public void getCaseInstances_singleCaseInstance() {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        final CaseInstance instance = createTestInstance(caseId);
        when(clientMock.getCaseInstances(eq(singletonList(request.getStatus().getName())),
                                         anyInt(),
                                         anyInt())).thenReturn(singletonList(instance));

        final List<CaseInstanceSummary> instances = testedService.getCaseInstances(request);
        assertNotNull(instances);
        assertEquals(1,
                     instances.size());
        assertCaseInstance(instance,
                           instances.get(0));
    }

    @Test
    public void getCaseInstances_emptyList() {
        final CaseInstanceSearchRequest request = new CaseInstanceSearchRequest();
        when(clientMock.getCaseInstances(eq(singletonList(request.getStatus().getName())),
                                         anyInt(),
                                         anyInt())).thenReturn(emptyList());

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

        when(clientMock.getCaseInstances(anyList(),
                                         anyInt(),
                                         anyInt())).thenReturn(Arrays.asList(c1,
                                                                             c2));

        CaseInstanceSearchRequest defaultSortRequest = new CaseInstanceSearchRequest(); //Default sort is by CASE_ID
        List<CaseInstanceSummary> sortedInstances = testedService.getCaseInstances(defaultSortRequest);
        assertEquals("id1",
                     sortedInstances.get(0).getCaseId());
        assertEquals("id2",
                     sortedInstances.get(1).getCaseId());

        CaseInstanceSearchRequest sortByIdRequest = new CaseInstanceSearchRequest();
        sortByIdRequest.setSortBy(CaseInstanceSortBy.CASE_ID);
        sortByIdRequest.setSortByAsc(true);
        sortedInstances = testedService.getCaseInstances(sortByIdRequest);
        assertEquals("id1",
                     sortedInstances.get(0).getCaseId());
        assertEquals("id2",
                     sortedInstances.get(1).getCaseId());
        sortByIdRequest.setSortByAsc(false);
        sortedInstances = testedService.getCaseInstances(sortByIdRequest);
        assertEquals("id2",
                     sortedInstances.get(0).getCaseId());
        assertEquals("id1",
                     sortedInstances.get(1).getCaseId());

        CaseInstanceSearchRequest sortByStarted = new CaseInstanceSearchRequest();
        sortByStarted.setSortBy(CaseInstanceSortBy.START_TIME);
        sortByStarted.setSortByAsc(true);
        sortedInstances = testedService.getCaseInstances(sortByStarted);
        assertEquals("id2",
                     sortedInstances.get(0).getCaseId());
        assertEquals("id1",
                     sortedInstances.get(1).getCaseId());
        sortByStarted.setSortByAsc(false);
        sortedInstances = testedService.getCaseInstances(sortByStarted);
        assertEquals("id1",
                     sortedInstances.get(0).getCaseId());
        assertEquals("id2",
                     sortedInstances.get(1).getCaseId());
    }

    @Test
    public void testStartCaseInstance() {
        final String owner = "userx";
        final String role = "test";
        final String user = "user1";
        final List<CaseRoleAssignmentSummary> roles = singletonList(CaseRoleAssignmentSummary.builder().name(role).users(singletonList(user)).build());
        testedService.startCaseInstance(serverTemplateId,
                                        containerId,
                                        caseDefinitionId,
                                        owner,
                                        roles);

        final ArgumentCaptor<CaseFile> caseFileCaptor = ArgumentCaptor.forClass(CaseFile.class);
        verify(clientMock).startCase(eq(containerId),
                                     eq(caseDefinitionId),
                                     caseFileCaptor.capture());
        final CaseFile caseFile = caseFileCaptor.getValue();
        assertEquals(owner,
                     caseFile.getUserAssignments().get(CASE_OWNER_ROLE));
        assertEquals(user,
                     caseFile.getUserAssignments().get(role));
    }

    @Test
    public void testCancelCaseInstance() {
        testedService.cancelCaseInstance(serverTemplateId,
                                         containerId,
                                         caseId);

        verify(clientMock).cancelCaseInstance(containerId,
                                              caseId);
    }

    @Test
    public void testDestroyCaseInstance() {
        testedService.destroyCaseInstance(serverTemplateId,
                                          containerId,
                                          caseId);

        verify(clientMock).destroyCaseInstance(containerId,
                                               caseId);
    }

    @Test
    public void getCaseInstance_whenClientReturnsInstance() {
        final CaseInstance ci = createTestInstance(caseId);
        when(clientMock.getCaseInstance(ci.getContainerId(),
                                        ci.getCaseId(),
                                        false,
                                        true,
                                        true,
                                        true))
                .thenReturn(ci);

        final CaseInstanceSummary cis = testedService.getCaseInstance(serverTemplateId,
                                                                      ci.getContainerId(),
                                                                      ci.getCaseId());
        assertCaseInstance(ci,
                           cis);
    }

    @Test
    public void getCaseInstance_whenClientReturnsNull() {
        when(clientMock.getCaseInstance(containerId,
                                        caseId,
                                        false,
                                        true,
                                        true,
                                        true))
                .thenReturn(null);

        final CaseInstanceSummary cis = testedService.getCaseInstance(serverTemplateId,
                                                                      containerId,
                                                                      caseId);
        assertNull(cis);
    }

    @Test
    public void testGetComments_singleComment() {
        final CaseComment caseComment = createTestComment();
        when(clientMock.getComments(containerId,
                                    caseId,
                                    0,
                                    10)).thenReturn(singletonList(caseComment));

        final List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId,
                                                                            containerId,
                                                                            caseId,
                                                                            0,
                                                                            10);
        assertNotNull(comments);
        assertEquals(1,
                     comments.size());
        assertCaseComment(caseComment,
                          comments.get(0));
    }

    @Test
    public void testGetComments_bulkComments() {
        int pageSize = 20;
        List<CaseComment> caseComments = new ArrayList<>();

        for (int i = 0; i < 55; i++) {
            final CaseComment caseComment = createTestComment();
            caseComments.add(caseComment);
        }

        List<CaseComment> firstPage = caseComments.subList(0,
                                                           20);
        List<CaseComment> secondPage = caseComments.subList(20,
                                                            40);
        List<CaseComment> thirdPage = caseComments.subList(40,
                                                           55);

        when(clientMock.getComments(containerId,
                                    caseId,
                                    0,
                                    20)).thenReturn(firstPage);

        List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId,
                                                                      containerId,
                                                                      caseId,
                                                                      0,
                                                                      pageSize);

        assertNotNull(comments);
        assertEquals(20,
                     comments.size());

        when(clientMock.getComments(containerId,
                                    caseId,
                                    1,
                                    20)).thenReturn(secondPage);

        comments = testedService.getComments(serverTemplateId,
                                             containerId,
                                             caseId,
                                             1,
                                             pageSize);

        assertNotNull(comments);
        assertEquals(20,
                     comments.size());

        when(clientMock.getComments(containerId,
                                    caseId,
                                    2,
                                    20)).thenReturn(thirdPage);

        comments = testedService.getComments(serverTemplateId,
                                             containerId,
                                             caseId,
                                             2,
                                             pageSize);

        assertNotNull(comments);
        assertEquals(15,
                     comments.size());
    }

    @Test
    public void testGetComments_emptyList() {
        when(clientMock.getComments(containerId,
                                    caseId,
                                    0,
                                    0)).thenReturn(emptyList());

        final List<CaseCommentSummary> comments = testedService.getComments(serverTemplateId,
                                                                            containerId,
                                                                            caseId,
                                                                            0,
                                                                            10);
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testAddComment() {
        testedService.addComment(serverTemplateId,
                                 containerId,
                                 caseId,
                                 author,
                                 text);

        verify(clientMock).addComment(containerId,
                                      caseId,
                                      author,
                                      text);
    }

    @Test
    public void testUpdateComment() {
        testedService.updateComment(serverTemplateId,
                                    containerId,
                                    caseId,
                                    commentId,
                                    author,
                                    text);

        verify(clientMock).updateComment(containerId,
                                         caseId,
                                         commentId,
                                         author,
                                         text);
    }

    @Test
    public void testRemoveComment() {
        testedService.removeComment(serverTemplateId,
                                    containerId,
                                    caseId,
                                    commentId);

        verify(clientMock).removeComment(containerId,
                                         caseId,
                                         commentId);
    }

    @Test
    public void getCaseMilestones_sorting() {
        CaseMilestone c1 = createTestMilestone("id1",
                                               "milestone1",
                                               "Available");
        CaseMilestone c2 = createTestMilestone("id2",
                                               "milestone2",
                                               "Available");
        CaseMilestone c3 = createTestMilestone("id3",
                                               "milestone3",
                                               "Completed");

        when(clientMock.getMilestones(anyString(),
                                      anyString(),
                                      anyBoolean(),
                                      anyInt(),
                                      anyInt())).thenReturn(Arrays.asList(c1,
                                                                          c2,
                                                                          c3));

        CaseMilestoneSearchRequest defaultSortRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        List<CaseMilestoneSummary> sortedMilestones = testedService.getCaseMilestones("containerId",
                                                                                      "caseId",
                                                                                      defaultSortRequest);
        assertEquals("id1",
                     sortedMilestones.get(0).getIdentifier());
        assertEquals("id2",
                     sortedMilestones.get(1).getIdentifier());
        assertEquals("id3",
                     sortedMilestones.get(2).getIdentifier());

        CaseMilestoneSearchRequest sortByNameAscRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        sortByNameAscRequest.setSortByAsc(true);
        sortedMilestones = testedService.getCaseMilestones("containerId",
                                                           "caseId",
                                                           sortByNameAscRequest);
        assertEquals("id1",
                     sortedMilestones.get(0).getIdentifier());
        assertEquals("id2",
                     sortedMilestones.get(1).getIdentifier());
        assertEquals("id3",
                     sortedMilestones.get(2).getIdentifier());

        CaseMilestoneSearchRequest sortByNameDescRequest = new CaseMilestoneSearchRequest(); //Default sort is by MILESTONE_NAME
        sortByNameDescRequest.setSortByAsc(false);
        sortedMilestones = testedService.getCaseMilestones("containerId",
                                                           "caseId",
                                                           sortByNameDescRequest);
        assertEquals("id2",
                     sortedMilestones.get(0).getIdentifier());
        assertEquals("id1",
                     sortedMilestones.get(1).getIdentifier());
        assertEquals("id3",
                     sortedMilestones.get(2).getIdentifier());
    }

    @Test
    public void testGetCaseStages() {
        CaseStage stage1 = createTestCaseStage("stage1",
                                               "stage1-name",
                                               CaseStageStatus.COMPLETED.getStatus());
        CaseStage stage2 = createTestCaseStage("stage2",
                                               "stage2-name",
                                               CaseStageStatus.ACTIVE.getStatus());

        final List<CaseStage> caseStages = Arrays.asList(stage1,
                                                         stage2);
        when(clientMock.getStages(any(),
                                  any(),
                                  eq(false),
                                  any(),
                                  any())).thenReturn(caseStages);

        final List<CaseStageSummary> stages = testedService.getCaseStages("containerId",
                                                                          "caseId");
        assertNotNull(stages);
        assertEquals(2,
                     stages.size());
        assertCaseStages(caseStages,
                         stages);
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

    private CaseMilestone createTestMilestone(String caseMilestoneId,
                                              String caseMilestoneName,
                                              String status) {
        CaseMilestone milestone = CaseMilestone.builder()
                .name(caseMilestoneName)
                .status(status)
                .id(caseMilestoneId)
                .achieved(false)
                .build();

        return milestone;
    }

    private CaseAdHocFragment createTestCaseAdHocFragment(String name,
                                                          String type) {
        CaseAdHocFragment caseAdHocFragment = CaseAdHocFragment.builder()
                .name(name)
                .type(type)
                .build();

        return caseAdHocFragment;
    }

    private NodeInstance createTestNodeInstance(String name,
                                                String nodeType,
                                                Long workItemId) {
        NodeInstance nodeInstance = NodeInstance.builder()
                .name(name)
                .nodeType(nodeType)
                .workItemId(workItemId)
                .date(new Date())
                .build();

        return nodeInstance;
    }

    private CaseStage createTestCaseStage(String stageId,
                                          String stageName,
                                          String stageStatus) {
        CaseStage stage = CaseStage.builder()
                .id(stageId)
                .name(stageName)
                .status(stageStatus)
                .build();
        return stage;
    }

    @Test
    public void getCaseActionsTest() {
        final CaseInstance ci = createTestInstance(caseId);
        CaseStage stage1 = createTestCaseStage("stage1",
                                               "stage1-name",
                                               CaseStageStatus.ACTIVE.getStatus());
        CaseAdHocFragment cAHF1_stage1 = createTestCaseAdHocFragment("stage1-adHoc-1",
                                                                     "adHocFragment-type-1");
        CaseAdHocFragment cAHF2_stage1 = createTestCaseAdHocFragment("stage1-adHoc-2",
                                                                     "adHocFragment-type-2");

        CaseStage stage2 = createTestCaseStage("stage2",
                                               "stage2-name",
                                               CaseStageStatus.COMPLETED.getStatus());
        CaseAdHocFragment cAHF1_stage2 = createTestCaseAdHocFragment("stage2-adHoc-1",
                                                                     "adHocFragment-type-1");
        CaseAdHocFragment cAHF2_stage2 = createTestCaseAdHocFragment("stage2-adHoc-2",
                                                                     "adHocFragment-type-2");

        stage1.setAdHocFragments(Arrays.asList(cAHF1_stage1,
                                               cAHF2_stage1));
        stage2.setAdHocFragments(Arrays.asList(cAHF1_stage2,
                                               cAHF2_stage2));
        ci.setStages(Arrays.asList(stage1,
                                   stage2));

        when(clientMock.getCaseInstance(ci.getContainerId(),
                                        ci.getCaseId(),
                                        false,
                                        true,
                                        true,
                                        true))
                .thenReturn(ci);

        CaseAdHocFragment cAHF1 = createTestCaseAdHocFragment("adHocFragment-name-1",
                                                              "adHocFragment-type-1");
        CaseAdHocFragment cAHF2 = createTestCaseAdHocFragment("adHocFragment-name-2",
                                                              "adHocFragment-type-2");
        CaseAdHocFragment cAHF3 = createTestCaseAdHocFragment("active1",
                                                              "Human Task");
        CaseAdHocFragment cAHF4 = createTestCaseAdHocFragment("active2",
                                                              "HumanTaskNode");
        CaseAdHocFragment cAHF5 = createTestCaseAdHocFragment("Milestone 1",
                                                              "MilestoneNode");
        CaseAdHocFragment cAHF6 = createTestCaseAdHocFragment("Milestone 2",
                                                              "MilestoneNode");
        when(clientMock.getAdHocFragments(containerId,
                                          caseId)).thenReturn(Arrays.asList(cAHF1,
                                                                            cAHF2,
                                                                            cAHF3,
                                                                            cAHF4,
                                                                            cAHF5,
                                                                            cAHF6));

        Long nodeWorkItemId = 0L;
        NodeInstance node1 = createTestNodeInstance("active1",
                                                    "Human Task",
                                                    nodeWorkItemId++);
        NodeInstance node2 = createTestNodeInstance("active2",
                                                    "Service Task",
                                                    nodeWorkItemId++);
        NodeInstance node3 = createTestNodeInstance("active2",
                                                    "HumanTaskNode",
                                                    nodeWorkItemId++);
        NodeInstance node4 = createTestNodeInstance("Milestone 1",
                                                    "MilestoneNode",
                                                    nodeWorkItemId++);
        when(clientMock.getActiveNodes(eq(containerId),
                                       eq(caseId),
                                       anyInt(),
                                       anyInt())).thenReturn(Arrays.asList(node1,
                                                                           node2,
                                                                           node3,
                                                                           node4));

        NodeInstance node5 = createTestNodeInstance("complete1",
                                                    "Human Task",
                                                    nodeWorkItemId++);
        NodeInstance node6 = createTestNodeInstance("complete2",
                                                    "Service Task",
                                                    nodeWorkItemId++);
        when(clientMock.getCompletedNodes(eq(containerId),
                                          eq(caseId),
                                          anyInt(),
                                          anyInt())).thenReturn(Arrays.asList(node5,
                                                                              node6));

        TaskInstance t1 = TaskInstance.builder()
                .actualOwner("Koe")
                .build();

        when(userTaskServicesClient.findTaskByWorkItemId(node1.getWorkItemId())).thenReturn(t1);
        when(userTaskServicesClient.findTaskByWorkItemId(node3.getWorkItemId())).thenReturn(t1);
        when(userTaskServicesClient.findTaskByWorkItemId(node5.getWorkItemId())).thenReturn(t1);

        Actions actions = testedService.getCaseActions(serverTemplateId,
                                                       containerId,
                                                       caseId,
                                                       userId);

        assertEquals(5,
                     actions.getAvailableActions().size());
        assertCaseActionAdHocFragment(cAHF1,
                                      actions.getAvailableActions().get(0));
        assertCaseActionAdHocFragment(cAHF2,
                                      actions.getAvailableActions().get(1));
        assertCaseActionAdHocFragment(cAHF6,
                                      actions.getAvailableActions().get(2));
        assertCaseActionAdHocFragmentWithStage(cAHF1_stage1,
                                               actions.getAvailableActions().get(3));
        assertCaseActionAdHocFragmentWithStage(cAHF2_stage1,
                                               actions.getAvailableActions().get(4));

        assertEquals(4,
                     actions.getInProgressAction().size());
        assertCaseActionNodeInstance(node1,
                                     actions.getInProgressAction().get(0));
        assertCaseActionNodeInstance(node2,
                                     actions.getInProgressAction().get(1));
        assertCaseActionNodeInstance(node3,
                                     actions.getInProgressAction().get(2));
        assertCaseActionNodeInstance(node4,
                                     actions.getInProgressAction().get(3));

        assertEquals(2,
                     actions.getCompleteActions().size());
        assertCaseActionNodeInstance(node5,
                                     actions.getCompleteActions().get(0));
        assertCaseActionNodeInstance(node6,
                                     actions.getCompleteActions().get(1));

        verify(clientMock).getAdHocFragments(containerId,
                                             caseId);
        verify(clientMock).getActiveNodes(eq(containerId),
                                          eq(caseId),
                                          eq(0),
                                          anyInt());
        verify(clientMock).getCompletedNodes(eq(containerId),
                                             eq(caseId),
                                             eq(0),
                                             anyInt());
        verify(userTaskServicesClient).findTaskByWorkItemId(node1.getWorkItemId());
        verify(userTaskServicesClient,
               never()).findTaskByWorkItemId(node2.getWorkItemId());
        verify(userTaskServicesClient).findTaskByWorkItemId(node3.getWorkItemId());
        verify(userTaskServicesClient,
               never()).findTaskByWorkItemId(node4.getWorkItemId());
        verify(userTaskServicesClient).findTaskByWorkItemId(node5.getWorkItemId());
        verify(userTaskServicesClient,
               never()).findTaskByWorkItemId(node6.getWorkItemId());
    }

    @Test
    public void getInProgressActionsTest() {
        Long node1WorkItemId = 1L;
        Long node2WorkItemId = 2L;
        String taskActualOwner = "Owner";

        NodeInstance node1 = createTestNodeInstance("active1",
                                                    "Human Task",
                                                    node1WorkItemId);
        NodeInstance node2 = createTestNodeInstance("active2",
                                                    "Service Task",
                                                    node2WorkItemId);
        TaskInstance t1 = TaskInstance.builder()
                .actualOwner(taskActualOwner)
                .build();

        when(clientMock.getActiveNodes(eq(containerId),
                                       eq(caseId),
                                       anyInt(),
                                       anyInt())).thenReturn(Arrays.asList(node1,
                                                                           node2));
        when(userTaskServicesClient.findTaskByWorkItemId(node1WorkItemId)).thenReturn(t1);

        List<CaseActionSummary> actionsSummaries = testedService.getInProgressActions(containerId,
                                                                                      caseId);

        assertEquals(2,
                     actionsSummaries.size());
        assertEquals(CaseActionStatus.IN_PROGRESS,
                     actionsSummaries.get(0).getActionStatus());
        assertEquals(CaseActionStatus.IN_PROGRESS,
                     actionsSummaries.get(1).getActionStatus());
        assertEquals(taskActualOwner,
                     actionsSummaries.get(0).getActualOwner());
        assertTrue(isNullOrEmpty(actionsSummaries.get(1).getActualOwner()));

        assertCaseActionNodeInstance(node1,
                                     actionsSummaries.get(0));
        assertCaseActionNodeInstance(node2,
                                     actionsSummaries.get(1));
        verify(userTaskServicesClient).findTaskByWorkItemId(node1WorkItemId);
        verify(userTaskServicesClient,
               never()).findTaskByWorkItemId(node2WorkItemId);
    }

    @Test
    public void getAdHocActionsTest() {
        final CaseInstance ci = createTestInstance(caseId);
        CaseStage stage1 = createTestCaseStage("stage1",
                                               "stage1-name",
                                               CaseStageStatus.ACTIVE.getStatus());
        CaseAdHocFragment cAHF1_stage1 = createTestCaseAdHocFragment("stage1-adHoc-1",
                                                                     "adHocFragment-type-1");
        CaseAdHocFragment cAHF2_stage1 = createTestCaseAdHocFragment("stage1-adHoc-2",
                                                                     "adHocFragment-type-2");

        CaseStage stage2 = createTestCaseStage("stage2",
                                               "stage2-name",
                                               CaseStageStatus.COMPLETED.getStatus());
        CaseAdHocFragment cAHF1_stage2 = createTestCaseAdHocFragment("stage2-adHoc-1",
                                                                     "adHocFragment-type-1");
        CaseAdHocFragment cAHF2_stage2 = createTestCaseAdHocFragment("stage2-adHoc-2",
                                                                     "adHocFragment-type-2");

        stage1.setAdHocFragments(Arrays.asList(cAHF1_stage1,
                                               cAHF2_stage1));
        stage2.setAdHocFragments(Arrays.asList(cAHF1_stage2,
                                               cAHF2_stage2));
        ci.setStages(Arrays.asList(stage1,
                                   stage2));

        when(clientMock.getCaseInstance(ci.getContainerId(),
                                        ci.getCaseId(),
                                        false,
                                        true,
                                        true,
                                        true))
                .thenReturn(ci);

        CaseAdHocFragment cAHF1 = createTestCaseAdHocFragment("adHocFragment-name-1",
                                                              "adHocFragment-type-1");
        CaseAdHocFragment cAHF2 = createTestCaseAdHocFragment("adHocFragment-name-2",
                                                              "adHocFragment-type-2");
        when(clientMock.getAdHocFragments(containerId,
                                          caseId)).thenReturn(Arrays.asList(cAHF1,
                                                                            cAHF2));

        List<CaseActionSummary> ahdocActions = testedService.getAdHocActions(serverTemplateId,
                                                                             containerId,
                                                                             caseId);

        assertEquals(4,
                     ahdocActions.size());

        assertCaseActionAdHocFragment(cAHF1,
                                      ahdocActions.get(0));
        assertCaseActionAdHocFragment(cAHF2,
                                      ahdocActions.get(1));
        assertCaseActionAdHocFragmentWithStage(cAHF1_stage1,
                                               ahdocActions.get(2));
        assertCaseActionAdHocFragmentWithStage(cAHF2_stage1,
                                               ahdocActions.get(3));

        verify(clientMock).getAdHocFragments(containerId,
                                             caseId);
    }

    @Test
    public void getCompletedActionsTest_withUserTasks() {
        final NodeInstance nodeInstance = createTestNodeInstance("completedNode",
                                                                 NODE_TYPE_HUMAN_TASK.get(0),
                                                                 1L);
        final TaskInstance taskInstance = TaskInstance.builder().actualOwner("owner").build();
        when(clientMock.getCompletedNodes(containerId,
                                          caseId,
                                          0,
                                          PAGE_SIZE_UNLIMITED)).thenReturn(singletonList(nodeInstance));
        when(userTaskServicesClient.findTaskByWorkItemId(nodeInstance.getWorkItemId())).thenReturn(taskInstance);

        final List<CaseActionSummary> result = testedService.getCompletedActions(containerId,
                                                                                 caseId);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.get(0).getName()).isEqualTo(nodeInstance.getName());
            softly.assertThat(result.get(0).getType()).isEqualTo(nodeInstance.getNodeType());
            softly.assertThat(result.get(0).getActualOwner()).isEqualTo(taskInstance.getActualOwner());
            softly.assertThat(result.get(0).getActionStatus()).isEqualTo(CaseActionStatus.COMPLETED);
        });
    }

    @Test
    public void getCompletedActionsTest_withoutUserTasks() {
        final NodeInstance nodeInstance = createTestNodeInstance("completedNode",
                                                                 "Service Task",
                                                                 1L);
        when(clientMock.getCompletedNodes(containerId,
                                          caseId,
                                          0,
                                          PAGE_SIZE_UNLIMITED)).thenReturn(singletonList(nodeInstance));

        final List<CaseActionSummary> result = testedService.getCompletedActions(containerId,
                                                                                 caseId);

        verifyZeroInteractions(userTaskServicesClient);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.get(0).getName()).isEqualTo(nodeInstance.getName());
            softly.assertThat(result.get(0).getType()).isEqualTo(nodeInstance.getNodeType());
            softly.assertThat(result.get(0).getActualOwner()).isEmpty();
            softly.assertThat(result.get(0).getActionStatus()).isEqualTo(CaseActionStatus.COMPLETED);
        });
    }

    @Test
    public void testMilestoneNodePredicate() {
        assertFalse(new RemoteCaseManagementServiceImpl.MilestoneNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(0)).build()));
        assertTrue(new RemoteCaseManagementServiceImpl.MilestoneNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_MILESTONE).build()));
    }

    @Test
    public void testHumanTaskNodePredicate() {
        final String taskName = "Task Name";
        final List<CaseActionSummary> actions = Arrays.asList(
                CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(0)).name(taskName).build(),
                CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(1)).name(taskName).build()
        );
        assertTrue(new RemoteCaseManagementServiceImpl.HumanTaskNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(0)).name(taskName).build(),
                                                                                     actions));
        assertTrue(new RemoteCaseManagementServiceImpl.HumanTaskNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(1)).name(taskName).build(),
                                                                                     actions));
        assertFalse(new RemoteCaseManagementServiceImpl.HumanTaskNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_HUMAN_TASK.get(0)).name("Task Name 2").build(),
                                                                                      actions));
        assertFalse(new RemoteCaseManagementServiceImpl.HumanTaskNodePredicate().test(CaseActionSummary.builder().type(NODE_TYPE_MILESTONE).name(taskName).build(),
                                                                                      actions));
    }
}