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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jbpm.process.workitem.repository.RepositoryEventListener;
import org.jbpm.process.workitem.repository.RepositoryStorage;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskResourceEvent;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.workbench.events.ResourceChangeType;


@ApplicationScoped
public class WorkbenchRepositoryEventListener implements RepositoryEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WorkbenchRepositoryEventListener.class);
    
    private static final String REPOSITORY_CONTENT_LOCATION = "/service-tasks/";
    private static final String REPOSITORY_PREFIX = "default://master@";
    
    private String repositoryVersion;
    
   

    private Predicate<ArtifactRepository> filter = new Predicate<ArtifactRepository>() {
        
        @Override
        public boolean test(ArtifactRepository t) {
            return t.getName().equals(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)
                    || t.getName().equals(ArtifactRepositoryService.WORKSPACE_M2_REPO_NAME);
        }
    };
    
    @Inject
    @Named("serviceTasksStorageVFS")
    private RepositoryStorage<ServiceTasksConfiguration> repositoryStorage;
    
    @Inject
    private GuvnorM2Repository m2Repository;
    
    @Inject
    private ServletContext servletContext;
    
    @Inject
    private POMService pomService;
    
    @Inject
    private ModuleService<?> moduleService;
    
    @Inject
    private MetadataService metadataService;
    
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    
    @Inject
    private Event<ServiceTaskResourceEvent> serviceTaskResourceEvent;
    
    
    @PostConstruct
    public void init() {
        final Properties properties = new Properties();
        try (final InputStream stream = this.getClass().getResourceAsStream("/service-tasks.properties")) {
            properties.load(stream);
            
            repositoryVersion = properties.getProperty("version");
            
        } catch (IOException e) {
            
        }
        
    }    

    @Override
    public void onServiceTaskAdded(RepoData service) {
       
    }
    
    @Override
    public void onServiceTaskEnabled(RepoData service) {
        logger.info("Service task {} has been enabled", service.getName());

        if (repositoryStorage.loadConfiguration().getMavenInstall()) {
            String resourceLocation = REPOSITORY_CONTENT_LOCATION + service.getModule() + "/" + service.getModule() + "-" + repositoryVersion + ".jar";
            InputStream stream = servletContext.getResourceAsStream(resourceLocation);
            
            if (stream != null) {
                String pomProperties = GuvnorM2Repository.loadPomPropertiesFromJar(stream);
                if (pomProperties != null) {
                    final AFReleaseId releaseId = AFReleaseIdImpl.fromPropertiesString(pomProperties);
                
                    GAV gav = new GAV(releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion());
                    if (!m2Repository.containsArtifact(gav, filter) && !ServiceTaskUtils.DEFAULT_HANDLERS.contains(service.getModule())) {
                        
                        stream = servletContext.getResourceAsStream(resourceLocation);
                        m2Repository.deployArtifact(stream, gav, false, filter);
                        logger.info("Service task {} has been installed in maven", service.getName());
                    }
                }
            }
        }
    }

    @Override
    public void onServiceTaskDisabled(RepoData service) {
        logger.info("Service task {} has been disabled", service.getName());

    }

    @Override
    public void onServiceTaskInstalled(RepoData service, String target, List<String> parameters) {
        
        try {
            
            boolean installPomDepds = repositoryStorage.loadConfiguration().getInstallPomDeps();
            Path path = PathFactory.newPath("pom.xml", REPOSITORY_PREFIX + target);
            
            Module module = moduleService.resolveModule(path);
            POM projectPOM = pomService.load(module.getPomXMLPath());
            
            if (installPomDepds && projectPOM != null && !ServiceTaskUtils.DEFAULT_HANDLERS.contains(service.getModule())) {
                boolean useVersionRange = repositoryStorage.loadConfiguration().getVersionRange();
                Dependencies projectDepends = projectPOM.getDependencies();
                Dependencies validDependsFromWorkitem = getValidDependenciesForWorkitem(projectDepends,
                                                                                        service,
                                                                                        useVersionRange);
                if (validDependsFromWorkitem != null && validDependsFromWorkitem.size() > 0) {
                    for (Dependency workitemDependency : validDependsFromWorkitem) {
                        projectPOM.getDependencies().add(workitemDependency);
                    }
    
                    pomService.save(module.getPomXMLPath(),
                                    projectPOM,
                                    metadataService.getMetadata(module.getPomXMLPath()),
                                    "System updated dependencies from service task installation.",
                                    false);
                }
            }            
            if (service.getGav() == null) {
                installFromLocal(service, module);
            } else {
                installFromJar(service, module);
            }
            String handler = service.getDefaultHandler();
            if (parameters != null && !parameters.isEmpty()) {
                String strParams = "(\"" + parameters.stream().collect(Collectors.joining("\", \"")) + "\")";
                
                handler = handler.replaceFirst("\\(.*?\\)", strParams);
            }
            // install service task into deployment descriptor
            serviceTaskResourceEvent.fire(new ServiceTaskResourceEvent(path, "mvel", handler, service.getName(), "", ResourceChangeType.ADD));
            
            logger.info("Service task {} has been installed", service.getName());
        } catch (Exception e) {
            logger.error("Unexpected error when installing service task {}", service.getName(), e);
        }
        
    }

    @Override
    public void onServiceTaskUninstalled(RepoData service, String target) {        
        Path path = PathFactory.newPath("pom.xml", REPOSITORY_PREFIX + target);
        
        Module module = moduleService.resolveModule(path);
        POM projectPOM = pomService.load(module.getPomXMLPath());
        
        boolean installPomDepds = repositoryStorage.loadConfiguration().getInstallPomDeps();
        
        if (installPomDepds && projectPOM != null) {    
            boolean useVersionRange = repositoryStorage.loadConfiguration().getVersionRange();
            Dependencies workItemDepends = new Dependencies();
            service.getMavenDependencies().forEach(dep -> workItemDepends.add(new Dependency(new GAV(dep.getGroupId(), 
                                                                                                   dep.getArtifactId(), 
                                                                                                   resolveVersion(dep.getVersion(), useVersionRange)))));
            if (workItemDepends != null && workItemDepends.size() > 0) {
                for (Dependency workitemDependency : workItemDepends) {
                    projectPOM.getDependencies().remove(workitemDependency);
                }

                pomService.save(module.getPomXMLPath(),
                                projectPOM,
                                metadataService.getMetadata(module.getPomXMLPath()),
                                "System updated dependencies from service task uninstallation.",
                                false);
            }
        }
        
        // remove icon
        String icon = service.getIcon();
        if (icon != null && !icon.isEmpty()) {  
            String iconName = service.getName() + ".png";
            Path iconPath = PathFactory.newPath(iconName, module.getRootPath().toURI() + "global/" + iconName);
            
            delete(Paths.convert(iconPath));
        }
        // remove wid
        Path widPath = PathFactory.newPath(service.getName() + ".wid", module.getRootPath().toURI() + "global/" + service.getName() + ".wid");
        
        delete(Paths.convert(widPath));
        
        // install service task into deployment descriptor
        serviceTaskResourceEvent.fire(new ServiceTaskResourceEvent(path, "mvel", service.getDefaultHandler(), service.getName(), "", ResourceChangeType.DELETE));
        logger.info("Service task {} has been uninstalled", service.getName());
    }

    /*
     * Helper methods
     */
    
    protected void installFromLocal(RepoData service, Module module) throws IOException {
        // install icon
        String icon = service.getIcon();
        if (icon != null && !icon.isEmpty()) {            
            String iconLocation = REPOSITORY_CONTENT_LOCATION + service.getModule() + "/" + icon;
            InputStream stream = servletContext.getResourceAsStream(iconLocation);
            if (stream == null) {
                iconLocation = REPOSITORY_CONTENT_LOCATION + service.getModule() + "/icon.png";
                stream = servletContext.getResourceAsStream(iconLocation);
            }
            
            String iconName = service.getName() + ".png";
            Path iconPath = PathFactory.newPath(iconName, module.getRootPath().toURI() + "global/"  + iconName);
            
            store(Paths.convert(iconPath), IOUtils.toByteArray(stream));
        }
        // install wid
        String widLocation = REPOSITORY_CONTENT_LOCATION + service.getModule() + "/" + service.getName() + ".wid";
        InputStream widStream = servletContext.getResourceAsStream(widLocation);
        
        
        Path widPath = PathFactory.newPath(service.getName() + ".wid", module.getRootPath().toURI() + "global/" + service.getName() + ".wid");            
        
        store(Paths.convert(widPath), IOUtils.toByteArray(widStream));
    }
    
    protected void installFromJar(RepoData service, Module module) throws IOException {
        // install icon
        File uploadedServiceArtifact = m2Repository.getArtifactFileFromRepository(new GAV(service.getGav()));
        if (uploadedServiceArtifact == null || !uploadedServiceArtifact.exists()) {
            throw new RuntimeException("No file found for artifact " + service.getGav());
        }
        
        try (ZipFile zipFile = new ZipFile(uploadedServiceArtifact)) {
        
            String icon = service.getIcon();
            if (icon != null && !icon.isEmpty()) {            
                ZipEntry location = zipFile.getEntry(icon);
                if (location == null) {
                    location = zipFile.getEntry("icon.png");
                }
                
                InputStream stream = zipFile.getInputStream(location);
     
                String iconName = service.getName() + ".png";
                Path iconPath = PathFactory.newPath(iconName, module.getRootPath().toURI() + "global/"  + iconName);
                
                store(Paths.convert(iconPath), IOUtils.toByteArray(stream));
            }
            // install wid
            ZipEntry widLocation = zipFile.getEntry(service.getName() + ".wid");
            InputStream widStream = zipFile.getInputStream(widLocation);
            
            
            Path widPath = PathFactory.newPath(service.getName() + ".wid", module.getRootPath().toURI() + "global/" + service.getName() + ".wid");            
            
            store(Paths.convert(widPath), IOUtils.toByteArray(widStream));
        }
    }
    
    private Dependencies getValidDependenciesForWorkitem(Dependencies projectDepends,
                                                                RepoData workitem,
                                                                boolean useVersionRange) {
        Dependencies validDepends = new Dependencies();

        Dependencies workItemDepends = new Dependencies();
        workitem.getMavenDependencies().forEach(dep -> workItemDepends.add(new Dependency(new GAV(dep.getGroupId(), 
                                                                                               dep.getArtifactId(), 
                                                                                               resolveVersion(dep.getVersion(), useVersionRange)))));
        for (Dependency depends : workItemDepends) {
            if (!projectDepends.containsDependency(depends)) {
                validDepends.add(depends);
            }
        }

        return validDepends;
    }
    
    protected void store(org.uberfire.java.nio.file.Path path, byte[] content) {
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.write(path, content);
        } finally {
            ioService.endBatch();
        }
    }
    
    protected void delete(org.uberfire.java.nio.file.Path path) {
        try {
            ioService.startBatch(path.getFileSystem());
            ioService.deleteIfExists(path);
        } finally {
            ioService.endBatch();
        }
    }
    
    protected String resolveVersion(String version, boolean useVersionRange) {
        if (useVersionRange) {
            String[] versionElements = version.split("\\.");
            
            StringBuilder builder = new StringBuilder("[");
            builder.append(versionElements[0]);
            if (versionElements.length > 1) {
                try {
                    Integer minor = Integer.parseInt(versionElements[1]);
                    builder.append(".");
                    builder.append(minor);
                } catch (NumberFormatException e) {
                    logger.debug("Version element {} is not a number, ignoring", versionElements[1]);
                }
            }
            builder.append(",)");
            
            return builder.toString();
        }
        
        return version;
    }


}
