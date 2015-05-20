package org.jbpm.console.ng.asset.backend.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

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
       ArgumentCaptor<String> fileNameCaptor = 
               ArgumentCaptor.forClass(String.class);
       
       // run method
       Set<DeploymentUnit> units = provider.getDeploymentUnits();

       // verify that GuvnorM2Repository instance was called (and that guvnor-asset-mgmt.properties file
       // is on classpath)
       // When the a method is called on the spy, it automatically stores the arg values
       // -- you just need to add a captor.capture() to the verify statement
       //    to retrieve the value later
       verify(repoSpy, times(1)).getFileName(fileNameCaptor.capture());

       // Was the guvnorm2Repository.getFileName(..) method called 
       //  when we called provider.getDeploymentUnits() ?
       String fileNameArg = fileNameCaptor.getValue();
       assertTrue( "Incorrect file name: " + fileNameArg, 
               fileNameArg != null && fileNameArg.contains("guvnor-asset-mgmt-project"));
       
       // verify that the return value is not empty
       assertNotNull( "Null deployment units set", units);
       assertNotNull( "Empty deployment units set", units.isEmpty());
       String gavId = units.iterator().next().getIdentifier();
       assertTrue( "Unexpected GAV id:" + gavId, gavId.contains("guvnor-asset-mgmt"));
    }
}