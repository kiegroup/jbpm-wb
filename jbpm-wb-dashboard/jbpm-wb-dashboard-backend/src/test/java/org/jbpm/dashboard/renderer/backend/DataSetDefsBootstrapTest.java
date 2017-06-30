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

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.jbpm.dashboard.renderer.model.DashboardData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    IdentityProvider identityProvider;
    @Spy
    DataSetDefRegistry dataSetRegistry = DataSetCore.get().getDataSetDefRegistry();

    @Spy
    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    List<String> deploymentIds = Arrays.asList("role1",
                                               "role2");

    @Before
    public void setUp() {
        // The two lines below is Mockito's issue work-around:
        // Can not use @_InjectMocks together with a @Spy annotation => https://github.com/mockito/mockito/issues/169

        dataSetsBootstrap.registerDataSetDefinitions();
//        when(deploymentRolesManager.getDeploymentsForUser(identityProvider)).thenReturn(deploymentIds);
    }

    @Test
    public void registerDataSetDefsTest() {
        ArgumentCaptor<DataSetDef> argument = ArgumentCaptor.forClass(DataSetDef.class);
        verify(dataSetRegistry,
               times(2)).registerDataSetDef(argument.capture());

        List<DataSetDef> dataSetDefList = argument.getAllValues();
        assertEquals(dataSetDefList.size(),
                     2);
        assertEquals(dataSetDefList.get(0).getUUID(),
                     DATASET_PROCESS_INSTANCES);
        assertEquals(dataSetDefList.get(1).getUUID(),
                     DATASET_HUMAN_TASKS);
    }

    //TODO Needs redesign as data source is deployed to kie server
    @Ignore
    public void procInstancesPreprocessorTest() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(DATASET_PROCESS_INSTANCES)
                .buildLookup();

        dataSetManager.lookupDataSet(lookup);
        ArgumentCaptor<DataSetLookup> argument = ArgumentCaptor.forClass(DataSetLookup.class);

        verify(dataSetManager).lookupDataSet(argument.capture());
        assertEquals(argument.getValue(),
                     DataSetLookupFactory.newDataSetLookupBuilder()
                             .dataset(DATASET_PROCESS_INSTANCES)
                             .filter(in(COLUMN_PROCESS_EXTERNAL_ID,
                                        deploymentIds))
                             .buildLookup());
    }

    //TODO Needs redesign as data source is deployed to kie server
    @Ignore
    public void tasksPreprocessorTest() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(DATASET_HUMAN_TASKS)
                .buildLookup();

        dataSetManager.lookupDataSet(lookup);
        ArgumentCaptor<DataSetLookup> argument = ArgumentCaptor.forClass(DataSetLookup.class);

        verify(dataSetManager).lookupDataSet(argument.capture());
        assertEquals(argument.getValue(),
                     DataSetLookupFactory.newDataSetLookupBuilder()
                             .dataset(DATASET_HUMAN_TASKS)
                             .filter(in(COLUMN_PROCESS_EXTERNAL_ID,
                                        deploymentIds))
                             .buildLookup());
    }
}
