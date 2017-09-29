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
package org.jbpm.workbench.ht.backend.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.impl.SQLDataSetDefBuilderImpl;
import org.jbpm.workbench.ks.integration.KieServerDataSetProvider;
import org.kie.server.api.KieServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetDefsBootstrap.class);
    private static final String JBPM_DATA_SOURCE = "${" + KieServerConstants.CFG_PERSISTANCE_DS + "}";
    private static final String SQL_SELECT_COMMON_COLS = "" + //See addBuilderCommonColumns()
            "t.activationTime, " +
            "t.actualOwner, " +
            "t.createdBy, " +
            "t.createdOn, " +
            "t.deploymentId, " +
            "t.description, " +
            "t.dueDate, " +
            "t.name, " +
            "t.parentId, " +
            "t.priority, " +
            "t.processId, " +
            "t.processInstanceId, " +
            "t.processSessionId, " +
            "t.status, " +
            "t.taskId, " +
            "t.workItemId, " +
            "t.lastModificationDate, " +
            "pil.correlationKey, " +
            "pil.processInstanceDescription ";

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        SQLDataSetDefBuilderImpl builder = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_DATASET)
                .name("Human tasks")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                               SQL_SELECT_COMMON_COLS +
                               "from " +
                                    "AuditTaskImpl t " +
                               "left join " +
                                    "ProcessInstanceLog pil " +
                               "on " +
                                    "pil.id=t.processInstanceId"
                        ,
                       false);
        builder = addBuilderCommonColumns(builder);
        DataSetDef humanTasksDef = builder.buildDef();

        builder = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_USER_DATASET)
                .name("FILTERED_PO_TASK-Human tasks and users")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                               SQL_SELECT_COMMON_COLS + ", " +
                                    "oe.id " +
                               "from " +
                                    "AuditTaskImpl t " +
                               "left join " +
                                    "PeopleAssignments_PotOwners po " +
                               "on " +
                                    "t.taskId=po.task_id " +
                               "left join " +
                                    "OrganizationalEntity oe " +
                               "on " +
                                    "po.entity_id=oe.id " +
                               "left join " +
                                    "ProcessInstanceLog pil " +
                               "on " +
                                    "pil.id=t.processInstanceId"
                        ,
                       false);
        builder = addBuilderCommonColumns(builder)
                .label(COLUMN_ORGANIZATIONAL_ENTITY);
        DataSetDef humanTasksWithUserDef = builder.buildDef();

        builder = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_ADMIN_DATASET)
                .name("FILTERED_BA_TASK-Human tasks and admins")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                               SQL_SELECT_COMMON_COLS +
                               "from " +
                               "AuditTaskImpl t  " +
                               "left join " +
                                    "ProcessInstanceLog pil " +
                               "on " +
                                    "pil.id=t.processInstanceId"
                        ,
                       false);

        builder = addBuilderCommonColumns(builder);
        DataSetDef humanTaskWithAdminDef = builder.buildDef();

        DataSetDef humanTasksWithUserDomainDef = DataSetDefFactory.newSQLDataSetDef()       //Add to this dataset TaskName? to apply with the specified filter
                .uuid(HUMAN_TASKS_WITH_VARIABLES_DATASET)
                .name("Domain Specific Task")
                .dataSource(JBPM_DATA_SOURCE)
                .dbSQL("select " +
                                    "tvi.taskId, " +
                                    "(select ati.name from AuditTaskImpl ati where ati.taskId = tvi.taskId) as \"" + COLUMN_TASK_VARIABLE_TASK_NAME + "\", " +
                                    "tvi.name, " +
                                    "tvi.value " +
                               "from " +
                                    "TaskVariableImpl tvi",
                       false)
                .number(COLUMN_TASK_VARIABLE_TASK_ID)
                .label(COLUMN_TASK_VARIABLE_TASK_NAME)
                .label(COLUMN_TASK_VARIABLE_NAME)
                .label(COLUMN_TASK_VARIABLE_VALUE)
                .buildDef();

        // Hide all these internal data set from end user view
        humanTasksDef.setPublic(false);
        humanTasksDef.setProvider(KieServerDataSetProvider.TYPE);
        humanTasksWithUserDef.setPublic(false);
        humanTasksWithUserDef.setProvider(KieServerDataSetProvider.TYPE);
        humanTaskWithAdminDef.setPublic(false);
        humanTaskWithAdminDef.setProvider(KieServerDataSetProvider.TYPE);
        humanTasksWithUserDomainDef.setPublic(false);
        humanTasksWithUserDomainDef.setProvider(KieServerDataSetProvider.TYPE);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(humanTasksDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDef);
        dataSetDefRegistry.registerDataSetDef(humanTaskWithAdminDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDomainDef);
        LOGGER.info("Human task datasets registered");
    }

    private SQLDataSetDefBuilderImpl addBuilderCommonColumns(SQLDataSetDefBuilderImpl builder) {
        return builder
                .date(COLUMN_ACTIVATION_TIME)
                .label(COLUMN_ACTUAL_OWNER)
                .label(COLUMN_CREATED_BY)
                .date(COLUMN_CREATED_ON)
                .label(COLUMN_DEPLOYMENT_ID)
                .text(COLUMN_DESCRIPTION)
                .date(COLUMN_DUE_DATE)
                .label(COLUMN_NAME)
                .number(COLUMN_PARENT_ID)
                .number(COLUMN_PRIORITY)
                .label(COLUMN_PROCESS_ID)
                .number(COLUMN_PROCESS_INSTANCE_ID)
                .number(COLUMN_PROCESS_SESSION_ID)
                .label(COLUMN_STATUS)
                .number(COLUMN_TASK_ID)
                .number(COLUMN_WORK_ITEM_ID)
                .date(COLUMN_LAST_MODIFICATION_DATE)
                .label(COLUMN_PROCESS_INSTANCE_CORRELATION_KEY)
                .text(COLUMN_PROCESS_INSTANCE_DESCRIPTION);
    }
}