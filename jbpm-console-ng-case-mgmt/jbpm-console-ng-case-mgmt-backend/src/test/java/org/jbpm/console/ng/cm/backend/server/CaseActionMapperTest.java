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

package org.jbpm.console.ng.cm.backend.server;

import java.util.Date;

import org.jbpm.console.ng.cm.model.CaseActionSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.TaskSummary;

import static org.junit.Assert.*;

public class CaseActionMapperTest {

    public static void assertCaseAction(final TaskSummary cc, final CaseActionSummary ccs) {
        assertNotNull(ccs);
        assertEquals(cc.getName(),ccs.getName());
        assertEquals(cc.getSubject(),ccs.getSubject());
        assertEquals(cc.getDescription(),ccs.getDescription());
        assertEquals(cc.getStatus(),ccs.getStatus());
        assertEquals(cc.getPriority(),ccs.getPriority());
        assertEquals(cc.getSkipable(),ccs.getSkipable());
        assertEquals(cc.getActualOwner(),ccs.getActualOwner());
        assertEquals(cc.getCreatedBy(),ccs.getCreatedBy());
        assertEquals(cc.getCreatedOn(),ccs.getCreatedOn());
        assertEquals(cc.getActivationTime(),ccs.getActivationTime());
        assertEquals(cc.getExpirationTime(),ccs.getExpirationTime());
        assertEquals(cc.getProcessInstanceId(),ccs.getProcessInstanceId());
        assertEquals(cc.getProcessId(),ccs.getProcessId());
        assertEquals(cc.getContainerId(),ccs.getContainerId());
        assertEquals(cc.getId(), ccs.getId());
    }

    @Test
    public void testCaseActionMapper_mapCaseAction() {
        final TaskSummary cc = TaskSummary.builder()
                .name("action-name")
                .subject("action-subject")
                .description("action-desc")
                .status("Available")
                .actualOwner("actualOwner")
                .createdBy("createdBy")
                .createdOn(new Date())
                .activationTime(new Date())
                .expirationTime(new Date())
                .processInstanceId(Long.valueOf(100))
                .processId("processId")
                .containerId("containerId")
                .build();

        final CaseActionSummary ccs = new CaseActionMapper().apply(cc);
        assertCaseAction(cc, ccs);
    }

    @Test
    public void testCaseActionMapper_mapNull() {
        final TaskSummary cc = null;
        final CaseActionSummary ccs = new CaseActionMapper().apply(cc);
        assertNull(ccs);
    }
}
