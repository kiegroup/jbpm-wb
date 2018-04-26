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

package org.jbpm.dashboard.renderer.backend;

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

import static org.jbpm.dashboard.renderer.model.DashboardData.DATASET_HUMAN_TASKS;
import static org.jbpm.dashboard.renderer.model.DashboardData.DATASET_PROCESS_INSTANCES;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    DataSetDefRegistry dataSetRegistry;

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    @Test
    public void testProcessInstancesDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(DATASET_PROCESS_INSTANCES).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(DATASET_PROCESS_INSTANCES,
                     dataSetDef.getUUID());
        assertEquals("target-" + DATASET_PROCESS_INSTANCES,
                     dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE,
                     dataSetDef.getProvider());
        assertEquals("SELECT *",
                     dataSetDef.getDbSQL());
        assertEquals(10,
                     dataSetDef.getColumns().size());
    }

    @Test
    public void testHumanTasksDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(DATASET_HUMAN_TASKS).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(DATASET_HUMAN_TASKS,
                     dataSetDef.getUUID());
        assertEquals("target-" + DATASET_HUMAN_TASKS,
                     dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE,
                     dataSetDef.getProvider());
        assertEquals("SELECT *",
                     dataSetDef.getDbSQL());
        assertEquals(11,
                     dataSetDef.getColumns().size());
    }
}
