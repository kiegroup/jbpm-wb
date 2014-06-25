/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.backend.server.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.deployment.DeploymentConfig;
import org.guvnor.structure.deployment.DeploymentConfigService;
import org.jbpm.console.ng.bd.api.Vfs;
import org.jbpm.console.ng.bd.service.DeploymentUnitProvider;
import org.jbpm.kie.services.api.Kjar;
import org.kie.internal.deployment.DeploymentUnit;

@ApplicationScoped
@Vfs
@Kjar
public class SystemRepoDeploymentUnitProvider implements DeploymentUnitProvider<DeploymentUnit> {

    @Inject
    private DeploymentConfigService deploymentConfigService;

    @Override
    public Set<DeploymentUnit> getDeploymentUnits() {
        Set<DeploymentUnit> deploymentUnits = new HashSet<DeploymentUnit>();
        Collection<DeploymentConfig> deploymentConfigs = deploymentConfigService.getDeployments();
        if ( deploymentConfigs != null ) {
            for ( DeploymentConfig config : deploymentConfigs ) {
                deploymentUnits.add( (DeploymentUnit) config.getDeploymentUnit() );
            }
        }
        return deploymentUnits;
    }
}
