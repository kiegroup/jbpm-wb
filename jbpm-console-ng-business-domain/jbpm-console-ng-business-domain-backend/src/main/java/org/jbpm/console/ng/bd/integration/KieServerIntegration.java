/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.bd.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.bd.integration.security.KeyCloakTokenCredentialsProvider;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.balancer.LoadBalancer;
import org.kie.server.client.credentials.EnteredCredentialsProvider;
import org.kie.server.client.credentials.EnteredTokenCredentialsProvider;
import org.kie.server.client.credentials.SubjectCredentialsProvider;
import org.kie.server.client.impl.AbstractKieServicesClientImpl;
import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class KieServerIntegration {

    private static final Logger logger = LoggerFactory.getLogger(KieServerIntegration.class);

    private KieServices kieServices;

    private ConcurrentMap<String, KieServicesClient> serverTemplatesClients = new ConcurrentHashMap<String, KieServicesClient>();
    private ConcurrentMap<String, KieServicesClient> adminClients = new ConcurrentHashMap<String, KieServicesClient>();

    private ConcurrentMap<String, ServerInstanceKey> serverInstancesById = new ConcurrentHashMap<String, ServerInstanceKey>();

    @Inject
    private SpecManagementService specManagementService;

    @PostConstruct
    public void createAvailableClients() {
        kieServices = KieServices.Factory.get();

        Collection<ServerTemplate> serverTemplates = specManagementService.listServerTemplates();
        logger.debug("Found {} server templates, creating clients for them...", serverTemplates.size());

        for (ServerTemplate serverTemplate : serverTemplates) {
            buildClientsForServer(serverTemplate);

        }
    }

    public KieServicesClient getServerClient(String serverTemplateId) {
        return serverTemplatesClients.get(serverTemplateId);
    }


    public KieServicesClient getServerClient(String serverTemplateId, String containerId) {
        return serverTemplatesClients.get(serverTemplateId + "|" + containerId);
    }

    public KieServicesClient getAdminServerClient(String serverTemplateId) {
        return adminClients.get(serverTemplateId);
    }

    public KieServicesClient getAdminServerClientCheckEndpoints(String serverTemplateId) {
        KieServicesClient adminClient = adminClients.get(serverTemplateId);
        if (adminClient != null) {
            LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) adminClient).getLoadBalancer();
            loadBalancer.checkFailedEndpoints();
        }
        return adminClient;
    }

    protected void indexServerInstances(ServerTemplate serverTemplate) {
        for (ServerInstanceKey serverInstanceKey : serverTemplate.getServerInstanceKeys()) {
            serverInstancesById.put(serverInstanceKey.getServerInstanceId(), serverInstanceKey);
        }
    }

    protected void removeServerInstancesFromIndex(String serverTemplateId) {
        Iterator<Map.Entry<String,ServerInstanceKey>> iterator = serverInstancesById.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,ServerInstanceKey> entry = iterator.next();
            if (entry.getValue().getServerTemplateId().equals(serverTemplateId)) {
                iterator.remove();
            }
        }
    }

    public void onServerTemplateDeleted(@Observes ServerTemplateUpdated serverTemplateUpdated) {
        buildClientsForServer(serverTemplateUpdated.getServerTemplate());
    }

    public void onServerTemplateDeleted(@Observes ServerTemplateDeleted serverTemplateDeleted) {
        // remove all clients for this server template and its containers
        Iterator<Map.Entry<String,KieServicesClient>> iterator = serverTemplatesClients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,KieServicesClient> entry = iterator.next();
            if (entry.getKey().startsWith(serverTemplateDeleted.getServerTemplateId())) {
                //KieServicesClient client = entry.getValue();
                //client.close();
                logger.debug("KieServerClient removed and closed for server template {}", entry.getKey());

                iterator.remove();
            }
        }
        // remove admin client
        adminClients.remove(serverTemplateDeleted.getServerTemplateId());

        removeServerInstancesFromIndex(serverTemplateDeleted.getServerTemplateId());
    }

    public void onServerInstanceDisconnected(@Observes ServerInstanceDisconnected serverInstanceDisconnected) {
        ServerInstanceKey serverInstanceKey = serverInstancesById.get(serverInstanceDisconnected.getServerInstanceId());

        if (serverInstanceKey != null) {
            Iterator<Map.Entry<String,KieServicesClient>> iterator = serverTemplatesClients.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String,KieServicesClient> entry = iterator.next();
                if (entry.getKey().startsWith(serverInstanceKey.getServerTemplateId())) {
                    KieServicesClient client = entry.getValue();
                    if (client != null) {
                        LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) client).getLoadBalancer();
                        loadBalancer.markAsFailed(serverInstanceKey.getUrl());

                        logger.debug("Server instance {} for server template {} removed from client thus won't be used for operations", serverInstanceKey.getUrl(), serverInstanceKey.getServerTemplateId());
                    }

                    logger.debug("KieServerClient load balancer updated for server template {}", entry.getKey());
                }
            }

            serverInstancesById.remove(serverInstanceKey.getServerInstanceId());

            // update admin client
            KieServicesClient adminClient = adminClients.get(serverInstanceKey.getServerTemplateId());
            if (adminClient != null) {
                LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) adminClient).getLoadBalancer();
                loadBalancer.markAsFailed(serverInstanceKey.getUrl());

                logger.debug("Server instance {} for server template {} removed from client thus won't be used for operations", serverInstanceKey.getUrl(), serverInstanceKey.getServerTemplateId());
            }
        }
    }

    public void onServerInstanceConnected(@Observes ServerInstanceConnected serverInstanceConnected) {

        ServerInstance serverInstance = serverInstanceConnected.getServerInstance();

        Iterator<Map.Entry<String,KieServicesClient>> iterator = serverTemplatesClients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,KieServicesClient> entry = iterator.next();
            if (entry.getKey().startsWith(serverInstance.getServerTemplateId())) {
                KieServicesClient client = entry.getValue();
                // update regular clients
                updateOrBuildClient(client, serverInstance);

                logger.debug("KieServerClient load balancer updated for server template {}", entry.getKey());
            }
        }

        KieServicesClient adminClient = adminClients.get(serverInstance.getServerTemplateId());
        // update admin clients
        updateOrBuildClient(adminClient, serverInstance);
    }

    protected void updateOrBuildClient(KieServicesClient client, ServerInstance serverInstance) {
        if (client != null) {
            LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) client).getLoadBalancer();
            loadBalancer.activate(serverInstance.getUrl());

            logger.debug("Server instance {} for server template {} activated on client thus will be used for operations", serverInstance.getUrl(), serverInstance.getServerTemplateId());
        } else {
            logger.debug("No kie server client yet created, attempting to create one for server template {}", serverInstance.getServerTemplateId());

            ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverInstance.getServerTemplateId());

            buildClientsForServer(serverTemplate);
        }
    }

    protected void buildClientsForServer(ServerTemplate serverTemplate) {
        KieServicesClient kieServicesClient = createClientForTemplate(serverTemplate, null, getCredentialsProvider());
        if (kieServicesClient != null) {
            serverTemplatesClients.put(serverTemplate.getId(), kieServicesClient);
        }

        if (serverTemplate.getContainersSpec() != null) {
            for (ContainerSpec containerSpec : serverTemplate.getContainersSpec()) {
                try {
                    String key = serverTemplate.getId()+"|" + containerSpec.getId();
                    if (serverTemplatesClients.containsKey(key)){
                        logger.debug("KieServerClient for {} is already created", key);
                        continue;
                    }

                    KieContainer kieContainer = kieServices.newKieContainer(containerSpec.getReleasedId());

                    KieServicesClient kieServicesClientForContainer = createClientForTemplate(serverTemplate, kieContainer.getClassLoader(), getCredentialsProvider());
                    if (kieServicesClient != null) {
                        serverTemplatesClients.put(key, kieServicesClientForContainer);
                    }
                } catch (Exception e) {
                    logger.warn("Failed ot create kie server client for container {} due to {}", containerSpec.getId(), e.getMessage());
                }
            }
        }
        // lastly create admin client
        KieServicesClient adminKieServicesClient = createClientForTemplate(serverTemplate, null, getAdminCredentialsProvider());
        if (adminKieServicesClient != null) {
            adminClients.put(serverTemplate.getId(), adminKieServicesClient);
        }
    }

    protected KieServicesClient createClientForTemplate(ServerTemplate serverTemplate, ClassLoader classLoader, CredentialsProvider credentialsProvider) {

        if (serverTemplate.getServerInstanceKeys() == null || serverTemplate.getServerInstanceKeys().isEmpty()) {
            return null;
        }
        try {
            StringBuilder endpoints = new StringBuilder();
            for (ServerInstanceKey serverInstanceKey : serverTemplate.getServerInstanceKeys()) {
                endpoints.append(serverInstanceKey.getUrl() + "|");
            }
            endpoints.deleteCharAt(endpoints.length() - 1);
            logger.debug("Creating client that will use following list of endpoints {}", endpoints);
            KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(endpoints.toString(), credentialsProvider);
            configuration.setTimeout(60000);

            List<String> mappedCapabilities = new ArrayList<String>();
            if (serverTemplate.getCapabilities().contains(Capability.PROCESS.name())) {
                mappedCapabilities.add(KieServerConstants.CAPABILITY_BPM);
                mappedCapabilities.add(KieServerConstants.CAPABILITY_BPM_UI);
                mappedCapabilities.add(KieServerConstants.CAPABILITY_CASE);
            }
            if (serverTemplate.getCapabilities().contains(Capability.RULE.name())) {
                mappedCapabilities.add(KieServerConstants.CAPABILITY_BRM);
            }
            if (serverTemplate.getCapabilities().contains(Capability.PLANNING.name())) {
                mappedCapabilities.add(KieServerConstants.CAPABILITY_BRP);
            }

            configuration.setCapabilities(mappedCapabilities);
            configuration.setMarshallingFormat(MarshallingFormat.XSTREAM);
            configuration.setLoadBalancer(LoadBalancer.getDefault(endpoints.toString()));

            KieServicesClient kieServicesClient = null;

            if (classLoader == null) {
                kieServicesClient = KieServicesFactory.newKieServicesClient(configuration);
            } else {
                kieServicesClient = KieServicesFactory.newKieServicesClient(configuration, classLoader);
            }
            logger.debug("KieServerClient created successfully for server template {}", serverTemplate);

            indexServerInstances(serverTemplate);

            return kieServicesClient;
        } catch (Exception e) {
            logger.error("Unable to create kie server client for server template {} due to {}", serverTemplate, e.getMessage(), e);
            return null;
        }
    }

    protected CredentialsProvider getCredentialsProvider() {
        CredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = new KeyCloakTokenCredentialsProvider();
        } catch (UnsupportedOperationException e) {
            credentialsProvider = new SubjectCredentialsProvider();
        }
        logger.debug("{} initialized for the client.", credentialsProvider.getClass().getName());
        return credentialsProvider;
    }

    protected CredentialsProvider getAdminCredentialsProvider() {
        if (System.getProperty(KieServerConstants.CFG_KIE_TOKEN) != null) {
            return new EnteredTokenCredentialsProvider(System.getProperty(KieServerConstants.CFG_KIE_TOKEN));
        } else {
            return new EnteredCredentialsProvider(System.getProperty(KieServerConstants.CFG_KIE_USER, "kieserver"), System.getProperty(KieServerConstants.CFG_KIE_PASSWORD, "kieserver1!"));
        }
    }
}
