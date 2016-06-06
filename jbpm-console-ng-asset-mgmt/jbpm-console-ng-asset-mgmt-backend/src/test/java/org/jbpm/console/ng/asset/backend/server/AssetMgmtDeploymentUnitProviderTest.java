/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.asset.backend.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class AssetMgmtDeploymentUnitProviderTest {

    /**
     * This test makes sure that the {@link AssetMgmtDeploymentUnitProvider#getDeploymentUnits()}
     * does what it should.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    public void testGetDeploymentUnits() throws Exception {
       AssetMgmtDeploymentUnitProvider provider = new AssetMgmtDeploymentUnitProvider();

       // setup provider with spy
       GuvnorM2Repository repo = new GuvnorM2Repository();
       repo.init();
       GuvnorM2Repository repoSpy = Mockito.spy(repo);
       provider.setM2Repository(repoSpy);

       // Create argument captor
       ArgumentCaptor<GAV> fileNameCaptor =
               ArgumentCaptor.forClass(GAV.class);

       // run method
       Set<DeploymentUnit> units = provider.getDeploymentUnits();

       // verify that GuvnorM2Repository instance was called (and that guvnor-asset-mgmt.properties file
       // is on classpath)
       // When the a method is called on the spy, it automatically stores the arg values
       // -- you just need to add a captor.capture() to the verify statement
       //    to retrieve the value later
       verify(repoSpy, times(1)).containsArtifact(fileNameCaptor.capture());

       // Was the guvnorm2Repository.getFileName(..) method called
       //  when we called provider.getDeploymentUnits() ?
       GAV fileNameArg = fileNameCaptor.getValue();
       assertTrue( "Incorrect file name: " + fileNameArg,
               fileNameArg != null && fileNameArg.getArtifactId().equals("guvnor-asset-mgmt-project"));

       // verify that the return value is not empty
       assertNotNull( "Null deployment units set", units);
       assertNotNull( "Empty deployment units set", units.isEmpty());
       String gavId = units.iterator().next().getIdentifier();
       assertTrue( "Unexpected GAV id:" + gavId, gavId.contains("guvnor-asset-mgmt"));
    }
}