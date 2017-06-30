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

import java.util.Map;

import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.junit.Test;
import org.kie.server.api.model.instance.RequestInfoInstance;

import static org.jbpm.workbench.es.backend.server.ErrorSummaryMapperTest.assertErrorSummary;
import static org.jbpm.workbench.es.backend.server.RequestSummaryMapperTest.assertRequestSummary;
import static org.jbpm.workbench.es.backend.server.RequestSummaryMapperTest.newRequestInfoInstance;
import static org.junit.Assert.*;

public class RequestDetailsMapperTest {

    public static void assertRequestDetails(final RequestInfoInstance ri,
                                            final RequestDetails rd) {
        assertNotNull(rd);

        assertNotNull(rd.getRequest());
        assertRequestSummary(ri,
                             rd.getRequest());
        assertNotNull(rd.getErrors());
        assertErrorSummary(ri.getErrors().getItems().get(0),
                           rd.getErrors().get(0));
        assertNotNull(rd.getParams());
        assertRequestParameterSummary(ri.getData().entrySet().iterator().next(),
                                      rd.getParams().get(0));
    }

    public static void assertRequestParameterSummary(final Map.Entry<String, Object> param,
                                                     final RequestParameterSummary rps) {
        assertNotNull(rps);

        assertEquals(param.getKey(),
                     rps.getKey());
        assertEquals(param.getValue(),
                     rps.getValue());
    }

    @Test
    public void testRequestDetailsMapper() {
        final RequestInfoInstance ri = newRequestInfoInstance();

        final RequestDetails rd = new RequestDetailsMapper().apply(ri);

        assertRequestDetails(ri,
                             rd);
    }

    @Test
    public void testRequestDetailsMapperNull() {
        assertNull(new RequestDetailsMapper().apply(null));
    }
}
