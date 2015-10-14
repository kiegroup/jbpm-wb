/*
 * Copyright 2015 JBoss Inc
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
package org.jbpm.console.ng.server.impl;

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.console.ng.es.client.editors.requestlist.dataset.DataSetRequestListViewImpl;
import org.jbpm.console.ng.ht.client.editors.taskslist.grid.dash.DataSetTasksListGridViewImpl;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.DataSetProcessInstanceListViewImpl;
import org.uberfire.commons.services.cdi.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@ApplicationScoped
public class DashbuilderBootstrap {
    public static final String JBPM_DATASOURCE = "java:jboss/datasources/ExampleDS";
    public static final String HUMAN_TASKS_DATASET = "jbpmHumanTasks";
    public static final String HUMAN_TASKS_TABLE = "AuditTaskImpl";

    public static final String PROCESS_INSTANCE_DATASET = "jbpmProcessInstances";
    public static final String PROCESS_INSTANCE_TABLE = "ProcessInstanceLog";

    public static final String HUMAN_TASKS_WITH_USER_DATASET = "jbpmHumanTasksWithUser";
    public static final String HUMAN_TASKS_WITH_ADMIN_DATASET = "jbpmHumanTasksWithAdmin";

    public static final String REQUEST_LIST_DATASET = "jbpmRequestList";
    public static final String REQUEST_LIST_TABLE = "RequestInfo";

    public static final String  PROCESS_INSTANCE_WITH_VARIABLES_DATASET = "jbpmProcessInstancesWithVariables";

    public static final String TASKS_MONITORING_DATASET = "tasksMonitoring";
    public static final String PROCESSES_MONITORING_DATASET = "processesMonitoring";

    @Inject
    protected DataSetDefRegistry dataSetDefRegistry;

    @PostConstruct
    protected void init() {
        registerDataSetDefinitions();
    }

    protected void registerDataSetDefinitions() {

        DataSetDef humanTasksDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_DATASET)
                .name("Human tasks")
                .dataSource(JBPM_DATASOURCE)
                .dbTable(HUMAN_TASKS_TABLE, false)
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .number(DataSetTasksListGridViewImpl.COLUMN_TASKID)
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .buildDef();

        DataSetDef processInstancesDef = DataSetFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_DATASET)
                .name("Process Instances")
                .dataSource(JBPM_DATASOURCE)
                .dbTable(PROCESS_INSTANCE_TABLE, false)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSID)
                .date(DataSetProcessInstanceListViewImpl.COLUMN_START)
                .date(DataSetProcessInstanceListViewImpl.COLUMN_END)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_STATUS)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_PARENTPROCESSINSTANCEID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_OUTCOME)
                .number(DataSetProcessInstanceListViewImpl.COLUMN_DURATION)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_IDENTITY)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSVERSION)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSNAME)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_CORRELATIONKEY)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_EXTERNALID)
                .label(DataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION)
                .buildDef();

        DataSetDef humanTasksWithUserDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_USER_DATASET)
                .name("Human tasks and users")
                .dataSource(JBPM_DATASOURCE)
                .dbSQL("select  t.activationTime, t.actualOwner, t.createdBy, "
                        + "t.createdOn, t.deploymentId, t.description, t.dueDate, "
                        + "t.name, t.parentId, t.priority, t.processId, t.processInstanceId, "
                        + "t.processSessionId, t.status, t.taskId, t.workItemId, oe.id oeid "
                        + "from AuditTaskImpl t, "
                        + "PeopleAssignments_PotOwners po, "
                        + "OrganizationalEntity oe "
                        + "where t.id = po.task_id and po.entity_id = oe.id", false)
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .label(DataSetTasksListGridViewImpl.COLUMN_TASKID)   //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .label(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

        DataSetDef humanTaskWithAdminDef = DataSetFactory.newSQLDataSetDef()
                .uuid(HUMAN_TASKS_WITH_ADMIN_DATASET)
                .name("Human tasks and admins")
                .dataSource(JBPM_DATASOURCE)
                .dbSQL("select t.activationTime, t.actualOwner, t.createdBy, "
                        + "t.createdOn, t.deploymentId, t.description, t.dueDate, "
                        + "t.name, t.parentId, t.priority, t.processId, t.processInstanceId, "
                        + "t.processSessionId, t.status, t.taskId, t.workItemId, oe.id oeid "
                        + "from AuditTaskImpl t, "
                        + "PeopleAssignments_BAs bas, "
                        + "OrganizationalEntity oe "
                        + "where t.id = bas.task_id and bas.entity_id = oe.id", false)
                .date(DataSetTasksListGridViewImpl.COLUMN_ACTIVATIONTIME)
                .label(DataSetTasksListGridViewImpl.COLUMN_ACTUALOWNER)
                .label(DataSetTasksListGridViewImpl.COLUMN_CREATEDBY)
                .date(DataSetTasksListGridViewImpl.COLUMN_CREATEDON)
                .label(DataSetTasksListGridViewImpl.COLUMN_DEPLOYMENTID)
                .text(DataSetTasksListGridViewImpl.COLUMN_DESCRIPTION)
                .date(DataSetTasksListGridViewImpl.COLUMN_DUEDATE)
                .label(DataSetTasksListGridViewImpl.COLUMN_NAME)
                .number(DataSetTasksListGridViewImpl.COLUMN_PARENTID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PRIORITY)
                .label(DataSetTasksListGridViewImpl.COLUMN_PROCESSID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSINSTANCEID)
                .number(DataSetTasksListGridViewImpl.COLUMN_PROCESSSESSIONID)
                .label(DataSetTasksListGridViewImpl.COLUMN_STATUS)
                .label(DataSetTasksListGridViewImpl.COLUMN_TASKID)     //declaring as label(even though it's numeric) because needs apply groupby and  Group by number not supported
                .number(DataSetTasksListGridViewImpl.COLUMN_WORKITEMID)
                .label(DataSetTasksListGridViewImpl.COLUMN_ORGANIZATIONAL_ENTITY)
                .buildDef();

        DataSetDef requestListDef = DataSetFactory.newSQLDataSetDef()
                .uuid(REQUEST_LIST_DATASET)
                .name("Request List")
                .dataSource(JBPM_DATASOURCE)
                .dbTable(REQUEST_LIST_TABLE, false)
                .number(DataSetRequestListViewImpl.COLUMN_ID)
                .date(DataSetRequestListViewImpl.COLUMN_TIMESTAMP)
                .label(DataSetRequestListViewImpl.COLUMN_STATUS)
                .label(DataSetRequestListViewImpl.COLUMN_COMMANDNAME)
                .label(DataSetRequestListViewImpl.COLUMN_MESSAGE)
                .label(DataSetRequestListViewImpl.COLUMN_BUSINESSKEY)
                .buildDef();

        DataSetDef processWithVariablesDef = DataSetFactory.newSQLDataSetDef()
                .uuid(PROCESS_INSTANCE_WITH_VARIABLES_DATASET)
                .name("Variable for Evalution Process Instances")
                .dataSource(JBPM_DATASOURCE)
                .dbSQL("select pil.processInstanceId pid, pil.processId pname, v.id varid, v.variableId varname, v.value varvalue from ProcessInstanceLog pil, "
                        + "(select vil.variableId, max(vil.id) as maxvilid from VariableInstanceLog vil  group by vil.processInstanceId, vil.variableId) "
                        + "as x inner join VariableInstanceLog as v on "
                        + "v.variableId = x.variableId and v.processInstanceId = pil.processInstanceId and "
                        + "v.id = x.maxvilid", false)
                .number("pid")
                .label("pname")
                .number("varid")
                .label("varname")
                .label("varvalue")
                .buildDef();

        DataSetDef processMonitoringDef = DataSetFactory.newSQLDataSetDef()
                .uuid(PROCESSES_MONITORING_DATASET)
                .name("Processes monitoring")
                .dataSource(JBPM_DATASOURCE)
                .dbTable(PROCESS_INSTANCE_TABLE, true)
                .buildDef();

        DataSetDef taskMonitoringDef = DataSetFactory.newSQLDataSetDef()
                .uuid(TASKS_MONITORING_DATASET)
                .name("Tasks monitoring")
                .dataSource(JBPM_DATASOURCE)
                .dbSQL("select p.processname, t.* " +
                                "from processinstancelog p " +
                                "inner join bamtasksummary t on (t.processinstanceid = p.processinstanceid) " +
                                "inner join (select min(pk) pk from bamtasksummary group by taskid) d on t.pk=d.pk",
                        true)
                .buildDef();


        // Hide all these internal data set from end user view
        humanTasksDef.setPublic(false);
        processInstancesDef.setPublic(false);
        humanTasksWithUserDef.setPublic(false);
        humanTaskWithAdminDef.setPublic(false);
        requestListDef.setPublic(false);
        processWithVariablesDef.setPublic(false);
        processMonitoringDef.setPublic(false);
        taskMonitoringDef.setPublic(false);

        // Register the data set definitions
        dataSetDefRegistry.registerDataSetDef(humanTasksDef);
        dataSetDefRegistry.registerDataSetDef(processInstancesDef);
        dataSetDefRegistry.registerDataSetDef(humanTasksWithUserDef);
        dataSetDefRegistry.registerDataSetDef(humanTaskWithAdminDef);
        dataSetDefRegistry.registerDataSetDef(requestListDef);
        dataSetDefRegistry.registerDataSetDef(processWithVariablesDef);
        dataSetDefRegistry.registerDataSetDef(processMonitoringDef);
        dataSetDefRegistry.registerDataSetDef(taskMonitoringDef);
    }
}
