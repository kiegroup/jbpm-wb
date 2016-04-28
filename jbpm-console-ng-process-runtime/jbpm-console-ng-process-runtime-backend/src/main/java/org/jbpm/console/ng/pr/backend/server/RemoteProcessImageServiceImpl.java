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

package org.jbpm.console.ng.pr.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.pr.service.ProcessImageService;
import org.kie.server.client.UIServicesClient;

@Service
@ApplicationScoped
public class RemoteProcessImageServiceImpl extends AbstractKieServerService implements ProcessImageService {

    @Override
    public String getProcessInstanceDiagram(String serverTemplateId, String containerId, Long processInstanceId) {
        UIServicesClient uiServicesClient = getClient(serverTemplateId, containerId, UIServicesClient.class);

        return uiServicesClient.getProcessInstanceImage(containerId, processInstanceId);
    }

    @Override
    public String getProcessDiagram(String serverTemplateId, String containerId, String processId) {
        UIServicesClient uiServicesClient = getClient(serverTemplateId, containerId, UIServicesClient.class);

        return uiServicesClient.getProcessImage(containerId, processId);
    }

}