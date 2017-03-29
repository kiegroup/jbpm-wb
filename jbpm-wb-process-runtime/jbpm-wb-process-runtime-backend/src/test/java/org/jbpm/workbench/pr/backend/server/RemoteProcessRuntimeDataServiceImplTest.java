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

package org.jbpm.workbench.pr.backend.server;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static java.util.Collections.singletonList;
import static org.jbpm.workbench.pr.backend.server.ProcessSummaryMapperTest.assertProcessSummary;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessRuntimeDataServiceImplTest {

    @Mock
    private KieServerIntegration kieServerIntegration;

    @Mock
    private QueryServicesClient queryServicesClient;

    @Mock
    private ProcessServicesClient processServicesClient;

    @InjectMocks
    private RemoteProcessRuntimeDataServiceImpl service;

    private final String processId = "processId";
    private final String containerId = "containerId";
    private final String serverTemplateId = "serverTemplateId";

    @Before
    public void setup(){
        final KieServicesClient kieServicesClient = mock(KieServicesClient.class);
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
        when(kieServicesClient.getServicesClient(ProcessServicesClient.class)).thenReturn(processServicesClient);
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = ProcessRuntimeDataService.class.getMethods();
        for (Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            final Object[] args = new Object[method.getParameterCount()];
            Object result = method.invoke(service, args);

            assertMethodResult(method, returnType, result);

            args[0] = "";
            result = method.invoke(service, args);
            assertMethodResult(method, returnType, result);
        }
    }

    private void assertMethodResult(final Method method, final Class<?> returnType, final Object result) {
        if (Collection.class.isAssignableFrom(returnType)) {
            assertNotNull(format("Returned collection for method %s should not be null", method.getName()), result);
            assertTrue(format("Returned collection for method %s should be empty", method.getName()), ((Collection) result).isEmpty());
        } else {
            assertNull(format("Returned object for method %s should be null", method.getName()), result);
        }
    }

    @Test
    public void testGetProcesses(){
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).build();

        when(queryServicesClient.findProcesses(0, 10, "", true)).thenReturn(singletonList(def));

        final List<ProcessSummary> summaries = service.getProcesses(serverTemplateId, 0, 10, "", true);

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertProcessSummary(def, summaries.get(0));
    }

    @Test
    public void testGetProcessesByFilter(){
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).build();

        when(queryServicesClient.findProcesses("filter", 0, 10, "", true)).thenReturn(singletonList(def));

        final List<ProcessSummary> summaries = service.getProcessesByFilter(serverTemplateId, "filter", 0, 10, "", true);

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertProcessSummary(def, summaries.get(0));
    }

    @Test
    public void testGetProcess(){
        final ProcessDefinition def = ProcessDefinition.builder().id(processId).build();

        when(processServicesClient.getProcessDefinition(containerId, processId)).thenReturn(def);

        final ProcessDefinitionKey pdk = new ProcessDefinitionKey(serverTemplateId, containerId, processId);

        final ProcessSummary summary = service.getProcess(serverTemplateId, pdk);

        assertNotNull(summary);
        assertProcessSummary(def, summary);
    }

}