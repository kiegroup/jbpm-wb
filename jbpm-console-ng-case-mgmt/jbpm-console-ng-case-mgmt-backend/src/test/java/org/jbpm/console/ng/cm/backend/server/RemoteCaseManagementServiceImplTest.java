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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jbpm.console.ng.bd.integration.KieServerIntegration;
import org.jbpm.console.ng.cm.model.CaseDefinitionSummary;
import org.jbpm.console.ng.cm.model.CaseInstanceSummary;
import org.jbpm.console.ng.cm.service.CaseManagementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.cases.CaseDefinition;
import org.kie.server.api.model.cases.CaseInstance;
import org.kie.server.client.CaseServicesClient;
import org.kie.server.client.KieServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteCaseManagementServiceImplTest {

    @Mock
    KieServicesClient kieServicesClient;

    @Mock
    CaseServicesClient caseServicesClient;

    @Mock
    KieServerIntegration kieServerIntegration;

    @InjectMocks
    RemoteCaseManagementServiceImpl service;

    @Before
    public void setup() {
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServerIntegration.getServerClient(anyString(), anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(CaseServicesClient.class)).thenReturn(caseServicesClient);
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = CaseManagementService.class.getMethods();
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
    public void testGetCaseDefinitions() throws Exception {
        final CaseDefinition definition = new CaseDefinition();
        definition.setIdentifier("org.jbpm.case");
        definition.setName("New case");
        definition.setContainerId("org.jbpm");
        when(caseServicesClient.getCaseDefinitions(anyInt(), anyInt())).thenReturn(Collections.singletonList(definition));

        final List<CaseDefinitionSummary> definitions = service.getCaseDefinitions("id", 0, 0);
        assertNotNull(definitions);
        assertEquals(1, definitions.size());
        final CaseDefinitionSummary caseDefinitionSummary = definitions.get(0);
        assertEquals(definition.getName(), caseDefinitionSummary.getName());
        assertEquals(definition.getIdentifier(), caseDefinitionSummary.getId());
        assertEquals(definition.getContainerId(), caseDefinitionSummary.getContainerId());
    }

    @Test
    public void testGetCaseInstances() throws Exception {
        final CaseInstance instance = new CaseInstance();
        instance.setCaseDescription("New case");
        instance.setCaseId("CASE-1");
        instance.setCaseStatus(1);
        instance.setContainerId("org.jbpm");
        when(caseServicesClient.getCaseInstances(anyInt(), anyInt())).thenReturn(Collections.singletonList(instance));

        final List<CaseInstanceSummary> instances = service.getCaseInstances("id", 0, 0);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        final CaseInstanceSummary caseInstanceSummary = instances.get(0);
        assertEquals(instance.getCaseDescription(), caseInstanceSummary.getDescription());
        assertEquals(instance.getCaseId(), caseInstanceSummary.getId());
        assertEquals(instance.getCaseStatus(), caseInstanceSummary.getStatus());
        assertEquals(instance.getContainerId(), caseInstanceSummary.getContainerId());
        assertTrue(caseInstanceSummary.isActive());
    }

    @Test
    public void testStartCaseInstance() throws Exception {
        final String caseDefinitionId = "org.jbpm";
        final String container = "container";

        service.startCaseInstance("server", container, caseDefinitionId);

        verify(caseServicesClient).startCase(container, caseDefinitionId);
    }

    @Test
    public void testCancelCaseInstance() throws Exception {
        final String caseId = "CASE-1";
        final String container = "container";

        service.cancelCaseInstance("server", container, caseId);

        verify(caseServicesClient).cancelCaseInstance(container, caseId);
    }

    @Test
    public void testDestroyCaseInstance() throws Exception {
        final String caseId = "CASE-1";
        final String container = "container";

        service.destroyCaseInstance("server", container, caseId);

        verify(caseServicesClient).destroyCaseInstance(container, caseId);
    }

}