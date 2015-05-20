package org.jbpm.console.ng.asset.backend.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class AssetMgmtDeploymentUnitProviderTest extends GuvnorM2Repository {

    @Test
    public void testGetDeploymentUnits() throws Exception { 
       AssetMgmtDeploymentUnitProvider provider = new AssetMgmtDeploymentUnitProvider();
      
       // setup provider with spy
       GuvnorM2Repository repo = new GuvnorM2Repository();
       repo.init();
       GuvnorM2Repository repoSpy = Mockito.spy(repo);
       provider.setM2Repository(repoSpy); 
    
       ArgumentCaptor<String> fileNameCaptor = 
               ArgumentCaptor.forClass(String.class);
       
       // run method
       provider.getDeploymentUnits();

       verify(repoSpy, times(1)).getFileName(fileNameCaptor.capture());

       String fileNameArg = fileNameCaptor.getValue();
       assertTrue( "Incorrect file name: " + fileNameArg, 
               fileNameArg.contains("guvnor-asset-mgmt-project"));
    }
}