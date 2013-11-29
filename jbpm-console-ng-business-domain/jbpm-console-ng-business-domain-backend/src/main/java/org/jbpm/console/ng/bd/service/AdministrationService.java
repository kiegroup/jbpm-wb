package org.jbpm.console.ng.bd.service;

import java.util.Set;

import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.deployment.DeploymentUnit;

public interface AdministrationService {

    public void bootstrapRepository(String ou, String repoAlias, String repoUrl, String userName, String password);

    public void bootstrapConfig();

    public void bootstrapDeployments();

    public boolean getBootstrapDeploymentsDone();

    public Set<DeploymentUnit> produceDeploymentUnits();

    public DeploymentService getDeploymentService();

    public void bootstrapProject(String repoAlias, String group, String artifact, String version);

}