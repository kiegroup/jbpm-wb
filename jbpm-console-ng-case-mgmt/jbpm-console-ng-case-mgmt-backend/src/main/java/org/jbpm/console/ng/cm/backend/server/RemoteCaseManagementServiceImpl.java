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
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.client.CaseServicesClient;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

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

        return caseDefinitions.stream()
                .map(cd -> new CaseDefinitionSummary(cd.getIdentifier(), cd.getName(), cd.getContainerId()))
                .collect(toList());
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

        return Optional.ofNullable(client.getCaseInstance(containerId, caseId, true, true, true, true))
                .map(new CaseInstanceMapper())
                .orElse(null);
    }

}