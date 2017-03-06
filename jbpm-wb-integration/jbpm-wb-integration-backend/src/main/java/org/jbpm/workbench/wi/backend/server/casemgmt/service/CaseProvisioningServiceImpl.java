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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.build.maven.config.impl.MavenDependencyConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenDependencyConfigExecutor;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.local.InMemoryBuildRegistry;
import org.guvnor.ala.registry.local.InMemoryRuntimeRegistry;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.impl.WildflyAccessInterfaceImpl;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.executor.WildflyRuntimeExecExecutor;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningService;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningSettings;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

import static java.util.Arrays.asList;
import static org.guvnor.ala.pipeline.StageUtil.config;
import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.*;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
@Service
public class CaseProvisioningServiceImpl implements CaseProvisioningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseProvisioningServiceImpl.class);
    private static final String PIPELINE_NAME = "jBPM Case Management showcase pipeline";

    @Inject
    private CaseProvisioningSettings settings;

    @Inject
    private CaseProvisioningExecutor executor;

    private CaseProvisioningStatus status = DISABLED;

    @PostConstruct
    public void init() {
        if (settings.isProvisioningEnabled() == false) {
            //Provisioning disabled, skipping it entirely
            return;
        }

        LOGGER.info("jBPM Case Management Showcase deployment enabled");

        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final WildflyAccessInterface wildflyAccessInterface = new WildflyAccessInterfaceImpl();

        final Stage<ProviderConfig, RuntimeConfig> runtimeExec = config("Wildfly Runtime Exec", (s) -> new ContextAwareWildflyRuntimeExecConfig());

        final PipelineExecutor pipelineExecutor;
        final Pipeline pipeline;

        final Input input = new Input();
        input.put("wildfly-user", settings.getUsername());
        input.put("wildfly-password", settings.getPassword());
        input.put("host", settings.getHost());
        input.put("management-port", settings.getManagementPort());
        input.put("redeploy", "none");

        if (settings.isDeployFromLocalPath()) {
            final Stage<Input, ProviderConfig> providerConfig = config("Wildfly Provider Config", (s) -> new WildflyProviderConfig() {
            });

            pipelineExecutor = new PipelineExecutor(asList(
                    new WildflyProviderConfigExecutor(runtimeRegistry),
                    new WildflyRuntimeExecExecutor(runtimeRegistry, wildflyAccessInterface)
            ));

            pipeline = PipelineFactory
                    .startFrom(providerConfig)
                    .andThen(runtimeExec)
                    .buildAs(PIPELINE_NAME);

            input.put("war-path", settings.getPath());
        } else {
            final Stage<Input, BinaryConfig> mavenConfig = config("Maven Artifact", (s) -> new MavenDependencyConfigImpl());
            final Stage<BinaryConfig, ProviderConfig> providerConfig = config("Wildfly Provider Config", (s) -> new WildflyProviderConfig() {
            });
            final BuildRegistry buildRegistry = new InMemoryBuildRegistry();

            pipelineExecutor = new PipelineExecutor(asList(
                    new MavenDependencyConfigExecutor(buildRegistry),
                    new WildflyProviderConfigExecutor(runtimeRegistry),
                    new WildflyRuntimeExecExecutor(runtimeRegistry, wildflyAccessInterface)
            ));

            pipeline = PipelineFactory
                    .startFrom(mavenConfig)
                    .andThen(providerConfig)
                    .andThen(runtimeExec)
                    .buildAs(PIPELINE_NAME);

            input.put("artifact", settings.getGAV());
        }

        executor.execute(pipelineExecutor, pipeline, input);
    }

    @Override
    public CaseProvisioningStatus getProvisioningStatus() {
        return status;
    }

    public void onCaseManagementProvisioningStartedEvent(@Observes CaseProvisioningStartedEvent event) {
        status = STARTED;
    }

    public void onCaseManagementProvisioningCompletedEvent(@Observes CaseProvisioningCompletedEvent event) {
        status = COMPLETED;
    }

    public void onCaseManagementProvisioningFailedEvent(@Observes CaseProvisioningFailedEvent event) {
        status = FAILED;
    }

}