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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.repository.RepositoryEventListener;
import org.jbpm.process.workitem.repository.RepositoryStorage;
import org.jbpm.process.workitem.repository.service.RepoAuthParameter;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.jbpm.process.workitem.repository.service.RepoMavenDepend;
import org.jbpm.process.workitem.repository.service.RepoParameter;
import org.jbpm.process.workitem.repository.service.RepoResult;
import org.jbpm.process.workitem.repository.service.RepoService;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@ApplicationScoped
public class ServiceTaskServiceImpl implements ServiceTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskServiceImpl.class);
    
    private RepoService repoService;
    
    @Inject
    @Named("serviceTasksStorageVFS")
    private RepositoryStorage<ServiceTasksConfiguration> repositoryStorage;
    
    @Inject
    private RepositoryEventListener eventListener;
    
    @Inject
    private GuvnorM2Repository m2Repository;

    @PostConstruct
    public void initialLoad() {
        
        repoService = new RepoService(repositoryStorage, eventListener); 
    }

    protected void setRepoService(RepoService repoService) {
        this.repoService = repoService;
    }

    @Override
    public List<ServiceTaskSummary> getServiceTasks() {
        List<ServiceTask> serviceTaskSummaries = repoService.getServices().stream()
                .map(repoData -> new ServiceTask(repoData.getId(), "fa fa-cogs", repoData.getName(), repoData.getDescription(), repoData.getActiontitle(),
                                                 repoData.isEnabled(), repoData.getInstalledOn(), extractAuthParameters(repoData), repoData.getAuthreferencesite(),
                                                 repoData.getInstalledOnBranch(), getGAV(repoData)))
                .sorted((service, service2) -> service.getName().compareTo(service2.getName()))
                .collect(Collectors.toList());

        return removeOldVersionServiceTask(serviceTaskSummaries).stream()
                .map(v -> ServiceTaskHelp.createServiceTaskSummary(v)).collect(Collectors.toList());
    }

    protected List<ServiceTask> removeOldVersionServiceTask(List<ServiceTask> serviceTaskSummaries) {
        Map<String, List<ServiceTask>> groupingServiceTaskSummariesByGA = serviceTaskSummaries.stream().
                collect(Collectors.groupingBy(ServiceTask::getGroupingConditionByGA, Collectors.toList()));

        final List<ServiceTask> resultServiceTaskSummaries = new ArrayList<>();
        groupingServiceTaskSummariesByGA.values()
                .forEach(serviceTaskSummariesByGA -> {
                    OptionalInt maxOptional = serviceTaskSummariesByGA.stream().mapToInt(value -> value.getCompareAbleVersion()).max();
                    if (maxOptional.isPresent()) {
                        resultServiceTaskSummaries.addAll(serviceTaskSummariesByGA.stream().filter(serviceTaskSummariesByV -> serviceTaskSummariesByV.getCompareAbleVersion() == maxOptional.getAsInt()).collect(Collectors.toList()));
                    } else {
                        resultServiceTaskSummaries.addAll(serviceTaskSummariesByGA);
                    }
                });
        return resultServiceTaskSummaries;
    }

    protected String getGAV(RepoData repoData) {
        return repoData.getGav() != null && !repoData.getGav().isEmpty() ? repoData.getGav() : getGAVFromMavenDependencies(repoData);
    }

    private String getGAVFromMavenDependencies(RepoData repoData) {
        if (repoData.getMavenDependencies().size() > 0) {
            return repoData.getMavenDependencies().get(0).getGroupId() + ":" + repoData.getMavenDependencies().get(0).getArtifactId() + ":" + repoData.getMavenDependencies().get(0).getVersion();
        }
        return null;
    }

    @Override
    public List<ServiceTaskSummary> getEnabledServiceTasks(String branchName) {
        List<ServiceTask> enabledServiceTasks = repoService.getServices().stream()
                .filter(service -> service.isEnabled())
                .map(repoData -> new ServiceTask(repoData.getId(), "fa fa-cogs", repoData.getName(), repoData.getDescription(), repoData.getActiontitle(),
                                                 repoData.isEnabled(), repoData.getInstalledOn(), extractAuthParameters(repoData), repoData.getAuthreferencesite(),
                                                 repoData.getInstalledOnBranch(), getGAV(repoData)))
                .sorted((service, service2) -> service.getName().compareTo(service2.getName()))
                .collect(Collectors.toList());

        return removeOldVersionServiceTask(enabledServiceTasks).stream()
                .map(v -> ServiceTaskHelp.createServiceTaskSummary(v)).collect(Collectors.toList());
        
    }

    @Override
    public void enableServiceTask(String id) {
        repoService.enableService(id);
        
    }

    @Override
    public void disableServiceTask(String id) {
        repoService.disableService(id);
    }

    @Override
    public void installServiceTask(String id, String target, List<String> parameters, String branchName) {
        repoService.installService(id, target, parameters, branchName);
    }

    @Override
    public void uninstallServiceTask(String id, String target, String branchName) {
        repoService.uninstallService(id, target, branchName);
        
    }

    @Override
    public ServiceTasksConfiguration getConfiguration() {
        return repositoryStorage.loadConfiguration();
    }

    @Override
    public void saveConfiguration(ServiceTasksConfiguration configuration) {
        repositoryStorage.storeConfiguration(configuration);
        
    }

    protected boolean compareRepoData(RepoData service, GAV gav) {
        if (service.getGav() != null) {
            return service.getGav().contains(gav.getGroupId() + ":" + gav.getArtifactId());
        }
        if (service.getMavenDependencies().size() > 0) {
            return service.getMavenDependencies().get(0).getGroupId().equals(gav.getGroupId()) && service.getMavenDependencies().get(0).getArtifactId().equals(gav.getArtifactId());
        }
        return false;
    }

    protected void disableOldGAVServiceTask(GAV gav) {
        repoService.getServices().stream().filter(repoService -> compareRepoData(repoService, gav)).forEach(repoService -> disableServiceTask(repoService.getId()));
    }

    @Override
    public List<String> addServiceTasks(String gav) {
        List<String> installedServiceTasks = new ArrayList<>();
        GAV actualGav = new GAV(gav);
        disableOldGAVServiceTask(actualGav);
        File uploadedServiceArtifact = m2Repository.getArtifactFileFromRepository(actualGav);
        if (uploadedServiceArtifact == null || !uploadedServiceArtifact.exists()) {
            throw new RuntimeException("No file found for artifact " + gav);
        }
        
        try {
            URL[] urls = new URL[] {uploadedServiceArtifact.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls, Wid.class.getClassLoader());
            
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addUrls(urls);
            builder.addClassLoader(classLoader);

            Reflections reflections = new Reflections(builder);

            Set<Class<?>> workItems = reflections.getTypesAnnotatedWith(Wid.class);
            
            for (Class<?> workItem : workItems) {
                
                if (workItem.isAnnotationPresent(Wid.class)) {
                    Wid widInfo = workItem.getAnnotation(Wid.class);
                    
                    RepoData service = new RepoData();
                    service.setGav(gav);
                    service.setActiontitle(widInfo.serviceInfo().action().title());
                    service.setAuthparams(Stream.of(widInfo.serviceInfo().authinfo().paramsdescription()).filter(name -> !name.isEmpty()).map(name -> {
                                            RepoAuthParameter p = new RepoAuthParameter(); 
                                            p.setName(name); 
                                            return p;})
                                          .collect(Collectors.toList()));
                    service.setAuthreferencesite(widInfo.serviceInfo().authinfo().referencesite());
                    service.setCategory(widInfo.category());
                    service.setDescription(widInfo.serviceInfo().description());
                    service.setDisplayName(widInfo.displayName());
                    service.setDefaultHandler(widInfo.defaultHandler());
                    service.setDocumentation(widInfo.documentation());
                    service.setEnabled(false);
                    service.setIcon(widInfo.icon());
                    service.setIsaction(null);
                    service.setIstrigger(null);
                    service.setKeywords(Arrays.asList(widInfo.serviceInfo().keywords().split(" ")));
                    service.setMavenDependencies(Stream.of(widInfo.mavenDepends()).map(dep -> {
                                                        RepoMavenDepend dependency = new RepoMavenDepend();
                                                        dependency.setArtifactId(dep.artifact());
                                                        dependency.setGroupId(dep.group());
                                                        dependency.setVersion(dep.version());
                                                        
                                                        return dependency;
                                                    })
                                                 .collect(Collectors.toList()));
                    service.setModule(actualGav.getArtifactId());                
                    service.setName(widInfo.name());
                    service.setParameters(Stream.of(widInfo.parameters()).map(p -> {
                                            RepoParameter param = new RepoParameter();
                                            param.setName(p.name());
                                            param.setType(p.runtimeType());
                                            
                                            return param;
                                        })
                                     .collect(Collectors.toList()));
                    service.setRequiresauth(String.valueOf(widInfo.serviceInfo().authinfo().required()));
                    service.setResults(Stream.of(widInfo.parameters()).map(r -> {
                                        RepoResult result = new RepoResult();
                                        result.setName(r.name());
                                        result.setType(r.runtimeType());
                                        return result;
                                    })
                                   .collect(Collectors.toList()));
                    service.setTriggertitle(widInfo.serviceInfo().trigger().title());
                    
                    installedServiceTasks.add(service.getName());
                    logger.debug("Adding service task with name {} of type {}", service.getName(), workItem);
                    repoService.addService(service);
                } else {
                    logger.warn("Wid annotation was not found on type {}", workItem);
                }
            }
            
            return installedServiceTasks;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } 
    }

    @Override
    public void updateInstalledServiceTasks(String newBranchName, String fromBranchName) {
        repoService.updateInstalled(newBranchName, fromBranchName);
    }
    
    /*
     * Helper methods
     */
    
    protected List<String> extractAuthParameters(RepoData service) {
        List<String> authParams = new ArrayList<>();
        
        if (service.getAuthparams() != null) {
            service.getAuthparams().forEach(param -> authParams.add(param.getName()));
        }
        
        return authParams;
    }
}
