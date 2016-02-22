/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.ht.backend.server;

import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.console.ng.ht.model.TaskDataSetConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    DataSetDefRegistry dataSetDefRegistryMock;

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    @Test
    public void registerDataSetDefinitionsTest() {
        dataSetsBootstrap.registerDataSetDefinitions();
        ArgumentCaptor<DataSetDef> argument = ArgumentCaptor.forClass(DataSetDef.class);
        verify(dataSetDefRegistryMock, times(4)).registerDataSetDef(argument.capture());

        List<DataSetDef> dsList = argument.getAllValues();
        assertEquals(dsList.size(), 4);
        assertEquals(dsList.get(0).getUUID(), HUMAN_TASKS_DATASET);
        assertEquals(dsList.get(1).getUUID(), HUMAN_TASKS_WITH_USER_DATASET);
        assertEquals(dsList.get(2).getUUID(), HUMAN_TASKS_WITH_ADMIN_DATASET);
        assertEquals(dsList.get(3).getUUID(), HUMAN_TASKS_WITH_VARIABLES_DATASET);
    }

    @Test
    public void noOrderByPresentInDefinitionsSQLTest() {
        dataSetsBootstrap.registerDataSetDefinitions();
        ArgumentCaptor<DataSetDef> argument = ArgumentCaptor.forClass(DataSetDef.class);
        verify(dataSetDefRegistryMock, times(4)).registerDataSetDef(argument.capture());

        List<DataSetDef> dsList = argument.getAllValues();
        assertNull(((SQLDataSetDef) dsList.get(0)).getDbSQL());
        assertFalse(((SQLDataSetDef) dsList.get(1)).getDbSQL().contains("order by"));
        assertFalse(((SQLDataSetDef) dsList.get(2)).getDbSQL().contains("order by"));
        assertFalse(((SQLDataSetDef) dsList.get(3)).getDbSQL().contains("order by"));

    }

    @Test
    public void columnAliasPresentInHumanTaskWithUserDataSet() {
        dataSetsBootstrap.registerDataSetDefinitions();
        ArgumentCaptor<DataSetDef> argument = ArgumentCaptor.forClass(DataSetDef.class);
        verify(dataSetDefRegistryMock, times(4)).registerDataSetDef(argument.capture());

        List<DataSetDef> dsList = argument.getAllValues();

        String strSQL = ((SQLDataSetDef) dsList.get(1)).getDbSQL();
        String[] columnsStr = strSQL.substring(strSQL.indexOf("select") + 6, strSQL.indexOf("from")).split(",");
        for (String columnStr : columnsStr) {
            final String[] split = columnStr.trim().split(" ");
            assertEquals(3, split.length);
            assertEquals("as", split[1]);
            final String alias = split[0].substring(split[0].indexOf(".") + 1).toUpperCase();
            assertEquals(split[2], "\"" + alias + "\"");
        }

    }

}
