package org.jbpm.console.ng.pr.backend.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.pr.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.pr.service.Initializable;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;

@Service
@ApplicationScoped
public class DeploymentManagerEntryPointImpl implements DeploymentManagerEntryPoint, Initializable {

    @Inject
    private DeploymentService deploymentService;

    @Inject
    @RequestScoped
    private Set<DeploymentUnit> deploymentUnits;


    @Override
    public void initDeployments(Set<DeploymentUnit> deploymentUnits) {
        for (DeploymentUnit unit : deploymentUnits) {
            if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
                cleanup(unit.getIdentifier());
                deploymentService.deploy(unit);
            }
        }
    }

    public void redeploy() {
        for (DeploymentUnit unit : deploymentUnits) {
            if (deploymentService.getDeployedUnit(unit.getIdentifier()) != null) {
                deploymentService.undeploy(unit);
            }
            deploymentService.deploy(unit);
        }
    }
    
    protected void cleanup(final String identifier) {
        String location = System.getProperty("jbpm.data.dir", System.getProperty("jboss.server.data.dir"));
        if (location == null) {
            location = System.getProperty("java.io.tmpdir");
        }
        File dataDir = new File(location);
        if (dataDir.exists()) {
            
            String[] jbpmSerFiles = dataDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.equals(identifier + "-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                new File(dataDir, file).delete();
            }
        }
    }

}
