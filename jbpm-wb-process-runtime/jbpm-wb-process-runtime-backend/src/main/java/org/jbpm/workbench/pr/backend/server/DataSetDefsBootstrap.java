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
package org.jbpm.workbench.pr.backend.server;

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

import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetDefsBootstrap.class);
    private static final String JBPM_DATA_SOURCE = "${"+ KieServerConstants.CFG_PERSISTANCE_DS + "}";

    @Inject
    DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        DataSetDef processInstancesDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_DATASET)
                .name("FILTERED_PROCESS-Process Instances")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                            "log.processInstanceId, " +
                            "log.processId, " +
                            "log.start_date, " +
                            "log.end_date, " +
                            "log.status, " +
                            "log.parentProcessInstanceId, " +
                            "log.outcome, " +
                            "log.duration, " +
                            "log.user_identity, " +
                            "log.processVersion, " +
                            "log.processName, " +
                            "log.correlationKey, " +
                            "log.externalId, " +
                            "log.processInstanceDescription, " +
                            "info.lastModificationDate " +
                        "from " +
                            "ProcessInstanceLog log " +
                        "left join " +
                            "ProcessInstanceInfo info " +
                        "on " +
                            "info.InstanceId=log.processInstanceId"
                 , false)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .label(COLUMN_PROCESS_ID)
                .date(COLUMN_START)
                .date(COLUMN_END)
                .number(COLUMN_STATUS)
                .number(COLUMN_PARENT_PROCESS_INSTANCE_ID)
                .label(COLUMN_OUTCOME)
                .number(COLUMN_DURATION)
                .label(COLUMN_IDENTITY)
                .label(COLUMN_PROCESS_VERSION)
                .label(COLUMN_PROCESS_NAME)
                .label(COLUMN_CORRELATION_KEY)
                .label(COLUMN_EXTERNAL_ID)
                .label(COLUMN_PROCESS_INSTANCE_DESCRIPTION)
                .date(COLUMN_LAST_MODIFICATION_DATE)
                .buildDef();

        DataSetDef processWithVariablesDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_WITH_VARIABLES_DATASET)
                .name("Domain Specific Process Instances")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                            "vil.processInstanceId, " +
                            "vil.processId, " +
                            "vil.id, " +
                            "vil.variableId, " +
                            "vil.value " +
                        "from VariableInstanceLog vil " +
                        "where " +
                            "vil.id = " +
                                "(select MAX(v.id) " +
                                "from VariableInstanceLog v " +
                                "where " +
                                "v.variableId = vil.variableId and " +
                                "v.processInstanceId = vil.processInstanceId)" , false )
                .number(PROCESS_INSTANCE_ID)
                .label(PROCESS_NAME)
                .number(VARIABLE_ID)
                .label(VARIABLE_NAME)
                .label(VARIABLE_VALUE)
                .buildDef();

        // Hide all these internal data set from end user view
        processInstancesDef.setPublic(false);
        processInstancesDef.setProvider(KieServerDataSetProvider.TYPE);
        processWithVariablesDef.setPublic(false);
        processWithVariablesDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(processInstancesDef);
        dataSetDefRegistry.registerDataSetDef(processWithVariablesDef);
        LOGGER.info("Process instance datasets registered");
    }

}
