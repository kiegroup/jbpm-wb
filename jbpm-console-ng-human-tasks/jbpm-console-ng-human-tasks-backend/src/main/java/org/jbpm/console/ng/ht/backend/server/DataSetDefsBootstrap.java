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
package org.jbpm.console.ng.ht.backend.server;

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

import static org.jbpm.console.ng.ht.model.TaskDataSetConstants.*;

@Startup
@ApplicationScoped
public class DataSetDefsBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(DataSetDefsBootstrap.class);

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void registerDataSetDefinitions() {
        String jbpmDataSource = JpaSettings.get().getDataSourceJndiName();

        DataSetDef humanTasksDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_DATASET)
                .name("Human tasks")
                .dataSource(jbpmDataSource)
                .dbTable("AuditTaskImpl", false)
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
                .buildDef();

        DataSetDef humanTasksWithUserDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_USER_DATASET)
                .name("Human tasks and users")
                .dataSource(jbpmDataSource)
                .dbSQL( "select " +
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
                            "oe.id " +
                        "from " +
                            "AuditTaskImpl t, " +
                            "PeopleAssignments_PotOwners po, " +
                            "OrganizationalEntity oe " +
                        "where " +
                            "t.taskId = po.task_id and " +
                            "po.entity_id = oe.id", false )
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
                .label(COLUMN_TASK_ID)   //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(COLUMN_WORK_ITEM_ID)
                .label(COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

        DataSetDef humanTaskWithAdminDef = DataSetDefFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_ADMIN_DATASET)
                .name("Human tasks and admins")
                .dataSource(jbpmDataSource)
                .dbSQL("select " +
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
                            "oe.id " +
                        "from " +
                            "AuditTaskImpl t, " +
                            "PeopleAssignments_BAs bas, " +
                            "OrganizationalEntity oe " +
                        "where " +
                            "t.taskId = bas.task_id and " +
                            "bas.entity_id = oe.id", false)
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
                .label(COLUMN_TASK_ID)     //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(COLUMN_WORK_ITEM_ID)
                .label(COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

       DataSetDef humanTasksWithUserDomainDef = DataSetDefFactory.newSQLDataSetDef()       //Add to this dataset TaskName? to apply with the specified filter
                .uuid(HUMAN_TASKS_WITH_VARIABLES_DATASET)
                .name("Domain Specific Task")
                .dataSource(jbpmDataSource)
                .dbSQL("select " +
                            "tvi.taskId, " +
                            "(select ati.name from AuditTaskImpl ati where ati.taskId = tvi.taskId) as \"" + COLUMN_TASK_VARIABLE_TASK_NAME + "\", " +
                            "tvi.name, " +
                            "tvi.value " +
                        "from " +
                            "TaskVariableImpl tvi", false)
               .number(COLUMN_TASK_VARIABLE_TASK_ID)
               .label(COLUMN_TASK_VARIABLE_TASK_NAME)
               .label(COLUMN_TASK_VARIABLE_NAME)
               .label(COLUMN_TASK_VARIABLE_VALUE)
               .buildDef();


        // Hide all these internal data set from end user view
        humanTasksDef.setPublic(false);
        humanTasksWithUserDef.setPublic(false);
        humanTaskWithAdminDef.setPublic(false);
        humanTasksWithUserDomainDef.setPublic(false);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(humanTasksDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDef);
        dataSetDefRegistry.registerDataSetDef(humanTaskWithAdminDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDomainDef);
        logger.info("Human task datasets registered");

    }
}
