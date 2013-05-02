/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.server.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.pr.service.DeploymentManagerEntryPoint;
import org.jbpm.console.ng.pr.service.Initializable;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.Vfs;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.VFSDeploymentUnit;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.services.cdi.Startup;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@ApplicationScoped
@Startup
public class AppSetup {

    private static final String REPO_PLAYGROUND = "jbpm-playground";
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private final IOService ioService = new IOServiceDotFileImpl();

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private GroupService groupService;

    @Inject
    private DeploymentManagerEntryPoint deploymentManager;

    @Inject
    @Vfs
    private DeploymentService deploymentService;

    private Repository repository;

    @PostConstruct
    public void onStartup() {

        repository = repositoryService.getRepository(REPO_PLAYGROUND);
        if(repository == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            repositoryService.cloneRepository("git", REPO_PLAYGROUND, ORIGIN_URL, userName, password);
            repository = repositoryService.getRepository(REPO_PLAYGROUND);
        }
        Collection<Group> groups = groupService.getGroups();
        if (groups == null || groups.isEmpty()) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add(repository);
            groupService.createGroup("demo", "demo@jbpm.org", repositories);
        }
        try {
            ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());

        } catch (FileSystemAlreadyExistsException e) {
            ioService.getFileSystem(URI.create(repository.getUri()));

        }
        Set<DeploymentUnit> deploymentUnits = produceDeploymentUnits();
        ((Initializable)deploymentManager).initDeployments(deploymentUnits);
    }

    @Produces
    @RequestScoped
    public Set<DeploymentUnit> produceDeploymentUnits() {
        Set<DeploymentUnit> deploymentUnits = new HashSet<DeploymentUnit>();
        Iterable<Path> assetDirectories = ioService.newDirectoryStream( ioService.get( repository.getUri() + "/processes" ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) {
                if ( org.kie.commons.java.nio.file.Files.isDirectory(entry) ) {
                    return true;
                }
                return false;
            }
        } );

        for (Path p : assetDirectories) {
            String folder = p.toString();
            if (folder.startsWith("/")) {
                folder = folder.substring(1);
            }
            deploymentUnits.add(new VFSDeploymentUnit(p.getFileName().toString(), REPO_PLAYGROUND, folder));
        }
//        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit("org.jbpm.test", "test-module", "1.0.0-SNAPSHOT");
//        deploymentUnits.add(deploymentUnit);
        return deploymentUnits;
    }


    @Produces
    public DeploymentService getDeploymentService() {
        return this.deploymentService;
    }
}