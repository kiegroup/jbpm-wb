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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.apache.commons.io.FilenameUtils;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;

@Dependent
public class CaseProvisioningExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseProvisioningExecutor.class);

    @Inject
    private CaseProvisioningSettings settings;

    @Inject
    private Event<CaseProvisioningStartedEvent> startedEvent;

    @Inject
    private Event<CaseProvisioningCompletedEvent> completedEvent;

    @Inject
    private Event<CaseProvisioningFailedEvent> failedEvent;

    public void execute(final PipelineExecutor executor, final Pipeline pipeline, final Input input) {
        SimpleAsyncExecutorService.getDefaultInstance().execute(new ProvisionRunnable(() -> {
            startedEvent.fire(new CaseProvisioningStartedEvent());
            executor.execute(input, pipeline, (WildflyRuntime b) -> {
                final String context = "/" + FilenameUtils.getBaseName(b.getId());
                completedEvent.fire(new CaseProvisioningCompletedEvent(context));
                LOGGER.info("jBPM Case Management Showcase app provisioning completed.");
            });
        }));
    }

    protected int getManagementInterfaceStatus(String host, String managementPort, String password, String username) {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(host, Integer.valueOf(managementPort)), new UsernamePasswordCredentials(username, password));

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build()) {
            final HttpGet httpget = new HttpGet("http://" + host + ":" + managementPort + "/management");
            return httpClient.execute(httpget).getStatusLine().getStatusCode();
        } catch (Exception ex) {
            LOGGER.error("Exception while trying to connect to Wildfly Management interface", ex);
            return -1;
        }
    }

    private interface ProvisioningCommand {

        void execute();

    }

    private class ProvisionRunnable implements DescriptiveRunnable {

        private ProvisioningCommand provisioning;

        public ProvisionRunnable(ProvisioningCommand provisioning) {
            this.provisioning = provisioning;
        }

        @Override
        public String getDescription() {
            return "jBPM Case Management Showcase app provisioning";
        }

        @Override
        public void run() {
            try {
                LOGGER.info("Executing jBPM Case Management Showcase app provisioning...");
                provisionApp();
            } catch (Exception ex) {
                failedEvent.fire(new CaseProvisioningFailedEvent());
                LOGGER.error("Failed to provision jBPM Case Management Showcase app: " + ex.getMessage(), ex);
            }
        }

        public void provisionApp() throws Exception {
            long waitLimit = 5 * 60 * 1000;   // default 5 min
            long sleep = 5 * 1000;
            long elapsed = 0;

            while (elapsed < waitLimit) {
                //Need to wait for management interface to become available, server might still be starting.
                int responseCode = getManagementInterfaceStatus(
                        settings.getHost(),
                        settings.getManagementPort(),
                        settings.getPassword(),
                        settings.getUsername()
                );

                LOGGER.debug("HTTP Response code from Management interface: {}", responseCode);

                if (responseCode == 200) {
                    provisioning.execute();
                    return;
                } else {
                    Thread.sleep(sleep);
                    elapsed += sleep;
                }
            }

            LOGGER.info("Timeout while trying to connect with Wildfly Management interface");
        }

    }

}
