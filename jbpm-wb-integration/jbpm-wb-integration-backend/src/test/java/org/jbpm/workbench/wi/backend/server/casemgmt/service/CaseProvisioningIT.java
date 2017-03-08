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

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.enterprise.inject.spi.Extension;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningService;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningSettings;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.server.cdi.SystemConfigProducer;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.jbpm.workbench.wi.backend.server.casemgmt.service.CaseProvisioningSettingsImpl.CASEMGMT_PROPERTIES;

@RunWith(Arquillian.class)
public class CaseProvisioningIT {

    @Deployment(testable = false)
    public static WebArchive create() {
        final File[] files = Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .resolve(
                        "org.guvnor:guvnor-ala-wildfly-provider",
                        "org.guvnor:guvnor-ala-spi",
                        "org.guvnor:guvnor-ala-registry-local",
                        "org.guvnor:guvnor-ala-services-api",
                        "org.guvnor:guvnor-ala-build-maven",
                        "org.uberfire:uberfire-io"
                )
                .withTransitivity()
                .asFile();

        final WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addClass(CaseProvisioningSettings.class)
                .addClass(CaseProvisioningStartedEvent.class)
                .addClass(CaseProvisioningCompletedEvent.class)
                .addClass(CaseProvisioningFailedEvent.class)
                .addClass(CaseProvisioningService.class)
                .addClass(CaseProvisioningStatus.class)
                .addClass(CaseProvisioningServiceImpl.class)
                .addClass(CaseProvisioningExecutor.class)
                .addClass(CaseProvisioningSettingsImpl.class)
                .addClass(SystemConfigProducer.class)
                .addAsResource(CASEMGMT_PROPERTIES)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(files)
                .addAsServiceProvider(Extension.class, SystemConfigProducer.class)
                .addAsServiceProvider(FileSystemProvider.class, SimpleFileSystemProvider.class);

        return war;
    }

    @Test(timeout = 30000)
    @RunAsClient
    public void testDeployment(@ArquillianResource URL baseURL) throws Exception {
        final URL cm = new URL(baseURL, "/jbpm-cm");
        try {
            while (true) {
                HttpURLConnection c = null;
                try {
                    c = (HttpURLConnection) cm.openConnection();
                    if (c.getResponseCode() == 200) {
                        break;
                    } else {
                        Thread.sleep(500);
                    }
                } finally {
                    if (c != null) {
                        c.disconnect();
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
