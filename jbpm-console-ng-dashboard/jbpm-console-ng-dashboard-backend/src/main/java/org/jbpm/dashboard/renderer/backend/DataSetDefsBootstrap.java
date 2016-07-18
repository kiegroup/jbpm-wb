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
package org.jbpm.dashboard.renderer.backend;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.bd.integration.KieServerDataSetProvider;
import org.kie.server.api.KieServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.dashboard.renderer.model.DashboardData.*;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetDefsBootstrap.class);
    private static final String JBPM_DATA_SOURCE = "${"+ KieServerConstants.CFG_PERSISTANCE_DS + "}";

    @Inject
    DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        DataSetDef processMonitoringDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(DATASET_PROCESS_INSTANCES)
                .name("FILTERED_PROCESS-Processes monitoring")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                        "log.processInstanceId, " +
                        "log.processId, " +
                        "log.start_date, " +
                        "log.end_date, " +
                        "log.status, " +
                        "log.duration, " +
                        "log.user_identity, " +
                        "log.processVersion, " +
                        "log.processName, " +
                        "log.externalId " +
                        "from " +
                        "ProcessInstanceLog log", false)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .label(COLUMN_PROCESS_ID)
                .date(COLUMN_PROCESS_START_DATE)
                .date(COLUMN_PROCESS_END_DATE)
                .number(COLUMN_PROCESS_STATUS)
                .number(COLUMN_PROCESS_DURATION)
                .label(COLUMN_PROCESS_USER_ID)
                .label(COLUMN_PROCESS_VERSION)
                .label(COLUMN_PROCESS_NAME)
                .label(COLUMN_PROCESS_EXTERNAL_ID)
                .buildDef();

        DataSetDef taskMonitoringDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(DATASET_HUMAN_TASKS)
                .name("FILTERED_PROCESS-Tasks monitoring")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                                "p.processName, " +
                                "p.externalId, " +
                                "t.taskId, " +
                                "t.taskName, " +
                                "t.status, " +
                                "t.createdDate, " +
                                "t.startDate, " +
                                "t.endDate, " +
                                "t.processInstanceId, " +
                                "t.userId, " +
                                "t.duration " +
                                "from ProcessInstanceLog p " +
                                "inner join BAMTaskSummary t on (t.processInstanceId = p.processInstanceId) " +
                                "inner join (select min(pk) as pk from BAMTaskSummary group by taskId) d on t.pk = d.pk",
                        true)
                .label(COLUMN_PROCESS_NAME)
                .label(COLUMN_PROCESS_EXTERNAL_ID)
                .label(COLUMN_TASK_ID)
                .label(COLUMN_TASK_NAME)
                .label(COLUMN_TASK_STATUS)
                .date(COLUMN_TASK_CREATED_DATE)
                .date(COLUMN_TASK_START_DATE)
                .date(COLUMN_TASK_END_DATE)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .label(COLUMN_TASK_OWNER_ID)
                .number(COLUMN_TASK_DURATION)
                .buildDef();

        // Hide all these internal data set from end user view
        processMonitoringDef.setPublic(false);
        processMonitoringDef.setProvider(KieServerDataSetProvider.TYPE);
        taskMonitoringDef.setPublic(false);
        taskMonitoringDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(processMonitoringDef);
        dataSetDefRegistry.registerDataSetDef(taskMonitoringDef);
        LOGGER.info("Process dashboard datasets registered");
    }

}