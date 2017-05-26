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

import java.util.Date;

import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.junit.Test;
import org.kie.server.api.model.admin.ExecutionErrorInstance;

import static org.junit.Assert.*;

public class ExecutionErrorSummaryMapperTest {

    public static void assertExecutionErrorSummary(final ExecutionErrorInstance error,
                                                   final ExecutionErrorSummary es) {
        assertNotNull(es);

        assertEquals(error.getErrorMessage(),
                     es.getErrorMessage());
        assertEquals(error.getError(),
                     es.getError());
        assertEquals(error.getType(),
                     es.getType().getType());
        assertEquals(error.getErrorMessage(),
                     es.getErrorMessage());
        assertEquals(error.getAcknowledgedAt(),
                     es.getAcknowledgedAt());
        assertEquals(error.getAcknowledgedBy(),
                     es.getAcknowledgedBy());
        assertEquals(error.isAcknowledged(),
                     es.isAcknowledged());
        assertEquals(error.getActivityId(),
                     es.getActivityId());
        assertEquals(error.getActivityName(),
                     es.getActivityName());
        assertEquals(error.getContainerId(),
                     es.getDeploymentId());
        assertEquals(error.getErrorId(),
                     es.getErrorId());
        assertEquals(error.getProcessId(),
                     es.getProcessId());
        assertEquals(error.getProcessInstanceId(),
                     es.getProcessInstanceId());
        assertEquals(error.getJobId(),
                     es.getJobId());
    }

    public static ExecutionErrorInstance createTestError(String id) {
        return ExecutionErrorInstance.builder()
                .errorId(id + "")
                .error(id + "_stackTrace")
                .acknowledged(false)
                .acknowledgedAt(new Date())
                .acknowledgedBy("testUser")
                .activityId(Long.valueOf(id + 20))
                .activityName(id + "_Act_name")
                .errorDate(new Date())
                .type("Task")
                .containerId(id + "_deployment")
                .processInstanceId(Long.valueOf(id))
                .processId(id + "_processId")
                .jobId(Long.valueOf(id))
                .message(id + "_message").build();
    }

    @Test
    public void testErrorSummaryMapper() {
        final ExecutionErrorInstance error = createTestError("1");

        final ExecutionErrorSummary ees = new ExecutionErrorSummaryMapper().apply(error);

        assertExecutionErrorSummary(error,
                                    ees);
    }

    @Test
    public void testErrorSummaryMapperNull() {
        assertNull(new ErrorSummaryMapper().apply(null));
    }
}
