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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.model.CaseMilestoneSummary;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.jbpm.console.ng.cm.util.CaseActionSearchRequest;
import org.jbpm.console.ng.cm.util.CaseActionsLists;
import org.jbpm.console.ng.cm.util.CaseInstanceSearchRequest;
import org.jbpm.console.ng.cm.util.CaseInstanceSortBy;
import org.jbpm.console.ng.cm.util.CaseMilestoneSearchRequest;
import org.kie.server.api.model.cases.CaseComment;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.api.model.cases.CaseMilestone;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.CaseServicesClient;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;

@Service
@ApplicationScoped
public class RemoteCaseManagementServiceImpl implements CaseManagementService {

    public static final int PAGE_SIZE_UNLIMITED = Integer.MAX_VALUE;

    @Inject
    private CaseServicesClient client;

    @Override
    public List<CaseDefinitionSummary> getCaseDefinitions() {
        final List<CaseDefinition> caseDefinitions = client.getCaseDefinitions(0, PAGE_SIZE_UNLIMITED);
        return caseDefinitions.stream().map(new CaseDefinitionMapper()).collect(toList());
    }

    @Override
    public CaseDefinitionSummary getCaseDefinition(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        return ofNullable(client.getCaseDefinition(containerId, caseDefinitionId)).map(new CaseDefinitionMapper()).orElse(null);
    }

    @Override
    public List<CaseInstanceSummary> getCaseInstances(final CaseInstanceSearchRequest request) {
        final List<CaseInstance> caseInstances = client.getCaseInstances(singletonList(request.getStatus()), 0, PAGE_SIZE_UNLIMITED);
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
    public String startCaseInstance(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        return client.startCase(containerId, caseDefinitionId);
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
    public List<CaseMilestoneSummary> getCaseMilestones(final String containerId, final String caseId , final CaseMilestoneSearchRequest request) {
        final List<CaseMilestone> caseMilestones = client.getMilestones(containerId,caseId, false, 0, PAGE_SIZE_UNLIMITED);
        final Comparator<CaseMilestoneSummary> comparator = getCaseMilestoneSummaryComparator(request);
        return caseMilestones.stream().map(new CaseMilestoneMapper()).sorted(comparator).collect(toList());
    }

    protected Comparator<CaseMilestoneSummary> getCaseMilestoneSummaryComparator(final CaseMilestoneSearchRequest request) {
        Comparator<CaseMilestoneSummary> comparatorByName =comparing(CaseMilestoneSummary::getName);
        return comparing(CaseMilestoneSummary::getStatus).thenComparing(request.getSortByAsc() ? comparatorByName: comparatorByName.reversed());
    }

    @Override
    public CaseActionsLists getCaseActionsLists(String containerId, String caseId, String userId) {
        CaseActionSearchRequest request = new CaseActionSearchRequest();

        List<CaseActionSummary> availableActions =getCaseActionsByStatus(caseId,request, userId, "Ready" );
        availableActions.addAll(client.getAdHocFragments(containerId,caseId)
                .stream()
                .map(new CaseActionAdhocFragmentMapper())
                .sorted(comparing(CaseActionSummary::getName)).collect(toList()));

        CaseActionsLists caseActionsLists = new CaseActionsLists();
        caseActionsLists.setAvailableActionList(availableActions);
        caseActionsLists.setInprogressActionList(getCaseActionsByStatus(caseId,request, userId, "InProgress","Reserved" ));
        caseActionsLists.setCompleteActionList(getCaseActionsByStatus(caseId,request, userId, "Completed","Suspended","Failed",
                "Error","Exited","Obsolete"));
        return caseActionsLists;
    }

    @Override
    public List<CaseActionSummary> getCaseActions(final String containerId, final String caseId,
                                                       final CaseActionSearchRequest request, String userId) {
        switch(request.getFilterBy()){
        case AVAILABLE:{
                List<CaseActionSummary> actions =getCaseActionsByStatus(caseId,request, userId, "Ready" );
                actions.addAll(client.getAdHocFragments(containerId,caseId)
                                .stream()
                                .map(new CaseActionAdhocFragmentMapper())
                                .sorted(getCaseActionsSummaryComparator(request)).collect(toList()));
                return actions;
            }
            case IN_PROGRESS:{
                return getCaseActionsByStatus(caseId,request, userId, "InProgress","Reserved" );
            }
            case COMPLETED:{
                return getCaseActionsByStatus(caseId,request, userId, "Completed","Suspended","Failed",
                        "Error","Exited","Obsolete");
            }
        }
        return new ArrayList();
    }

    protected List<CaseActionSummary> getCaseActionsByStatus(final String caseId,final CaseActionSearchRequest request, String userId, String ... statuses ){
        List<String> statusList = new ArrayList<>();
        for(String status : statuses){
             statusList.add(status);
        }
        List<TaskSummary> actionSummaries = client.findCaseTasksAssignedAsBusinessAdministrator(caseId, userId,
                statusList,0, PAGE_SIZE_UNLIMITED,request.getSort(),request.isSortOrder());
        return actionSummaries.stream().map(new CaseActionMapper()).sorted(getCaseActionsSummaryComparator(request)).collect(toList());
    }

    protected Comparator<CaseActionSummary> getCaseActionsSummaryComparator(final CaseActionSearchRequest request) {
        Comparator<CaseActionSummary> comparatorByName =comparing(CaseActionSummary::getName);
        return comparing(CaseActionSummary::getStatus).thenComparing(request.isSortOrder() ? comparatorByName: comparatorByName.reversed());
    }

    public void addDynamicUserTask(String containerId, String caseId, String name, String description, String actors, String groups, Map<String, Object> data){
        client.addDynamicUserTask(containerId,caseId,name,description,actors,groups,data);
    }

    @Override
    public void triggerAdHocFragmentInStage(String containerId, String caseId, String stageId, String adHocName, Map<String, Object> data) {
        client.triggerAdHocFragmentInStage(containerId,caseId,stageId,adHocName,data);
    }

}