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

package org.jbpm.workbench.ks.integration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.workbench.ks.integration.event.ServerInstanceRegistered;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.server.api.KieServerConstants;
import org.kie.server.client.CredentialsProvider;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.balancer.LoadBalancer;
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
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.server.controller.impl.client.KieServicesClientProvider;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

import static java.util.Collections.emptyMap;
import static org.jbpm.workbench.ks.utils.KieServerUtils.createKieServicesClient;
import static org.jbpm.workbench.ks.utils.KieServerUtils.getAdminCredentialsProvider;
import static org.jbpm.workbench.ks.utils.KieServerUtils.getCredentialsProvider;

@Startup
@ApplicationScoped
public class KieServerIntegration {

    private static final Logger logger = LoggerFactory.getLogger(KieServerIntegration.class);

    protected static final String SERVER_TEMPLATE_KEY = "_SERVER_TEMPLATE_MAIN_CLIENT_";

    private KieServices kieServices;

    private ConcurrentMap<String, Map<String, KieServicesClient>> serverTemplatesClients = new ConcurrentHashMap<String, Map<String, KieServicesClient>>();
    private ConcurrentMap<String, KieServicesClient> adminClients = new ConcurrentHashMap<String, KieServicesClient>();
    private ConcurrentMap<String, ServerInstanceKey> serverInstancesById = new ConcurrentHashMap<String, ServerInstanceKey>();

    private List<KieServicesClientProvider> clientProviders = new ArrayList<>();
    private List<KieServicesClientProvider> allClientProviders = new ArrayList<>();
    
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    private SpecManagementService specManagementService;

    @Inject
    private Event<ServerInstanceRegistered> serverInstanceRegisteredEvent;

    @PostConstruct
    public void createAvailableClients() {
        executorService.submit(() -> {
            ServiceLoader<KieServicesClientProvider> loader = ServiceLoader.load(KieServicesClientProvider.class);

            loader.forEach(provider -> {
                // skip default http/rest based client providers and use admin client created here
                if (!provider.supports("http")) {
                    clientProviders.add(provider);
                }
                allClientProviders.add(provider);
            });

            clientProviders.sort((KieServicesClientProvider one, KieServicesClientProvider two) -> one.getPriority().compareTo(two.getPriority()));

            kieServices = KieServices.Factory.get();

            ServerTemplateList serverTemplates = specManagementService.listServerTemplates();
            logger.debug("Found {} server templates, creating clients for them...", serverTemplates.getServerTemplates().length);

            for (ServerTemplate serverTemplate : serverTemplates.getServerTemplates()) {
                buildClientsForServer(serverTemplate);
            }    
        });
    }
    
    @PreDestroy
    public void stop(){
        executorService.shutdownNow();
    }

    protected void setKieServices(final KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public KieServicesClient getServerClient(String serverTemplateId) {
        return serverTemplatesClients.getOrDefault(serverTemplateId,
                                                   emptyMap()).get(SERVER_TEMPLATE_KEY);
    }

    public KieServicesClient getServerClient(String serverTemplateId,
                                             String containerId) {
        KieServicesClient client = serverTemplatesClients.getOrDefault(serverTemplateId,
                                                                       emptyMap()).get(containerId);

         if (client == null) {
             logger.warn("Container {} not found in server template {}, returning global kie server client",
                          containerId,
                          serverTemplateId);
             client = getServerClient(serverTemplateId);
         }
         return client;
    }

    public KieServicesClient getAdminServerClient(String serverTemplateId,
                                                  String serverInstanceId) {
        try {
            ServerInstanceKey instance = specManagementService.getServerTemplate(serverTemplateId).getServerInstanceKeys()
                    .stream()
                    .filter(si -> si.getServerInstanceId().equals(serverInstanceId))
                    .findFirst()
                    .get();
            String url = instance.getUrl();
            KieServicesClient client = clientProviders
                    .stream()
                    .filter(provider -> provider.supports(url))
                    .findFirst()
                    .get()
                    .get(url);
            logger.debug("Using client {}",
                         client);
            return client;
        } catch (Exception e) {
            return adminClients.get(serverTemplateId);
        }
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
        serverTemplate.getServerInstanceKeys().forEach(serverInstanceKey -> serverInstancesById.put(serverInstanceKey.getServerInstanceId(),
                                                                                                    serverInstanceKey));
    }

    protected void removeServerInstancesFromIndex(String serverTemplateId) {
        Iterator<Map.Entry<String, ServerInstanceKey>> iterator = serverInstancesById.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ServerInstanceKey> entry = iterator.next();
            if (entry.getValue().getServerTemplateId().equals(serverTemplateId)) {
                iterator.remove();
            }
        }
    }

