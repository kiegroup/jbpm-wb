/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * This test class has been created to make contributors aware about the importance of the queries defined in the
 * "default-query-definitions.json" file. These queries has been tested and optimized due to some important performance
 * issues found on a very important and critical customer.
 * Please, consider running a performance test when one of them is changed.
 *
 * @see <a href="https://issues.redhat.com/browse/JBPM-9099">JBPM-9099</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class KieServerQueryDefinitionLoaderTest {

    private final Map<String, QueryDefinition> receivedEvents = new HashMap<>();

    private static final int TOTAL_QUERY_DEFINITIONS_EXPECTED = 12;

    @Mock
    private Event<QueryDefinitionLoaded> event;

    @InjectMocks
    private KieServerQueryDefinitionLoader kieServerQueryDefinitionLoader;

    @Before
    public void setup() {
        openMocks(this);
        doAnswer(invocation -> {
            QueryDefinitionLoaded queryDefinitionLoaded = invocation.getArgument(0);
            receivedEvents.put(queryDefinitionLoaded.getDefinition().getName(), queryDefinitionLoaded.getDefinition());
            return null;
        }).when(event).fire(any(QueryDefinitionLoaded.class));
        this.kieServerQueryDefinitionLoader.init();
    }

    @Test
    public void testJbpmProcessInstances() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmProcessInstances")
                .expression("SELECT log.processInstanceId, log.processId, log.start_date, log.end_date, log.status, log.parentProcessInstanceId, log.outcome, log.duration, log.user_identity, log.processVersion, log.processName, log.correlationKey, log.externalId,  log.processInstanceDescription, log.sla_due_date, log.slaCompliance, COALESCE ( info.lastModificationDate, log.end_date ) AS lastModificationDate, COUNT( errInfo.id ) errorCount FROM ProcessInstanceLog log INNER JOIN ExecutionErrorInfo errInfo ON errInfo.Process_Inst_Id=log.processInstanceId AND errInfo.ERROR_ACK=0 LEFT JOIN ProcessInstanceInfo info ON info.InstanceId=log.processInstanceId GROUP BY log.processInstanceId, log.processId, log.start_date, log.end_date, log.status, log.parentProcessInstanceId, log.outcome, log.duration, log.user_identity, log.processVersion, log.processName, log.correlationKey, log.externalId, log.processInstanceDescription, log.sla_due_date, log.slaCompliance, COALESCE ( info.lastModificationDate, log.end_date ) HAVING COUNT( errInfo.id ) > 0")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmProcessInstancesWithVariables() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmProcessInstancesWithVariables")
                .expression("select vil.processInstanceId, vil.processId, vil.id, " +
                                    "vil.variableId, vil.value from VariableInstanceLog vil " +
                                    "left join VariableInstanceLog vil2 on vil.processInstanceId = vil2.processInstanceId " +
                                    "and vil.variableId = vil2.variableId and vil.id < vil2.id where vil2.id is null")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testProcessesMonitoring() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("processesMonitoring")
                .expression("select log.processInstanceId, log.processId, log.start_date, log.end_date, log.status, " +
                                    "log.duration, log.user_identity, log.processVersion, log.processName, " +
                                    "log.externalId from ProcessInstanceLog log")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testTasksMonitoring() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("tasksMonitoring")
                .expression("select p.processName, p.externalId, t.taskId, t.taskName, t.status, t.createdDate, " +
                                    "t.startDate, t.endDate, t.processInstanceId, t.userId, t.duration " +
                                    "from ProcessInstanceLog p inner join BAMTaskSummary t on " +
                                    "(t.processInstanceId = p.processInstanceId) inner join (select min(pk) as pk " +
                                    "from BAMTaskSummary group by taskId) d on t.pk = d.pk")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmRequestList() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmRequestList")
                .expression("select ri.id, ri.timestamp, ri.status, ri.commandName, ri.message, ri.businessKey, " +
                                    "ri.retries, ri.executions, pil.processName, pil.processInstanceId, " +
                                    "pil.processInstanceDescription, ri.deploymentId from RequestInfo ri left join " +
                                    "ProcessInstanceLog pil on pil.processInstanceId=ri.processInstanceId")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmExecutionErrorList() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmExecutionErrorList")
                .expression("select eri.ERROR_ACK, eri.ERROR_ACK_BY, eri.ERROR_ACK_AT, eri.ACTIVITY_ID, " +
                                    "eri.ACTIVITY_NAME, eri.DEPLOYMENT_ID, eri.ERROR_DATE, eri.ERROR_ID, " +
                                    "eri.ERROR_MSG, eri.JOB_ID, eri.PROCESS_ID, eri.PROCESS_INST_ID, eri.ERROR_TYPE " +
                                    "from ExecutionErrorInfo eri")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmHumanTasks() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmHumanTasks")
                .expression("select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, " +
                                    "t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, " +
                                    "t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, " +
                                    "t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription, " +
                                    "nil.sla_due_date, nil.slaCompliance from AuditTaskImpl t left join " +
                                    "ProcessInstanceLog pil on pil.processInstanceId=t.processInstanceId left join " +
                                    "NodeInstanceLog nil on nil.workItemId=t.workItemId")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmHumanTasksWithUser() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmHumanTasksWithUser")
                .expression("select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, " +
                                    "t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, " +
                                    "t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, " +
                                    "t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription , " +
                                    "oe.id, eo.entity_id, nil.sla_due_date, nil.slaCompliance from AuditTaskImpl t " +
                                    "left join PeopleAssignments_PotOwners po on t.taskId=po.task_id left join " +
                                    "OrganizationalEntity oe on po.entity_id=oe.id left join ProcessInstanceLog pil on " +
                                    "pil.processInstanceId=t.processInstanceId left join PeopleAssignments_ExclOwners eo " +
                                    "on t.taskId=eo.task_id left join NodeInstanceLog nil on nil.workItemId=t.workItemId")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmHumanTasksWithAdmin() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmHumanTasksWithAdmin")
                .expression("select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, " +
                                    "t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, " +
                                    "t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, " +
                                    "t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription ,oe.id, " +
                                    "nil.sla_due_date, nil.slaCompliance, (select COUNT(errInfo.id) from ExecutionErrorInfo " +
                                    "errInfo where errInfo.ACTIVITY_ID = t.taskId and errInfo.PROCESS_INST_ID = pil.processInstanceId " +
                                    "and errInfo.ERROR_ACK = 0 and errInfo.ERROR_TYPE = 'Task') as errorCount from AuditTaskImpl t  " +
                                    "left join ProcessInstanceLog pil on pil.processInstanceId = t.processInstanceId " +
                                    "left join PeopleAssignments_BAs ba on t.taskId = ba.task_id left join OrganizationalEntity oe " +
                                    "on ba.entity_id = oe.id left join NodeInstanceLog nil on nil.workItemId=t.workItemId")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }
    
    @Test
    public void testJbpmHumanTasksWithAdminExtended() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmHumanTasksWithAdminExtended")
                .expression("select t.activationTime, t.actualOwner, t.createdBy, t.createdOn, t.deploymentId, " +
                                    "t.description, t.dueDate, t.name, t.parentId, t.priority, t.processId, " +
                                    "t.processInstanceId, t.processSessionId, t.status, t.taskId, t.workItemId, " +
                                    "t.lastModificationDate, pil.correlationKey, pil.processInstanceDescription ,oe.id, " +
                                    "nil.sla_due_date, nil.slaCompliance,(select COUNT(errInfo.id) from ExecutionErrorInfo " +
                                    "errInfo where errInfo.ACTIVITY_ID = t.taskId and errInfo.PROCESS_INST_ID = pil.processInstanceId " +
                                    "and errInfo.ERROR_ACK = 0 and errInfo.ERROR_TYPE = 'Task') as errorCount, i18n.text  as subject, " +
                                    "i18n.language as language, task.formname as formname, (SELECT te1.userId FROM taskEvent te1 " +
                                    "LEFT JOIN taskEvent te2 ON te1.id < te2.id WHERE te2.id IS NULL) as lastUser from AuditTaskImpl t  " +
                                    "left join ProcessInstanceLog pil on pil.processInstanceId = t.processInstanceId " +
                                    "left join PeopleAssignments_BAs ba on t.taskId = ba.task_id left join OrganizationalEntity oe " +
                                    "on ba.entity_id = oe.id left join NodeInstanceLog nil on nil.workItemId=t.workItemId " +
                                    "left join Task task on task.id = t.taskId left join I18NText i18n ON i18n.Task_Subjects_Id = t.taskId")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }
    
    @Test
    public void testJbpmHumanTasksWithVariables() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmHumanTasksWithVariables")
                .expression("select tvi.taskId, tvi.name, tvi.value from TaskVariableImpl tvi")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testJbpmProcessInstanceLogs() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmProcessInstanceLogs")
                .expression("select log.id, log.nodeId, log.nodeName, log.nodeType, log.externalId, " +
                                    "log.processInstanceId, log.log_date, log.connection, log.type, log.workItemId, " +
                                    "log.referenceId, log.nodeContainerId, log.sla_due_date, log.slaCompliance " +
                                    "from NodeInstanceLog log ")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    @Test
    public void testNonExistingQueryDefinition() {
        QueryDefinition nonExistingQuery = QueryDefinition.builder()
                .name("nonExistingQueryDefinition")
                .expression("a fake query")
                .build();
        assertTrue("No named-queries were found", receivedEvents.size() > 0);
        assertNull(nonExistingQuery.getName(), receivedEvents.get(nonExistingQuery.getName()));
    }

    @Test
    public void testNonMatchingQueryDefinition() {
        QueryDefinition nonMatchingQuery = QueryDefinition.builder()
                .name("jbpmProcessInstancesWithVariables")
                .expression("select vil.processInstanceId, vil.processId, vil.id, " +
                                    "vil.variableId, vil.value from VariableInstanceLog vil " +
                                    "left join VariableInstanceLog vil2 on vil.processInstanceId = vil2.processInstanceId ")
                .build();
        assertTrue("No named-queries were found", receivedEvents.size() > 0);
        assertNotNull("No query definition found for " + nonMatchingQuery.getName(), receivedEvents.get(nonMatchingQuery.getName()));
        assertNotEquals(nonMatchingQuery.getExpression(), receivedEvents.get(nonMatchingQuery.getName()).getExpression());
    }

    @Test
    public void testTotalQueryDefinitionsLoaded() {
        assertEquals("Number of QueryDefinitions loaded (" + receivedEvents.size() + ") does not match with expected (" + TOTAL_QUERY_DEFINITIONS_EXPECTED + ")",
                     TOTAL_QUERY_DEFINITIONS_EXPECTED, receivedEvents.size());
    }

    private void testQueryDefinitionLoaded(QueryDefinition expectedQueryDefinition) {
        assertTrue("No named-queries were found", receivedEvents.size() > 0);
        assertNotNull("No query definition found for " + expectedQueryDefinition.getName(), receivedEvents.get(expectedQueryDefinition.getName()));
        assertEquals(expectedQueryDefinition.getExpression(), receivedEvents.get(expectedQueryDefinition.getName()).getExpression());
    }
}
