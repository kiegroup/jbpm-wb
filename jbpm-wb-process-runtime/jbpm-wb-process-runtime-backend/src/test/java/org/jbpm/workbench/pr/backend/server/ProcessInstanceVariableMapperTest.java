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

package org.jbpm.workbench.pr.backend.server;

import java.util.Date;

import org.jbpm.workbench.pr.model.ProcessVariableSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.instance.VariableInstance;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ProcessInstanceVariableMapperTest {

    private String deploymentId = "deploymentId";
    private String serverTemplateId = "serverTemplateId";
    private String varType = "String";

    public static void assertVariableInstance(final VariableInstance vi,
                                              final ProcessVariableSummary pivs) {
        assertNotNull(vi);

        assertEquals(vi.getVariableName(), pivs.getName());
        assertEquals(vi.getProcessInstanceId(), Long.valueOf(pivs.getProcessInstanceId()));
        assertEquals(vi.getOldValue(), pivs.getOldValue());
        assertEquals(vi.getValue(), pivs.getNewValue());
        assertEquals(vi.getDate().getTime(), pivs.getTimestamp());
    }

    @Test
    public void testProcessInstanceVariableMapperTest() {

        final VariableInstance vi = VariableInstance.builder()
                .name("variableName")
                .processInstanceId(1L)
                .value("variableNewValue")
                .oldValue("variableOldValue")
                .date(new Date())
                .build();

        final ProcessVariableSummary pivs = new ProcessInstanceVariableMapper(deploymentId, serverTemplateId, varType).apply(vi);

        assertVariableInstance(vi, pivs);
        assertEquals(deploymentId, pivs.getDeploymentId());
        assertEquals(serverTemplateId, pivs.getServerTemplateId());
        assertEquals(varType, pivs.getType());
    }

    @Test
    public void testProcessInstanceVariableMapperMapNull() {
        final VariableInstance vi = null;
        final ProcessVariableSummary pivs = new ProcessInstanceVariableMapper(deploymentId, serverTemplateId, "").apply(vi);
        assertNull(pivs);
    }
}
