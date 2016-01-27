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
import org.jbpm.persistence.settings.JpaSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(DataSetDefsBootstrap.class);

    public static final String PROCESS_INSTANCE_TABLE = "ProcessInstanceLog";
    public static final String TASKS_MONITORING_DATASET = "tasksMonitoring";
    public static final String PROCESSES_MONITORING_DATASET = "processesMonitoring";

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        String jbpmDataSource = JpaSettings.get().getDataSourceJndiName();

        DataSetDef processMonitoringDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(PROCESSES_MONITORING_DATASET)
                .name("Processes monitoring")
                .dataSource(jbpmDataSource)
                .dbTable(PROCESS_INSTANCE_TABLE, true)
                .buildDef();

        DataSetDef taskMonitoringDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(TASKS_MONITORING_DATASET)
                .name("Tasks monitoring")
                .dataSource(jbpmDataSource)
                .dbSQL("select p.processName, t.* " +
                        "from ProcessInstanceLog p " +
                        "inner join BAMTaskSummary t on (t.processInstanceId = p.processInstanceId) " +
                        "inner join (select min(pk) pk from BAMTaskSummary group by taskId) d on t.pk=d.pk",
                        true)
                .buildDef();


        // Hide all these internal data set from end user view
        processMonitoringDef.setPublic(false);
        taskMonitoringDef.setPublic(false);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(processMonitoringDef);
        dataSetDefRegistry.registerDataSetDef(taskMonitoringDef);
        logger.info("Process dashboard datasets registered");
    }
}
