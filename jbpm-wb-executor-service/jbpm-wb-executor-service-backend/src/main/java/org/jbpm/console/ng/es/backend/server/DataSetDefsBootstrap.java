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
package org.jbpm.console.ng.es.backend.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.ks.integration.KieServerDataSetProvider;
import org.kie.server.api.KieServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.console.ng.es.model.RequestDataSetConstants.*;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(DataSetDefsBootstrap.class);
    private static final String JBPM_DATA_SOURCE = "${"+ KieServerConstants.CFG_PERSISTANCE_DS + "}";

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        DataSetDef requestListDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(REQUEST_LIST_DATASET)
                .name("Request List")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select id, timestamp, status, commandName, message, businessKey, retries, executions from RequestInfo", false)
                .number(COLUMN_ID)
                .date(COLUMN_TIMESTAMP)
                .label(COLUMN_STATUS)
                .label(COLUMN_COMMANDNAME)
                .label(COLUMN_MESSAGE)
                .label(COLUMN_BUSINESSKEY)
                .number(COLUMN_RETRIES)
                .number(COLUMN_EXECUTIONS)
                .buildDef();

        // Hide all these internal data set from end user view
        requestListDef.setPublic(false);
        requestListDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(requestListDef);
        logger.info("Executor service datasets registered");
    }

}