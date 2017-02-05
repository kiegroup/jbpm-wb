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

package org.jbpm.workbench.cm.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.workbench.cm.model.CaseCommentSummary;
import org.jbpm.workbench.cm.model.CaseDefinitionSummary;
import org.jbpm.workbench.cm.model.CaseMilestoneSummary;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;

@Remote
public interface CaseManagementService {

    CaseDefinitionSummary getCaseDefinition(String serverTemplateId, String containerId, String caseDefinitionId);

    List<CaseDefinitionSummary> getCaseDefinitions();

    String startCaseInstance(String serverTemplateId, String containerId, String caseDefinitionId);

    List<CaseInstanceSummary> getCaseInstances(CaseInstanceSearchRequest request);

    CaseInstanceSummary getCaseInstance(String serverTemplateId, String containerId, String caseId);

    void cancelCaseInstance(String serverTemplateId, String containerId, String caseId);

    void destroyCaseInstance(String serverTemplateId, String containerId, String caseId);

    List<CaseCommentSummary> getComments(String serverTemplateId, String containerId, String caseId);

    void addComment(String serverTemplateId, String containerId, String caseId, String author, String text);

    void updateComment(String serverTemplateId, String containerId, String caseId, String commentId, String author, String text);

    void removeComment(String serverTemplateId, String containerId, String caseId, String commentId);

    void assignUserToRole(String serverTemplateId, String containerId, String caseId, String roleName, String user);

    void assignGroupToRole(String serverTemplateId, String containerId, String caseId, String roleName, String group);

    void removeUserFromRole(String serverTemplateId, String containerId, String caseId, String roleName, String user);

    void removeGroupFromRole(String serverTemplateId, String containerId, String caseId, String roleName, String group);

    List<CaseMilestoneSummary> getCaseMilestones(final String containerId, final String caseId , final CaseMilestoneSearchRequest request);

    Actions getCaseActions(String templateId, String container, String caseId, String userId);

    void addDynamicUserTask(String containerId, String caseId, String name, String description, String actors, String groups, Map<String, Object> data);

    void addDynamicUserTaskToStage(String containerId, String caseId, String stageId, String name, String description, String actors, String groups, Map<String, Object> data);

    void triggerAdHocActionInStage(String containerId, String caseId, String stageId, String adHocName, Map<String, Object> data);

    void triggerAdHocAction(String containerId, String caseId, String adHocName, Map<String, Object> data);

}