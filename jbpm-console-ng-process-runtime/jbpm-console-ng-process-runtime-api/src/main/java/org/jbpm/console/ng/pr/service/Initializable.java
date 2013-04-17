package org.jbpm.console.ng.pr.service;

import java.util.Set;

import org.droolsjbpm.services.api.DeploymentUnit;

public interface Initializable {

    void initDeployments(Set<DeploymentUnit> deploymentUnits);
}
