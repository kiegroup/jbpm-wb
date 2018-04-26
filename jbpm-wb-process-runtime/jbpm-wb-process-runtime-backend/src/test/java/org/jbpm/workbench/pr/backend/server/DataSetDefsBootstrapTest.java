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

import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jbpm.workbench.ks.integration.KieServerDataSetProvider;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.definition.QueryDefinition;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.PROCESS_INSTANCE_DATASET;
import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.PROCESS_INSTANCE_WITH_VARIABLES_DATASET;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    DataSetDefRegistry dataSetRegistry;

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    @Test
    public void testProcessInstanceDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(PROCESS_INSTANCE_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(PROCESS_INSTANCE_DATASET,
                     dataSetDef.getUUID());
        assertEquals("target-" + PROCESS_INSTANCE_DATASET,
                     dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE,
                     dataSetDef.getProvider());
        assertEquals("SELECT *",
                     dataSetDef.getDbSQL());
        assertEquals(18,
                     dataSetDef.getColumns().size());
    }

    @Test
    public void testProcessInstanceWithVariablesDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(PROCESS_INSTANCE_WITH_VARIABLES_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(PROCESS_INSTANCE_WITH_VARIABLES_DATASET,
                     dataSetDef.getUUID());
        assertEquals("target-" + PROCESS_INSTANCE_WITH_VARIABLES_DATASET,
                     dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE,
                     dataSetDef.getProvider());
        assertEquals("SELECT *",
                     dataSetDef.getDbSQL());
        assertEquals(5,
                     dataSetDef.getColumns().size());
    }
}
