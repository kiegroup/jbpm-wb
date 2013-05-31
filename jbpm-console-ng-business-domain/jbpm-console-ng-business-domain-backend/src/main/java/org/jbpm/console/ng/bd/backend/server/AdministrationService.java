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
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@ApplicationScoped
public class AdministrationService {

    private static final String DEPLOYMENT_SERVICE_TYPE_CONFIG = "deployment.service";

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

    public void bootstrapRepository( String repoAlias,
                                     String repoUrl,
                                     String userName,
                                     String password ) {

        Repository repository = repositoryService.getRepository( repoAlias );
        if ( repository == null ) {

            final Map<String, Object> env = new HashMap<String, Object>( 3 );
            env.put( "origin", repoUrl );
            env.put( "username", userName );
            env.put( "crypt:password", password );

            repositoryService.createRepository( "git", repoAlias, env );
            repository = repositoryService.getRepository( repoAlias );
        }
        Collection<Group> groups = groupService.getGroups();
        if ( groups == null || groups.isEmpty() ) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repository );
            groupService.createGroup( "demo", "demo@jbpm.org", repositories );
        }
        try {
            ioService.newFileSystem( URI.create( repository.getUri() ), repository.getEnvironment() );

        } catch ( FileSystemAlreadyExistsException e ) {
            ioService.getFileSystem( URI.create( repository.getUri() ) );

        }
    }

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

    public void bootstrapDeployments() {

        Set<DeploymentUnit> deploymentUnits = produceDeploymentUnits();
        ( (Initializable) deploymentManager ).initDeployments( deploymentUnits );
    }

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
