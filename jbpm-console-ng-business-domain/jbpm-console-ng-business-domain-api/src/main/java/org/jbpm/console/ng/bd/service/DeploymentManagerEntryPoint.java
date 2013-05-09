package org.jbpm.console.ng.bd.service;

import java.util.List;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.bd.model.DeploymentUnitSummary;

@Remote
public interface DeploymentManagerEntryPoint {

    void redeploy();
    
    List<String> getDeploymentUnits();
     
    void deploy(DeploymentUnitSummary unit);
   
}
