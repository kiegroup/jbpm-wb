/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.dd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskResourceEvent;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceChangeType;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@ApplicationScoped
public class DDConfigUpdater {

    private static final String MVEL_PREFIX = "mvel:";
    private static final String REFLECTION_PREFIX = "reflection:";
    private static final String DEFAULT_RESOLVER = "reflection";

    private KieModuleService moduleService;

    private DDEditorService ddEditorService;

    private DDConfigUpdaterHelper configUpdaterHelper;

    public DDConfigUpdater() {
    }

    @Inject
    public DDConfigUpdater(DDEditorService ddEditorService,
                           KieModuleService moduleService,
                           DDConfigUpdaterHelper configUpdaterHelper) {
        this.ddEditorService = ddEditorService;
        this.moduleService = moduleService;
        this.configUpdaterHelper = configUpdaterHelper;
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        if (configUpdaterHelper.isPersistenceFile(resourceAddedEvent.getPath())) {
            //persistence.xml has been added to current project.
            updateConfig(moduleService.resolveModule(resourceAddedEvent.getPath()));
        }
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        if (configUpdaterHelper.isPersistenceFile(resourceUpdatedEvent.getPath())) {
            //persistence.xml has been updated in current project.
            updateConfig(moduleService.resolveModule(resourceUpdatedEvent.getPath()));
        }
    }

    public void processWorkitemInstall(@Observes final DesignerWorkitemInstalledEvent workitemInstalledEvent) {
        addWorkItemToConfig(moduleService.resolveModule(workitemInstalledEvent.getPath()),
                            workitemInstalledEvent.getName(),
                            workitemInstalledEvent.getValue(),
                            workitemInstalledEvent.getResolver());
    }
    
    public void processServiceTaskEvent(@Observes final ServiceTaskResourceEvent serviceResourceEvent) {
        
        if (serviceResourceEvent.getType().equals(ResourceChangeType.ADD)) {
            addWorkItemToConfig(moduleService.resolveModule(serviceResourceEvent.getPath()),
                            serviceResourceEvent.getName(),
                            serviceResourceEvent.getValue(),
                            serviceResourceEvent.getResolver());
        } else if (serviceResourceEvent.getType().equals(ResourceChangeType.DELETE)) {
            removeWorkItemFromConfig(moduleService.resolveModule(serviceResourceEvent.getPath()),
                            serviceResourceEvent.getName());
        }
    }

    private void addWorkItemToConfig(final KieModule kieModule,
                                     final String name, 
                                     final String value, 
                                     final String resolver) {
        Path deploymentDescriptorPath = getDeploymentDescriptorPath(kieModule);
        ddEditorService.createIfNotExists(deploymentDescriptorPath);
        DeploymentDescriptorModel descriptorModel = ddEditorService.load(deploymentDescriptorPath);

        if (descriptorModel != null) {
            if (descriptorModel.getWorkItemHandlers() == null) {
                descriptorModel.setWorkItemHandlers(new ArrayList<>());
            }

            if (isValidWorkitem(name, value) && !workItemAlreadyInstalled(descriptorModel.getWorkItemHandlers(),
                                                                                     name)) {
                ItemObjectModel itemModel = new ItemObjectModel(name,
                                                                parseWorkitemValue(value),
                                                                getWorkitemResolver(value,
                                                                                    resolver),
                                                                null);
                descriptorModel.getWorkItemHandlers().add(itemModel);

                CommentedOption commentedOption = new CommentedOption("system",
                                                                      null,
                                                                      "Workitem config added by system.",
                                                                      new Date());
                ((DDEditorServiceImpl) ddEditorService).save(deploymentDescriptorPath,
                                                             descriptorModel,
                                                             descriptorModel.getOverview().getMetadata(),
                                                             commentedOption);
            }
        }
    }
    
    private void removeWorkItemFromConfig(final KieModule kieModule,
                                     final String name) {
        Path deploymentDescriptorPath = getDeploymentDescriptorPath(kieModule);
        ddEditorService.createIfNotExists(deploymentDescriptorPath);
        DeploymentDescriptorModel descriptorModel = ddEditorService.load(deploymentDescriptorPath);

        if (descriptorModel != null && descriptorModel.getWorkItemHandlers() != null) {
                
            ItemObjectModel found = null;
            
            for (ItemObjectModel itemObject : descriptorModel.getWorkItemHandlers()) {
                if (itemObject != null && itemObject.getName().equals(name)) {
                    found = itemObject;
                    break;
                }
            }

            if (found != null) {
                descriptorModel.getWorkItemHandlers().remove(found);
                CommentedOption commentedOption = new CommentedOption("system",
                                                                      null,
                                                                      "Workitem config added by system.",
                                                                      new Date());
                ((DDEditorServiceImpl) ddEditorService).save(deploymentDescriptorPath,
                                                             descriptorModel,
                                                             descriptorModel.getOverview().getMetadata(),
                                                             commentedOption);
            }
            
        }
    }

