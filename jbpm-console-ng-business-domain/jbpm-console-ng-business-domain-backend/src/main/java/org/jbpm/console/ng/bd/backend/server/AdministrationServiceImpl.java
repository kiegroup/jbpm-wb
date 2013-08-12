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
package org.jbpm.console.ng.bd.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.bd.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.bd.service.DeploymentUnitProvider;
import org.jbpm.console.ng.bd.service.Initializable;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.Kjar;
import org.jbpm.kie.services.api.Vfs;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@ApplicationScoped
public class AdministrationServiceImpl implements AdministrationService {

    private static final String DEPLOYMENT_SERVICE_TYPE_CONFIG = "deployment.service";
    private static final Logger logger = LoggerFactory.getLogger(AdministrationServiceImpl.class);
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private GroupService groupService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private DeploymentManagerEntryPoint deploymentManager;

    @Inject
    @Any
    private Instance<DeploymentService> deploymentService;

    @Inject
    @Any
    private Instance<DeploymentUnitProvider> deploymentUnitProviders;

    private String deploymentServiceType;
    
    /**
     * This flag is necessary to let dependent services know when the deployments have been bootstrapped. 
     * Because retrieving the status of the deployments will be frequently called, it's more efficient
     *  to use a boolean to save this status than to have the dependent services call produceDeploymentUnits().
     */
    private volatile boolean bootstrapDeploymentsDone = false;

    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#bootstrapRepository(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void bootstrapRepository( String repoAlias,
                                     String repoUrl,
                                     String userName,
                                     String password ) {

        Repository repository = null;
        try {
            repository = repositoryService.getRepository( repoAlias );
            if ( repository == null ) {

                final Map<String, Object> env = new HashMap<String, Object>( 3 );
                env.put( "origin", repoUrl );
                env.put( "username", userName );
                env.put( "crypt:password", password );

                repositoryService.createRepository( "git", repoAlias, env );
                repository = repositoryService.getRepository( repoAlias );

            }
        } catch (Exception e) {
            // don't fail on creation of repository, just log the cause
            logger.warn("Unable to create repository with alias {} due to {}", repoAlias, e.getMessage());
        }

        Group demoGroup = groupService.getGroup("demo");
        if ( demoGroup == null ) {
            List<Repository> repositories = new ArrayList<Repository>();
            if (repository != null) {
                repositories.add(repository);
            }
            groupService.createGroup( "demo", "demo@jbpm.org", repositories );

        } else {
            Collection<Repository> repositories = demoGroup.getRepositories();
            if (repositories != null) {
                boolean found = false;
                for (Repository repo : repositories) {
                    if (repo.getAlias().equals(repository.getAlias())) {
                        found = true;
                    }
                }
                if (!found) {
                    groupService.addRepository(demoGroup, repository);
                }
            }
        }

        if (repository != null) {

            try {
                ioService.newFileSystem( URI.create( repository.getUri() ), repository.getEnvironment() );

            } catch ( FileSystemAlreadyExistsException e ) {
                ioService.getFileSystem( URI.create( repository.getUri() ) );

            }
        }
    }

    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#bootstrapConfig()
     */
    public void bootstrapConfig() {
        ConfigGroup deploymentServiceTypeConfig = null;
        List<ConfigGroup> configGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
        if ( configGroups != null ) {
            for ( ConfigGroup configGroup : configGroups ) {
                if ( DEPLOYMENT_SERVICE_TYPE_CONFIG.equals( configGroup.getName() ) ) {
                    deploymentServiceTypeConfig = configGroup;
                    break;
                }
            }
        }
        if ( deploymentServiceTypeConfig == null ) {
            deploymentServiceTypeConfig = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                    DEPLOYMENT_SERVICE_TYPE_CONFIG, "" );
            deploymentServiceTypeConfig.addConfigItem( configurationFactory.newConfigItem( "type", "kjar" ) );

            configurationService.addConfiguration( deploymentServiceTypeConfig );
        }

        deploymentServiceType = deploymentServiceTypeConfig.getConfigItemValue( "type" );
    }

    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#bootstrapDeployments()
     */
    public void bootstrapDeployments() {
        Set<DeploymentUnit> deploymentUnits = produceDeploymentUnits();
        ( (Initializable) deploymentManager ).initDeployments( deploymentUnits );
        bootstrapDeploymentsDone = true;
    }

    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#areDeploymentsBootstrapped()
     */
    public boolean getBootstrapDeploymentsDone() { 
        return bootstrapDeploymentsDone;
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#produceDeploymentUnits()
     */
    @Produces
    @RequestScoped
    public Set<DeploymentUnit> produceDeploymentUnits() {
        Set<DeploymentUnit> deploymentUnits = new HashSet<DeploymentUnit>();

        Instance<DeploymentUnitProvider> suitableProviders = this.deploymentUnitProviders.select( getDeploymentType() );

        for ( DeploymentUnitProvider provider : suitableProviders ) {
            deploymentUnits.addAll( provider.getDeploymentUnits() );
        }

        return deploymentUnits;
    }

    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#getDeploymentService()
     */
    @Produces
    public DeploymentService getDeploymentService() {
        return this.deploymentService.select( getDeploymentType() ).get();
    }

    protected AnnotationLiteral getDeploymentType() {
        if ( deploymentServiceType.equals( "kjar" ) ) {
            return new AnnotationLiteral<Kjar>() {
            };
        } else if ( deploymentServiceType.equals( "vfs" ) ) {
            return new AnnotationLiteral<Vfs>() {
            };
        } else {
            throw new IllegalStateException( "Unknown type of deployment service " + deploymentServiceType );
        }
    }

}
