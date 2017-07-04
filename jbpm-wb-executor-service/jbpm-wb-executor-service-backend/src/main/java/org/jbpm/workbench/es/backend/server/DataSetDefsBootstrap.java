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
package org.jbpm.workbench.es.backend.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.workbench.ks.integration.KieServerDataSetProvider;
import org.kie.server.api.KieServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;

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
                .dbSQL("select "
                            + "ri.id, "
                            + "ri.timestamp, "
                            + "ri.status, "
                            + "ri.commandName, "
                            + "ri.message, "
                            + "ri.businessKey, "
                            + "ri.retries, "
                            + "ri.executions, "
                            + "pil.processName, "
                            + "pil.processInstanceId, "
                            + "pil.processInstanceDescription "
                        + "from "
                            + "RequestInfo ri "
                        + "left join "
                            + "ProcessInstanceLog pil "
                        + "on "
                            + "pil.processInstanceId=ri.processInstanceId", false)
                .number(COLUMN_ID)
                .date(COLUMN_TIMESTAMP)
                .label(COLUMN_STATUS)
                .label(COLUMN_COMMANDNAME)
                .label(COLUMN_MESSAGE)
                .label(COLUMN_BUSINESSKEY)
                .number(COLUMN_RETRIES)
                .number(COLUMN_EXECUTIONS)
                .label(COLUMN_PROCESS_NAME)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .label(COLUMN_PROCESS_INSTANCE_DESCRIPTION)
                .buildDef();

        // Hide all these internal data set from end user view
        requestListDef.setPublic(false);
        requestListDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(requestListDef);
        logger.info("Executor service datasets registered");

        DataSetDef executionErrorListDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(EXECUTION_ERROR_LIST_DATASET)
                .name("Error Management")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select "
                               + "eri.ERROR_ACK, "
                               + "eri.ERROR_ACK_BY, "
                               + "eri.ERROR_ACK_AT, "
                               + "eri.ACTIVITY_ID, "
                               + "eri.ACTIVITY_NAME, "
                               + "eri.DEPLOYMENT_ID, "
                               + "eri.ERROR_DATE, "
                               + "eri.ERROR_ID, "
                               + "eri.ERROR_MSG, "
                               + "eri.JOB_ID, "
                               + "eri.PROCESS_ID, "
                               + "eri.PROCESS_INST_ID, "
                               + "eri.ERROR_TYPE "
                               + "from "
                               + "ExecutionErrorInfo eri ",
                       false)
                .number(COLUMN_ERROR_ACK)
                .text(COLUMN_ERROR_ACK_BY)
                .date(COLUMN_ERROR_ACK_AT)
                .number(COLUMN_ACTIVITY_ID)
                .label(COLUMN_ACTIVITY_NAME)
                .label(COLUMN_DEPLOYMENT_ID)
                .date(COLUMN_ERROR_DATE)
                .label(COLUMN_ERROR_ID)
                .label(COLUMN_ERROR_MSG)
                .number(COLUMN_JOB_ID)
                .label(COLUMN_PROCESS_ID)
                .number(COLUMN_PROCESS_INST_ID)
                .label(COLUMN_ERROR_TYPE)
                .buildDef();

        // Hide all these internal data set from end user view
        executionErrorListDef.setPublic(false);
        executionErrorListDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(executionErrorListDef);
        logger.info("Error Management dataset registered");
    }

}