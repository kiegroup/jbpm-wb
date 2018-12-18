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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.process.workitem.repository.RepositoryEventListener;
import org.jbpm.process.workitem.repository.RepositoryStorage;
import org.jbpm.process.workitem.repository.service.RepoService;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;


@Service
@ApplicationScoped
public class ServiceTaskServiceImpl implements ServiceTaskService {
    
    private RepoService repoService;
    
    @Inject
    @Named("serviceTasksStorageVFS")
    private RepositoryStorage<ServiceTasksConfiguration> repositoryStorage;
    
    @Inject
    private RepositoryEventListener eventListener;

    @PostConstruct
    public void initialLoad() {
        
        repoService = new RepoService(repositoryStorage, eventListener); 
    }
    
    @Override
    public List<ServiceTaskSummary> getServiceTasks() {
        return repoService.getServices().stream()
                .map(repoData -> new ServiceTaskSummary(repoData.getId(), "fa fa-cogs", repoData.getName(), repoData.getDescription(), repoData.getActiontitle(), repoData.isEnabled(), repoData.getInstalledOn()))
                .sorted((service, service2) -> service.getName().compareTo(service2.getName()))
                .collect(Collectors.toList());
        
    }
    
    @Override
    public List<ServiceTaskSummary> getEnabledServiceTasks() {
        return repoService.getServices().stream()
                .filter(service -> service.isEnabled())
                .map(repoData -> new ServiceTaskSummary(repoData.getId(), "fa fa-cogs", repoData.getName(), repoData.getDescription(), repoData.getActiontitle(), repoData.isEnabled(), repoData.getInstalledOn()))
                .sorted((service, service2) -> service.getName().compareTo(service2.getName()))
                .collect(Collectors.toList());
        
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
    public void installServiceTask(String id, String target) {
        repoService.installService(id, target);
    }

    @Override
    public void uninstallServiceTask(String id, String target) {
        repoService.uninstallService(id, target);
        
    }

    @Override
    public ServiceTasksConfiguration getConfiguration() {
        return repositoryStorage.loadConfiguration();
    }

    @Override
    public void saveConfiguration(ServiceTasksConfiguration configuration) {
        repositoryStorage.storeConfiguration(configuration);
        
    }

}
