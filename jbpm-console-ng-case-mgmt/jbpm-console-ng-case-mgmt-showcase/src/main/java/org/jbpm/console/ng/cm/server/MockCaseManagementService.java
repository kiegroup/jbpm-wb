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

package org.jbpm.console.ng.cm.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.console.ng.cm.backend.server.RemoteCaseManagementServiceImpl;
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.model.CaseMilestoneSummary;
import org.jbpm.console.ng.cm.model.CaseRoleAssignmentSummary;
import org.jbpm.console.ng.cm.model.CaseStageSummary;
import org.jbpm.console.ng.cm.util.CaseActionSearchRequest;
import org.jbpm.console.ng.cm.util.CaseActionsLists;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;

import org.jbpm.console.ng.cm.util.CaseMilestoneSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Specializes
public class MockCaseManagementService extends RemoteCaseManagementServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockCaseManagementService.class);
    private static final String CASE_DEFINITIONS_JSON = "case_definitions.json";
    private static final String CASE_MILESTONES_JSON = "case_milestones.json";
    private static final String CASE_STAGES_JSON = "case_stages.json";
    private static final String CASE_ACTIONS_JSON = "case_actions.json";

    private static int commentIdGenerator = 0;
    private static long actionIdLongenerator = 8;

    @Inject
    protected User identity;

    private List<CaseDefinitionSummary> caseDefinitionList = emptyList();
    private List<CaseInstanceSummary> caseInstanceList = new ArrayList<>();
    private List<CaseStageSummary> caseStageList = new ArrayList<>();
    private Map<String, List<CaseCommentSummary>> caseCommentMap = new HashMap<>();
    private List<CaseMilestoneSummary> caseMilestoneList = new ArrayList<>();
    private Map<String, List<CaseActionSummary>> caseActionMap = new HashMap<>();
    private List<CaseActionSummary> caseActionList = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CASE_DEFINITIONS_JSON);
            caseDefinitionList = Arrays.asList(mapper.readValue(inputStream, CaseDefinitionSummary[].class));
            caseMilestoneList = Arrays.asList(mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(CASE_MILESTONES_JSON), CaseMilestoneSummary[].class));
            caseStageList = Arrays.asList(mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(CASE_STAGES_JSON), CaseStageSummary[].class));
            caseActionList = new ArrayList<CaseActionSummary>(Arrays.asList(mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream(CASE_ACTIONS_JSON), CaseActionSummary[].class)));

            LOGGER.info("Loaded {} case definitions", caseDefinitionList.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load json data file", e);
        }
    }

    @Override
    public CaseDefinitionSummary getCaseDefinition(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        return caseDefinitionList.stream().filter(c -> c.getId().equals(caseDefinitionId)).findFirst().get();
    }

    @Override
    public List<CaseDefinitionSummary> getCaseDefinitions() {
        return caseDefinitionList;
    }

    @Override
    public String startCaseInstance(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        final CaseInstanceSummary ci = CaseInstanceSummary
                .builder()
                .caseId("CASE-" + Strings.padStart(String.valueOf(caseInstanceList.size() + 1), 5, '0'))
                .owner(identity.getIdentifier())
                .startedAt(new Date())
                .caseDefinitionId(caseDefinitionId)
                .status(1)
                .description("New case instance for development")
                .containerId(containerId)
                .stages(caseStageList)
                .build();
        caseInstanceList.add(ci);
        caseActionMap.putIfAbsent(ci.getCaseId(), caseActionList);
        return ci.getCaseId();
    }

    @Override
    public List<CaseInstanceSummary> getCaseInstances(final CaseInstanceSearchRequest request) {
        return caseInstanceList.stream()
                .filter(c -> c.getStatus().equals(request.getStatus()))
                .sorted(getCaseInstanceSummaryComparator(request))
                .collect(toList());
    }

    @Override
    public CaseInstanceSummary getCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        return caseInstanceList.stream().filter(c -> c.getCaseId().equals(caseId)).findFirst().get();
    }

    @Override
    public void cancelCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        executeOnCaseInstance(caseId, c -> c.setStatus(3));
    }

    @Override
    public void destroyCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        executeOnCaseInstance(caseId, c -> c.setStatus(3));
    }

    @Override
    public List<CaseCommentSummary> getComments(final String serverTemplateId, final String containerId, final String caseId) {
        return ofNullable(caseCommentMap.get(caseId)).orElse(emptyList());
    }

    @Override
    public void addComment(final String serverTemplateId, final String containerId, final String caseId, final String author, final String text) {
        final List<CaseCommentSummary> commentSummaryList = caseCommentMap.getOrDefault(caseId, new ArrayList<>());

        final String newId = String.valueOf(commentIdGenerator++);

        final CaseCommentSummary caseCommentSummary = CaseCommentSummary.builder().id(newId).author(author).text(text).addedAt(new Date()).build();
        commentSummaryList.add(caseCommentSummary);
        caseCommentMap.putIfAbsent(caseId, commentSummaryList);
    }

    @Override
    public void updateComment(final String serverTemplateId, final String containerId, final String caseId, final String commentId, final String author, final String text) {
        ofNullable(caseCommentMap.get(caseId)).ifPresent(l -> l.stream().filter(c -> c.getId().equals(commentId)).findFirst().ifPresent(c -> c.setText(text)));
    }

    @Override
    public void removeComment(final String serverTemplateId, final String containerId, final String caseId, final String commentId) {
        ofNullable(caseCommentMap.get(caseId)).ifPresent(l -> l.stream().filter(c -> c.getId().equals(commentId)).findFirst().ifPresent(c -> l.remove(c)));
    }

    @Override
    public void assignUserToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        executeOnCaseRole(caseId, roleName, r -> r.getUsers().add(user));
    }

    @Override
    public void assignGroupToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        executeOnCaseRole(caseId, roleName, r -> r.getGroups().add(group));
    }

    @Override
    public void removeUserFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        executeOnCaseRole(caseId, roleName, r -> r.getUsers().remove(user));
    }

    @Override
    public void removeGroupFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        executeOnCaseRole(caseId, roleName, r -> r.getGroups().remove(group));
    }

    private void executeOnCaseInstance(final String caseId, final Consumer<CaseInstanceSummary> consumer) {
        caseInstanceList.stream().filter(c -> c.getCaseId().equals(caseId)).findFirst().ifPresent(consumer);
    }

    private void executeOnCaseRole(final String caseId, final String roleName, final Consumer<CaseRoleAssignmentSummary> consumer) {
        executeOnCaseInstance(caseId,
                c -> {
                    final CaseRoleAssignmentSummary role = c.getRoleAssignments().stream()
                            .filter(r -> r.getName().equals(roleName)).findFirst()
                            .orElseGet(() -> {
                                final CaseRoleAssignmentSummary newRole = CaseRoleAssignmentSummary.builder().name(roleName).build();
                                c.getRoleAssignments().add(newRole);
                                return newRole;
                            });
                    consumer.accept(role);
                }
        );
    }

    @Override
    public List<CaseMilestoneSummary> getCaseMilestones(final String containerId, final String caseId, final CaseMilestoneSearchRequest request) {
        return caseMilestoneList.stream()
                .sorted(getCaseMilestoneSummaryComparator(request))
                .collect(toList());
    }

    @Override
    public CaseActionsLists getCaseActionsLists(String containerId, String caseId, String userId) {
        CaseActionSearchRequest request = new CaseActionSearchRequest();

        CaseActionsLists caseActionsLists = new CaseActionsLists();
        caseActionsLists.setAvailableActionList(getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"AdhocFragment","Ready"));
        caseActionsLists.setInprogressActionList(getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"InProgress","Reserved"));
        caseActionsLists.setCompleteActionList(getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"Completed","Suspended","Failed","Error","Exited","Obsolete"));
        return caseActionsLists;
    }

    @Override
    public List<CaseActionSummary> getCaseActions(final String containerId, final String caseId, final CaseActionSearchRequest request, String userId) {

        switch (request.getFilterBy()) {
            case AVAILABLE: {
                return getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"AdhocFragment","Ready");
            }
            case IN_PROGRESS: {
                return getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"InProgress","Reserved");
            }
            case COMPLETED: {
                return getCaseActionsLists(caseId,getCaseActionsSummaryComparator(request),"Completed","Suspended","Failed","Error","Exited","Obsolete");
            }
            default: {
                return getCaseActionsLists(caseId, null, getCaseActionsSummaryComparator(request));
            }
        }

    }

    List<CaseActionSummary> getCaseActionsLists(String caseId, Comparator<CaseActionSummary> comparator, final String ... statuses) {
        List<CaseActionSummary> actions = new ArrayList<CaseActionSummary>();
        for(String status :statuses){
            actions.addAll(getCaseActionsLists(caseId, status, comparator));
        }
        return actions;
    }

    List<CaseActionSummary> getCaseActionsLists(String caseId, String status, Comparator<CaseActionSummary> comparator) {
        List<CaseActionSummary> actionsList = ofNullable(caseActionMap.get(caseId)).orElse(emptyList());

        return actionsList.stream()
                .filter(c -> (status != null ? c.getStatus().equals(status) : true))
                .sorted(comparator).collect(toList());
    }


    public void addDynamicUserTask(String containerId, String caseId, String name, String description, String actors, String groups, Map<String, Object> data) {
        final List<CaseActionSummary> actionSummaryList = caseActionMap.getOrDefault(caseId, new ArrayList<>());

        final CaseActionSummary action = CaseActionSummary.builder()
                .id(actionIdLongenerator++)
                .name(name)
                .containerId(containerId)
                .actualOwner(actors)
                .status("Ready")
                .createdOn(new Date())
                .build();
        actionSummaryList.add(action);

        caseActionMap.putIfAbsent(caseId, actionSummaryList);

    }

    @Override
    public void triggerAdHocFragmentInStage(String containerId, String caseId, String stageId, String adHocName, Map<String, Object> data) {
        ofNullable(caseActionMap.get(caseId)).ifPresent(l -> l.stream().filter(c -> c.getName().equals(adHocName)).findFirst().ifPresent(c -> c.setStatus("Ready")));
    }
}