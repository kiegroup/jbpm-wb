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

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.jbpm.workbench.ks.events.KieServerDataSetRegistered;
import org.jbpm.workbench.ks.integration.event.ServerInstanceRegistered;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.common.rest.KieServerHttpRequestException;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.concurrent.Managed;

@ApplicationScoped
public class KieServerDataSetManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerDataSetManager.class);

    private ExecutorService executorService;

    private DataSetDefRegistry dataSetDefRegistry;

    private KieServerIntegration kieServerIntegration;

    private Event<KieServerDataSetRegistered> event;

    @Inject
    public KieServerDataSetManager(DataSetDefRegistry dataSetDefRegistry,
                                   KieServerIntegration kieServerIntegration,
                                   Event<KieServerDataSetRegistered> event,
                                   @Managed ExecutorService executorService) {
        this.dataSetDefRegistry = dataSetDefRegistry;
        this.kieServerIntegration = kieServerIntegration;
        this.event = event;
        this.executorService = executorService;
    }

    public void registerInKieServer(@Observes final ServerInstanceRegistered serverInstanceRegistered) {
        final ServerInstance serverInstance = serverInstanceRegistered.getServerInstance();
        final String serverInstanceId = serverInstance.getServerInstanceId();
        final String serverTemplateId = serverInstance.getServerTemplateId();
        LOGGER.info("Server instance '{}' connected, registering data sets",
                    serverInstanceId);

        final List<DataSetDef> dataSetDefs = dataSetDefRegistry.getDataSetDefs(false);

        LOGGER.debug("Found {} data sets to register",
                     dataSetDefs.size());

        if (dataSetDefs.isEmpty()) {
            return;
        }

        this.executorService.execute(() -> {
            try {
                LOGGER.debug("Registering data set definitions on connected server instance '{}'",
                             serverInstanceId);

                final Set<QueryDefinition> queryDefinitions = dataSetDefs.stream()
                        .filter(dataSetDef -> dataSetDef.getProvider().getName().equals("REMOTE"))
                        .map(
                                dataSetDef ->
                                        QueryDefinition.builder()
                                                .name(dataSetDef.getUUID())
                                                .expression(((SQLDataSetDef) dataSetDef).getDbSQL())
                                                .source(((SQLDataSetDef) dataSetDef).getDataSource())
                                                .target(extractQueryTarget(dataSetDef))
                                                .build()
                        ).collect(Collectors.toSet());

                registerQueriesWithRetry(serverTemplateId,
                                         serverInstanceId,
                                         queryDefinitions);
            } catch (Exception e) {
                LOGGER.warn("Unable to register query definition on '{}' due to {}",
                            serverInstanceId,
                            e.getMessage(),
                            e);
            }
        });
    }
    
    protected String extractQueryTarget(DataSetDef dataSetDef) {
        if (dataSetDef instanceof RemoteDataSetDef && ((RemoteDataSetDef) dataSetDef).getQueryTarget() != null) {
            
            return ((RemoteDataSetDef) dataSetDef).getQueryTarget();
        }
        
        return dataSetDef.getName().contains("-") ? dataSetDef.getName().substring(0,
                                                                                   dataSetDef.getName().indexOf("-")) : "CUSTOM";
    }

    protected void registerQueriesWithRetry(String serverTemplateId,
                                            String serverInstanceId,
                                            Set<QueryDefinition> queryDefinitions) throws Exception {
        long waitLimit = 5 * 60 * 1000;   // default 5 min
        long elapsed = 0;
        try {
            QueryServicesClient queryClient = kieServerIntegration.getAdminServerClient(serverTemplateId, serverInstanceId).getServicesClient(QueryServicesClient.class);

            while (elapsed < waitLimit) {
                try {
                    Iterator<QueryDefinition> definitionIt = queryDefinitions.iterator();

                    while (definitionIt.hasNext()) {
                        QueryDefinition definition = definitionIt.next();
                        queryClient.replaceQuery(definition);
                        LOGGER.info("Query definition {} (type {}) successfully registered on kie server '{}'",
                                    definition.getName(),
                                    definition.getTarget(),
                                    serverInstanceId);
                        // remove successfully stored definition to avoid duplicated reads in case of intermediate error
                        definitionIt.remove();
                    }

                    event.fire(new KieServerDataSetRegistered(serverInstanceId,
                                                              serverTemplateId));
                    return;
                } catch (KieServicesException | KieServerHttpRequestException e) {
                    // unable to register, might still be booting
                    Thread.sleep(500);
                    elapsed += 500;
                    // get admin client with forced check of endpoints as they might have been banned (marked as failed)
                    queryClient = kieServerIntegration.getAdminServerClientCheckEndpoints(serverTemplateId).getServicesClient(QueryServicesClient.class);
                    LOGGER.debug("Cannot reach KIE Server, elapsed time while waiting '{}', max time '{}' error {}",
                                 elapsed,
                                 waitLimit,
                                 e.getMessage());
                }
            }

            LOGGER.warn("Timeout while trying to register query definitions on '{}'",
                        serverInstanceId);
        } catch (KieServicesException ex) {
            // in case getting queryClient fails due to missing capability to handle advanced queries
            LOGGER.info("Not possible to register queries on server {} most likely due to BPM capability missing (details {})",
                        serverInstanceId,
                        ex.getMessage());
        }
    }
    
    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        checkNotNull("event",
                     event);
        DataSetDef def = event.getDataSetDef();
        replaceQueryInKieServers(def);
    }

    void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        checkNotNull("event",
                     event);
        DataSetDef def = event.getNewDataSetDef();
        replaceQueryInKieServers(def);        
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        checkNotNull("event",
                     event);

        DataSetDef def = event.getDataSetDef();
        if (def instanceof RemoteDataSetDef) {
            kieServerIntegration.broadcastToKieServers(((RemoteDataSetDef) def).getServerTemplateId(), (KieServicesClient client) -> {
                QueryServicesClient instanceQueryClient = client.getServicesClient(QueryServicesClient.class);
                instanceQueryClient.unregisterQuery(def.getUUID());
                return null;
            });
            
            LOGGER.info("Data set definition {} ({}) deletion event processed", def.getUUID(), def.getName());
        }
        
    }    

    protected void replaceQueryInKieServers(DataSetDef def) {
        
        if (def instanceof RemoteDataSetDef && ((RemoteDataSetDef) def).getServerTemplateId() != null) {
            QueryDefinition queryDefinition = QueryDefinition.builder()
                    .name(def.getUUID())
                    .source(((RemoteDataSetDef) def).getDataSource())
                    .target(((RemoteDataSetDef) def).getQueryTarget())
                    .expression(((RemoteDataSetDef) def).getDbSQL())
                    .build();
            
            kieServerIntegration.broadcastToKieServers(((RemoteDataSetDef) def).getServerTemplateId(), (KieServicesClient client) -> {
                QueryServicesClient instanceQueryClient = client.getServicesClient(QueryServicesClient.class);
                QueryDefinition registered = instanceQueryClient.replaceQuery(queryDefinition);
                return registered;
            });
            LOGGER.info("Data set definition {} ({}) modification event processed", def.getUUID(), def.getName());
        }
    }
}
