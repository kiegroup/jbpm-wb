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

package org.jbpm.workbench.wi.backend.server.builder;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class BPMPostBuildHandler implements PostBuildHandler {

    protected static Logger logger = LoggerFactory.getLogger(BPMPostBuildHandler.class);

    @Inject
    private DDEditorService deploymentDescriptorService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public void process(BuildResults buildResults) {
        String rootPathString = buildResults.getParameter("RootPath");
        if (rootPathString == null) {
            return;
        }

        org.uberfire.java.nio.file.Path ddPath = ioService.get(URI.create(rootPathString + "/src/main/resources/META-INF/kie-deployment-descriptor.xml"));
        if (ioService.exists(ddPath)) {
            Path deploymentDescriptorPath = Paths.convert(ddPath);
            DeploymentDescriptorModel ddModel = deploymentDescriptorService.load(deploymentDescriptorPath);
            buildResults.addParameter("RuntimeStrategy", ddModel.getRuntimeStrategy());
        } else {
            logger.debug("Deployment descriptor not found {}", ddPath);
        }
    }

    // for test purpose only
    public void setDeploymentDescriptorService(DDEditorService deploymentDescriptorService) {
        this.deploymentDescriptorService = deploymentDescriptorService;
    }

    public void setIoService(IOService ioService) {
        this.ioService = ioService;
    }
}
