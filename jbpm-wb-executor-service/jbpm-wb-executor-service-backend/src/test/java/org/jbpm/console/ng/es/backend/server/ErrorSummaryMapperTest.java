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

package org.jbpm.console.ng.es.backend.server;

import java.util.Date;

import org.jbpm.console.ng.es.model.ErrorSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.ErrorInfoInstance;

import static org.junit.Assert.*;

public class ErrorSummaryMapperTest {

    public static void assertErrorSummary(final ErrorInfoInstance error, final ErrorSummary es) {
        assertNotNull(es);

        assertEquals(error.getId(), es.getId());
        assertEquals(error.getMessage(), es.getMessage());
        assertEquals(error.getErrorDate(), es.getTime());
        assertEquals(error.getRequestInfoId(), es.getRequestInfoId());
        assertEquals(error.getStacktrace(), es.getStacktrace());
    }

    public static ErrorInfoInstance newErrorInfoInstance() {
        return ErrorInfoInstance.builder()
                .id(1l)
                .errorDate(new Date())
                .requestId(2l)
                .stacktrace("stacktrace")
                .message("message")
                .build();
    }

    @Test
    public void testErrorSummaryMapper() {
        final ErrorInfoInstance error = newErrorInfoInstance();

        final ErrorSummary es = new ErrorSummaryMapper().apply(error);

        assertErrorSummary(error, es);
    }

    @Test
    public void testErrorSummaryMapperNull() {
        assertNull(new ErrorSummaryMapper().apply(null));
    }

}
