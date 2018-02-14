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

import java.util.Arrays;
import java.util.Collection;
import javax.enterprise.event.Event;

import org.jbpm.workbench.ks.integration.event.ServerInstanceRegistered;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.balancer.LoadBalancer;
import org.kie.server.client.impl.AbstractKieServicesClientImpl;
import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.jbpm.workbench.ks.integration.KieServerIntegration.SERVER_TEMPLATE_KEY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class KieServerIntegrationServerTemplateTest {

    @Parameterized.Parameter(0)
    public String serverTemplateId1;

    @Parameterized.Parameter(1)
    public String serverTemplateId2;

    @Mock
    SpecManagementService specManagementService;

    @Mock
    KieServices kieServices;

    @Mock
    Event<ServerInstanceRegistered> serverInstanceRegisteredEvent;

    @InjectMocks
    KieServerIntegration kieServerIntegration;

    public static ContainerSpec newContainerSpec() {
        final ContainerSpec spec = new ContainerSpec();
        spec.setId("id");
        spec.setContainerName("name");
        spec.setStatus(KieContainerStatus.STARTED);
        spec.setReleasedId(new ReleaseId("groupId",
                                         "artifactId",
                                         "1.0"));
        return spec;
    }

    @Parameterized.Parameters(name = "Server Template 1: {0}, Server Template 2: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"test-kie-server", "test-kie-server2"},
                {"test-kie-server2", "test-kie-server"},
                {"test-kie-server1", "test-kie-server2"},
                {"test-kie-server2", "test-kie-server1"},
                {"test-kie-server|", "test-kie-server"},
                {"test-kie-server", "test-kie-server|"},
        });
    }

    public static void assertServerInstanceFailedEndpoint(final KieServerIntegration kieServerIntegration,
                                                          final ServerInstance serverInstance) {
        final KieServicesClient serverClient = kieServerIntegration.getServerClient(serverInstance.getServerTemplateId());
        LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) serverClient).getLoadBalancer();
        assertTrue(loadBalancer.getFailedEndpoints().contains(serverInstance.getUrl()));
    }

    public static void assertContainerFailedEndpoint(final KieServerIntegration kieServerIntegration,
                                                     final ServerInstance serverInstance,
                                                     final String containerId) {
        final KieServicesClient serverClient = kieServerIntegration.getServerClient(serverInstance.getServerTemplateId(),
                                                                                    containerId);
        LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) serverClient).getLoadBalancer();
        assertTrue(loadBalancer.getFailedEndpoints().contains(serverInstance.getUrl()));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(kieServices.newKieContainer(any())).thenReturn(mock(KieContainer.class));
    }

    @Test
    public void testSimilarKieServerName() {
        final String serverInstanceId1 = serverTemplateId1 + "@1";
        final String serverInstanceId2 = serverTemplateId2 + "@1";
        final ContainerSpec containerSpec = newContainerSpec();
        final ServerInstance serverInstance1 = new ServerInstance();
        serverInstance1.setServerTemplateId(serverTemplateId1);
        serverInstance1.setUrl("http://1");

        serverInstance1.setServerInstanceId(serverInstanceId1);
        final ServerTemplate serverTemplate1 = new ServerTemplate(serverTemplateId1,
                                                                  serverTemplateId1);
        serverTemplate1.addServerInstance(serverInstance1);

        serverTemplate1.addContainerSpec(containerSpec);

        final ServerInstance serverInstance2 = new ServerInstance();
        serverInstance2.setServerTemplateId(serverTemplateId2);
        serverInstance2.setUrl("http://2");

        serverInstance2.setServerInstanceId(serverInstanceId2);
        final ServerTemplate serverTemplate2 = new ServerTemplate(serverTemplateId2,
                                                                  serverTemplateId2);
        serverTemplate2.addServerInstance(serverInstance2);
        serverTemplate2.addContainerSpec(containerSpec);

        when(specManagementService.getServerTemplate(serverTemplateId1)).thenReturn(serverTemplate1);
        when(specManagementService.getServerTemplate(serverTemplateId2)).thenReturn(serverTemplate2);

        kieServerIntegration.onServerInstanceConnected(new ServerInstanceConnected(serverInstance2));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId2));
        assertEquals(2,
                     kieServerIntegration.getServerTemplatesClients().get(serverTemplateId2).size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId2).get(SERVER_TEMPLATE_KEY));
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId2).get(containerSpec.getId()));
        assertEquals(1,
                     kieServerIntegration.getServerInstancesById().size());
        assertNotNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId2));

        kieServerIntegration.onServerInstanceConnected(new ServerInstanceConnected(serverInstance1));

        assertEquals(2,
                     kieServerIntegration.getServerTemplatesClients().size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId1));
        assertEquals(2,
                     kieServerIntegration.getServerTemplatesClients().get(serverTemplateId1).size());
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId1).get(SERVER_TEMPLATE_KEY));
        assertNotNull(kieServerIntegration.getServerTemplatesClients().get(serverTemplateId1).get(containerSpec.getId()));
        assertEquals(2,
                     kieServerIntegration.getServerInstancesById().size());
        assertNotNull(kieServerIntegration.getServerInstancesById().get(serverInstanceId1));

        kieServerIntegration.onServerInstanceDisconnected(new ServerInstanceDisconnected(serverInstanceId1));

        assertEquals(2,
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

        assertEquals(2,
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

        kieServerIntegration.onServerTemplateDeleted(new ServerTemplateDeleted(serverTemplateId1));

        assertEquals(1,
                     kieServerIntegration.getServerTemplatesClients().size());

        kieServerIntegration.onServerTemplateDeleted(new ServerTemplateDeleted(serverTemplateId2));
        assertEquals(0,
                     kieServerIntegration.getServerTemplatesClients().size());
    }
}
