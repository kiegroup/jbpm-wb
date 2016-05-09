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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jbpm.console.ng.bd.backend.server.dd.DeploymentDescriptorManager;
import org.jbpm.console.ng.bd.service.AdministrationService;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;


@ApplicationScoped
public class AdministrationServiceImpl implements AdministrationService {

    private static final Logger logger = LoggerFactory.getLogger( AdministrationServiceImpl.class );
    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private KieProjectService projectService;


    /* (non-Javadoc)
     * @see org.jbpm.console.ng.bd.backend.server.AdministrationService#bootstrapRepository(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void bootstrapRepository( String ou,
                                     String repoAlias,
                                     String repoUrl,
                                     String userName,
                                     String password ) {

        Repository repository = null;
        try {
            repository = repositoryService.getRepository( repoAlias );
            if ( repository == null ) {

                final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();

                if ( repoUrl != null ) {
                    configurations.setOrigin( repoUrl );
                }
                configurations.setUserName( userName );
                configurations.setPassword( password );

                repository = repositoryService.createRepository( "git",
                                                                 repoAlias,
                                                                 configurations );
            }
        } catch ( Exception e ) {
            // don't fail on creation of repository, just log the cause
            logger.warn( "Unable to create repository with alias {} due to {}", repoAlias, e.getMessage() );
        }

        OrganizationalUnit demoOrganizationalUnit = organizationalUnitService.getOrganizationalUnit( ou );
        if ( demoOrganizationalUnit == null ) {
            List<Repository> repositories = new ArrayList<Repository>();
            if ( repository != null ) {
                repositories.add( repository );
            }
            organizationalUnitService.createOrganizationalUnit( ou, ou + "@jbpm.org", null, repositories );

        } else {
            Collection<Repository> repositories = demoOrganizationalUnit.getRepositories();
            if ( repositories != null ) {
                boolean found = false;
                for ( Repository repo : repositories ) {
                    if ( repo.getAlias().equals( repository.getAlias() ) ) {
                        found = true;
                    }
                }
                if ( !found ) {
                    organizationalUnitService.addRepository( demoOrganizationalUnit, repository );
                }
            }
        }
    }

    @Override
    public void bootstrapProject( String repoAlias,
                                  String group,
                                  String artifact,
                                  String version ) {
        GAV gav = new GAV( group, artifact, version );
        try {
            Repository repository = repositoryService.getRepository( repoAlias );
            if ( repository != null ) {
                String projectLocation = repository.getUri() + ioService.getFileSystem( URI.create( repository.getUri() ) ).getSeparator() + artifact;
                if ( !ioService.exists( ioService.get( URI.create( projectLocation ) ) ) ) {
                    projectService.newProject( repository.getBranchRoot( repository.getDefaultBranch() ),
                                               new POM( gav ),
                                               "/" );
                }
            } else {
                logger.error( "Repository " + repoAlias + " was not found, cannot add project" );
            }
        } catch ( Exception e ) {
            logger.error( "Unable to bootstrap project {} in repository {}", gav, repoAlias, e );
        }
    }

    public void createDeploymentDescriptor( @Observes NewProjectEvent newProjectEvent ) {
        KieProject project = (KieProject) newProjectEvent.getProject();
        URI projectRootURI = URI.create( project.getRootPath().toURI() );
        String repositoryAlias = projectRootURI.getHost();
        String metaInfPath = Paths.convert( project.getKModuleXMLPath() ).getParent().toUri().toString();
        String separator = Paths.convert( project.getRootPath() ).getFileSystem().getSeparator();
        String deploymentDescriptorPath = metaInfPath + separator + "kie-deployment-descriptor.xml";
        Path ddVFSPath = ioService.get( URI.create( deploymentDescriptorPath ) );
        if ( !ioService.exists( ddVFSPath ) ) {
            DeploymentDescriptor dd = new DeploymentDescriptorManager( "org.jbpm.domain" ).getDefaultDescriptor();
            Set<String> groups = new HashSet<String>( project.getGroups() );

            Repository repo = repositoryService.getRepository( repositoryAlias );
            if ( repo != null ) {
                groups.addAll( repo.getGroups() );
            }
            dd.getBuilder().setRequiredRoles( new ArrayList<String>( groups ) );

            String xmlDescriptor = dd.toXml();
            ioService.write( ddVFSPath, xmlDescriptor );
        }
    }

}
