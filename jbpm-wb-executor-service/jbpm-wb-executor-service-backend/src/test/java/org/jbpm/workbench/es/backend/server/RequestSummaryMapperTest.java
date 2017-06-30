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

import org.jbpm.workbench.es.model.RequestSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.ErrorInfoInstanceList;
import org.kie.server.api.model.instance.RequestInfoInstance;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.jbpm.workbench.es.backend.server.ErrorSummaryMapperTest.newErrorInfoInstance;
import static org.junit.Assert.*;

public class RequestSummaryMapperTest {

    public static void assertRequestSummary(final RequestInfoInstance request,
                                            final RequestSummary rs) {
        assertNotNull(rs);

        assertEquals(request.getId(),
                     rs.getJobId());
        assertEquals(request.getId(),
                     rs.getId());
        assertEquals(request.getStatus(),
                     rs.getStatus());
        assertEquals(request.getCommandName(),
                     rs.getCommandName());
        assertEquals(request.getBusinessKey(),
                     rs.getKey());
        assertEquals(request.getRetries(),
                     rs.getRetries());
        assertEquals(request.getScheduledDate(),
                     rs.getTime());
        assertEquals(request.getMessage(),
                     rs.getMessage());
        assertEquals(null,
                     rs.getProcessName());
        assertEquals(null,
                     rs.getProcessInstanceId());
        assertEquals(null,
                     rs.getProcessInstanceDescription());
    }

    public static RequestInfoInstance newRequestInfoInstance() {
        return RequestInfoInstance.builder()
                .id(1l)
                .businessKey("businessKey")
                .command("commandName")
                .data(singletonMap("key",
                                   "data"))
                .errors(new ErrorInfoInstanceList(singletonList(newErrorInfoInstance())))
                .executions(10)
                .message("message")
                .retries(2)
                .scheduledDate(new Date())
                .status("status")
                .responseData(singletonMap("responseKey",
                                           "responseData"))
                .build();
    }

    @Test
    public void testRequestSummaryMapper() {
        final RequestInfoInstance request = newRequestInfoInstance();

        final RequestSummary rs = new RequestSummaryMapper().apply(request);

        assertRequestSummary(request,
                             rs);
    }

    @Test
    public void testRequestSummaryMapperNull() {
        assertNull(new RequestSummaryMapper().apply(null));
    }
}
