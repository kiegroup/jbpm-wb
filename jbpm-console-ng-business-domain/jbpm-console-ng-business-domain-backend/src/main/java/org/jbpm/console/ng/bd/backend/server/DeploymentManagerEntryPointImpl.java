package org.jbpm.console.ng.bd.backend.server;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.exception.DeploymentException;
import org.jbpm.console.ng.bd.model.DeploymentUnitSummary;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.bd.service.Initializable;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.deployment.DeploymentConfigService;
import org.uberfire.backend.server.config.Added;
import org.uberfire.backend.server.config.Removed;
import org.uberfire.backend.server.deployment.DeploymentConfigChangedEvent;

@Service
@ApplicationScoped
public class DeploymentManagerEntryPointImpl implements DeploymentManagerEntryPoint, Initializable<DeploymentUnit>, PostBuildHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentManagerEntryPointImpl.class);

    @Inject
    private DeploymentService deploymentService;

    @Inject
    @RequestScoped
    private Set<DeploymentUnit> deploymentUnits;

    @Inject
    private DeploymentConfigService deploymentConfigService;

    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @PostConstruct
    public void configure() {
        guvnorM2Repository.getRepositoryURL();
    }

    @Override
    public void initDeployments(Set<DeploymentUnit> deploymentUnits) {
        for (DeploymentUnit unit : deploymentUnits) {
            if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
                try {
                    if ("true".equals(System.getProperty("org.kie.clean.onstartup"))) {
                        cleanup(unit.getIdentifier());
                    }
                    deploymentService.deploy(unit);
                } catch (Exception e) {
                    logger.warn("Error when deploying unit {} error message {}", unit, e.getMessage());
                    logger.debug("Stacktrace:", e);
                }
            }
        }
    }

    @Override
    public void deploy(DeploymentUnitSummary unitSummary) {
        DeploymentUnit unit = null;
        if (unitSummary.getType().equals("kjar")) {
            unit = new KModuleDeploymentUnit(((KModuleDeploymentUnitSummary) unitSummary).getGroupId(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getArtifactId(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getVersion(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getKbaseName(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getKsessionName(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getStrategy());
        }// add for vfs
        deploy(unit);
    }

    protected void deploy(DeploymentUnit unit) {
        if (deploymentService.getDeployedUnit(unit.getIdentifier()) == null) {
            String[] gavElemes = unit.getIdentifier().split(":");
            GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
            BuildResults buildResults = new BuildResults(gav);

            try {
                deploymentService.deploy(unit);
            } catch (Exception e) {
                BuildMessage message = new BuildMessage();
                message.setLevel(BuildMessage.Level.ERROR);
                message.setText("Deployment of unit " + gav + " failed: " + e.getMessage());
                buildResults.addBuildMessage(message);
                throw new DeploymentException(e.getMessage(), e);
            } finally {
                buildResultsEvent.fire(buildResults);
            }
        }
    }
    
    @Override
    public void undeploy(DeploymentUnitSummary unitSummary) {
        DeploymentUnit unit = null;
        if (unitSummary.getType().equals("kjar")) {
            unit = new KModuleDeploymentUnit(((KModuleDeploymentUnitSummary) unitSummary).getGroupId(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getArtifactId(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getVersion(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getKbaseName(),
                    ((KModuleDeploymentUnitSummary) unitSummary).getKsessionName());
        }// add for vfs
        undeploy(unit);
    }

    protected void undeploy(DeploymentUnit unit) {
        String[] gavElemes = unit.getIdentifier().split(":");
        GAV gav = new GAV(gavElemes[0], gavElemes[1], gavElemes[2]);
        BuildResults buildResults = new BuildResults(gav);
        try {
            if (deploymentService.getDeployedUnit(unit.getIdentifier()) != null) {
                deploymentService.undeploy(unit);
                cleanup(unit.getIdentifier());

            }
        } catch (Exception e) {
            BuildMessage message = new BuildMessage();
            message.setLevel(BuildMessage.Level.ERROR);
            message.setText("Undeployment of unit " + gav + " failed: " + e.getMessage());
            buildResults.addBuildMessage(message);
            throw new DeploymentException(e.getMessage(), e);
        } finally {

            buildResultsEvent.fire(buildResults);
        }
    }

    @Override
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

    @Override
    public List<KModuleDeploymentUnitSummary> getDeploymentUnits() {
        Collection<DeployedUnit> deployedUnits = deploymentService.getDeployedUnits();
        List<KModuleDeploymentUnitSummary> unitsIds = new ArrayList<KModuleDeploymentUnitSummary>(deployedUnits.size());
        for (DeployedUnit du : deployedUnits) {
            KModuleDeploymentUnit kdu =  (KModuleDeploymentUnit)du.getDeploymentUnit();
            KModuleDeploymentUnitSummary duSummary = new KModuleDeploymentUnitSummary(kdu.getIdentifier(), kdu.getGroupId(),
                    kdu.getArtifactId(), kdu.getVersion(), kdu.getKbaseName(), kdu.getKsessionName(), kdu.getStrategy().toString());
            unitsIds.add(duSummary);

        }
        return unitsIds;
    }

    /**
     * Reacts on events fired by deployment service upon successful deployment to runtime environment so that can be stored in
     * system repository
     * @param event deploymentEvent that holds all required information from runtime point of view
     */
    public void saveDeployment(@Observes @Deploy DeploymentEvent event) {
        if (deploymentConfigService.getDeployment(event.getDeploymentId()) == null) {
            deploymentConfigService.addDeployment(event.getDeploymentId(), event.getDeployedUnit().getDeploymentUnit());
        }
    }

    /**
     * Reacts on events fired by deployment service upon successful undeployment from runtime environment
     * so that can be stored in system repository
     * @param event deploymentEvent that holds all required information from runtime point of view
     */
    public void removeDeployment(@Observes @Undeploy DeploymentEvent event) {
        deploymentConfigService.removeDeployment(event.getDeploymentId());
    }


    /**
     * Auto deployed is called from authoring environment after successful build and deploy (to maven)
     * @param buildResults Maven deploy result that holds GAV to construct KModuleDeploymentUnit
     */
    @Override
    public void process(BuildResults buildResults) {

        if (!buildResults.getMessages().isEmpty()) {
            return;
        }
        try {

            KModuleDeploymentUnitSummary unit = new KModuleDeploymentUnitSummary("",
                    buildResults.getGAV().getGroupId(),
                    buildResults.getGAV().getArtifactId(),
                    buildResults.getGAV().getVersion(), "", "", DeploymentUnit.RuntimeStrategy.SINGLETON.toString());

            undeploy(unit);
            deploy(unit);
        } catch (Exception e) {
            BuildMessage message = new BuildMessage();
            message.setLevel(BuildMessage.Level.ERROR);
            message.setText("Deployment of unit " + buildResults.getGAV() + " failed: " + e.getMessage());
            buildResults.addBuildMessage(message);
            // always catch exceptions to not break originator of the event
            logger.error("Deployment of unit {} failed: {}",buildResults.getGAV(), e.getMessage(), e);
        }
    }

    /**
     * Reacts on events fired based on changes to system repository - important in cluster environment
     * where system repo will be synchronized
     * @param event - event that carries the complete DeploymentUnit to be undeployed
     */
    public void undeployOnEvent(@Observes @Removed DeploymentConfigChangedEvent event) {
        undeploy((DeploymentUnit) event.getDeploymentUnit());
    }

    /**
     * Reacts on events fired based on changes to system repository - important in cluster environment
     * where system repo will be synchronized
     * @param event - event that carries the complete DeploymentUnit to be deployed
     */
    public void deployOnEvent(@Observes @Added DeploymentConfigChangedEvent event) {
        deploy((DeploymentUnit) event.getDeploymentUnit());
    }


}
