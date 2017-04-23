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

package org.jbpm.workbench.wi.backend.server.casemgmt.service;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Project;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class CaseDemoProvisioningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDemoProvisioningService.class);

    @Inject
    private LibraryService libraryService;

    @Inject
    private BuildService buildService;

    @Inject
    private SpecManagementService specManagementService;

    private AtomicBoolean deployToServerTemplate = new AtomicBoolean(false);

    private Project newProject;

    @PostConstruct
    public void init() {
        if ("true".equalsIgnoreCase(System.getProperty(ExamplesService.EXAMPLES_SYSTEM_PROPERTY))) {
            final Set<ExampleProject> projects = libraryService.getExampleProjects();
            projects.stream().filter(p -> "itorders".equals(p.getName())).findFirst().ifPresent(p -> {
                LOGGER.info("Importing IT Orders case management demo project...");
                newProject = libraryService.importProject(p);
                LOGGER.info("Building It Orders case management demo project...");
                final BuildResults results = buildService.buildAndDeploy(newProject);
                LOGGER.debug("It Orders project build errors: {}",
                             results.getErrorMessages().size());
                if (results.getErrorMessages().isEmpty()) {
                    deployToServerTemplate.set(true);
                }
            });
        }
    }

    public void onServerTemplateUpdated(@Observes ServerTemplateUpdated serverTemplateUpdated) {
        if (deployToServerTemplate.compareAndSet(true,
                                                 false)) {
            final GAV gav = newProject.getPom().getGav();
            final String containerId = gav.getArtifactId() + "_" + gav.getVersion();
            final ReleaseId releaseId = new ReleaseId(gav.getGroupId(),
                                                      gav.getArtifactId(),
                                                      gav.getVersion());
            final ContainerSpec containerSpec = new ContainerSpec(containerId,
                                                                  gav.getArtifactId(),
                                                                  serverTemplateUpdated.getServerTemplate(),
                                                                  releaseId,
                                                                  KieContainerStatus.STARTED,
                                                                  new HashMap<>());

            LOGGER.info("Creating Kie Server container with id: {} for Server Template: {}",
                        containerId,
                        serverTemplateUpdated.getServerTemplate().getId());
            specManagementService.saveContainerSpec(serverTemplateUpdated.getServerTemplate().getId(),
                                                    containerSpec);
        }
    }
}