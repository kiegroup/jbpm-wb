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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.services.cdi.Startup;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@Singleton
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

    @PostConstruct
    public void onStartup() {

        Repository repository = repositoryService.getRepository(REPO_PLAYGROUND);
        if(repository == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            repositoryService.cloneRepository("git", REPO_PLAYGROUND, ORIGIN_URL, userName, password);
            repository = repositoryService.getRepository(REPO_PLAYGROUND);
        }
        try {
            ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());

        } catch (FileSystemAlreadyExistsException e) {
            ioService.getFileSystem(URI.create(repository.getUri()));

        }
    }
}