    public void onServerTemplateUpdated(@Observes ServerTemplateUpdated serverTemplateUpdated) {
        buildClientsForServer(serverTemplateUpdated.getServerTemplate());
    }

    public void onServerTemplateDeleted(@Observes ServerTemplateDeleted serverTemplateDeleted) {
        // remove all clients for this server template and its containers
        final Map<String, KieServicesClient> clients = serverTemplatesClients.remove(serverTemplateDeleted.getServerTemplateId());
        if (clients != null) {
            logger.debug("KieServerClient removed and closed for server template {}",
                         serverTemplateDeleted.getServerTemplateId());
        }

        // remove admin client
        adminClients.remove(serverTemplateDeleted.getServerTemplateId());

        removeServerInstancesFromIndex(serverTemplateDeleted.getServerTemplateId());
    }

    public void onServerInstanceDisconnected(@Observes ServerInstanceDisconnected serverInstanceDisconnected) {
        ServerInstanceKey serverInstanceKey = serverInstancesById.get(serverInstanceDisconnected.getServerInstanceId());

        if (serverInstanceKey != null) {
            serverTemplatesClients.computeIfPresent(serverInstanceKey.getServerTemplateId(),
                                                    (serverTemplateId, clients) -> {
                                                        clients.forEach((key, client) -> {
                                                            LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) client).getLoadBalancer();
                                                            loadBalancer.markAsFailed(serverInstanceKey.getUrl());

                                                            logger.debug("Server instance '{}' for server template {} removed from client thus won't be used for operations",
                                                                         serverInstanceKey.getUrl(),
                                                                         serverInstanceKey.getServerTemplateId());

                                                            logger.debug("KieServerClient load balancer updated for server template {}",
                                                                         serverTemplateId.equals(SERVER_TEMPLATE_KEY) ? serverInstanceKey.getServerTemplateId() : serverTemplateId);
                                                        });
                                                        return clients;
                                                    });

            serverInstancesById.remove(serverInstanceKey.getServerInstanceId());

