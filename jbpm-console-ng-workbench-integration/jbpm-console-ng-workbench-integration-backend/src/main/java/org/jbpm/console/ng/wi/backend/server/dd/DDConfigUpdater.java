/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.wi.backend.server.dd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.console.ng.wi.dd.model.ItemObjectModel;
import org.jbpm.console.ng.wi.dd.service.DDEditorService;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@ApplicationScoped
public class DDConfigUpdater {

    private KieProjectService projectService;

    private IOService ioService;

    private DDEditorService ddEditorService;

    private DDConfigUpdaterHelper configUpdaterHelper;

    private static final String MVEL_PREFIX = "mvel:";

    private static final String REFLECTION_PREFIX = "reflection:";

    public DDConfigUpdater() {
    }
    

    @Inject
    public DDConfigUpdater( DDEditorService ddEditorService,
                            KieProjectService projectService,
                            @Named("ioStrategy") IOService ioService,
                            DDConfigUpdaterHelper configUpdaterHelper ) {
        this.ddEditorService = ddEditorService;
        this.projectService = projectService;
        this.ioService = ioService;
        this.configUpdaterHelper = configUpdaterHelper;
    }

    public void processResourceAdd( @Observes final ResourceAddedEvent resourceAddedEvent ) {
        if ( configUpdaterHelper.isPersistenceFile(resourceAddedEvent.getPath()) ) {
            //persistence.xml has been added to current project.
            updateConfig(projectService.resolveProject(resourceAddedEvent.getPath()));
        }
    }

    public void processResourceUpdate( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        if ( configUpdaterHelper.isPersistenceFile(resourceUpdatedEvent.getPath()) ) {
            //persistence.xml has been updated in current project.
            updateConfig(projectService.resolveProject(resourceUpdatedEvent.getPath()));
        }
    }

    public void processWorkitemInstall( @Observes final DesignerWorkitemInstalledEvent workitemInstalledEvent ) {
        addWorkItemToConfig(projectService.resolveProject(workitemInstalledEvent.getPath()), workitemInstalledEvent);
    }

    private void addWorkItemToConfig( final KieProject kieProject, final DesignerWorkitemInstalledEvent workitemInstalledEvent) {
        Path deploymentDescriptorPath = getDeploymentDescriptorPath( kieProject );
        if ( ioService.exists(Paths.convert(deploymentDescriptorPath)) ) {
            DeploymentDescriptorModel descriptorModel = ddEditorService.load( deploymentDescriptorPath );

            if(descriptorModel != null) {
                if(descriptorModel.getWorkItemHandlers() == null) {
                    descriptorModel.setWorkItemHandlers(new ArrayList<ItemObjectModel>());
                }

                if( isValidWorkitem(workitemInstalledEvent) && !workItemAlreadyInstalled( descriptorModel.getWorkItemHandlers(), workitemInstalledEvent.getName()) ) {
                    ItemObjectModel itemModel = new ItemObjectModel(workitemInstalledEvent.getName(),
                            parseWorkitemValue(workitemInstalledEvent.getValue()),
                            getWorkitemResolver(workitemInstalledEvent.getValue(), workitemInstalledEvent.getResolver()),
                            null);
                    descriptorModel.getWorkItemHandlers().add(itemModel);

                    CommentedOption commentedOption = new CommentedOption( "system", null, "Workitem config added by system.", new Date() );
                    ( ( DDEditorServiceImpl ) ddEditorService ).save(deploymentDescriptorPath, descriptorModel, descriptorModel.getOverview().getMetadata(), commentedOption);
                }
            }
        }
    }

    public String parseWorkitemValue(String value) {
        if(value.trim().toLowerCase().startsWith(MVEL_PREFIX)) {
            return value.trim().substring(MVEL_PREFIX.length()).trim();
        } else if(value.trim().toLowerCase().startsWith(REFLECTION_PREFIX)) {
            return value.trim().substring(REFLECTION_PREFIX.length()).trim();
        } else {
            return value.trim();
        }
    }

    public String getWorkitemResolver(String value, String defaultResolver) {
        if(value.trim().toLowerCase().startsWith(MVEL_PREFIX)) {
            return MVEL_PREFIX.substring(0,MVEL_PREFIX.length()-1);
        } else if(value.trim().toLowerCase().startsWith(REFLECTION_PREFIX)) {
            return REFLECTION_PREFIX.substring(0,REFLECTION_PREFIX.length()-1);
        } else {
            return defaultResolver;
        }
    }

    public boolean isValidWorkitem(DesignerWorkitemInstalledEvent workitemInstalledEvent) {
        return workitemInstalledEvent != null &&
                workitemInstalledEvent.getName() != null &&
                workitemInstalledEvent.getName().trim().length() > 0 &&
                workitemInstalledEvent.getValue() != null &&
                workitemInstalledEvent.getValue().trim().length() > 0;
    }

    private boolean workItemAlreadyInstalled(List<ItemObjectModel> workitemHandlers, String name) {
        if(name == null || name.trim().length() < 1) {
            return false;
        }

        for(ItemObjectModel itemObject : workitemHandlers) {
            if(itemObject != null && itemObject.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private void updateConfig( final KieProject kieProject ) {

        Path deploymentDescriptorPath = getDeploymentDescriptorPath( kieProject );

        if ( ioService.exists(Paths.convert( deploymentDescriptorPath )) ) {
            //if deployment descriptor exists created, then update it.
            DeploymentDescriptorModel descriptorModel = ddEditorService.load( deploymentDescriptorPath );
            updateMarshallingConfig( descriptorModel, deploymentDescriptorPath, kieProject );
        }
    }

    private Path getDeploymentDescriptorPath( final KieProject kieProject ) {
        return PathFactory.newPath( "kie-deployment-descriptor.xml",
                kieProject.getRootPath().toURI() + "/src/main/resources/META-INF/kie-deployment-descriptor.xml" );
    }

    private void updateMarshallingConfig( final DeploymentDescriptorModel descriptorModel,
            final Path path, final KieProject kieProject ) {

        String marshallingValue = configUpdaterHelper.buildJPAMarshallingStrategyValue( kieProject );
        if ( marshallingValue != null ) {
            if ( descriptorModel != null ) {
                ItemObjectModel oldMarshallingStrategy = null;
                if ( descriptorModel.getMarshallingStrategies() != null ) {
                    //check if the marshalling strategy is already configured
                    for ( ItemObjectModel itemModel: descriptorModel.getMarshallingStrategies() ) {
                        if ( itemModel.getValue() != null &&
                                itemModel.getValue().contains( "org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy" ) ) {
                            oldMarshallingStrategy = itemModel;
                            break;
                        }
                    }
                    if ( oldMarshallingStrategy != null ) {
                        descriptorModel.getMarshallingStrategies().remove( oldMarshallingStrategy );
                    }
                } else {
                    descriptorModel.setMarshallingStrategies( new ArrayList<ItemObjectModel>() );
                }

                ItemObjectModel marshallingStrategy = new ItemObjectModel();
                marshallingStrategy.setResolver( "mvel" );
                marshallingStrategy.setValue( marshallingValue );
                descriptorModel.getMarshallingStrategies().add( marshallingStrategy );
                CommentedOption commentedOption = new CommentedOption( "system", null, "JPA marshalling strategy added by system", new Date() );
                ( ( DDEditorServiceImpl ) ddEditorService ).save( path, descriptorModel, descriptorModel.getOverview().getMetadata(), commentedOption );

            }
        }
    }

}

