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
package org.jbpm.console.ng.pr.backend.server;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.persistence.settings.JpaSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.console.ng.pr.model.ProcessInstanceDataSetConstants.*;
import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefsBootstrapTest {

    @Mock
    DeploymentRolesManager deploymentRolesManager;

    @Mock
    IdentityProvider identityProvider;

    @Mock
    JpaSettings jpaSettings;

    @Spy
    DeploymentIdsPreprocessor deploymentIdsPreprocessor;

    @Spy
    DataSetDefRegistry dataSetRegistry = DataSetCore.get().getDataSetDefRegistry();

    @Spy
    DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();

    @InjectMocks
    DataSetDefsBootstrap dataSetsBootstrap;

    List<String> deploymentIds = Arrays.asList("role1", "role2");

    @Before
    public void setUp() {
        // The two lines below is Mockito's issue work-around:
        // Can not use @_InjectMocks together with a @Spy annotation => https://github.com/mockito/mockito/issues/169
        deploymentIdsPreprocessor.deploymentRolesManager = deploymentRolesManager;
        deploymentIdsPreprocessor.identityProvider = identityProvider;

        dataSetsBootstrap.registerDataSetDefinitions();
        when(deploymentRolesManager.getDeploymentsForUser(identityProvider)).thenReturn(deploymentIds);
    }

    @Test
    public void registerDataSetDefsTest() {
        ArgumentCaptor<DataSetDef> argument = ArgumentCaptor.forClass(DataSetDef.class);
        verify(dataSetRegistry, times(2)).registerDataSetDef(argument.capture());

        List<DataSetDef> dataSetDefList = argument.getAllValues();
        assertEquals(dataSetDefList.size(), 2);
        assertEquals(dataSetDefList.get(0).getUUID(), PROCESS_INSTANCE_DATASET);
        assertEquals(dataSetDefList.get(1).getUUID(), PROCESS_INSTANCE_WITH_VARIABLES_DATASET);
    }

    @Test
    public void procInstancesPreprocessorTest() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(PROCESS_INSTANCE_DATASET)
                .buildLookup();

        dataSetManager.lookupDataSet(lookup);
        ArgumentCaptor<DataSetLookup> argument = ArgumentCaptor.forClass(DataSetLookup.class);

        verify(deploymentIdsPreprocessor).preprocess(lookup);
        verify(dataSetManager).lookupDataSet(argument.capture());
        assertEquals(argument.getValue(), DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(PROCESS_INSTANCE_DATASET)
                .filter(in(COLUMN_EXTERNALID, deploymentIds))
                .buildLookup());
    }
}
