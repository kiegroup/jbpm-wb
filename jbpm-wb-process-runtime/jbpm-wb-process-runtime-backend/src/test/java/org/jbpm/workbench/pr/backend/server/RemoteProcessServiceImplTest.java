/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessServiceImplTest {

    private static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    private ProcessServicesClient processServicesClientMock;

    @Mock
    private KieServerIntegration kieServerIntegration;

    @InjectMocks
    private RemoteProcessServiceImpl remoteProcessService;

    @Before
    public void setup() {
        final KieServicesClient kieServicesClientMock = mock(KieServicesClient.class);
        processServicesClientMock = mock(ProcessServicesClient.class);
        when(kieServerIntegration.getServerClient(eq(SERVER_TEMPLATE_ID),
                                                  anyString())).thenReturn(kieServicesClientMock);
        when(kieServicesClientMock.getServicesClient(ProcessServicesClient.class)).thenReturn(processServicesClientMock);
    }

    @Test
    public void bulkAbortProcessInstancesTest_singleProcessInstance() {
        final String containerId = "containerId";
        final Long processInstanceId = 1L;

        remoteProcessService.abortProcessInstances(SERVER_TEMPLATE_ID,
                                                   singletonList(containerId),
                                                   singletonList(processInstanceId));
        verify(processServicesClientMock).abortProcessInstances(containerId,
                                                                singletonList(processInstanceId));
    }

    @Test
    public void bulkAbortProcessInstancesTest_multipleProcessInstancesOneContainer() {
        final String containerId = "containerId";
        final List<Long> processInstanceIds = new ArrayList<>(Arrays.asList(1L,
                                                                            2L));

        remoteProcessService.abortProcessInstances(SERVER_TEMPLATE_ID,
                                                   new ArrayList<>(Arrays.asList(containerId,
                                                                                 containerId)),
                                                   processInstanceIds);
        verify(processServicesClientMock).abortProcessInstances(containerId,
                                                                processInstanceIds);
    }

    @Test
    public void bulkAbortProcessInstancesTest_multipleProcessInstancesMultipleContainers() {
        final List<String> containerIds = new ArrayList<>(Arrays.asList("containerId_1",
                                                                        "containerId_2"));
        final List<Long> processInstanceIds = new ArrayList<>(Arrays.asList(1L,
                                                                            2L));

        remoteProcessService.abortProcessInstances(SERVER_TEMPLATE_ID,
                                                   containerIds,
                                                   processInstanceIds);
        verify(processServicesClientMock).abortProcessInstance(containerIds.get(0),
                                                               processInstanceIds.get(0));
        verify(processServicesClientMock).abortProcessInstance(containerIds.get(1),
                                                               processInstanceIds.get(1));
        verifyNoMoreInteractions(processServicesClientMock);
    }

    private final String signal = "signal";

    private final Object event = new Object();

    @Test
    public void bulkSignalProcessInstancesTest_singleProcessInstance() {
        final String containerId = "containerId";
        final Long processInstanceId = 1L;

        remoteProcessService.signalProcessInstances(SERVER_TEMPLATE_ID,
                                                    singletonList(containerId),
                                                    singletonList(processInstanceId),
                                                    signal,
                                                    event);
        verify(processServicesClientMock).signalProcessInstances(containerId,
                                                                 singletonList(processInstanceId),
                                                                 signal,
                                                                 event);
    }

    @Test
    public void bulkSignalProcessInstancesTest_multipleProcessInstancesOneContainer() {
        final String containerId = "containerId";
        final List<Long> processInstanceIds = new ArrayList<>(Arrays.asList(1L,
                                                                            2L));

        remoteProcessService.signalProcessInstances(SERVER_TEMPLATE_ID,
                                                    new ArrayList<>(Arrays.asList(containerId,
                                                                                 containerId)),
                                                    processInstanceIds,
                                                    signal,
                                                    event);
        verify(processServicesClientMock).signalProcessInstances(containerId,
                                                                 processInstanceIds,
                                                                 signal,
                                                                 event);
    }

    @Test
    public void bulkSignalProcessInstancesTest_multipleProcessInstancesMultipleContainers() {
        final List<String> containerIds = new ArrayList<>(Arrays.asList("containerId_1",
                                                                        "containerId_2"));
        final List<Long> processInstanceIds = new ArrayList<>(Arrays.asList(1L,
                                                                            2L));

        remoteProcessService.signalProcessInstances(SERVER_TEMPLATE_ID,
                                                    containerIds,
                                                    processInstanceIds,
                                                    signal,
                                                    event);
        verify(processServicesClientMock).signalProcessInstance(containerIds.get(0),
                                                                processInstanceIds.get(0),
                                                                signal,
                                                                event);
        verify(processServicesClientMock).signalProcessInstance(containerIds.get(1),
                                                                processInstanceIds.get(1),
                                                                signal,
                                                                event);
        verifyNoMoreInteractions(processServicesClientMock);
    }
}
