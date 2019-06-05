/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.backend.server.util;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.VariableInstance;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VariableHelperTest {

    private final String deploymentId = "deploymentId";
    private final String serverTemplateId = "serverTemplateId";
    private final String processId = "processId";
    private final Long processInstanceId = 1L;

    private String processDefinitionVar1Name = "var1";
    private String processDefinitionVar2Name = "var2";
    private String processDefinitionVar3Name = "var3";
    private String processDefaultVar_name = "initiator";

    private String processDefinitionVar1Value = "value1";
    private String processDefaultVarValue = "testUser";
    private String processDefaultExcludedVarName = "processId";
    private List<VariableInstance> variables;
    private Map<String, String> properties;

    @Before
    public void setUp() {
        variables = Arrays.asList(VariableInstance.builder().processInstanceId(processInstanceId)
                                          .name(processDefinitionVar1Name).value(processDefinitionVar1Value)
                                          .date(new Date()).build(),
                                  VariableInstance.builder().processInstanceId(processInstanceId)
                                          .name(processDefaultVar_name).value(processDefaultVarValue)
                                          .date(new Date()).build(),
                                  VariableInstance.builder().processInstanceId(processInstanceId)
                                          .name(processDefaultExcludedVarName).value(processId)
                                          .date(new Date()).build());
        properties = new HashMap<>();
        properties.put(processDefinitionVar1Name, "String");
        properties.put(processDefinitionVar2Name, "String");
        properties.put(processDefinitionVar3Name, "String");
    }

    @Test
    public void addDeploymentIdServerTemplateToAllProcessInstancesVarsTest() {

        List<ProcessVariableSummary> varList =
                VariableHelper.adaptCollection(variables, properties, processInstanceId, deploymentId, serverTemplateId);

        //Check all ProcessVariableSummary have deploymentId and serverTemplateId
        varList.stream().forEach(processVariableSummary -> {
            assertEquals(deploymentId, processVariableSummary.getDeploymentId());
            assertEquals(serverTemplateId, processVariableSummary.getServerTemplateId());
        });

        assertEquals(4, varList.size()); //excludedVariables not included ("processId")
        assertEquals(processDefaultVar_name, varList.get(0).getName());
        assertEquals(processDefinitionVar1Name, varList.get(1).getName());
        assertEquals(processDefinitionVar2Name, varList.get(2).getName());
        assertEquals(processDefinitionVar3Name, varList.get(3).getName());

        //only variables included at variable instance list update the newValue
        assertEquals(processDefaultVarValue, varList.get(0).getNewValue());
        assertEquals(processDefinitionVar1Value, varList.get(1).getNewValue());
        assertEquals("", varList.get(2).getNewValue());
        assertEquals("", varList.get(3).getNewValue());
    }
}