    public String parseWorkitemValue(String value) {
        if (value.trim().toLowerCase().startsWith(MVEL_PREFIX)) {
            return value.trim().substring(MVEL_PREFIX.length()).trim();
        } else if (value.trim().toLowerCase().startsWith(REFLECTION_PREFIX)) {
            return value.trim().substring(REFLECTION_PREFIX.length()).trim();
        } else {
            return value.trim();
        }
    }

    public String getWorkitemResolver(String value,
                                      String defaultResolver) {
        if (value.trim().toLowerCase().startsWith(MVEL_PREFIX)) {
            return MVEL_PREFIX.substring(0,
                                         MVEL_PREFIX.length() - 1);
        } else if (value.trim().toLowerCase().startsWith(REFLECTION_PREFIX)) {
            return REFLECTION_PREFIX.substring(0,
                                               REFLECTION_PREFIX.length() - 1);
        } else {
            if (defaultResolver == null || defaultResolver.trim().isEmpty()) {
                return DEFAULT_RESOLVER;
            } else {
                return defaultResolver;
            }
        }
    }

    public boolean isValidWorkitem(String name, String value) {
        return name != null && name.trim().length() > 0 && value != null && value.trim().length() > 0;
    }

    private boolean workItemAlreadyInstalled(List<ItemObjectModel> workitemHandlers,
                                             String name) {
        if (name == null || name.trim().length() < 1) {
            return false;
        }

        for (ItemObjectModel itemObject : workitemHandlers) {
            if (itemObject != null && itemObject.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private void updateConfig(final KieModule kieModule) {

        Path deploymentDescriptorPath = getDeploymentDescriptorPath(kieModule);

        ddEditorService.createIfNotExists(deploymentDescriptorPath);
        //if deployment descriptor exists created, then update it.
        DeploymentDescriptorModel descriptorModel = ddEditorService.load(deploymentDescriptorPath);
        updateMarshallingConfig(descriptorModel,
                                deploymentDescriptorPath,
                                kieModule);
    }

    private Path getDeploymentDescriptorPath(final KieModule kieModule) {
        return PathFactory.newPath("kie-deployment-descriptor.xml",
                                   kieModule.getRootPath().toURI() + "src/main/resources/META-INF/kie-deployment-descriptor.xml");
    }

    private void updateMarshallingConfig(final DeploymentDescriptorModel descriptorModel,
                                         final Path path,
                                         final KieModule kieModule) {

        String marshallingValue = configUpdaterHelper.buildJPAMarshallingStrategyValue(kieModule);
        if (marshallingValue != null && descriptorModel != null) {
            ItemObjectModel oldMarshallingStrategy = null;
            if (descriptorModel.getMarshallingStrategies() != null) {
                //check if the marshalling strategy is already configured
                for (ItemObjectModel itemModel : descriptorModel.getMarshallingStrategies()) {
                    if (itemModel.getValue() != null &&
                            itemModel.getValue().contains("org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy")) {
                        oldMarshallingStrategy = itemModel;
                        break;
                    }
                }
                if (oldMarshallingStrategy != null) {
                    descriptorModel.getMarshallingStrategies().remove(oldMarshallingStrategy);
                }
            } else {
                descriptorModel.setMarshallingStrategies(new ArrayList<ItemObjectModel>());
            }

            ItemObjectModel marshallingStrategy = new ItemObjectModel();
            marshallingStrategy.setResolver("mvel");
            marshallingStrategy.setValue(marshallingValue);
            descriptorModel.getMarshallingStrategies().add(marshallingStrategy);
            CommentedOption commentedOption = new CommentedOption("system",
                                                                  null,
                                                                  "JPA marshalling strategy added by system",
                                                                  new Date());
            ((DDEditorServiceImpl) ddEditorService).save(path,
                                                         descriptorModel,
                                                         descriptorModel.getOverview().getMetadata(),
                                                         commentedOption);
        }
    }
}