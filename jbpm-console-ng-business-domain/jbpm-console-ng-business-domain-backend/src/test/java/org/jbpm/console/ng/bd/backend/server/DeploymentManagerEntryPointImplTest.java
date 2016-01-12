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
package org.jbpm.console.ng.bd.backend.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentManagerEntryPointImplTest {

    @Mock
    private DeploymentService deploymentService;
    @Mock
    private GuvnorM2Repository guvnorM2Repository;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Event<BuildResults> buildResultsEvent;

    @InjectMocks
    private DeploymentManagerEntryPointImpl deploymentManager;

    @Before
    public void setUp() {
        // call @PostConstruct hook
        deploymentManager.configure();
    }

    /**
     * Test coverage for BZ-1171810.
     */
    @Test
    public void testDuplicateDeployment() {
        // pretend any deployment unit is deployed
        when(deploymentService.isDeployed(anyString())).thenReturn(true);

        // process a successful build result
        GAV gav = new GAV("g:a:1");
        BuildResults result = new BuildResults(gav);
        deploymentManager.process(result);

        // deployment unit with the build result GAV must not be deployed (it has already been deployed)
        verify(deploymentService, atLeast(1)).isDeployed("g:a:1");
        verify(deploymentService, never()).undeploy(any(DeploymentUnit.class));
        verify(deploymentService, never()).deploy(any(DeploymentUnit.class));

        // check error messages added to the build result
        assertNotEquals(0, result.getErrorMessages().size());
        for (BuildMessage msg : result.getErrorMessages()) {
            assertTrue(msg.getText(), msg.getText().contains("already deployed"));
        }
    }
}
