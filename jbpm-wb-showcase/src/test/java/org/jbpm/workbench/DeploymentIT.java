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

package org.jbpm.workbench;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jbpm.workbench.wi.backend.server.casemgmt.service.CaseProvisioningSettingsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.apache.commons.io.FilenameUtils;

import static org.jbpm.workbench.wi.backend.server.casemgmt.service.CaseProvisioningSettingsImpl.CASEMGMT_PROPERTIES;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class DeploymentIT {

    public static final String ARCHIVE_NAME = "wildfly.war";

    @Deployment(testable = false)
    public static WebArchive create() {
        final String warFile = System.getProperty(ARCHIVE_NAME);
        return ShrinkWrap.create(ZipImporter.class,
                                 warFile)
                .importFrom(new File("target/" + warFile))
                .as(WebArchive.class);
    }

    @Test
    @RunAsClient
    public void testShowcaseDeployment(@ArquillianResource URL baseURL) throws Exception {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) baseURL.openConnection();
            assertEquals(200,
                         c.getResponseCode());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }

    @Test(timeout = 30000)
    @RunAsClient
    public void testCaseManagementDeployment(@ArquillianResource URL baseURL) throws Exception {
        final URL cm = new URL(baseURL,
                               getCaseAppContext());
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
    }

    private String getCaseAppContext() {
        final String showcaseGAV = loadShowcaseGAV();
        final File file = Maven.configureResolver().workOffline().resolve(showcaseGAV).withoutTransitivity().asSingleFile();
        return "/" + FilenameUtils.getBaseName(file.getName());
    }

    private String loadShowcaseGAV() {
        final Properties properties = new Properties();
        try (final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(CASEMGMT_PROPERTIES)) {
            properties.load(resource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties.getProperty(CaseProvisioningSettingsImpl.SHOWCASE_GAV);
    }
}