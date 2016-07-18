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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jbpm.console.ng.ga.events.KieServerDataSetRegistered;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.SimpleAsyncExecutorService;

@ApplicationScoped
public class KieServerDataSetManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerDataSetManager.class);

    @Inject
    private DataSetDefRegistry dataSetDefRegistry;

    @Inject
    private KieServerIntegration kieServerIntegration;

    @Inject
    private Event<KieServerDataSetRegistered> event;

    public void registerInKieServer(@Observes final ServerInstanceConnected serverInstanceConnected) {
        final ServerInstance serverInstance = serverInstanceConnected.getServerInstance();
        final String serverInstanceId = serverInstance.getServerInstanceId();
        final String serverTemplateId = serverInstance.getServerTemplateId();
        LOGGER.debug("Server instance '{}' connected, registering data sets", serverInstanceId);

        final List<DataSetDef> dataSetDefs = dataSetDefRegistry.getDataSetDefs(false);

        LOGGER.debug("Found {} data sets to register", dataSetDefs.size());

        if( dataSetDefs.size() == 0 ){
            return;
        }

        SimpleAsyncExecutorService.getDefaultInstance().execute(() -> {
            try {
                long waitLimit = 5 * 60 * 1000;   // default 5 min
                long elapsed = 0;

                LOGGER.info("Registering data set definitions on connected server instance '{}'", serverInstanceId);
                final QueryServicesClient queryClient = kieServerIntegration.getAdminServerClient(serverTemplateId).getServicesClient(QueryServicesClient.class);

                final Set<QueryDefinition> queryDefinitions = dataSetDefs.stream()
                                        .filter(dataSetDef -> dataSetDef.getProvider().getName().equals("REMOTE"))
                                        .map(
                                                dataSetDef ->
                                                        QueryDefinition.builder()
                                                                .name(dataSetDef.getUUID())
                                                                .expression(((SQLDataSetDef) dataSetDef).getDbSQL())
                                                                .source(((SQLDataSetDef) dataSetDef).getDataSource())
                                                                .target(dataSetDef.getName().contains("-") ? dataSetDef.getName().substring(0, dataSetDef.getName().indexOf("-")) : "CUSTOM")
                                                                .build()
                                        ).collect(Collectors.toSet());

                while (elapsed < waitLimit) {
                    try {
                        queryDefinitions.forEach(definition -> {
                            queryClient.replaceQuery(definition);
                            LOGGER.info("Query definition {} (type {}) successfully registered on kie server '{}'", definition.getName(), definition.getTarget(), serverInstanceId);
                        });

                        event.fire(new KieServerDataSetRegistered(serverInstanceId, serverTemplateId));
                        return;
                    } catch (KieServicesException e) {
                        // unable to register, might still be booting
                        Thread.sleep(500);
                        elapsed += 500;
                        LOGGER.debug("Cannot reach KIE Server, elapsed time while waiting '{}', max time '{}'", elapsed, waitLimit);
                    }
                }
                LOGGER.warn("Timeout while trying to register query definition on '{}'", serverInstanceId);
            } catch (Exception e) {
                LOGGER.warn("Unable to register query definition on '{}' due to {}", serverInstanceId, e.getMessage(), e);
            }
        });
    }

}