            // update admin client
            KieServicesClient adminClient = adminClients.get(serverInstanceKey.getServerTemplateId());
            if (adminClient != null) {
                LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) adminClient).getLoadBalancer();
                loadBalancer.markAsFailed(serverInstanceKey.getUrl());

                logger.debug("Server instance {} for server template {} removed from client thus won't be used for operations",
                             serverInstanceKey.getUrl(),
                             serverInstanceKey.getServerTemplateId());
            }
        }
    }

    public void onServerInstanceConnected(@Observes ServerInstanceConnected serverInstanceConnected) {
        ServerInstance serverInstance = serverInstanceConnected.getServerInstance();

        serverTemplatesClients.computeIfPresent(serverInstance.getServerTemplateId(),
                                                (serverTemplateId, clients) -> {
                                                    clients.forEach((key, client) -> {
                                                        // update regular clients
                                                        updateOrBuildClient(client,
                                                                            serverInstance);

                                                        logger.debug("KieServerClient load balancer updated for server template {}",
                                                                     serverTemplateId.equals(SERVER_TEMPLATE_KEY) ? serverInstance.getServerTemplateId() : serverTemplateId);
                                                    });
                                                    return clients;
                                                });

        serverInstancesById.put(serverInstance.getServerInstanceId(),
                                serverInstance);

        KieServicesClient adminClient = adminClients.get(serverInstance.getServerTemplateId());
        // update admin clients
        updateOrBuildClient(adminClient,
                            serverInstance);
        // once all steps are completed successfully notify other parts interested so the serverClient can actually be used
        serverInstanceRegisteredEvent.fire(new ServerInstanceRegistered(serverInstanceConnected.getServerInstance()));
    }

    public List<Object> broadcastToKieServers(String serverTemplateId,
                                              Function<KieServicesClient, Object> operation) {
        List<Object> results = new ArrayList<>();

        ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverTemplateId);

        if (serverTemplate.getServerInstanceKeys() == null || serverTemplate.getServerInstanceKeys().isEmpty()) {

            return results;
        }

        for (ServerInstanceKey instanceUrl : serverTemplate.getServerInstanceKeys()) {

            try {
                KieServicesClient client = getClient(instanceUrl.getUrl());

                Object result = operation.apply(client);
                results.add(result);
                logger.debug("KIE Server at {} returned result {} for broadcast operation {}", instanceUrl, result, operation);
            } catch (Exception e) {
                logger.debug("Unable to send breadcast to {} due to {}", instanceUrl, e.getMessage(), e);
            }
        }

        return results;
    }

    protected KieServicesClient getClient(String url) {
        KieServicesClient client = allClientProviders.stream().filter(provider -> provider.supports(url)).findFirst().get().get(url);
        logger.debug("Using client {}", client);
        return client;
    }

    protected void updateOrBuildClient(KieServicesClient client,
                                       ServerInstance serverInstance) {
        if (client != null) {
            LoadBalancer loadBalancer = ((AbstractKieServicesClientImpl) client).getLoadBalancer();
            loadBalancer.activate(serverInstance.getUrl());

            logger.debug("Server instance {} for server template {} activated on client thus will be used for operations",
                         serverInstance.getUrl(),
                         serverInstance.getServerTemplateId());
        } else {
            logger.debug("No kie server client yet created, attempting to create one for server template {}",
                         serverInstance.getServerTemplateId());

            ServerTemplate serverTemplate = specManagementService.getServerTemplate(serverInstance.getServerTemplateId());

            buildClientsForServer(serverTemplate);
        }
    }

    protected void buildClientsForServer(ServerTemplate serverTemplate) {
        KieServicesClient kieServicesClient = createClientForTemplate(serverTemplate,
                                                                      null,
                                                                      getCredentialsProvider());
        if (kieServicesClient != null) {
            serverTemplatesClients.computeIfAbsent(serverTemplate.getId(), (k) -> new ConcurrentHashMap<>());
            serverTemplatesClients.get(serverTemplate.getId()).put(SERVER_TEMPLATE_KEY, kieServicesClient);
        }

        if (serverTemplate.getContainersSpec() != null) {
            for (ContainerSpec containerSpec : serverTemplate.getContainersSpec()) {
                try {
                    if (serverTemplatesClients.get(serverTemplate.getId()).containsKey(containerSpec.getId())) {
                        logger.debug("KieServerClient for {} is already created", containerSpec.getId());
                        continue;
                    }

                    KieContainer kieContainer = kieServices.newKieContainer(containerSpec.getReleasedId());

                    KieServicesClient kieServicesClientForContainer = createClientForTemplate(serverTemplate,
                                                                                              kieContainer.getClassLoader(),
                                                                                              getCredentialsProvider());
                    if (kieServicesClient != null) {
                        serverTemplatesClients.get(serverTemplate.getId()).put(containerSpec.getId(),
                                                                               kieServicesClientForContainer);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to create kie server client for container {} due to {}",
                                containerSpec.getId(),
                                e.getMessage());
                }
            }
        }
        // lastly create admin client
        KieServicesClient adminKieServicesClient = createClientForTemplate(serverTemplate,
                                                                           null,
                                                                           getAdminCredentialsProvider());
        if (adminKieServicesClient != null) {
            adminClients.put(serverTemplate.getId(),
                             adminKieServicesClient);
        }
    }

    protected KieServicesClient createClientForTemplate(ServerTemplate serverTemplate,
                                                        ClassLoader classLoader,
                                                        CredentialsProvider credentialsProvider) {

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

            final List<String> mappedCapabilities = new ArrayList<>();
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

            final KieServicesClient kieServicesClient = createKieServicesClient(endpoints.toString(),
                                                                                classLoader,
                                                                                credentialsProvider,
                                                                                mappedCapabilities.toArray(new String[mappedCapabilities.size()]));

            logger.debug("KieServerClient created successfully for server template {}", serverTemplate.getId());

            indexServerInstances(serverTemplate);

            return kieServicesClient;
        } catch (Exception e) {
            logger.error("Unable to create kie server client for server template {} due to {}",
                         serverTemplate,
                         e.getMessage(),
                         e);
            return null;
        }
    }

    protected Map<String, Map<String, KieServicesClient>> getServerTemplatesClients() {
        return serverTemplatesClients;
    }

    protected Map<String, ServerInstanceKey> getServerInstancesById() {
        return serverInstancesById;
    }

    protected void setKieServicesClientProviders(List<KieServicesClientProvider> providers) {
        this.allClientProviders = providers;
    }

}
