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

package org.jbpm.workbench.cm.server;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenDependencyConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.SourceConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.local.InMemoryBuildRegistry;
import org.guvnor.ala.registry.local.InMemorySourceRegistry;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.jbpm.console.ng.ks.utils.KieServerUtils;
import org.kie.server.api.KieServerConstants;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.client.KieServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.services.cdi.Startup;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.guvnor.ala.pipeline.StageUtil.config;

@ApplicationScoped
@Startup
public class DemoExamplesProvisioningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoExamplesProvisioningService.class);

    private KieServicesClient kieServicesClient;

    private PipelineExecutor executor;

    private Pipeline examplePipe;

    @PostConstruct
    public void init() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        executor = new PipelineExecutor(asList(new GitConfigExecutor(sourceRegistry),
                new MavenProjectConfigExecutor(sourceRegistry),
                new MavenBuildConfigExecutor(),
                new MavenDependencyConfigExecutor(buildRegistry),
                new MavenBuildExecConfigExecutor(buildRegistry)));

        final Stage<Input, SourceConfig> sourceConfig = config("Git Source", (s) -> new GitConfigImpl());
        final Stage<SourceConfig, ProjectConfig> projectConfig = config("Maven Project", (s) -> new MavenProjectConfigImpl());
        final Stage<ProjectConfig, BuildConfig> buildConfig = config("Maven Build Config", (s) -> new MavenBuildConfigImpl(singletonList("install"), new Properties()));
        final Stage<BuildConfig, BinaryConfig> buildExec = config("Maven Build", (s) -> new MavenBuildExecConfigImpl());

        examplePipe = PipelineFactory
                .startFrom(sourceConfig)
                .andThen(projectConfig)
                .andThen(buildConfig)
                .andThen(buildExec)
                .buildAs("jBPM Case Management examples pipeline");

        if ("true".equalsIgnoreCase(System.getProperty("org.kie.demo")) &&
                isNullOrEmpty(System.getProperty(KieServerConstants.KIE_SERVER_LOCATION)) == false) {

            kieServicesClient = KieServerUtils.createAdminKieServicesClient(KieServerConstants.CAPABILITY_CASE);

            SimpleAsyncExecutorService.getDefaultInstance().execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return "Demo projects provisioning";
                }

                @Override
                public void run() {
                    try {
                        LOGGER.info("Executing demo projects provisioning...");
                        provisionExamples();
                        LOGGER.info("Demo projects provisioning completed.");
                    } catch (Exception ex) {
                        LOGGER.error("Failed to provision examples: " + ex.getMessage(), ex);
                    }
                }
            } );
        }
    }

    public void provisionExamples() throws Exception {
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("jbpm-cm-demo").toFile();

            provisionExample(tempDir.getAbsolutePath(), "true", "itorders");
            provisionExample(tempDir.getAbsolutePath(), "false", "itorders-data");
            provisionExample(tempDir.getAbsolutePath(), "false", "itorders-dynamic");
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }

    private void provisionExample(final String tempDir, final String createRepo, final String projectDir) {
        Input input = new Input();

        input.put("repo-name", "case-examples");
        input.put("create-repo", createRepo);
        input.put("branch", "master");
        input.put("out-dir", tempDir);
        input.put("origin", "https://github.com/mswiderski/case-examples.git");
        input.put("project-dir", projectDir);

        executor.execute(input, examplePipe, (MavenBinary b) -> createKieServerContainer(b));
    }

    private void createKieServerContainer(final MavenBinary mavenBinary) {
        final String containerId = mavenBinary.getArtifactId() + "-" + mavenBinary.getVersion();
        final ReleaseId releaseId = new ReleaseId(mavenBinary.getGroupId(), mavenBinary.getArtifactId(), mavenBinary.getVersion());
        KieContainerResource resource = new KieContainerResource(containerId, releaseId);
        LOGGER.info("Creating Kie Server container with id: {}", containerId);
        kieServicesClient.createContainer(containerId, resource);
    }

}