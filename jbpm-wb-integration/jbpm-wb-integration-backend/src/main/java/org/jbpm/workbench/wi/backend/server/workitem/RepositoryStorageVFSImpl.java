/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.workitem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.jbpm.process.workitem.repository.storage.InMemoryRepositoryStorage;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import com.thoughtworks.xstream.XStream;

@Named("serviceTasksStorageVFS")
@ApplicationScoped
public class RepositoryStorageVFSImpl extends InMemoryRepositoryStorage<ServiceTasksConfiguration> {

    
    private static final Logger logger = LoggerFactory.getLogger(RepositoryStorageVFSImpl.class);

    private IOService ioService;
    private FileSystem fileSystem;

    private XStream xs;
        
    private Path storagePath;
    private Path configPath;
    
    private ServiceTasksConfiguration configuration;
    
    //enable proxy
    public RepositoryStorageVFSImpl() {
        xs = XStreamUtils.createTrustingXStream();
    }

    @Inject
    public RepositoryStorageVFSImpl( @Named("configIO") final IOService ioService, @Named("systemFS") final FileSystem fileSystem ) {
        this();
        this.ioService = ioService;
        this.fileSystem = fileSystem;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        configPath = fileSystem.getPath( "service-tasks", "remote", "service-repository-config.xml" );
        if (ioService.exists(configPath)) {
            try {
                ioService.startBatch(configPath.getFileSystem());
                configuration = (ServiceTasksConfiguration) xs.fromXML(ioService.readAllString(configPath));
            } finally {
                ioService.endBatch();
            }            
            
        } else {
            configuration = new ServiceTasksConfiguration(true, true, false);            
        }
        
        storagePath = fileSystem.getPath( "service-tasks", "remote", "service-repository-storage.xml" );
        if (ioService.exists(storagePath)) {
            try {
                ioService.startBatch(storagePath.getFileSystem());
                services = (List<RepoData>) xs.fromXML(ioService.readAllString(storagePath));
            } finally {
                ioService.endBatch();
            }
            logger.debug("Loaded all known service tasks from the storage (size {})", services.size());
            
        } else {
            services = new ArrayList<>();
            logger.debug("Service tasks not found in storage ");
        }
    }
    
    @Override
    public List<RepoData> synchronizeServices(List<RepoData> currentServices) {
        if (!currentServices.isEmpty()) {
            for (RepoData service : currentServices) {
                enforceId(service);
                if (!services.contains(service)) {
                    // by default service is disabled unless is in default set
                    if (!ServiceTaskUtils.DEFAULT_HANDLERS.contains(service.getModule())) {
                        service.setEnabled(false);
                    }
                    services.add(service);
                }
            }
        }
        store(storagePath, services);
        return services;
    }

    @Override
    public void onAdded(RepoData service) {
        enforceId(service);
        store(storagePath, services);
    }
    
    @Override
    public void onEnabled(RepoData service) {
        store(storagePath, services);
    }

    @Override
    public void onDisabled(RepoData service) {
        store(storagePath, services);
    }

    @Override
    public void onInstalled(RepoData service, String target) {
        store(storagePath, services);
    }

    @Override
    public void onUninstalled(RepoData service, String target) {
        store(storagePath, services);
    }
    
    @Override
    public ServiceTasksConfiguration loadConfiguration() {
        return configuration;
    }
    
    @Override
    public void storeConfiguration(ServiceTasksConfiguration configuration) {
        store(configPath, configuration);
        this.configuration = configuration;
    }
    
    
    public void onProjectDeleted(@Observes RepositoryRemovedEvent deletedEvent) {
        
        uninstallOnRepositoryRemoved(deletedEvent.getRepository());        
    }
    
    public void onSpaceDeleted(@Observes RepoRemovedFromOrganizationalUnitEvent deletedEvent) {
        
        uninstallOnRepositoryRemoved(deletedEvent.getRepository());
    }
        
    
    /*
     * Helper methods
     */

    protected void uninstallOnRepositoryRemoved(Repository repository) {
        String target = ServiceTaskUtils.extractTargetInfo(repository.getUri());        
        logger.debug("{} has been removed, removing any references to it on service tasks", target);
        
        services.stream()
                .filter(service -> service.getInstalledOn().contains(target))
                .forEach(service -> {
                    service.uninstall(target);
                    logger.debug("Service {} uninstalled from deleted target {}", service.getName(), target);
                });  
    }
    
    protected void store(Path path, Object data) {
        try {
            ioService.startBatch(fileSystem);
            ioService.write(path, xs.toXML(data));
        } finally {
            ioService.endBatch();
        }
    }
}
