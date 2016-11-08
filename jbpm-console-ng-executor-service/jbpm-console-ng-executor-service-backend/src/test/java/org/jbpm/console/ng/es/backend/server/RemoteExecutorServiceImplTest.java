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

import org.jbpm.console.ng.bd.integration.KieServerIntegration;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.client.JobServicesClient;
import org.kie.server.client.KieServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.console.ng.es.backend.server.RequestDetailsMapperTest.assertRequestDetails;
import static org.jbpm.console.ng.es.backend.server.RequestSummaryMapperTest.newRequestInfoInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteExecutorServiceImplTest {

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    JobServicesClient jobServicesClient;

    @InjectMocks
    RemoteExecutorServiceImpl executorService;

    @Before
    public void init() {
        final KieServicesClient servicesClient = mock(KieServicesClient.class);
        when(servicesClient.getServicesClient(JobServicesClient.class)).thenReturn(jobServicesClient);
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(servicesClient);
    }

    @Test
    public void testGetRequestDetails() {
        final RequestInfoInstance ri = newRequestInfoInstance();

        when(jobServicesClient.getRequestById(ri.getId(), true, true)).thenReturn(ri);

        final RequestDetails rd = executorService.getRequestDetails("", ri.getId());
        assertRequestDetails(ri, rd);
    }

    @Test
    public void testGetRequestDetailsEmpty() {
        final Long requestId = 1l;
        when(jobServicesClient.getRequestById(requestId, true, true)).thenReturn(new RequestInfoInstance());

        final RequestDetails requestDetails = executorService.getRequestDetails("", requestId);
        assertNotNull(requestDetails);
        assertNotNull(requestDetails.getRequest());
        assertTrue(requestDetails.getErrors().isEmpty());
        assertTrue(requestDetails.getParams().isEmpty());
    }

    @Test
    public void testGetRequestDetailsNull() {
        final RequestDetails requestDetails = executorService.getRequestDetails(null, null);
        assertNull(requestDetails);
    }

}
