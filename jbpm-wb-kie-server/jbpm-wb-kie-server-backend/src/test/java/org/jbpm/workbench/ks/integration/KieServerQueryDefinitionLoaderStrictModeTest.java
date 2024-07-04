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

import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.enterprise.event.Event;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
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
public class KieServerQueryDefinitionLoaderStrictModeTest {

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
        this.kieServerQueryDefinitionLoader.init(new HashMap<String, String>(){{
            put(KieServerQueryDefinitionLoader.JBPM_WB_QUERY_MODE, "STRICT");
        }});
    }

    @Test
    public void testJbpmProcessInstances() {
        QueryDefinition expectedQuery = QueryDefinition.builder()
                .name("jbpmProcessInstances")
                .expression("SELECT LOG.PROCESSINSTANCEID, LOG.PROCESSID, LOG.START_DATE, LOG.END_DATE, LOG.STATUS, LOG.PARENTPROCESSINSTANCEID, LOG.OUTCOME, LOG.DURATION, LOG.USER_IDENTITY, LOG.PROCESSVERSION, LOG.PROCESSNAME, LOG.CORRELATIONKEY, LOG.EXTERNALID, LOG.PROCESSINSTANCEDESCRIPTION, LOG.SLA_DUE_DATE, LOG.SLACOMPLIANCE, COALESCE ( INFO.LASTMODIFICATIONDATE, LOG.END_DATE ) AS LASTMODIFICATIONDATE, COUNT( ERRINFO.ID ) ERRORCOUNT FROM ProcessInstanceLog LOG LEFT JOIN ExecutionErrorInfo ERRINFO ON ERRINFO.PROCESS_INST_ID=LOG.PROCESSINSTANCEID AND ERRINFO.ERROR_ACK=0 LEFT JOIN ProcessInstanceInfo INFO ON INFO.INSTANCEID=LOG.PROCESSINSTANCEID GROUP BY LOG.PROCESSINSTANCEID, LOG.PROCESSID, LOG.START_DATE, LOG.END_DATE, LOG.STATUS, LOG.PARENTPROCESSINSTANCEID, LOG.OUTCOME, LOG.DURATION, LOG.USER_IDENTITY, LOG.PROCESSVERSION, LOG.PROCESSNAME, LOG.CORRELATIONKEY, LOG.EXTERNALID, LOG.PROCESSINSTANCEDESCRIPTION, LOG.SLA_DUE_DATE, LOG.SLACOMPLIANCE, COALESCE ( INFO.LASTMODIFICATIONDATE, LOG.END_DATE )")
                .target("FILTERED_PROCESS")
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
                .target("FILTERED_PROCESS")
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
                .target("FILTERED_PROCESS")
                .build();
        testQueryDefinitionLoaded(expectedQuery);
    }

    private void testQueryDefinitionLoaded(QueryDefinition expectedQueryDefinition) {
        assertTrue("No named-queries were found", receivedEvents.size() > 0);
        assertNotNull("No query definition found for " + expectedQueryDefinition.getName(), receivedEvents.get(expectedQueryDefinition.getName()));
        assertEquals(expectedQueryDefinition.getExpression(), receivedEvents.get(expectedQueryDefinition.getName()).getExpression());
        assertEquals(expectedQueryDefinition.getTarget(), receivedEvents.get(expectedQueryDefinition.getName()).getTarget());
    }
}
