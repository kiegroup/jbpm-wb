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

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.kie.server.api.model.cases.CaseComment;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.client.CaseServicesClient;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
@ApplicationScoped
public class RemoteCaseManagementServiceImpl extends AbstractKieServerService implements CaseManagementService {

    @Override
    public List<CaseDefinitionSummary> getCaseDefinitions(final String serverTemplateId, final Integer page, final Integer pageSize) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        final List<CaseDefinition> caseDefinitions = client.getCaseDefinitions(page, pageSize);

        return caseDefinitions.stream().map(new CaseDefinitionMapper()).collect(toList());
    }

    @Override
    public CaseDefinitionSummary getCaseDefinition(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        return ofNullable(client.getCaseDefinition(containerId, caseDefinitionId)).map(new CaseDefinitionMapper()).orElse(null);
    }

    @Override
    public List<CaseInstanceSummary> getCaseInstances(final String serverTemplateId, final Integer page, final Integer pageSize) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        final List<CaseInstance> caseInstances = client.getCaseInstances(page, pageSize);

        return caseInstances.stream().map(new CaseInstanceMapper()).collect(toList());
    }

    @Override
    public String startCaseInstance(final String serverTemplateId, final String containerId, final String caseDefinitionId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        final CaseServicesClient client = getClient(serverTemplateId, containerId, CaseServicesClient.class);

        return client.startCase(containerId, caseDefinitionId);
    }

    @Override
    public void cancelCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, containerId, CaseServicesClient.class);

        client.cancelCaseInstance(containerId, caseId);
    }

    @Override
    public void destroyCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, containerId, CaseServicesClient.class);

        client.destroyCaseInstance(containerId, caseId);
    }

    @Override
    public CaseInstanceSummary getCaseInstance(final String serverTemplateId, final String containerId, final String caseId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return null;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        return ofNullable(client.getCaseInstance(containerId, caseId, true, true, true, true))
                .map(new CaseInstanceMapper())
                .orElse(null);
    }

    @Override
    public void assignUserToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        client.assignUserToRole(containerId, caseId, roleName, user);
    }

    @Override
    public void assignGroupToRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        client.assignGroupToRole(containerId, caseId, roleName, group);
    }

    @Override
    public void removeUserFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String user) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        client.removeUserFromRole(containerId, caseId, roleName, user);
    }

    @Override
    public void removeGroupFromRole(final String serverTemplateId, final String containerId, final String caseId, final String roleName, final String group) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);

        client.removeGroupFromRole(containerId, caseId, roleName, group);
    }
    @Override
    public List<CaseCommentSummary> getComments(final String serverTemplateId, final String containerId,
                                                final String caseId, final Integer page, final Integer pageSize) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return emptyList();
        }
        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);
        final List<CaseComment> caseComments = client.getComments(containerId, caseId, page, pageSize);
        return caseComments.stream()
                .map(cd -> new CaseCommentSummary(caseId, cd.getId(), cd.getAuthor(), cd.getText(), cd.getAddedAt()))
                .collect(toList());

    }

    @Override
    public void addComment(final String serverTemplateId, final String containerId, final String caseId,
                           final String author, final String text) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);
        client.addComment(containerId, caseId, author, text);
    }

    @Override
    public void updateComment(final String serverTemplateId, final String containerId, final String caseId,
                              final String commentId, final String author, final String text) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);
        client.updateComment(containerId, caseId, commentId, author, text);

    }

    @Override
    public void removeComment(final String serverTemplateId, final String containerId, final String caseId,
                              final String commentId) {
        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return;
        }

        final CaseServicesClient client = getClient(serverTemplateId, CaseServicesClient.class);
        client.removeComment(containerId, caseId, commentId);
    }

}