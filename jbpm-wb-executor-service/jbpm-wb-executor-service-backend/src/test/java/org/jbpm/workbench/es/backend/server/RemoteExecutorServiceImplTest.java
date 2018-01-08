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

import static org.jbpm.workbench.es.backend.server.ExecutionErrorSummaryMapperTest.assertExecutionErrorSummary;
import static org.jbpm.workbench.es.backend.server.ExecutionErrorSummaryMapperTest.createTestError;
import static org.jbpm.workbench.es.backend.server.RequestDetailsMapperTest.assertRequestDetails;
import static org.jbpm.workbench.es.backend.server.RequestSummaryMapperTest.newRequestInfoInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.admin.ExecutionErrorInstance;
import org.kie.server.api.model.instance.RequestInfoInstance;
import org.kie.server.client.JobServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.admin.ProcessAdminServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoteExecutorServiceImplTest {

    @Mock
    KieServerIntegration kieServerIntegration;

    @Mock
    JobServicesClient jobServicesClient;

    @Mock
    ProcessAdminServicesClient processAdminServicesClient;

    @InjectMocks
    RemoteExecutorServiceImpl executorService;

    @Before
    public void init() {
        final KieServicesClient servicesClient = mock(KieServicesClient.class);
        when(servicesClient.getServicesClient(JobServicesClient.class)).thenReturn(jobServicesClient);
        when(servicesClient.getServicesClient(ProcessAdminServicesClient.class)).thenReturn(processAdminServicesClient);
        when(kieServerIntegration.getServerClient(nullable(String.class))).thenReturn(servicesClient);
    }

    @Test
    public void testGetRequestDetails() {
        final RequestInfoInstance ri = newRequestInfoInstance();

        when(jobServicesClient.getRequestById("",
                                              ri.getId(),
                                              true,
                                              true)).thenReturn(ri);

        final RequestDetails rd = executorService.getRequestDetails("",
                                                                    "",
                                                                    ri.getId());
        assertRequestDetails(ri,
                             rd);
    }

    @Test
    public void testGetRequestDetailsEmpty() {
        final Long requestId = 1l;
        when(jobServicesClient.getRequestById("",
                                              requestId,
                                              true,
                                              true)).thenReturn(new RequestInfoInstance());

        final RequestDetails requestDetails = executorService.getRequestDetails("",
                                                                                "",
                                                                                requestId);
        assertNotNull(requestDetails);
        assertNotNull(requestDetails.getRequest());
        assertTrue(requestDetails.getErrors().isEmpty());
        assertTrue(requestDetails.getParams().isEmpty());
    }

    @Test
    public void testGetRequestDetailsNull() {
        final RequestDetails requestDetails = executorService.getRequestDetails(null,
                                                                                null,
                                                                                null);
        assertNull(requestDetails);
    }

    @Test
    public void testAcknowledgeError() {
        String serverTemplateId = "testServerTemplateId";
        String deploymentId = "testDeploymentId";
        String errorId = "errorId";

        executorService.acknowledgeError(serverTemplateId,
                                         deploymentId,
                                         errorId);

        verify(kieServerIntegration).getServerClient(serverTemplateId);
        verify(processAdminServicesClient).acknowledgeError(deploymentId,
                                                            errorId);
    }

    @Test
    public void testGetExecutionErrorDetails() {
        final ExecutionErrorInstance errorInstance = createTestError("1");
        String serverTemplateId = "testServerTemplateId";

        when(processAdminServicesClient.getError(errorInstance.getContainerId(),
                                                 errorInstance.getErrorId()))
                .thenReturn(errorInstance);

        final ExecutionErrorSummary errorSummary =
                executorService.getError(serverTemplateId,
                                         errorInstance.getContainerId(),
                                         errorInstance.getErrorId());

        verify(kieServerIntegration).getServerClient(serverTemplateId);
        verify(processAdminServicesClient).getError(errorInstance.getContainerId(),
                                                    errorInstance.getErrorId());
        assertExecutionErrorSummary(errorInstance,
                                    errorSummary);
    }
}
