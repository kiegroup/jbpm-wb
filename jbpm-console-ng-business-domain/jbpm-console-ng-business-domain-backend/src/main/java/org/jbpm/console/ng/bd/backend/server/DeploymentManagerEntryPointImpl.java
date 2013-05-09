package org.jbpm.console.ng.bd.backend.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.model.DeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.bd.service.Initializable;
import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.uberfire.backend.deployment.DeploymentConfigService;


@Service
@ApplicationScoped
public class DeploymentManagerEntryPointImpl implements DeploymentManagerEntryPoint, Initializable {

    @Inject
    private DeploymentService deploymentService;
    @Inject
    @RequestScoped
    private Set<DeploymentUnit> deploymentUnits;
    @Inject
    private DeploymentConfigService deploymentConfigService;

    @Override
    public void initDeployments(Set<DeploymentUnit> deploymentUnits) {
        for (DeploymentUnit unit : deploymentUnits) {
            if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
                cleanup(unit.getIdentifier());
                deploymentService.deploy(unit);
            }
        }
    }

    @Override
    public void deploy(DeploymentUnitSummary unitSummary) {
        DeploymentUnit unit = null;
        if(unitSummary.getType().equals("kjar")){
           unit =  new KModuleDeploymentUnit(((KModuleDeploymentUnitSummary)unitSummary).getGroupId(), 
                                                ((KModuleDeploymentUnitSummary)unitSummary).getArtifactId(), 
                                                ((KModuleDeploymentUnitSummary)unitSummary).getVersion(), 
                                                ((KModuleDeploymentUnitSummary)unitSummary).getKbaseName(), 
                                                ((KModuleDeploymentUnitSummary)unitSummary).getKsessionName());
        }// add for vfs
        
        if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
            cleanup(unit.getIdentifier());
            deploymentService.deploy(unit);
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

    public void saveDeployment(@Observes @Deploy DeploymentEvent event) {
        if (deploymentConfigService.getDeployment(event.getDeploymentId()) == null) {
            deploymentConfigService.addDeployment(event.getDeploymentId(), event.getDeployedUnit().getDeploymentUnit());
        }
    }

    public void removeDeployment(@Observes @Undeploy DeploymentEvent event) {
        deploymentConfigService.removeDeployment(event.getDeploymentId());
    }

    @Override
    public List<String> getDeploymentUnits() {
        Collection<DeployedUnit> deployedUnits = deploymentService.getDeployedUnits();
        List<String> unitsIds = new ArrayList<String>(deployedUnits.size());
        for (DeployedUnit du : deployedUnits) {
            unitsIds.add(du.getDeploymentUnit().getIdentifier());

        }
        return unitsIds;
    }
}
