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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseCommentSummary;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;
import org.jbpm.workbench.cm.model.CaseRoleAssignmentSummary;
import org.jbpm.workbench.cm.model.ProcessDefinitionSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.jbpm.workbench.cm.util.CaseInstanceSortBy;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.kie.server.api.model.cases.CaseComment;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseFile;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.api.model.cases.CaseMilestone;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.NodeInstance;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;

@Service
@ApplicationScoped
@KieServerExceptionHandler
public class RemoteCaseManagementServiceImpl implements CaseManagementService {

    public static final int PAGE_SIZE_UNLIMITED = Integer.MAX_VALUE;
    public static final String CASE_OWNER_ROLE = "owner";
    public static final List<String> NODE_TYPE_HUMAN_TASK = Arrays.asList("Human Task", "HumanTaskNode");

    @Inject
    private CaseServicesClient client;

    @Inject
    private UserTaskServicesClient userTaskServicesClient;

    @Override
    public List<CaseDefinitionSummary> getCaseDefinitions() {
        final List<CaseDefinition> caseDefinitions = client.getCaseDefinitions(0, PAGE_SIZE_UNLIMITED, CaseServicesClient.SORT_BY_CASE_DEFINITION_NAME, true);
        return caseDefinitions.stream().map(new CaseDefinitionMapper()).collect(toList());
    }

    @Override
    public CaseDefinitionSummary getCaseDefinition(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        return ofNullable(client.getCaseDefinition(containerId, caseDefinitionId)).map(new CaseDefinitionMapper()).orElse(null);
    }

    @Override
    public List<CaseInstanceSummary> getCaseInstances(final CaseInstanceSearchRequest request) {
        final List<CaseInstance> caseInstances = client.getCaseInstances(singletonList(request.getStatus().getName()), 0, PAGE_SIZE_UNLIMITED);
        final Comparator<CaseInstanceSummary> comparator = getCaseInstanceSummaryComparator(request);
        return caseInstances.stream().map(new CaseInstanceMapper()).sorted(comparator).collect(toList());
    }

    protected Comparator<CaseInstanceSummary> getCaseInstanceSummaryComparator(final CaseInstanceSearchRequest request) {
        Comparator<CaseInstanceSummary> comparator;
        switch (ofNullable(request.getSortBy()).orElse(CaseInstanceSortBy.CASE_ID)) {
            case START_TIME:
                comparator = comparing(CaseInstanceSummary::getStartedAt);
                break;
            case CASE_ID:
            default:
                comparator = comparing(CaseInstanceSummary::getCaseId);
        }
        return request.getSortByAsc() ? comparator : comparator.reversed();
    }

    @Override
    public String startCaseInstance(final String serverTemplateId, final String containerId, final String caseDefinitionId, final String owner,
                                    final List<CaseRoleAssignmentSummary> roleAssignments) {
        final CaseFile.Builder builder = CaseFile.builder();
        builder.addUserAssignments(CASE_OWNER_ROLE, owner);
        roleAssignments.forEach( a -> {
            a.getGroups().forEach(g -> builder.addGroupAssignments(a.getName(), g));
            a.getUsers().forEach(u -> builder.addUserAssignments(a.getName(), u));
        });
        return client.startCase(containerId, caseDefinitionId, builder.build());
    }

