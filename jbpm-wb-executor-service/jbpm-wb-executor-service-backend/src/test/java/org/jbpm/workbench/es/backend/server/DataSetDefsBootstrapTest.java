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
package org.jbpm.workbench.es.backend.server;

import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.jbpm.workbench.es.model.RequestDataSetConstants.*;
import static org.jbpm.workbench.es.model.ExecutionErrorDataSetConstants.*;

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
        verify(dataSetDefRegistryMock,
               times(2)).registerDataSetDef(argument.capture());

        List<DataSetDef> dataSetDefList = argument.getAllValues();
        assertEquals(dataSetDefList.size(),
                     2);
        assertEquals(dataSetDefList.get(0).getUUID(),
                     REQUEST_LIST_DATASET);
        assertEquals(dataSetDefList.get(1).getUUID(),
                     EXECUTION_ERROR_LIST_DATASET);
    }
}
