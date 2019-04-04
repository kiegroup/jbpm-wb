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
package org.jbpm.workbench.ht.backend.server;

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

import static org.jbpm.workbench.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    DataSetDefRegistry dataSetDefRegistry;

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    @Test
    public void testHumanTasksDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(HUMAN_TASKS_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetDefRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(HUMAN_TASKS_DATASET, dataSetDef.getUUID());
        assertEquals("target-" + HUMAN_TASKS_DATASET, dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE, dataSetDef.getProvider());
        assertEquals("SELECT *", dataSetDef.getDbSQL());
        assertEquals(21, dataSetDef.getColumns().size());
    }

    @Test
    public void testHumanTasksWithUserDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(HUMAN_TASKS_WITH_USER_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetDefRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(HUMAN_TASKS_WITH_USER_DATASET, dataSetDef.getUUID());
        assertEquals("target-" + HUMAN_TASKS_WITH_USER_DATASET, dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE, dataSetDef.getProvider());
        assertEquals("SELECT *", dataSetDef.getDbSQL());
        assertEquals(23, dataSetDef.getColumns().size());
    }

    @Test
    public void testHumanTasksWithAdminDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(HUMAN_TASKS_WITH_ADMIN_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetDefRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(HUMAN_TASKS_WITH_ADMIN_DATASET, dataSetDef.getUUID());
        assertEquals("target-" + HUMAN_TASKS_WITH_ADMIN_DATASET, dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE, dataSetDef.getProvider());
        assertEquals("SELECT *", dataSetDef.getDbSQL());
        assertEquals(23, dataSetDef.getColumns().size());
    }

    @Test
    public void testHumanTasksWithVariablesDataSet() {
        QueryDefinition qd = QueryDefinition.builder().name(HUMAN_TASKS_WITH_VARIABLES_DATASET).expression("SELECT *").source("source").target("target").build();
        dataSetsBootstrap.registerDataSetDefinitions(new QueryDefinitionLoaded(qd));

        ArgumentCaptor<SQLDataSetDef> argument = ArgumentCaptor.forClass(SQLDataSetDef.class);
        verify(dataSetDefRegistry).registerDataSetDef(argument.capture());

        SQLDataSetDef dataSetDef = argument.getValue();
        assertEquals(HUMAN_TASKS_WITH_VARIABLES_DATASET, dataSetDef.getUUID());
        assertEquals("target-" + HUMAN_TASKS_WITH_VARIABLES_DATASET, dataSetDef.getName());
        assertEquals(KieServerDataSetProvider.TYPE, dataSetDef.getProvider());
        assertEquals("SELECT *", dataSetDef.getDbSQL());
        assertEquals(3, dataSetDef.getColumns().size());
    }
}