    @Override
    public void cancelCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        client.cancelCaseInstance(containerId, caseId);
    }

    @Override
    public void destroyCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        client.destroyCaseInstance(containerId, caseId);
    }

    @Override
    public CaseInstanceSummary getCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        return ofNullable(client.getCaseInstance(containerId, caseId, true, true, true, true))
                .map(new CaseInstanceMapper())
                .orElse(null);
    }

    @Override
    public void assignUserToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        client.assignUserToRole(containerId, caseId, roleName, user);
    }

    @Override
    public void assignGroupToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        client.assignGroupToRole(containerId, caseId, roleName, group);
    }

    @Override
    public void removeUserFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        client.removeUserFromRole(containerId, caseId, roleName, user);
    }

    @Override
    public void removeGroupFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        client.removeGroupFromRole(containerId, caseId, roleName, group);
    }

    @Override
    public List<CaseCommentSummary> getComments(final String serverTemplateId, final String containerId, final String caseId) {
        final List<CaseComment> caseComments = client.getComments(containerId, caseId, 0, PAGE_SIZE_UNLIMITED);
        return caseComments.stream().map(new CaseCommentMapper()).collect(toList());
    }

    @Override
    public void addComment(final String serverTemplateId, final String containerId, final String caseId,
                           final String author, final String text) {
        client.addComment(containerId, caseId, author, text);
    }

    @Override
    public void updateComment(final String serverTemplateId, final String containerId, final String caseId,
                              final String commentId, final String author, final String text) {
        client.updateComment(containerId, caseId, commentId, author, text);
    }

    @Override
    public void removeComment(final String serverTemplateId, final String containerId, final String caseId,
                              final String commentId) {
        client.removeComment(containerId, caseId, commentId);
    }

    @Override
    public List<CaseMilestoneSummary> getCaseMilestones(final String containerId, final String caseId, final CaseMilestoneSearchRequest request) {
        final List<CaseMilestone> caseMilestones = client.getMilestones(containerId, caseId, false, 0, PAGE_SIZE_UNLIMITED);
        final Comparator<CaseMilestoneSummary> comparator = getCaseMilestoneSummaryComparator(request);
        return caseMilestones.stream().map(new CaseMilestoneMapper()).sorted(comparator).collect(toList());
    }

    protected Comparator<CaseMilestoneSummary> getCaseMilestoneSummaryComparator(final CaseMilestoneSearchRequest request) {
        Comparator<CaseMilestoneSummary> comparatorByName = comparing(CaseMilestoneSummary::getName);
        return comparing(CaseMilestoneSummary::getStatus).thenComparing(request.getSortByAsc() ? comparatorByName : comparatorByName.reversed());
    }

    @Override
    public Actions getCaseActions(String serverTemplateId, String container, String caseId, String userId) {
        Actions actions = new Actions();
        actions.setAvailableActions(getAdHocActions(serverTemplateId, container, caseId));
        actions.setInProgressAction(getInProgressActions(container, caseId));
        actions.setCompleteActions(getCompletedActions(container, caseId));
        return actions;
    }

    public List<CaseActionSummary> getInProgressActions(String containerId, String caseId) {
        List<NodeInstance> activeNodes = client.getActiveNodes(containerId, caseId, 0, PAGE_SIZE_UNLIMITED);

        return activeNodes.stream()
                .map(s -> new CaseActionNodeInstanceMapper(
                        (NODE_TYPE_HUMAN_TASK.contains(s.getNodeType()) ?
                                userTaskServicesClient.findTaskByWorkItemId(s.getWorkItemId()).getActualOwner() :
                                ""),
                        CaseActionStatus.IN_PROGRESS).apply(s))
                .collect(toList());
    }

    public List<NodeInstance> getCaseCompletedNodes(String containerId, String caseId) {
        return client.getCompletedNodes(containerId, caseId, 0, PAGE_SIZE_UNLIMITED);
    }

    public List<CaseActionSummary> getCompletedActions(String containerId, String caseId) {
        List<NodeInstance> activeNodes = getCaseCompletedNodes(containerId, caseId);
        return activeNodes.stream()
                .map(s -> new CaseActionNodeInstanceMapper(
                        (NODE_TYPE_HUMAN_TASK.contains(s.getNodeType()) ?
                                userTaskServicesClient.findTaskByWorkItemId(s.getWorkItemId()).getActualOwner() :
                                ""),
                        CaseActionStatus.COMPLETED).apply(s))
                .collect(toList());
    }

    public List<CaseActionSummary> getAdHocFragments(String containerId, String caseId) {
        return client.getAdHocFragments(containerId, caseId)
                .stream()
                .map(new CaseActionAdHocMapper(""))
                .collect(toList());
    }

    public List<CaseActionSummary> getAdHocActions(String serverTemplateId, String containerId, String caseId) {
        List<CaseActionSummary> adHocActions = getAdHocFragments(containerId, caseId);
        CaseInstanceSummary caseInstanceSummary = getCaseInstance(serverTemplateId, containerId, caseId);
        caseInstanceSummary.getStages().stream()
                .filter(s -> s.getStatus().equals(CaseStageStatus.ACTIVE.getStatus()))
                .forEach(ah -> {
                    if (ah.getAdHocActions().size() > 0) {
                        adHocActions.addAll(ah.getAdHocActions());
                    }
                    return;
                });
        return adHocActions;
    }

    public void addDynamicUserTask(String containerId, String caseId, String name, String description, String actors, String groups, Map<String, Object> data) {
        client.addDynamicUserTask(containerId, caseId, name, description, actors, groups, data);
    }

    public void addDynamicUserTaskToStage(String containerId, String caseId, String stageId, String name, String description, String actors, String groups, Map<String, Object> data) {
        client.addDynamicUserTaskToStage(containerId, caseId, stageId, name, description, actors, groups, data);
    }

    public void addDynamicSubProcess(String containerId, String caseId, String processId, Map<String, Object> data) {
        client.addDynamicSubProcess(containerId, caseId, processId, data);
    }

    public void addDynamicSubProcessToStage(String containerId, String caseId, String stageId, String processId, Map<String, Object> data) {
        client.addDynamicSubProcessToStage(containerId, caseId, stageId, processId, data);
    }

    @Override
    public void triggerAdHocActionInStage(String containerId, String caseId, String stageId, String adHocName, Map<String, Object> data) {
        client.triggerAdHocFragmentInStage(containerId, caseId, stageId, adHocName, data);
    }

    @Override
    public void triggerAdHocAction(String containerId, String caseId, String adHocName, Map<String, Object> data) {
        client.triggerAdHocFragment(containerId, caseId, adHocName, data);
    }

    @Override
    public List<ProcessDefinitionSummary> getProcessDefinitions(String containerId) {
        final List<ProcessDefinition> processDefinitions = client.findProcessesByContainerId(containerId, 0, PAGE_SIZE_UNLIMITED);
        return processDefinitions.stream().map(new ProcessDefinitionMapper()).collect(toList());
    }
}