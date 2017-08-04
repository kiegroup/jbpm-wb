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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DDConfigUpdaterHelper {

    private KieModuleService moduleService;

    private IOService ioService;

    private PersistenceDescriptorService pdService;

    public DDConfigUpdaterHelper() {
        //Injection required
    }

    @Inject
    public DDConfigUpdaterHelper(KieModuleService moduleService,
                                 @Named("ioStrategy") IOService ioService,
                                 PersistenceDescriptorService pdService) {
        this.moduleService = moduleService;
        this.pdService = pdService;
        this.ioService = ioService;
    }

    public boolean isPersistenceFile(Path path) {
        if (path.getFileName().equals("persistence.xml")) {
            KieModule kieModule = moduleService.resolveModule(path);
            String persistenceURI;
            if (kieModule != null && kieModule.getRootPath() != null) {
                //ok, we have a well formed project
                persistenceURI = kieModule.getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml";
                return persistenceURI.equals(path.toURI());
            }
        }
        return false;
    }

    public boolean hasPersistenceFile(Path path) {

        KieModule kieModule = moduleService.resolveModule(path);
        if (kieModule != null && kieModule.getRootPath() != null) {
            //ok, we have a well formed project
            String persistenceURI = kieModule.getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml";
            Path persistencePath = PathFactory.newPath("persistence.xml",
                                                       persistenceURI);
            return ioService.exists(Paths.convert(persistencePath));
        }
        return false;
    }

    public String buildJPAMarshallingStrategyValue(KieModule kieModule) {
        PersistenceDescriptorModel pdModel = pdService.load(kieModule);
        if (pdModel != null) {
            return "new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(\"" + pdModel.getPersistenceUnit().getName() + "\", classLoader)";
        }
        return null;
    }

    public void addJPAMarshallingStrategy(DeploymentDescriptor dd,
                                          Path path) {
        KieModule kieModule = moduleService.resolveModule(path);
        String marshalingValue = null;
        if (kieModule != null && (marshalingValue = buildJPAMarshallingStrategyValue(kieModule)) != null) {
            List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
            ObjectModel objectModel = new ObjectModel();
            objectModel.setResolver("mvel");
            objectModel.setIdentifier(marshalingValue);
            marshallingStrategies.add(objectModel);
            ((DeploymentDescriptorImpl) dd).setMarshallingStrategies(marshallingStrategies);
        }
    }
}
