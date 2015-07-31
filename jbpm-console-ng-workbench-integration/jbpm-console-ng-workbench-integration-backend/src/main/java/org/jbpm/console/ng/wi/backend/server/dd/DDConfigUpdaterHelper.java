/*
 * Copyright 2015 JBoss Inc
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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DDConfigUpdaterHelper {

    private KieProjectService projectService;

    private IOService ioService;

    private PersistenceDescriptorService pdService;

    public DDConfigUpdaterHelper() {
        //Injection required
    }

    @Inject
    public DDConfigUpdaterHelper( KieProjectService projectService,
            @Named("ioStrategy") IOService ioService,
            PersistenceDescriptorService pdService ) {
        this.projectService = projectService;
        this.pdService = pdService;
        this.ioService = ioService;
    }

    public boolean isPersistenceFile( Path path ) {
        if ( path.getFileName().equals( "persistence.xml" ) ) {
            KieProject kieProject = projectService.resolveProject( path );
            String persistenceURI;
            if ( kieProject != null && kieProject.getRootPath() != null ) {
                //ok, we have a well formed project
                persistenceURI = kieProject.getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml";
                return persistenceURI.equals( path.toURI() );
            }
        }
        return false;
    }

    public boolean hasPersistenceFile( Path path ) {

        KieProject kieProject = projectService.resolveProject( path );
        if ( kieProject != null && kieProject.getRootPath() != null ) {
            //ok, we have a well formed project
            String persistenceURI = kieProject.getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml";
            Path persistencePath = PathFactory.newPath( "persistence.xml", persistenceURI );
            return ioService.exists( Paths.convert( persistencePath ) );
        }
        return false;
    }


    public String buildJPAMarshallingStrategyValue( KieProject kieProject ) {
        PersistenceDescriptorModel pdModel = pdService.load( kieProject );
        if ( pdModel != null ) {
            return "new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(\"" +  pdModel.getPersistenceUnit().getName() + "\", classLoader)";
        }
        return null;
    }

    public void addJPAMarshallingStrategy( DeploymentDescriptor dd, Path path ) {
        KieProject kieProject = projectService.resolveProject( path );
        String marshalingValue = null;
        if ( kieProject != null && ( ( marshalingValue = buildJPAMarshallingStrategyValue( kieProject )) != null ) ) {
            List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>(  );
            ObjectModel objectModel = new ObjectModel();
            objectModel.setResolver( "mvel" );
            objectModel.setIdentifier( marshalingValue );
            marshallingStrategies.add( objectModel );
            ((DeploymentDescriptorImpl )dd ).setMarshallingStrategies( marshallingStrategies );
        }
    }
}
