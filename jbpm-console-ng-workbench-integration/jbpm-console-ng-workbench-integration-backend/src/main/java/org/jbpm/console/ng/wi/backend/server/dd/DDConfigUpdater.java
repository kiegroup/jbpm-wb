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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.console.ng.wi.dd.model.ItemObjectModel;
import org.jbpm.console.ng.wi.dd.service.DDEditorService;
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
        if ( configUpdaterHelper.isPersistenceFile( resourceAddedEvent.getPath() ) ) {
            //persistence.xml has been added to current project.
            updateConfig( projectService.resolveProject( resourceAddedEvent.getPath() ) );
        }
    }

    public void processResourceUpdate( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        if ( configUpdaterHelper.isPersistenceFile( resourceUpdatedEvent.getPath() ) ) {
            //persistence.xml has been updated in current project.
            updateConfig( projectService.resolveProject( resourceUpdatedEvent.getPath() ) );
        }
    }

    private void updateConfig( final KieProject kieProject ) {

        Path deploymentDescriptorPath = PathFactory.newPath( "kie-deployment-descriptor.xml",
                kieProject.getRootPath().toURI() + "/src/main/resources/META-INF/kie-deployment-descriptor.xml" );

        if ( ioService.exists( Paths.convert( deploymentDescriptorPath ) ) ) {
            //if deployment descriptor exists created, then update it.
            DeploymentDescriptorModel descriptorModel = ddEditorService.load( deploymentDescriptorPath );
            updateMarshallingConfig( descriptorModel, deploymentDescriptorPath, kieProject );
        }
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
