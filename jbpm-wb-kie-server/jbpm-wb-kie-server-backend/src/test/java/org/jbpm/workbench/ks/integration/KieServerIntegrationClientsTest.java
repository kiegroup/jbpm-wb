/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ks.integration;

import javax.enterprise.event.Event;

import org.jbpm.workbench.ks.integration.event.ServerInstanceRegistered;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.server.client.KieServicesClient;
import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.impl.client.KieServicesClientProvider;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.workbench.ks.integration.KieServerIntegration.SERVER_TEMPLATE_KEY;
import static org.jbpm.workbench.ks.integration.KieServerIntegrationServerTemplateTest.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class KieServerIntegrationClientsTest {

    @Mock
    SpecManagementService specManagementService;

    @Mock
    KieServices kieServices;

    @Mock
    Event<ServerInstanceRegistered> serverInstanceRegisteredEvent;

    @InjectMocks
    KieServerIntegration kieServerIntegration;

    @Before
    public void setup() {
        when(kieServices.newKieContainer(any())).thenReturn(mock(KieContainer.class));
    }

    @Test
    public void testServerTemplateClients() {
        final String serverTemplateId = "kie-server-test";
        final String serverInstanceId1 = serverTemplateId + "@1";
        final String serverInstanceId2 = serverTemplateId + "@2";
        final ContainerSpec containerSpec = newContainerSpec();
        final ServerInstance serverInstance1 = new ServerInstance();
        serverInstance1.setServerTemplateId(serverTemplateId);
        serverInstance1.setUrl("http://1");

        serverInstance1.setServerInstanceId(serverInstanceId1);
        final ServerTemplate serverTemplate = new ServerTemplate(serverTemplateId,
                                                                 serverTemplateId);
        serverTemplate.addContainerSpec(containerSpec);

        final ServerInstance serverInstance2 = new ServerInstance();
        serverInstance2.setServerTemplateId(serverTemplateId);
        serverInstance2.setUrl("http://2");
        serverInstance2.setServerInstanceId(serverInstanceId2);

        serverTemplate.addServerInstance(serverInstance2);

        when(specManagementService.getServerTemplate(serverTemplateId)).thenReturn(serverTemplate);

        kieServerIntegration.onServerInstanceConnected(new ServerInstanceConnected(serverInstance2));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId));
        assertEquals(2,
                     kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).get(SERVER_TEMPLATE_KEY));
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).get(containerSpec.getId()));
        assertEquals(1,
                     kieServerIntegration.getServerInstancesById().size());
        assertNotNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId2));

        serverTemplate.addServerInstance(serverInstance1);

        kieServerIntegration.onServerInstanceConnected(new ServerInstanceConnected(serverInstance1));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId));
        assertEquals(2,
                     kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).get(SERVER_TEMPLATE_KEY));
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId).get(containerSpec.getId()));
        assertEquals(2,
                     kieServerIntegration.getServerInstancesById().size());
        assertNotNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId1));

        kieServerIntegration.onServerInstanceDisconnected(new ServerInstanceDisconnected(serverInstanceId1));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertServerInstanceFailedEndpoint(kieServerIntegration,
                                           serverInstance1);
        assertContainerFailedEndpoint(kieServerIntegration,
                                      serverInstance1,
                                      containerSpec.getId());

        assertEquals(1,
                     kieServerIntegration.getServerInstancesById().size());
        assertNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId1));
        assertNotNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId2));

        kieServerIntegration.onServerInstanceDisconnected(new ServerInstanceDisconnected(serverInstanceId2));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertServerInstanceFailedEndpoint(kieServerIntegration,
                                           serverInstance2);
        assertContainerFailedEndpoint(kieServerIntegration,
                                      serverInstance2,
                                      containerSpec.getId());

        assertEquals(0,
                     kieServerIntegration.getServerInstancesById().size());
        assertNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId1));
        assertNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId2));

        kieServerIntegration.onServerTemplateDeleted(new ServerTemplateDeleted(serverTemplateId));
        assertEquals(0,
                     kieServerIntegration.getServerTemplatesClients().size());
    }
    
    @Test
    public void testBroadcastToKieServers() {
        final String serverTemplateId = "kie-server-test";
        final String serverInstanceId1 = serverTemplateId + "@1";
        final String serverInstanceId2 = serverTemplateId + "@2";
        
        final ServerInstance serverInstance1 = new ServerInstance();
        serverInstance1.setServerTemplateId(serverTemplateId);
        serverInstance1.setUrl("http://1");

        serverInstance1.setServerInstanceId(serverInstanceId1);
        final ServerTemplate serverTemplate = new ServerTemplate(serverTemplateId,
                                                                 serverTemplateId);
        serverTemplate.addServerInstance(serverInstance1);

        final ServerInstance serverInstance2 = new ServerInstance();
        serverInstance2.setServerTemplateId(serverTemplateId);
        serverInstance2.setUrl("http://2");
        serverInstance2.setServerInstanceId(serverInstanceId2);

        serverTemplate.addServerInstance(serverInstance2);

        KieServicesClientProvider provider = Mockito.mock(KieServicesClientProvider.class);
        List<KieServicesClientProvider> providers = new ArrayList<>();
        providers.add(provider);
        
        kieServerIntegration.setKieServicesClientProviders(providers);
        
        when(provider.supports(anyString())).thenReturn(true);
        when(provider.get(anyString())).thenReturn(Mockito.mock(KieServicesClient.class));
        
        when(specManagementService.getServerTemplate(serverTemplateId)).thenReturn(serverTemplate);
        Function<KieServicesClient, Object> operation = Mockito.mock(Function.class);
        kieServerIntegration.broadcastToKieServers(serverTemplateId, operation);
                
        verify(operation, times(2)).apply(any());
    }
}
