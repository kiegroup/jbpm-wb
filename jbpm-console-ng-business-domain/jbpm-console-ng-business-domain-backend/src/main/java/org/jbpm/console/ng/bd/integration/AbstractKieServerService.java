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

package org.jbpm.console.ng.bd.integration;

import javax.inject.Inject;

import org.kie.server.client.KieServicesClient;

public abstract class AbstractKieServerService {

    @Inject
    private KieServerIntegration kieServerIntegration;

    protected <T> T getClient(final String serverTemplateId, final Class<T> clientType) {
        KieServicesClient client = getKieServicesClient(serverTemplateId);
        return client.getServicesClient(clientType);
    }

    protected <T> T getClient(final String serverTemplateId, final String containerId, final Class<T> clientType) {
        KieServicesClient client = getKieServicesClient(serverTemplateId, containerId);
        return client.getServicesClient(clientType);
    }

    protected KieServicesClient getKieServicesClient(final String serverTemplateId, final String containerId) {
        KieServicesClient client = kieServerIntegration.getServerClient(serverTemplateId, containerId);
        if (client == null) {
            throw new RuntimeException("No client to interact with server " + serverTemplateId);
        }
        return client;
    }

    protected KieServicesClient getKieServicesClient(final String serverTemplateId) {
        KieServicesClient client = kieServerIntegration.getServerClient(serverTemplateId);
        if (client == null) {
            throw new RuntimeException("No client to interact with server " + serverTemplateId);
        }
        return client;
    }
}