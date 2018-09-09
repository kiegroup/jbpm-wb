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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import org.jbpm.workbench.common.model.PortableQueryFilter;
import org.jbpm.workbench.common.model.QueryFilter;
import org.jbpm.workbench.ks.integration.KieServerIntegration;
import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.server.api.model.definition.VariablesDefinition;
import org.kie.server.api.model.instance.VariableInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.paging.PageResponse;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteProcessVariablesServiceImplTest {

    private final String containerId = "containerId";
    private final String serverTemplateId = "serverTemplateId";
    private final String processId = "processId";
    private final String processInstanceId = "1";

    @Mock
    private KieServerIntegration kieServerIntegration;

    @Mock
    private QueryServicesClient queryServicesClient;

    @Mock
    private ProcessServicesClient processServicesClient;

    private QueryFilter queryFilter;

    @InjectMocks
    private RemoteProcessVariablesServiceImpl processVariablesService;

    @Before
    public void setup() {
        final KieServicesClient kieServicesClient = mock(KieServicesClient.class);
        when(kieServerIntegration.getServerClient(any())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
        when(kieServicesClient.getServicesClient(ProcessServicesClient.class)).thenReturn(processServicesClient);

        HashMap<String, Object> params = new HashMap<>();
        params.put("serverTemplateId",
                   serverTemplateId);
        params.put("deploymentId",
                   containerId);
        params.put("processInstanceId",
                   processInstanceId);
        params.put("processDefId",
                   processId);
        queryFilter = new PortableQueryFilter(0,
                                              10,
                                              false,
                                              "",
                                              "",
                                              false,
                                              null,
                                              params);
    }

    @Test
    public void getProcessVariablesTestWithContainerStarted() {
        String var1 = "var1";
        String var1_value = "valueVar1";
        String var2 = "var2";

        HashMap processDefVars = new HashMap();
        processDefVars.put(var1,
                           "");
        processDefVars.put(var2,
                           "");

        VariablesDefinition variablesDefinition = new VariablesDefinition(processDefVars);

        VariableInstance variableInstance =
                VariableInstance.builder()
                        .name(var1)
                        .value(var1_value)
                        .processInstanceId(Long.valueOf(processInstanceId))
                        .date(new Date())
                        .build();
        when(queryServicesClient.findVariablesCurrentState(any())).thenReturn(singletonList(variableInstance));

        when(processServicesClient.getProcessVariableDefinitions(containerId,
                                                                 processId)).thenReturn(variablesDefinition);

        List<ProcessVariableSummary> processInstanceVariables = processVariablesService.getProcessVariables(queryFilter);

        verify(processServicesClient).getProcessVariableDefinitions(containerId,
                                                                    processId);
        verify(queryServicesClient).findVariablesCurrentState(Long.valueOf(processInstanceId));
        assertEquals(2,
                     processInstanceVariables.size());
        assertEquals(var1,
                     processInstanceVariables.get(0).getName());
        assertEquals(var1_value,
                     processInstanceVariables.get(0).getNewValue());
        assertEquals(var2,
                     processInstanceVariables.get(1).getName());
        assertEquals("",
                     processInstanceVariables.get(1).getNewValue());
    }

    @Test
    public void getProcessVariablesTestWithContainerStopped() {

        String var1 = "var1";
        String var1_value = "valueVar1";

        VariableInstance variableInstance =
                VariableInstance.builder()
                        .name(var1)
                        .value(var1_value)
                        .processInstanceId(Long.valueOf(processInstanceId))
                        .date(new Date())
                        .build();
        when(processServicesClient.getProcessVariableDefinitions(containerId,
                                                                 processId)).thenThrow(new KieServicesHttpException());
        when(queryServicesClient.findVariablesCurrentState(any())).thenReturn(singletonList(variableInstance));

        List<ProcessVariableSummary> processInstanceVariables = processVariablesService.getProcessVariables(queryFilter);

        verify(processServicesClient).getProcessVariableDefinitions(containerId,
                                                                    processId);
        verify(queryServicesClient).findVariablesCurrentState(Long.valueOf(processInstanceId));
        assertEquals(1,
                     processInstanceVariables.size());
        assertEquals(var1,
                     processInstanceVariables.get(0).getName());
        assertEquals(var1_value,
                     processInstanceVariables.get(0).getNewValue());
    }

    @Test
    public void testGetData() {
        String var1 = "var1";
        String var1_value = "valueVar1";
        VariablesDefinition variablesDefinition = new VariablesDefinition(singletonMap("var1",
                                                                                       ""));

        when(processServicesClient.getProcessVariableDefinitions(containerId,
                                                                 processId)).thenReturn(variablesDefinition);

        VariableInstance variableInstance =
                VariableInstance.builder()
                        .name(var1)
                        .value(var1_value)
                        .processInstanceId(Long.valueOf(processInstanceId))
                        .date(new Date())
                        .build();

        when(queryServicesClient.findVariablesCurrentState(any())).thenReturn(singletonList(variableInstance));

        final PageResponse<ProcessVariableSummary> response = processVariablesService.getData(queryFilter);

        assertEquals(1,
                     response.getTotalRowSize());
        assertEquals(0,
                     response.getStartRowIndex());
        assertTrue(response.isTotalRowSizeExact());
        assertTrue(response.isFirstPage());
        assertTrue(response.isLastPage());
    }

    @Test
    public void testGetDataPaginated() {
        int totalItems = 12;

        final HashMap<String, String> variables = new HashMap<>();
        IntStream.range(0,
                        totalItems).forEach(i -> variables.put("var_" + i,
                                                               ""));
        VariablesDefinition variablesDefinition = new VariablesDefinition(variables);

        when(processServicesClient.getProcessVariableDefinitions(containerId,
                                                                 processId)).thenReturn(variablesDefinition);

        final PageResponse<ProcessVariableSummary> response = processVariablesService.getData(queryFilter);

        assertEquals(totalItems,
                     response.getTotalRowSize());
        assertEquals(0,
                     response.getStartRowIndex());
        assertTrue(response.isTotalRowSizeExact());
        assertTrue(response.isFirstPage());
        assertFalse(response.isLastPage());
    }
}