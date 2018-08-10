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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
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

    @Mock
    ServerTemplate serverTemplateMock;

    @InjectMocks
    private RemoteProcessVariablesServiceImpl processVariablesService;

    @Before
    public void setup() {
        final KieServicesClient kieServicesClient = mock(KieServicesClient.class);
        when(kieServerIntegration.getServerClient(anyString())).thenReturn(kieServicesClient);
        when(kieServicesClient.getServicesClient(QueryServicesClient.class)).thenReturn(queryServicesClient);
        when(kieServicesClient.getServicesClient(ProcessServicesClient.class)).thenReturn(processServicesClient);

        HashMap params = new HashMap();
        params.put("serverTemplateId",
                   serverTemplateId);
        params.put("deploymentId",
                   containerId);
        params.put("processInstanceId",
                   processInstanceId);
        params.put("processDefId",
                   processId);
        queryFilter = mock(QueryFilter.class);
        when(queryFilter.getParams()).thenReturn(params);
    }

    @Test
    public void getProcessVariablesTestWithContainerStarted() {
        String var1 = "var1";
        String var1_value = "valueVar1";
        String var2 = "var2";
        VariablesDefinition variablesDefinition = mock(VariablesDefinition.class);

        HashMap processDefVars = new HashMap();
        processDefVars.put(var1,
                           "");
        processDefVars.put(var2,
                           "");

        VariableInstance variableInstace =
                VariableInstance.builder()
                        .name(var1)
                        .value(var1_value)
                        .processInstanceId(Long.valueOf(processInstanceId))
                        .date(new Date())
                        .build();
        when(queryServicesClient.findVariablesCurrentState(anyLong())).thenReturn(Arrays.asList(variableInstace));

        when(variablesDefinition.getVariables()).thenReturn(processDefVars);
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

        VariableInstance variableInstace =
                VariableInstance.builder()
                        .name(var1)
                        .value(var1_value)
                        .processInstanceId(Long.valueOf(processInstanceId))
                        .date(new Date())
                        .build();
        when(processServicesClient.getProcessVariableDefinitions(containerId,
                                                                 processId)).thenThrow(new KieServicesHttpException());
        when(queryServicesClient.findVariablesCurrentState(anyLong())).thenReturn(Arrays.asList(variableInstace));

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
}